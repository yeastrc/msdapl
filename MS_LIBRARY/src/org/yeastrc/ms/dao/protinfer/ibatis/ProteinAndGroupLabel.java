/**
 * ProteinAndGroupLabel.java
 * @author Vagisha Sharma
 * Mar 21, 2010
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.ibatis;

/**
 * 
 */
public final class ProteinAndGroupLabel {

	private int proteinId;
    private int proteinGroupLabel;
    
    public void setProteinId(int proteinId) {
        this.proteinId = proteinId;
    }
    
    public void setGroupLabel(int proteinGroupLabel) {
        this.proteinGroupLabel = proteinGroupLabel;
    }

	public int getProteinId() {
		return proteinId;
	}

	public int getGroupLabel() {
		return proteinGroupLabel;
	}
}
