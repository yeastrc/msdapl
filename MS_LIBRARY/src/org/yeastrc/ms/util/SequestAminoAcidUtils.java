/**
 * SequestAminoAcidUtils.java
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
public class SequestAminoAcidUtils extends BaseAminoAcidUtils {

	private static char[] aminoAcidsChars;
    private static final Set<Character> validChars = new HashSet<Character>();
    
    static {
        validChars.add('X'); // Leucine or Isoleucine
        validChars.add('B'); // Aspartic Acid or Asparagine
        validChars.add('Z'); // Glutamatic Acid or Glutamine
        validChars.add('O'); // Ornithine
    }
    
    public SequestAminoAcidUtils() {}
    
    public String getFullName(char aa) {
    	
    	switch(aa) {
	    	case 'X':
	    		return "LorI";
	    	case 'B':
	    		return "avg_NandD";
	    	case 'Z': 
	    		return "avg_QandE";
	    	case 'O':
	    		return "Ornithine";
	    	default:
	    		return super.getFullName(aa);
    	}
    }

    public char[] getAminoAcidChars() {
    	
    	if(aminoAcidsChars == null) {
    		aminoAcidsChars = new char[24];
    		char[] stdAA = super.getAminoAcidChars();
    		for(int i = 0; i < stdAA.length; i++) {
    			aminoAcidsChars[i] = stdAA[i];
    		}
    		
    		int idx = stdAA.length;
    		aminoAcidsChars[idx++] = 'X';
    		aminoAcidsChars[idx++] = 'B'; // Aspartic Acid or Asparagine
    		aminoAcidsChars[idx++] = 'Z'; // Glutamatic Acid or Glutamine
    		aminoAcidsChars[idx++] = 'O'; // Ornithine
    		
    	}
    	
        return aminoAcidsChars;
    }
    
    public boolean isAminoAcid(char aa) {
    	if(super.isAminoAcid(aa))
    		return true;
        return validChars.contains(Character.toUpperCase(aa));
    }
    
    public double avgMass(char aminoAcid) {
    	
    	double mass = super.avgMass(aminoAcid);
    	if(mass > 0)
    		return mass;
    	
        switch(aminoAcid) {
            case 'X': return avgMass_X();
            case 'O': return avgMass_O();
            case 'B': return avgMass_B();
            case 'Z': return avgMass_Z();
            default : return 0;
        }
    }
    
    public double monoMass(char aminoAcid) {
    	
    	double mass = super.monoMass(aminoAcid);
    	if(mass > 0)
    		return mass;
    	
        switch(aminoAcid) {
            case 'X': return monoMass_X();
            case 'O': return monoMass_O();
            case 'B': return monoMass_B();
            case 'Z': return monoMass_Z();
            default : return 0;
        }
    }
    
    
//  add_X_LorI = 0.0000                    ; added to X - avg. 113.1594, mono. 113.08406
    public  double avgMass_X() {return 113.1594;}
    public  double monoMass_X(){return 113.08406;}
//  add_O_Ornithine = 0.0000               ; added to O - avg. 114.1472, mono  114.07931
    public  double avgMass_O() {return 114.1472;}
    public  double monoMass_O(){return 114.07931;}
//  add_B_avg_NandD = 0.0000               ; added to B - avg. 114.5962, mono. 114.53494
    public  double avgMass_B() {return 114.5962;}
    public  double monoMass_B(){return 114.53494;}
//  add_Z_avg_QandE = 0.0000               ; added to Z - avg. 128.6231, mono. 128.55059
    public  double avgMass_Z() {return 128.6231;}
    public  double monoMass_Z(){return 128.55059;}
}
