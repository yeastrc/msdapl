/**
 * SelectableModificationBean.java
 * @author Vagisha Sharma
 * Sep 30, 2010
 */
package org.yeastrc.www.project.experiment;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.yeastrc.ms.domain.search.impl.ResidueModificationBean;

/**
 * 
 */
public class SelectableModificationBean extends ResidueModificationBean {

	private boolean selected = false;

	private static final DecimalFormat format = new DecimalFormat("0.00");
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public String getModificationMassString() {
		double mass = super.getModificationMass().doubleValue();
		return format.format(mass);
	}
}
