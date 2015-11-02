/**
 * FastaInMemorySuffixCreator.java
 * @author Vagisha Sharma
 * Oct 1, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.database.fasta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrDbProteinFull;

public class FastaInMemorySuffixCreator {

	public static final int SUFFIX_LENGTH = 4;
	
    private Map<String, List<Integer>> suffixMap;
    
    private static final Logger log = Logger.getLogger(FastaInMemorySuffixCreator.class.getName());
    
    
    public Map<String, List<Integer>> buildInMemorySuffixes(int databaseId) {
        
    	log.info("Building suffix map in memory");
    	
        suffixMap = new HashMap<String, List<Integer>>(3200000);
        
        // get all the ids from tblProteinDatabase for the given databaseID
        List<Integer> dbProteinIds = NrSeqLookupUtil.getDbProteinIdsForDatabase(databaseId);
        log.info("# proteins: "+dbProteinIds.size()+" in databaseID: "+databaseId);
        
        if ( dbProteinIds.isEmpty() ) {
        	
        	log.error( "ERROR:  No proteins loaded from tblProteinDatabase for databaseID: " + databaseId );
        }
        
        // some proteins in a fasta file have the same sequence.  We will not create suffixes twice
        Set<Integer> seenSequenceIds = new HashSet<Integer>(dbProteinIds.size());
        
        long s = System.currentTimeMillis();
        
        int cnt = 0;
        for(int dbProteinId: dbProteinIds) {
            NrDbProteinFull protein = NrSeqLookupUtil.getDbProteinFull(dbProteinId);
            
//            if(seenSequenceIds.contains(protein.getSequenceId()))
//                continue;
//            else
            seenSequenceIds.add(protein.getSequenceId());
            
            String sequence = NrSeqLookupUtil.getProteinSequenceForNrSeqDbProtId(dbProteinId);
            
            cnt++;
            if(seenSequenceIds.size() % 1000 == 0) {
                log.info("# sequences seen: "+ seenSequenceIds.size()+"; Number of db proteins seen: "+cnt);
            }
            createSuffixes(sequence, protein.getSequenceId(), dbProteinId);
            
        }
        log.info("# sequences seen: "+ seenSequenceIds.size()+"; Number of db proteins seen: "+cnt);
        
        long e = System.currentTimeMillis();
        
        log.info("Created suffix map with "+suffixMap.size()+" entries");
        log.info("Total time to create "+SUFFIX_LENGTH+"-mer suffix map for databaseID: "+databaseId+" was "
                +TimeUtils.timeElapsedSeconds(s, e)+"\n\n");
        
        return suffixMap;
    }
    
    private void createSuffixes(String sequence, int sequenceId, int dbProteinId) {
        
        sequence = format(sequence); // removes any '*' and replaces 'I' and 'L' with '1'
        
        Integer idObj = Integer.valueOf(dbProteinId);
        for(int i = 0; i < sequence.length(); i++) {
            int end = Math.min(i+SUFFIX_LENGTH, sequence.length());
            String subseq = sequence.substring(i, end);
            
            List<Integer> matchingProteins = suffixMap.get(subseq);
            if(matchingProteins == null) {
                matchingProteins = new ArrayList<Integer>();
                suffixMap.put(subseq, matchingProteins);
            }
            matchingProteins.add(idObj);
            
            if(i+SUFFIX_LENGTH >= sequence.length())
                break;
        }
    }
    
    /**
     * Removes '*' characters from the sequence and replaces 'L' and 'I' with '1'. 
     * @param sequence
     * @return
     */
    public static String format(String sequence) {
    	
    	// Remove any '*' characters from the sequence
    	sequence = removeAsterisks(sequence);
    	sequence = doIAndLSubstitution(sequence);
    	
    	return sequence;
   
    }

	public static String removeAsterisks(String sequence) {
		return sequence.replaceAll("\\*", "");
	}

	public static String doIAndLSubstitution(String sequence) {
		// Replace all 'L' with '1'
    	sequence = sequence.replaceAll("L", "1");
        // Replace all "I" with "1"
    	sequence = sequence.replaceAll("I", "1");
		return sequence;
	}
}
