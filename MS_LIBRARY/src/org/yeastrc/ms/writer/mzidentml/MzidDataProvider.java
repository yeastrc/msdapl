/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.util.List;

import org.yeastrc.ms.parser.unimod.UnimodRepository;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.DBSequenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputSpectraType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputsType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideEvidenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ProteinAmbiguityGroupType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationProtocolType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationResultType;

/**
 * MzidDataProvider.java
 * @author Vagisha Sharma
 * Aug 16, 2011
 * 
 */
public interface MzidDataProvider {

	public void setUnimodRepository(UnimodRepository repository);
	
	public void initializeFieldsBeforeWrite() throws MzidDataProviderException;
	
	public DBSequenceType getNextSequence() throws MzidDataProviderException;
	
	public PeptideType getNextPeptide() throws MzidDataProviderException;
	
	public List<PeptideEvidenceType> getNextPeptideEvidenceSet() throws MzidDataProviderException;
	
	public String getSpectrumIdentificationId();
	
	public String getSpectrumIdentificationListId();
	
	public String getSpectrumIdentificationProtocolId();
	
	public List<InputSpectraType> getInputSpectraList();
	
	public String getFastaFilePath();
	
	public String getFastaFileName();
	
	public AnalysisSoftwareListType getAnalysisSoftware() throws MzidDataProviderException;
	
	public SpectrumIdentificationProtocolType getSpectrumIdentificationProtocol() throws MzidDataProviderException;
	
	public InputsType getInputs() throws MzidDataProviderException;
	
	public SpectrumIdentificationResultType getNextSpectrumIdentificationResult() throws MzidDataProviderException;
	
	public boolean hasProteinAnalysis();
	
	public ProteinAmbiguityGroupType getNextProteinAmbiguityGroup() throws MzidDataProviderException;
	
}
