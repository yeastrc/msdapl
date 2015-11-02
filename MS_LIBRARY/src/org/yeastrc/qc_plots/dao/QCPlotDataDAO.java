package org.yeastrc.qc_plots.dao;

import org.yeastrc.qc_plots.dto.QCPlotDataDTO;


/**
 * 
 * for table qc_plot_data
 */
public interface QCPlotDataDAO {

    /**
     * Save the data or update an existing record with the same experiment id and plot type (primary key)
     * @param qcPlotDataDTO
     * @return
     */
    public int saveOrUpdate(QCPlotDataDTO qcPlotDataDTO);
    
    /**
     * @param experimentId
     * @param plotType
     * @return
     */
    public QCPlotDataDTO load(int experimentId, String plotType);
    
    /**
     * @param experimentId
     * @param plotType
     * @param dataVersion
     * @return
     */
    public QCPlotDataDTO loadFromExperimentIdPlotTypeAndDataVersion(int experimentId, String plotType, int dataVersion);
    

	/**
	 * Delete the records for this experiment Id and this plotType
	 * @param experimentId
     * @param plotType
	 */
	public void deleteForExperimentIdPlotType(	int experimentId, String plotType );
}