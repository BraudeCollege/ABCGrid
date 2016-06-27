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
import pku.cbi.abcgrid.master.service.App;
import pku.cbi.abcgrid.master.service.CE.CE;
import pku.cbi.abcgrid.master.service.HMMER.hmmpfam;
import pku.cbi.abcgrid.master.service.NCBI_BLAST.bl2seq;
import pku.cbi.abcgrid.master.service.NCBI_BLAST.blastall;
import pku.cbi.abcgrid.master.service.NCBI_BLAST.megablast;
import pku.cbi.abcgrid.master.service.NCBI_BLAST.rpsblast;
import pku.cbi.abcgrid.master.user.UserManager;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class JobScheduler
{
    final int   MAX_TASKS;
    boolean     bcompress;
    AtomicLong  atomic_job_id;
    Log         logger;
    WorkerManager   workermgr;
    UserManager     usermgr;
    JobMonitor      job_monitor;
    Map<String, App> apps;
    ConcurrentMap<String, BlockingQueue<Task>> task_queue;

    private static JobScheduler js = new JobScheduler();

    /**
     * single instance
     */
    private JobScheduler()
    {
        MAX_TASKS = 4096;
        bcompress = false;
        atomic_job_id = new AtomicLong(1L);
        logger = LogFactory.getLog(JobScheduler.class);
        workermgr = WorkerManager.getInstance();
        usermgr   = UserManager.getInstance();
        job_monitor = JobMonitor.getInstance();
        task_queue = new ConcurrentHashMap<String, BlockingQueue<Task>>();
        apps = new HashMap<String, App>();
        setHandlers();
        //new Thread(new JobDispatchThread()).start();
    }
    /**
     * single instance factory.
     *
     * @return An instance of JobScheduler
     */
    public static JobScheduler getInstance()
    {
        return js;
    }

    //get job from job_queue and dispatch(partition) the job
    class JobDispatchThread implements Runnable
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    //splitJob(job_queue.take());
                }
                catch (Exception e)
                {
                    System.out.println(e);
                    //System.out.println(">>>");
                }
            }
        }
    }

    /**
     * accept a job command. push this job into job_queue.
     *
     * @param submitter user who submit this job
     * @param commands command string
     * @return Id of job
     * @throws Exception Failed to spawn a job
     */
    public long acceptJob(String submitter, String[] commands) throws Exception
    {

        String appName = commands[0];
        if(job_monitor.isJobFull(appName))
            throw new Exception("Job queue is full (Maximum:32)");

        App app = apps.get(appName);
        int priority = usermgr.getUserPriority(submitter);
        Job j = app.parse(submitter, commands, priority);
        j.setId(atomic_job_id.getAndIncrement());
        //partition this job into tasks
        List<Task> tasks = app.fragment(j);
        JobInfo ji = new JobInfo(j.getId(), j.getSubmitter(),commands);
        for (Task t : tasks)
        {
            ji.add(new TaskInfo(t));
        }
        job_monitor.add(ji);
        BlockingQueue<Task> tq = task_queue.get(j.getServiceName());
        for (Task t : tasks)
        {
            tq.put(t);
        }
        return j.getId();
    }
    /**
     * Called by worker to ask for a task
     *
     * @param worker_id Identity of Worker
     * @param service   App name of task that supported by Worker.
     *                  for example, "NCBI_BLAST"
     * @return A task
     */
    public Task getTask(long worker_id, String service)
    {
        try
        {
            long jid;
            long tid;
            //locate the task queue.
            BlockingQueue<Task> tq = task_queue.get(service);
            if (tq == null)
            {
                //System.out.println("NO handler for "+service);
                return null;
            }
            //the Task in task_queue could be a backup task.
            //if the corresponding Job has been finished,
            //delete(do not send task to worker) the backup task.
            Task t;
            while (true)
            {
                //try to get a task, if no tasks availiable, wait.
                t = tq.take();
                jid = t.getJobId();
                tid = t.getTaskId();
                if (!job_monitor.isJobFinished(jid))
                {
                    break;
                }
            }
            job_monitor.getJobInfo(jid).takeByWorker(tid, worker_id);
            return t;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void setHandlers()
    {
        createServiceQueue("NCBI_BLAST");
        createServiceQueue("HMMER");
        createServiceQueue("CE");
        //createServiceQueue("DALI");
        App s;

        s = new blastall();
        apps.put(s.getAppName(), s);

        s = new bl2seq();
        apps.put(s.getAppName(), s);

        s = new rpsblast();
        apps.put(s.getAppName(), s);

        s = new megablast();
        apps.put(s.getAppName(), s);

        s = new CE();
        apps.put(s.getAppName(), s);

        //s = new DaliLite();
        //services.put(s.getAppName(), s);

        s = new hmmpfam();
        apps.put(s.getAppName(), s);
    }

    /**
     * create a task queue for each service.
     */
    private void createServiceQueue(String ServiceName)
    {
        BlockingQueue<Task> q =
                new PriorityBlockingQueue<Task>(MAX_TASKS, new Comparator<Task>()
                {
                    public int compare(Task A, Task B)
                    {
                        int ret;
                        long aid = A.getJobId()+A.getPriority();
                        long bid = B.getJobId()+B.getPriority();
                        if (aid < bid)
                        {
                            ret = -1;
                        }
                        else if (aid > bid)
                        {
                            ret = 1;
                        }
                        else
                        {
                            ret = 0;
                        }
                        return ret;
                    }
                });
        //every service has its own queue.
        task_queue.put(ServiceName, q);
    }

    /**
     * change the state of all remaining tasks from "in-progress" to "idle".
     * and put these tasks to TaskQueue again.
     */
    public int backupTask(long jid)
    {
        List<Task> ts = job_monitor.getJobInfo(jid).backupTasks();
        for (Task t : ts)
        {
            String s = t.getServiceName();
            BlockingQueue<Task> q = task_queue.get(s);
            //To complete this job ASAP,
            //The backup tasks must be given higher priority than next waiting job.
            //in order to be inserted into the head of waiting queue.
            t.setPriority(0);
            try//add the backup task to TaskQueue again.
            {
                q.put(t);
            }
            catch (InterruptedException e)
            {
                //e.printStackTrace();
            }
        }
        return ts.size();
    }

    /**
     * @param application Name of the application
     * @return boolean
     */
    public boolean isAppSupported(String application)
    {
        return apps.containsKey(application);
    }

    /**
     * @return A String array of supported applications.
     */
    public String[] getSupportedApps()
    {
        List<String> buffer = new ArrayList<String>();
        for (Map.Entry<String, App> ss : apps.entrySet())
        {
            String name = ss.getKey();
            App serv = ss.getValue();
            String sa = serv.getServiceName() + "." + name + "\n";
            buffer.add(sa);
        }
        String[] ss = new String[buffer.size()];
        buffer.toArray(ss);
        return ss;
    }


}
