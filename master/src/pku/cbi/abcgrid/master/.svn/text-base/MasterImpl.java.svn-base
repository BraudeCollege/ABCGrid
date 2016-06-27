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
import pku.cbi.abcgrid.master.conf.Config;
import pku.cbi.abcgrid.worker.Worker;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class MasterImpl extends UnicastRemoteObject implements Master
{
    JobMonitor      monitor;
    JobScheduler    scheduler;
    JobCollector    collector;
    WorkerManager   workermgr;
    Config          config;
    Log             logger;

    public MasterImpl() throws RemoteException
    {
        logger    = LogFactory.getLog(MasterImpl.class);
        monitor   = JobMonitor.getInstance(); 
        scheduler = JobScheduler.getInstance();
        collector = JobCollector.getInstance();
        workermgr = WorkerManager.getInstance();
        config    = Config.getInstance();
    }
    /**
     *register a Worker.
     * 
     * Note: this method could be call more than once from same Worker.
     * After a Worker update itself(e.g. add support to new service),
     * the worker will call this method again to report latest supported services. 
     *
     * @param capability Worker's capability including CPU, OS, arch, free memory, free disk space, etc.
     * @param services list of service that this Worker supported.
     * @param callback A pointer which point to the Worker's <b>Worker</b> interface.
     * Master use this pointer to call mathods of Worker.
     * */
    public long register(String capability, List<String> services,Worker callback)throws RemoteException
    {
        //logger.info(callback.name()+" registered");
        return workermgr.register(capability,services,callback);
    }
    /**
     *unregister a Worker.
     *
     * @param worker_id Worker identifier
     * */
    public void  unregister(long worker_id)throws RemoteException
    {
        workermgr.unregister(worker_id);
    }

    /**
     * update supported services.
     * 
     * @param worker_id identifier of the Worker node.
     * @param services list of services' name.
     * */
    public void updateService(long worker_id, List<String> services)throws RemoteException
    {
        workermgr.updateService(worker_id,services);
    }

    /**
     *get a Task.
     *
     * @param worker_id identifier of the Worker node.
     * @param service App name, e.g. "NCBI_BLAST", "CE".
     *                This param could be null, which means that
     *                the worker does not specifiy a service,
     *                it depends on the Master to return a task
     *                which requested service is among all service
     *                supported by this worker. 
     * @return A Task
     * */
    public Task getTask(long worker_id,String service) throws RemoteException
    {
        try
        {
            //logger.info(t.getJobId()+","+t.getTaskId()+","+Util.join(" ",t.getCommand()));
            return scheduler.getTask(worker_id,service);
        }
        catch(Exception e)
        {
            return null;
        }
    }
    /**
     *task result.
     *
     * @param worker_id identifier of the Worker node
     * @param job_id job identifier
     * @param task_id task identifier
     * @param command Command of this task
     * @param fdata The computation result.
     * */
    public void  putResultFile(long worker_id,long job_id,long task_id,String command,String fname,byte[] fdata)
            throws RemoteException
    {
        collector.putResultFile(worker_id,job_id,task_id,fname,fdata);
    }

    /**
     *error message.
     *
     * @param worker_id identifier of the Worker node
     * @param job_id job identifier
     * @param task_id task identifier
     * @param command Command of this task
     * @param stderr error message
     * */
    public void  putStderr(long worker_id,long job_id,long task_id,String command,byte[]  stderr)
            throws RemoteException
    {
        System.err.println(new String(stderr));
        /*
        String submitter = monitor.getJobInfo(job_id).getJobSubmitter();
        String dir = config.getUserDir(submitter);
        String fstderr = FilenameUtils.concat(dir,String.format("%d_%d.stderr",job_id,task_id));
        try
        {
            //PrintWriter pw = new PrintWriter(new File(fstdout,"w"));
            PrintWriter pw = new PrintWriter(new File(fstderr));
            pw.write(new String(stderr));
        }
        catch(FileNotFoundException e)
        {
            logger.error(e);
        }
        */
    }
    /**
     *sandard output message.
     *
     * @param worker_id identifier of the Worker node
     * @param job_id job identifier
     * @param task_id task identifier
     * @param command Command of this task
     * @param stdout output message
     * */
    public void  putStdout(long worker_id,long job_id,long task_id,String command,byte[]  stdout)
            throws RemoteException
    {
        System.out.println(new String(stdout));
        /*
        String submitter = monitor.getJobInfo(job_id).getJobSubmitter();
        String dir = config.getUserDir(submitter);
        String fstdout = FilenameUtils.concat(dir,String.format("%d_%d.stdout",job_id,task_id));
        try
        {
            //PrintWriter pw = new PrintWriter(new File(fstdout,"w"));
            PrintWriter pw = new PrintWriter(new File(fstdout));
            pw.write(new String(stdout));
        }
        catch(FileNotFoundException e)
        {
            logger.error(e);
        }
        */
    }
}