/**
 * DatasetSource.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.dataset;

import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;

/**
 * 
 */
public enum DatasetSource {

    PROTINFER, DTA_SELECT, PROTEIN_PROPHET;
    
    public static DatasetSource instance(String name) {
        if(PROTINFER.name().equals(name))               return PROTINFER;
        else if(DTA_SELECT.name().equals(name))         return DTA_SELECT;
        else if(PROTEIN_PROPHET.name().equals(name))    return PROTEIN_PROPHET;
        return null;
    }
    
    public static DatasetSource getSourceForProtinferProgram(ProteinInferenceProgram program) {
        if(program == ProteinInferenceProgram.PROTEIN_PROPHET)
            return PROTEIN_PROPHET;
        else if(ProteinInferenceProgram.isIdPicker(program))
            return PROTINFER;
        else if(program == ProteinInferenceProgram.DTA_SELECT)
            return DTA_SELECT;
        else
            return null;
    }
    
    public boolean isIdPicker() {
    	return this.name().equals("PROTINFER");
    }
}
