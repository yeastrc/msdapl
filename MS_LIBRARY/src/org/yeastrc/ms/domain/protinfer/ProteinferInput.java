package org.yeastrc.ms.domain.protinfer;

public class ProteinferInput {

    private int id;
    private int pinferId;
    private int inputId;
//    private InputType inputType;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getProteinferId() {
        return pinferId;
    }
    public void setProteinferId(int pinferId) {
        this.pinferId = pinferId;
    }
    
    public int getInputId() {
        return inputId;
    }
    public void setInputId(int inputId) {
        this.inputId = inputId;
    }
    
//    public InputType getInputType() {
//        return this.inputType;
//    }
//    
//    public void setInputType(InputType inputType) {
//        this.inputType = inputType;
//    }
    
    public static enum InputType {
        SEARCH('S'), ANALYSIS('A');
        
        private char shortName;
        
        private InputType(char shortName) {this.shortName = shortName;}
        
        public char getShortName() {return shortName;}
        
        public static InputType getInputTypeForChar(char shortName) {
            switch (shortName) {
                case 'S': return SEARCH;
                case 'A': return ANALYSIS;
                default:  return null;
            }
        }
    }
}
