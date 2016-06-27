package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.util.*;
import java.util.zip.InflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.*;

import pku.cbi.abcgrid.worker.conf.Config;

/**
 *
 * Some utility methods
 *
 */
public class Util
{
    public static String join(Object[] s, String delimiter)
    {
        StringBuffer buffer = new StringBuffer();
        int i;
        for(i=0;i<s.length-1;i++)
        {
            buffer.append(s[i].toString());
            buffer.append(delimiter);
        }
        buffer.append(s[i].toString());
        return buffer.toString();
    }
    /**
     *join some strings into one string.
     * */
    public static String join(Collection s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    /**
     * uncompress. byte[] -> byte[]
     * @param incoming Compressed data
     * @return Raw data
     * */
    public static byte[] uncompress(byte[] incoming)
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        InputStream is = new ByteArrayInputStream(incoming);
        InflaterInputStream iis = new InflaterInputStream(is);
        byte[] outcoming = new byte[1024*1024];


        try
        {
            int rd;
            while((rd=iis.read(outcoming))!=-1)
            {
                buffer.write(outcoming,0,rd);
            }
            buffer.close();
            iis.close();
        }
        catch(IOException e)
        {
            //e.printStackTrace();
        }
        return buffer.toByteArray();
    }
    public static InputStream uncompress(InputStream incoming)
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        InflaterInputStream iis = new InflaterInputStream(incoming);
        byte[] outcoming = new byte[1024*1024];
        try
        {
            int rd;
            while((rd=iis.read(outcoming))!=-1)
            {
                buffer.write(outcoming,0,rd);
            }
            buffer.close();
            iis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        //return buffer.toByteArray();
        return new ByteArrayInputStream(buffer.toByteArray());
    }
    
    /**
     * compress. byte[] -> byte[]
     * @param incoming Raw data
     * @return Compressed data 
     * */
    public static byte[] compress(byte[] incoming)
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DeflaterOutputStream dos = new DeflaterOutputStream(os);
        try
        {
            dos.write(incoming);
            dos.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return os.toByteArray();
    }
    public static InputStream compress(InputStream incoming)
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DeflaterOutputStream dos = new DeflaterOutputStream(buffer);
        try
        {
            dos.write(IOUtils.toByteArray(incoming));
            dos.close();
        }
        catch(IOException e)
        {
            //e.printStackTrace();
        }
        return new ByteArrayInputStream(buffer.toByteArray());
    }

    //abcd.dat.exe -> abcd.dat
    public static String chopPostfix(String executable)
    {
        String sz = executable.trim();
        int index = sz.lastIndexOf(".");
        return index>0?sz.substring(0,index):sz;
    }
    // chop the leading directory seperator.
    // "/abcd/dat/hello" -> "abcd/dat/hello"
    public static String chopLeadingDS(String path)
    {
        String p = path.trim();
        return p.startsWith("/")?p.substring(1):p;
    }
    // chop the leading directory seperator.
    // "/abcd/dat/hello" -> "abcd/dat/hello"
    public static String appendEndSeparator(String path)
    {
        return FilenameUtils.getPathNoEndSeparator(path)+"/";
    }

    private static Map<String,File> exec_path = new HashMap<String, File>();
    
    private static void putName(File f,String executable)
    {
        exec_path.put(executable,f);
    }
    public static File findFile(File root, String fileName)
    {
        if(!exec_path.containsKey(fileName))
        {
            visitAllFiles(root,fileName);
        }
        return exec_path.get(fileName);
    }
    private static boolean matchName(File f,String executable)
    {
        String basename = FilenameUtils.getBaseName(f.getName());
        return basename.equalsIgnoreCase(executable);
    }
    // Process only files under dir
    private static void visitAllFiles(File file,String executable)
    {
        //System.out.println(dir.getAbsolutePath());
        if (file.isDirectory())
        {
            String[] children = file.list();
            for (String child:children)
            {
                visitAllFiles(new File(file, child),executable);
            }
        }
        else
        {
            if(matchName(file,executable))
            {
                putName(file,executable);
            }
        }
    }
    public static void deleteNewFiles(File root,long threadhold)
    {
        String[] files = root.list( new AgeFileFilter(threadhold,false));
        for(String s:files)
        {
            try
            {
                //System.out.println(s);
                FileUtils.forceDelete(new File(s));
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public static void getNewFiles(File root,long threadhold)
    {
        String[] files = root.list( new AgeFileFilter(threadhold,false));
        for(String s:files)
        {
            System.out.println(s);
        }
    }
}
