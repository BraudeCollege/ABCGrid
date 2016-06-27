package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.master.conf.Config;
import pku.cbi.abcgrid.worker.Worker;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
/**
 * Manage connected ABCWorkers
 *
 * */
public class WorkerManager
{
    private ConcurrentMap<Long, WorkerInfo> workers =
            new ConcurrentHashMap<Long, WorkerInfo>();
    private AtomicLong grid_id = new AtomicLong(1L);
    private Config config = Config.getInstance();
    private String updateinfo = null;
    private static WorkerManager instance = new WorkerManager();
    private WorkerManager()
    {
        updateinfo = config.loadUpdateConfig();
        //ws = new HashMap<Long,WorkerInfo>();
    }
    public static WorkerManager getInstance()
    {
        return instance;
    }
    /**
     * get the number of connected worker nodes.
     *
     * @return number of worker nodes
     * */
    public int getWorkerNum()
    {
        return workers.size();
    }
    /**
     * unregister a worker node.
     *
     * @param worker_id ID of worker node. 
     * */
    public void unregister(long worker_id)
    {
        //supported_services.remove(identifier);
        workers.remove(worker_id);
    }
    /**
     * register a worker node.
     *
     * @param capability The worker node's capability (including supported services, CPU, Memeory, etc.
     *                   reserved for future use.) 
     * @param services Services supported by the registered Worker
     * @param callback callback interface used to communicate with node
     * @return id of the node, a long value.
     */
    public long register(String capability, List<String> services, Worker callback)
    {
        long workerid = grid_id.getAndIncrement();
        workers.put(workerid, new WorkerInfo(capability,services,callback));
        //supported_services.put(workerid, new ArrayList<String>(services));

        //keep worker node updated.
        update(callback);
        return workerid;
    }
    /**
     * return a WorkerInfo
     *
     * @param worker_id ID of that worker node
     * @return a WorkerInfo 
     * */
    public WorkerInfo getWorkerInfo(long worker_id)
    {
        return workers.get(worker_id);
    }

    /**
     * return all WorkerInfo
     *
     * @return List of WorkerInfo 
     * */
    public List<WorkerInfo> getWorkerInfo()
    {
        //Set<Long> keys = workers.keySet();
        List<WorkerInfo> ret = new ArrayList<WorkerInfo>();
        for(Map.Entry<Long,WorkerInfo> wi:workers.entrySet())
        {
            Long k = wi.getKey();
            WorkerInfo inf = wi.getValue();
            try
            {
                String worker_status = inf.getWorker().status();
                inf.setStatus(worker_status);
                ret.add(inf);
            }
            catch(RemoteException e)
            {
                workers.remove(k);
                //e.printStackTrace();
            }

        }
        return ret;
    }
    /**
     * get a worker node's supported service.
     *
     * @param worker_id ID of that worker node 
     * @return Name of supported services 
     * */
    public List<String> getSupportedService(long worker_id)
    {
        return getWorkerInfo(worker_id).getServices();
    }

    /**
     * test if a service is supported by a worker node.
     *
     * @param worker_id ID of that worker node
     * @param service Name of the service
     * @return true->supported
     * */
    public boolean isServiceSupported(long worker_id, String service)
    {
        List<String> services = getWorkerInfo(worker_id).getServices();
        for (String s : services)
        {
            if (s.equalsIgnoreCase(service))
                return true;
        }
        return false;
    }
    /**
     * update local information on a worker node.
     * @param worker_id ID of that worker node
     * @param services Name of services
     * */
    public void updateService(long worker_id, List<String> services)
    {
        workers.get(worker_id).setServices(services);
    }

    /**
     * Update a worker node.
     *
     * @param worker A Worker 
     */
    public void update(Worker worker)
    {
        try
        {
            //WorkerManager workers = WorkerManager.getInstance();
            if (updateinfo == null)
                updateinfo = config.loadUpdateConfig();
            worker.update(updateinfo);
        }
        catch (RemoteException e)
        {
            //System.err.println("Error in update :"+e);
        }
    }

    /**
     * Update all worker nodes.
     */
    public void update()
    {
        updateinfo = config.loadUpdateConfig();
        for (WorkerInfo w : workers.values())
        {
            try
            {
                w.getWorker().update(updateinfo);
            }
            catch (RemoteException e)
            {
                //e.printStackTrace();
            }
        }

    }
    /**
     * check the status of a worker node.
     *
     * @param worker_id ID of that worker node
     *  
     * */
    public void status(long worker_id)
    {
        try
        {
            if (worker_id <= 0)
            {
                for (WorkerInfo w : workers.values())
                {
                    w.getWorker().status();
                }
            }
            else
            {
                WorkerInfo w = workers.get(worker_id);
                if (w != null)
                {
                    w.getWorker().status();
                }
            }
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }

    }
    /**
     * kill a running task in worker node.
     *
     * @param job_id ID of job
     * @param task_id ID of task 
     * */
    public void killWorkerTask(long job_id,long task_id)
    {
        for(WorkerInfo wi:getWorkerInfo())
        {
            try
            {
                String msg = wi.getWorker().cancelTask(job_id,task_id);
            }
            catch(RemoteException e)
            {

            }
        }
    }
}
