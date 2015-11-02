/**
 * MsResultLoader.java
 * @author Vagisha Sharma
 * Sep 22, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.Program;

/**
 * 
 */
public class MsResultLoader {

    
    private final SequestSearchResultDAO seqResDao;
    private final ProlucidSearchResultDAO plcidResDao;
    private final PercolatorResultDAO percResDao;
    private final PeptideProphetResultDAO ppResDao;
    
    private static final Logger log = Logger.getLogger(MsResultLoader.class.getName());
    
    private static MsResultLoader instance;
    
    private MsResultLoader() {
        DAOFactory daofactory = DAOFactory.instance();
        seqResDao = daofactory.getSequestResultDAO();
        plcidResDao = daofactory.getProlucidResultDAO();
        percResDao = daofactory.getPercolatorResultDAO();
        ppResDao = daofactory.getPeptideProphetResultDAO();
    }
    
    public static MsResultLoader getInstance() {
        if(instance == null) {
            instance = new MsResultLoader();
        }
        return instance;
    }
    
    public MsSearchResult getResult(int resultId, Program inputGenerator) {
        
    	log.debug("Getting result for "+inputGenerator+" resultID: "+resultId);
    
        MsSearchResult result = null;
        if(inputGenerator == Program.SEQUEST) {
            return seqResDao.load(resultId);
        }
        else if(inputGenerator == Program.PROLUCID) {
            return plcidResDao.load(resultId);
        }
        else if(inputGenerator == Program.PERCOLATOR) {
            return percResDao.loadForPercolatorResultId(resultId);
        }
        else if(inputGenerator == Program.PEPTIDE_PROPHET) {
            return ppResDao.loadForProphetResultId(resultId);
        }
        else {
            log.warn("Unrecognized input generator for protein inference: "+inputGenerator);
        }
        
        return result;
    }
}
