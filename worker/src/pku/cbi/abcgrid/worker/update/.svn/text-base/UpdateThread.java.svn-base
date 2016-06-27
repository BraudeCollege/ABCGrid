package pku.cbi.abcgrid.worker.update;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */
import pku.cbi.abcgrid.worker.conf.*;

import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdateThread extends Thread
{
    private String info;
    private Log logger;
    private String env_home_name;
    private String env_home_value;
    public UpdateThread(String inf)
    {
        logger = LogFactory.getLog(UpdateThread.class);
        info = inf;
    }
    /**
     * initilize local configuration and synchronized with master configuration.
     * do necessary updates.
     * <p/>
     * If one Componet bowth exist in MasterAddress and Worker, the component will be updated.
     * Otherwise, do nothing.
     */
    private List<DownloadableComponent> getDownloableComponents(Entry master_entry)
    {
        //iterate local services
        List<DownloadableComponent> cmps =
                new ArrayList<DownloadableComponent>();
        String myOS = Config.getOS();
        String myArch = Config.getArch();
        for (Services svrs : master_entry.getServices())
        {
            //make sure Operating System and Architecture(X86, PPC, X64 etc.) is same
            if (svrs.equalOsArch(myOS, myArch))
            {
                for (Service svr : svrs.getService())
                {
                    for (Component comp : svr.getComponent())
                    {
                        String ftpname = comp.getFtp();
                        FTP ftp = master_entry.getFTP(ftpname);
                        cmps.add(new DownloadableComponent(
                                ftp,//the ftp site to download data from.
                                svr.getName(),// service name
                                comp.getType(),//component type
                                comp.getName(),
                                comp.getPath()
                        )); //component file
                    }
                }
            }
        }
        return cmps;
    }
    
    /**create and write configuration file*/
    private void writeConfFile(List<DownloadableComponent> dcl)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" ?>\n");
        sb.append("<grid>\n");
        String services_tag = String.format(
                "<services os=\"%s\" arch=\"%s\">\n",
                Config.getOS(), Config.getArch());
        sb.append("\t");
        sb.append(services_tag);
        String current_service = null;
        for (DownloadableComponent d : dcl)
        {
            String service_name = d.getServiceName();
            String component_name = d.getComponentName();
            String component_type = d.getComponentType();
            String component_path = d.getComponentPath();
            String service_tag = String.format("<service name=\"%s\">\n", service_name);
            if (! service_tag.equalsIgnoreCase(current_service))
            {
                if(current_service!=null)
                    sb.append("\t\t</service>\n");

                sb.append("\t\t");
                sb.append(service_tag);
                current_service = service_tag;
            }
            String component_tag = String.format(
                    "<component type=\"%s\">\n", component_type);
            sb.append("\t\t\t");
            sb.append(component_tag);
            sb.append(String.format("\t\t\t\t<name>%s</name>\n", component_name));
            sb.append(String.format("\t\t\t\t<path>%s</path>\n", component_path));
            sb.append("\t\t\t</component>\n");
        }
        sb.append("\t\t</service>\n");
        sb.append("\t</services>\n");
        sb.append("</grid>\n");
        try
        {
            String conffile = FilenameUtils.concat(Config.getConfPath(),"worker.conf");
            FileWriter  fw = new FileWriter(conffile);
            fw.write(sb.toString());
            fw.close();
        }
        catch(Exception e)
        {
            logger.error(e);
        }
        Config.reloadConfiguration();
    }
    private Entry parseUpdateInfo(String update_info)
    {
        UpdateConfParser parser = new UpdateConfParser();
        return parser.parse(update_info);
    }
    /**
     * if failed to download specififed file, it must the error of update information.
     * */
    private void download(DownloadableComponent dc)throws Exception
    {
        if(Config.isVerbose())
        {
            logger.info("[DOWNLOAD] " + dc.getSourceFile() + " from " + dc.getFtpInfo().getIp());
        }
        FTP ftp = dc.getFtpInfo();
        String host = ftp.getIp();
        int port = Integer.valueOf(ftp.getPort());
        String user = ftp.getUser();
        String password = ftp.getPassword();
        String datapath = ftp.getBasepath();
        int timeout = 3600;
        boolean pasv = false;
        if (ftp.getPasv().equalsIgnoreCase("1"))
            pasv = true;
        //.tar.gz or .exe ... with suffix.
        String sourcefile = dc.getSourceFile();
        Downloader loader = new Downloader(host,port,user,password,datapath,timeout,pasv);
        loader.connect();
        String[] files = {sourcefile};
        //sourcefile -> nr.tar.gz could be a multiple volumn database.
        String[] srcfiles = loader.getFilesToDownload(files);
        //srcfiles = nr.00.tar.gz,nr.01.tar.gz
        if(srcfiles.length==0)
        {
            throw new Exception(sourcefile+" not found");
        }
        String targetDir = Config.getUpdatePath();
        String[] downloadedfiles = loader.download(srcfiles,targetDir);
        for (String s : downloadedfiles)
        {
            String localdir = dc.getLocalPathToExtractData();
            String fpath = FilenameUtils.concat(targetDir,s);
            Extractor.extract(fpath,localdir,false);
        }
        loader.close();
    }
    public void run()
    {
        Entry update_entry = parseUpdateInfo(info);
        List<DownloadableComponent> dcl = getDownloableComponents(update_entry);
        try
        {
            for (DownloadableComponent d : dcl)
            {
                download(d);
            }
            //update local configuration file "worker.conf"
            writeConfFile(dcl);
            if(Config.isVerbose())
            {
                logger.info("Update finished." );
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
