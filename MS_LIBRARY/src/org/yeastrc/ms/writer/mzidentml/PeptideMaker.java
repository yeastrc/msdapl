/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.ms.parser.unimod.UnimodRepository;
import org.yeastrc.ms.parser.unimod.UnimodRepositoryException;
import org.yeastrc.ms.parser.unimod.jaxb.ModT;
import org.yeastrc.ms.writer.mzidentml.jaxb.CVParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ModificationType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideType;

/**
 * PeptideMaker.java
 * @author Vagisha Sharma
 * Aug 2, 2011
 * 
 */
public class PeptideMaker {

	private String id;
	private String sequence;
	private List<ModificationType> modifications;
	
	private final UnimodRepository unimodRepository;
	
	public PeptideMaker(UnimodRepository unimodRepository) {
		
		this.unimodRepository = unimodRepository;
		
		modifications = new ArrayList<ModificationType>();
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	public void addModification(int zero_based_index, double modMass) throws UnimodRepositoryException {
		
		ModificationType modType = new ModificationType();
		modType.setLocation(zero_based_index + 1);
		
		List<String> residues = modType.getResidues();
		residues.add( String.valueOf(sequence.charAt(zero_based_index)) );
		
		modType.setMonoisotopicMassDelta(modMass);
		
		// get the Unimod entry for this modification
		ModT unimod_mod = unimodRepository.getModification(sequence.charAt(zero_based_index), modMass, UnimodRepository.DELTA_MASS_TOLERANCE_1, true);
		
		if(unimod_mod != null) {
			
			CvParamMaker paramMaker = CvParamMaker.getInstance();
			CVParamType param = paramMaker.make("UNIMOD:" + unimod_mod.getRecordId(), unimod_mod.getTitle(), CvConstants.UNIMOD_CV);
			
			modType.getCvParam().add(param);
		}
		
		modifications.add(modType);
	}

	public PeptideType make() {
		
		if(StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("id given to PeptideMaker cannot be blank");
		}
		
		if(StringUtils.isBlank(sequence)) {
			throw new IllegalArgumentException("sequence given to PeptideMaker cannot be blank");
		}
		
		PeptideType peptide = new PeptideType();
		
		peptide.setId(id);
		peptide.setPeptideSequence(sequence);
		
		if(modifications.size() > 0) {
			
			List<ModificationType> modTypeList = peptide.getModification();
			
			for(ModificationType modType: modifications) {
				
				modTypeList.add(modType);
			}
		}
		
		return peptide;
	}
}
