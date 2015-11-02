/**
 * XtandemResult.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.xtandem.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.impl.SearchResult;
import org.yeastrc.ms.domain.search.xtandem.XtandemResultData;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchResultIn;

/**
 * 
 */
public class XtandemResult extends SearchResult implements XtandemSearchResultIn{

    
    private XtandemResultDataBean resultData;
    
    
    public XtandemResult() {
        super();
        resultData = new XtandemResultDataBean();
    }
    
    public void setNumMatchingIons(int numMatchingIons) {
       resultData.setMatchingIons(numMatchingIons);
    }

    public void setNumPredictedIons(int numPredictedIons) {
        resultData.setPredictedIons(numPredictedIons);
    }
    
    public void setCalculatedMass(BigDecimal mass) {
        resultData.setCalculatedMass(mass);
    }
    
    public void setHyperScore(BigDecimal hyperScore) {
        resultData.setHyperScore(hyperScore);
    }
    
    public void setNextScore(BigDecimal nextScore) {
        resultData.setNextScore(nextScore);
    }
    
    public void setBscore(BigDecimal bscore) {
        resultData.setBscore(bscore);
    }
    
    public void setYscore(BigDecimal yscore) {
        resultData.setYscore(yscore);
    }
    
    public void setExpect(BigDecimal expect) {
        resultData.setExpect(expect);
    }
    
    @Override
    public XtandemResultData getXtandemResultData() {
        return resultData;
    }
}
