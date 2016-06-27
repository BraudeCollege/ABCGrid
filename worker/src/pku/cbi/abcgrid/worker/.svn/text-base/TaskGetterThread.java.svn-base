package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.master.Task;
import pku.cbi.abcgrid.worker.conf.Config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * one thread per service (NCBI, HMMER, CE, etc.)
 *
 * */
public class TaskGetterThread extends Thread
{
    private BlockingQueue<Task> queue;
    private String              service;
    private Log                 logger;
    public TaskGetterThread(BlockingQueue<Task> q, String s)
    {
        queue = q;
        service = s;
        logger = LogFactory.getLog(TaskGetterThread.class);
    }
    /**
     * stop this thread.
     */
    public void cancel()
    {
        interrupt();
    }
    public void run()
    {
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                Task t = MasterProxy.getTask(service);
                if (t != null)
                {
                    queue.put(t);
                }
            }
        }
        catch (InterruptedException e)//queue.put
        {
            if (Config.isVerbose())
            {
                logger.error("Interrupted putQueue");
            }
        }
        catch (RemoteException e)//MasterProxy.getTask
        {
            if (Config.isVerbose())
            {
                logger.error("Interrupted getTask");
            }
        }
    }
}
