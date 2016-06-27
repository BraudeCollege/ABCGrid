package pku.cbi.abcgrid.master.user;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */
import pku.cbi.abcgrid.master.conf.Config;
import pku.cbi.abcgrid.master.conf.Entry;

/**
 * Manage ABCUser
 * */
public class UserManager
{
    private Config config = Config.getInstance();
    private Entry entry = config.getEntry();
    private static UserManager inst = new UserManager();
    private UserManager()
    {
        //entry
    }
    public static UserManager getInstance()
    {
        return inst;
    }
    public boolean isValidUser(String name,String password,String address)
    {
        return entry.isValidUser(name,password,address);
    }
    public void registerUser(String name, String address)
    {
        entry.registerUser(name,address);
    }
    public String getAddressOfRegisteredUser(String username)
    {
        return entry.getAddressOfRegisteredUser(username);
    }
    public int getUserPriority(String username)
    {
        return entry.getUserPriority(username);
    }
    public String getUserGroup(String username)
    {
        return entry.getUserGroup(username);
    }
}
