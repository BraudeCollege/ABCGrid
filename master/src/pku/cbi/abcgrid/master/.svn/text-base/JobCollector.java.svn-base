package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class JobCollector
{
    private BlockingQueue<TaskResult>   result_queue;
    private JobMonitor                  job_monitor;
    private JobScheduler                job_scheduler;
    private Log                         logger;
    private WorkerManager               wm;
    private static JobCollector jc = new JobCollector();
    private JobCollector()
    {
        result_queue    = new ArrayBlockingQueue<TaskResult>(4096);
        job_monitor     = JobMonitor.getInstance();
        job_scheduler   = JobScheduler.getInstance(); 
        logger          = LogFactory.getLog(JobCollector.class);
        wm              = WorkerManager.getInstance();
        //job_monitor.remove(job_id);
        new Thread(new ResultCollectThread()).start();
    }
    public static JobCollector getInstance()
    {
        return jc;
    }
    class ResultCollectThread implements Runnable
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    writeResult(result_queue.take());
                }
                catch (Exception e)
                {
                    logger.error(e);
                }
            }
        }
    }
    private void  writeResult(TaskResult tr)
            throws Exception
    {
        long jobid = tr.getJobId();
        long taskid = tr.getTaskId();
        if(job_monitor.isJobFinished(jobid))
        {
            //finished or be killed.
            return;
        }
        //System.out.println(jobid+","+taskid);
        job_monitor.getJobInfo(jobid).setFinished(taskid);
        JobResult jr = job_monitor.getOutput(jobid);
        jr.addResult(tr);
        if(job_monitor.isJobFinished(jobid))
        {
            job_monitor.finishJob(jobid);
            jr.finish();
            wm.killWorkerTask(jobid,0);
            //System.out.println(jobid+" finished");
        }
        else
        {
            job_scheduler.backupTask(jobid);
        }
    }
    /**
     * called from ABCWorker to put result file.
     * put a result into result_queue.
     * A task could have one or more result files returned with same taskid
     * fname and fdata could be null.  
     * */
    public void putResultFile(long nodeid, long jid, long taskid, String fname,byte[] fdata)
    {
        //System.out.println(nodeid + " -> " + taskid);
        try
        {
            result_queue.put(new TaskResult(nodeid, jid, taskid, fname,fdata));
        }
        catch (Exception e)
        {
            logger.error(e);
        }

    }
}
