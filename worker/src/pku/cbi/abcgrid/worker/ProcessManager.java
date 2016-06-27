package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.worker.conf.Config;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**Control processes.
 *
 * To run a task, a process is created using ProcessBuilder.
 * This process can be destroyed before it exist normally.
 *
 * For example, the ABCMaster send a "kill" command to a running job.
 * */
public class ProcessManager
{
    private ConcurrentMap<Long, List<Process>> process_map;
    private Log logger;
    private static ProcessManager inst = new ProcessManager();
    private ProcessManager()
    {
        logger = LogFactory.getLog(Console.class);
        process_map = new ConcurrentHashMap<Long,List<Process>>();
    }
    public static ProcessManager getInstance()
    {
        return inst;
    }
    /**
     * add a running process.
     *
     * @param job_id job_id of the task that the process is running
     * @param task_id task_id of the task that the process is running
     * @param p The <code>java.lang.Process</code> object
     * */
    public void add(long job_id,long task_id,Process p)
    {
        //Long[] key = {job_id,task_id};
        List<Process> px = process_map.get(job_id);
        if(px==null)
        {
            px = new ArrayList<Process>();
        }
        px.add(p);
        process_map.put(job_id,px);
        //process_queue.put(new TaskProcess(job_id,task_id,ps));
    }
    public void remove(long job_id)
    {
        process_map.remove(job_id);
        //process_queue.put(new TaskProcess(job_id,task_id,ps));
    }
    public List<Process> get(long job_id)
    {
        return process_map.get(job_id);
    }
    /**
     * kill the process which is running a task.
     *
     * @param job_id job_id of this task.
     * @param task_id task_id of this task.
     * @return A message denote the status.
     * */
    public String kill(long job_id,long task_id)
    {
        List<Process> pcs = get(job_id);
        int number = 0;
        if(pcs!=null)
        {
            for(Process p:pcs)
            {
                p.destroy();
                number += 1;
            }
        }
        process_map.remove(job_id);
        return "Killed:"+number+" processes";
    }

}
