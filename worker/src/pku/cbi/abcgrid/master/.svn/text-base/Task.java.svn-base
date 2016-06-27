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
 * Task was serialized and transfered between ABCWorker and ABCMaster.
 * It is the subunit a Job. A job is fragmentated to one or more tasks,
 * each task hold a piece of input data of parental job's input data.
 */

public class Task implements java.io.Serializable
{
    private long job_id;
    private long task_id;
    private String[] command;
    private List<TaskInput> input;
    private String service_name;
    private int priority;
    private int timeout;

    public Task(long gi, long pi, String sn, String[] cmd, int pro, int to)
    {
        job_id = gi;
        task_id = pi;
        service_name = sn;
        command = new String[cmd.length];
        System.arraycopy(cmd, 0, command, 0, cmd.length);
        priority = pro;
        timeout = to;
        input = new ArrayList<TaskInput>();
    }

    /**
     * add a Input
     *
     * @param fname File name
     * @param data  A job may be fragmented into many tasks  share the same input file name.
     *              to discriminate, every file name will inserted the task's id into the header
     *              of the file name. example:
     *              <p/>
     *              |---> "1_abc.txt"(for task 1)
     *              "abc.txt"----|---> "2_abc.txt"(for task 2)
     *              |---> ...
     *              |---> "N_abc.txt"(for task N)
     */
    public void addInput(String fname, byte[] data)
    {
        input.add(new TaskInput(fname, data));
    }

    public void addInput(TaskInput ti) {
        input.add(ti);
    }

    /**
     * @return The Input data. "-i" "example.fasta" "content of example.fasta"
     */
    public List<TaskInput> getInput() {
        return input;
    }

    /**
     * @return Name of service. e.g. "NCBI_BLAST"
     */
    public String getServiceName() {
        return service_name;
    }

    /**
     * @return Command strings.
     *         e.g. {"blastall","-p","blastp","-d","pdb","-i","abc.fasta","-o","abc.blast"}
     */
    public String[] getCommand() {
        return command;
    }

    /**
     * @return A unique identity of the task.
     */
    public long getTaskId() {
        return task_id;
    }

    /**
     * @return A unique identity of parental job.
     */
    public long getJobId() {
        return job_id;
    }

    /**
     * @deprecated
     * @return time to wait
     */
    public int getWaitTime() {
        return timeout;
    }

    public void setPriority(int val) {
        priority = val;
    }

    public int getPriority() {
        return priority;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("JobId:");
        sb.append(job_id);
        sb.append(",");
        sb.append("TaskId:");
        sb.append(task_id);
        sb.append(", command:");
        for (String s : command) {
            sb.append(" ");
            sb.append(s);
        }
        sb.append("\n");
        return sb.toString();
    }
}
