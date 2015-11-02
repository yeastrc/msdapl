/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml.sequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParser;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;
import org.yeastrc.ms.parser.unimod.UnimodRepositoryException;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.writer.mzidentml.CvConstants;
import org.yeastrc.ms.writer.mzidentml.CvParamMaker;
import org.yeastrc.ms.writer.mzidentml.DbSequenceMaker;
import org.yeastrc.ms.writer.mzidentml.MzidDataProviderException;
import org.yeastrc.ms.writer.mzidentml.PeptideEvidenceMaker;
import org.yeastrc.ms.writer.mzidentml.PeptideMaker;
import org.yeastrc.ms.writer.mzidentml.SequenceCollectionWriter;
import org.yeastrc.ms.writer.mzidentml.SpectrumIdentificationResultMaker;
import org.yeastrc.ms.writer.mzidentml.jaxb.DBSequenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.FileFormatType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputSpectraType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputsType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideEvidenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ProteinAmbiguityGroupType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SearchDatabaseType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectraDataType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIDFormatType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationProtocolType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationResultType;
import org.yeastrc.ms.writer.mzidentml.jaxb.UserParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputsType.SourceFile;

/**
 * SequestSqt2MzidDataProvider.java
 * @author Vagisha Sharma
 * Aug 1, 2011
 * 
 */

//!!! ------------- IMPORTANT --------------!!!
// FIX THIS
public class SequestSqt2MzidDataProvider extends AbstractSequestMzidDataProvider {
	
	private SequestParamsParser seqParamsparser;
	private String sequestParamsDir = null;
	
	private String sequestVersion;
	
	private String sqtFilePath = null;
	private String filename = null;
	
	// modifications
	private List<MsResidueModificationIn> dynamicResidueMods;
	private List<MsResidueModificationIn> staticResidueMods;
	
	// fasta database
	private String fastaFilePath;
	private String fastaFileName;
	
	
	public void setSequestParamsDir(String sequestParamsDir) throws MzidDataProviderException {
		
		this.sequestParamsDir = sequestParamsDir;
	}
	
	public void setSqtFilePath(String sqtFilePath) throws MzidDataProviderException {
		
		if(!(new File(sqtFilePath).exists())) {
			throw new MzidDataProviderException("SQT file does not exist: "+sqtFilePath);
		}
		this.sqtFilePath = sqtFilePath;
		
		this.filename = new File(sqtFilePath).getName();
		if(filename.toLowerCase().endsWith(".sqt"));
		this.filename = filename.substring(0, filename.length() - 4);
	}

	public void initializeFieldsBeforeWrite() throws MzidDataProviderException {
		
		// read the sequest params file
        try {
			readSequestParams();
		} catch (DataProviderException e) {
			throw new MzidDataProviderException("Error reading sequest.params file", e);
		}
		
		// read the sequest version from the SQT header
		try {
			readSequestVersion();
		} catch (DataProviderException e) {
			throw new MzidDataProviderException("Error reading version of sequest from sqt file header", e);
		}
		
	}
	
	private void readSequestVersion() throws DataProviderException {
		
		SequestSQTFileReader reader = null;
		try {
			
			reader = new SequestSQTFileReader();
			reader.open(this.sqtFilePath);
			SQTHeader header = reader.getSearchHeader();
			this.sequestVersion = header.getSearchEngineVersion();
		}
		finally {
			if(reader != null) reader.close();
		}
	}
	
	private void readSequestParams() throws DataProviderException {
		
		seqParamsparser = new SequestParamsParser();
		
		seqParamsparser.parseParams(null, this.sequestParamsDir);
		this.dynamicResidueMods = seqParamsparser.getDynamicResidueMods();
		this.staticResidueMods = seqParamsparser.getStaticResidueMods();
		this.fastaFilePath = seqParamsparser.getSearchDatabase().getServerPath();
		this.fastaFileName = new File(this.fastaFilePath).getName();

	}

	// --------------------------------------------------------------------------------------------
	// Implementation of superclass' abstract methods
	String getSequestVersion() {
		return this.sequestVersion;
	}
	
	public String getFastaFilePath() {
		return this.fastaFilePath;
	}
	
	public String getFastaFileName() {
		return this.fastaFileName;
	}
	
	// !!! ------------- IMPORTANT --------------!!!
	// FIX THIS
	@Override
	public DBSequenceType getNextSequence() throws MzidDataProviderException {
		
		
		Set<String> proteinAccessions = new HashSet<String>();
		
		// start reading the sqt file
        SequestSQTFileReader sqtReader = new SequestSQTFileReader();
        
        try {
        	
        	sqtReader.open(this.sqtFilePath);
        	sqtReader.getSearchHeader(); // go past the file header
        	
        	if(StringUtils.isBlank(this.fastaFileName)) {
        		throw new MzidDataProviderException("Could not find fasta database name");
        	}

        	// read the PSMs
        	while(sqtReader.hasNextSearchScan()) {
        		
        		SequestSearchScan scanResult = sqtReader.getNextSearchScan();
        		List<SequestSearchResultIn> psmList = scanResult.getScanResults();
        		
        		for(SequestSearchResultIn psm: psmList) {
        			
        			List<MsSearchResultProteinIn> proteins = psm.getProteinMatchList();
        			
        			for(MsSearchResultProteinIn protein: proteins) {
        				
        				String accession = protein.getAccession();
        				
        				if(!proteinAccessions.contains(accession)) {
        					
        					DbSequenceMaker seqMaker = new DbSequenceMaker();
        					seqMaker.setAccession(accession);
        					seqMaker.setId(accession);
        					seqMaker.setSearchDatabase(this.fastaFileName);
        					
        					// if we have a description for this protein in the sqt file add it
        					if(!StringUtils.isBlank(protein.getDescription())) {
        						seqMaker.addDescription(protein.getDescription());
        					}
        					
        					DBSequenceType seqType = seqMaker.make();
        					
        					// add to our list
        					proteinAccessions.add(accession);
        				}
        			}
        		}
        		
        	}
        }
        catch(DataProviderException e) {
        	throw new MzidDataProviderException("Error getting data from sqt file", e);
        } 
        finally {
        	sqtReader.close(); // close the file handle
        }
		return null;
	}

	void writePeptideSequences(SequenceCollectionWriter seqCollWriter) throws IOException, MzidDataProviderException {
		
		Set<String> peptideSequences = new HashSet<String>();
		
		// start reading the sqt file
        SequestSQTFileReader sqtReader = new SequestSQTFileReader();
        
        
        try {
        	
        	sqtReader.open(this.sqtFilePath);
        	sqtReader.getSearchHeader();
        	
        	// read the PSMs
        	while(sqtReader.hasNextSearchScan()) {
        		
        		SequestSearchScan scanResult = sqtReader.getNextSearchScan();
        		List<SequestSearchResultIn> psmList = scanResult.getScanResults();
        		
        		for(SequestSearchResultIn psm: psmList) {
        			
        			MsSearchResultPeptide resultPeptide = psm.getResultPeptide();
        			
        			String modifiedSeq = resultPeptide.getModifiedPeptide();
        			
        			
        			if(peptideSequences.contains(modifiedSeq))
        				continue;
        			
        			PeptideMaker peptideMaker = new PeptideMaker(this.getUnimodRepository());
        			
        			peptideMaker.setId(modifiedSeq);
        			peptideMaker.setSequence(resultPeptide.getPeptideSequence());
        			
        			// look for modifications (DYNAMIC)
        			List<MsResultResidueMod> dynaResMods = resultPeptide.getResultDynamicResidueModifications();
        			
        			for(MsResultResidueMod mod: dynaResMods) {

        				try {
        					peptideMaker.addModification(mod.getModifiedPosition(), mod.getModificationMass().doubleValue());
        				}
        				catch(UnimodRepositoryException e) {
        					throw new MzidDataProviderException("Unimod repository lookup failed for dynamic modification at position "+
        							mod.getModifiedPosition()+" with delta mass: "+mod.getModificationMass()+" in peptide: "+resultPeptide.getPeptideSequence(), e);
        				}
        			}
        			
        			
        			// Do we have any static modifications? 
        			for(MsResidueModificationIn smod: staticResidueMods) {
        				
        				String peptideSequence = resultPeptide.getPeptideSequence();
        				int idx = -1;
        				
        				while((idx = peptideSequence.indexOf(smod.getModifiedResidue(), idx+1)) != -1) {
        					
        					try {
								peptideMaker.addModification(idx, smod.getModificationMass().doubleValue());
							} catch (UnimodRepositoryException e) {
								throw new MzidDataProviderException("Unimod repository lookup failed for static modification at position "+
	        							idx+" with delta mass: "+smod.getModificationMass()+" in peptide: "+resultPeptide.getPeptideSequence(), e);
							}
        				}
        			}
        			
        			// TODO deal with terminal modifications
        			List<MsResultTerminalMod> dynaTermMods = resultPeptide.getResultDynamicTerminalModifications();
        			
        			PeptideType peptideType = peptideMaker.make();
        			seqCollWriter.addPeptide(peptideType);
        			
        			peptideSequences.add(modifiedSeq);
        		}
        		
        	}
        }
        catch(DataProviderException e) {
        	throw new MzidDataProviderException("Error getting data from sqt file", e);
        } catch (JAXBException e) {
        	throw new MzidDataProviderException("Error marshalling DBSequenceType", e);
		} catch (ModifiedSequenceBuilderException e) {
			throw new MzidDataProviderException("There was an error building modified sequence for a peptide", e);
		}
        finally {
        	sqtReader.close(); // close the file handle
        }
	}

	void writePeptideEvidences(SequenceCollectionWriter seqCollWriter) throws IOException, MzidDataProviderException {
		
		Set<String> peptideSequences = new HashSet<String>();
		
		// start reading the sqt file
        SequestSQTFileReader sqtReader = new SequestSQTFileReader();
        
        
        try {
        	
        	sqtReader.open(this.sqtFilePath);
        	sqtReader.getSearchHeader();
        	
        	// read the PSMs
        	while(sqtReader.hasNextSearchScan()) {
        		
        		SequestSearchScan scanResult = sqtReader.getNextSearchScan();
        		List<SequestSearchResultIn> psmList = scanResult.getScanResults();
        		
        		for(SequestSearchResultIn psm: psmList) {
        			
        			MsSearchResultPeptide resultPeptide = psm.getResultPeptide();
        			
        			String modifiedSeq = resultPeptide.getModifiedPeptide();
        			
        			
        			if(peptideSequences.contains(modifiedSeq))
        				continue;
        			
        			List<MsSearchResultProteinIn> proteins = psm.getProteinMatchList();
        			
        			int evidenceCount = 1;
        			
        			for(MsSearchResultProteinIn protein: proteins) {
        				
        				PeptideEvidenceMaker pevMaker = new PeptideEvidenceMaker();
        				
        				pevMaker.setId(modifiedSeq+"_Ev"+evidenceCount++);
        				pevMaker.setPeptide_ref(modifiedSeq);
        				
        				// we use protein accession as the id for the protein (DBSequence element)
        				pevMaker.setDbSequence_ref(protein.getAccession());
        				
        				pevMaker.setPreResidue(String.valueOf(resultPeptide.getPreResidue()));
        				pevMaker.setPostResidue(String.valueOf(resultPeptide.getPostResidue()));
        				
        				PeptideEvidenceType pevT = pevMaker.make();
        				
        				seqCollWriter.addPeptideEvidence(pevT);
        			}
        			
        			peptideSequences.add(modifiedSeq);
        		}
        		
        	}
        }
        catch(DataProviderException e) {
        	throw new MzidDataProviderException("Error getting data from sqt file", e);
        } catch (JAXBException e) {
        	throw new MzidDataProviderException("Error marshalling DBSequenceType", e);
		} catch (ModifiedSequenceBuilderException e) {
			throw new MzidDataProviderException("There was an error building modified sequence for a peptide", e);
		}
        finally {
        	sqtReader.close(); // close the file handle
        }
	}

	public List<InputSpectraType> getInputSpectraList() {
		
		InputSpectraType spectra = new InputSpectraType();
		spectra.setSpectraDataRef(this.filename+".cms2");
		
		List<InputSpectraType> list = new ArrayList<InputSpectraType>();
		list.add(spectra);
		
		return list;
	}
	
	
	
	public InputsType getInputs() throws MzidDataProviderException {
		
		InputsType inputs = new InputsType();
		
		// Source file (the sqt file)
		SourceFile srcFile = new SourceFile();
		inputs.getSourceFile().add(srcFile);
		srcFile.setId(this.filename+".sqt");
		srcFile.setLocation(this.sqtFilePath);
		FileFormatType fileFormat = new FileFormatType();
		fileFormat.setCvParam(CvParamMaker.getInstance().make("MS:1001563", "Sequest SQT", CvConstants.PSI_CV));
		srcFile.setFileFormat(fileFormat);
		
		// Database (fasta)
		SearchDatabaseType searchDbType = new SearchDatabaseType();
		inputs.getSearchDatabase().add(searchDbType);
		searchDbType.setId(this.fastaFileName);
		ParamType dbName = new ParamType();
		UserParamType userParam = new UserParamType();
		userParam.setName(this.fastaFileName);
		dbName.setUserParam(userParam);
		searchDbType.setDatabaseName(dbName);
		searchDbType.setLocation(this.fastaFilePath);
		
		// Spectra file (.cms2 file)
		SpectraDataType spectraData = new SpectraDataType();
		inputs.getSpectraData().add(spectraData);
		spectraData.setId(this.filename+".cms2");
		
		SpectrumIDFormatType specIdFmt = new SpectrumIDFormatType();
		specIdFmt.setCvParam(CvParamMaker.getInstance().make("MS:1000776", "scan number only nativeID format", CvConstants.PSI_CV));
		spectraData.setSpectrumIDFormat(specIdFmt);
		
		String cms2file = this.sqtFilePath.replace(".sqt", ".cms2");
		spectraData.setLocation(cms2file);
		fileFormat = new FileFormatType();
		fileFormat.setCvParam(CvParamMaker.getInstance().make("MS:1001466", "MS2 file", CvConstants.PSI_CV));
		spectraData.setFileFormat(fileFormat);
		
		return inputs;
	}
	
	void writeSearchResults() throws MzidDataProviderException {
		
		
		// start reading the sqt file
        SequestSQTFileReader sqtReader = new SequestSQTFileReader();
        
        try {
        	
        	sqtReader.open(this.sqtFilePath);
        	sqtReader.getSearchHeader();
        	
        	// read the PSMs
        	while(sqtReader.hasNextSearchScan()) {
        		
        		SequestSearchScan scanResult = sqtReader.getNextSearchScan();
        		List<SequestSearchResultIn> psmList = scanResult.getScanResults();
        		
        		SpectrumIdentificationResultMaker specResultMaker = new SpectrumIdentificationResultMaker();
        		specResultMaker.initSpectrumResult(filename+".cms2", scanResult.getScanNumber());
        		
        		for(SequestSearchResultIn psm: psmList) {
        			
        			specResultMaker.addSequestResult(psm);
        		}
        		
        		SpectrumIdentificationResultType result = specResultMaker.getSpectrumResult();
        		
        	}
        }
        catch(DataProviderException e) {
        	throw new MzidDataProviderException("Error getting data from sqt file", e);
        } catch (ModifiedSequenceBuilderException e) {
        	throw new MzidDataProviderException("Error building modified peptide sequence for psm", e);
		}
        finally {
        	sqtReader.close(); // close the file handle
        }
		
	}

	@Override
	public PeptideType getNextPeptide() throws MzidDataProviderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PeptideEvidenceType> getNextPeptideEvidenceSet()
			throws MzidDataProviderException {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public SpectrumIdentificationResultType getNextSpectrumIdentificationResult()
			throws MzidDataProviderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpectrumIdentificationProtocolType getSpectrumIdentificationProtocol()
			throws MzidDataProviderException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ProteinAmbiguityGroupType getNextProteinAmbiguityGroup()
			throws MzidDataProviderException {
		throw new UnsupportedOperationException("Sequest results do not contains protein-level analysis");
	}

	@Override
	public boolean hasProteinAnalysis() {
		return false;
	}
	
}
