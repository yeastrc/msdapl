/**
 * MsTerminalModification.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;



/**
 * 
 */
public interface MsTerminalModification extends MsTerminalModificationIn {

    final char N = 'N';
    final char C = 'C';
    
    public enum Terminal {CTERM(C), NTERM(N);
        
        public static Terminal instance(char termChar) {
            termChar = Character.toUpperCase(termChar);
            switch(termChar) {
                case(N):   return NTERM;
                case(C):   return CTERM;
                default:     return null;
            }
        }
        private final char myChar;
        private Terminal(char myChar) {this.myChar = myChar;}
        public char toChar() {return myChar;}
    };
    
    public abstract int getId();
}
