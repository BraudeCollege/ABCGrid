package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.master.conf.Config;

import java.io.IOException;
import java.net.ServerSocket;

public class RequestServer extends Thread
{
    private Config config = Config.getInstance();
    public void run()
    {
        ServerSocket echoServer = null;
        try
        {
            echoServer = new ServerSocket(config.getUserPort());
        }
        catch (IOException e)
        {
            System.out.println(e);
            System.exit(0);
        }
        try
        {
            while (true)
            {
                try
                {
                    new Request(echoServer.accept());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

}
