/**
 * XtandemResultFilterCriteria.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.xtandem;

import org.yeastrc.ms.domain.search.ResultFilterCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;

/**
 * 
 */
public class XtandemResultFilterCriteria extends ResultFilterCriteria {

    
    private Integer maxRank;
    
    private Double minHyperScore;
    private Double minNextScore;
    private Double minBscore;
    private Double minYscore;
    private Double minExpect;
    
    
    public boolean hasFilters() {
        if(super.hasFilters())
            return true;
        
        return (hasRankFilter() ||
                hasHyperScoreFilter() ||
                hasNextScoreFilter() ||
                hasBscoreFilter() ||
                hasYscoreFilter() ||
                hasExpectFilter());
    }
    
    public boolean superHasFilters() {
        return super.hasFilters();
    }
    
    
    //-------------------------------------------------------------
    // RANK FILTER
    //-------------------------------------------------------------
    public Integer getRank() {
        return maxRank;
    }

    public void setRank(Integer rank) {
        this.maxRank = rank;
    }
    
    public boolean hasRankFilter() {
        return maxRank != null;
    }
    
    public String makeRankFilterSql() {
        
        if(!hasRankFilter())
            return "";
        StringBuilder buf = new StringBuilder();
        String rankColumn = SORT_BY.XTANDEM_RANK.getColumnName();
        
        int rank = this.maxRank == null ? 1 : this.maxRank;
        
        buf.append(" ( ");
        buf.append(" (" +rankColumn+" <= "+rank+") ");
        buf.append(" ) ");
        
        return buf.toString();
        
    }    
    
    //-------------------------------------------------------------
    // HYPER_SCORE  FILTER
    //-------------------------------------------------------------
    public Double getMinHyperScore() {
        return minHyperScore;
    }

    public void setMinHyperScore(Double hyperScore) {
        this.minHyperScore = hyperScore;
    }
    
    public boolean hasHyperScoreFilter() {
        return minHyperScore != null;
    }
    
    public String makeHyperScoreFilterSql() {
        return makeFilterSql(SORT_BY.HYPER_SCORE.getColumnName(), minHyperScore, null);
    }
    
    //-------------------------------------------------------------
    // NEXT_SCORE  FILTER
    //-------------------------------------------------------------
    public Double getMinNextScore() {
        return minNextScore;
    }
    
    public void setMinNextScore(Double nextScore) {
        this.minNextScore = nextScore;
    }
    
    public boolean hasNextScoreFilter() {
        return this.minNextScore != null;
    }
    
    public String makeNextScoreFilterSql() {
        return makeFilterSql(SORT_BY.NEXT_SCORE.getColumnName(), minNextScore, null);
    }
    
    //-------------------------------------------------------------
    // B_SCORE  FILTER
    //-------------------------------------------------------------
    public Double getMinBscore() {
        return minBscore;
    }
    
    public void setMinBscore(Double bscore) {
        this.minBscore = bscore;
    }
    
    public boolean hasBscoreFilter() {
        return this.minBscore != null;
    }
    
    public String makeBscoreFilterSql() {
        return makeFilterSql(SORT_BY.B_SCORE.getColumnName(), minBscore, null);
    }
    
    //-------------------------------------------------------------
    // Y_SCORE  FILTER
    //-------------------------------------------------------------
    public Double getMinYscore() {
        return minYscore;
    }
    
    public void setMinYscore(Double yscore) {
        this.minYscore = yscore;
    }
    
    public boolean hasYscoreFilter() {
        return this.minYscore != null;
    }
    
    public String makeYscoreFilterSql() {
        return makeFilterSql(SORT_BY.Y_SCORE.getColumnName(), minYscore, null);
    }
    
    //-------------------------------------------------------------
    // EXPECT  FILTER
    //-------------------------------------------------------------
    public Double getMinExpect() {
        return minExpect;
    }
    
    public void setMinExpectScore(Double expect) {
        this.minExpect = expect;
    }
    
    public boolean hasExpectFilter() {
        return this.minExpect != null;
    }
    
    public String makeExpectFilterSql() {
        return makeFilterSql(SORT_BY.XTANDEM_EXPECT.getColumnName(), minExpect, null);
    }
}
