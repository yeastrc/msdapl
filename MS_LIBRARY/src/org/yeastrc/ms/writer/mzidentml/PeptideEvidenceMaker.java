/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideEvidenceType;

/**
 * PeptideEvidenceMaker.java
 * @author Vagisha Sharma
 * Aug 2, 2011
 * 
 */
public class PeptideEvidenceMaker {

	private String id;
	private String preResidue;
	private String postResidue;
	private String dbSequence_ref;
	private String peptide_ref;
	
	public void setId(String id) {
		this.id = id;
	}

	public void setPreResidue(String preResidue) {
		this.preResidue = preResidue;
	}

	public void setPostResidue(String postResidue) {
		this.postResidue = postResidue;
	}

	public void setDbSequence_ref(String dbSequenceRef) {
		dbSequence_ref = dbSequenceRef;
	}

	public void setPeptide_ref(String peptideRef) {
		peptide_ref = peptideRef;
	}

	public PeptideEvidenceType make() {
		
		if(StringUtils.isBlank(dbSequence_ref)) {
			throw new IllegalArgumentException("dbSequence_ref given to PeptideEvidenceMaker cannot be blank");
		}
		
		if(StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("id given to PeptideEvidenceMaker cannot be blank");
		}
		
		if(StringUtils.isBlank(peptide_ref)) {
			throw new IllegalArgumentException("peptide_ref given to PeptideEvidenceMaker cannot be blank");
		}
		
		PeptideEvidenceType pev = new PeptideEvidenceType();
		pev.setId(id);
		pev.setDBSequenceRef(dbSequence_ref);
		pev.setPeptideRef(peptide_ref);
		pev.setPre(preResidue);
		pev.setPost(postResidue);
		
		return pev;
	}
}
