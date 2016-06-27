package pku.cbi.abcgrid.user;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import java.io.IOException;
public class Test
{
    class PseudoUser extends Thread
    {
        String cmd;
        public PseudoUser(String param)
        {
            cmd = param;
        }
        public void run()
        {
            UserMain user = new UserMain();
            Config config = new Config();
            try
            {
                //read configuration from user.conf
                config.init();
                user.connectMaster(config.getMasterAddress(), config.getMasterPort());
                user.sendCommand(config.getUser(), config.getPassword(), cmd);
                System.out.println(user.recvResponse());
            }
            catch (IOException e)
            {
                System.err.println("Error: "+e);
            }
            finally
            {
                user.close();
            }
        }
    }
    public static void main(String[] args)
    {
        String[] commands = {"r blastall -p -i pdb_1.fasta -d pataa -o pdb",
        "CE - pdb1acj.ent - pdb2acj.ent - scratch"};
    }

}
