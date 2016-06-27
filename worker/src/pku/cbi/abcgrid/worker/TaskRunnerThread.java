package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.master.Task;
import pku.cbi.abcgrid.worker.conf.Config;

import java.util.concurrent.*;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * get & process Task from Master and send results back.
 */

public class TaskRunnerThread implements Runnable
{
    BlockingQueue<Task> queue;
    ExecutorService     executor;
    JobManager          jm;
    Log                 logger;

    public TaskRunnerThread(BlockingQueue<Task> jobqueue)
    {
        queue = jobqueue;
        executor = Executors.newCachedThreadPool();
        logger = LogFactory.getLog(TaskRunnerThread.class);
        jm = JobManager.getInstance();
    }

    public void run()
    {
        //if(Config.isVerbose())
        //    logger.debug("TaskRunnerThread started");
        while (true)
        {
            try
            {
                Task task = queue.take();
                //Future<Result> f = executor.submit(new TaskRunner(task));
                //Status.set("process task: "+service);
                String msg = String.format("[%d,%d], %s",
                        task.getJobId(),
                        task.getTaskId(),
                        Util.join(task.getCommand(), " "));
                if (Config.isVerbose())
                {
                    logger.info(msg);
                }
                Status.set("execute: " + msg);
                Future<Result> f = executor.submit(new TaskRunner(task));
                transferResult(f);
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }
    }

    /**
     * Send task results back to Master.
     *
     * @param f The result of a task.
     * @throws InterruptedException If the execution process is
     *                              interrupted.
     * @throws ExecutionException   Other errors during execution
     */
    public void transferResult(Future<Result> f)
            throws InterruptedException, ExecutionException
    {

        Result r = f.get();
        //if(r.getData()==null)
        //{//no data
        //    MasterProxy.putResultFile(r.getJobId(), r.getTaskId(), r.getCommand(),
        //            null, null);
            //return;
        //}
        String msg = String.format("[%d,%d], %s",
                r.getJobId(),
                r.getTaskId(),
                r.getCommand());
        Status.set("finish: " + msg);
        if (Config.isVerbose())
        {
            logger.info("Finish:" + msg);
        }
        Map<String, byte[]> data = r.getData();
        if(data==null)
        {
            MasterProxy.putResultFile(r.getJobId(), r.getTaskId(), r.getCommand(),
                    null, null);
        }
        for (Map.Entry<String, byte[]> ent : data.entrySet())
        {
            String filename = ent.getKey();
            byte[] filedata = ent.getValue();
            MasterProxy.putResultFile(r.getJobId(), r.getTaskId(), r.getCommand(),
                    filename, filedata);
        }
        if (data.size() == 0)//no output file. print to screen
        {
            //send empty task result denotes that this task is over.
            MasterProxy.putResultFile(r.getJobId(), r.getTaskId(), r.getCommand(),
                    null, null);
        }
        if (r.getStdout() != null)
        {
            MasterProxy.putStdout(r.getJobId(), r.getTaskId(), r.getCommand(), r.getStdout());
        }

        if (r.getStderr() != null)
        {
            MasterProxy.putStderr(r.getJobId(), r.getTaskId(), r.getCommand(), r.getStderr());
        }

    }
}