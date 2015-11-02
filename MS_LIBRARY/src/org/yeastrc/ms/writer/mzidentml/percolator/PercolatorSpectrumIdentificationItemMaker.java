/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml.percolator;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.writer.mzidentml.CvConstants;
import org.yeastrc.ms.writer.mzidentml.CvParamMaker;
import org.yeastrc.ms.writer.mzidentml.MzidDataProviderException;
import org.yeastrc.ms.writer.mzidentml.jaxb.CVParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationItemType;
import org.yeastrc.ms.writer.mzidentml.sequest.SequestSpectrumIdentificationItemMaker;

/**
 * PercolatorSpectrumIdentificationItemMaker.java
 * @author Vagisha Sharma
 * Aug 17, 2011
 * 
 */
public class PercolatorSpectrumIdentificationItemMaker {

	private final String filename;
	private final Program searchProgram;
	
	public PercolatorSpectrumIdentificationItemMaker(String filename, Program searchProgram) {
		this.filename = filename;
		this.searchProgram = searchProgram;
	}
	
	public SpectrumIdentificationItemType make(PercolatorResult result, int scanNumber, int resultIndex) 
		throws ModifiedSequenceBuilderException, MzidDataProviderException {
		
		
		SpectrumIdentificationItemType specIdItem;
		
		if(Program.isSequest(this.searchProgram)) {
			
			SequestSearchResult seqResult = DAOFactory.instance().getSequestResultDAO().load(result.getId());
			
			SequestSpectrumIdentificationItemMaker seqIdMaker = new SequestSpectrumIdentificationItemMaker(this.filename);
			
			specIdItem = seqIdMaker.make(seqResult, scanNumber, resultIndex);
			
		}
		else {
			
			throw new MzidDataProviderException("Cannot build SpectrumIdentificationType for search program: "+searchProgram);
		}
		
		// add the Percolator scores
		
		// 1. psm qvalue
		/*
		 	id: MS:1001491
			name: percolator:Q value
			def: "percolator:Q value." [PSI:PI]
			xref: value-type:xsd\:double "The allowed value-type for this CV term."
			is_a: MS:1001143 ! search engine specific score for peptides
			is_a: MS:1001153 ! search engine specific score

		 */
		CVParamType param = CvParamMaker.getInstance().make("MS:1001491", "percolator:Q value", 
				String.valueOf(result.getQvalue()), CvConstants.PSI_CV);
		specIdItem.getParamGroup().add(param);
		
		// 2. psm PEP
		/*
		 	id: MS:1001493
			name: percolaror:PEP
			def: "Posterior error probability." [PSI:PI]
			xref: value-type:xsd\:double "The allowed value-type for this CV term."
			is_a: MS:1001143 ! search engine specific score for peptides
			is_a: MS:1001153 ! search engine specific score
		 */
		param = CvParamMaker.getInstance().make("MS:1001493", "percolaror:PEP", 
				String.valueOf(result.getPosteriorErrorProbability()), CvConstants.PSI_CV);
		specIdItem.getParamGroup().add(param);
		
		return specIdItem;
	}
}
