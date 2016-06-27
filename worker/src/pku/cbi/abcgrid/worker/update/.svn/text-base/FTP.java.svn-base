package pku.cbi.abcgrid.worker.update;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */
import org.apache.commons.io.FilenameUtils;

/**
 * Created by IntelliJ IDEA.
 * User: suny
 * Date: 2006-6-14
 * Time: 1:28:29
 * To change this template use File | Settings | File Templates.
 */
public class FTP
{
    private String name;
    private String ip;
    private String port;
    private String user;
    private String password;
    private String basepath;
    private String pasv;
    //private String timeout;

    //public String getTimeout()
    //{
    //    return timeout;
    //}
    //public void setTimeout(String timeout) {
    //    this.timeout = timeout;
    //}

    //public String getPasv() {
    //    return pasv;
    //}

    //public void setPasv(String passive) {
    //    this.pasv = passive;
    //}

    public void setIp(String val)
    {
        ip = val;
    }
    public String getIp()
    {
        return ip;
    }
    public void setPort(String val)
    {
        port = val;
    }
    public String getPort()
    {
        return port;
    }
    public void setUser(String val)
    {
        user = val;
    }
    public String getUser()
    {
        return user;
    }
    public void setPassword(String val)
    {
        password = val;
    }
    public String getPassword()
    {
        return password;
    }
    public void setBasepath(String val)
    {
        basepath = val;
    }
    public String getBasepath()
    {
        int prefix = FilenameUtils.getPrefixLength(basepath);
        if(prefix>0)
        {
            basepath = basepath.substring(prefix);
        }
        return FilenameUtils.getFullPathNoEndSeparator(basepath)+"/";
    }
    public void setPasv(String val)
    {
        pasv = val;
    }
    public String getPasv()
    {
        return pasv;
    }
    public void setName(String val)
    {
        name = val;
    }
    public String getName()
    {
        return name;
    }
}
