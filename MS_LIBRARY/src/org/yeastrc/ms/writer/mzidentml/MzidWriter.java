/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.yeastrc.ms.parser.unimod.UnimodRepository;
import org.yeastrc.ms.parser.unimod.UnimodRepositoryException;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisCollectionType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisProtocolCollectionType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CVListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CvType;
import org.yeastrc.ms.writer.mzidentml.jaxb.DBSequenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputSpectraType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputsType;
import org.yeastrc.ms.writer.mzidentml.jaxb.MzIdentMLType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideEvidenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ProteinAmbiguityGroupType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SearchDatabaseRefType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationProtocolType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationResultType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationType;

/**
 * MzidWriter.java
 * @author Vagisha Sharma
 * Aug 16, 2011
 * 
 */
public class MzidWriter {

	// controlled vocabularies
	private CvType psiCv;
	private CvType unimodCv;
	private CvType unitOntologyCv;
	
	private String outputFilePath = null;
	
	private BufferedWriter writer = null;
	private Marshaller marshaller = null;
	
	// unimod repository
	private UnimodRepository unimodRepository;
	
	private static final String ENCODING = "UTF-8";
	
	public MzidDataProvider dataProvider;
	
	public void setDataProvider(MzidDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public void setUnimodRepository(UnimodRepository repository) throws MzIdentMlWriterException {
		
		if(repository == null) {
			throw new MzIdentMlWriterException("Unimod repository cannot be null");
		}
		this.unimodRepository = repository;
	}
	
	private void initializeUnimodRepository() throws MzIdentMlWriterException {
		
		unimodRepository = new UnimodRepository();
		try {
			unimodRepository.initialize();
		} catch (UnimodRepositoryException e) {
			throw new MzIdentMlWriterException("There was an error initializing the Unimod repository", e);
		}
	}
	
	public void setOutputFilePath(String outputFilePath) throws MzIdentMlWriterException {
		
		this.outputFilePath = outputFilePath;
		
		try {
			writer = new BufferedWriter(new FileWriter(outputFilePath));
		} catch (IOException e) {
			throw new MzIdentMlWriterException("Error opening output file: "+outputFilePath, e);
		}
	}
	
	public void setWriter(BufferedWriter writer) throws MzIdentMlWriterException {
		
		if(writer == null) {
			throw new MzIdentMlWriterException("Writer was null");
		}
		else
			this.writer = writer;
	}
	
	private void initialize() throws MzIdentMlWriterException {
		
		// initialize the unimod repository for modification lookup
		if(this.unimodRepository == null) {
			initializeUnimodRepository();
		}
		
		dataProvider.setUnimodRepository(this.unimodRepository);
		
		try {
			dataProvider.initializeFieldsBeforeWrite();
		} catch (MzidDataProviderException e) {
			throw new MzIdentMlWriterException("Error initializing data provider", e);
		}
		
	}
	
	public void start() throws MzIdentMlWriterException {
		
		initialize();
		
		try {
			
			writer.write("<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>");
			writer.newLine();
			writer.write("<MzIdentML ");
			writer.newLine();
			writer.write("\txmlns=\"http://psidev.info/psi/pi/mzIdentML/1.1\"");
			writer.newLine();
			writer.write("\txmlns:psi-pi=\"http://psidev.info/psi/pi/mzIdentML/1.1\"");
			writer.newLine();
			writer.write("\txmlns:pf=\"http://psidev.info/fuge-light/1.1\"");
			writer.newLine();
			writer.write("\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
			writer.write("xsi:schemaLocation=\"http://psidev.info/psi/pi/mzIdentML/1.1 ../../schema/mzIdentML1.1.0.xsd\" ");
			writer.write("id=\""+this.outputFilePath+"\" ");
			writer.write("version=\"1.1.0\">");
			
			writer.newLine();
			
			
		} catch (IOException e) {
			throw new MzIdentMlWriterException("Error writing to file: "+outputFilePath, e);
		}
		
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(MzIdentMLType.class);
			
			marshaller = jc.createMarshaller();
	        marshaller.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
	        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	        marshaller.setProperty( Marshaller.JAXB_ENCODING, ENCODING);
	        
	        CVListType cvl = getCVlist();
	        marshaller.marshal(cvl, writer);
	        writer.newLine();
	        
	        AnalysisSoftwareListType swlist = dataProvider.getAnalysisSoftware();
	        marshaller.marshal(swlist, writer);
	        writer.newLine();
	        
	        writeSequenceCollection();
	        writer.newLine();
	        
	        writeAnalysisCollection();
	        writer.newLine();
	       
	        writeAnalysisProtocolCollection();
	        writer.newLine();
	        
	        writeDataCollection();
	        writer.newLine();
	        
	        
		} catch (JAXBException e) {
			throw new MzIdentMlWriterException("Error marshalling data", e);
		} 
		catch (IOException e) {
			throw new MzIdentMlWriterException("Error writing data", e);
		} catch (MzidDataProviderException e) {
			throw new MzIdentMlWriterException("Error getting data", e);
		}
	}

	private CVListType getCVlist(){

        CVListType cvListType = new CVListType();
        List<CvType> cvList = cvListType.getCv();
        
        psiCv = CvConstants.PSI_CV;
        cvList.add(psiCv);
        
        unitOntologyCv = CvConstants.UNIT_ONTOLOGY_CV;
        cvList.add(unitOntologyCv);
        
        unimodCv = CvConstants.UNIMOD_CV;
        cvList.add(unimodCv);
        
        return cvListType;
        
    }
	
	private void writeSequenceCollection() throws IOException, MzIdentMlWriterException {
		
		SequenceCollectionWriter seqCollWriter = new SequenceCollectionWriter();
		seqCollWriter.setWriter(writer);
		seqCollWriter.setMarshaller(marshaller);
		
        seqCollWriter.startCollection();
        writer.newLine();
        
        // write <DBSequence> elements
        DBSequenceType seq = null;
        try {
	        while((seq = dataProvider.getNextSequence()) != null) {
	        	try {
	        		seqCollWriter.addSequence(seq);
	        	}
	        	catch (JAXBException e) {
	            	throw new MzIdentMlWriterException("Error marshalling DBSequenceType", e);
	    		}
	        }
        } catch (MzidDataProviderException e1) {
			throw new MzIdentMlWriterException("Error getting data for DBSequenceType", e1);
		}
        
        // write <Peptide> elements
        PeptideType peptide = null;
        try {
			while((peptide = dataProvider.getNextPeptide()) != null) {
				try {
					seqCollWriter.addPeptide(peptide);
				}
				catch (JAXBException e) {
			    	throw new MzIdentMlWriterException("Error marshalling PeptideType", e);
				}
			}
		} catch (MzidDataProviderException e1) {
			throw new MzIdentMlWriterException("Error getting data for PeptideType", e1);
		}
        
        // write <PeptideEvidence> elements
		try {
			List<PeptideEvidenceType> peptideEvidenceList = dataProvider.getNextPeptideEvidenceSet();
		
			while(peptideEvidenceList != null) {
				try {

					for(PeptideEvidenceType peptideEvidence: peptideEvidenceList)
						seqCollWriter.addPeptideEvidence(peptideEvidence);
				}
				catch (JAXBException e) {
					throw new MzIdentMlWriterException("Error marshalling PeptideEvidenceType", e);
				}

				peptideEvidenceList = dataProvider.getNextPeptideEvidenceSet();
			}
		} catch (MzidDataProviderException e1) {
			throw new MzIdentMlWriterException("Error getting data for PeptideEvidenceType", e1);
		}
        
        seqCollWriter.endCollection();
	}

	private void writeAnalysisCollection() throws JAXBException {
		
		AnalysisCollectionType acType = getAnalysisCollection();
		
		marshaller.marshal(acType, writer);
	}
	
	private AnalysisCollectionType getAnalysisCollection() {
		
		AnalysisCollectionType acType = new AnalysisCollectionType();
		
		SpectrumIdentificationType specId = new SpectrumIdentificationType();
		specId.setId(dataProvider.getSpectrumIdentificationId());
		acType.getSpectrumIdentification().add(specId);
		
		specId.setSpectrumIdentificationListRef(dataProvider.getSpectrumIdentificationListId());
		
		specId.setSpectrumIdentificationProtocolRef(dataProvider.getSpectrumIdentificationProtocolId());
		
		List<InputSpectraType> inputSpectra = dataProvider.getInputSpectraList();
		specId.getInputSpectra().addAll(inputSpectra);
		
		SearchDatabaseRefType searchDbRef = new SearchDatabaseRefType();
		searchDbRef.setSearchDatabaseRef(dataProvider.getFastaFileName());
		specId.getSearchDatabaseRef().add(searchDbRef);
		
		return acType;
	}
	
	private void writeAnalysisProtocolCollection() throws MzidDataProviderException, JAXBException {
		
		AnalysisProtocolCollectionType protocolColl = new AnalysisProtocolCollectionType();
		
		SpectrumIdentificationProtocolType specIdProtocol = dataProvider.getSpectrumIdentificationProtocol();
		if(specIdProtocol != null) {
			protocolColl.getSpectrumIdentificationProtocol().add(specIdProtocol);
		}
		
		
		marshaller.marshal(protocolColl, writer);
	}

	private void writeDataCollection() throws IOException, MzIdentMlWriterException {
		
		writer.write("<DataCollection>");
		writer.newLine();
		
		writeInputs();
		writer.newLine();
		
		writeAnalysisData();
		writer.newLine();
		
		writer.write("</DataCollection>");
		writer.newLine();
	}

	private void writeInputs() throws MzIdentMlWriterException {
		
		InputsType inputs = null;
		try {
			inputs = dataProvider.getInputs();
		} catch (MzidDataProviderException e1) {
			throw new MzIdentMlWriterException("Error getting data for InputsType element", e1);
		}
		
		try {
			marshaller.marshal(inputs, writer);
		} catch (JAXBException e) {
			throw new MzIdentMlWriterException("Error marshalling InputsType element", e);
		}
	}

	private void writeAnalysisData() throws MzIdentMlWriterException, IOException {
		
		AnalysisDataWriter adataWriter = new AnalysisDataWriter();
		adataWriter.setMarshaller(marshaller);
		adataWriter.setWriter(writer);
		
		adataWriter.start();
		
		// start <SpectrumIdentificationList> element
		adataWriter.startSpectrumIdentificationList(dataProvider.getSpectrumIdentificationListId());
		
		SpectrumIdentificationResultType result = null;
		try {
			while((result = dataProvider.getNextSpectrumIdentificationResult()) != null) {
				
				if(result.getSpectrumIdentificationItem().size() > 0) {
					marshalElement(result);
				}
				
			}
		} catch (MzidDataProviderException e) {
			throw new MzIdentMlWriterException("Error getting data for SpectrumIdentificationResultType", e);
		}
		
		// end <SpectrumIdentificationList> element
		adataWriter.endSpectrumIdentificationList();
		
		// Write ProteinDetectionList, if available
		if(dataProvider.hasProteinAnalysis()) {
			
			// start <<ProteinDetectionList> element
			adataWriter.startProteinDetectionList("PDL");
			
			ProteinAmbiguityGroupType proteinGrp = null;
			
			try {
				while((proteinGrp = dataProvider.getNextProteinAmbiguityGroup()) != null) {
					
					marshalElement(proteinGrp);
				}
			} catch (MzidDataProviderException e) {
				throw new MzIdentMlWriterException("Error getting data for ProteinAmbiguityGroupType", e);
			}
			
			// end <<ProteinDetectionList> element
			adataWriter.endProteinDetectionList();
		}
		
		adataWriter.end();
	}


	public void end() throws MzIdentMlWriterException {
		
		try {
			writer.write("</MzIdentML>");
			writer.newLine();
		} catch (IOException e) {
			throw new MzIdentMlWriterException("Error writing to file", e);
		}
		finally {
			if(writer != null) try {writer.close();} catch(IOException e){}
		}
	}

	
	protected void marshalElement(Object element) throws MzIdentMlWriterException {
		
		try {
			marshaller.marshal(element, writer);
			writer.newLine();
		}
		catch (JAXBException e) {
	    	throw new MzIdentMlWriterException("Error marshalling "+element.getClass().getName(), e);
		}
		catch(IOException e) {
			throw new MzIdentMlWriterException("Error writing to file", e);
		}
	}
}
