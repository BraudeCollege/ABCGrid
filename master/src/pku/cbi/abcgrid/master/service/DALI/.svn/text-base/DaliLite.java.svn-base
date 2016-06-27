package pku.cbi.abcgrid.master.service.DALI;
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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class DaliLite extends AbstractApp
{
    private Config config = Config.getInstance();
    ExecutorService executor = Executors.newCachedThreadPool();
    Map<String, String> inpname = new HashMap<String, String>();

    public DaliLite()
    {
        super();
    }

    public String getServiceName()
    {
        return "DALI";
    }

    public String[] parseCommands(String user, String[] cmds)
    {
        /*
           DaliLite -pairwise <pdbfile1> <pdbfile2>"
           0            1           2        3      
        */
        String[] commands = cmds;
        String user_dir = config.getUserDir(user);
        int length = commands.length;
        for (int i = 0; i < length; i++)
        {
            if (i == 0) //blastall
            {
                commands[i] = "#" + commands[i];
            }
            else if (i == 1)// remote hmm database
            {
                if (commands[i].equalsIgnoreCase("-pairwise"))
                {
                    //the next two params(commands[2] and commands[3]) should be two pdb files.
                    try
                    {
                        String pdb1 = commands[2];
                        InputStream stream1 = new FileInputStream(new File(user_dir, pdb1));
                        addLocalInput(pdb1, IOUtils.toByteArray(stream1));
                        String pdb2 = commands[3];
                        InputStream stream2 = new FileInputStream(new File(user_dir, pdb2));
                        addLocalInput(pdb2, IOUtils.toByteArray(stream2));
                    }
                    catch (FileNotFoundException e)
                    {
                    }
                    catch (IOException e)
                    {
                    }
                }
                else if (commands[i].equalsIgnoreCase("-readbrk"))
                {
                    String pdb1 = commands[2];
                    try
                    {
                        InputStream stream = new FileInputStream(new File(user_dir, pdb1));
                        addLocalInput(pdb1, IOUtils.toByteArray(stream));
                    }
                    catch (FileNotFoundException e)
                    {
                    }
                    catch (IOException e)
                    {
                    }
                }
                else
                {//no local inputs.
                }
            }
            else if (i == 2)//first pdb file
            {
                String fname = commands[i];
                try
                {
                    InputStream stream = new FileInputStream(new File(user_dir, fname));
                    addLocalInput(fname, IOUtils.toByteArray(stream));
                }
                catch (FileNotFoundException e)
                {
                }
                catch (IOException e)
                {
                }
            }
            else if (i == 3)//chain identifier of first pdb file
            {
                //commands[i]="-";
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

}