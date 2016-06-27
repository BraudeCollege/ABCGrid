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
import pku.cbi.abcgrid.worker.Status;
import pku.cbi.abcgrid.worker.CommandExecutor;
import pku.cbi.abcgrid.worker.tar.Tar;

import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * extract content from compressed files with formats:
 * 1) .exe (must be self-extracting)
 * 2) .tar.gz
 * 3) .gz
 * 4) .zip
 */
public class Extractor
{
    /**
     * After extracting, the old file should be truncated to zero length to save
     * disk space.
     */
    private static Log logger = LogFactory.getLog(Extractor.class);

    private static void truncate(File f) throws FileNotFoundException, IOException
    {
        long val = f.lastModified();
        RandomAccessFile raf = new RandomAccessFile(f, "rw");
        //set file length = 0
        raf.getChannel().truncate(0);
        raf.close();
        //change the last modify timestamp.(for comparing old or new)
        f.setLastModified(val);
    }

    private static boolean isMultipleVolume(String fname)
    {
        //hello.00.tar.gz -> true
        //hello.tar.gz ->false
        Pattern pMultipleVolume = Pattern.compile(".*\\d{2}\\.tar\\.gz$");
        Matcher m = pMultipleVolume.matcher(fname);
        return m.matches();
    }

    private static int getNumVolumes(String fname, String[] fnames)
    {
        int num = 0;
        for (String s : fnames)
        {
            //System.out.println(extractName(s));
            if (fname.equalsIgnoreCase(extractName(s)))
            {
                if (isMultipleVolume(s))
                    num += 1;
            }
        }
        return num;
    }

    private static File makeDir(String target)
            throws IOException
    {
        File ftarget = new File(target);
        if (! ftarget.exists())
        {
            ftarget.mkdirs();
        }
        else
        {
            if (!ftarget.isDirectory())
            {
                throw new IOException(ftarget + " must be a directory.");
            }
        }
        return ftarget;
    }

    /**
     * extract .exe file.
     */
    private static void extractExe(String fname, String targetDir, boolean removeSourceFile)
            throws IOException, InterruptedException
    {
        File ftarget = makeDir(targetDir);
        String updatedir = Config.getUpdatePath();
        //File fileupdate = new File(updatedir, fname);
        File fileupdate = new File(fname);
        FileUtils.copyFileToDirectory(fileupdate, ftarget);
        String appname = FilenameUtils.concat(ftarget.getPath(), fileupdate.getName());
        StringBuffer output = new StringBuffer();
        StringBuffer error = new StringBuffer();
        //extract a self-extracting .exe file by executing this file.
        new CommandExecutor().exec(appname, output, error, new File(targetDir));
        truncate(fileupdate);
        if (removeSourceFile)
            new File(appname).delete();
    }

    /**
     * extract .tar.gz file.
     */
    private static void extractTargz(String fname, String targetDir, boolean removeSourceFile)
            throws Exception
    {
        Tar app = new Tar();
        try
        {
            makeDir(targetDir);
            File fsrc = new File(fname);
            app.open(fsrc, "r:gz");
            //String dbname = extractName(fname);
            //String targetDirAndDB  = FilenameUtils.concat(targetDir,dbname);
            //app.extract(targetDirAndDB);
            app.extract(targetDir);
            //for saving the disk space, truncate file size to zero but
            //reserve the "date of last modified" as the tag.
            truncate(fsrc);
            if (removeSourceFile)
                fsrc.delete();
        }
        finally
        {
            app.close();
        }
    }

    /**
     * extract .zip file
     * A .zip file may have many files(entry).
     */
    public static void extractZip(String fname, String targetDir, boolean removeSourceFile)
            throws IOException
    {
        //String out = FilenameUtils.concat(Config.getUpdatePath(), fname);
        // Open the ZIP file
        File ftarget = makeDir(targetDir);
        File fsrc = new File(fname);
        ZipInputStream in = new ZipInputStream(new FileInputStream(fsrc));
        ZipEntry entry;
        while ((entry = in.getNextEntry()) != null)
        {
            File fout = new File(ftarget, entry.getName());
            OutputStream outstream = new FileOutputStream(fout);
            // Transfer bytes from the ZIP file to the output file
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                outstream.write(buf, 0, len);
            }
            outstream.close();
        }
        in.close();
        truncate(fsrc);
        if (removeSourceFile)
            fsrc.delete();
    }

    /**
     * extract .gz file
     * A .zip file has ONE file.
     */
    private static void extractGz(String fname, String targetDir, boolean removeSourceFile)
            throws IOException
    {
        File ftarget = makeDir(targetDir);
        File fsrc = new File(fname);
        GZIPInputStream in = new GZIPInputStream(new FileInputStream(fname));
        File fdest = new File(ftarget,fsrc.getName().replace(".gz",""));
        //System.out.println(fdest.getAbsolutePath());
        OutputStream outstream = new FileOutputStream(fdest);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
        {
            outstream.write(buf, 0, len);
        }
        in.close();
        outstream.close();
        truncate(fsrc);
        if (removeSourceFile)
            fsrc.delete();
    }

    /**
     * just copy , no decompress.
     * */
    public static void extractUnknown(String fname, String targetDir, boolean removeSourceFile)
            throws IOException
    {
        File ftarget = makeDir(targetDir);
        File fsrc = new File(fname);
        File fdest = new File(ftarget,fsrc.getName());
        //System.out.println(fdest.getAbsolutePath());
        InputStream instream = new FileInputStream(fsrc);
        OutputStream outstream = new FileOutputStream(fdest);
        byte[] buf = new byte[1024];
        int len;
        while ((len = instream.read(buf)) > 0)
        {
            outstream.write(buf, 0, len);
        }
        instream.close();
        outstream.close();
        truncate(fsrc);
        if (removeSourceFile)
            fsrc.delete();
    }

    /**
     * chop postfix .##.tar.gz or .tar.gz
     * Example:
     * extractName(hello.00.tar.gz) -> hello.tar.gz
     */
    public static void extract(String fname, String targetDir, boolean removeSourceFile)
            throws IOException, InterruptedException, Exception
    {
        if (Config.isVerbose())
        {
            logger.info("[EXTRACT] " + fname + " to " + targetDir + " ... ");
        }
        Status.set("unzip:"+fname);
        if (fname.endsWith(".exe"))
            extractExe(fname, targetDir, removeSourceFile);
        else if (fname.endsWith(".tar.gz") || fname.endsWith(".tgz"))
            extractTargz(fname, targetDir, removeSourceFile);
        else if (fname.endsWith(".gz"))
            extractGz(fname, targetDir, removeSourceFile);
        else if (fname.endsWith(".zip"))
            extractZip(fname, targetDir, removeSourceFile);
        else
            extractUnknown(fname, targetDir, removeSourceFile);

    }

    public static String extractName(String fname)
    {
        //System.out.println(fname);
        if (fname.endsWith(".tar.gz"))
        {
            if (isMultipleVolume(fname))
                return fname.replaceAll("\\.\\d{2}\\.tar\\.gz", "");
            else
                return fname.replaceAll("\\.tar\\.gz", "");
        }
        else if (fname.endsWith(".exe"))
        {
            return fname.replaceAll("\\.exe", "");
        }
        else if (fname.endsWith(".gz"))
        {
            return fname.replaceAll("\\.gz", "");
        }
        else
            return fname;
    }
}
