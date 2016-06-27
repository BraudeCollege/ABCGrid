package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.master.user.UserManager;

import java.util.List;

/**
 * kill a job which is running on worker node. Trigered by command "k"
 * >>>k
 */
public class KillCommand implements Command
{
    //the short command
    public static final String NAME = "k";
    private WorkerManager wm = WorkerManager.getInstance();
    private UserManager   um = UserManager.getInstance();
    private JobMonitor    jm = JobMonitor.getInstance();

    public Object execute(String user,String[] cmd)throws Exception
    {
        //k job_id
        /*
        k 0     //kill all submitted jobs
        k 1     //kill job with id = 1
        k 1 2 3 //kill jobs with id = 1,2 and 3 
         */
        if(cmd.length<=1)
            return usage();
        
        long job_id=0;
        long task_id=0;
        List<JobInfo> jobs;

        StringBuffer buffer = new StringBuffer();
        if(um.getUserGroup(user).equalsIgnoreCase("admin"))
        {
            //administrator can kill any jobs
            jobs = jm.getJobInfo();
        }
        else
        {
            //user can kill his jobs only
            jobs = jm.getJobInfo(user);
        }
        for(int i=1;i<cmd.length;i++)
        {
            try
            {
                job_id = Long.valueOf(cmd[i]);
                if(job_id==0)//kill all
                {
                    for(JobInfo j:jobs)
                    {
                        jm.remove(j.getJobId());
                        wm.killWorkerTask(j.getJobId(),task_id);
                        buffer.append(String.format("killed: %s\n",j));
                    }
                }
                else
                {
                    for(JobInfo j:jobs)
                    {
                        long jid = j.getJobId();
                        if(jid==job_id)
                        {
                            jm.remove(j.getJobId());
                            wm.killWorkerTask(job_id,task_id);
                            buffer.append(String.format("killed: %s\n",j));
                        }
                    }
                }
            }
            catch(NumberFormatException e)
            {
                for(JobInfo j:jobs)
                {
                    if(j.getSubmitter().equalsIgnoreCase(cmd[i]))
                    {
                        jm.remove(j.getJobId());
                        wm.killWorkerTask(j.getJobId(),task_id);
                        buffer.append(String.format("killed: %s\n",j));
                    }
                }
                //kill a user's job
            }

        }
        return buffer.toString();
    }
    public String usage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("kill submitted job(s)\n");
        sb.append("COMMAND: k [job_id] \n");
        sb.append("EXAMPLE: \n\n");
        sb.append(">>>k 1\n");
        sb.append("kill job with id=1.\n\n");
        sb.append(">>>k 1 2 4\n");
        sb.append("kill jobs with id=1, 2 and 4\n\n");
        sb.append(">>>k demo\n");
        sb.append("kill jobs submitted by user 'demo'\n\n");
        sb.append(">>>k 0\n");
        sb.append("kill *ALL* jobs. If you are 'admin', ALL jobs are killed,\n");
        sb.append("otherwise, all jobs submitted by you are killed.\n\n");
        return sb.toString();
    }

}

