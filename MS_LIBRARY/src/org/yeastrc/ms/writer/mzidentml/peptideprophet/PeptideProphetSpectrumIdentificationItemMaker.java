/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml.peptideprophet;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.writer.mzidentml.MzidDataProviderException;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationItemType;
import org.yeastrc.ms.writer.mzidentml.jaxb.UserParamType;
import org.yeastrc.ms.writer.mzidentml.sequest.SequestSpectrumIdentificationItemMaker;

/**
 * PeptideProphetSpectrumIdentificationItemMaker.java
 * @author Vagisha Sharma
 * Aug 17, 2011
 * 
 */
public class PeptideProphetSpectrumIdentificationItemMaker {

	private final String filename;
	private final Program searchProgram;
	
	public PeptideProphetSpectrumIdentificationItemMaker(String filename, Program searchProgram) {
		this.filename = filename;
		this.searchProgram = searchProgram;
	}
	
	public SpectrumIdentificationItemType make(PeptideProphetResult result, int scanNumber, int resultIndex) 
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
		
		// add the PeptideProphet scores
		
		/*
		 [Term]
		 id: MS:1001143
		 name: search engine specific score for peptides
		 is_a: MS:1001105 ! peptide result details
		 */
		 
		UserParamType userParam = new UserParamType();
		userParam.setName("PeptideProphet_fval");
		userParam.setValue(String.valueOf(result.getfVal()));
		userParam.setType("xsd:float");
		specIdItem.getParamGroup().add(userParam);
		
		
		userParam = new UserParamType();
		userParam.setName("PeptideProphet_probability");
		userParam.setValue(String.valueOf(result.getProbability()));
		userParam.setType("xsd:float");
		specIdItem.getParamGroup().add(userParam);
		
		
		userParam = new UserParamType();
		userParam.setName("PeptideProphet_probability_net_0");
		userParam.setValue(String.valueOf(result.getProbabilityNet_0()));
		userParam.setType("xsd:float");
		specIdItem.getParamGroup().add(userParam);
		
		
		userParam = new UserParamType();
		userParam.setName("PeptideProphet_probability_net_1");
		userParam.setValue(String.valueOf(result.getProbabilityNet_1()));
		userParam.setType("xsd:float");
		specIdItem.getParamGroup().add(userParam);
		
		
		userParam = new UserParamType();
		userParam.setName("PeptideProphet_probability_net_2");
		userParam.setValue(String.valueOf(result.getProbabilityNet_2()));
		userParam.setType("xsd:float");
		specIdItem.getParamGroup().add(userParam);
		
		
		return specIdItem;
	}
}
