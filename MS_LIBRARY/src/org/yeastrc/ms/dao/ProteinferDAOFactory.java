package org.yeastrc.ms.dao;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferInputDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferIonDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunSummaryDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerInputDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerIonDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerParamDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerPeptideBaseDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerPeptideDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinBaseDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerRunDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerSpectrumMatchDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetParamDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetPeptideDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinGroupDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinIonDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetRocDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetRunDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetRunSummaryDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetSubsumedProteinDAO;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;


public class ProteinferDAOFactory {

    private static final Logger log = Logger.getLogger(ProteinferDAOFactory.class);
    
    // initialize the SqlMapClient
    private static SqlMapClient sqlMap;
    
    static {
        Reader reader = null;
//        String ibatisConfigFile = "ProteinferSqlMapConfig.xml";
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
            log.error("Error initializing "+ProteinferDAOFactory.class.getName()+" class: ", e);
            throw new RuntimeException("Error initializing "+ProteinferDAOFactory.class.getName()+" class: ", e);
        }
        System.out.println("Loaded Ibatis SQL map config -- "+ibatisConfigFile);
    }
    
    private static ProteinferDAOFactory instance = new ProteinferDAOFactory();
    
    private ProteinferRunDAO pinferRunDao;
    private IdPickerParamDAO pinferParamDao;
    private ProteinferInputDAO pinferInputDao;
    private ProteinferPeptideDAO peptideDao;
    private ProteinferProteinDAO proteinDao;
    private ProteinferIonDAO ionDao;
    private ProteinferSpectrumMatchDAO spectrumMatchDao;
    
    // IDPicker related
    private IdPickerSpectrumMatchDAO idpSpectrumMatchDao;
    private IdPickerIonDAO idpIonDao;
    private IdPickerPeptideDAO idpPeptideDao;
    private IdPickerPeptideBaseDAO idpPeptideBaseDao;
    private IdPickerProteinDAO idpProteinDao;
    private IdPickerProteinBaseDAO idpProteinBaseDao;
    private IdPickerInputDAO idpInputDao;
    private IdPickerRunDAO idpRunDao;
    
    // ProteinProphet related
    private ProteinProphetParamDAO ppParamDao;
    private ProteinProphetRocDAO ppRocDao;
    private ProteinProphetRunDAO ppRunDao;
    private ProteinProphetProteinGroupDAO ppProteinGrpDao;
    private ProteinProphetProteinDAO ppProteinDao;
    private ProteinProphetPeptideDAO ppPeptideDao;
    private ProteinProphetProteinIonDAO ppProteinIonDao;
    private ProteinProphetSubsumedProteinDAO ppSubsumedDao;
    
    // Summary table related
    private ProteinferRunSummaryDAO pinferRunSummaryDao;
    private ProteinProphetRunSummaryDAO ppRunSummaryDao;
    
    
    
    private ProteinferDAOFactory() {
        
       pinferRunDao = new ProteinferRunDAO(sqlMap);
       pinferParamDao = new IdPickerParamDAO(sqlMap);
       pinferInputDao = new ProteinferInputDAO(sqlMap);
       spectrumMatchDao = new ProteinferSpectrumMatchDAO(sqlMap);
       ionDao = new ProteinferIonDAO(sqlMap);
       peptideDao = new ProteinferPeptideDAO(sqlMap);
       proteinDao = new ProteinferProteinDAO(sqlMap);
       
       
       // IDPicker related
       idpSpectrumMatchDao = new IdPickerSpectrumMatchDAO(sqlMap, spectrumMatchDao);
       idpIonDao = new IdPickerIonDAO(sqlMap, ionDao);
       idpPeptideDao = new IdPickerPeptideDAO(sqlMap, peptideDao);
       idpPeptideBaseDao = new IdPickerPeptideBaseDAO(sqlMap, peptideDao);
       idpProteinDao = new IdPickerProteinDAO(sqlMap, proteinDao);
       idpProteinBaseDao = new IdPickerProteinBaseDAO(sqlMap, proteinDao);
       idpInputDao = new IdPickerInputDAO(sqlMap, pinferInputDao);
       idpRunDao = new IdPickerRunDAO(sqlMap, pinferRunDao);
       
       // ProteinProphet related
       ppParamDao = new ProteinProphetParamDAO(sqlMap);
       ppRocDao = new ProteinProphetRocDAO(sqlMap);
       ppRunDao = new ProteinProphetRunDAO(sqlMap, pinferRunDao);
       ppProteinGrpDao = new ProteinProphetProteinGroupDAO(sqlMap);
       ppProteinDao = new ProteinProphetProteinDAO(sqlMap, proteinDao);
       ppPeptideDao = new ProteinProphetPeptideDAO(sqlMap, peptideDao);
       ppProteinIonDao = new ProteinProphetProteinIonDAO(sqlMap);
       ppSubsumedDao = new ProteinProphetSubsumedProteinDAO(sqlMap);
       
       // Summary table related
       pinferRunSummaryDao = new ProteinferRunSummaryDAO(sqlMap);
       ppRunSummaryDao = new ProteinProphetRunSummaryDAO(sqlMap);
    }
    
    public static ProteinferDAOFactory instance() {
        return instance;
    }

    public static ProteinferDAOFactory testInstance() {
        Reader reader = null;
        String ibatisConfigFile = "edu/uwpr/protinfer/database/sqlmap/TestProteinferSqlMapConfig.xml";
        try {
            reader = Resources.getResourceAsReader(ibatisConfigFile);
            sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
        }
        catch (IOException e) {
            log.error("Error reading Ibatis config xml: "+ibatisConfigFile, e);
            throw new RuntimeException("Error reading Ibatis config xml: "+ibatisConfigFile, e);
        }
        catch (Exception e) {
            log.error("Error initializing "+ProteinferDAOFactory.class.getName()+" class: ", e);
            throw new RuntimeException("Error initializing "+ProteinferDAOFactory.class.getName()+" class: ", e);
        }
        System.out.println("Loaded Ibatis SQL map config: "+ibatisConfigFile);
        return new ProteinferDAOFactory();
    }
    
    //---------------------------------------------------------------------
    // Protein Inference run related
    //---------------------------------------------------------------------
    public ProteinferRunDAO getProteinferRunDao() {
        return pinferRunDao;
    }

    public IdPickerParamDAO getProteinferParamDao() {
        return pinferParamDao;
    }

    public ProteinferInputDAO getProteinferInputDao() {
        return pinferInputDao;
    }
    
    public ProteinferProteinDAO getProteinferProteinDao() {
        return proteinDao;
    }

    public ProteinferPeptideDAO getProteinferPeptideDao() {
        return peptideDao;
    }
        
    public ProteinferIonDAO getProteinferIonDao() {
        return ionDao;
    }
    
    public ProteinferSpectrumMatchDAO getProteinferSpectrumMatchDao() {
        return spectrumMatchDao;
    }
    
    //---------------------------------------------------------------------
    // IDPicker related
    //---------------------------------------------------------------------
    public IdPickerSpectrumMatchDAO getIdPickerSpectrumMatchDao() {
        return idpSpectrumMatchDao;
    }
    
    public IdPickerIonDAO getIdPickerIonDao() {
        return idpIonDao;
    }
    
    public IdPickerPeptideBaseDAO getIdPickerPeptideBaseDao() {
        return idpPeptideBaseDao;
    }
    
    public IdPickerPeptideDAO getIdPickerPeptideDao() {
        return idpPeptideDao;
    }
    
    public IdPickerProteinDAO getIdPickerProteinDao() {
        return idpProteinDao;
    }
    
    public IdPickerProteinBaseDAO getIdPickerProteinBaseDao() {
        return idpProteinBaseDao;
    }
    
    public IdPickerInputDAO getIdPickerInputDao() {
        return idpInputDao;
    }
    
    public IdPickerRunDAO getIdPickerRunDao() {
        return idpRunDao;
    }
    
    //---------------------------------------------------------------------
    // ProteinProphet related
    //---------------------------------------------------------------------
    public ProteinProphetParamDAO getProteinProphetParamDao() {
        return ppParamDao;
    }
    
    public ProteinProphetRocDAO getProteinProphetRocDao() {
        return ppRocDao;
    }
    
    public ProteinProphetRunDAO getProteinProphetRunDao() {
        return ppRunDao;
    }
    
    public ProteinProphetProteinGroupDAO getProteinProphetProteinGroupDao() {
        return ppProteinGrpDao;
    }
    
    public ProteinProphetProteinDAO getProteinProphetProteinDao() {
        return ppProteinDao;
    }
    
    public ProteinProphetPeptideDAO getProteinProphetPeptideDao() {
        return ppPeptideDao;
    }
    
    public ProteinProphetProteinIonDAO getProteinProphetProteinIonDao() {
        return ppProteinIonDao;
    }
    
    public ProteinProphetSubsumedProteinDAO getProteinProphetSubsumedProteinDao() {
        return ppSubsumedDao;
    }
    
    //---------------------------------------------------------------------
    // Summary table related
    //---------------------------------------------------------------------
    public ProteinferRunSummaryDAO getProteinferRunSummaryDao() {
    	return pinferRunSummaryDao;
    }
    
    public ProteinProphetRunSummaryDAO getProteinProphetRunSummaryDao() {
    	return ppRunSummaryDao;
    }
}
