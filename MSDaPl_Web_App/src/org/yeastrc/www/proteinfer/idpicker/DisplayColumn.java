/**
 * DisplayColumn.java
 * @author Vagisha Sharma
 * Sep 27, 2010
 */
package org.yeastrc.www.proteinfer.idpicker;

/**
 * 
 */
public class DisplayColumn {

	private char columnCode;
	private String columnName;
	private boolean disabled = false;
	private boolean selected = false;
	
	public DisplayColumn() {}
	
	public DisplayColumn(char columnCode, String columnName, boolean disabled, boolean selected) {
		this.columnCode = columnCode;
		this.columnName = columnName;
		this.disabled = disabled;
		this.selected = selected;
	}
	
	public char getColumnCode() {
		return columnCode;
	}
	public void setColumnCode(char columnCode) {
		this.columnCode = columnCode;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public String toString() {
		return this.columnCode+" "+this.columnName+" "+this.selected+" "+this.disabled;
	}
}
