package pku.cbi.abcgrid.master.fragment;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import java.io.File;
import java.io.FileReader;

/*

 */
public class PDB extends BaseFragment
{
    protected boolean include_delimiter_at_end = false;
    public String   getSeperator()
    {
        return null; 
    }
    public Object[] fragment2(File finput,int number)
    {
        //A PDB file could NOT be fragmented, return the content of finput.
        try
        {
            int length = (int)finput.length();
            char[] buffer = new char[length];
            FileReader fr = new FileReader(finput);
            fr.read(buffer,0,length);
            fr.close();
            Object[] ret = new Object[1];
            ret[0] = new String(buffer);
            return ret;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new Object[0];
    }
}
