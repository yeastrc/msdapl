package org.yeastrc.qc_plots.dto;


/**
 * 
 * for table qc_plot_data
 */
public class QCPlotDataDTO {

	private int experimentId;
	private String plotType;
	private String plotData;
	private int scanCount;
	private int createTimeInSeconds;
	private int dataVersion;

	public int getExperimentId() {
		return experimentId;
	}
	public void setExperimentId(int experimentId) {
		this.experimentId = experimentId;
	}
	public String getPlotType() {
		return plotType;
	}
	public void setPlotType(String plotType) {
		this.plotType = plotType;
	}
	public String getPlotData() {
		return plotData;
	}
	public void setPlotData(String plotData) {
		this.plotData = plotData;
	}
	public int getScanCount() {
		return scanCount;
	}
	public void setScanCount(int scanCount) {
		this.scanCount = scanCount;
	}
	public int getCreateTimeInSeconds() {
		return createTimeInSeconds;
	}
	public void setCreateTimeInSeconds(int createTimeInSeconds) {
		this.createTimeInSeconds = createTimeInSeconds;
	}	
	public int getDataVersion() {
		return dataVersion;
	}
	public void setDataVersion(int dataVersion) {
		this.dataVersion = dataVersion;
	}
}
