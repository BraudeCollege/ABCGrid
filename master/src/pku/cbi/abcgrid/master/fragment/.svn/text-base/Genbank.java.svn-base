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
GenBank format

A sequence file in GenBank format can contain several sequences.
One sequence in GenBank format starts with a line containing the word LOCUS
and a number of annotation lines.
The start of the sequence is marked by a line containing "ORIGIN" and the end
of the sequence is marked by two slashes ("//").

An example sequence in GenBank format is:


LOCUS       AB000263                 368 bp    mRNA    linear   PRI 05-FEB-1999
DEFINITION  Homo sapiens mRNA for prepro cortistatin like peptide, complete
            cds.
ACCESSION   AB000263
ORIGIN
        1 acaagatgcc attgtccccc ggcctcctgc tgctgctgct ctccggggcc acggccaccg
       61 ctgccctgcc cctggagggt ggccccaccg gccgagacag cgagcatatg caggaagcgg
      121 caggaataag gaaaagcagc ctcctgactt tcctcgcttg gtggtttgag tggacctccc
      181 aggccagtgc cgggcccctc ataggagagg aagctcggga ggtggccagg cggcaggaag
      241 gcgcaccccc ccagcaatcc gcgcgccggg acagaatgcc ctgcaggaac ttcttctgga
      301 agaccttctc ctcctgcaaa taaaacctca cccatgaatg ctcacgcaag tttaattaca
      361 gacctgaa
//


*/


public class Genbank extends BaseFragment
{
    protected boolean include_delimiter_at_end = true;
    public Genbank()
    {
        super();
        MAX_TASK_SIZE_BYTES = 1024*1024;//1024KB
        MIN_TASK_SIZE_BYTES = 1024*1;//1KB
        MIN_WORKERS_NUMBER  = 10;//10 workers(split one job into 10 or more tasks)
    }
    public String   getSeperator()
    {
        return "//"; 
    }

}
