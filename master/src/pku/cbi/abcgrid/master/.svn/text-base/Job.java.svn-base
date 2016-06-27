package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import java.util.ArrayList;
import java.util.List;

/**
 * A job is generated from a user's "r" command.
 * for example, command:
 *">>>r blastall -p blastp -d swissport -i abc.fasta -o abc.blast -m 9"
 * will generate a blastall job.
 *   
 */
public class Job
{
    String      submitter; //
    String[]    command; //{"blastall","-p","blastp","-d","swissport","-i","abc.fasta","-o","abc.blast","-m","9"}
    int         priority; // depends on submitter
    long        identity; //auto-assigned , an increased long value.
    String      servicename; //NCBI_BLAST
    String      appname;//blastall
    List<TaskInput> inputs = new ArrayList<TaskInput>();

    public List<Task> fragment()
    {
        return null;
    }
    /**
     * Constructor
     *
     * @param s The user who submitted this job
     * @param sn App name of job, for example, "NCBI_BLAST"
     * @param an application name of job, e.g. "blastall"
     * @param cmd Submitted command e.g.
     * "blastall -p blastp -d swissprot -i demo.fasta -m 9 -o demo.blast"
     * @param prior Priority of job, little value means higher priority
     * */
    public Job(String s,String sn,String an,String[] cmd,int prior)
    {
        submitter = s;
        servicename = sn;
        appname = an;
        command = cmd;
        priority = prior;
    }
    /**
     * get the user who submitted this job.
     * @return Name of the user who submit this job.
     * */
    public String getSubmitter() {
        return submitter;
    }
    /**
     * get the uniqe identifier of this job.
     * @return identifier of this job
     * */
    public long getId()
    {
        return identity;
    }
    /**
     * set the uniqe identifier of this job.
     * @param i identifier of this job
     * */
    public void setId(long i)
    {
        identity = i;
    }
    /**
     *get name of service, such as "NCBI_BLAST"
     *
     * @return name of service
     * */
    public String getServiceName()
    {
        return servicename;
    }
    /**
     *get name of application, such as "blastall"
     *
     * @return name of application
     * */
    public String getAppName()
    {
        return appname;
    }
    /**
     *get a array of command, such as "NCBI_BLAST
     * blastall -p blastp -d swissport -i abc.fasta -o abc.blast -m 9"
     *
     * @return array of command parameters
     * */
    public String[] getCommand()
    {
        return command;
    }
    public int getPriority()
    {
        return priority;
    }
    public void addInputStream(String filename,byte[] data)
    {
        inputs.add(new TaskInput(filename,data));
        //inputs.put(filename,i);
    }
    public List<TaskInput> getInput()
    {
        return inputs;
    }
    public String toString()
    {
        //format:
        //| identity | user | command |
        return String.format("| %d | %s | %s |\n",
                identity,
                submitter,
                Util.join(command));
    }
}
