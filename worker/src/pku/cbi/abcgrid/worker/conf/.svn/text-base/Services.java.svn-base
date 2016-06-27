package pku.cbi.abcgrid.worker.conf;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import java.util.List;
import java.util.ArrayList;

/**
 *
 * A Services class represent a "<services>" element defined in worker.conf
 *
  */
public class Services
{
    private List<Service> services = new ArrayList<Service>();
    private String os;
    private String arch;
    public void addService(Service s)
    {
        services.add(s);
    }
    public List<Service> getService()
    {
        return services;
    }
    public Service getService(String name)
    {
        for(Service svr:services)
        {
            if(svr.getName().equalsIgnoreCase(name))
                return svr;
        }
        return null;
    }
    /**
     * Operating System, Windows Xp, Linux, UNIX, Mac OS X ...
     * */
    public void setOs(String val)
    {
        os = val;
    }
    public String getOs()
    {
        return os;
    }
    /**
     * Architectue, AMD64,i386,x86 ...
     * */
    public void setArch(String val)
    {
        arch = val;
    }
    public String getArch()
    {
        return arch;
    }
    private boolean matchOS(String a)
    {
        String[] prefix1 = a.split("\\s");
        String[] prefix2 = os.split("\\s");
        return prefix1[0].equalsIgnoreCase(prefix2[0]);
    }
    private boolean matchArch(String a)
    {
        return a.equalsIgnoreCase(arch);
    }
    /**
     *
     * Check if matched to Operating System and Architecture.
     *
     * */
    public boolean equalOsArch(String os,String arch)
    {
        return matchOS(os)&&matchArch(arch);
    }
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        for(Service svr:services)
        {
            buf.append(svr.toString());
        }
        return buf.toString();
    }

}
