/**
 * ResultsGetter.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.List;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

/**
 * 
 */
public interface ResultsGetter {
    
    public abstract List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(IdPickerRun idpRun, IDPickerParams params) 
        throws ResultGetterException;
    
    public abstract List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(List<IdPickerInput> inputList,
                                Program inputGenerator,
                                IDPickerParams params) throws ResultGetterException;

    public abstract List<PeptideSpectrumMatchIDP> getResults(IdPickerRun idpRun, IDPickerParams params) 
        throws ModifiedSequenceBuilderException;
    
    public abstract List<PeptideSpectrumMatchIDP> getResults(List<IdPickerInput> inputList, Program inputGenerator, IDPickerParams params) 
        throws ModifiedSequenceBuilderException;
}
