/**
 * BaseAminoAcidUtils.java
 * @author Vagisha Sharma
 * Apr 15, 2010
 * @version 1.0
 */
package org.yeastrc.ms.util;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 */
public class BaseAminoAcidUtils {

	public static final double HYDROGEN = 1.00794;
    public static final double OXYGEN = 15.9994;
    public static final double PROTON = 1.00727646688;
    
    public static final double NTERM_MASS = BaseAminoAcidUtils.HYDROGEN;
    public static final double CTERM_MASS =  BaseAminoAcidUtils.HYDROGEN + BaseAminoAcidUtils.OXYGEN;
    
	private static final char[] aminoAcidsChars = new char[20];
    private static final Set<Character> validChars = new HashSet<Character>();
    
    static {
        validChars.add('G');
        validChars.add('A');
        validChars.add('S');
        validChars.add('P');
        validChars.add('V');
        validChars.add('T');
        validChars.add('C');
        validChars.add('L');
        validChars.add('I');
        validChars.add('N');
        validChars.add('D');
        validChars.add('Q');
        validChars.add('K');
        validChars.add('E');
        validChars.add('M');
        validChars.add('H');
        validChars.add('F');
        validChars.add('R');
        validChars.add('Y');
        validChars.add('W');
        
        int i = 0;
        aminoAcidsChars[i++] = 'A'; // Ananine
        aminoAcidsChars[i++] = 'C'; // Cysteine
        aminoAcidsChars[i++] = 'D'; // Aspartic Acid
        aminoAcidsChars[i++] = 'E'; // Glutamic Acid
        aminoAcidsChars[i++] = 'F'; // Phenyl-ananine
        aminoAcidsChars[i++] = 'G'; // Glycine
        aminoAcidsChars[i++] = 'H'; // Histidine
        aminoAcidsChars[i++] = 'I'; // Isoleucine
        aminoAcidsChars[i++] = 'K'; // Lysine
        aminoAcidsChars[i++] = 'L'; // Leucine
        aminoAcidsChars[i++] = 'M'; // Methionine
        aminoAcidsChars[i++] = 'N'; // Asparagine
        aminoAcidsChars[i++] = 'P'; // Proline  
        aminoAcidsChars[i++] = 'Q'; // Glutamine
        aminoAcidsChars[i++] = 'R'; // Arginine        
        aminoAcidsChars[i++] = 'S'; // Serine
        aminoAcidsChars[i++] = 'T'; // Threonine
        aminoAcidsChars[i++] = 'V'; // Valine
        aminoAcidsChars[i++] = 'W'; // Tryptophan
        aminoAcidsChars[i++] = 'Y'; // Tyrosine
        
    }
    
    public BaseAminoAcidUtils() {}
    
    public char[] getAminoAcidChars() {
        return aminoAcidsChars;
    }
    
    public boolean isAminoAcid(char aa) {
        return validChars.contains(Character.toUpperCase(aa));
    }
    
    public String getFullName(char aa) {
    	
    	if(isAminoAcid(aa)) {
    		switch(aa) {
	    		case 'A':
	    			return "Alanine";
	    		case 'C':
	    			return "Cysteine";
	    		case 'D': 
	    			return "Aspartic_Acid";
	    		case 'E':
	    			return "Glutamic_Acid";
	    		case 'F': 
	    			return "Phenyalanine";
	    		case 'G':
	    			return "Glycine";
	    		case 'H': 
	    			return "Histidine";
	    		case 'I':
	    			return "Isoleucine";
	    		case 'K':
	    			return "Lysine";
	    		case 'L': 
	    			return "Leucine";
	    		case 'M':
	    			return "Methionine";
	    		case 'N':
	    			return "Asparagine";
	    		case 'P':
	    			return "Proline";  
	    		case 'Q': 
	    			return "Glutamine";
	    		case 'R': 
	    			return "Arginine";        
	    		case 'S': 
	    			return "Serine";
	    		case 'T': 
	    			return "Threonine";
	    		case 'V': 
	    			return "Valine";
	    		case 'W': 
	    			return "Tryptophan";
	    		case 'Y': 
	    			return "Tyrosine";
	    		default:
	    			return aa+"";
    		}
    	}
    	
    	return aa+"";
    }
    
    public double avgMass(char aminoAcid) {
        switch(aminoAcid) {
            case 'G': return avgMass_G();
            case 'A': return avgMass_A();
            case 'S': return avgMass_S();
            case 'P': return avgMass_P();
            case 'V': return avgMass_V();
            case 'T': return avgMass_T();
            case 'C': return avgMass_C();
            case 'L': return avgMass_L();
            case 'I': return avgMass_I();
            case 'N': return avgMass_N();
            case 'D': return avgMass_D();
            case 'Q': return avgMass_Q();
            case 'K': return avgMass_K();
            case 'E': return avgMass_E();
            case 'M': return avgMass_M();
            case 'H': return avgMass_H();
            case 'F': return avgMass_F();
            case 'R': return avgMass_R();
            case 'Y': return avgMass_Y();
            case 'W': return avgMass_W();
            default : return 0;
        }
    }
    
    public double monoMass(char aminoAcid) {
        switch(aminoAcid) {
            case 'G': return monoMass_G();
            case 'A': return monoMass_A();
            case 'S': return monoMass_S();
            case 'P': return monoMass_P();
            case 'V': return monoMass_V();
            case 'T': return monoMass_T();
            case 'C': return monoMass_C();
            case 'L': return monoMass_L();
            case 'I': return monoMass_I();
            case 'N': return monoMass_N();
            case 'D': return monoMass_D();
            case 'Q': return monoMass_Q();
            case 'K': return monoMass_K();
            case 'E': return monoMass_E();
            case 'M': return monoMass_M();
            case 'H': return monoMass_H();
            case 'F': return monoMass_F();
            case 'R': return monoMass_R();
            case 'Y': return monoMass_Y();
            case 'W': return monoMass_W();
            default : return 0;
        }
    }
    
    
//  add_G_Glycine = 0.0000                 ; added to G - avg.  57.0519, mono.  57.02146
    private  double avgMass_G() {return 57.0519;}
    private  double monoMass_G(){return 57.02146;}
//  add_A_Alanine = 0.0000                 ; added to A - avg.  71.0788, mono.  71.03711
    private  double avgMass_A() {return 71.0788;}
    private  double monoMass_A(){return 71.03711;}
//  add_S_Serine = 0.0000                  ; added to S - avg.  87.0782, mono.  87.02303
    private  double avgMass_S() {return 87.0782;}
    private  double monoMass_S(){return 87.02303;}
//  add_P_Proline = 0.0000                 ; added to P - avg.  97.1167, mono.  97.05276
    private  double avgMass_P() {return 97.1167;}
    private  double monoMass_P(){return 97.05276;}
//  add_V_Valine = 0.0000                  ; added to V - avg.  99.1326, mono.  99.06841
    private  double avgMass_V() {return 99.1326;}
    private  double monoMass_V(){return 99.06841;}
//  add_T_Threonine = 0.0000               ; added to T - avg. 101.1051, mono. 101.04768
    private  double avgMass_T() {return 101.1051;}
    private  double monoMass_T(){return 101.04768;}
//  add_C_Cysteine = 57.021                ; added to C - avg. 103.1388, mono. 103.00919
    private  double avgMass_C() {return 103.1388;}
    private  double monoMass_C(){return 103.00919;}
//  add_L_Leucine = 0.0000                 ; added to L - avg. 113.1594, mono. 113.08406
    private  double avgMass_L() {return 113.1594;}
    private  double monoMass_L(){return 113.08406;}
//  add_I_Isoleucine = 0.0000              ; added to I - avg. 113.1594, mono. 113.08406
    private  double avgMass_I() {return 113.1594;}
    private  double monoMass_I(){return 113.08406;}
//  add_N_Asparagine = 0.0000              ; added to N - avg. 114.1038, mono. 114.04293
    private  double avgMass_N() {return 114.1038;}
    private  double monoMass_N(){return 114.04293;}
//  add_D_Aspartic_Acid = 0.0000           ; added to D - avg. 115.0886, mono. 115.02694
    private  double avgMass_D() {return 115.0886;}
    private  double monoMass_D(){return 115.02694;}
//  add_Q_Glutamine = 0.0000               ; added to Q - avg. 128.1307, mono. 128.05858
    private  double avgMass_Q() {return 128.1307;}
    private  double monoMass_Q(){return 128.05858;}
//  add_K_Lysine = 0.0000                  ; added to K - avg. 128.1741, mono. 128.09496
    private  double avgMass_K() {return 128.1741;}
    private  double monoMass_K(){return 128.09496;}
//  add_E_Glutamic_Acid = 0.0000           ; added to E - avg. 129.1155, mono. 129.04259
    private  double avgMass_E() {return 129.1155;}
    private  double monoMass_E(){return 129.04259;}
//  add_M_Methionine = 0.0000              ; added to M - avg. 131.1926, mono. 131.04049
    private  double avgMass_M() {return 131.1926;}
    private  double monoMass_M(){return 131.04049;}
//  add_H_Histidine = 0.0000               ; added to H - avg. 137.1411, mono. 137.05891
    private  double avgMass_H() {return 137.1411;}
    private  double monoMass_H(){return 137.05891;}
//  add_F_Phenyalanine = 0.0000            ; added to F - avg. 147.1766, mono. 147.06841
    private  double avgMass_F() {return 147.1766;}
    private  double monoMass_F(){return 147.06841;}
//  add_R_Arginine = 0.0000                ; added to R - avg. 156.1875, mono. 156.10111
    private  double avgMass_R() {return 156.1875;}
    private  double monoMass_R(){return 156.10111;}
//  add_Y_Tyrosine = 0.0000                ; added to Y - avg. 163.1760, mono. 163.06333
    private  double avgMass_Y() {return 163.1760;}
    private  double monoMass_Y(){return 163.06333;}
//  add_W_Tryptophan = 0.0000              ; added to W - avg. 186.2132, mono. 186.07931
    private  double avgMass_W() {return 186.2132;}
    private  double monoMass_W(){return 186.07931;}
}
