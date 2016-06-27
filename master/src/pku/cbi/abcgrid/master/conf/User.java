package pku.cbi.abcgrid.master.conf;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

public class User
{
    private String name;
    private String password;
    private String address;
    private String priority;
    private String group;
    private String home;
    public void setUser(String n,String pwd,String addr,String pri,String grp,String hm)
    {
        name = n;
        password = pwd;
        address = addr;
        priority = pri;
        group = grp;
        home = hm;
    }
    public String getName()
    {
        return name;
    }
    public String getPassword()
    {
        return password;
    }
    public String getAddress()
    {
        return address;
    }
    public int getPriority()
    {
        return Integer.valueOf(priority);
    }
    public String getGroup()
    {
        return group;
    }
    public String getHome()
    {
        return home;
    }
}
