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
 * NCBI bl2seq has 2 input files and does not require local database.
 */

public class bl2seq extends NCBI_BLASTBase
{
    private Config config = Config.getInstance();
    ExecutorService executor = Executors.newCachedThreadPool();

    public bl2seq()
    {
        super();
        param_linput.add("-i");
        param_linput.add("-j");
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
            String user_dir = config.getUserDir(job.getSubmitter());
            List<Task> tasks = new ArrayList<Task>(1);
            Task t = new Task(
                    job.getId(),
                    task_id.getAndIncrement(),
                    job.getServiceName(),
                    job.getCommand(),
                    job.getPriority(),
                    0);
            for (TaskInput entry : job.getInput())
            {
                String fname = entry.getName();
                byte[] fdata = entry.getData();
                Fragmentable frag = FormatDetector.getFragmenter(new File(user_dir, fname));
                if (frag == null)
                    throw new Exception("Failed to  detect the format of input file:" + fname);
                Object[] data;
                int num_workers = WorkerManager.getInstance().getWorkerNum();
                num_workers = num_workers > 0 ? num_workers : 10;
                data = frag.fragment(new ByteArrayInputStream(fdata), num_workers);
                t.addInput(new TaskInput(fname, data[0].toString().getBytes()));
            }
            tasks.add(t);
            return tasks;
        }
    }

    public List<Task> fragment(Job j) throws Exception
    {
        //executor.execute(new SequenceDataSplitter(j,"FASTA","AA",this.fragment_size));
        Future<List<Task>> tasks = executor.submit(new Fragmenter(j));
        return tasks.get();
    }
}
