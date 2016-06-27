package pku.cbi.abcgrid.master.service.HMMER;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import org.apache.commons.io.IOUtils;
import pku.cbi.abcgrid.master.*;
import pku.cbi.abcgrid.master.conf.Config;
import pku.cbi.abcgrid.master.fragment.FormatDetector;
import pku.cbi.abcgrid.master.fragment.Fragmentable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * search a HMM database for matches to a query seqyence file.
 * <p/>
 * hmmpfam [options]  hmm_database query_file  2>output 1>error
 * <p/>
 * query_file should be fragmented to multiple sequence files.
 */
public class hmmpfam extends HMMERBase
{
    private Config config = Config.getInstance();
    ExecutorService executor = Executors.newCachedThreadPool();

    public hmmpfam()
    {
        super();
    }

    /**
     * create a job object based on user's inputs.
     *
     * @param user     user(job submitter) name. e.g. "demo".
     * @param commands a command string array. e.g. "hmmpfam ... ".
     * @param priority priority of this user. from 1 to 10
     * @return Job
     */
    public Job parse(String user, String[] commands, int priority) throws Exception
    {
        String user_dir = config.getUserDir(user);
        String inputFileName = null;
        InputStream inputstream = null;
        String hmmerdb = null;
        //"hmmpfam [options] pfam input_seq" -> "#hmmpfam [options] @pfam input_seq >da 2> 1>dad"
        int length = commands.length;
        if (length < 3)
            throw new Exception(usage());
        //the hmmpfam executable.
        commands[0] = "#" + commands[0];
        int index_of_input_file = 0;
        for (int i = commands.length - 1; i > 0; i--)
        {
            if (commands[i].startsWith(">") ||
                    commands[i].startsWith("1>") ||
                    commands[i].startsWith("2>"))
            {

            }
            else
            {
                index_of_input_file = i;
                if (Util.isLegalFilename(commands[i]))
                    break;
            }
        }
        inputFileName = commands[index_of_input_file];
        int index_of_db = index_of_input_file - 1;
        hmmerdb = commands[index_of_db];
        //make assure the last two params does not start with "-" or "--"
        if (inputFileName.startsWith("-") || hmmerdb.startsWith("-"))
        {
            throw new Exception(usage());
        }
        else
        {
            //the hmmer database. e.g. pfam
            commands[index_of_db] = "@" + commands[index_of_db];
            //the input stream should be taken as Input.
            File finput = new File(user_dir, inputFileName);
            inputstream = new FileInputStream(finput);
        }
        List<String> ls = new ArrayList<String>();
        for (String ss : commands)
        {
            ls.add(ss);
        }
        String[] ncmds = new String[ls.size()];
        ls.toArray(ncmds);
        Job job = new Job(user, getServiceName(), getAppName(), ncmds, priority);
        if (inputstream != null)
        {
            job.addInputStream(inputFileName, IOUtils.toByteArray(inputstream));
        }
        return job;
    }

    class Fragmenter implements Callable<List<Task>>
    {
        private Job job;

        public Fragmenter(Job j)
        {
            job = j;
        }

        public List<Task> call() throws Exception
        {
            AtomicLong task_id = new AtomicLong(1L);
            String fname = null;
            byte[] fdata = null;
            for (TaskInput entry : job.getInput())
            {
                fname = entry.getName();
                fdata = entry.getData();
                break;
            }
            if (fdata != null)
            {
                String user_dir = config.getUserDir(job.getSubmitter());
                Fragmentable frag = FormatDetector.getFragmenter(new File(user_dir, fname));
                if (frag == null)
                    throw new Exception("Failed to  detect the format of input file:" + fname);
                Object[] data;
                int num_workers = WorkerManager.getInstance().getWorkerNum();
                num_workers = num_workers > 0 ? num_workers : 10;
                data = frag.fragment(new ByteArrayInputStream(fdata), num_workers);
                List<Task> tasks = new ArrayList<Task>(data.length);
                for (Object d : data)
                {
                    Task t = new Task(
                            job.getId(),
                            task_id.getAndIncrement(),
                            job.getServiceName(),
                            job.getCommand(),
                            job.getPriority(),
                            0);
                    t.addInput(new TaskInput(fname, d.toString().getBytes()));
                    tasks.add(t);
                }
                return tasks;
            }
            else // no input
            {
                List<Task> tasks = new ArrayList<Task>(1);
                Task t = new Task(
                        job.getId(),
                        task_id.getAndIncrement(),
                        job.getServiceName(),
                        job.getCommand(),
                        job.getPriority(),
                        0);
                tasks.add(t);
                return tasks;
            }
        }
    }

    /**
     * fragment a Job into multiple Tasks.
     *
     * @param j Input Job
     * @return List of tasks
     */
    public List<Task> fragment(Job j) throws Exception
    {
        //executor.execute(new SequenceDataSplitter(j,"FASTA","AA",this.fragment_size));
        Future<List<Task>> tasks = executor.submit(new Fragmenter(j));
        return tasks.get();
    }
    public String usage()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\nUsage:\n");
        buffer.append("hmmpfam [options] <hmm database> <sequence file>\n\n");
        buffer.append("Example:\n");
        buffer.append("hmmpfam Pfam_ls pdb_10.fasta\n");
        return buffer.toString();
    }
}
