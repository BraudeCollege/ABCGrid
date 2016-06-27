package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import pku.cbi.abcgrid.worker.conf.Config;
import pku.cbi.abcgrid.worker.conf.MasterAddress;

public class Console
{
    Log logger;
    public Console()
    {
        logger = LogFactory.getLog(Console.class);

    }

    /*
    * start the Master application.
    *
    *@param master_to_connect The Master's address.
    * 
    * */
    public void start(MasterAddress master_to_connect)
    {
        if(Config.isVerbose())
        {
            String sz = String.format("ABCWorker version:%s\nHOME: %s\nABCMaster: %s",
                    Config.getVersion(),Config.getHome(),Config.getMasterAddress());
            System.out.println(sz);
        }
        //connect to Master
        MasterProxy.connect(master_to_connect);
        JobManager jm = JobManager.getInstance();
        //start task runner threads
        jm.startTaskRunner();
    }
    /**
     * Main entry of Worker application.
     *
     * */
    public static void main(String[] args)
    {
        Console con = new Console();
        MasterAddress master_to_connect;
        master_to_connect = parseArgs(args);
        if (master_to_connect == null)
        {
            printUsageAndExit(null);
        }
        else
        {
            Config.setMasterAddress(master_to_connect);
        }
        con.start(master_to_connect);
    }

    private static void printUsageAndExit(String error)
    {
        StringBuffer usage = new StringBuffer();
        if (error != null)
        {
            usage.append(error);
            usage.append("\n");
        }
        usage.append("Usage:\n");
        usage.append("java -jar worker.jar [-h ip_of_master] [-p port] [-v]\n\n");
        usage.append("Example:java -jar worker.jar -h 162.105.250.180\n");
        usage.append("This command will connect to Master which has IP=162.105.250.180,port=4170(default)\n\n");
        usage.append("Example:java -jar worker.jar -h 162.105.250.180 -p 1999\n");
        usage.append("This command will connect to Master which has IP=162.105.250.180,port=1999\n\n");
        System.out.println(usage.toString());
        System.exit(0);
    }

    private static MasterAddress parseArgs(String[] args)
    {
        MasterAddress ma = new MasterAddress();
        if (args.length < 2)
            return null;
        for (int i = 0; i < args.length; i++)
        {
            String s = args[i];
            if (s.equalsIgnoreCase("-h"))
            {
                String host = "";
                try
                {
                    host = args[i+1];
                    ma.setIp(host);
                }
                catch(Exception e)
                {
                    printUsageAndExit(String.format("host:[%s] is incorrect", host));
                }

            }
            if (s.equalsIgnoreCase("-p"))
            {
                String port="";
                try
                {
                    port = args[i + 1];
                    Integer.valueOf(port);
                    ma.setPort(port);
                }
                catch(Exception e)
                {
                    printUsageAndExit(String.format("port:[%s] is incorrect", port));
                }
            }
            if (s.equalsIgnoreCase("-s"))
            {
                String serv="";
                try
                {
                    serv = args[i+1];
                    ma.setName(serv);
                }
                catch(Exception e)
                {
                    printUsageAndExit(String.format("service:[%s] is incorrect", serv));
                }
            }
            if (s.equalsIgnoreCase("-v"))
            {
                Config.setVerbose();
            }
            if (s.equalsIgnoreCase("-ver"))
            {
                System.out.println(Config.getVersion());
                System.exit(0);
            }
            if (s.equalsIgnoreCase("-a"))
            {
                //if use all CPU.
                Config.setUseAllCPU();
            }
        }
        return ma;
    }
}
