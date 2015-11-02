/**
 * SequestResultsGetter.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

import edu.uwpr.protinfer.ProgramParam.SCORE;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class SequestResultsGetter extends SearchResultsGetter<SequestSearchResult> {

    private static final Logger log = Logger.getLogger(SequestResultsGetter.class);
    
    private static final SequestResultsGetter instance = new SequestResultsGetter();
    
    private SequestResultsGetter() {}
    
    public static SequestResultsGetter instance() {
        return instance;
    }
    

    PeptideSpectrumMatchIDP createPeptideSpectrumMatch(SequestSearchResult result, PeptideHit peptHit, SCORE scoreForFdr) 
        throws ModifiedSequenceBuilderException {
        
        SequestResultData scores = result.getSequestResultData();
        
        SpectrumMatchIDPImpl specMatch = new SpectrumMatchIDPImpl();
        specMatch.setResultId(result.getId());
        specMatch.setSearchResultId(result.getId());
        specMatch.setScanId(result.getScanId());
        specMatch.setCharge(result.getCharge());
        specMatch.setSourceId(result.getRunSearchId());
        specMatch.setModifiedSequence(result.getResultPeptide().getModifiedPeptide());
//            specMatch.setRank(scores.getxCorrRank()); // Rank will be based on calculated FDR
        
        PeptideSpectrumMatchIDPImpl psm = new PeptideSpectrumMatchIDPImpl();
        psm.setPeptide(peptHit);
        psm.setSpectrumMatch(specMatch);
        if(scoreForFdr == SCORE.XCorr)
            psm.setScore(scores.getxCorr().doubleValue());
        else if(scoreForFdr == SCORE.DeltaCN)
            psm.setScore(scores.getDeltaCN().doubleValue());
        else
            throw new IllegalArgumentException("Unknow score type: "+scoreForFdr);
        return psm;
    }
    
    
    List<SequestSearchResult> getAllSearchResults(List<IdPickerInput> inputList, Program inputGenerator,  IDPickerParams params) {
        
        
        SequestSearchResultDAO resultDao = DAOFactory.instance().getSequestResultDAO();
        
        List<SequestSearchResult> allResults = new ArrayList<SequestSearchResult>();
        
        for(IdPickerInput input: inputList) {
            
            int inputId = input.getInputId();
            log.info("Loading top hits for runSearchID: "+inputId);
            
            long start = System.currentTimeMillis();
            long s = start;
            List<SequestSearchResult> resultList = resultDao.loadTopResultsForRunSearchN(inputId, true); // get modifications
            log.info("\tTotal top hits for "+inputId+": "+resultList.size());
            long e = System.currentTimeMillis();
            
            log.info("\tTime: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
            
            allResults.addAll(resultList);
        }
        
        return allResults;
    }

}
