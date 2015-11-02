/**
 * 
 */
package org.yeastrc.www.proteinfer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.bio.go.EvidenceCode;
import org.yeastrc.bio.go.EvidenceUtils;
import org.yeastrc.bio.go.GOCache;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.GOProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.www.go.Annotation;
import org.yeastrc.www.go.GOEvidenceCodeConverter;
import org.yeastrc.www.go.ProteinGOAnnotationChecker;

/**
 * ProteinGoTermsFilter.java
 * @author Vagisha Sharma
 * Jul 2, 2010
 * 
 */
public class ProteinGoTermsFilter {

    
    private static ProteinGoTermsFilter instance;
    
    private static final Logger log = Logger.getLogger(ProteinGoTermsFilter.class.getName());
    
    private ProteinGoTermsFilter() {}
    
    public static ProteinGoTermsFilter getInstance() {
    	if(instance == null)
    		instance = new ProteinGoTermsFilter();
    	return instance;
    }

    public List<Integer> filterNrseqProteins(List<Integer> nrseqIds, GOProteinFilterCriteria filters) throws Exception {
    	
    	if(filters == null)
    		return nrseqIds;
    	
    	List<String> goAccessions = filters.getGoAccessions();
    	if(goAccessions == null || goAccessions.size() == 0)
    		return nrseqIds;
    	
    	// Convert the GO accessions into GONode objects
    	List<GONode> goNodes = getGoNodes(goAccessions);
    	
    	// Get any evidence codes to exclude
    	List<EvidenceCode> evidenceCodes = getEvidenceCodes(filters);
    	
    	List<Integer> filteredIds = null;
    	if(!filters.isMatchAllGoTerms())
    		filteredIds = getFilteredNrseqProteinIdsAnyMatch(nrseqIds, filters.isExactAnnotation(), goNodes, evidenceCodes);
    	else
    		filteredIds = getFilteredNrseqProteinIdsAllMatch(nrseqIds, filters.isExactAnnotation(), goNodes, evidenceCodes);
    	
        return filteredIds;
    }
    
    public List<Integer> filterPinferProteins(List<Integer> allProteinIds, GOProteinFilterCriteria filters) throws Exception {
        
    	if(filters == null)
    		return allProteinIds;
    	
    	List<String> goAccessions = filters.getGoAccessions();
    	if(goAccessions == null || goAccessions.size() == 0)
    		return allProteinIds;
    	
    	// Convert the GO accessions into GONode objects
    	List<GONode> goNodes = getGoNodes(goAccessions);
    	
    	// Get any evidence codes to exclude
    	List<EvidenceCode> evidenceCodes = getEvidenceCodes(filters);
    	
    	List<Integer> filteredIds = null;
    	if(!filters.isMatchAllGoTerms())
    		filteredIds = getFilteredPinferProteinIdsAnyMatch(allProteinIds, filters.isExactAnnotation(), goNodes, evidenceCodes);
    	else
    		filteredIds = getFilteredPinferProteinIdsAllMatch(allProteinIds, filters.isExactAnnotation(), goNodes, evidenceCodes);
    	
        return filteredIds;
    }

	private List<EvidenceCode> getEvidenceCodes(GOProteinFilterCriteria filters) {
		
		return GOEvidenceCodeConverter.convert(filters.getExcludeEvidenceCodes());
	}

	private List<GONode> getGoNodes(List<String> goAccessions) throws Exception {
		List<GONode> goNodes = new ArrayList<GONode>(goAccessions.size());
    	for(String goAccession: goAccessions) {
    		goAccession = goAccession.trim();
    		if(goAccession == null || goAccession.length() == 0)
    			continue;
    		GONode node = GOCache.getInstance().getGONode(goAccession);
    		if(node != null)
    			goNodes.add(node);
    	}
		return goNodes;
	}

    // Returns a list of protein inference proteinIds that match any one of the given GO terms
	private List<Integer> getFilteredPinferProteinIdsAnyMatch(List<Integer> allProteinIds,
			boolean exact, List<GONode> goNodes, List<EvidenceCode> evidenceCodes) throws SQLException {
		
		List<Integer> filteredIds = new ArrayList<Integer>();
    	ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        ProteinferProteinDAO protDao = factory.getProteinferProteinDao();
        
    	for(Integer proteinId: allProteinIds) {
    		ProteinferProtein protein = protDao.loadProtein(proteinId);
    		
    		if(anyMatch(protein.getNrseqProteinId(), exact, goNodes, evidenceCodes))
    			filteredIds.add(proteinId);
    	}
		return filteredIds;
	}
	
	// Returns a list of protein inference proteinIds that match all of the given GO terms
	private List<Integer> getFilteredPinferProteinIdsAllMatch(List<Integer> allProteinIds,
			boolean exact, List<GONode> goNodes, List<EvidenceCode> evidenceCodes) throws SQLException {
		
		List<Integer> filteredIds = new ArrayList<Integer>();
    	ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        ProteinferProteinDAO protDao = factory.getProteinferProteinDao();
        
    	for(Integer proteinId: allProteinIds) {
    		ProteinferProtein protein = protDao.loadProtein(proteinId);
    		if(allMatch(protein.getNrseqProteinId(), exact, goNodes, evidenceCodes))
    			filteredIds.add(proteinId);
    	}
		return filteredIds;
	}
	
	// Returns a list of nrseq proteinIds that match any one of the given GO terms
	private List<Integer> getFilteredNrseqProteinIdsAnyMatch(List<Integer> nrseqProteinIds,
			boolean exact, List<GONode> goNodes, List<EvidenceCode> evidenceCodes) throws SQLException {
		
		List<Integer> filteredIds = new ArrayList<Integer>();
        
    	for(Integer proteinId: nrseqProteinIds) {
    		if(anyMatch(proteinId, exact, goNodes, evidenceCodes))
    			filteredIds.add(proteinId);
    	}
		return filteredIds;
	}
	
	// Returns a list of nrseq proteinIds that match all of the given GO terms
	private List<Integer> getFilteredNrseqProteinIdsAllMatch(List<Integer> nrseqProteinIds,
			boolean exact, List<GONode> goNodes, List<EvidenceCode> evidenceCodes) throws SQLException {
		
		List<Integer> filteredIds = new ArrayList<Integer>();
        
    	for(Integer proteinId: nrseqProteinIds) {
    		if(allMatch(proteinId, exact, goNodes, evidenceCodes))
    			filteredIds.add(proteinId);
    	}
		return filteredIds;
	}
	
	// Returns a list of proteinIds that match any one of the given GO terms
	private boolean anyMatch(int nrseqProteinId,
			boolean exact, List<GONode> goNodes, List<EvidenceCode> evidenceCodes) throws SQLException {
		
		for(GONode node: goNodes) {
			Annotation annot = null;
			if(evidenceCodes.size() == 0)
				annot = ProteinGOAnnotationChecker.isProteinAnnotated(nrseqProteinId, node.getId());
			else
				annot = ProteinGOAnnotationChecker.isProteinAnnotated(nrseqProteinId, node.getId(), evidenceCodes);

			if(annot == Annotation.NONE)
				continue;
			if(exact) {
				if(annot == Annotation.EXACT) {
					return true;
				}
			}
			else {
				return true;
			}
		}
		return false;
	}
	
	// Returns a list of proteinIds that match all of the given GO terms
	private boolean allMatch(int nrseqProteinId,
			boolean exact, List<GONode> goNodes, List<EvidenceCode> evidenceCodes) throws SQLException {
		

		for(GONode node: goNodes) {
			Annotation annot = null;

			if(evidenceCodes.size() == 0)
				annot = ProteinGOAnnotationChecker.isProteinAnnotated(nrseqProteinId, node.getId());
			else
				annot = ProteinGOAnnotationChecker.isProteinAnnotated(nrseqProteinId, node.getId(), evidenceCodes);

			if(annot == Annotation.NONE) {
				return false;
			}
			if(exact) {
				if(annot != Annotation.EXACT) {
					return false;
				}
			}
		}
		return true;
	}
}
