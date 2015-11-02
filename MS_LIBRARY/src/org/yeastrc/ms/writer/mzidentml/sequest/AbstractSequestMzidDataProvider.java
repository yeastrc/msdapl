/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml.sequest;

import java.util.List;

import org.yeastrc.ms.parser.unimod.UnimodRepository;
import org.yeastrc.ms.writer.mzidentml.AnalysisSoftwareMaker;
import org.yeastrc.ms.writer.mzidentml.MzidConstants;
import org.yeastrc.ms.writer.mzidentml.MzidDataProvider;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareType;

/**
 * AbstractSequestMzidDataProvider.java
 * @author Vagisha Sharma
 * Aug 15, 2011
 * 
 */
public abstract class AbstractSequestMzidDataProvider implements MzidDataProvider {

	private UnimodRepository unimodRepository;
	
	public AnalysisSoftwareListType getAnalysisSoftware() {
		
		AnalysisSoftwareListType listType = new AnalysisSoftwareListType();
		
		List<AnalysisSoftwareType> swList = listType.getAnalysisSoftware();
		
		AnalysisSoftwareType sequestSw = new AnalysisSoftwareMaker().makeSequestAnalysisSoftware(this.getSequestVersion());
		swList.add(sequestSw);
		
		return listType;
	}

	public String getSpectrumIdentificationId() {
		return MzidConstants.SPEC_IDENT_ID_SEQ;
	}
	
	public String getSpectrumIdentificationListId() {
		return MzidConstants.SPEC_ID_LIST_ID;
	}
	
	public String getSpectrumIdentificationProtocolId() {
		return MzidConstants.SEQUEST_PROTOCOL_ID;
	}
	
	public void setUnimodRepository(UnimodRepository repository) {
		this.unimodRepository = repository;
	}
	
	UnimodRepository getUnimodRepository() {
		return this.unimodRepository;
	}

//	// --------------------------------------------------------------------------------------------
//	// Abstract methods
	abstract String getSequestVersion();
//	// --------------------------------------------------------------------------------------------
}
