package pku.cbi.abcgrid.worker.update;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */
import pku.cbi.abcgrid.worker.ftp.*;

import java.io.*;
import java.util.*;
import java.text.ParseException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pku.cbi.abcgrid.worker.conf.Config;
import pku.cbi.abcgrid.worker.Status;

public class Downloader
{
    private Log logger = LogFactory.getLog(Downloader.class);
    private boolean force_download = false;
    private FTPClient ftp = null;
    //SimpleFTPProgressMonitor monitor = new SimpleFTPProgressMonitor();
    //ThreadFactory factory = Executors.defaultThreadFactory();

    String  host;
    int     port;
    String  user;
    String  password;
    String  path;
    boolean pasv;
    int     timeout;

    public Downloader(String h,int p,String u,String pass,
                      String pth, int to, boolean psv)
    {
        host = h;
        port = p;
        user = u;
        password = pass;
        path = pth;
        timeout = to;
        pasv = psv;
    }

    private long modtime(String fname)
    {
        File f = new File(fname);
        return f.lastModified();
    }


    /**connect to ftp*/
    public void connect() throws FTPException, IOException
    {
        FTPClient ftpclient = new FTPClient(host, port);
        ftpclient.login(user, password);
        if (pasv)
            ftpclient.setConnectMode(FTPConnectMode.PASV);
        else
            ftpclient.setConnectMode(FTPConnectMode.ACTIVE);

        ftpclient.setType(FTPTransferType.BINARY);
        ftpclient.setTimeout(Integer.MAX_VALUE); //120 seconds
        //may failed to change directory.
        String[] ss = ftpclient.dir();
        ftpclient.chdir(path);
        ftp = ftpclient;
    }

    /**close the ftp connection*/
    public void close() throws FTPException, IOException
    {
        ftp.quit();
    }

    /**
     * A database could be composed of multiple volumes(files),
     * for example, the NR database of blast include nr.00.tar.gz
     * nr.01.tar.gz ...

     * @param fnames name of file
     *
     * @return Volumn list
     */
    public String[] getFilesToDownload(String[] fnames)
    {
        Set<String> ss = new HashSet<String>();
        String[] availiable = getAvailiable();
        for (String requested : fnames)
        {
            requested = Extractor.extractName(requested);
            //requested could be a multiple volume file.
            //for example. nr.tar.gz ---> nr.00.tar.gz and nr.00.tar.gz
            for (String avai : availiable)
            {
                String shortname = Extractor.extractName(avai);
                if (requested.equalsIgnoreCase(shortname))
                {
                    ss.add(avai);
                }
            }
        }
        String[] arr = new String[ss.size()];
        ss.toArray(arr);
        return arr;
    }

    public String[] getAvailiable()
    {
        Set<String> ss = new HashSet<String>();
        try
        {
            String[] fns = ftp.dir(".");
            for (String f : fns)
            {
                if (f.endsWith(".tar.gz") ||
                        f.endsWith(".exe") ||
                        f.endsWith(".gz")||
                        f.endsWith(".zip")||
                        f.endsWith(".tgz"))
                {
                    ss.add(f);
                }
            }
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        String[] arr = new String[ss.size()];
        ss.toArray(arr);
        return arr;
    }

    /**
     * download files from ftp.
     *
     * @param sourceFiles source files in ftp.
     * @param targetDir local directory to hold downloaded source files.
     * @return name of downloaded files.
     * */
    public String[] download(String[] sourceFiles,String targetDir)
            throws IOException,FTPException,ParseException
    {
        //factory.newThread(new DownloadThread(fnames,targetDir)).start();
        List<String> downloaded = new ArrayList<String>();
        //check the targetDir
        //String targetDir = Config.getUpdatePath();
        for (String f : sourceFiles)
        {
            //f is hello.##.tar.gz or hello.tar.gz
            //nr.tar.gz ---> nr
            //String dbname = extractName(f);
            String fpath = FilenameUtils.concat(targetDir, f);
            //if (Config.isVerbose() && isMultipleVolume(f))
            {
                //int vols = getNumVolumes(f,fnames);
                //System.err.println("Downloading "+dbname+":"+vols+" volumes");
            }
            if (force_download || ! new File(fpath).exists() ||
                    ftp.modtime(f).getTime() > modtime(fpath))
            {
                ftp.getFile(targetDir, f);
                if (Config.isVerbose())
                {
                    logger.info("[DOWNLOAD] " + f);
                }
                Status.set("download:"+f);
                downloaded.add(f);
            }
            else
            {
                if (Config.isVerbose())
                {
                    //logger.info("[DOWNLOAD] " + f + " is up-to-date");
                }
            }
        }
        String[] arr = new String[downloaded.size()];
        downloaded.toArray(arr);
        return arr;
    }

    /**For TEST*/
    public static void main(String[] args)
    {
        try
        {
            String host = "ftp.cbi.pku.edu.cn";
            int port = 21;
            String user = "anonymous";
            String password = "anonymous";
            //String path="/blast/db";
            String path = "/pub/databases/blast/";
            int timeout = 1200000;
            boolean pasv = true;

            Downloader myftp = new Downloader(host, port, user, password, path, timeout, pasv);
            //for(String avai:myftp.getAvailiable())
            //{
            //    System.out.println(avai);
            //}
            String[] files = {"pataa"};
            //String[] files = {"nr","swissprot"};
            //String[] files = {"blast-2.2.14-ia64-linux"};
            String[] myfiles = myftp.getFilesToDownload(files);
            String[] targzfiles = myftp.download(myfiles,".");
            for (String s : targzfiles)
            {
                //myftp.extract(s, "/rd2/user/suny/NCBI_BLAST/db/", false);
                Extractor.extract(s, "/rd2/user/suny/NCBI_BLAST/db/", false);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
