/**
 * ProlucidResultsGetter.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

import edu.uwpr.protinfer.ProgramParam.SCORE;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class ProlucidResultsGetter extends SearchResultsGetter<ProlucidSearchResult> {

    private static final Logger log = Logger.getLogger(SequestResultsGetter.class);

    private static final ProlucidResultsGetter instance = new ProlucidResultsGetter();

    private ProlucidResultsGetter() {}

    public static ProlucidResultsGetter instance() {
        return instance;
    }


    PeptideSpectrumMatchIDP createPeptideSpectrumMatch(ProlucidSearchResult result, PeptideHit peptHit, SCORE scoreForFdr) 
        throws ModifiedSequenceBuilderException {

        ProlucidResultData scores = result.getProlucidResultData();

        SpectrumMatchIDPImpl specMatch = new SpectrumMatchIDPImpl();
        specMatch.setResultId(result.getId());
        specMatch.setSearchResultId(result.getId());
        specMatch.setScanId(result.getScanId());
        specMatch.setCharge(result.getCharge());
        specMatch.setSourceId(result.getRunSearchId());
        specMatch.setModifiedSequence(result.getResultPeptide().getModifiedPeptide());
//      specMatch.setRank(scores.getxCorrRank()); // Rank will be based on calculated FDR

        PeptideSpectrumMatchIDPImpl psm = new PeptideSpectrumMatchIDPImpl();
        psm.setPeptide(peptHit);
        psm.setSpectrumMatch(specMatch);
        if(scoreForFdr == SCORE.PrimaryScore)
            psm.setScore(scores.getPrimaryScore().doubleValue());
        else if(scoreForFdr == SCORE.DeltaCN)
            psm.setScore(scores.getDeltaCN().doubleValue());
        else
            throw new IllegalArgumentException("Unknow score type: "+scoreForFdr);
        return psm;
    }


    List<ProlucidSearchResult> getAllSearchResults(List<IdPickerInput> inputList, Program inputGenerator,  IDPickerParams params) {


        ProlucidSearchResultDAO resultDao = DAOFactory.instance().getProlucidResultDAO();

        List<ProlucidSearchResult> allResults = new ArrayList<ProlucidSearchResult>();

        for(IdPickerInput input: inputList) {

            int inputId = input.getInputId();
            log.info("Loading top hits for runSearchID: "+inputId);

            long start = System.currentTimeMillis();
            long s = start;
            List<ProlucidSearchResult> resultList = resultDao.loadTopResultsForRunSearchN(inputId, true); // get modifications
            log.info("\tTotal top hits for "+inputId+": "+resultList.size());
            long e = System.currentTimeMillis();

            log.info("\tTime: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");

            allResults.addAll(resultList);
        }

        return allResults;
    }

}
