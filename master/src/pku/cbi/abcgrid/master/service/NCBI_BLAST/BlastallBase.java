package pku.cbi.abcgrid.master.service.NCBI_BLAST;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.master.Job;
import pku.cbi.abcgrid.master.Task;
import pku.cbi.abcgrid.master.TaskInput;
import pku.cbi.abcgrid.master.WorkerManager;
import pku.cbi.abcgrid.master.conf.Config;
import pku.cbi.abcgrid.master.fragment.FormatDetector;
import pku.cbi.abcgrid.master.fragment.Fragmentable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * blastall, megablast, rpsblast is similiar in usage and parameters.
 */
public class BlastallBase extends NCBI_BLASTBase
{
    protected ExecutorService executor = Executors.newCachedThreadPool();
    private Config config = Config.getInstance();

    //protected String param_input = "-i";
    //protected String param_database = "-d";
    public BlastallBase()
    {
        super();
        param_linput.add("-i");
        param_rinput.add("-d");
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
            String input_filename = null;
            byte[] input_data = null;
            for (TaskInput entry : job.getInput())
            {
                //only one input file.
                input_filename = entry.getName();
                input_data = entry.getData();
                break;
            }
            if (input_data != null)
            {
                String user_dir = config.getUserDir(job.getSubmitter());
                Object[] data;
                Fragmentable frag = FormatDetector.getFragmenter(new File(user_dir, input_filename));
                if (frag == null)
                    throw new Exception("Failed to  detect the format of input file:" + input_filename);
                int num_workers = WorkerManager.getInstance().getWorkerNum();
                num_workers = num_workers > 0 ? num_workers : 10;
                data = frag.fragment(new ByteArrayInputStream(input_data), num_workers);
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
                    t.addInput(new TaskInput(input_filename, d.toString().getBytes()));
                    tasks.add(t);
                }
                return tasks;
            }
            else//Task could have no any data. e.g. just use -h to print help message.
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

    public List<Task> fragment(Job j) throws Exception
    {
        //executor.execute(new SequenceDataSplitter(j,"FASTA","AA",this.fragment_size));
        Future<List<Task>> tasks = executor.submit(new Fragmenter(j));
        return tasks.get();
    }
}

