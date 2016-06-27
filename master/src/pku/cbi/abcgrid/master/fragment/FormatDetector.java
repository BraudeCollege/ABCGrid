package pku.cbi.abcgrid.master.fragment;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * detect the format of files.
 * 
 * */
public class FormatDetector
{
    /**
     * get the fragmenter used to fragment files.
     * Support FASTA, Genbank, EMBL and PDB currently.
     *
     * @param input Input file
     * @return A <code>Fragmentable</code> object.  
     * */
    public static Fragmentable getFragmenter(File input)
    {
        // Well-behaved formats identify themselves in first nonblank line.
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String line;
            while ((line = reader.readLine()) != null)
            {
                String nline = line.trim();
                if (nline.length() > 0)
                {
                    if (nline.startsWith(">"))
                    {
                        return new FASTA();
                    }
                    else if (nline.startsWith("LOCUS") || nline.startsWith("ORIGIN"))
                    {
                        return new Genbank();
                    }
                    else if (nline.startsWith("ID") || nline.startsWith("SQ"))
                    {
                        //SwissProt and EMBL have same signature...
                        return new EMBL();
                    }
                    else if (nline.startsWith("HEADER"))
                    {
                        return new PDB();
                    }
                    else
                    {
                        //unknown format
                        return null;
                    }
                }
            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
        return null;
    }
}
