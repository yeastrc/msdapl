/**
 * PiCalculator.java
 * @author Vagisha Sharma
 * Nov 2, 2009
 * @version 1.0
 */
package org.yeastrc.ms.util;


/**
 * Code taken from calc_pI.cpp in the Sashimi SourceForge repository: 
 * http://sashimi.svn.sourceforge.net/viewvc/sashimi/trunk/trans_proteomic_pipeline/src/util/ 
 */
public class PiCalculator {

    private static final int PH_MIN = 0;        /* minimum pH value */
    private static final int PH_MAX = 14;       /* maximum pH value */
    private static final int MAXLOOP = 2000;    /* maximum number of iterations */
    private static final double EPSI = 0.0001;  /* desired precision */
    
    
    /* the 7 amino acid which matter */
    private static int R = 'R' - 'A',
               H = 'H' - 'A',
               K = 'K' - 'A',
               D = 'D' - 'A',
               E = 'E' - 'A',
               C = 'C' - 'A',
               Y = 'Y' - 'A';
    /*
     *  table of pk values : 
     *  Note: the current algorithm does not use the last two columns. Each 
     *  row corresponds to an amino acid starting with Ala. J, O and U are 
     *  inexistant, but here only in order to have the complete alphabet.
     *
     *          Ct    Nt   Sm     Sc     Sn
     */
    private static double  pk [][] = {
        /* A */    {3.55, 7.59, 0.0  , 0.0  , 0.0}   ,
        /* B */    {3.55, 7.50, 0.0  , 0.0  , 0.0}   ,
        /* C */    {3.55, 7.50, 9.00 , 9.00 , 9.00}  ,
        /* D */    {4.55, 7.50, 4.05 , 4.05 , 4.05}  ,
        /* E */    {4.75, 7.70, 4.45 , 4.45 , 4.45}  ,
        /* F */    {3.55, 7.50, 0.0  , 0.0  , 0.0}   ,
        /* G */    {3.55, 7.50, 0.0  , 0.0  , 0.0}   ,
        /* H */    {3.55, 7.50, 5.98 , 5.98 , 5.98}  ,
        /* I */    {3.55, 7.50, 0.0  , 0.0  , 0.0}   ,
        /* J */    {0.00, 0.00, 0.0  , 0.0  , 0.0}   ,
        /* K */    {3.55, 7.50, 10.00, 10.00, 10.00} ,
        /* L */    {3.55, 7.50, 0.0  , 0.0  , 0.0}   ,
        /* M */    {3.55, 7.00, 0.0  , 0.0  , 0.0}   ,
        /* N */    {3.55, 7.50, 0.0  , 0.0  , 0.0}   ,
        /* O */    {0.00, 0.00, 0.0  , 0.0  , 0.0}   ,
        /* P */    {3.55, 8.36, 0.0  , 0.0  , 0.0}   ,
        /* Q */    {3.55, 7.50, 0.0  , 0.0  , 0.0}   ,
        /* R */    {3.55, 7.50, 12.0 , 12.0 , 12.0}  ,
        /* S */    {3.55, 6.93, 0.0  , 0.0  , 0.0}   ,
        /* T */    {3.55, 6.82, 0.0  , 0.0  , 0.0}   ,
        /* U */    {0.00, 0.00, 0.0  , 0.0  , 0.0}   ,
        /* V */    {3.55, 7.44, 0.0  , 0.0  , 0.0}   ,
        /* W */    {3.55, 7.50, 0.0  , 0.0  , 0.0}   ,
        /* X */    {3.55, 7.50, 0.0  , 0.0  , 0.0}   ,
        /* Y */    {3.55, 7.50, 10.00, 10.00, 10.00} ,
        /* Z */    {3.55, 7.50, 0.0  , 0.0  , 0.0}   };
    
    

    
    private PiCalculator() {}
    
    private static double exp10(double value) {
        return Math.pow(10, value);
    }
    
    public static double calculatePi(String sequence) {
        
        int[] aaCount = new int[26];
        int nterm_res; // nterm residue
        int cterm_res; // cterm residue
        
        sequence = sequence.trim().toUpperCase().replaceAll("[^A-Z]", "");
        
        for(int i = 0; i < sequence.length(); i++) {
            aaCount[sequence.charAt(i) - 'A']++;
        }
        
        nterm_res = sequence.charAt(0) - 'A';                       /* Look up N-terminal residue */
        cterm_res = sequence.charAt(sequence.length()-1) - 'A';     /* Look up C-terminal residue */

        
        double ph_min = PH_MIN;
        double ph_max = PH_MAX;
        double ph_mid = ph_min + (ph_max - ph_min) / 2.0;;
        double charge = 1.0;
        double cter, nter;
        
        double carg, chis, clys, casp, cglu, ccys, ctyr;
        
        int charge_increment = 0;
        
        for (int i = 0; i<MAXLOOP && (ph_max - ph_min)>EPSI; i++) {
           ph_mid = ph_min + (ph_max - ph_min) / 2.0;
         
           cter = exp10(-pk[cterm_res][0]) / (exp10(-pk[cterm_res][0]) + exp10(-ph_mid));
           nter = exp10(-ph_mid) / (exp10(-pk[nterm_res][1]) + exp10(-ph_mid));
         
           carg = aaCount[R] * exp10(-ph_mid) / (exp10(-pk[R][2]) + exp10(-ph_mid));
           chis = aaCount[H] * exp10(-ph_mid) / (exp10(-pk[H][2]) + exp10(-ph_mid));
           clys = aaCount[K] * exp10(-ph_mid) / (exp10(-pk[K][2]) + exp10(-ph_mid));
         
           casp = aaCount[D] * exp10(-pk[D][2]) / (exp10(-pk[D][2]) + exp10(-ph_mid));
           cglu = aaCount[E] * exp10(-pk[E][2]) / (exp10(-pk[E][2]) + exp10(-ph_mid));
         
           ccys = aaCount[C] * exp10(-pk[C][2]) / (exp10(-pk[C][2]) + exp10(-ph_mid));
           ctyr = aaCount[Y] * exp10(-pk[Y][2]) / (exp10(-pk[Y][2]) + exp10(-ph_mid));
         
           charge = carg + clys + chis + nter + charge_increment 
              - (casp + cglu + ctyr + ccys + cter);
         
           if (charge > 0.0)
           {
              ph_min = ph_mid;
           }
           else
           {
              ph_max = ph_mid;
           }
        }
        return Math.round(ph_mid*100.0) / 100.0;
    }
}
