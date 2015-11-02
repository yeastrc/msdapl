/**
 * ProphetProteinProperties.java
 * @author Vagisha Sharma
 * Mar 30, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import org.yeastrc.www.proteinfer.ProteinProperties;

/**
 * 
 */
public class ProphetProteinProperties extends ProteinProperties {

	private int proteinProphetGroupId;
	
	public ProphetProteinProperties(int pinferProteinId) {
        super(pinferProteinId);
    }
	
	public ProphetProteinProperties (ProteinProperties props) {
		super(props.getPinferProteinId());
		this.setAccession(props.getAccessions());
		this.setMolecularWt(props.getMolecularWt());
		this.setNrseqId(props.getNrseqId());
		this.setPi(props.getPi());
		this.setProteinGroupId(props.getProteinGroupId());
	}

	public int getProteinProphetGroupId() {
		return proteinProphetGroupId;
	}

	public void setProteinProphetGroupId(int proteinProphetGroupId) {
		this.proteinProphetGroupId = proteinProphetGroupId;
	}
}
