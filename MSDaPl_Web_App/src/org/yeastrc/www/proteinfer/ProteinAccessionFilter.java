/**
 * ProteinAccessionFilter.java
 * @author Vagisha Sharma
 * Aug 29, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.nrseq.FastaProteinLookupUtil;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrDbProtein;

/**
 * 
 */
public class ProteinAccessionFilter {

    private ProteinferProteinDAO protDao = null; 
    
    private static ProteinAccessionFilter instance;
    
    private ProteinAccessionFilter() {
    	protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
    }
    
    public static ProteinAccessionFilter getInstance() {
    	if(instance == null)
    		instance = new ProteinAccessionFilter();
    	return instance;
    }

    public List<Integer> filterForProtInferByProteinAccession(int pinferId,
            List<Integer> allProteinIds, String accessionLike) {
        
        // get the protein accession map if we have one
        // but don't create a new one if it does not exist for
        // the protein inference we are looking at right now. 
    	Map<Integer, ? extends ProteinProperties> propsMap = ProteinPropertiesStore.getInstance().getPropertiesMapForAccession(pinferId, false);
        
        return filterForProtInferByProteinAccession(pinferId, allProteinIds, propsMap, accessionLike);
    }
    
    private List<Integer> filterForProtInferByProteinAccession(int pinferId,
            List<Integer> allProteinIds,
            Map<Integer, ? extends ProteinProperties> propsMap, String accessionLike) {
        
        Set<String> reqAcc = new HashSet<String>();
        String[] tokens = accessionLike.split(",");
        for(String tok: tokens)
            reqAcc.add(tok.trim().toLowerCase());
        
        List<Integer> filtered = new ArrayList<Integer>();
        
        
        // If we have a properties map look in there
        if(propsMap != null) {
        
            for(int id: allProteinIds) {
                ProteinProperties props = propsMap.get(id);
                for(String ra: reqAcc) {
                    if(props.hasPartialAccession(ra)) {
                        filtered.add(id);
                        break;
                    }
                }
            }
        }
        
        // Look in the database for matching ids.
        else {
            List<Integer> found = FastaProteinLookupUtil.getInstance().getProteinIdsForAccessions(new ArrayList<String>(reqAcc), pinferId);
            
            // get the corresponding protein inference protein ids
            if(found.size() > 0) {
                List<Integer> piProteinIds = protDao.getProteinIdsForNrseqIds(pinferId, new ArrayList<Integer>(found));
                Collections.sort(piProteinIds);
                for(int id: allProteinIds) {
                    if(Collections.binarySearch(piProteinIds, id) >= 0)
                        filtered.add(id);
                }
            }
        }
        
        return filtered;
    }
    
    public List<Integer> filterNrseqIdsByAccession(List<Integer> allNrseqIds, 
    		String searchString, List<Integer> fastaDbIds) throws SQLException {
        
    	List<Integer> sortedIds = getSortedNrseqIdsMatchingAccession(searchString, fastaDbIds);
        if(sortedIds == null || sortedIds.size() == 0)
            return new ArrayList<Integer>(0); // no matching nrseq IDs found

        // Remove the ones that match
        return getMatching(allNrseqIds, sortedIds);
    }
    
    private List<Integer> getSortedNrseqIdsMatchingAccession(String searchString, List<Integer> fastaDbIds) throws SQLException {

    	if(searchString == null || searchString.trim().length() == 0)
    		return null;

    	//get the protein ids for the names the user is searching for
    	Set<Integer> proteinIds = new HashSet<Integer>();
    	String tokens[] = searchString.split(",");

    	
    	// Now look at the accession strings in tblProteinDatabase;
    	for(String token: tokens) {
    		String accession = token.trim();
    		if(accession.length() > 0) {
    			List<NrDbProtein> proteins = NrSeqLookupUtil.getDbProteinsForAccession(fastaDbIds, accession);
    			for(NrDbProtein prot: proteins)
    				proteinIds.add(prot.getProteinId());
    		}
    	}

        // sort the matching protein ids.
        List<Integer> sortedIds = new ArrayList<Integer>(proteinIds.size());
        sortedIds.addAll(proteinIds);
        Collections.sort(sortedIds);
        return sortedIds;
    }
    
    private List<Integer> getMatching(List<Integer> allNrseqIds, List<Integer> sortedMatchingIds) {
    	
    	List<Integer> matching = new ArrayList<Integer>();
        for(int nrseqId: allNrseqIds) {
            if(Collections.binarySearch(sortedMatchingIds, nrseqId) >= 0)
                matching.add(nrseqId);
        }
        return matching;
    }
}
