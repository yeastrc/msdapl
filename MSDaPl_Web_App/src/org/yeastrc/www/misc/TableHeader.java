/**
 * TableHeader.java
 * @author Vagisha Sharma
 * Apr 7, 2009
 * @version 1.0
 */
package org.yeastrc.www.misc;

import java.util.HashMap;
import java.util.Map;

import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.www.project.SORT_CLASS;

/**
 * 
 */
public class TableHeader {

    private String headerName;
    private String headerId;
    private boolean sortable = true;
    private boolean isSorted;
    private SORT_ORDER sortOrder = SORT_ORDER.ASC;
    private SORT_ORDER defaultSortOrder = SORT_ORDER.ASC;
    private int width;
    private int colspan = 0;
    private int rowspan = 0;
    private int rowIndex = 1;
    private String title;

	private SORT_CLASS sortClass = SORT_CLASS.SORT_ALPHA;
    private String styleClass = null;
    private Map<String, String> styles;
    
    public SORT_CLASS getSortClass() {
        return sortClass;
    }

    public void setSortClass(SORT_CLASS sortClass) {
        this.sortClass = sortClass;
    }

    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }
    
    public int getRowspan() {
        return rowspan;
    }

    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }
    
    public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
    
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public TableHeader() {}
    
    public TableHeader(String headerName) {
        this(headerName, null);
    }
    
    public TableHeader(String headerName, String headerId) {
        this.headerName = headerName;
        this.headerId = headerId;
    }
    
    public String getHeaderName() {
        return headerName;
    }
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
    public String getHeaderId() {
        return headerId;
    }
    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }
    
    public boolean isSorted() {
        return isSorted;
    }
    
    public void setSorted(boolean sorted) {
        this.isSorted = sorted;
    }
    
    public SORT_ORDER getSortOrder() {
        return this.sortOrder;
    }
    
    public void setSortOrder(SORT_ORDER sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SORT_ORDER getDefaultSortOrder() {
    	return this.defaultSortOrder;
    }
    
    public void setDefaultSortOrder(SORT_ORDER sortOrder) {
    	this.defaultSortOrder = sortOrder;
    }
    
    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    
    public void addStyle(String key, String value) {
    	if(this.styles == null)
    		styles = new HashMap<String, String>();
    	styles.put(key, value);
    }
    
    public String getStyleString() {
    	if(this.styles == null) {
    		return null;
    	}
    	else {
    		StringBuilder buf = new StringBuilder();
    		for(String key: styles.keySet()) {
    			buf.append(key+":");
    			buf.append(styles.get(key));
    			buf.append(";");
    		}
    		return buf.toString();
    	}
    }
    
    public void setTitle(String title) {
    	this.title = title;
    }
    
    public String getTitle() {
    	return title;
    }
    
}
