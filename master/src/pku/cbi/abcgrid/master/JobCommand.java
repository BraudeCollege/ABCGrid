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
 *
 * print the information of one or all running jobs
 *
 * 1) completed,idle and total
 * 2) Status of submitted jobs.
 *      |---
 *      |---
 * 2) Status of running jobs.
 *      |---
 *      |---
 *      |---
 *      |---
 *      |---
 *      |---
 *      |---
 * 3) Status of completed jobs(1024 jobs maximum)
 * 4)
 *
  */
public class JobCommand implements Command
{
    public static final String NAME = "j";
    JobMonitor monitor = JobMonitor.getInstance();
    UserManager umg    = UserManager.getInstance();
    public Object execute(String user,String[] cmd)
    {
        //System.out.println("HELLO:"+user+":"+Util.join(cmd));
        StringBuffer sb = new StringBuffer();
        String header = "| id | user | total | finished | command | start time | elapsed time |\n";
        String delim  = "|********************************************************************|\n";
        sb.append(header);
        sb.append(delim);
        if(cmd.length==1)//list all jobs
        {
            List<JobInfo> ji = monitor.getJobInfo();
            for(JobInfo j:ji)
            {
                //System.out.println(j.getJob().getSubmitter());
                String group = umg.getUserGroup(user);
                if(group.equalsIgnoreCase("admin"))
                {
                    sb.append(j.toString());
                }
                else
                {
                    if(j.getSubmitter().equalsIgnoreCase(user))
                    {
                        sb.append(j.toString());
                    }
                }
                sb.append("\n");
            }
        }
        else//list one or more jobs
        {
            String group = umg.getUserGroup(user);
            for(int i=1;i<cmd.length;i++)
            {
                try
                {
                    //query the job with id
                    long jid = Long.valueOf(cmd[i]);
                    JobInfo info = monitor.getJobInfo(jid);
                    if(group.equalsIgnoreCase("admin") ||
                        info.getSubmitter().equalsIgnoreCase(user)                            )
                    {
                        sb.append(info.toString());
                    }
                }
                catch(NumberFormatException e)
                {
                    //query the job with submitter
                    List<JobInfo> info = monitor.getJobInfo(cmd[i]);
                    for(JobInfo jm:info)
                    {
                        if(group.equalsIgnoreCase("admin") ||
                            jm.getSubmitter().equalsIgnoreCase(user)                            )
                        {
                            sb.append(jm.toString());
                        }
                    }
                }
            }
        }
        return sb.toString();
    }
    public String usage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("query the status of job(s)\n");
        sb.append("COMMAND: j [job_id]\n");
        sb.append("EXAMPLE: \n\n");
        sb.append(">j 1\n");
        sb.append("query the status of job with id=1.\n\n");
        sb.append(">j 1 2\n");
        sb.append("query the status of job with id=1 and 2.\n\n");
        sb.append("\n\nNote: 'admin' can query all jobs, common user can only query his jobs.\n");
        return sb.toString();
    }

}
