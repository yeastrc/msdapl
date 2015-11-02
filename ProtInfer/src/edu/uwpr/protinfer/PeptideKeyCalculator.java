/**
 * PeptideKeyCalculator.java
 * @author Vagisha Sharma
 * Jan 20, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer;

import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinferIon;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

/**
 * 
 */
public class PeptideKeyCalculator {

    private PeptideKeyCalculator() {}
    
    public static String getKey(MsSearchResult result, PeptideDefinition peptideDef) throws ModifiedSequenceBuilderException {
        String key = null;
        if(peptideDef.isUseMods()) {
            key = result.getResultPeptide().getModifiedPeptide();
        }
        else {
            key = result.getResultPeptide().getPeptideSequence();
        }
        if(peptideDef.isUseCharge())
            key += "_"+result.getCharge();
        return key;
    }
    
    public static String getKey(ProteinferIon ion, PeptideDefinition peptideDef) {
        String key = ""+ion.getProteinferPeptideId();
        if(peptideDef.isUseMods()) {
            key += "_"+ ion.getModificationStateId();
        }
        if(peptideDef.isUseCharge())
            key += "_"+ion.getCharge();
        return key;
    }
}
