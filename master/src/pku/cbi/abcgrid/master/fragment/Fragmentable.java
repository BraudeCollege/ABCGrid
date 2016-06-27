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
import java.io.InputStream;

public interface Fragmentable
{
    /**
     * @param finput Input file
     * @param number Number of sequences per fragment.
     * */
    public Object[] fragment(File finput,int number)throws Exception;
    public Object[] fragment(InputStream finput,int number)throws Exception;
    public String   getSeperator();
}
