/**
 * FastaProteinNameLookupUtil.java
 * @author Vagisha Sharma
 * May 16, 2009
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrDbProtein;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;

/**
 * 
 */
public class FastaProteinLookupUtil {

    private static FastaProteinLookupUtil instance;
    
    private FastaProteinLookupUtil() {}
    
    public static FastaProteinLookupUtil getInstance() {
        if(instance == null)
            instance = new FastaProteinLookupUtil();
        return instance;
    }


    public List<Integer> getProteinIdsForAccessions(List<String> fastaAccessions, int pinferId) {
        
        List<Integer> dbIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        Set<Integer> found = new HashSet<Integer>();
        for(String ra: fastaAccessions) {
            found.addAll(getProteinIdsForAccession(ra, dbIds));
        }
        return new ArrayList<Integer>(found);
    }
    
    private List<Integer> getProteinIdsForAccession(String fastaAccession, List<Integer> dbIds) {
        Set<Integer> found = new HashSet<Integer>();
        List<NrDbProtein> matching = NrSeqLookupUtil.getDbProteinsForAccession(dbIds, fastaAccession);
        for(NrDbProtein prot: matching)
            found.add(prot.getProteinId());
        return new ArrayList<Integer>(found);
    }
    
    public List<Integer> getNrseqIdsForDescriptions(List<String> descriptionTerms, int pinferId) {
        
    	// get a list of databases associated with this protein inference
    	// Add the standard databases to the list too.
        List<Integer> dbIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId, false);
        
        return getNrseqIdsForDescriptions(descriptionTerms, dbIds);
    }
    
    public List<Integer> getPiProteinIdsForDescriptions(List<String> descriptionTerms, int pinferId) {
        
    	// get a list of databases associated with this protein inference
        List<Integer> dbIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId, false);
        
        List<Integer> nrseqIds = getNrseqIdsForDescriptions(descriptionTerms, dbIds);
        
        ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
        
        // Get the protein inference IDs corresponding to the matching NRSEQ IDs.
        return protDao.getProteinIdsForNrseqIds(pinferId, new ArrayList<Integer>(nrseqIds));
    }

	public List<Integer> getNrseqIdsForDescriptions(List<String> descriptionTerms, List<Integer> dbIds) {
		if(dbIds.size() == 0)
			return new ArrayList<Integer>(0);
		
		Set<Integer> found = new HashSet<Integer>();
        for(String descTerm: descriptionTerms) {
            found.addAll(getNrSeqIdsForDescription(descTerm, dbIds));
        }
        return new ArrayList<Integer>(found);
	}
	
	public List<Integer> getPiProteinIdsForDescriptions(List<String> descriptionTerms, List<Integer> dbIds, int pinferId) {
		if(dbIds.size() == 0)
			return new ArrayList<Integer>(0);
		
		Set<Integer> nrseqIds = new HashSet<Integer>();
        for(String descTerm: descriptionTerms) {
        	nrseqIds.addAll(getNrSeqIdsForDescription(descTerm, dbIds));
        }
        
        ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
        // Get the protein inference IDs corresponding to the matching NRSEQ IDs.
        return protDao.getProteinIdsForNrseqIds(pinferId, new ArrayList<Integer>(nrseqIds));
	}
    
    private List<Integer> getNrSeqIdsForDescription(String descriptionTerm, List<Integer> dbIds) {
        
    	if(dbIds.size() == 0)
			return new ArrayList<Integer>(0);
    	
        Set<Integer> found = new HashSet<Integer>();
        List<NrDbProtein> matching = NrSeqLookupUtil.getDbProteinsForDescription(dbIds, descriptionTerm);
        for(NrDbProtein prot: matching)
            found.add(prot.getProteinId());
        return new ArrayList<Integer>(found);
    }
}
