package pku.cbi.abcgrid.master.user;

import pku.cbi.abcgrid.master.JobScheduler;

import java.util.Random;


/**
 * This is used for testing.
 */
public class FakeUser implements Runnable
{
    JobScheduler scheduler;
    Random rand = new Random();

    public FakeUser()
    {
        scheduler = JobScheduler.getInstance();
    }

    private String ce(String pdb1,String chain1, String pdb2,String chain2)
    {
        //return "CE CE - pdb1acj.ent - pdb2acj.ent - scratch";
        return String.format("CE - %s %s %s %s scratch", pdb1, chain1, pdb2,chain2);
    }

    private String hmmpfam(String db, String inputSeq)
    {
        return String.format("hmmpfam %s %s >hmmpfamtest.dat", db, inputSeq);
    }

    private String[] blastall(String app, String db, String inputSeq)
    {
        return new String[]{"blastall", "-p",app,"-d",db,"-i",inputSeq,"-o","blastall."+System.currentTimeMillis(),"-m","9"};
    }
    private String[] bl2seq(String app, String seq1, String seq2)
    {
        return new String[]{"bl2seq", "-p",app,"-i",seq1,"-j",seq2,"-o","blseq."+System.currentTimeMillis()};
    }
    public void run()
    {
        try
        {
            String user = "admin";
            int priority = 10;
            for(int i=0;i<32;i++)
            {
                scheduler.acceptJob(user,blastall("blastp","pataa","pdb_10.fasta"));
                scheduler.acceptJob(user,bl2seq("blastp","pdb_1.fasta","pdb_1.fasta"));
            }
            while(true)
            {
                try
                {
                    break;
                }
                catch(Exception e)
                {
                    System.out.println(e);
                    //print
                }
            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }
}
