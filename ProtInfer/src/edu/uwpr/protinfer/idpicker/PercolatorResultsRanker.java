/**
 * PercolatorResultsRanker.java
 * @author Vagisha Sharma
 * Feb 8, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;

/**
 * 
 */
public class PercolatorResultsRanker {

    
    private static final PercolatorResultsRanker instance = new PercolatorResultsRanker();
    
    private PercolatorResultsRanker() {}
    
    public static final PercolatorResultsRanker instance() {
        return instance;
    }
    
    public Map<Integer, Integer> rankResultsByPeptide(List<PercolatorResult> resultList,
            boolean useQValue, boolean usePEP, boolean useDiscriminantScore) {
        
        if(usePEP && useDiscriminantScore)
            throw new IllegalArgumentException("Cannot use both PEP and DiscriminantScore to rank results");
        
//      final PeptideDefinition peptideDef = percParams.getIdPickerParams().getPeptideDefinition(); 
 
      // sort the results by the peptide sequence (w/o mods)
      Collections.sort(resultList, new Comparator<PercolatorResult>() {
          @Override
          public int compare(PercolatorResult o1, PercolatorResult o2) {
              //return PeptideKeyCalculator.getKey(o1, peptideDef).compareTo(PeptideKeyCalculator.getKey(o2, peptideDef));
              return o1.getResultPeptide().getPeptideSequence().compareTo(o2.getResultPeptide().getPeptideSequence());
          }});
      
      // which score comparator will we use
      Comparator<PercolatorResult> scoreComparator = null;
      // we have both qvalue and PEP cutoff
      if(useQValue && usePEP) {
              scoreComparator = new PercolatorResultComparatorPEPQVal();
      }
      else if(useQValue && useDiscriminantScore) {
          scoreComparator = new PercolatorResultComparatorDSQVal();
      }
      else if(usePEP && !useQValue) {
          scoreComparator = new PercolatorResultComparatorPEP();
      }
      else if(useDiscriminantScore && !useQValue) {
          scoreComparator = new PercolatorResultComparatorDS();
      }
      else {
          scoreComparator = new PercolatorResultComparatorQVal();
      }
      
      
      // Map of percolatorResultId and rank
      Map<Integer, Integer> resultRankMap = new HashMap<Integer, Integer>((int)(resultList.size()*1.5));
      
      List<PercolatorResult> resForPeptide = new ArrayList<PercolatorResult>();
      String lastPeptide = null;
      for(PercolatorResult result: resultList) {
          
          if(!result.getResultPeptide().getPeptideSequence().equals(lastPeptide)) {
              if(lastPeptide != null) {
                  Collections.sort(resForPeptide, scoreComparator);
                  int rank = 1;
                  for(PercolatorResult res: resForPeptide) {
                      resultRankMap.put(res.getId(), rank); rank++;
                  }
              }
              resForPeptide.clear();
              lastPeptide = result.getResultPeptide().getPeptideSequence();
          }
          resForPeptide.add(result);
      }
      // last one
      Collections.sort(resForPeptide, scoreComparator);
      int rank = 1;
      for(PercolatorResult res: resForPeptide) {
          resultRankMap.put(res.getId(), rank); rank++;
      }
      return resultRankMap;
  }
    
    private static final class PercolatorResultComparatorQVal implements Comparator<PercolatorResult> {
        @Override
        public int compare(PercolatorResult o1, PercolatorResult o2) {
            return Double.valueOf(o1.getQvalue()).compareTo(o2.getQvalue());
        }
    }
    
    private static final class PercolatorResultComparatorPEP implements Comparator<PercolatorResult> {
        @Override
        public int compare(PercolatorResult o1, PercolatorResult o2) {
            return Double.valueOf(o1.getPosteriorErrorProbability()).compareTo(o2.getPosteriorErrorProbability());
        }
    }
    
    private static final class PercolatorResultComparatorPEPQVal implements Comparator<PercolatorResult> {
        @Override
        public int compare(PercolatorResult o1, PercolatorResult o2) {
            int val = Double.valueOf(o1.getQvalue()).compareTo(o2.getQvalue());
            if(val != 0)    return val;
            return Double.valueOf(o1.getPosteriorErrorProbability()).compareTo(o2.getPosteriorErrorProbability());
        }
    }
    
    private static final class PercolatorResultComparatorDS implements Comparator<PercolatorResult> {
        @Override
        public int compare(PercolatorResult o1, PercolatorResult o2) {
            return Double.valueOf(o1.getDiscriminantScore()).compareTo(o2.getDiscriminantScore());
        }
    }
    
    private static final class PercolatorResultComparatorDSQVal implements Comparator<PercolatorResult> {
        @Override
        public int compare(PercolatorResult o1, PercolatorResult o2) {
            int val = Double.valueOf(o1.getQvalue()).compareTo(o2.getQvalue());
            if(val != 0)    return val;
            return Double.valueOf(o1.getDiscriminantScore()).compareTo(o2.getDiscriminantScore());
        }
    }
}
