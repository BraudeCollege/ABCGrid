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

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;


/**
 * The main entry of this application.
 * */
public class GridMain
{
    private Log     logger = LogFactory.getLog(GridMain.class);
    private Config  config = Config.getInstance();
    private Master  center = null;
    private String  host;
    private String  rmi_address;
    //private boolean pseudo_user = false;
    CommandHandler  cmdparser;
    public GridMain()
    {
        
        cmdparser = CommandHandler.getInstance();
        try
        {
            center = new MasterImpl();
            host = config.getLocalIPAddress();
            rmi_address = config.getRMIAddress();
            LocateRegistry.createRegistry(config.getWorkerPort());
        }
        catch (java.rmi.RemoteException e)
        {
            logger.error(e);
            System.exit(-1);
        }
        catch (Exception e)
        {
            logger.error(e);
            System.exit(-1);
        }
    }
    /**
     * start the ABCMaster server.
     * */
    public void start()
    {
        try
        {
            System.out.println("Starting ABCMaster on :"+rmi_address);
            Naming.rebind(rmi_address,center);
        }
        catch (Exception e)
        {
            logger.error(e);
            System.exit(-1);
        }
    }
    /**
     * stop the ABCMaster server.
     * */
    public void stop()
    {
        try
        {
            Naming.unbind(rmi_address);
        }
        catch (Exception e)
        {
            System.exit(-1);
            //logger.error(e);
        }
    }
    /**
     * show a console window to read commands.
     * */
    private void showConsole()
    {
        byte[] cmdbuffer = new byte[512];
        String localinfo = String.format("ip=%s, worker_port=%d, user_port=%d",
                host,config.getWorkerPort(),config.getUserPort());
        //basic information of master node.
        System.out.println("ABCMaster version:"+config.getVersion());
        System.out.println(localinfo);
        System.out.print(">>>");
        String cmd;
        while (true)
        {
            try
            {
                int rd = System.in.read(cmdbuffer);
                if (rd > 0)
                {
                    cmd = new String(cmdbuffer, 0, rd).trim();
                    if(cmd.equalsIgnoreCase("quit")||
                            cmd.equalsIgnoreCase("bye"))
                    {
                        try
                        {
                            System.out.print("Quit the system will discard all running jobs," +
                                    "continue to quit? (y/n) : ");
                            rd = System.in.read(cmdbuffer);
                            if (rd > 0)
                            {
                                cmd = new String(cmdbuffer, 0, rd).trim();
                                if(cmd.equalsIgnoreCase("y")||
                                   cmd.equalsIgnoreCase("yes"))
                                break;
                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        String default_user = "admin";
                        System.out.println(cmdparser.handle(default_user,cmd));
                    }
                }
            }
            catch (IOException e)
            {
                logger.error(e);
            }
            System.out.print(">>>");
        }
    }

    /**
     * start the server that listen and accept requests from ABCUser
     * */
    private void startRequestServer()
    {
        new RequestServer().start();
    }

    public static void main(String[] args)
    {

        GridMain gm = new GridMain();
        gm.start();
        gm.startRequestServer();
        gm.showConsole();
        gm.stop();
        System.exit(0);
    }
}
