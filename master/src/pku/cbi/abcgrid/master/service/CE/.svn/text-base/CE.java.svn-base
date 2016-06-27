package pku.cbi.abcgrid.master.service.CE;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import org.apache.commons.io.IOUtils;
import pku.cbi.abcgrid.master.Job;
import pku.cbi.abcgrid.master.Task;
import pku.cbi.abcgrid.master.TaskInput;
import pku.cbi.abcgrid.master.WorkerManager;
import pku.cbi.abcgrid.master.conf.Config;
import pku.cbi.abcgrid.master.fragment.Fragmentable;
import pku.cbi.abcgrid.master.fragment.PDB;
import pku.cbi.abcgrid.master.service.AbstractApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class CE extends AbstractApp
{
    private Config config = Config.getInstance();
    ExecutorService executor = Executors.newCachedThreadPool();
    Map<String, String> inpname = new HashMap<String, String>();

    public CE()
    {
        super();
    }

    /**
     * get the service name supported by this JobReader
     */
    public String getServiceName()
    {
        return "CE";
    }

    public String[] parseCommands(String user, String[] cmds)
            throws Exception
    {
        /*
           CE - pdb1acj.ent -  pdb2acj.ent - *scratch
           "scratch"-> "/fdfsdf/fsdfsf/scratch"
           0  1    2        3      4       5
        */
        String[] commands = cmds;
        String user_dir = config.getUserDir(user);
        int length = commands.length;
        if(length<7)
        {
            throw new Exception(usage());
        }
        for (int i = 0; i < length; i++)
        {
            if (i == 0) //blastall
            {
                commands[i] = "#" + commands[i];
            }
            else if (i == 1)//
            {
                //commands[i]="-";
            }
            else if (i == 2)//first pdb file
            {
                String fname = commands[i];
                InputStream stream = new FileInputStream(new File(user_dir, fname));
                addLocalInput(fname, IOUtils.toByteArray(stream));
            }
            else if (i == 3)//chain identifier of first pdb file
            {
                //commands[i]="-";
            }
            else if (i == 4)//second pdb file
            {
                String fname = commands[i];
                InputStream stream = new FileInputStream(new File(user_dir, fname));
                addLocalInput(fname, IOUtils.toByteArray(stream));
            }
            else if (i == 5)//chain identifier of second pdb file
            {
                //commands[i] = "-";
            }
            else if (i == 6)//"scratch" sub-directory
            {
                commands[i] = "*" + commands[i];
            }
            else
            {

            }
        }
        return commands;
    }

    public List<Task> fragment(Job job) throws Exception
    {
        AtomicLong task_id = new AtomicLong(1L);
        String user_dir = config.getUserDir(job.getSubmitter());
        List<Task> tasks = new ArrayList<Task>();
        //String[] taskcmd = removePathOfInputFile(job);
        Task t = new Task(
                job.getId(),
                task_id.getAndIncrement(),
                job.getServiceName(),
                job.getCommand(),
                job.getPriority(),
                0);
        Fragmentable frag = new PDB();
        for (TaskInput input : job.getInput())
        {
            File f = new File(user_dir, input.getName());
            int num_workers = WorkerManager.getInstance().getWorkerNum();
            num_workers = num_workers > 0 ? num_workers : 10;
            Object[] data = frag.fragment(f, num_workers);
            t.addInput(new TaskInput(input.getName(), data[0].toString().getBytes()));
        }
        tasks.add(t);
        return tasks;
    }
    public String usage()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\nUSAGE:\n");
        buffer.append("CE - [pdb_file1] [chain_id1] [pdb_file2] [chain_id2] scratch\n\n");
        buffer.append("Example:\n");
        buffer.append("CE - pdb1acj.ent - pdb2acj.ent - scratch\n");
        return buffer.toString();
    }
    
}
