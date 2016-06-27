package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pku.cbi.abcgrid.worker.update.UpdateThread;

/**
 * Manage the update of local services(including applications and databases)
 * 
 *
 * ftp://anonymous:anonymous@ftp.ncbi.nih.gov/blast/db/pdb.tar.gz --->
 * update/pdb.tar.gz ---> service/$service/db/*
 */
public class UpdateManager
{
    /**
     * Ala -> i386-linux
     * fas -> amd64-linux
     * Suny-> X86-Windows Xp
     * Yezq-> i386-Linux
     * Licy-> ppc-Mac OS X
     */
    //ia32 : i386,x86
    //x64  : amd64
    private Log                     logger;
    private JobManager              jm;
    private static UpdateManager    inst = new UpdateManager();
    
    private UpdateManager()
    {
        logger = LogFactory.getLog(UpdateManager.class);
        jm = JobManager.getInstance();
    }
    /**
     * Single instance factory.
     *
     * @return <code>UpdateManager</code> 
     * */
    public static UpdateManager getInstance()
    {
        return inst;
    }
    
    class WaitUpdateFinishThread extends Thread
    {
        private Thread update_thread;
        public WaitUpdateFinishThread(Thread uthread)
        {
            update_thread = uthread;
        }
        public void run()
        {
            try
            {
                Thread.sleep(3000);
                //wait for the update thread to exit.
                update_thread.join();
                //sleep 3 seconds.
                //MasterProxy.updateService();
                //restart the task getter(stop old and start new)
                jm.restartTaskGetter();
            }
            catch(Exception e)
            {
            }
        }
    }

    /**
     * @param info The information for update. Coming from file "update.conf" in Master.
     *
     * */
    public void update(String info)
    {
        Status.set("updating");
        //start the update thread
        Thread ut = new UpdateThread(info);
        ut.start();
        try
        {
            //JobManager.getJobManager().startTaskGetter();
            //wait for the UpdateThread to start.
            //Thread.sleep(1000);
        }
        catch(Exception e)
        {
        }
        //start the wait thread, wait the end of update thread.
        new WaitUpdateFinishThread(ut).start();
    }
    /**
     * Test only
     * */
    public static void main(String[] args)
    {
        try
        {
            UpdateManager up = new UpdateManager();
            File f = new File("update.conf");
            char[] buffer = new char[(int) f.length()];
            Reader r = new FileReader(f);
            r.read(buffer);
            String master_conf = new String(buffer);
            up.update(master_conf);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
