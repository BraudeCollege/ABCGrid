package pku.cbi.abcgrid.master.conf;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

public class Port
{
    private String worker_port="4170";
    private String user_port="4171";
    public void setPort(String wport,String uport)
    {
        worker_port = wport;
        user_port = uport;
    }
    public int getWorkerPort()
    {
        return Integer.valueOf(worker_port);
    }
    public int getUserPort()
    {
        return Integer.valueOf(user_port);
    }

}
