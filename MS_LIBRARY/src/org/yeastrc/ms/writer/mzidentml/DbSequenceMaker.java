/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.ms.writer.mzidentml.jaxb.AbstractParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CVParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.DBSequenceType;

/**
 * DbSequenceMaker.java
 * @author Vagisha Sharma
 * Aug 2, 2011
 * 
 */
public class DbSequenceMaker {

	private String accession;
	private String id;
	private String name;
	private String sequence;
	private String searchDatabase;
	private Set<String> descriptions;
	
	public DbSequenceMaker() {
		descriptions = new HashSet<String>();
	}
	
	public void setAccession(String accession) {
		this.accession = accession;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public void setSearchDatabase(String searchDatabase) {
		this.searchDatabase = searchDatabase;
	}

	public void addDescription(String description) {
		this.descriptions.add(description);
	}
	
	public DBSequenceType make() {
		
		if(StringUtils.isBlank(accession)) {
			throw new IllegalArgumentException("accession given to DbSequenceMaker cannot be blank");
		}
		
		if(StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("id given to DbSequenceMaker cannot be blank");
		}
		
		if(StringUtils.isBlank(searchDatabase)) {
			throw new IllegalArgumentException("search database reference given to DbSequenceMaker cannot be blank");
		}
		
		// required attributes
		DBSequenceType dbSeq = new DBSequenceType();
		dbSeq.setAccession(accession);
		dbSeq.setId(id);
		dbSeq.setSearchDatabaseRef(searchDatabase);
		
		// optional attributes
		if(!StringUtils.isBlank(name))
			dbSeq.setName(name);
		
		if(!StringUtils.isBlank(sequence)) {
			dbSeq.setLength(sequence.length());
			dbSeq.setSeq(sequence);
		}
		
		if(this.descriptions.size() > 0) {
			
			List<AbstractParamType> params = dbSeq.getParamGroup();
			
			for(String description: descriptions) {
				
				/*
					id: MS:1001088
					name: protein description
					def: "The protein description line from the sequence entry in the source database FASTA file." [PSI:PI]
					xref: value-type:xsd\:string "The allowed value-type for this CV term."
					is_a: MS:1001116 ! single protein result details
					is_a: MS:1001342 ! database sequence details
				 */
				CVParamType cvParam = CvParamMaker.getInstance().make("MS:1001088", "protein description", description, CvConstants.PSI_CV);
				
				params.add(cvParam);
			}
			
		}
        
		return dbSeq;
	}
}
