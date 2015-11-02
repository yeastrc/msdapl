/**
 * ResultsSortCriteria.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;


/**
 * 
 */
public class ResultSortCriteria {

    private final SORT_BY sortBy;
    private final SORT_ORDER sortOrder;
    private Integer limitCount;
    private Integer offset;
    
    public Integer getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public ResultSortCriteria(SORT_BY sortBy, SORT_ORDER sortOrder) {
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }
    
    public SORT_BY getSortBy() {
        return sortBy;
    }
    
    public SORT_ORDER getSortOrder() {
        return sortOrder;
    }
    
}
