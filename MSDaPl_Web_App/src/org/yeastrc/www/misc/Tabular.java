/**
 * Tabular.java
 * @author Vagisha Sharma
 * Apr 4, 2009
 * @version 1.0
 */
package org.yeastrc.www.misc;

import java.util.List;

/**
 * 
 */
public interface Tabular {

    /**
     * Any pre-processing should be done in this method.
     * This method should be called before any or the other methods.
     */
    public abstract void tabulate();
    
    public abstract int columnCount();
    
    public abstract int rowCount();
    
    public abstract List<TableHeader> tableHeaders();
    
    public abstract TableRow getRow(int row);
}
