package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import java.util.Map;

/**
 * Result of a task MAY consist of some output files,
 * and/or a standard output message and/or a standard error message. 
 *
  */
public final class Result
{
    long job_id;
    long task_id;
    //fileName: fileData
    Map<String,byte[]> output;
    String command;
    byte[] stdout;
    byte[] stderr;
    public Result(long jid,long tid,String[] cmd,
                  Map<String,byte[]> out,
                  byte[] sout,byte[] serr)
    {
        job_id = jid;
        task_id = tid;
        command = Util.join(cmd," ");
        output = out;
        stdout = sout;
        stderr = serr;
    }
    public long getJobId()
    {
        return job_id;
    }
    public long getTaskId()
    {
        return task_id;
    }
    public Map<String,byte[]> getData()
    {
        return output;
    }
    public String getCommand()
    {
        return command;
    }
    public byte[] getStdout()
    {
        return stdout;
    }
    public byte[] getStderr()
    {
        return stderr;
    }
}
