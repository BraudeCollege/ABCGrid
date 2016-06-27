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

/**
 * Update all connected worker nodes. 
 * >>>u
 */
public class UpdateCommand implements Command
{
    public static final String NAME = "u";
    private WorkerManager mgr = WorkerManager.getInstance();
    private UserManager um = UserManager.getInstance();

    public Object execute(String user,String[] cmd)
    {
        StringBuffer buffer = new StringBuffer();
        if(um.getUserGroup(user).equalsIgnoreCase("admin"))
        {
            mgr.update();
            buffer.append("Online updating started");
        }
        else
        {
            buffer.append("Insufficient privilege. Only admin can execute this command");
        }
        return buffer.toString();
    }

    public String usage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("update all worker nodes using update.conf\n");
        sb.append("COMMAND: u\n");
        sb.append("EXAMPLE: \n\n");
        sb.append(">>>u\n");
        return sb.toString();
    }
    public String toString()
    {
        return usage();
    }
    
}
