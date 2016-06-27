package pku.cbi.abcgrid.worker.conf;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.worker.update.FTP;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * The configuration entry for update.conf from Master.
 * update.conf has many <services></services> elements which each of them
 * refer to a platform_os architecture.
 *
 * but in worker.conf, there is only one <services></services> element refer
 * to the architecture of local machine.
 *
 */
public class Entry
{
    private List<Services> services_list = new ArrayList<Services>();
    private List<FTP> ftps = new ArrayList<FTP>();
    public void addFtp(FTP rs)
    {
        ftps.add(rs);
    }
    /**
     * get all FTP sites.
     * */
    public FTP[] getFTP()
    {
        FTP[] ss = new FTP[ftps.size()];
        ftps.toArray(ss);
        return ss;
    }
    public FTP getFTP(String name)
    {
        for(FTP ss:ftps)
        {
            if(ss.getName().equalsIgnoreCase(name))
            {
                return ss;
            }
        }
        return null;
    }
    public void addServices(Services rs)
    {
        services_list.add(rs);
    }
    /**
     * get all supported Service.
     * */
    public Services[] getServices()
    {
        Services[] ss = new Services[services_list.size()];
        services_list.toArray(ss);
        return ss;
    }
    /**
     * get a Service by given name.
     * */
    public Services getServices(String os,String arch)
    {
        for(Services ss:getServices())
        {
            if(ss.getOs().equalsIgnoreCase(os)&&
                    arch.equalsIgnoreCase(arch))
            {
                return ss;
            }
        }
        return null;
    }
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        for(Services svrs:services_list)
        {
            buf.append(svrs.toString());
        }
        return buf.toString();
    }

}
