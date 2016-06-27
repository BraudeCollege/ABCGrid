package pku.cbi.abcgrid.worker.conf;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import java.util.Properties;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.net.InetAddress;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pku.cbi.abcgrid.worker.Util;
import pku.cbi.abcgrid.worker.SysInfo;

/**
 * The configuration utility class
 * */
public class Config
{
    private String HOME;
    private String os;
    private String arch;
    private String path_log;
    private String path_service;
    private String path_update;
    private String path_job;
    private String path_conf;
    private String version           = "0.1";
    private boolean verbose          = false;
    private boolean use_all_CPU      = false;
    private long    worker_node_id   = -1L;
    private Entry entry              = null;
    private MasterAddress masteraddr = null;
    private Digester digester        = null;


    private static Config inst       = new Config();
    private Log logger               = LogFactory.getLog(Config.class);
    private Config()
    {
        Properties pro = System.getProperties();
        String user_dir = pro.getProperty("user.dir");
        os = pro.getProperty("os.name");
        arch = pro.getProperty("os.arch");
        String home = System.getenv("ABCWORKER_HOME");
        if(home!=null)
            HOME = home;
        else
            HOME = new File(user_dir).getParent();
        //using in IDE
        //HOME = user_dir;
        
        path_log = FilenameUtils.concat(HOME,"log");
        path_service = FilenameUtils.concat(HOME,"service");
        path_update = FilenameUtils.concat(HOME,"update");
        path_job = FilenameUtils.concat(HOME,"job");
        path_conf = FilenameUtils.concat(HOME,"conf");
        loadConfiguration();
    }
    private void loadConfiguration()
    {
        try
        {
            String confile = FilenameUtils.concat(path_conf,"worker.conf");
            entry = parse(confile);
        }
        catch(Exception e)
        {
            //logger.error(e);
        }

    }
    private static Service getService(String name)
    {
        Services svrs = inst.entry.getServices(Config.getOS(),Config.getArch());
        return svrs.getService(name);
    }
    private Digester makerule()
    {
        Digester dg = new Digester();
        dg.setValidating(false);
        dg.addObjectCreate("grid",Entry.class);
        dg.addObjectCreate("grid/services", Services.class);
        dg.addSetProperties( "grid/services","os", "os" );
        dg.addSetProperties( "grid/services","arch", "type" );

        dg.addObjectCreate("grid/services/service", Service.class);
        dg.addSetProperties( "grid/services/service","name", "name" );

        dg.addObjectCreate("grid/services/service/component", Component.class);
        dg.addSetProperties( "grid/services/service/component","type", "type" );
        dg.addBeanPropertySetter("grid/services/service/component/name","name");
        dg.addBeanPropertySetter("grid/services/service/component/path","path");
        dg.addSetNext("grid/services/service/component","addComponent");
        dg.addSetNext("grid/services/service","addService"); //define in Services
        dg.addSetNext("grid/services","addServices"); //define in Entry
        return dg;
    }
    private Entry parse(String fname)
    {
        if(digester==null)
        {
            digester = makerule();
        }
        try
        {
            return (Entry)digester.parse(new FileInputStream(new File(fname)));
        }
        catch(Exception e)
        {
            //logger.error(e);
        }
        return null;
    }
    public static void reloadConfiguration()
    {
        inst.loadConfiguration();
    }
    public static void setMasterAddress(MasterAddress addr)
    {
        inst.masteraddr = addr;
        //inst.entry.setMasterAddress(addr);
    }
    public static MasterAddress getMasterAddress()
    {
        return inst.masteraddr;
    }
    /**
     * Get supported service in this machine.
     * */
    public static List<Service> getService()
    {
        Services svrs;
        if(inst.entry!=null)
        {
            svrs = inst.entry.getServices(Config.getOS(),Config.getArch());
            return svrs.getService();
        }
        return null;
    }

    /**
     * Get environmental variables of this service.
     * @param service name of the service
     * @return a Map, e.g. JAVA_HOME:/HOME/TO/JAVA 
     * */
    public static Map<String,String> getServiceEnv(String service)
    {
        //return getService(service).getEnv();
        return null;
    }
    /**
     * get the basic information of this computer.
     * */
    public static String getBasicInfo()
    {
        StringBuffer buffer = new StringBuffer(); 
        String host="hostname";
        String ip = "0.0.0.0";
        try
        {
            host = InetAddress.getLocalHost().getHostName();
            ip   = InetAddress.getLocalHost().getHostAddress();
            /*
            buffer.append("#########################################\n");
            buffer.append(String.format("Hostname:%s\n",host));
            buffer.append(String.format("IP:%s\n",ip));
            System.out.println("host and IP");
            buffer.append(String.format("Operating System:%s\n",getOS()));
            buffer.append(String.format("Architecture:%s\n",getArch()));
            buffer.append(String.format("Number of CPU:%d\n",SysInfo.getNumOfCPU()));
            buffer.append(String.format("Supported services:\n"));
            System.out.println("Before service");
            for(Service s:getService())
            {
                buffer.append("*************************\n");
                buffer.append(s.getName());
                buffer.append("\n");
                for(Component c:s.getComponent())
                {
                    buffer.append(String.format("component %s:%s\n",c.getType(),c.getName()));
                }
            }
            */
        }
        catch(Exception e)
        {
            if(Config.isVerbose())
                inst.logger.error(e);
        }
        return host+"("+ip+")"+"#"+getOS()+"#"+getArch();
        //return buffer.toString();
    }
    /***/
    public static Entry getEntry()
    {
        return inst.entry;
    }
    /**
     * get the JBGRID_WORKER_HOME env value.
     * */
    public static String getHome()
    {
        return inst.HOME;
    }
    public static String getServicePath()
    {
        return inst.path_service;
    }
    public static String getConfPath()
    {
        return inst.path_conf;
    }
    public static String getUpdatePath()
    {
        return inst.path_update;
    }
    public static String getJobPath()
    {
        return inst.path_job;
    }
    public static String getLibPath()
    {
        //return inst.path_job;
        return FilenameUtils.concat(getHome(),"lib");
    }
    /**
     * @return $JBGRID_WORKER_HOME/log
     * */
    public static String getLogPath()
    {
        return inst.path_log;
    }
    /**
     * @return verbose mode
     * */
    public static boolean isVerbose()
    {
        return inst.verbose;
    }
    public static void setVerbose()
    {
        inst.verbose = true;
    }
    public static boolean ifUseAllCPU()
    {
        return inst.use_all_CPU;
    }
    public static void setUseAllCPU()
    {
        inst.use_all_CPU = true;
    }
    public static long getWorkerNodeId()
    {
        return inst.worker_node_id;
    }
    public static void setWorkerNodeId(long v)
    {
        inst.worker_node_id = v;
    }

    /**
     * version of jbGridWorker
     * */
    public static String getVersion()
    {
        return inst.version;
    }
    /**
     * @return Operating System
     * */
    public static String getOS()
    {
        return inst.os;
    }
    /**
     * @return Computer architecture. i386,x86,x64 ...
     * */
    public static String getArch()
    {
        return inst.arch;
    }
    /*
    * -> /home/suny/worker/service/$servicename/$componentType
    * */
    public static String getComponentBasePath(String servicename,String componentType)
    {
        Service ss = getService(servicename);
        //base->$ABCGRID_WORKER_HOME/service/$servicename
        String base = FilenameUtils.concat(inst.path_service,servicename);
        return FilenameUtils.concat(base,componentType);
    }


    private static Map<String,String> comp_path = new HashMap<String, String>();
    /**
     * @param service_name "NCBI_BLAST","HMMER"
     * @param component_type e.g. "executable","data"
     * @param component_name e.g. "blastall","hmmpfam"
     * @return Full path of this service
     *
     * e.g -> /home/suny/worker/service/$servicename/$componentType/$componentName
     * */
    public static String getComponentPath(String service_name, String component_type, String component_name)
    {
        String p = comp_path.get(component_name);
        if(p==null)
        {
            //cmd is case-sensitive in UNIX-like system.
            Service ss = getService(service_name);
            String base = FilenameUtils.concat(inst.path_service,service_name);
            String comppath = ss.getComponentPath(component_type,component_name);
            p = FilenameUtils.concat(base,comppath);
            comp_path.put(component_name,p);
        }
        return p;
    }
}
