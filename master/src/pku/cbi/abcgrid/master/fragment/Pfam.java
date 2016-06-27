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
Pfam format

A database file in Pfam format can contain several entries.
One entry in Pfam format starts with a line containing the word HMMER2.0
and a number of annotation lines.
The start of the HMM sequence is marked by a line containing "HMM" and the end
of the HMM sequence is marked by two slashes ("//").

An example sequence in GenBank format is:

HMMER2.0  [2.3.2]
NAME  14-3-3
ACC   PF00244.9
DESC  14-3-3 protein
LENG  236
ALPH  Amino
RF    no
CS    no
MAP   yes
COM   hmmbuild -F HMM_ls.ann SEED.ann
COM   hmmcalibrate --seed 0 HMM_ls.ann
NSEQ  16
DATE  Mon Feb 20 09:44:02 2006
CKSUM 1793
GA    25.0 25.0
TC    28.5 28.5
NC    22.7 22.7
XT      -8455     -4  -1000  -1000  -8455     -4  -8455     -4
NULT      -4  -8455
NULE     595  -1558     85    338   -294    453  -1158    197    249    902  -1085   -142    -21   -313     45    531    201    384  -1998   -644
EVD   -94.870102   0.166866
HMM        A      C      D      E      F      G      H      I      K      L      M      N      P      Q      R      S      T      V      W      Y
         m->m   m->i   m->d   i->m   i->i   d->m   d->d   b->m   m->e
         -600      *  -1555
     1   -622  -1462   -841   -124  -1852  -1462    158  -1385   1453  -1390   -668   -229  -1527    548   2515   -593   -496   -556  -1546  -1178     1
     -   -149   -500    233     43   -381    399    106   -626    210   -466   -720    275    394     45     96    359    117   -369   -294   -249
     -    -34  -5995  -7037   -894  -1115   -701  -1378   -600      *
     2     30  -1691    929   2139  -2002   -936     57  -1692    251  -1753   -944    384  -1246    441   -300   -212     49  -1309  -2011  -1304     2
     -   -149   -500    233     43   -381    399    106   -626    210   -466   -720    275    394     45     96    359    117   -369   -294   -249
     -    -34  -5995  -7037   -894  -1115   -701  -1378      *      *
     6   -606  -1113   -601   -277    392  -1511    138   -829     80   -841   -314   -373  -1652   1678   -154   -657   -556   -701   -172   2815     6
     -   -149   -500    233     43   -381    399    106   -626    210   -466   -720    275    394     45     96    359    117   -369   -294   -249
     -    -34  -5995  -7037   -894  -1115   -701  -1378      *      *
   236     36  -1496    613   1547  -1759    207    191  -1470    495  -1503   -652    345  -1182    837    -22    -71   -142   -704  -1749  -1083   242
     -      *      *      *      *      *      *      *      *      *      *      *      *      *      *      *      *      *      *      *      *
     -      *      *      *      *      *      *      *      *      0
//
*/

public class Pfam  extends BaseFragment
{

   protected String DELIMITER = "//";
   protected boolean include_delimiter_at_end = true;
    public String   getSeperator()
    {
        return "//";
    }
    
}
