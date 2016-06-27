package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Some utility methods
 */
public class Util
{


    /**
     * join strings with " "
     * "AAAA","BBB","CCC" -> "AAA BBB CCC"
     * */
    public static String join(String... s)
    {
        StringBuffer buffer = new StringBuffer();
        int i;
        for(i=0;i<s.length-1;i++)
        {
            buffer.append(s[i]);
            buffer.append(" ");
        }
        buffer.append(s[i]);
        return buffer.toString();
    }
    /*
    public static String join(String[]... s)
    {
        String sperator = " ";
        StringBuffer buffer = new StringBuffer();
        int i;
        for(i=0;i<s.length-1;i++)
        {
            buffer.append(join(sperator,s[i]));
            buffer.append(sperator);
        }
        buffer.append(join(sperator,s[i]));
        return buffer.toString();
    }
    */
    /**
     * join some strings into one string.
     * */
    public static String join(Collection s)
    {
        String sperator = " ";
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext())
        {
            buffer.append(iter.next());
            if (iter.hasNext())
            {
                buffer.append(sperator);
            }
        }
        return buffer.toString();
    }

    /**
     * uncompress data
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
            e.printStackTrace();
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
     * compress data
     * */
    public static byte[] compress(byte[] incoming)
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DeflaterOutputStream dos = new DeflaterOutputStream(buffer);
        try
        {
            dos.write(incoming);
            dos.close();
        }
        catch(IOException e)
        {
            //e.printStackTrace();
        }
        return buffer.toByteArray();
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
    public static String[] split2(String cmd)
    {
        String[] commands = cmd.split("\\s");
        List<String> cmds = new ArrayList<String>();
        for(String c:commands)
        {
            String nblank = c.trim();
            if(nblank.length()>0)
                cmds.add(nblank);
        }
        String[] ret = new String[cmds.size()];
        cmds.toArray(ret);
        return ret;
    }
    /*
    public static String[] split(String command)throws Exception
    {
        //"2 > output 1 > error" ---> ["2 > output","1 > error"]
        //do not split redirect ">" : "2 > abc" ---> "2>abc"
        String cmd = command;
        List<String> cmdlist = new ArrayList<String>();
        List<String> redirect = new ArrayList<String>();
        Matcher m = pattern_redirect.matcher(command);
        while(m.find())
        {
            redirect.add(m.group(0));
        }
        cmd = command.replaceAll("(\\d?>\\s*.*?\\s|\\d?>\\s*.*?$)","");
        String[] cmds = cmd.split("\\s");
        for(String c:cmds)
        {
            String nblank = c.trim();
            if(nblank.length()>0)
                cmdlist.add(nblank);
        }
        cmdlist.addAll(redirect);
        String[] ret = new String[cmdlist.size()];
        cmdlist.toArray(ret);
        //System.out.println(cmdlist);
        return ret;
    }
    
    private static Pattern pattern_redirect = Pattern.compile("(\\d?>\\s*.*?\\s|\\d?>\\s*.*?$)");
    public static List<String[]> split_redirect(String command)throws Exception
    {
        //"hmmpfam -h  hmm_database query_file  2 > output 1 > error" --->
        //["hmmpfam","-h","hmm_database","query_file","2 > output","1 > error"]
        //do not split redirect ">" : "2 > abc" ---> "2>abc"
        List<String[]> ret = new ArrayList<String[]>();
        List<String> cmdlist = new ArrayList<String>();
        List<String> redirect = new ArrayList<String>();
        Matcher m = pattern_redirect.matcher(command);
        while(m.find())
        {
            redirect.add(m.group(0).trim());
        }
        String cmd = command.replaceAll("(\\d?>\\s*.*?\\s|\\d?>\\s*.*?$)","");
        String[] cmds = cmd.split("\\s");
        for(String c:cmds)
        {
            String nblank = c.trim();
            if(nblank.length()>0)
                cmdlist.add(nblank);
        }
        String[] a = new String[cmdlist.size()];
        cmdlist.toArray(a);
        ret.add(a);
        String[] b = new String[redirect.size()];
        redirect.toArray(b);
        ret.add(b);
        return ret;
    }
    */
    private static char[] ILLEGAL_FILENAME_CHARS = {'/', '\\', ':', '*', '?', '"', '<', '>', '|', ',', '=', ';', '&' };
    public static boolean isLegalFilename(String filename)
    {
        for(int i = 0; i < ILLEGAL_FILENAME_CHARS.length; i++)
        {
            if(filename.indexOf(ILLEGAL_FILENAME_CHARS[i]) >= 0)
            {
                return false;
            }
        }
        return true;
    }
    /**
     * Deal with redirect tag.
     *
     * merge:    "2> abc.txt" ---> "2>abc.txt"
     * override: "2>stderr 1>stdout >stdout_stderr" ---> ">","stdout_stderr"
     *
     * */
    public static String[] normalize_command(String command)
            throws Exception
    {
        //try to split command by " "
        Vector<String> cmds = new Vector<String>();
        String seperator_regex = " ";
        String redirect_output=null;
        String redirect_error=null;
        String redirect_output_and_error=null;
        String[] ps = command.split(seperator_regex);
        for(int i=0;i<ps.length;i++)
        {
            if(ps[i]==null)
                continue;
            String p = ps[i].trim();
            if(p.startsWith("1>"))
            {
                if(p.length()>2)//"1>abc"
                {
                    String fname = p.substring(2); 
                    if(!isLegalFilename(fname))
                        throw new Exception("Illegal filename: "+fname);
                    redirect_output = p;
                }
                else//"1>  abc"
                {
                    try
                    {
                        String fname = ps[i+1].trim();
                        if(!isLegalFilename(fname))
                            throw new Exception("Illegal filename: "+fname);
                        redirect_output = "1>"+fname;
                        ps[i+1] = null;
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new Exception("No filename after 1>");
                    }
                }
            }
            else if(p.startsWith("2>"))
            {
                if(p.length()>2)//"2>abc"
                {
                    String fname = p.substring(2);
                    if(!isLegalFilename(fname))
                        throw new Exception("Illegal filename: "+fname);
                    redirect_error = p;
                }
                else//"2>  abc" ---> "2>abc", merge the two tokens
                {
                    try
                    {
                        String fname = ps[i+1].trim();
                        if(!isLegalFilename(fname))
                            throw new Exception("Illegal filename: "+fname);
                        redirect_error = "2>"+fname;
                        ps[i+1] = null;
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new Exception("No filename after 2>");
                    }
                }
            }
            else if(p.startsWith(">"))
            {
                if(p.length()>1)//">abc"
                {
                    String fname = p.substring(1);
                    if(!isLegalFilename(fname))
                        throw new Exception("Illegal filename: "+fname);
                    redirect_output_and_error = p;
                }
                else//">  abc"
                {
                    try
                    {
                        String fname = ps[i+1].trim();
                        if(!isLegalFilename(fname))
                            throw new Exception("Illegal filename: "+fname);
                        redirect_output_and_error = ">"+fname;
                        ps[i+1] = null;
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new Exception("No filename after >");
                    }
                }
            }
            else
            {
                cmds.add(p);
            }

        }
        if(redirect_output_and_error!=null)
        {
            cmds.add(redirect_output_and_error);
        }
        else
        {
            if(redirect_output!=null)
                cmds.add(redirect_output);
            if(redirect_error!=null)
                cmds.add(redirect_error);
        }
        String[] ret = new String[cmds.size()];
        cmds.toArray(ret);
        return ret;
    }
}
