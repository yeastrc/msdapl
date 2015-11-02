package org.yeastrc.ms.domain.protinfer;

public enum ProteinferStatus {

    PENDING('P'), RUNNING('R'), COMPLETE('C'), SAVED('S');
    
    private char statusChar;
    
    private ProteinferStatus(char statusChar) {this.statusChar = statusChar;}
    
    public char getStatusChar() {return statusChar;}
    
    public static ProteinferStatus getStatusForChar(char status) {
        switch (status) {
            case 'P':
                return PENDING;
            case 'R':
                return RUNNING;
            case 'C':
                return COMPLETE;
            case 'S':
                return SAVED;
            default:
                return null;
        }
    }
}
