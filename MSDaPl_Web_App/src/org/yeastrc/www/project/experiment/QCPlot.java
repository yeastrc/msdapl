/**
 * QCPlot.java
 * @author Vagisha Sharma
 * Jan 22, 2011
 */
package org.yeastrc.www.project.experiment;

/**
 * 
 */
public class QCPlot {

	private String plotUrl;
	private String plotTitle;
	
	public QCPlot(String plotUrl, String plotTitle) {
		this.plotUrl = plotUrl;
		this.plotTitle = plotTitle;
	}
	public String getPlotUrl() {
		return plotUrl;
	}
	public void setPlotUrl(String plotUrl) {
		this.plotUrl = plotUrl;
	}
	public String getPlotTitle() {
		return plotTitle;
	}
	public void setPlotTitle(String plotTitle) {
		this.plotTitle = plotTitle;
	}
}
