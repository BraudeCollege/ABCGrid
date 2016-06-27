package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.worker.Worker;

import java.util.List;

/**
 * Information of a worker node.
 */
public class WorkerInfo
{
    //int CPU_NUM = 4; //
    //int CPU_MHZ = 1024; //MB
    //int CPU_CACHE = 2028; //KB
    //String CPU_VENDOR = "Intel";
    //String MODEL_NAME = "Opteron848";
    //int MEM_FREE = 1024;//MB
    //int DISK_FREE = 1024;//MB
    String OS = "N/A";
    String ARCH = "N/A";
    String IP = "0.0.0.0";
    String status = "N/A";
    List<String> services;
    Worker callback;
    /**
     * <worker>
     *  <CPU>
     *      <number>4</number>
     *      <speed>2400</speed>
     *      <cache>512</cache>
     *      <vendor>intel</vendor>
     *      <model>pentium 4</model>
     *  <CPU>
     *  <MEMORY>
     *      <total>4</total>
     *      <free>4</free>
     *  </MEMORY>
     *  <OS>Linux</OS>
     *  <ARCH>Linux</ARCH>
     *  <IP>Linux</IP>
     * </worker>
     *
     * */
    public WorkerInfo(String workerinfo,List<String> srv, Worker w)
    {
        //parse xmlinfo into internal vars.
        //String sz = "OS#ARCH#IP";
        String[] info = workerinfo.split("#");
        IP = info[0];
        OS = info[1];
        ARCH = info[2];
        services = srv;
        callback = w;
    }
    public Worker getWorker()
    {
        return callback;
    }
    public List<String> getServices()
    {
        return services;
    }
    public void setServices(List<String> val)
    {
        services = val;
    }
    public String getIP()
    {
        return IP;
    }
    public void setStatus(String val)
    {
        status = val;
    }
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(IP);
        sb.append(" # ");
        sb.append(OS);
        sb.append(" # ");
        sb.append(ARCH);
        sb.append(" # ");
        sb.append(status);
        return sb.toString();
    }
}
