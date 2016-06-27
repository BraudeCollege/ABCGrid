package pku.cbi.abcgrid.user;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import org.apache.commons.io.FilenameUtils;
import java.net.InetAddress;
import java.io.*;

public class Config
{
    private int port = 4171;
    private InetAddress master = null;
    private String  uname;
    private String  password;
    private String HOME;
    public Config()
    {
        String curr = System.getProperty("user.dir");
        HOME = new File(curr).getParent();
        //HOME = ".";
    }
    public void init()throws IOException
    {
        String token_user = "user="; 
        String token_password = "password=";
        String token_master = "master=";
        String token_port = "port="; 
        String dconf = FilenameUtils.concat(HOME,"conf");
        String fconf = FilenameUtils.concat(dconf,"user.conf");
        BufferedReader fr = new BufferedReader(new FileReader(fconf));
        String line;
        while ((line = fr.readLine()) != null)
        {
            if(line.startsWith("#"))
            {
                continue;
            }
            if(line.startsWith(token_user))
            {
                //uname must NOT be empty
                uname = line.substring(token_user.length()).trim();
                if(uname.length()<1)
                {
                    throw new IOException("user.conf: user must NOT be empty");
                }
            }
            else if(line.startsWith(token_password))
            {
                //passwd could be empty
                password = line.substring(token_password.length()).trim();
            }
            else if(line.startsWith(token_master))
            {
                master = InetAddress.getByName(line.substring(token_master.length()).trim());
            }
            else if(line.startsWith(token_port))
            {
                port = Integer.valueOf(line.substring(token_port.length()).trim());
            }
            else
            {
            }
        }
    }
    public int getMasterPort()
    {
        return port;
    }
    public int getlocalPort()
    {
        return -1;
    }
    public InetAddress getMasterAddress()
    {
        return master;
    }
    public String getUser()
    {
        return uname;
    }
    public String getPassword()
    {
        return password;
    }
}
