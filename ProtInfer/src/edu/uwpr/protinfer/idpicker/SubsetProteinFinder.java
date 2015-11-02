/**
 * SubsetProteinFinder.java
 * @author Vagisha Sharma
 * Oct 18, 2010
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.SpectrumMatch;

/**
 * 
 */
public class SubsetProteinFinder {

	private static final Logger log = Logger.getLogger(SubsetProteinFinder.class);
	
	public void markSubsetProteins(List<? extends InferredProtein<? extends SpectrumMatch>> inputProteinList) throws SubsetProteinFinderException {
		
		
		// Make a copy of the list; we will be sorting this list
		List<InferredProtein<? extends SpectrumMatch>> proteinList = new ArrayList<InferredProtein<? extends SpectrumMatch>>(inputProteinList.size());
		proteinList.addAll(inputProteinList);
				
		Set<String> peptides = new HashSet<String>();
		
		// sort the list by cluster ID; We will look at one cluster at a time;
		Collections.sort(proteinList, new Comparator<InferredProtein<? extends SpectrumMatch>>() {
			@Override
			public int compare(InferredProtein<? extends SpectrumMatch> o1, InferredProtein<? extends SpectrumMatch> o2) {
				return Integer.valueOf(o1.getProteinClusterLabel()).compareTo(o2.getProteinClusterLabel());
			}
		});
		
		
		int clusterLabel = -1;
		
		List<InferredProtein<? extends SpectrumMatch>> clusterProteins = new ArrayList<InferredProtein<? extends SpectrumMatch>>();
		Set<String> clusterPeptides = new HashSet<String>();
		
		Map<Integer, Set<Integer>> subsetSuperProteinGroupIdMap = new HashMap<Integer, Set<Integer>>();
		
		for(InferredProtein<? extends SpectrumMatch> iProtein: proteinList) {
			if(iProtein.getProteinClusterLabel() != clusterLabel) {
				
				if(clusterLabel != -1) {
					
					// make sure that the peptides in this cluster were unique to this cluster
					for(String peptide: clusterPeptides) {
						if(peptides.contains(peptide)) {
							throw new SubsetProteinFinderException("Petide "+peptide+" in cluster "+clusterLabel+" also found in another cluster");
						}
						peptides.add(peptide);
					}
					Map<Integer, Set<Integer>> mapForCluster = getSubsetSuperProteinGroupIdMap(clusterProteins);
					for(Integer subsetGroupId: mapForCluster.keySet()) {
						if(subsetSuperProteinGroupIdMap.containsKey(subsetGroupId))
							throw new SubsetProteinFinderException("Group ID: "+subsetGroupId+" seen in multiple clusters");
						
						subsetSuperProteinGroupIdMap.put(subsetGroupId, mapForCluster.get(subsetGroupId));
					}
				}
				clusterProteins.clear();
				clusterPeptides.clear();
				clusterProteins = new ArrayList<InferredProtein<? extends SpectrumMatch>>();
				clusterPeptides = new HashSet<String>();
				clusterLabel = iProtein.getProteinClusterLabel();
			}
			clusterProteins.add(iProtein);

			for(PeptideEvidence<? extends SpectrumMatch> pev: iProtein.getPeptides()) {
				clusterPeptides.add(pev.getPeptide().getPeptideSequence());
			}
		}
		
		// last one
		// make sure that the peptides in this cluster were unique to this cluster
		for(String peptide: clusterPeptides) {
			if(peptides.contains(peptide)) {
				throw new SubsetProteinFinderException("Petide "+peptide+" in cluster "+clusterLabel+" also found in another cluster");
			}
			peptides.add(peptide);
		}
		Map<Integer, Set<Integer>> mapForCluster = getSubsetSuperProteinGroupIdMap(clusterProteins);
		for(Integer subsetGroupId: mapForCluster.keySet()) {
			if(subsetSuperProteinGroupIdMap.containsKey(subsetGroupId))
				throw new SubsetProteinFinderException("Group ID: "+subsetGroupId+" seen in multiple clusters");
			subsetSuperProteinGroupIdMap.put(subsetGroupId, mapForCluster.get(subsetGroupId));
		}
		
		// mark the subset proteins
		for(InferredProtein<? extends SpectrumMatch> protein: inputProteinList) {
			Set<Integer> superProteinGroupLabels = subsetSuperProteinGroupIdMap.get(protein.getProteinGroupLabel());
			if(superProteinGroupLabels != null && superProteinGroupLabels.size() > 0) {
				protein.getProtein().setSubset(true);
				protein.getProtein().setSuperProteinGroupLabels(new ArrayList<Integer>(superProteinGroupLabels));
			}
		}
	}

	/*
	 * Keys in the returned Map are group IDs of subset proteins; the values are group IDs or corresponding 
	 * super proteins. 
	 * @param clusterProteins
	 * @return
	 */
	private Map<Integer, Set<Integer>> getSubsetSuperProteinGroupIdMap(List<InferredProtein<? extends SpectrumMatch>> clusterProteins) 
			throws SubsetProteinFinderException {
		
		
		// get one representative from each indistinguishable protein group
		List<InferredProtein<? extends SpectrumMatch>> sparseClusterProteins = new ArrayList<InferredProtein<? extends SpectrumMatch>>();
		
		
		Set<Integer> groupIds = new HashSet<Integer>();
		for(InferredProtein<? extends SpectrumMatch> iProtein: clusterProteins) {
			
			if(groupIds.contains(iProtein.getProteinGroupLabel()))
				continue;
			
			groupIds.add(iProtein.getProteinGroupLabel());
			sparseClusterProteins.add(iProtein);
		}
		
		
		// Look for proteins whose peptides are a subset of peptides of another protein
		
		// key in subsetSuperGroupIdMap is the group label of a subset protein
		// values are a set of group labels of the super proteins.
		Map<Integer, Set<Integer>> subsetSuperGroupIdMap = new HashMap<Integer, Set<Integer>>();
		for(int i = 0; i < sparseClusterProteins.size(); i++) {
			
			for(int j = 0; j < sparseClusterProteins.size(); j++) {
				
				if(i == j)
					continue;
				
				InferredProtein<? extends SpectrumMatch> protein_i = sparseClusterProteins.get(i);
				InferredProtein<? extends SpectrumMatch> protein_j = sparseClusterProteins.get(j);
				
				// If protein_i is a subset of protein_j
				if(isSubset(protein_i, protein_j)) {
					Set<Integer> superProteinGroupIds = subsetSuperGroupIdMap.get(protein_i.getProteinGroupLabel());
					if(superProteinGroupIds == null) {
						superProteinGroupIds = new HashSet<Integer>();
						subsetSuperGroupIdMap.put(protein_i.getProteinGroupLabel(), superProteinGroupIds);
					}
					superProteinGroupIds.add(protein_j.getProteinGroupLabel());
				}
			}		
		}
		
		// remove group labels that have been added as super proteins but are themselves subset proteins
		Set<Integer> subsetProteinGrpLabels = subsetSuperGroupIdMap.keySet();
		for(Set<Integer> superProteinGrpLabels: subsetSuperGroupIdMap.values()) {
			superProteinGrpLabels.removeAll(subsetProteinGrpLabels);
		}
		
		return subsetSuperGroupIdMap;
	}

	// Returns true if protein_i is a subset protein of protein_j
	private boolean isSubset(InferredProtein<? extends SpectrumMatch> protein_i,
			InferredProtein<? extends SpectrumMatch> protein_j) throws SubsetProteinFinderException {
		
		Set<String> peptides_i = new HashSet<String>();
		for(PeptideEvidence<? extends SpectrumMatch> pev: protein_i.getPeptides()) {
			peptides_i.add(pev.getPeptide().getPeptideSequence());
		}
		
		Set<String> peptides_j = new HashSet<String>();
		for(PeptideEvidence<? extends SpectrumMatch> pev: protein_j.getPeptides()) {
			peptides_j.add(pev.getPeptide().getPeptideSequence());
		}
		
		if(peptides_j.containsAll(peptides_i)) {
			if(peptides_j.size() == peptides_i.size()) {
				
				log.error("Same set of peptides found for protein_i ("+protein_i.getProteinId()+","+protein_i.getAccession()+") and protein_j ("+
						+protein_j.getProteinId()+","+protein_j.getAccession()+")");
				log.error("Peptides are: "+org.yeastrc.ms.util.StringUtils.makeCommaSeparated(peptides_i));
				
				throw new SubsetProteinFinderException("Proteins "+protein_i.getProteinId()+", and "+protein_j.getProteinId()+
						" are in different groups but have the same set of peptides");
			}
			return true;
		}
		else
			return false;
	}
}
