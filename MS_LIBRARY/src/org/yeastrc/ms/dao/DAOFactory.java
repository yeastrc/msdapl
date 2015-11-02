package org.yeastrc.ms.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.PeptideTerminiStatsDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetResultDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetRocDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.ProphetFilteredPsmResultDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.ProphetFilteredSpectraResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorFilteredPsmResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorFilteredSpectraResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorParamsDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.general.MsEnzymeDAO;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.dao.general.MsInstrumentDAO;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2HeaderDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanChargeDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.mascot.MascotSearchDAO;
import org.yeastrc.ms.dao.search.mascot.MascotSearchResultDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTHeaderDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.search.xtandem.XtandemSearchDAO;
import org.yeastrc.ms.dao.search.xtandem.XtandemSearchResultDAO;
import org.yeastrc.ms.service.MsDataUploadProperties;

public class DAOFactory {

    private static final Logger log = Logger.getLogger(DAOFactory.class);
    
    private static boolean useIbatisDAO = true;
    
    private static DAOFactory instance = new DAOFactory();
    
    private static IbatisDAOFactory ibatisDaoFactory;
    
    private DAOFactory() {
       useIbatisDAO = MsDataUploadProperties.useIbatisDAO();
       if(useIbatisDAO)
    	   ibatisDaoFactory = IbatisDAOFactory.getInstance();
    }
    
    public static DAOFactory instance() {
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
    	if(useIbatisDAO)
    		return ibatisDaoFactory.getConnection();
    	else
    		return ConnectionFactory.getMsDataConnection();
    }
    //-------------------------------------------------------------------------------------------
    // EXPERIMENT related
    //-------------------------------------------------------------------------------------------
    public MsExperimentDAO getMsExperimentDAO() {
        return ibatisDaoFactory.getMsExperimentDAO();
    }
    
    
    //-------------------------------------------------------------------------------------------
    // ENZYME related
    //-------------------------------------------------------------------------------------------
    public MsEnzymeDAO getEnzymeDAO() {
        return ibatisDaoFactory.getEnzymeDAO();
    }
    
    //-------------------------------------------------------------------------------------------
    // INSTRUMENT related
    //-------------------------------------------------------------------------------------------
    public MsInstrumentDAO getInstrumentDAO() {
        return ibatisDaoFactory.getInstrumentDAO();
    }
    
    //-------------------------------------------------------------------------------------------
    // RUN related
    //-------------------------------------------------------------------------------------------
    public MsRunDAO getMsRunDAO() {
        return ibatisDaoFactory.getMsRunDAO();
    }
    
    public MsScanDAO getMsScanDAO() {
        return ibatisDaoFactory.getMsScanDAO();
    }
    
    //-------------------------------------------------------------------------------------------
    // MS2 RUN related
    //-------------------------------------------------------------------------------------------
    public MS2RunDAO getMS2FileRunDAO() {
        return ibatisDaoFactory.getMS2FileRunDAO();
    }
    
    public MS2ScanDAO getMS2FileScanDAO() {
        return ibatisDaoFactory.getMS2FileScanDAO();
    }
    
    public MS2ScanChargeDAO getMS2FileScanChargeDAO() {
        return ibatisDaoFactory.getMS2FileScanChargeDAO();
    }
    
    public MS2HeaderDAO getMS2FileRunHeadersDAO() {
        return ibatisDaoFactory.getMS2FileRunHeadersDAO();
    }
    
    public MS2ChargeDependentAnalysisDAO getMs2FileChargeDAnalysisDAO() {
        return ibatisDaoFactory.getMs2FileChargeDAnalysisDAO();
    }
    
    public MS2ChargeIndependentAnalysisDAO getMs2FileChargeIAnalysisDAO() {
        return ibatisDaoFactory.getMs2FileChargeIAnalysisDAO();
    }
    
    //-------------------------------------------------------------------------------------------
    // SEARCH related
    //-------------------------------------------------------------------------------------------
    public MsSearchDAO getMsSearchDAO() {
        return ibatisDaoFactory.getMsSearchDAO();
    }
    
    public MsRunSearchDAO getMsRunSearchDAO() {
        return ibatisDaoFactory.getMsRunSearchDAO();
    }
    
    public MsSearchResultDAO getMsSearchResultDAO() {
        return ibatisDaoFactory.getMsSearchResultDAO();
    }
    
    public MsSearchResultProteinDAO getMsProteinMatchDAO() {
        return ibatisDaoFactory.getMsProteinMatchDAO();
    }
    
    public MsSearchModificationDAO getMsSearchModDAO() {
        return ibatisDaoFactory.getMsSearchModDAO();
    }
    
    public MsSearchDatabaseDAO getMsSequenceDatabaseDAO() {
        return ibatisDaoFactory.getMsSequenceDatabaseDAO();
    }
    
    //-------------------------------------------------------------------------------------------
    // SQT file related
    //-------------------------------------------------------------------------------------------
    public SQTHeaderDAO getSqtHeaderDAO() {
        return ibatisDaoFactory.getSqtHeaderDAO();
    }
    
    public SQTRunSearchDAO getSqtRunSearchDAO() {
        return ibatisDaoFactory.getSqtRunSearchDAO();
    }
    
    public SQTSearchScanDAO getSqtSpectrumDAO() {
        return ibatisDaoFactory.getSqtSpectrumDAO();
    }
    
    //-------------------------------------------------------------------------------------------
    // Sequest SEARCH related
    //-------------------------------------------------------------------------------------------
    public SequestSearchResultDAO getSequestResultDAO() {
        return ibatisDaoFactory.getSequestResultDAO();
    }
    
    public SequestSearchDAO getSequestSearchDAO() {
        return ibatisDaoFactory.getSequestSearchDAO();
    }
    
    //-------------------------------------------------------------------------------------------
    // Mascot SEARCH related
    //-------------------------------------------------------------------------------------------
    public MascotSearchResultDAO getMascotResultDAO() {
        return ibatisDaoFactory.getMascotResultDAO();
    }
    
    public MascotSearchDAO getMascotSearchDAO() {
        return ibatisDaoFactory.getMascotSearchDAO();
    }
    
    //-------------------------------------------------------------------------------------------
    // Xtandem SEARCH related
    //-------------------------------------------------------------------------------------------
    public XtandemSearchResultDAO getXtandemResultDAO() {
        return ibatisDaoFactory.getXtandemResultDAO();
    }
    
    public XtandemSearchDAO getXtandemSearchDAO() {
        return ibatisDaoFactory.getXtandemSearchDAO();
    }
    
    //-------------------------------------------------------------------------------------------
    // ProLuCID SEARCH related
    //-------------------------------------------------------------------------------------------
    public ProlucidSearchResultDAO getProlucidResultDAO() {
        return ibatisDaoFactory.getProlucidResultDAO();
    }
    
    public ProlucidSearchDAO getProlucidSearchDAO() {
        return ibatisDaoFactory.getProlucidSearchDAO();
    }
    
    //-------------------------------------------------------------------------------------------
    // Post search analysis related
    //-------------------------------------------------------------------------------------------
    public MsSearchAnalysisDAO getMsSearchAnalysisDAO() {
        return ibatisDaoFactory.getMsSearchAnalysisDAO();
    }
    
    public MsRunSearchAnalysisDAO getMsRunSearchAnalysisDAO(){
        return ibatisDaoFactory.getMsRunSearchAnalysisDAO();
    }
    
    //-------------------------------------------------------------------------------------------
    // Percolator related
    //-------------------------------------------------------------------------------------------
    public PercolatorParamsDAO getPercoltorParamsDAO() {
        return ibatisDaoFactory.getPercoltorParamsDAO();
    }
    
    public PercolatorResultDAO getPercolatorResultDAO() {
        return ibatisDaoFactory.getPercolatorResultDAO();
    }
    
    public PercolatorPeptideResultDAO getPercolatorPeptideResultDAO() {
    	return ibatisDaoFactory.getPercolatorPeptideResultDAO();
    }
    
    
    //-------------------------------------------------------------------------------------------
    // PeptideProphet related
    //-------------------------------------------------------------------------------------------
    public PeptideProphetResultDAO getPeptideProphetResultDAO() {
        return ibatisDaoFactory.getPeptideProphetResultDAO();
    }
    
    public PeptideProphetRocDAO getPeptideProphetRocDAO() {
        return ibatisDaoFactory.getPeptideProphetRocDAO();
    }
    
    //-------------------------------------------------------------------------------------------
    // stats related
    //-------------------------------------------------------------------------------------------
    public PeptideTerminiStatsDAO getPeptideTerminiStatsDAO() {
    	return ibatisDaoFactory.getPeptideTerminiStatsDAO();
    }
    
    public PercolatorFilteredPsmResultDAO getPrecolatorFilteredPsmResultDAO() {
    	return ibatisDaoFactory.getPrecolatorFilteredPsmResultDAO();
    }
    
    public PercolatorFilteredSpectraResultDAO getPrecolatorFilteredSpectraResultDAO() {
    	return ibatisDaoFactory.getPrecolatorFilteredSpectraResultDAO();
    }
    
    public ProphetFilteredPsmResultDAO getProphetFilteredPsmResultDAO() {
    	return ibatisDaoFactory.getProphetFilteredPsmResultDAO();
    }
    
    public ProphetFilteredSpectraResultDAO getProphetFilteredSpectraResultDAO() {
    	return ibatisDaoFactory.getProphetFilteredSpectraResultDAO();
    }
}
