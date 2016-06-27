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
import java.rmi.ConnectException;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import pku.cbi.abcgrid.master.Master;
import pku.cbi.abcgrid.master.Task;
import pku.cbi.abcgrid.worker.conf.MasterAddress;
import pku.cbi.abcgrid.worker.conf.Config;
import pku.cbi.abcgrid.worker.conf.Service;

/**
 * This class is used by Worker as the Proxy to communicate with Master.
 * Most of method are static.
 */
public class MasterProxy
{
    private static Master           master = null;
    private static Worker           worker = null;
    private static long             my_id = -1;
    private static volatile boolean isConnected = false;
    private static Log logger = LogFactory.getLog(MasterProxy.class);
    static
    {
        try
        {
            worker = new WorkerImpl();
        }
        catch (RemoteException e)
        {
            logger.error(e);
        }
    }

    public static void setWorker(Worker w)
    {
        worker = w;
    }
    /**
     * connect to Master.
     *
     * @param masteraddr Address of Master.
     * */
    public static synchronized void connect(MasterAddress masteraddr)
    {
        if (isConnected())
            return;
        if (masteraddr == null)
        {
            masteraddr = Config.getMasterAddress();
            if (masteraddr == null)
            {
                logger.error("Failed to get Master address from configuration," +
                        "please restart this application");
                System.exit(-1);
            }
        }
        String uri = masteraddr.getURI();
        if(Config.isVerbose())
            logger.info("Connecting to " + uri);
        while (true)
        {
            try
            {
                master = (Master) java.rmi.Naming.lookup(uri);
                if (master != null)
                {
                    //logger.info("Successfully connect to "+uri);
                    my_id = register();
                    isConnected = true;
                    break;
                }
            }
            catch (Exception e)
            {
                try
                {
                    //wait for 3 seconds to try next connection
                    Thread.sleep(3000);
                }
                catch (Exception ex)
                {
                    //String err = ex.getMessage();
                }
            }
        }
        Config.setWorkerNodeId(my_id);
        if(Config.isVerbose())
        {
            logger.info("Connecting is [OK]");
        }

    }

    /**
     * If the connection betwen Worker and Master is OK
     *
     * @return true for connected
     * */
    public static boolean isConnected()
    {
        return isConnected;
    }

    /**
     * Set the connection flag
     *
     * */
    public static void setConnected(boolean b)
    {
        isConnected = b;
    }

    /**
     * Register to Master.
     *
     *
     * @return identity. Assigned by Master. Worker will use this long value
     * as identity for later communications.
     * */
    public static long register()
    {
        //grid_worker = callback;
        String mycapability = Config.getBasicInfo();
        List<Service> svrs = Config.getService();
        List<String> local_service = new ArrayList<String>();
        if (svrs != null)
        {
            for (Service ss : svrs)
            {
                local_service.add(ss.getName());
            }
        }
        long workerid = 0L;
        try
        {
            //register myself to Master.
            workerid = master.register(mycapability, local_service, worker);
        }
        catch (RemoteException e)
        {
            if(Config.isVerbose())
                logger.error(e);
        }
        return workerid;
    }

    /**
     * Unegister to Master.
     *
     * @return Not used.
     *
     * */
    public static int unregister()
    {
        int value = -1;
        try
        {
            master.unregister(my_id);
            //if(Config.isVerbose())
            //logger.info("unregistered to master");
        }
        catch (RemoteException e)
        {
            logger.error(e);
        }
        return value;
    }

    /**
     *Tell Master that supported services are updated.  
     * */
    public static void updateService()
    {
        //Status.set("update supported services in Master");
        List<Service> svrs = Config.getService();
        List<String> local_service = new ArrayList<String>();
        if (svrs != null)
        {
            for (Service ss : svrs)
            {
                local_service.add(ss.getName());
            }
        }
        try
        {
            master.updateService(my_id, local_service);
        }
        catch (ConnectException e)
        {
            logger.error("Connection lost,try to reconnect to Master: " + Config.getMasterAddress().getURI());
            setConnected(false);
            connect(null);
        }
        catch (RemoteException e)
        {
            logger.error(e);
        }

    }
    /**
     *try to get a task.
     *
     * @param service Service type of task.
     * @return A Task Object.
     * @throws RemoteException This method is blocked when there is no pending tasks
     * on Master's task queue. A broken connection may leads to RemoteException.  
     * */
    public static Task getTask(String service)
            throws RemoteException
    {
        Task task;
        while (true)
        {
            try
            {
                Status.set("idle");
                task = master.getTask(my_id, service);
                if (task != null)
                {
                    if (Config.isVerbose())
                    {
                        //String msg = String.format("<---[GET]\t Task[job_id=%d,task_id=%d,service=%s]",
                        //        task.getJobId(), task.getTaskId(), task.getServiceName());
                        //logger.info(msg);
                    }
                    return task;
                }
            }
            catch (RemoteException e)
            {
                //JobManager.getInstance().stopTaskGetter();
                logger.error("proxy.getTask RemoteException,try to reconnect to Master: " + Config.getMasterAddress().getURI());
                setConnected(false);
                connect(null);
                throw new RemoteException("get Task");
            }
        }
    }
    /**
     *try to transfer a task's result file to Master.
     *
     * @param job_id Job identity of the task.
     * @param task_id Task identity of the task.
     * @param command command of the task
     * @param fileName Name of the task's result file
     * @param fileData Data of the task's result file
     * 
     * */

    public static void putResultFile(long job_id, long task_id,String command, String fileName, byte[] fileData)
    {
        try
        {
            master.putResultFile(my_id, job_id, task_id, command, fileName, fileData);
            if (Config.isVerbose())
            {
                //String msg = String.format("--->[RET]\t Task[job_id=%d,task_id=%d,service=%s,length=%d]",
                //        job_id,task_id,servrice,result.length);
                //logger.info(msg);
            }
        }
        catch (ConnectException e)
        {
            logger.error("Connection lost,try to reconnect to Master: " + Config.getMasterAddress().getURI());
            setConnected(false);
            connect(null);
        }
        catch (RemoteException e)
        {
            logger.error(e);
        }

    }
    /**
     *try to transfer a task's standard output message to Master.
     *
     * @param job_id Job identity of the task.
     * @param task_id Task identity of the task.
     * @param command command of the task
     * @param stdout The output message
     *
     * */
    public static void putStdout(long job_id, long task_id, String command, byte[] stdout)
    {
        try
        {
            master.putStdout(my_id, job_id, task_id, command, stdout);
        }
        catch (ConnectException e)
        {
            logger.error("Connection lost,try to reconnect to Master: " + Config.getMasterAddress().getURI());
            setConnected(false);
            connect(null);
        }
        catch (RemoteException e)
        {
            logger.error(e);
        }

    }
    /**
     *try to transfer a task's standard error message to Master.
     *
     * @param job_id Job identity of the task.
     * @param task_id Task identity of the task.
     * @param command command of the task
     * @param stderr The error message
     *
     * */
    public static void putStderr(long job_id, long task_id,String command, byte[] stderr)
    {
        try
        {
            master.putStderr(my_id, job_id, task_id, command, stderr);
        }
        catch (ConnectException e)
        {
            logger.error("Connection lost,try to reconnect to Master: " + Config.getMasterAddress().getURI());
            setConnected(false);
            connect(null);
        }
        catch (RemoteException e)
        {
            logger.error(e);
        }
    }
}

