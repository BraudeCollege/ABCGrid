package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */
import pku.cbi.abcgrid.master.Task;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * A callback interface called by ABCMaster.
 * 
 */
public interface Worker extends Remote
{

    /**
     * New tasks are availiable.
     * @param task A <code>Task</code> object
     * @return status code
     * @throws RemoteException specified by javaRMI  
     * */
    public int notify(Task task) throws RemoteException;
    /**
     * start the update process immediately.
     * @param info The content of Master's update.conf
     * @throws RemoteException specified by javaRMI
     * */
    public void update(String info)throws RemoteException;
    /**
     * report current status.
     * @return status message.
     * @throws RemoteException specified by javaRMI
     * */
    public String  status() throws RemoteException;
    /**
     * report name of this Worker.
     * @return name.
     * @throws RemoteException specified by javaRMI
     * */
    public String name()throws RemoteException;
    /**
     * cancel the execution of this task.
     * @param job_id job identity of this task
     * @param task_id  task identity of this task
     * @return message.
     * @throws RemoteException specified by javaRMI
     * */
    public String cancelTask(long job_id,long task_id)throws RemoteException;
}





