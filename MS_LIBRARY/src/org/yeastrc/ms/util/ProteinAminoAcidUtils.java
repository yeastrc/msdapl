/**
 * ProteinAminoAcidUtils.java
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
public class ProteinAminoAcidUtils extends BaseAminoAcidUtils {

	private static char[] aminoAcidsChars;
    private static final Set<Character> validChars = new HashSet<Character>();
    
    static {
    	validChars.add('X');
    	validChars.add('J');
        validChars.add('B');
        validChars.add('Z');
        validChars.add('U');
    }
    
    public ProteinAminoAcidUtils() {}
    
    public char[] getAminoAcidChars() {
    	if(aminoAcidsChars == null) {
    		aminoAcidsChars = new char[25];
    		char[] stdAA = super.getAminoAcidChars();
    		for(int i = 0; i < stdAA.length; i++) {
    			aminoAcidsChars[i] = stdAA[i];
    		}
    		
    		int idx = stdAA.length;
    		aminoAcidsChars[idx++] = 'X';
    		aminoAcidsChars[idx++] = 'J';
    		aminoAcidsChars[idx++] = 'B'; // Aspartic Acid or Asparagine
    		aminoAcidsChars[idx++] = 'Z'; // Glutamatic Acid or Glutamine
    		aminoAcidsChars[idx++] = 'U'; // Glutamatic Acid or Glutamine
    		
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
            case 'B': return avgMass_B();
            case 'Z': return avgMass_Z();
            case 'U': return avgMass_U();
            default : return 0;
        }
    }
    
    public double monoMass(char aminoAcid) {
    	
    	double mass = super.monoMass(aminoAcid);
    	if(mass > 0)
    		return mass;
        switch(aminoAcid) {
            case 'B': return monoMass_B();
            case 'Z': return monoMass_Z();
            case 'U': return monoMass_U();
            default : return 0;
        }
    }
    
    
//  add_B_avg_NandD = 0.0000               ; added to B - avg. 114.5962, mono. 114.53494
    public  double avgMass_B() {return 114.5962;}
    public  double monoMass_B(){return 114.53494;}
//  add_Z_avg_QandE = 0.0000               ; added to Z - avg. 128.6231, mono. 128.55059
    public  double avgMass_Z() {return 128.6231;}
    public  double monoMass_Z(){return 128.55059;}
    // from http://www.matrixscience.com/help/aa_help.html
    // Selenocysteine
    public  double avgMass_U() {return 150.0379;}
    public  double monoMass_U(){return 150.95363;}
}
