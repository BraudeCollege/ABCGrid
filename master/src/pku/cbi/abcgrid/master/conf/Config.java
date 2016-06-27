package pku.cbi.abcgrid.master.conf;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import org.apache.commons.digester.Digester;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * configuration.
 */
public class Config
{
    private String VERSION = "0.1";
    private String HOME = ".";
    private String CONF_DIR = "";
    private String LOG_DIR = "";
    private String USER_DIR = "";
    public boolean DEBUG = true;
    private static Config inst = new Config();
    private Entry entry;

    private Config()
    {
        String home = System.getenv("ABCMASTER_HOME");
        if (home != null)
            HOME = home;
        else
        {
            String curr = System.getProperty("user.dir");
            HOME = new File(curr).getParent();
            //if IDE mode
            //HOME = curr;
        }
        CONF_DIR = FilenameUtils.concat(HOME, "conf");
        LOG_DIR = FilenameUtils.concat(HOME, "log");
        USER_DIR = FilenameUtils.concat(HOME, "user");
        try
        {
            parseMasterConf();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            System.exit(0);
        }

    }

    /**
     * Single instance factory.
     *
     * @return An instance of Config
     */
    public static Config getInstance()
    {
        return inst;
    }

    public Entry getEntry()
    {
        return entry;
    }

    /**
     * parse conf/master.conf.
     *
     * @throws Exception Digester
     */
    private void parseMasterConf() throws Exception
    {

        Digester parser = new Digester();
        parser.setValidating(false);

        parser.addObjectCreate("master", Entry.class);

        parser.addObjectCreate("master/port", Port.class);
        parser.addCallMethod("master/port", "setPort", 2);
        parser.addCallParam("master/port/worker", 0);
        parser.addCallParam("master/port/user", 1);
        parser.addSetNext("master/port", "setPort");

        parser.addObjectCreate("master/auth/user", User.class);
        parser.addCallMethod("master/auth/user", "setUser", 6);
        parser.addCallParam("master/auth/user/name", 0);
        parser.addCallParam("master/auth/user/password", 1);
        parser.addCallParam("master/auth/user/address", 2);
        parser.addCallParam("master/auth/user/priority", 3);
        parser.addCallParam("master/auth/user/group", 4);
        parser.addCallParam("master/auth/user/home", 5);
        parser.addSetNext("master/auth/user", "addUser");

        String confile = FilenameUtils.concat(getConfDir(), "master.conf");
        entry = (Entry) parser.parse(confile);
    }

    /**
     * get the IP address of local machine.
     *
     * @return IP address e.g. "162.105.250.180"
     * @throws SocketException Can not get IP address
     */
    public String getLocalIPAddress() throws SocketException
    {
        //String ipaddr = InetAddress.getLocalHost().getHostAddress();
        //if(! ipaddr.equalsIgnoreCase("127.0.0.1"))
        //{
        //    return ipaddr;
        //}
        Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();
        while (eni.hasMoreElements())
        {
            NetworkInterface face = eni.nextElement();
            Enumeration<InetAddress> ei = face.getInetAddresses();
            while (ei.hasMoreElements())
            {
                InetAddress i = ei.nextElement();
                if (i.isSiteLocalAddress() || i.isLoopbackAddress())
                {
                    //local address
                }
                else
                {
                    return i.getHostAddress();
                }
            }
        }
        return null;
    }

    /**
     * get the address in the format of JavaRMI's address URI.
     *
     * @return //IP_ADDRESS:PORT/SERVICE
     * @throws SocketException throwed by getLocalIPAddress()  
     */
    public String getRMIAddress() throws SocketException
    {
        StringBuffer addr = new StringBuffer(512);
        addr.append("//");
        addr.append(getLocalIPAddress());
        addr.append(":");
        addr.append(entry.getWorkerPort());
        addr.append("/");
        addr.append("ABCGrid");
        return addr.toString();
    }

    /**
     * get the update.conf from the master node.
     *
     * @return Content of update.conf
     */
    public String loadUpdateConfig()
    {
        try
        {
            String fupdate = FilenameUtils.concat(getConfDir(), "update.conf");
            File f = new File(fupdate);
            char[] buffer = new char[(int) f.length()];
            Reader r = new FileReader(f);
            r.read(buffer);
            r.close();
            return new String(buffer);
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        return null;
    }

    /**
     * get the port number used to connect ABCWorker and ABCMaster.
     *
     * @return port
     */
    public int getWorkerPort()
    {
        return entry.getWorkerPort();
    }

    /**
     * get the port number used to connect ABCUser and ABCMaster.
     *
     * @return port
     */
    public int getUserPort()
    {
        return entry.getUserPort();
    }

    /**
     * get HOME directory of ABCMaster.
     *
     * @return ABCMaster's HOME directory
     */
    public String getHome()
    {
        return HOME;
    }

    /**
     * directory to configuration files
     *
     * @return ABCMaster_HOME/conf
     */
    private String getConfDir()
    {
        return CONF_DIR;
    }

    /**
     * get the directory to log file
     *
     * @return ABCMASTER_HOME/log/
     */
    private String getLogPath()
    {
        return LOG_DIR;
    }

    /**
     * get a user's working directory.
     *
     * @param user Name of the user
     * @return working directory
     */
    public String getUserDir(String user)
    {
        String user_home = entry.getUserHome(user).trim();
        if (user_home.equalsIgnoreCase(""))
        {
            return FilenameUtils.concat(USER_DIR, user);
        }
        else
        {
            if (FilenameUtils.getPrefixLength(user_home) > 0)
            {
                //absolute path
                return user_home;
            }
            else
            {
                //relative path, should be "ABCMASTER_HOME/user/$user_name"
                return FilenameUtils.concat(USER_DIR, user_home);
            }
        }
    }

    /**
     * get the version of ABCMaster
     *
     * @return version
     */
    public String getVersion()
    {
        return VERSION;
    }
}
