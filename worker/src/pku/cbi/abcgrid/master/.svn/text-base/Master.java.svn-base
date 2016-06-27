package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.worker.Worker;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Master extends Remote
{
    public long register(String capability, List<String> services,Worker callback)throws RemoteException;
    public void  unregister(long lValue)throws RemoteException;
    public void updateService(long worker_id, List<String> services)throws RemoteException;
    public Task getTask(long worker_id,String preferService) throws RemoteException;
    public void  putResultFile(long worker_id,long job_id,long task_id,String service,String fileName,byte[] fileData) throws RemoteException;
    public void  putStdout(long worker_id,long job_id,long task_id,String command,byte[]  stdout) throws RemoteException;
    public void  putStderr(long worker_id,long job_id,long task_id,String command,byte[]  error) throws RemoteException;
}
