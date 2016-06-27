package pku.cbi.abcgrid.worker.conf;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

public class MasterAddress {
    private String ip ;
    private String port;
    private String name;
    public MasterAddress()
    {
        port = "4170";
        name = "ABCGrid";
    }
    public String getURI()
    {
        return toString();
    }
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
    public void setName(String val)
    {
        name = val;
    }
    public String getName()
    {
        return name;
    }
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("//");
        sb.append(ip);
        sb.append(":");
        sb.append(port);
        sb.append("/");
        sb.append(name);
        return sb.toString();
    }
}
