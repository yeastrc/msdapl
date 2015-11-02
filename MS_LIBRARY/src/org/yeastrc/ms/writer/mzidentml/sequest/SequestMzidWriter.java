/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml.sequest;

import java.io.IOException;
import java.util.List;

import org.yeastrc.ms.writer.mzidentml.AnalysisDataWriter;
import org.yeastrc.ms.writer.mzidentml.AnalysisSoftwareMaker;
import org.yeastrc.ms.writer.mzidentml.MzIdentMlWriterException;
import org.yeastrc.ms.writer.mzidentml.MzidConstants;
import org.yeastrc.ms.writer.mzidentml.MzidWriter;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationResultType;

/**
 * SequestMzidWriter.java
 * @author Vagisha Sharma
 * Aug 15, 2011
 * 
 */
public abstract class SequestMzidWriter extends MzidWriter {

	
	AnalysisSoftwareListType getAnalysisSoftware() {
		
		AnalysisSoftwareListType listType = new AnalysisSoftwareListType();
		
		List<AnalysisSoftwareType> swList = listType.getAnalysisSoftware();
		
		AnalysisSoftwareType sequestSw = new AnalysisSoftwareMaker().makeSequestAnalysisSoftware(this.getSequestVersion());
		swList.add(sequestSw);
		
		return listType;
	}

	String getSpectrumIdentificationId() {
		return MzidConstants.SPEC_IDENT_ID_SEQ;
	}
	
	String getSpectrumIdentificationListId() {
		return MzidConstants.SPEC_ID_LIST_ID;
	}
	
	String getSpectrumIdentificationProtocolId() {
		return MzidConstants.SEQUEST_PROTOCOL_ID;
	}
	
	void writeAnalysisResults(AnalysisDataWriter adataWriter) throws MzIdentMlWriterException, IOException {
		
		adataWriter.startSpectrumIdentificationList(getSpectrumIdentificationListId());
		
		writeSearchResults();
		
		adataWriter.endSpectrumIdentificationList();
		
	}

	void writeSearchResult(SpectrumIdentificationResultType result)
			throws MzIdentMlWriterException {
		
		if(result.getSpectrumIdentificationItem().size() > 0) {
			super.marshalElement(result);
		}
	}
	

//	// --------------------------------------------------------------------------------------------
//	// Abstract methods
	abstract String getSequestVersion();
	
	abstract void writeSearchResults() throws MzIdentMlWriterException;
//	// --------------------------------------------------------------------------------------------
}
