package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pku.cbi.abcgrid.master.Task;

public class WorkerImpl extends UnicastRemoteObject implements Worker
{

    private Log logger = LogFactory.getLog(WorkerImpl.class);
    JobManager jm = JobManager.getInstance();
    public WorkerImpl()throws RemoteException
    {
        logger  = LogFactory.getLog(WorkerImpl.class);
        jm      = JobManager.getInstance();
    }
    /**
     * Not used. reserved for future.
     * */
    public int notify(Task  task) throws RemoteException
    {
        return 0;
    }
    public void update(String info)throws RemoteException
    {
        //update applications and data
        try
        {
            UpdateManager.getInstance().update(info);
            //if any execptions occured, halt the system until got next update message.
        }
        catch (Exception e)
        {
            logger.error(e);
        }
    }
    /**
     * get the name of this worker.
     *
     * @return IP address of this grid
     * @throws RemoteException
     * */
    public String name()throws RemoteException
    {
        String ret = "Unknown";
        try
        {
            ret = java.net.InetAddress.getLocalHost().getHostAddress();
        }
        catch(Exception e)
        {
            logger.error(e);
        }
        return ret;
    }
    public String  status() throws RemoteException
    {
        //return Status.get()+", last error:"+Status.getLastError();
        return Status.get();
    }
    public String cancelTask(long job_id,long task_id)throws RemoteException
    {
        Status.set("kill job:"+job_id);
        return jm.killJob(job_id);
    }
}
