package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

/**
 * print help message. Trigered by command "h"
 * >>>h
 */
public class HelpCommand implements Command
{
    public static final String NAME = "h";
    public Object execute(String user,String[] cmds)
    {
        if(cmds.length==1)
        {
            return usage();
        }
        else
        {
            String cmdtohelp = cmds[1];
        }
        return usage();
    }
    public String usage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("h:\tprint this message.\n");
        sb.append("j:\tquery submitted job(s).\n");
        sb.append("k:\tkill submitted job(s).\n");
        sb.append("r:\trun a job.\n");
        sb.append("s:\tshow supported bioinformatics services.\n");
        sb.append("u:\tupdate worker nodes.\n");
        sb.append("w:\tshow connected worker nodes.\n");
        sb.append("quit:\tquit ABCMaster.\n");
        return sb.toString();
    }
}