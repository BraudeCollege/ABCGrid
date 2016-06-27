package pku.cbi.abcgrid.worker.update;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */
import pku.cbi.abcgrid.worker.conf.Config;
import org.apache.commons.io.FilenameUtils;


/**
 * This class is corresponding to the <component></component> entity
 * in Master's update.conf.
 * 
 * */
public class DownloadableComponent
{
    private FTP ftp;
    private String service_name;
    private String component_type;
    private String component_file;
    private String component_path;
    public DownloadableComponent(FTP src, String s,String t,String fn,String p) {
        ftp = src;
        service_name = s;
        component_type = t;
        component_file = fn;
        component_path = p;
    }
    /**
     * get the infomation of FTP site used to download data from.
     *
     * @return An <code>FTP</code> object
     * */
    public FTP getFtpInfo() {
        return ftp;
    }
    /**
     * get file name in ftp.
     *
     * @return File name in ftp server.
     * */
    public String getSourceFile() {
        return component_file;
    }
    /**
     * get the service's name.
     *
     * @return Name of this service.
     *
     * */
    public String getServiceName()
    {
        return service_name ;
    }
    /**
     * get the component's name. Chop the postfix.
     *
     * @return Name of this component.
     * */
    public String getComponentName()
    {
        int pos = component_file.length();
        if(component_file.endsWith(".exe"))
            pos = component_file.indexOf(".exe");
        else if(component_file.endsWith(".tar.gz"))
            pos = component_file.indexOf(".tar.gz");
        else if(component_file.endsWith(".gz"))
            pos = component_file.indexOf(".gz");
        else if(component_file.endsWith(".zip"))
            pos = component_file.indexOf(".zip");
        else
            pos = pos;  
        return component_file.substring(0,pos);
    }
    /**
     * get the component's type.
     *
     * @return Name of this component's type.
     * */
    public String getComponentType()
    {
        return component_type;
    }
    public String getComponentPath()
    {
        return component_path;
    }
    public String toString()
    {
        return ftp.getIp()+"\\"+component_path+"\\"+component_file;
    }

    /**
     * get the full path to extract this componnet to.
     * the path is: $ABCGRID_WORKER_HOME/service/$service_name/$component_type/
     *
     * @return The full path.
     * */
    public String getLocalPathToExtractData()
    {
        String parent = Config.getServicePath();
        String servicepath = FilenameUtils.concat(parent,service_name);
        return FilenameUtils.concat(servicepath,component_type);
    }
}
