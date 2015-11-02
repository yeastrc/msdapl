/**
 * Pageable.java
 * @author Vagisha Sharma
 * Apr 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.misc;

import java.util.List;

/**
 * 
 */
public interface Pageable {

    public abstract int getCurrentPage();
    
    public abstract int getLastPage();
    
    public abstract int getPageCount();
    
    public abstract int getNumPerPage();
    
    public abstract List<Integer> getDisplayPageNumbers();

    public abstract void setCurrentPage(int pageNum);

    public abstract void setLastPage(int pageCount);

    public abstract void setDisplayPageNumbers(List<Integer> pageList);
    
    public abstract void setNumPerPage(int num);
}
