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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * 1)add(start monitor) a new job
 * 2)remove(stop monitor) a completed job
 * 3)get the status of one job
 * 4)check one user's running jobs.
 */
public class JobMonitor
{
    //number of un-finished jobs
    int                 MAX_JOB_TO_RUN;
    //Number of displayed jobs
    int                 MAX_JOB_TO_SHOW;
    ConcurrentMap<Long,JobInfo> jobs;
    ConcurrentMap<Long,JobResult> output;
    
    private Log                 logger;
    private static JobMonitor   inst     = new JobMonitor();
    //job_dispatched is used to trace every dispatched tasks
    private JobMonitor()
    {
        MAX_JOB_TO_RUN = 32;
        MAX_JOB_TO_SHOW   = 64;
        jobs     = new ConcurrentHashMap<Long,JobInfo>(MAX_JOB_TO_SHOW);
        output   = new ConcurrentHashMap<Long, JobResult>();
        logger   = LogFactory.getLog(JobMonitor.class);
    }
    public static JobMonitor getInstance()
    {
        return inst;
    }
    public JobResult getOutput(long jid)
    {
        //set the output for later writing result into.
        return output.get(jid);
    }
    private void removeFirstEntry()
    {
        List<Long> keys = new ArrayList<Long>(jobs.keySet());
        Collections.sort(keys);
        jobs.remove(keys.get(0));
    }
    public boolean isJobFull(String app)
    {
        int unfinished = 0;
        //for each application, MAX_JOBS
        for(JobInfo j: jobs.values())
        {
            if(! j.isFinished()&&
                 j.getAppName().equalsIgnoreCase(app))
            {
                unfinished += 1;
            }
        }
        return unfinished>=MAX_JOB_TO_RUN;
    }
    public void add(JobInfo ji)
    {
        
        if(jobs.size()>=MAX_JOB_TO_SHOW)
            removeFirstEntry();
        //if(jobs.size())
        jobs.put(ji.getJobId(),ji);
        output.put(ji.getJobId(),new JobResult(ji.getSubmitter()));
    }
    public void remove(long jobid)
    {
        jobs.get(jobid).terminate();
        finishJob(jobid);
        jobs.remove(jobid);
    }
    public JobInfo getJobInfo(long jobid)
    {
        return jobs.get(jobid);
    }
    /**
     * get job info owned by one user
     * */
    public List<JobInfo> getJobInfo(String user)
    {
        List<JobInfo> ret = new ArrayList<JobInfo>(MAX_JOB_TO_SHOW);
        for(JobInfo j: jobs.values())
        {
            if(j.getSubmitter().equalsIgnoreCase(user))
                ret.add(j);
        }
        Collections.sort(ret);
        return ret;
    }
    /**
     * get all job info
     * */
    public List<JobInfo> getJobInfo()
    {
        List<JobInfo> ret  = new ArrayList<JobInfo>(jobs.values());
        Collections.sort(ret);
        return ret;
    }
    public void finishJob(long jobid)
    {
        output.remove(jobid);
        JobInfo j = jobs.get(jobid);
        if(j!=null)
        {
            j.stopTiming();

        }
    }
    public boolean isJobFinished(long jobid)throws Exception
    {
        try
        {
            return jobs.get(jobid).isFinished();
        }
        catch(NullPointerException e)
        {//the job does not exit. May be killed.

            return true;
        }
    }
    public String toString()
    {
        return "";
    }
}
