package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.master.user.UserManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A Request is coming from ABCUser.
 A valid request should have following format:

 username:password
 command
 //

 */
public class Request extends Thread
{
    private Socket client;
    private UserManager umg = UserManager.getInstance();

    public Request(Socket c)
    {
        client = c;
        start();
    }

    private String authenticate(String line) throws Exception
    {
        if (line == null)
            throw new Exception("Invalid command format");
        //"username:password"
        String[] tokens = line.split(":");
        if (tokens.length < 2)
        {
            //wrong format
            throw new Exception("Invalid command format");
        }
        else
        {
            String username = tokens[0];
            String password = tokens[1];
            //String callbackport = tokens[2];
            String address = client.getInetAddress().getHostAddress();
            if (!umg.isValidUser(username, password, address))
            {
                throw new Exception("Failed to pass authentication");
            }
            else
            {
                return username;
            }
            //else
            //{
            //System.out.println("OK");
            //umg.registerUser(username,"address"+":"+callbackport);
            //}
        }
    }

    private void writeMesage(String msg, boolean close)
    {
        try
        {
            PrintWriter out = new PrintWriter(client.getOutputStream());
            out.println(msg);
            out.flush();
            if (close)
                client.close();
        }
        catch(Exception e)
        {
            
        }
    }

    public void run()
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String auth = in.readLine();
            String user = authenticate(auth);
            if (user == null)
            {
                writeMesage("Failed to pass authentication", true);
                return;
            }
            String cmd = in.readLine();
            String end = in.readLine();
            if (end == null || !end.equalsIgnoreCase("//"))
            {
                writeMesage("Invalid command format", true);
                return;
                //invalid format of command
            }
            String msg = CommandHandler.getInstance().handle(user, cmd);
            writeMesage(msg, true);
        }
        catch (Exception e)
        {
            writeMesage(e.getMessage(),true);
        }
    }

}
