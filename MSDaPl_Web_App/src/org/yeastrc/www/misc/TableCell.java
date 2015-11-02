/**
 * TableCell.java
 * @author Vagisha Sharma
 * Apr 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.misc;

import java.util.ArrayList;
import java.util.List;

public class TableCell {

	private List<Data> dataList;
	
    private String className = null;
    private String id = null;
    private String name = null;
    private int rowSpan = 0;
    private String backgroundColor = null;
    private String textColor = "0 0 0"; // black
    
    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TableCell(){
    	dataList = new ArrayList<Data>();
    }
    
    public TableCell(String data) {
    	this();
        this.dataList.add(new Data(data));
    }
    
    /**
     * If the full url is http://localhost:8080/viewProject.do?ID=20
     * the value of the <code>hyperlink</code> parameter should be
     * viewProject.do?ID=20
     * @param data
     * @param hyperlink
     */
    public TableCell(String data, String hyperlink) {
        this(data, hyperlink, false);
    }
    
    public TableCell(String data, String hyperlink, boolean newWindow) {
        this(data, hyperlink, false, newWindow);
    }
    
    public TableCell(String data, String hyperlink, boolean absoluteLink, boolean newWindow) {
    	this();
    	HyperlinkedData ldata = new HyperlinkedData(data);
    	ldata.setHyperlink(hyperlink, absoluteLink, newWindow);
    	this.dataList.add(ldata);
    }
    
    public List<Data> getDataList() {
        return dataList;
    }
    
    public void setData(String data) {
    	if(this.dataList.size() > 0)
    		this.dataList.clear();
        this.dataList.add(new Data(data));
    }
    
    public void addData(String data) {
    	this.dataList.add(new Data(data));
    }
    
    public void addData(Data data) {
    	this.dataList.add(data);
    }

    public void setBackgroundColor(String color) {
        this.backgroundColor = color;
    }
    
    public String getBackgroundColor() {
        return this.backgroundColor;
    }
    
    public void setTextColor(String color) {
        this.textColor = color;
    }
    
    public String getTextColor() {
        return this.textColor;
    }
    
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
