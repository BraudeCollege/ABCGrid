package pku.cbi.abcgrid.master.fragment;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BaseFragment implements Fragmentable
{
    protected boolean include_delimiter_at_end = false;
    protected int MAX_TASK_SIZE_BYTES;
    protected int MIN_TASK_SIZE_BYTES;
    protected int MIN_WORKERS_NUMBER;
    public BaseFragment()
    {
        MAX_TASK_SIZE_BYTES = 1024*512;//512KB
        MIN_TASK_SIZE_BYTES = 1024*1;//1KB
        MIN_WORKERS_NUMBER  = 10;//10 workers(split one job into 10 or more tasks)
    }
    public String   getSeperator()
    {
        return ">";
    }
    public Object[] fragment(File finput, int number)
            throws Exception
    {
        return _fragment(new BufferedReader(new FileReader(finput)),number);
    }
    public Object[] fragment(InputStream sinput,int number)
            throws Exception
    {
        return _fragment(new BufferedReader(new InputStreamReader(sinput)),number);
    }

    private Object[] _fragment(BufferedReader rd,int number)
            throws Exception
    {
        Queue<String> objs = new LinkedList<String>();
        String seperator = getSeperator();
        StringBuffer buffer = new StringBuffer();
        try
        {
            String line;
            while ((line = rd.readLine()) != null)
            {
                if (seperator!=null && line.startsWith(seperator))
                {
                    //a new sequence
                    if (buffer.length() > 0)
                    {
                        if (include_delimiter_at_end)
                        {
                            buffer.append(line);
                            buffer.append("\n");
                        }
                        objs.add(buffer.toString());
                        //clear the old string buffer(by create a now StringBuffer).
                        buffer = new StringBuffer();
                    }
                }
                buffer.append(line);
                buffer.append("\n");
            }
            if (buffer.length() > 0)
                objs.add(buffer.toString());
            return fill(objs, number);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Object[0];
    }
    /**
     *
     * dispatch onjects in Queue to N workers
     *
     * */
    protected Object[] fill(Queue objs, int worker_number)
    {
        int w = worker_number*2;
        if (w<MIN_WORKERS_NUMBER)
        {
            w = MIN_WORKERS_NUMBER;
        }
        int total = objs.size();
        int quota = total/w;//each worker should has quota objects.
        while(quota<1)
        {
            w -= 1;
            quota = total/w;
        }
        int left = total%w;
        List<String> ret = new ArrayList<String>();
        int number = 0;
        StringBuffer buffer = new StringBuffer();
        String entry;
        while((entry=(String)objs.poll())!=null)
        {
            if((buffer.length()+entry.length())>MAX_TASK_SIZE_BYTES)
            {
                if(buffer.length()==0)
                {
                    //anyway, give at least one entry.
                    ret.add(entry);
                }
                else
                {
                    ret.add(buffer.toString());
                    buffer.delete(0,buffer.length());
                }
                number = 0;
            }
            else
            {
                buffer.append(entry);
                number += 1;
                if(number >= quota)
                {
                    if(left>0)
                    {
                        buffer.append((String)objs.poll());
                        number += 1;
                        left -= 1;
                    }
                    ret.add(buffer.toString());
                    buffer.delete(0,buffer.length());
                    number = 0;
                }
            }
        }
        Object[] arr = new Object[ret.size()];
        ret.toArray(arr);
        return arr;
    }

    public static void main(String[] args)
    {
        try
        {
            BaseFragment bf = new Genbank();
            Object[] objs = bf.fragment(new File("f:\\download\\demo.genbank"),10);
            System.out.println(objs.length);
            
        }
        catch(Exception e)
        {

        }

    }
}