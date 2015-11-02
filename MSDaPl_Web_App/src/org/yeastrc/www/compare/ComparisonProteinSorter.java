/**
 * ProteinDatasetSorter.java
 * @author Vagisha Sharma
 * Apr 18, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.ms.util.ProteinUtils;
import org.yeastrc.nr_seq.listing.ProteinListing;
import org.yeastrc.nr_seq.listing.ProteinListingBuilder;
import org.yeastrc.nr_seq.listing.ProteinReference;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;

/**
 * 
 */
public class ComparisonProteinSorter {

    private static ComparisonProteinSorter instance;
    private ComparisonProteinSorter() {}
    
    public static ComparisonProteinSorter getInstance() {
        if(instance == null)
            instance = new ComparisonProteinSorter();
        return instance;
    }
    
    
    //-------------------------------------------------------------------------------------------------
    // SORT BY PEPTIDE COUNT
    //-------------------------------------------------------------------------------------------------
    public void sortByPeptideCount( List<ComparisonProtein> proteins, SORT_ORDER sortOrder) throws SQLException {
        for(ComparisonProtein protein: proteins) {
         // get the (max)number of peptides identified for this protein
            protein.setTotalPeptideSeqCount(DatasetPeptideComparer.instance().getTotalPeptSeqForProtein(protein));
        }
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteins, new PeptideCountCompartorDesc());
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteins, new PeptideCountCompartorAsc());
    }
    
    public void sortGroupsByPeptideCount(List<ComparisonProteinGroup> proteinGroups, SORT_ORDER sortOrder) throws SQLException {

        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteinGroups, new Comparator<ComparisonProteinGroup>() {
                @Override
                public int compare(ComparisonProteinGroup o1,
                        ComparisonProteinGroup o2) {
                    return Integer.valueOf(o2.getTotalPeptideSeqCount()).compareTo(o1.getTotalPeptideSeqCount());
                }});
        
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteinGroups, new Comparator<ComparisonProteinGroup>() {
                @Override
                public int compare(ComparisonProteinGroup o1,
                        ComparisonProteinGroup o2) {
                    return Integer.valueOf(o1.getTotalPeptideSeqCount()).compareTo(o2.getTotalPeptideSeqCount());
                }});
    }
    
    //-------------------------------------------------------------------------------------------------
    // SORT BY MOLECULAR WT.
    //-------------------------------------------------------------------------------------------------
    public void sortByMolecularWeight(List<ComparisonProtein> proteins, SORT_ORDER sortOrder) {
        for(ComparisonProtein protein: proteins) {
            if(protein.molWtAndPiSet())
                continue;
            // get the protein properties
            String sequence = NrSeqLookupUtil.getProteinSequence(protein.getNrseqId());
            protein.setMolecularWeight( (float) (Math.round(ProteinUtils.calculateMolWt(sequence)*100) / 100.0));
            protein.setPi((float) (Math.round(ProteinUtils.calculatePi(sequence)*100) / 100.0));
        }
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteins, new MolWtCompartorDesc());
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteins, new MolWtCompartorAsc());
    }
    
    public void sortGroupsByMolecularWeight(List<ComparisonProteinGroup> proteinGroups, SORT_ORDER sortOrder) {
        
        for(ComparisonProteinGroup proteinGroup: proteinGroups) {
            // get the protein properties
            sortByMolecularWeight(proteinGroup.getProteins(), sortOrder);
        }
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteinGroups, new MolWtCompartorGroupDesc());
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteinGroups, new MolWtCompartorGroupAsc());
    }
    
    //-------------------------------------------------------------------------------------------------
    // SORT BY pI
    //-------------------------------------------------------------------------------------------------
    public void sortByPi(List<ComparisonProtein> proteins, SORT_ORDER sortOrder) {
        for(ComparisonProtein protein: proteins) {
            if(protein.molWtAndPiSet())
                continue;
            // get the protein properties
            String sequence = NrSeqLookupUtil.getProteinSequence(protein.getNrseqId());
            protein.setMolecularWeight( (float) (Math.round(ProteinUtils.calculateMolWt(sequence)*100) / 100.0));
            protein.setPi((float) (Math.round(ProteinUtils.calculatePi(sequence)*100) / 100.0));
        }
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteins, new PiCompartorDesc());
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteins, new PiCompartorAsc());
    }
    
    public void sortGroupsByPi(List<ComparisonProteinGroup> proteinGroups, SORT_ORDER sortOrder) {
        
        for(ComparisonProteinGroup proteinGroup: proteinGroups) {
            // get the protein properties
            sortByPi(proteinGroup.getProteins(), sortOrder);
        }
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteinGroups, new PiCompartorGroupDesc());
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteinGroups, new PiCompartorGroupAsc());
    }
    
    //-------------------------------------------------------------------------------------------------
    // SORT BY Fasta Accession
    //-------------------------------------------------------------------------------------------------
    public void sortByAccession(List<ComparisonProtein> proteins, SORT_ORDER sortOrder, List<Integer> fastaDatabaseIds) {
    	
    	ProteinListingBuilder listingBuilder = ProteinListingBuilder.getInstance();
    	
        for(ComparisonProtein protein: proteins) {
            if(protein.getProteinListing() != null)
                continue;
            // build a dummy protein listing just for sorting
            ProteinListing listing = new ProteinListing(null);
            listingBuilder.getFastaReferences(fastaDatabaseIds, protein.getNrseqId(), listing);
            // If there are multiple references sort them 
            List<ProteinReference> fastaRefs = listing.getFastaReferences();
            
            if(sortOrder == SORT_ORDER.ASC) {
            	Collections.sort(fastaRefs, new Comparator<ProteinReference>() {
            		@Override
            		public int compare(ProteinReference o1, ProteinReference o2) {
            			return o1.getAccession().compareTo(o2.getAccession());
            		}
            	});
            }
            else {
            	Collections.sort(fastaRefs, new Comparator<ProteinReference>() {
            		@Override
            		public int compare(ProteinReference o1, ProteinReference o2) {
            			return o2.getAccession().compareTo(o1.getAccession());
            		}
            	});
            }
            protein.setProteinListing(listing);
        }
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteins, new AccessionCompartorDesc());
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteins, new AccessionCompartorAsc());
    }
    
    public void sortGroupsByAccession(List<ComparisonProteinGroup> proteinGroups, SORT_ORDER sortOrder, 
    			List<Integer> fastaDatabaseIds) {
        
        for(ComparisonProteinGroup proteinGroup: proteinGroups) {
            // get the protein properties
            sortByAccession(proteinGroup.getProteins(), sortOrder, fastaDatabaseIds);
        }
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteinGroups, new AccessionCompartorGroupDesc());
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteinGroups, new AccessionCompartorGroupAsc());
    }
    
    
    private static class PeptideCountCompartorDesc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return Integer.valueOf(o2.getTotalPeptideSeqCount()).compareTo(o1.getTotalPeptideSeqCount());
        }
    }
    
    private static class PeptideCountCompartorAsc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return Integer.valueOf(o1.getTotalPeptideSeqCount()).compareTo(o2.getTotalPeptideSeqCount());
        }
    }
    
    private static class MolWtCompartorDesc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return Float.valueOf(o2.getMolecularWeight()).compareTo(o1.getMolecularWeight());
        }
    }
    
    private static class MolWtCompartorAsc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return Float.valueOf(o1.getMolecularWeight()).compareTo(o2.getMolecularWeight());
        }
    }
    
    private static class MolWtCompartorGroupDesc implements Comparator<ComparisonProteinGroup> {
        @Override
        public int compare(ComparisonProteinGroup o1, ComparisonProteinGroup o2) {
            return Float.valueOf(o2.getProteins().get(0).getMolecularWeight()).compareTo(o1.getProteins().get(0).getMolecularWeight());
        }
    }
    
    private static class MolWtCompartorGroupAsc implements Comparator<ComparisonProteinGroup> {
        @Override
        public int compare(ComparisonProteinGroup o1, ComparisonProteinGroup o2) {
            return Float.valueOf(o1.getProteins().get(0).getMolecularWeight()).compareTo(o2.getProteins().get(0).getMolecularWeight());
        }
    }
    
    private static class PiCompartorDesc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return Float.valueOf(o2.getPi()).compareTo(o1.getPi());
        }
    }
    
    private static class PiCompartorAsc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return Float.valueOf(o1.getPi()).compareTo(o2.getPi());
        }
    }
    
    private static class PiCompartorGroupDesc implements Comparator<ComparisonProteinGroup> {
        @Override
        public int compare(ComparisonProteinGroup o1, ComparisonProteinGroup o2) {
            return Float.valueOf(o2.getProteins().get(0).getPi()).compareTo(o1.getProteins().get(0).getPi());
        }
    }
    
    private static class PiCompartorGroupAsc implements Comparator<ComparisonProteinGroup> {
        @Override
        public int compare(ComparisonProteinGroup o1, ComparisonProteinGroup o2) {
            return Float.valueOf(o1.getProteins().get(0).getPi()).compareTo(o2.getProteins().get(0).getPi());
        }
    }
    
    private static class AccessionCompartorDesc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return o1.getProteinListing().getFastaAccessions().get(0)
            .compareTo(o2.getProteinListing().getFastaAccessions().get(0));
        }
    }
    
    private static class AccessionCompartorAsc implements Comparator<ComparisonProtein> {
    	@Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return o2.getProteinListing().getFastaAccessions().get(0)
            .compareTo(o1.getProteinListing().getFastaAccessions().get(0));
        }
    }
    
    private static class AccessionCompartorGroupDesc implements Comparator<ComparisonProteinGroup> {
        @Override
        public int compare(ComparisonProteinGroup o1, ComparisonProteinGroup o2) {
            return o2.getProteins().get(0).getProteinListing().getFastaAccessions().get(0)
            	.compareTo(o1.getProteins().get(0).getProteinListing().getFastaAccessions().get(0));
        }
    }
    
    private static class AccessionCompartorGroupAsc implements Comparator<ComparisonProteinGroup> {
        @Override
        public int compare(ComparisonProteinGroup o1, ComparisonProteinGroup o2) {
        	return o1.getProteins().get(0).getProteinListing().getFastaAccessions().get(0)
        	.compareTo(o2.getProteins().get(0).getProteinListing().getFastaAccessions().get(0));
        }
    }
}
