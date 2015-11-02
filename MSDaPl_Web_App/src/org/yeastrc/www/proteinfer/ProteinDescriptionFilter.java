/**
 * ProteinDescriptionFilter.java
 * @author Vagisha Sharma
 * Aug 29, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nr_seq.database.StandardDatabase;
import org.yeastrc.nr_seq.database.StandardDatabaseCache;
import org.yeastrc.nrseq.FastaProteinLookupUtil;
import org.yeastrc.nrseq.FlyBaseUtils;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrDatabase;
import org.yeastrc.nrseq.domain.NrProtein;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;

/**
 * 
 */
public class ProteinDescriptionFilter {

    private ProteinferProteinDAO protDao = null;
    
    private static final Logger log = Logger.getLogger(ProteinDescriptionFilter.class.getName());
    
    private static ProteinDescriptionFilter instance = null;
    
    private ProteinDescriptionFilter() {
    	protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
    }
    
    public static ProteinDescriptionFilter getInstance() {
    	if(instance == null)
    		instance = new ProteinDescriptionFilter();
    	return instance;
    }
    
    
    /**
     * Returns a list of protein inference protein IDs that match one or more of the given description terms
     * Descriptions in the fasta files associated with the protein inference are looked at first
     * Descriptions in any species-specific databases are also searches. Target species associated with the experiments
     * are used. 
     * If useStandardDatabases == true, SWISS-PROT and NCBI are also searched
     * @param pinferId
     * @param proteinIds
     * @param descriptions
     * @param useStandardDatabases
     * @return
     */
    public List<Integer> filterPiProteinsByDescriptionLike(int pinferId, List<Integer> proteinIds,
    		String descriptions, boolean useStandardDatabases) {
    	
    	if(descriptions == null || descriptions.trim().length() == 0)
    		return proteinIds;
    	
    	if(useStandardDatabases)
    		log.info("Using StandardDatabases for filtering on description terms: "+descriptions);
    	
    	
    	Set<String> reqDescriptions = new HashSet<String>();
        String[] tokens = descriptions.split(",");
        for(String tok: tokens)
            reqDescriptions.add(tok.trim()); 
        
        	
    	Set<Integer> filtered = new HashSet<Integer>();
    	
    	// filtered Protein Inference Protein IDs from the fasta databases
    	long s = System.currentTimeMillis();
    	List<Integer> filteredFromFastaDbs = FastaProteinLookupUtil.getInstance().getPiProteinIdsForDescriptions(new ArrayList<String>(reqDescriptions), pinferId);
    	filtered.addAll(filteredFromFastaDbs);
    	long e = System.currentTimeMillis();
    	log.info("Got filtered results for description from fasta databases in "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    	
    	// filtered from any species-specific databases
    	s = System.currentTimeMillis();
    	List<Integer> speciesDbList = getSpeciesSpecificDatabases(pinferId);
    	
    	// If one of the databases is flybase lookup flybase for proteins matching description
    	// The descriptions in YRC_NRSEQ's tblProteinDatabase for flybase are not helpful.
    	Iterator<Integer> iter = speciesDbList.iterator();
    	NrDatabase flybaseDb = StandardDatabaseCache.getNrDatabase(StandardDatabase.FLYBASE);
    	while(iter.hasNext()) {
    		Integer speciesDbId = iter.next();
    		if(speciesDbId == flybaseDb.getId()) {
    			
    			List<Integer> nrseqFlybaseFiltered = null;
				try {
					nrseqFlybaseFiltered = FlyBaseUtils.getIdsMatchingDescription(new ArrayList<String>(reqDescriptions));
				} catch (SQLException e1) {
					log.error("Error getting flybase proteins matching description terms "+reqDescriptions, e1);
				}
				if(nrseqFlybaseFiltered != null) {
					ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
					// Get the protein inference IDs corresponding to the matching NRSEQ IDs.
					List<Integer> filteredFromFlybase = protDao.getProteinIdsForNrseqIds(pinferId, new ArrayList<Integer>(nrseqFlybaseFiltered));
					filtered.addAll(filteredFromFlybase);
				}
				
		    	e = System.currentTimeMillis();
		    	log.info("Got filtered results for description from FLYBASE in "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");

				// remove this from the database list; We will not search the flybase descriptions in nrseq.
    			iter.remove();
    			break;
    		}
    	}
        
        s = System.currentTimeMillis();
    	List<Integer> filterFromStdDbs = FastaProteinLookupUtil.getInstance().getPiProteinIdsForDescriptions(new ArrayList<String>(reqDescriptions), speciesDbList, pinferId);
    	filtered.addAll(filterFromStdDbs);
    	e = System.currentTimeMillis();
    	log.info("Got filtered results for description from species-specific databases in "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    	
    	
    	if(useStandardDatabases) {
    		
    		s = System.currentTimeMillis();
    		for(int piProteinId: proteinIds) {

    			if(filtered.contains(piProteinId)) // If this protein is already in the filtered list skip it
    				continue;
    			
    			// load the protein inference protein
    			ProteinferProtein protein = protDao.loadProtein(piProteinId);

    			NrProtein nrProtein = NrSeqLookupUtil.getNrProtein(protein.getNrseqProteinId());

    			// first look for matching descriptions in Swiss-Prot
    			s = System.currentTimeMillis();
    			boolean matches = hasDescriptionMatchInDatabase(reqDescriptions, nrProtein.getId(),
						StandardDatabase.SWISSPROT);
    			
    			e = System.currentTimeMillis();
    			
    			// Look in NCBI-NR if no match was found
    			if(!matches)
    				matches = hasDescriptionMatchInDatabase(reqDescriptions, nrProtein.getId(),
						StandardDatabase.NCBI_NR);


    			if(matches)
    				filtered.add(piProteinId);
    		}
    		e = System.currentTimeMillis();
	    	log.info("Got filtered results for description from Swiss-Prot and NCBI-NR in "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    	}
    	
    	// Finally return the intersection of the proteinIds given to us and the ones that pass the description filter 
    	List<Integer> filteredList = new ArrayList<Integer>(filtered);
    	Collections.sort(filteredList);
    	return getMatching(proteinIds, filteredList);
    }

    
    /**
     * Returns a list of protein inference protein IDs that DO NOT match one or more of the given description terms
     * Descriptions in the fasta files associated with the protein inference are looked at first
     * Descriptions in any species-specific databases are also searches. Target species associated with the experiments
     * are used. 
     * If useStandardDatabases == true, SWISS-PROT and NCBI are also searched
     * @param pinferId
     * @param proteinIds
     * @param descriptions
     * @param useStandardDatabases
     * @return
     */
    public List<Integer> filterPiProteinsByDescriptionNotLike(int pinferId, List<Integer> proteinIds,
    		String descriptions, boolean useStandardDatabses) {
    	
    	List<Integer> toUnfilter = filterPiProteinsByDescriptionLike(pinferId, proteinIds, 
    			descriptions, useStandardDatabses);
    	
    	if(toUnfilter.size() > 0) {
    		Collections.sort(toUnfilter);
    		return getNotMatching(proteinIds, toUnfilter);
    	}
    	else
    		return proteinIds;
    }
    
   
    /**
     * Returns a list of NRSEQ protein IDs that match one or more of the given description terms
     * Descriptions in the fasta files associated with the protein inference are looked at first
     * Descriptions in any species-specific databases are also searches. Target species associated with the experiments
     * are used. 
     * If useStandardDatabases == true, SWISS-PROT and NCBI are also searched
     * @param allNrseqIds
     * @param fastaDatabaseIds
     * @param searchString
     * @param includeMatching
     * @return
     * @throws SQLException
     */
    public List<Integer> filterNrseqIdsByDescriptionLike(List<Integer> pinferIds,
    		List<Integer> nrseqIds, String searchString, boolean useStandardDatabases) {
    	
        if(searchString == null || searchString.trim().length() == 0)
            return nrseqIds;

        if(useStandardDatabases)
    		log.info("Using StandardDatabases for filtering on description terms: "+searchString);
        
        Set<String> reqDescriptions = new HashSet<String>();
        String[] tokens = searchString.split(",");
        for(String tok: tokens)
            reqDescriptions.add(tok.trim()); 
        
        
        Set<Integer> filtered = new HashSet<Integer>();
    	
        // get a list of databases associated with the given protein inferences
        List<Integer> fastaDbIds =  ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInferences(pinferIds);
            
        
    	// filtered nrseq IDs from the fasta databases
    	long s = System.currentTimeMillis();
    	List<Integer> filteredFromFastaDbs = FastaProteinLookupUtil.getInstance().getNrseqIdsForDescriptions(new ArrayList<String>(reqDescriptions), fastaDbIds);
    	Collections.sort(filteredFromFastaDbs);
    	filtered.addAll(getMatching(nrseqIds, filteredFromFastaDbs));
    	long e = System.currentTimeMillis();
    	log.info("Got filtered results for description from fasta databases in "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    	
    	
    	// filtered from any species-specific databases
    	s = System.currentTimeMillis();
    	List<Integer> speciesDbList = getSpeciesSpecificDatabases(pinferIds);
    	
    	// If one of the databases is flybase lookup flybase for proteins matching description
    	// The descriptions in YRC_NRSEQ's tblProteinDatabase for flybase are not helpful.
    	Iterator<Integer> iter = speciesDbList.iterator();
    	NrDatabase flybaseDb = StandardDatabaseCache.getNrDatabase(StandardDatabase.FLYBASE);
    	while(iter.hasNext()) {
    		Integer speciesDbId = iter.next();
    		if(speciesDbId == flybaseDb.getId()) {
    			
    			List<Integer> nrseqFlybaseFiltered = null;
				try {
					nrseqFlybaseFiltered = FlyBaseUtils.getIdsMatchingDescription(new ArrayList<String>(reqDescriptions));
				} catch (SQLException e1) {
					log.error("Error getting flybase proteins matching description terms "+reqDescriptions, e1);
				}
				if(nrseqFlybaseFiltered != null) {
					filtered.addAll(nrseqFlybaseFiltered);
				}
				
		    	e = System.currentTimeMillis();
		    	log.info("Got filtered results for description from FLYBASE in "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");

				// remove this from the database list; We will not search the flybase descriptions in nrseq.
    			iter.remove();
    			break;
    		}
    	}
    	
    	s = System.currentTimeMillis();
    	List<Integer> filterFromStdDbs = FastaProteinLookupUtil.getInstance().getNrseqIdsForDescriptions(new ArrayList<String>(reqDescriptions), speciesDbList);
    	Collections.sort(filterFromStdDbs);
    	filtered.addAll(getMatching(nrseqIds, filterFromStdDbs));
    	e = System.currentTimeMillis();
    	log.info("Got filtered results for description from species-specific databases in "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");

        
    	if(useStandardDatabases) {
    		
    		s = System.currentTimeMillis();
    		for(int nrseqId: nrseqIds) {

    			if(filtered.contains(nrseqId)) // If this protein is already in the filtered list skip it
    				continue;
    			
    			// first look for matching descriptions in Swiss-Prot
    			s = System.currentTimeMillis();
    			boolean matches = hasDescriptionMatchInDatabase(reqDescriptions, nrseqId,
						StandardDatabase.SWISSPROT);
    			
    			e = System.currentTimeMillis();
    			
    			// Look in NCBI-NR if no match was found
    			if(!matches)
    				matches = hasDescriptionMatchInDatabase(reqDescriptions, nrseqId,
						StandardDatabase.NCBI_NR);


    			if(matches)
    				filtered.add(nrseqId);
    			
    			e = System.currentTimeMillis();
    	    	log.info("Got filtered results for description from Swiss-Prot and NCBI-NR in "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    		}
    	}
    	
    	// Finally return the intersection of the proteinIds given to us and the ones that pass the description filter 
    	List<Integer> filteredList = new ArrayList<Integer>(filtered);
    	Collections.sort(filteredList);
    	return getMatching(nrseqIds, filteredList);
    }
    
    
    /**
     * Returns a list of NRSEQ protein IDs that DO NOT match one or more of the given description terms
     * Descriptions in the fasta files associated with the protein inference are looked at first
     * Descriptions in any species-specific databases are also searches. Target species associated with the experiments
     * are used. 
     * @param pinferIds
     * @param nrseqIds
     * @param descriptions
     * @param databaseIds
     * @param useStandardDatabses
     * @return
     */
    public List<Integer> filterNrseqIdsByDescriptionNotLike(List<Integer> pinferIds, List<Integer> nrseqIds, 
    		String descriptions, boolean useStandardDatabses) {
        
    	List<Integer> toUnfilter = filterNrseqIdsByDescriptionLike(pinferIds, nrseqIds, 
    			descriptions, useStandardDatabses);
    	
    	if(toUnfilter.size() > 0) {
    		Collections.sort(toUnfilter);
    		return getNotMatching(nrseqIds, toUnfilter);
    	}
    	else
    		return nrseqIds;
    }
    
    
    private List<Integer> getSpeciesSpecificDatabases(int pinferId) {
		
		// get the species IDs for the experiments that were used to to this protein inference
        List<Integer> speciesIds = ProteinInferToSpeciesMapper.map(pinferId);
        // Get standard databases for these species, if any
        List<Integer> sdbList = new ArrayList<Integer>();
        String toPrint = "";
        for(int speciesId: speciesIds) {
        	
        	// FlyBase is not a good database for descriptions; we will search Swiss-Prot instead
        	// Searching Swiss-Prot is done for individual proteins, so we will not add it here
//        	if(speciesId == TaxonomyUtils.DROSOPHILA_MELANOGASTER)
//        		continue;
        	
        	StandardDatabase sdb = StandardDatabase.getStandardDatabaseForSpecies(speciesId);
        	if(sdb != null) {
        		NrDatabase nrDb = StandardDatabaseCache.getNrDatabase(sdb);
        		if(nrDb != null) {
        			sdbList.add(nrDb.getId());
        			toPrint += sdb.getDatabaseName()+", ";
        		}
        	}
        }
        log.info("Species specific databases for protein inference ID: "+pinferId+" are: "+toPrint);
        return sdbList;
	}
	
	private List<Integer> getSpeciesSpecificDatabases(List<Integer> pinferIds) {
		
		List<Integer> allDbs = new ArrayList<Integer>();
		for(int pinferId: pinferIds) {
			List<Integer> sdbList = getSpeciesSpecificDatabases(pinferId);
			for(int sdb: sdbList) {
				if(!allDbs.contains(sdb))
					allDbs.add(sdb);
			}
		}
		return allDbs;
	}

	private boolean hasDescriptionMatchInDatabase(Set<String> reqDescriptions, int nrseqId, StandardDatabase sdb) {
		
		NrDatabase nrdb = StandardDatabaseCache.getNrDatabase(sdb);
		return hasDescriptionMatchInDatabase(reqDescriptions, nrseqId, nrdb.getId());
	}

	private boolean hasDescriptionMatchInDatabase(Set<String> reqDescriptions,
			int nrseqId, int nrdbId) {
		for(String desc: reqDescriptions) {
			if(NrSeqLookupUtil.proteinMatchesDescriptionTerm(nrseqId, nrdbId, desc)) {
				return true;
			}
		}
		return false;
	}
	
    private List<Integer> getMatching(List<Integer> allIds, List<Integer> sortedMatchingIds) {
    	
    	List<Integer> matching = new ArrayList<Integer>();
        for(int nrseqId: allIds) {
            if(Collections.binarySearch(sortedMatchingIds, nrseqId) >= 0)
                matching.add(nrseqId);
        }
        return matching;
    }
    
    private List<Integer> getNotMatching(List<Integer> allIds, List<Integer> sortedMatchingIds) {
    	
    	List<Integer> nonMatching = new ArrayList<Integer>();
        for(int nrseqId: allIds) {
            if(Collections.binarySearch(sortedMatchingIds, nrseqId) < 0)
                nonMatching.add(nrseqId);
        }
        return nonMatching;
    }
    
}
