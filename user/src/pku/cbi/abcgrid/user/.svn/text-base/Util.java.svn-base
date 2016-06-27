package pku.cbi.abcgrid.user;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

public class Util
{
    public static String join(String[] strings, String seperator)
    {
        StringBuffer sb = new StringBuffer();
        int N = strings.length;
        if (N<=0)
        {
            return null;
        }
        for (int i = 0; i < N - 1; i++)
        {
            sb.append(strings[i]);
            sb.append(seperator);
        }
        sb.append(strings[N - 1]);
        return sb.toString();
    }
}
