package pku.cbi.abcgrid.worker;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.worker.conf.Config;
import java.io.*;
/**
 *
 * Get the system info
 *
  */
public class SysInfo
{
    private native int getProcessId();
    private native int getNumberOfCPU();
    private native long getProcessTime();
    private native double getProcessUsage();

    private static int num_of_cpu = 0;
    private static int mhz_of_cpu = 0;
    private static int cachesize_of_cpu = 0;
    private static String model_of_cpu = null;

    private static final  SysInfo inst = new SysInfo();
    private SysInfo()
    {
        init();
    }
    /**
     * get the number of CPU in this machine.
     *
     * @return Number of CPU
     * */
    public static int getNumOfCPU()
    {
        return num_of_cpu>0?num_of_cpu:1;
    }
    private void init()
    {
        String operatingsystem = Config.getOS();
        //System.out.println(operatingsystem);
        if(operatingsystem.startsWith("Linux"))
        {
            try
            {
                String cpuinfo="/proc/cpuinfo";
                BufferedReader br = new BufferedReader(new FileReader(cpuinfo));
                String sz = null;
                while((sz=br.readLine())!=null)
                {
                    if(sz.startsWith("processor"))
                        num_of_cpu ++;
                    if(sz.startsWith("cpu MHz")){}
                        //mhz_of_cpu = Integer.valueOf(sz);
                    if(sz.startsWith("cache size")){}
                        //cachesize_of_cpu= Integer.valueOf(sz);
                    if(sz.startsWith("model name"))
                        model_of_cpu = sz;
                }
            }
            catch(Exception e)
            {
                num_of_cpu = 1;
                // e.printStackTrace();
            }
        }
        if(operatingsystem.startsWith("Mac"))
        {
            ProcessBuilder pb = new ProcessBuilder("sysctl","-a","hw");
            try
            {
                Process p = pb.start();
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while((line=br.readLine())!=null)
                {
                    if(line.startsWith("hw.ncpu"))
                    {
                        String[] ss = line.split(":");
                        num_of_cpu =  Integer.valueOf(ss[1].trim());
                        break;
                    }
                }
            }
            catch(Exception e)
            {
                num_of_cpu = 1;
            }
        }
        if(operatingsystem.startsWith("Windows"))
        {
            num_of_cpu = 1;
            try
            {
                //System.loadLibrary("lib/sysinfo");
                //getNumberOfCPU();
            }
            catch(Exception e)
            {
                num_of_cpu = 1;
                e.printStackTrace();
            }
        }
    }
}
