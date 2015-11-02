package org.yeastrc.ms.domain.protinfer;

public enum ProteinUserValidation {

    UNVALIDATED('U'), ACCEPTED('A'), REJECTED('R'), NOT_SURE('N');
    
    private char statusChar;
    
    private ProteinUserValidation(char statusChar) {this.statusChar = statusChar;}
    
    public char getStatusChar() {return statusChar;}
    
    public static ProteinUserValidation getStatusForChar(char status) {
        switch (status) {
            case 'U':
                return UNVALIDATED;
            case 'A':
                return ACCEPTED;
            case 'R':
                return REJECTED;
            case 'N':
                return NOT_SURE;
            default:
                return null;
        }
    }
}
