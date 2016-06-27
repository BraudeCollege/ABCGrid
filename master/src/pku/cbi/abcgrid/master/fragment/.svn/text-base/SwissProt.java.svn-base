package pku.cbi.abcgrid.master.fragment;
/**
 * ######################################################
 * #    Ying Sun                                        #
 * #    Center for Bioinformatics, Peking University.   #
 * #    mail.suny@gmail.com                             #
 * #    Copyright 2006                                  #
 * ######################################################
 */

/*
SwissProt format

A sequence file in SwissProt format can contain one sequence.
One sequence in SwissProt format starts with a line containing the word ID
and a number of annotation lines.
The start of the sequence is marked by a line containing "SQ" and the end
of the sequence is marked by two slashes ("//").

An example sequence in GenBank format is:

ID   108_LYCES      STANDARD;      PRT;   102 AA.
AC   Q43495;
DT   15-JUL-1999 (Rel. 38, Created)
DR   SMART; SM00499; AAI; 1.
KW   Signal.
FT   SIGNAL        1     30       Potential.
FT   CHAIN        31    102       Protein 108.
SQ   SEQUENCE   102 AA;  10576 MW;  CFBAA1231C3A5E92 CRC64;
     MASVKSSSSS SSSSFISLLL LILLVIVLQS QVIECQPQQS CTASLTGLNV CAPFLVPGSP
     TASTECCNAV QSINHDCMCN TMRIAAQIPA QCNLPPLSCS AN
//

*/
public class SwissProt extends BaseFragment
{
    protected String DELIMITER = "//";
    protected boolean include_delimiter_at_end = true;
    public String   getSeperator()
    {
        return "//"; 
    }
}
