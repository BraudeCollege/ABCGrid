package pku.cbi.abcgrid.master.service;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.master.Job;
import pku.cbi.abcgrid.master.Task;
import pku.cbi.abcgrid.master.TaskInput;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractApp implements App
{
    protected List<TaskInput> inputs;
    public AbstractApp()
    {
        inputs = new ArrayList<TaskInput>();
    }
    public String getAppName()
    {
        //use class name as the application's name.
         //pku.cbi.abcgrid.master.service.NCBI_BLAST.NCBI_BLASTBase -> NCBI_BLASTBase
         String className = this.getClass().getName();
         String[] names = className.split("\\.");
         return names[names.length-1];
    }
    public abstract String getServiceName();
    public abstract List<Task> fragment(Job job)throws Exception;
    public abstract String[] parseCommands(String user,String[] cmds)
        throws Exception;
    /**
     * Parse a command string into a Job object.
     * 1) redirection token such as ">", "2>" and "1>"
     * 2) input file(s) or database in local machine. 
     * 3) input file(s) or database in Worker node.
     *
     * Insert a "#" to the head of EXECUTABLE, this EXECUTABLE will be mapped
     * to worker's local path when running in Worker machine.
     *
     * Insert a "@" to the head of DATABASE, this DATABASE will be mapped
     * to worker's local path when running in Worker machine.

     * For example,
     * command "blastall -p blastp -i abc.fasta -d pataa -o abc.blast"
     * will be parsed to "#blastall -p blastp -i abc.fasta -d @pataa -o abc.blast".
     *
     * @param user The user who submit this command
     * @param commands The command to be parsed (add tags).
     * @param priority The priority of this command(This value should be deduced from user.)
     * @return A Job object if jobcmd is successfully parsed. Otherwise return null.
     */
    public Job parse(String user,String[] commands,int priority)
            throws Exception
    {
        inputs.clear();
        String[] cmds = new String[commands.length];
        System.arraycopy(commands,0,cmds,0,commands.length);
        String[] tagged_commands = parseCommands(user,cmds);
        Job job = new Job(user,getServiceName(),getAppName(),tagged_commands, priority);
        for(TaskInput is:inputs)
        {
            job.addInputStream(is.getName(),is.getData());
        }
        return job;
    }
    protected void addLocalInput(String n,byte[] d)
    {
        inputs.add(new TaskInput(n,d));
    }

}
