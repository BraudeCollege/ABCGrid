package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pku.cbi.abcgrid.master.conf.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Result(output) of one job.
 */
public class JobResult
{
    //private boolean bcompress;
    private String                          output_path;
    private Map<String,FileOutputStream>    output_stream;
    private Log logger;

    //keep the order of results
    BlockingQueue<TaskResult> queue =
            new PriorityBlockingQueue<TaskResult>(1024, new Comparator<TaskResult>()
            {
                public int compare(TaskResult A, TaskResult B)
                {
                    int ret;
                    long aid = A.getTaskId();
                    long bid = B.getTaskId();
                    if (aid < bid)
                    {
                        ret = -1;
                    }
                    else if (aid > bid)
                    {
                        ret = 1;
                    }
                    else
                    {
                        ret = 0;
                    }
                    return ret;
                }
            });

    public JobResult(String submitter)
    {
        output_path = Config.getInstance().getUserDir(submitter);
        output_stream = new HashMap<String,FileOutputStream>();
        logger = LogFactory.getLog(JobResult.class);
    }
    /**
     * 
     * */
    public void addResult(TaskResult t)
    {
        queue.add(t);
    }

    public int size()
    {
        return queue.size();
    }
    /**
     * finish this job. Write all data into file.
     *
     *
     * */
    public void finish()
    {
        TaskResult r;
        FileOutputStream fos;
        while ((r = queue.poll()) != null)
        {
            String fout = r.getFileName();
            if(fout==null)
            {
                break;
            }
            fos = output_stream.get(fout);
            //there could be more than one output files.
            if(fos==null)//the first call to write a file.
            {
                try
                {
                    fos = new FileOutputStream(new File(output_path,fout));
                    output_stream.put(fout,fos);
                }
                catch(FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
            try
            {
                //byte[] data = bcompress ? Util.uncompress(r.getFileData()) : r.getFileData();
                byte[] data = r.getFileData();
                if(data!=null)
                {
                    fos.write(data);
                }
                else
                {
                    logger.error("ERROR DATA");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                logger.error(fout);
            }
        }
        //close all files.
        for(FileOutputStream f:output_stream.values())
        {
            try
            {
                f.close();
            }
            catch(Exception e)
            {
            }
        }
        //System.out.println("OK");
    }//end of while.
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Output:");
        sb.append(output_path);
        sb.append("\n");
        sb.append("Number of Tasks:");
        sb.append(queue.size());
        sb.append("\n");
        return sb.toString();
    }
}
