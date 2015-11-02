/**
 * ProteinPropertiesBuilder.java
 * @author Vagisha Sharma
 * Mar 22, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.domain.protinfer.GenericProteinferProtein;
import org.yeastrc.ms.util.ProteinUtils;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrDbProtein;

/**
 * 
 */
public class ProteinPropertiesBuilder {

	private boolean getPi = false;
	private boolean getMolWt = false;
	private boolean getAccession = false;
	private List<Integer> fastaDbIds;
	
	public void setGetPi(boolean getPi) {
		this.getPi = getPi;
	}
	public void setGetMolWt(boolean getMolWt) {
		this.getMolWt = getMolWt;
	}
	public void setGetAccession(boolean getAccession) {
		this.getAccession = getAccession;
	}
	public void setFastaDbIds(List<Integer> fastaDbIds) {
		this.fastaDbIds = fastaDbIds;
	}

	public ProteinProperties build(int pinferId, GenericProteinferProtein<?> protein) {
		
		String sequence = NrSeqLookupUtil.getProteinSequence(protein.getNrseqProteinId());

		ProteinProperties props = new ProteinProperties(protein.getId());
		props.setNrseqId(protein.getNrseqProteinId());
		if(this.getMolWt || this.getPi) {
			props.setMolecularWt(ProteinUtils.calculateMolWt(sequence));
			props.setPi(ProteinUtils.calculatePi(sequence));
		}
		if(this.getAccession) {
			List<NrDbProtein> matchingProteins = NrSeqLookupUtil.getDbProteins(protein.getNrseqProteinId(), fastaDbIds);
			Set<String> accessions = new HashSet<String>();
			for(NrDbProtein prot: matchingProteins)
				accessions.add(prot.getAccessionString());
			props.setAccession(accessions);
		}
		return props;
	}

	public void update(ProteinProperties properties) {
		
		String sequence = NrSeqLookupUtil.getProteinSequence(properties.getNrseqId());

		if(this.getMolWt || this.getPi) {
			properties.setMolecularWt(ProteinUtils.calculateMolWt(sequence));
			properties.setPi(ProteinUtils.calculatePi(sequence));
		}
		if(this.getAccession) {
			List<NrDbProtein> matchingProteins = NrSeqLookupUtil.getDbProteins(properties.getNrseqId(), fastaDbIds);
        	Set<String> accessions = new HashSet<String>();
        	for(NrDbProtein prot: matchingProteins)
        		accessions.add(prot.getAccessionString());
        	properties.setAccession(accessions);
		}
	}
}
