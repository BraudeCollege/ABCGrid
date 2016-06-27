package pku.cbi.abcgrid.master.conf;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entry
{
    private List<User> users;
    private Port port;
    private Map<String,String> reg_users;
    public Entry()
    {
        users = new ArrayList<User>();
        reg_users = new HashMap<String,String>();
    }
    public void addUser(User user)
    {
        users.add(user);
    }
    public void setPort(Port p)
    {
        port = p;
    }
    public int getWorkerPort()
    {
        return port.getWorkerPort();
    }
    public int getUserPort()
    {
        return port.getUserPort();
    }
    /**
     * authenticate a user by username, password and IP address.
     * @param name username
     * @param password password
     * @param address  IP address
     * @return true-valid user
     * */
    public boolean isValidUser(String name,String password,String address)
    {
        //password and address could be ""(empty string)
        for(User u:users)//iterate local user's list in master.conf
        {
            if(u.getName().equalsIgnoreCase(name))
            {
                //local password
                String lpasswd = u.getPassword().trim();
                //valiadte password. "" means no password validation
                if(lpasswd.length()!=0&&!lpasswd.equals(password))
                    return false;
                //password is OK. Next validate IP addess.
                String addr = u.getAddress().trim();
                return addr.length()==0||addr.equalsIgnoreCase(addr);
            }
        }
        return false;
    }
    public User getUser(String username)
    {
        for(User u:users)
        {
            if(u.getName().equalsIgnoreCase(username))
                return u;
        }
        return null;
    }
    public void registerUser(User user, String address)
    {
        reg_users.put(user.getName(),address);
    }
    public void registerUser(String user, String address)
    {
        reg_users.put(user,address);
    }
    public String getAddressOfRegisteredUser(String username)
    {
        return reg_users.get(username);
    }
    public int getUserPriority(String username)
    {
        return getUser(username).getPriority();
    }
    public String getUserGroup(String username)
    {
        return getUser(username).getGroup();
    }
    public String getUserHome(String username)
    {
        String home = getUser(username).getHome();
        return FilenameUtils.normalize(home);
    }
}
