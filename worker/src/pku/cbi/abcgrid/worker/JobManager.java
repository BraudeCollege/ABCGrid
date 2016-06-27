package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.worker.conf.Service;
import pku.cbi.abcgrid.worker.conf.Config;

import java.util.concurrent.*;
import java.util.List;

import pku.cbi.abcgrid.master.Task;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * JobManager create several Reader thread(TaskGetterThread,
 * which read Task from Master
 * and put getted Task into one public local queue - "task_queue")
 * and NumOfCPU Processor thread(TaskRunnerThread, which get Task from
 * "task_queue", process the task and return result to MasterAddress computer)
 */
public class JobManager
{
    int NUM_CPU;
    BlockingQueue<Task> task_queue;
    TaskGetterThread[] getter_thread_pool;
    Log logger;
    static JobManager inst = new JobManager();
    ProcessManager pm;

    private JobManager()
    {
        NUM_CPU = SysInfo.getNumOfCPU();
        task_queue = new SynchronousQueue<Task>();
        logger = LogFactory.getLog(JobManager.class);
        pm = ProcessManager.getInstance();
        new TaskCleaner(1).start();
    }

    /**
     * single instance factory.
     *
     * @return <code>JobManager</code> object.
     */
    public static JobManager getInstance()
    {
        return inst;
    }

    /**
     * Start Runner thread to run tasks
     */
    public void startTaskRunner()
    {
        if (Config.ifUseAllCPU())
        {
            for (int i = 0; i < NUM_CPU; i++)
            {
                new Thread(new TaskRunnerThread(task_queue)).start();
            }
        }
        else
        {
            new Thread(new TaskRunnerThread(task_queue)).start();
        }
    }

    /**
     * Start Getter thread to get tasks
     */
    public void startTaskGetter()
    {
        List<Service> supported_service = Config.getService();
        int lens = supported_service.size();
        getter_thread_pool = new TaskGetterThread[lens];
        //start new TaskGetter thread, one thread for one service.
        for (int i = 0; i < lens; i++)
        {
            String sn = supported_service.get(i).getName();
            TaskGetterThread t = new TaskGetterThread(task_queue, sn);
            getter_thread_pool[i] = t;
            t.start();
            //System.out.println("startTaskGetter:"+t.getName());
        }
    }

    /**
     * kill a running job
     *
     * @param job_id job's identity
     */
    public void addJob(long job_id, long task_id, Process process)
    {
        pm.add(job_id, task_id, process);
    }

    /**
     * kill a running job
     *
     * @param job_id job's identity
     */
    public String killJob(long job_id)
    {
        //kill the running process
        String ret = pm.kill(job_id, 0L);
        //clear the queue which may contain waiting job.
        try
        {
            //Thread.sleep(1000);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
        //pm.kill(job_id,0L);
        return ret;
    }

    /**
     * kill TaskGetterThread.
     */
    public void stopTaskGetter()
    {
        //kill old TaskGetter thread
        if (getter_thread_pool != null)
        {
            for (TaskGetterThread t : getter_thread_pool)
            {
                t.cancel();
            }
        }
    }

    /**
     * restart TaskGetterThread(stop and start).
     */
    public void restartTaskGetter()
    {
        stopTaskGetter();
        try
        {
            Thread.sleep(2000);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
        startTaskGetter();
    }
}
