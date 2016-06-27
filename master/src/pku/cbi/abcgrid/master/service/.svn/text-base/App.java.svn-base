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

import java.util.List;

/**
 * The base interface for all bioinformatics applications.
 * A application is a executable unit of a App.
 * For example, "blastall" is a Application, "NCBI_BLAST" is a App.
 *
 *
 * To add support to a new application, you should derived your class
 * from this interface. 
 */
public interface App
{
    //get the service name. e.g. NCBI_BLAST, HMMER, CE.
    public String getServiceName();
    //get the application name. e.g. blastall, hmmpfam 
    public String getAppName();
    //command string ---> Job
    public Job parse(String user,String[] commands,int priority)throws Exception;
    //fragment a Job into Tasks.
    public List<Task> fragment(Job job)throws Exception;
}
