package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

/**
 * A Task result may contain one or more  output files.
 */
public class TaskResult
{
    long worker_id;
    long job_id;
    long task_id;
    String filename;
    byte[] filedata;
    public TaskResult(long wid,long groupid,long taskid,String fname,byte[] fdata)
    {
        worker_id = wid;
        job_id = groupid;
        task_id = taskid;
        filename = fname;
        filedata = fdata;
    }
    public long getWorkerId()
    {
        return worker_id;
    }
    public long getJobId()
    {
        return job_id;
    }

    public long getTaskId()
    {
        return task_id;
    }

    public String getFileName()
    {
        return filename;
    }
    public byte[] getFileData()
    {
        return filedata;
    }
    public boolean isSame(TaskResult b)
    {
        if(b!=null)
        {
            String fname = b.getFileName();
            if(filename!=null && fname!=null )
                return fname.equalsIgnoreCase(filename);
        }
        return false;
    }
    public String toString()
    {
        return String.format("%d %d %d %s %d",worker_id,job_id,task_id,filename,filedata.length);
    }
}
