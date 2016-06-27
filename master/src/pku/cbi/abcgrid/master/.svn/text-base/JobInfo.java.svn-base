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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 */
public class JobInfo implements Comparable<JobInfo>
{
    int     num_task_total;
    int     num_task_idle;
    int     num_task_complete;
    int     num_task_progress;
    int     percentage;

    Date    time_start;
    Date    time_stop;
    
    long    job_id;
    String  submitter;
    String[] command;
    boolean isBackuped;
    final int BACKUP_CUTOFF = 80; //80% percent
    boolean bTerminated;
    Log      logger;
    ConcurrentMap<Long,TaskInfo> tasks;

    public JobInfo(long jid,String s,String[] cmd)
    {
        submitter = s;
        command = cmd;
        job_id      = jid;
        tasks       = new ConcurrentHashMap<Long,TaskInfo>();
        logger      = LogFactory.getLog(JobInfo.class);
        time_start = new Date();
        isBackuped = false;
        bTerminated = false;
    }
    public String getSubmitter()
    {
        return submitter;
    }
    public String[] getCommand()
    {
        return command;
    }
    public long getJobId()
    {
        return job_id;
    }
    public String getAppName()
    {
        return command[0];
    }
    public int compareTo(JobInfo other)
    {
        long id1 = this.job_id;
        long id2 = other.getJobId();
        if(id1<id2)
            return -1;
        else if(id1==id2)
            return 0;
        else
            return 1;
    }
    public void add(TaskInfo t)
    {
        tasks.put(t.getTaskId(),t);
    }
    public void remove(long tid)
    {
        tasks.remove(tid);
    }
    private void checkTaskStatus()
    {
        int finished=0;
        int processing=0;
        int idle=0;
        for(TaskInfo ti:tasks.values())
        {
            switch(ti.getState())
            {
                case complete:
                    finished += 1;
                    break;
                case idle:
                    idle += 1;
                    break;
                case progress:
                    processing += 1;
                    break;
            }
        }
        num_task_idle = idle;
        num_task_complete = finished;
        num_task_progress = processing;
    }
    /**
     * Number of tasks of this Job
     * */
    public int getNumTotal()
    {
        //checkTaskStatus();
        return tasks.size();
    }
    private int getNumIdle()
    {
        //checkTaskStatus();
        return num_task_idle;
    }
    private int getNumProgress()
    {
        //checkTaskStatus();
        return num_task_progress;
    }
    private int getNumComplete()
    {
        //checkTaskStatus();
        return num_task_complete;
    }

    public void terminate()
    {
        bTerminated = true;
        stopTiming();
    }
    /**
     * percentage of finished tasks
     * */
    public int getPercentage()
    {
        checkTaskStatus();
        return (getNumComplete()*100)/getNumTotal();
    }
    /**
     * percentage of finished tasks
     * */
    public boolean isFinished()
    {
        return (100.0-getPercentage())<0.00000001 || bTerminated; 
    }
    public List<Task> backupTasks()
    {
        List<Task> ts = new ArrayList<Task>();
        if(isBackuped)
        {
            return ts;
        }
        int per = getPercentage();
        int idle = getNumIdle();
        if (per >= BACKUP_CUTOFF && idle == 0)
        {
            for(TaskInfo ti:tasks.values())
            {
                if(ti.getState()==TaskState.progress)
                {
                    ti.setState(TaskState.idle);
                    ts.add(ti.getTask());
                }
            }
            isBackuped = true;
        }
        return ts;
    }
    /**
     * elasped time in seconds
     * */
    public int getElapsedSeconds()
    {
        int t = 0;
        if(!isFinished())
        {
            Date now = new Date();
            t =  (int)(now.getTime()-time_start.getTime())/1000;
        }
        else
        {
            t = (int)(time_stop.getTime()-time_start.getTime())/1000;
        }
        return t;
    }
    /**
     * The task is finished.
     *@param tid Id of the task 
     * */
    public void setFinished(long tid)
    {
        TaskInfo info = tasks.get(tid);
        info.setState(TaskState.complete);
    }
    public void setProgress(long tid)
    {
        TaskInfo info = tasks.get(tid);
        info.setState(TaskState.progress);
    }
    public void setIdle(long tid)
    {
        TaskInfo info = tasks.get(tid);
        info.setState(TaskState.idle);
    }
    public void takeByWorker(long tid,long wid)
    {
        TaskInfo info = tasks.get(tid);
        info.setState(TaskState.progress);
        info.setWorker(wid);
        info.startTiming();
    }

    /**
     * record the start time when job is started
     * */
    public void startTiming()
    {
        time_start = new Date();
    }
    /**
     * record the end time when job is finished
     * */
    public void stopTiming()
    {
        if(time_stop==null)
            time_stop = new Date(); 
    }

    private String formatTime(int seconds)
    {
        int hour = seconds/3600;
        int minute = (seconds-hour*3600)/60;
        int second = (seconds-hour*3600-minute*60);
        if(hour>0)
            return String.format("%dh%02dm%02ds",hour,minute,second);
        else if(minute>0)
            return String.format("%02dm%02ds",minute,second);
        else
            return String.format("%02ds",second);
    }
    public String toString()
    {
        //identity user total percentage command output time_start time_elapsed
        NumberFormat nf = NumberFormat.getPercentInstance();
        String percent = nf.format(getPercentage()/100.0);
        return String.format("| %d | %s | %d | %s | %s | %s | %s |\n",
                job_id,
                submitter,
                getNumTotal(),
                percent,
                Util.join(command),
                time_start,
                formatTime(getElapsedSeconds())
                );
    }
}
