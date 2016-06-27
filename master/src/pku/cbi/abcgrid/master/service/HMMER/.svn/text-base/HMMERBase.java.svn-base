package pku.cbi.abcgrid.master.service.HMMER;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import org.apache.commons.io.IOUtils;
import pku.cbi.abcgrid.master.Job;
import pku.cbi.abcgrid.master.Task;
import pku.cbi.abcgrid.master.conf.Config;
import pku.cbi.abcgrid.master.service.AbstractApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * pfam database.
 */
public class HMMERBase extends AbstractApp
{
    private Config config = Config.getInstance();

    public HMMERBase()
    {
        super();
    }

    public String getServiceName()
    {
        return "HMMER";
    }

    public Job parse(String user, String jobcmd, int priority) throws Exception
    {
        return null;
    }

    public List<Task> fragment(Job job)throws Exception
    {
        return null;
    }

    public String[] parseCommands(String user, String[] cmds)
            throws Exception
    {
        String[] commands = cmds;
        String user_dir = config.getUserDir(user);
        int length = commands.length;
        for (int i = 0; i < length; i++)
        {
            if (i == 0) //blastall
            {
                commands[i] = "#" + commands[i];
            }
            else if (i == length - 2)// remote hmm database
            {
                commands[i] = "@" + commands[i];
            }
            else if (i == length - 1)// input sequence
            {
                String fname = commands[i];
                InputStream stream = new FileInputStream(new File(user_dir, fname));
                addLocalInput(fname, IOUtils.toByteArray(stream));
            }
            else
            {

            }
        }
        return commands;
    }

}
