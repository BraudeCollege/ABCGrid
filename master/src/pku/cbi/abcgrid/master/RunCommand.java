package pku.cbi.abcgrid.master;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import pku.cbi.abcgrid.master.conf.Config;

import java.io.File;

/**
  * run job
 *
 * r [user] [application] [paramters ...]
 * e.g.
 * r suny blastall -p blastp -d nr -i abc.fasta -o abc.blastp
 */
public class RunCommand  implements Command
{
    public static final String NAME = "r";
    private JobScheduler    scheduler;
    private Config          config;

    public RunCommand()
    {
        config = Config.getInstance();
        scheduler = JobScheduler.getInstance();
    }

    public Object execute(String user,String[] cmd)throws Exception
    {
        //r [commands] 
        if(cmd.length<2)
        {
            return usage();
            //throw new Exception(sb.toString());
        }
        String working_dir = config.getUserDir(user);
        File f = new File(working_dir);
        if(!f.exists() || ! f.isDirectory())
        {
            String err = String.format("User [%s]'s working directory [%s] does not exist\n",user,working_dir);
            throw new Exception(err);
        }
        String app = cmd[1]; //the application name. e.g. blastall, dalilite.
        if(! scheduler.isAppSupported(app))
        {
            String err = String.format("Application [%s] is not supported (Name is case-sensitive)\n",app);
            throw new Exception(err);
        }
        //Remove the first word: "r blastall ..." -> "blastall ..."
        String[] cmd2 = new String[cmd.length-1];
        System.arraycopy(cmd,1,cmd2,0,cmd.length-1);
        long jid = scheduler.acceptJob(user,cmd2);
        return  String.format("Job:[%s] is accepted with assigned id %d\n",Util.join(cmd2),jid);
    }
    public String usage()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Run a job\n");
        sb.append("COMMAND: r [command]\n");
        sb.append("EXAMPLE: \n\n");
        sb.append(">>>r blastall -p blastp -i pdb_10.fasta -d nr -o pdb_10.blastp -m 9\n");
        sb.append("run a blastp search again database nr.\n\n");

        sb.append(">>>r hmmpfam  pdb_10.fasta Pfam_ls 1>pdb_10.hmmpfam\n");
        sb.append("run a hmmpfam search against database Pfam_ls, \n");
        sb.append("the output result is redirected to file 'pdb_10.hmmpfam'\n\n");

        sb.append(">>>r CE - pdb1acj.ent - pdb2acj.ent - scratch >1acj2acj.CE\n");
        sb.append("run CE to align PDB structure pdb1acj.ent and pdb1acj.ent, \n");
        sb.append("the output and error message are redirected to file '1acj2acj.CE'\n\n");


        return sb.toString();
    }
    public String toString()
    {
        return usage();
    }
}
