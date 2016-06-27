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
 * A command inputed by user from Console.
 * >>>COMMAND
  */
public interface Command
{
    public Object execute(String user, String[] cmd)throws Exception;
    public String usage();
}
