/**
 * SequestResultFilterCriteria.java
 * @author Vagisha Sharma
 * Apr 7, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest;

import org.yeastrc.ms.domain.search.ResultFilterCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;

/**
 * 
 */
public class SequestResultFilterCriteria extends ResultFilterCriteria {

    
    private Integer maxXcorrRank;
    
    private Double minXCorr_1;
    private Double minXCorr_2;
    private Double minXCorr_3;
    private Double minXCorr_H;
    
    private Double minDeltaCN;
    
    private Double minSp;
    
    public boolean hasFilters() {
        if(super.hasFilters())
            return true;
        
        return (hasXcorrRankFilter() ||
                hasXCorrFilter() ||
                hasDeltaCnFilter() ||
                hasSpFilter());
    }
    
    

    public boolean superHasFilters() {
        return super.hasFilters();
    }
    
    
    //-------------------------------------------------------------
    // SP  FILTER
    //-------------------------------------------------------------
    public Double getMinSp() {
        return minSp;
    }

    public void setMinSp(Double sp) {
        this.minSp = sp;
    }
    
    public boolean hasSpFilter() {
        return minSp != null;
    }
    
    public String makeSpFilterSql() {
        return makeFilterSql(SORT_BY.SP.getColumnName(), minSp, null);
    }
    
    
    //-------------------------------------------------------------
    // DELTACN  FILTER
    //-------------------------------------------------------------
    public Double getMinDeltaCn() {
        return minDeltaCN;
    }

    public void setMinDeltaCn(Double deltaCn) {
        this.minDeltaCN = deltaCn;
    }
    
    public boolean hasDeltaCnFilter() {
        return minDeltaCN != null;
    }
    
    public String makeDeltaCnFilterSql() {
        return makeFilterSql(SORT_BY.DELTACN.getColumnName(), minDeltaCN, null);
    }
    
    //-------------------------------------------------------------
    // XCORR RANK  FILTER
    //-------------------------------------------------------------
    public Integer getMaxXcorrRank() {
        return maxXcorrRank;
    }
    
    public void setMaxXcorrRank(Integer xcorrRank) {
        this.maxXcorrRank = xcorrRank;
    }
    
    public boolean hasXcorrRankFilter() {
        return this.maxXcorrRank != null;
    }
    
    public String makeXCorrRankFilterSql() {
        if(!hasXcorrRankFilter())
            return "";
        StringBuilder buf = new StringBuilder();
        String xCorrRankCol = SORT_BY.XCORR_RANK.getColumnName();
        
        
        int xcorrRank = this.maxXcorrRank == null ? 1 : this.maxXcorrRank;
        
        buf.append(" ( ");
        buf.append(" (" +xCorrRankCol+" <= "+xcorrRank+") ");
        buf.append(" ) ");
        
        return buf.toString();
    }
    
    //-------------------------------------------------------------
    // XCORR  FILTER
    //-------------------------------------------------------------
    public Double getMinXCorr_1() {
        return minXCorr_1;
    }

    public void setMinXCorr_1(Double minXCorr_1) {
        this.minXCorr_1 = minXCorr_1;
    }
    
    public Double getMinXCorr_2() {
        return minXCorr_2;
    }

    public void setMinXCorr_2(Double minXCorr_2) {
        this.minXCorr_2 = minXCorr_2;
    }
    
    public Double getMinXCorr_3() {
        return minXCorr_3;
    }

    public void setMinXCorr_3(Double minXCorr_3) {
        this.minXCorr_3 = minXCorr_3;
    }
    
    public Double getMinXCorr_H() {
        return minXCorr_H;
    }

    public void setMinXCorr_H(Double minXCorr_H) {
        this.minXCorr_H = minXCorr_H;
    }
    

    public boolean hasXCorrFilter() {
      return (minXCorr_1 != null || minXCorr_2 != null || 
              minXCorr_3 != null || minXCorr_H != null);
    }
    
    public String makeXCorrFilterSql() {
        if(!hasXCorrFilter())
            return "";
        StringBuilder buf = new StringBuilder();
        String xCorrCol = SORT_BY.XCORR.getColumnName();
        String chgCol = SORT_BY.CHARGE.getColumnName();
        
        
        double x1 = minXCorr_1 == null ? 0 : minXCorr_1;
        double x2 = minXCorr_2 == null ? 0 : minXCorr_2;
        double x3 = minXCorr_3 == null ? 0 : minXCorr_3;
        double xh = minXCorr_H == null ? 0 : minXCorr_H;
        
        buf.append(" ( ");
        buf.append(" (" +chgCol+" = 1 AND "+xCorrCol+" >= "+x1+") ");
        buf.append(" OR ");
        buf.append(" (" +chgCol+" = 2 AND "+xCorrCol+" >= "+x2+") ");
        buf.append(" OR ");
        buf.append(" (" +chgCol+" = 3 AND "+xCorrCol+" >= "+x3+") ");
        buf.append(" OR ");
        buf.append(" (" +chgCol+" >= 4 AND "+xCorrCol+" >= "+xh+") ");
        buf.append(" ) ");
        
        return buf.toString();
    }
    
}
