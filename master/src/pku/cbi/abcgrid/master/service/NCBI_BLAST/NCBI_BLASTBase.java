package pku.cbi.abcgrid.master.service.NCBI_BLAST;
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
import java.util.ArrayList;
import java.util.List;

public class NCBI_BLASTBase extends AbstractApp
{
    //local input file
    protected List<String> param_linput = new ArrayList<String>();
    //remote input file or database.
    protected List<String> param_rinput = new ArrayList<String>();

    private Config config = Config.getInstance();

    public NCBI_BLASTBase()
    {
        super();
    }

    public String getServiceName()
    {
        return "NCBI_BLAST";
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
        for (int i = 0; i < commands.length; i++)
        {
            if (i == 0) //blastall
            {
                commands[i] = "#" + commands[i];
            }
            else if (param_rinput.contains(commands[i]))// -d
            {
                commands[i + 1] = "@" + commands[i + 1];
                //filtered_cmds.add("#"+commands[i+1]);
            }
            else if (param_linput.contains(commands[i]))// -i
            {
                String fname = commands[i + 1];
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
