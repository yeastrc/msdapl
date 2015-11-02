package org.yeastrc.ms.dao;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.PeptideTerminiStatsDAO;
import org.yeastrc.ms.dao.analysis.ibatis.MsRunSearchAnalysisDAOImpl;
import org.yeastrc.ms.dao.analysis.ibatis.MsSearchAnalysisDAOImpl;
import org.yeastrc.ms.dao.analysis.ibatis.PeptideTerminiStatsDAOImpl;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetResultDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetRocDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.ProphetFilteredPsmResultDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.ProphetFilteredSpectraResultDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.ibatis.PeptideProphetResultDAOImpl;
import org.yeastrc.ms.dao.analysis.peptideProphet.ibatis.PeptideProphetRocDAOImpl;
import org.yeastrc.ms.dao.analysis.peptideProphet.ibatis.ProphetFilteredPsmResultDAOImpl;
import org.yeastrc.ms.dao.analysis.peptideProphet.ibatis.ProphetFilteredSpectraResultDAOImpl;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorFilteredPsmResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorFilteredSpectraResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorParamsDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.ibatis.PercolatorFilteredPsmResultDAOImpl;
import org.yeastrc.ms.dao.analysis.percolator.ibatis.PercolatorFilteredSpectraResultDAOImpl;
import org.yeastrc.ms.dao.analysis.percolator.ibatis.PercolatorParamsDAOImpl;
import org.yeastrc.ms.dao.analysis.percolator.ibatis.PercolatorPeptideResultDAOImpl;
import org.yeastrc.ms.dao.analysis.percolator.ibatis.PercolatorResultDAOImpl;
import org.yeastrc.ms.dao.general.MsEnzymeDAO;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.dao.general.MsInstrumentDAO;
import org.yeastrc.ms.dao.general.ibatis.MsEnzymeDAOImpl;
import org.yeastrc.ms.dao.general.ibatis.MsExperimentDAOImpl;
import org.yeastrc.ms.dao.general.ibatis.MsInstrumentDAOImpl;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ibatis.MsRunDAOImpl;
import org.yeastrc.ms.dao.run.ibatis.MsScanDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2HeaderDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanChargeDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.dao.run.ms2file.ibatis.MS2ChargeDependentAnalysisDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.ibatis.MS2ChargeIndependentAnalysisDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.ibatis.MS2HeaderDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.ibatis.MS2RunDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.ibatis.MS2ScanChargeDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.ibatis.MS2ScanDAOImpl;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.ibatis.MsRunSearchDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchDatabaseDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchModificationDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchResultDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchResultProteinDAOImpl;
import org.yeastrc.ms.dao.search.mascot.MascotSearchDAO;
import org.yeastrc.ms.dao.search.mascot.MascotSearchResultDAO;
import org.yeastrc.ms.dao.search.mascot.ibatis.MascotSearchDAOImpl;
import org.yeastrc.ms.dao.search.mascot.ibatis.MascotSearchResultDAOImpl;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.dao.search.prolucid.ibatis.ProlucidSearchDAOImpl;
import org.yeastrc.ms.dao.search.prolucid.ibatis.ProlucidSearchResultDAOImpl;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.ibatis.SequestSearchDAOImpl;
import org.yeastrc.ms.dao.search.sequest.ibatis.SequestSearchResultDAOImpl;
import org.yeastrc.ms.dao.search.sqtfile.SQTHeaderDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.search.sqtfile.ibatis.SQTHeaderDAOImpl;
import org.yeastrc.ms.dao.search.sqtfile.ibatis.SQTRunSearchDAOImpl;
import org.yeastrc.ms.dao.search.sqtfile.ibatis.SQTSearchScanDAOImpl;
import org.yeastrc.ms.dao.search.xtandem.XtandemSearchDAO;
import org.yeastrc.ms.dao.search.xtandem.XtandemSearchResultDAO;
import org.yeastrc.ms.dao.search.xtandem.ibatis.XtandemSearchDAOImpl;
import org.yeastrc.ms.dao.search.xtandem.ibatis.XtandemSearchResultDAOImpl;
import org.yeastrc.ms.service.MsDataUploadProperties;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class IbatisDAOFactory {

    private static final Logger log = Logger.getLogger(IbatisDAOFactory.class);
    
    
    // initialize the SqlMapClient
    private static SqlMapClient sqlMap;
    
    static {
        Reader reader = null;
        String ibatisConfigFile = "SqlMapConfig.xml";
        try {
            reader = Resources.getResourceAsReader(ibatisConfigFile);
            sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
        }
        catch (IOException e) {
            log.error("Error reading Ibatis config xml: "+ibatisConfigFile, e);
            throw new RuntimeException("Error reading Ibatis config xml: "+ibatisConfigFile, e);
        }
        catch (Exception e) {
            log.error("Error initializing "+IbatisDAOFactory.class.getName()+" class: ", e);
            throw new RuntimeException("Error initializing "+IbatisDAOFactory.class.getName()+" class: ", e);
        }
        finally {
        	if(reader != null) try {reader.close();} catch(IOException e){}
        }
        System.out.println("Loaded Ibatis SQL map config -- "+ibatisConfigFile);
    }
    
    private static IbatisDAOFactory instance = new IbatisDAOFactory();
    
    // DAO for Experiment
    private MsExperimentDAO experimentDAO;
    
    // DAOs for enzyme related objects
    private MsEnzymeDAO enzymeDAO;
    
    // DAOs for Instrument related objects
    private MsInstrumentDAO instrumentDAO;
    
    // DAOs for run related objects
    private MsRunDAO runDAO;
    private MsScanDAO scanDAO;
    
    // DAOs related to MS2 files. 
    private MS2RunDAO ms2RunDAO;
    private MS2ScanDAO ms2ScanDAO;
    private MS2ScanChargeDAO ms2FileScanChargeDAO;
    private MS2HeaderDAO ms2FileHeadersDAO;
    private MS2ChargeDependentAnalysisDAO ms2ChgDAnalysisDAO;
    private MS2ChargeIndependentAnalysisDAO ms2ChgIAnalysisDAO;
    
    // DAOs for search related objects
    private MsSearchDAO searchDAO;
    private MsRunSearchDAO runSearchDAO;
    private MsSearchResultDAO searchResultDAO;
    private MsSearchResultProteinDAO resultProteinDAO;
    private MsSearchModificationDAO modDAO;
    private MsSearchDatabaseDAO seqDbDao;
    
    // DAOs for SQT file related objects
    private SQTSearchScanDAO sqtSpectrumDAO;
    private SQTHeaderDAO sqtHeaderDAO;
    private SQTRunSearchDAO sqtRunSearchDAO;
    
    // DAOs for Sequest related objects
    private SequestSearchResultDAO sequestResultDAO;
    private SequestSearchDAO sequestSearchDAO;
    
    // DAOs for Mascot related objects
    private MascotSearchResultDAO mascotResultDAO;
    private MascotSearchDAO mascotSearchDAO;
    
    // DAOs for Xtandem related objects
    private XtandemSearchResultDAO xtandemResultDAO;
    private XtandemSearchDAO xtandemSearchDAO;
    
    // DAOs for Prolucid related objects
    private ProlucidSearchResultDAO prolucidResultDAO;
    private ProlucidSearchDAO prolucidSearchDAO;
    
    // DAOs related to post search analysis
    private MsSearchAnalysisDAO analysisDAO;
    private MsRunSearchAnalysisDAO rsAnalysisDAO;
    
    // DAOs related to Percolator post search analysis
    private PercolatorParamsDAO percSQTHeaderDAO;
    private PercolatorResultDAO percResultDAO;
    private PercolatorPeptideResultDAO percPeptResultDAO;
    
    // DAOs related to PeptideProphet search analysis
    private PeptideProphetRocDAO ppRocDAO;
    private PeptideProphetResultDAO pprophResultDAO;
    
    // DAOs for stats tables (Percolator)
    private PeptideTerminiStatsDAO peptideTerminiStatsDAO;
    private PercolatorFilteredPsmResultDAO percFiltersPsmDAO;
    private PercolatorFilteredSpectraResultDAO percFiltersSpectraDAO;
    
    // DAOs for stats tables (Percolator)
    private ProphetFilteredPsmResultDAO prophetFiltersPsmDAO;
    private ProphetFilteredSpectraResultDAO prophetFiltersSpectraDAO;
    
    private IbatisDAOFactory() {
        
        // Experiment related
        experimentDAO  = new MsExperimentDAOImpl(sqlMap);
        
        // Enzyme related
        enzymeDAO = new MsEnzymeDAOImpl(sqlMap);
        
        // Instrument related
        instrumentDAO = new MsInstrumentDAOImpl(sqlMap);
        
        // Run related
        scanDAO = new MsScanDAOImpl(sqlMap, MsDataUploadProperties.getPeakStorageType());
        runDAO = new MsRunDAOImpl(sqlMap, enzymeDAO);
        
        // ms2 file related
        ms2FileHeadersDAO = new MS2HeaderDAOImpl(sqlMap);
        ms2ChgIAnalysisDAO = new MS2ChargeIndependentAnalysisDAOImpl(sqlMap);
        ms2ChgDAnalysisDAO = new MS2ChargeDependentAnalysisDAOImpl(sqlMap);
        ms2FileScanChargeDAO = new MS2ScanChargeDAOImpl(sqlMap, ms2ChgDAnalysisDAO);
        ms2ScanDAO = new MS2ScanDAOImpl(sqlMap, scanDAO, ms2ChgIAnalysisDAO, ms2FileScanChargeDAO);
        ms2RunDAO = new MS2RunDAOImpl(sqlMap, runDAO, ms2FileHeadersDAO);
        
        // Search related
        seqDbDao = new MsSearchDatabaseDAOImpl(sqlMap);
        modDAO = new MsSearchModificationDAOImpl(sqlMap);
        resultProteinDAO = new MsSearchResultProteinDAOImpl(sqlMap);
        searchResultDAO = new MsSearchResultDAOImpl(sqlMap, resultProteinDAO, modDAO);
        runSearchDAO = new MsRunSearchDAOImpl(sqlMap);
        searchDAO = new MsSearchDAOImpl(sqlMap, seqDbDao, modDAO, enzymeDAO);
        
        // sqt file related
        sqtSpectrumDAO = new SQTSearchScanDAOImpl(sqlMap);
        sqtHeaderDAO = new SQTHeaderDAOImpl(sqlMap);
        sqtRunSearchDAO = new SQTRunSearchDAOImpl(sqlMap, runSearchDAO, sqtHeaderDAO);
        
        // sequest search related
        sequestResultDAO = new SequestSearchResultDAOImpl(sqlMap, searchResultDAO, runSearchDAO, modDAO);
        sequestSearchDAO = new SequestSearchDAOImpl(sqlMap, searchDAO);
        
        // mascot search related
        mascotResultDAO = new MascotSearchResultDAOImpl(sqlMap, searchResultDAO, runSearchDAO, modDAO);
        mascotSearchDAO = new MascotSearchDAOImpl(sqlMap, searchDAO);
        
        // xtandem search related
        xtandemResultDAO = new XtandemSearchResultDAOImpl(sqlMap, searchResultDAO, runSearchDAO, modDAO);
        xtandemSearchDAO = new XtandemSearchDAOImpl(sqlMap, searchDAO);
        
        // prolucid search related
        prolucidResultDAO = new ProlucidSearchResultDAOImpl(sqlMap, searchResultDAO, runSearchDAO, modDAO);
        prolucidSearchDAO = new ProlucidSearchDAOImpl(sqlMap, searchDAO);
        
        // post search analysis related
        analysisDAO = new MsSearchAnalysisDAOImpl(sqlMap);
        rsAnalysisDAO = new MsRunSearchAnalysisDAOImpl(sqlMap);
        
        // Percolator post search analysis related 
        percSQTHeaderDAO = new PercolatorParamsDAOImpl(sqlMap);
        percResultDAO = new PercolatorResultDAOImpl(sqlMap, rsAnalysisDAO, runSearchDAO, modDAO);
        percPeptResultDAO = new PercolatorPeptideResultDAOImpl(sqlMap, rsAnalysisDAO, runSearchDAO, modDAO, percResultDAO);
       
        
        // PeptideProphet post search analysis related
        ppRocDAO = new PeptideProphetRocDAOImpl(sqlMap);
        pprophResultDAO = new PeptideProphetResultDAOImpl(sqlMap, rsAnalysisDAO);
        
        // DAOs for stats tables (Percolator);
        percFiltersPsmDAO = new PercolatorFilteredPsmResultDAOImpl(sqlMap, rsAnalysisDAO);
		percFiltersSpectraDAO = new PercolatorFilteredSpectraResultDAOImpl(sqlMap, rsAnalysisDAO);
		peptideTerminiStatsDAO = new PeptideTerminiStatsDAOImpl(sqlMap);
		
		 // DAOs for stats tables (PeptideProphet);
        prophetFiltersPsmDAO = new ProphetFilteredPsmResultDAOImpl(sqlMap, rsAnalysisDAO);
        prophetFiltersSpectraDAO = new ProphetFilteredSpectraResultDAOImpl(sqlMap, rsAnalysisDAO);
        
    }
    
    public static IbatisDAOFactory getInstance() {
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        return sqlMap.getDataSource().getConnection();
    }
    //-------------------------------------------------------------------------------------------
    // EXPERIMENT related
    //-------------------------------------------------------------------------------------------
    public MsExperimentDAO getMsExperimentDAO() {
        return experimentDAO;
    }
    
    
    //-------------------------------------------------------------------------------------------
    // ENZYME related
    //-------------------------------------------------------------------------------------------
    public MsEnzymeDAO getEnzymeDAO() {
        return enzymeDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // INSTRUMENT related
    //-------------------------------------------------------------------------------------------
    public MsInstrumentDAO getInstrumentDAO() {
        return instrumentDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // RUN related
    //-------------------------------------------------------------------------------------------
    public MsRunDAO getMsRunDAO() {
        return runDAO;
    }
    
    public MsScanDAO getMsScanDAO() {
        return scanDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // MS2 RUN related
    //-------------------------------------------------------------------------------------------
    public MS2RunDAO getMS2FileRunDAO() {
        return ms2RunDAO;
    }
    
    public MS2ScanDAO getMS2FileScanDAO() {
        return ms2ScanDAO;
    }
    
    public MS2ScanChargeDAO getMS2FileScanChargeDAO() {
        return ms2FileScanChargeDAO;
    }
    
    public MS2HeaderDAO getMS2FileRunHeadersDAO() {
        return ms2FileHeadersDAO;
    }
    
    public MS2ChargeDependentAnalysisDAO getMs2FileChargeDAnalysisDAO() {
        return ms2ChgDAnalysisDAO;
    }
    
    public MS2ChargeIndependentAnalysisDAO getMs2FileChargeIAnalysisDAO() {
        return ms2ChgIAnalysisDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // SEARCH related
    //-------------------------------------------------------------------------------------------
    public MsSearchDAO getMsSearchDAO() {
        return searchDAO;
    }
    
    public MsRunSearchDAO getMsRunSearchDAO() {
        return runSearchDAO;
    }
    
    public MsSearchResultDAO getMsSearchResultDAO() {
        return searchResultDAO;
    }
    
    public MsSearchResultProteinDAO getMsProteinMatchDAO() {
        return resultProteinDAO;
    }
    
    public MsSearchModificationDAO getMsSearchModDAO() {
        return modDAO;
    }
    
    public MsSearchDatabaseDAO getMsSequenceDatabaseDAO() {
        return seqDbDao;
    }
    
    //-------------------------------------------------------------------------------------------
    // SQT file related
    //-------------------------------------------------------------------------------------------
    public SQTHeaderDAO getSqtHeaderDAO() {
        return sqtHeaderDAO;
    }
    
    public SQTRunSearchDAO getSqtRunSearchDAO() {
        return sqtRunSearchDAO;
    }
    
    public SQTSearchScanDAO getSqtSpectrumDAO() {
        return sqtSpectrumDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // Sequest SEARCH related
    //-------------------------------------------------------------------------------------------
    public SequestSearchResultDAO getSequestResultDAO() {
        return sequestResultDAO;
    }
    
    public SequestSearchDAO getSequestSearchDAO() {
        return sequestSearchDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // Mascot SEARCH related
    //-------------------------------------------------------------------------------------------
    public MascotSearchResultDAO getMascotResultDAO() {
        return mascotResultDAO;
    }
    
    public MascotSearchDAO getMascotSearchDAO() {
        return mascotSearchDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // Xtandem SEARCH related
    //-------------------------------------------------------------------------------------------
    public XtandemSearchResultDAO getXtandemResultDAO() {
        return xtandemResultDAO;
    }
    
    public XtandemSearchDAO getXtandemSearchDAO() {
        return xtandemSearchDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // ProLuCID SEARCH related
    //-------------------------------------------------------------------------------------------
    public ProlucidSearchResultDAO getProlucidResultDAO() {
        return prolucidResultDAO;
    }
    
    public ProlucidSearchDAO getProlucidSearchDAO() {
        return prolucidSearchDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // Post search analysis related
    //-------------------------------------------------------------------------------------------
    public MsSearchAnalysisDAO getMsSearchAnalysisDAO() {
        return analysisDAO;
    }
    
    public MsRunSearchAnalysisDAO getMsRunSearchAnalysisDAO(){
        return rsAnalysisDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // Percolator related
    //-------------------------------------------------------------------------------------------
    public PercolatorParamsDAO getPercoltorParamsDAO() {
        return percSQTHeaderDAO;
    }
    
    public PercolatorResultDAO getPercolatorResultDAO() {
        return percResultDAO;
    }
    
    public PercolatorPeptideResultDAO getPercolatorPeptideResultDAO() {
    	return percPeptResultDAO;
    }
    
    
    //-------------------------------------------------------------------------------------------
    // PeptideProphet related
    //-------------------------------------------------------------------------------------------
    public PeptideProphetResultDAO getPeptideProphetResultDAO() {
        return pprophResultDAO;
    }
    
    public PeptideProphetRocDAO getPeptideProphetRocDAO() {
        return ppRocDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // stats related
    //-------------------------------------------------------------------------------------------
    public PeptideTerminiStatsDAO getPeptideTerminiStatsDAO() {
    	return peptideTerminiStatsDAO;
    }
    
    public PercolatorFilteredPsmResultDAO getPrecolatorFilteredPsmResultDAO() {
    	return percFiltersPsmDAO;
    }
    
    public PercolatorFilteredSpectraResultDAO getPrecolatorFilteredSpectraResultDAO() {
    	return percFiltersSpectraDAO;
    }
    
    public ProphetFilteredPsmResultDAO getProphetFilteredPsmResultDAO() {
    	return prophetFiltersPsmDAO;
    }
    
    public ProphetFilteredSpectraResultDAO getProphetFilteredSpectraResultDAO() {
    	return prophetFiltersSpectraDAO;
    }
}
