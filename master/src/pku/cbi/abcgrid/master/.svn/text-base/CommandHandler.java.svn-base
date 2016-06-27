package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Process a Command, redirect to XXXCommand (sub-class of interface Command)
 */
public class CommandHandler
{
    private Map<String,Command> command_map;
    private static CommandHandler instance = new CommandHandler();
    private CommandHandler()
    {
        command_map = new HashMap<String,Command>();
        command_map.put(JobCommand.NAME,new JobCommand());
        command_map.put(HelpCommand.NAME,new HelpCommand());
        command_map.put(UpdateCommand.NAME,new UpdateCommand());
        command_map.put(WorkerCommand.NAME,new WorkerCommand());
        command_map.put(ServiceCommand.NAME,new ServiceCommand());
        command_map.put(RunCommand.NAME,new RunCommand());
        command_map.put(KillCommand.NAME,new KillCommand());
    }
    public static CommandHandler getInstance()
    {
        return instance;
    }
    /**
     * handle the command issuded by user.
     *
     * @param user commander
     * @param cmd command
     * @return Output message 
     * */
    public String handle(String user,String cmd)
    {
        String ret = null; 
        try
        {
            String[] cmds = Util.normalize_command(cmd);
            Command c = command_map.get(cmds[0]);
            if(c!=null)
            {
                try
                {
                    Object r = c.execute(user,cmds);
                    if(r!=null)
                        ret = r.toString();
                }
                catch(Exception e)
                {
                    ret = "Error:"+e.getMessage();
                }
            }
            else
            {
                ret="";
                //ret = "Error:"+" Invalid command, "+cmd;
            }
        }
        catch(Exception e)
        {
            ret = "Error:"+e.getMessage();
        }
        return ret;
    }
    class CommandRunner implements Callable<String>
    {
        private Command command;
        private String user;
        private String[] cmds;
        public CommandRunner(Command cm,String u,String[] c)
        {
            command = cm;
            user = u;
            cmds = c;
        }
        public String call()
        {
            String ret="";
            try
            {
                Object r = command.execute(user,cmds);
                if(r!=null)
                    ret = r.toString();
            }
            catch(Exception e)
            {
                ret = "Error:"+e.getMessage();
            }
            return ret;
        }

    }
}
