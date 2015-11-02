/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml.sequest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.parser.unimod.UnimodRepositoryException;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.writer.mzidentml.CvConstants;
import org.yeastrc.ms.writer.mzidentml.CvParamMaker;
import org.yeastrc.ms.writer.mzidentml.DbSequenceMaker;
import org.yeastrc.ms.writer.mzidentml.MsDataMzidDataProvider;
import org.yeastrc.ms.writer.mzidentml.MzidDataProviderException;
import org.yeastrc.ms.writer.mzidentml.PeptideEvidenceMaker;
import org.yeastrc.ms.writer.mzidentml.PeptideMaker;
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
 * SequestMzidDataProvider.java
 * @author Vagisha Sharma
 * Aug 1, 2011
 * 
 */
public class SequestMzidDataProvider extends AbstractSequestMzidDataProvider implements MsDataMzidDataProvider {
	
	private DAOFactory daoFactory = DAOFactory.instance();
	private MsSearchResultProteinDAO proteinDao = daoFactory.getMsProteinMatchDAO();
	private SequestSearchResultDAO searchResDao = daoFactory.getSequestResultDAO();
	
	private int experimentId;
	private MsSearch search;
	
	
	private String sequestVersion;
	
	// modifications
	// private List<MsResidueModification> dynamicResidueMods;
	private List<MsResidueModification> staticResidueMods;
	
	// fasta database
	private String fastaFilePath;
	private String fastaFileName;
	
	// original location of the search files
	private String originalSearchDirectory;
	
	// files
	private Map<Integer, String> runSearchIdFileNameMap;
	
	// file format (for searches)
	private SearchFileFormat searchFileFormat;
	private String searchFileExtension;
	
	// file format (for spectra)
	private RunFileFormat runFileFormat;
	private String runFileExtension;
	
	
	// resultIds
	// !-- IMPORTANT --!
	// Result IDs are sorted by scan IDs
	private List<Integer> resultIds;
	
	private int index = 0;
	// flags to indicate if we have started iterating over the protein or peptide list
	private boolean dbsequenceIterationStarted = false;
	private boolean peptideIterationStarted = false;
	private boolean peptideEvidenceIterationStarted = false;
	private boolean spectrumResultIterationStarted = false;
	
	
	private List<String> proteinAccessions;
	private Set<String> peptideSequences;
	
	
	public void setExperimentId(int experimentId) {
		
		this.experimentId = experimentId;
	}
	
	public void initializeFieldsBeforeWrite() throws MzidDataProviderException {
		
		
		if(experimentId <= 0) {
			throw new MzidDataProviderException("Invalid experimentId: "+experimentId);
		}
		
		// load the experiment
		MsExperiment experiment = daoFactory.getMsExperimentDAO().loadExperiment(experimentId);
		if(experiment == null) {
			throw new MzidDataProviderException("Experiment with ID: "+experimentId+" was not found in the database");
		}
		
		// get the search IDs for this experiment; make sure we have only one
		List<Integer> searchIds = daoFactory.getMsSearchDAO().getSearchIdsForExperiment(experimentId);
		if(searchIds.size() == 0) {
			throw new MzidDataProviderException("Experiment with ID: "+experimentId+" does not have any searches");
		}
		if(searchIds.size() > 1) {
			throw new MzidDataProviderException("Experiment with ID: "+experimentId+" has multiple searches");
		}
		
		int searchId = searchIds.get(0);
		
		// load the search
		this.search = daoFactory.getMsSearchDAO().loadSearch(searchId);
		if(!(Program.isSequest(search.getSearchProgram()))) {
			
			throw new MzidDataProviderException("Search with ID: "+searchId+" is not a Sequest search");
		}
		
		this.originalSearchDirectory = search.getServerDirectory();
		
		this.sequestVersion = search.getSearchProgramVersion();
		
		// get the fasta file used for this search; make sure there is only one
		List<MsSearchDatabase> searchDatabases = search.getSearchDatabases();
		if(searchDatabases.size() == 0) {
			
			throw new MzidDataProviderException("No fasta databases found for search with ID: "+searchId);
			
		}
		if(searchDatabases.size() > 1) {
			
			throw new MzidDataProviderException("Multiple fasta databases found for search with ID: "+searchId);
		}
		
		this.fastaFilePath = searchDatabases.get(0).getServerPath();
		this.fastaFileName = searchDatabases.get(0).getDatabaseFileName();
		
		if(StringUtils.isBlank(this.fastaFileName)) {
    		throw new MzidDataProviderException("Could not find fasta database name");
    	}
		
		// get the static modification
		this.staticResidueMods = search.getStaticResidueMods();
		
		// get the filename for the run in this search
		initializeFileNamesAndFileFormats();
		
		// !-- IMPORTANT --!
		// Result IDs are sorted by scan IDs
		ResultSortCriteria sortCriteria = new ResultSortCriteria(SORT_BY.SCAN, SORT_ORDER.ASC);
		// get the runSearchIds for this search; there is one runSearchId per .sqt file uploaded for this search
    	List<Integer> runSearchIds = daoFactory.getMsRunSearchDAO().loadRunSearchIdsForSearch(this.search.getId());
    	this.resultIds = new ArrayList<Integer>();
    	for(int runSearchId: runSearchIds) {
    		List<Integer> idsForRunSearch = daoFactory.getSequestResultDAO().loadResultIdsForRunSearch(runSearchId, null, sortCriteria);
    		resultIds.addAll(idsForRunSearch);
    	}
		
	}
	
	private void initializeFileNamesAndFileFormats() {
		
		MsRunSearchDAO rsDao = daoFactory.getMsRunSearchDAO();
		MsRunDAO runDao = daoFactory.getMsRunDAO();
		
		this.runSearchIdFileNameMap = new HashMap<Integer, String>();
		
		// get the runSearchIds for this search; there is one runSearchId per .sqt file uploaded for this search
		List<Integer> runSearchIds = daoFactory.getMsRunSearchDAO().loadRunSearchIdsForSearch(this.search.getId());
        	
		for(int runSearchId: runSearchIds) {
			
			String filename = rsDao.loadFilenameForRunSearch(runSearchId);
			
			this.runSearchIdFileNameMap.put(runSearchId, filename);
			
			MsRunSearch runSearch = rsDao.loadRunSearch(runSearchId);
			this.searchFileFormat = runSearch.getSearchFileFormat();
			if(this.searchFileFormat == SearchFileFormat.SQT_SEQ) {
				this.searchFileExtension = ".sqt";
			}
			else if(searchFileFormat == SearchFileFormat.PEPXML_SEQ) {
				this.searchFileExtension = "pep.xml";
			}
			
			MsRun run = runDao.loadRun(runSearch.getRunId());
			this.runFileFormat = run.getRunFileFormat();
			if(this.runFileFormat == RunFileFormat.MS2) {
				this.runFileExtension = ".ms2";
			}
			else if(this.runFileFormat == RunFileFormat.CMS2) {
				this.runFileExtension = ".cms2";
			}
			else if(this.runFileFormat == RunFileFormat.MZXML) {
				this.runFileExtension = ".mzXML";
			}
        }
	}
	
	
	String getSequestVersion() {
		return this.sequestVersion;
	}
	
	public String getFastaFilePath() {
		return this.fastaFilePath;
	}
	
	public String getFastaFileName() {
		return this.fastaFileName;
	}
	
	@Override
	public DBSequenceType getNextSequence() {
		
		if(!this.dbsequenceIterationStarted) {
			this.index = 0;
			this.dbsequenceIterationStarted = true;
			
			Set<String> uniqAccessions = new HashSet<String>();
			
			for(int resultId: resultIds) {
				
				List<MsSearchResultProtein> proteins = proteinDao.loadResultProteins(resultId);
				
				for(MsSearchResultProtein protein: proteins) {

					String accession = protein.getAccession();

					// add to our list
					uniqAccessions.add(accession);
				}
			}
			
			this.proteinAccessions = new ArrayList<String>(uniqAccessions.size());
			this.proteinAccessions.addAll(uniqAccessions);
		}
		
		if(index >= this.proteinAccessions.size()) {
			this.proteinAccessions.clear(); // we are at the end of the list; clean up
			this.proteinAccessions = null;
			this.dbsequenceIterationStarted = false;
			return null;
		}
		
		String accession = proteinAccessions.get(index);
		index++; // increment the index
		
		DbSequenceMaker seqMaker = new DbSequenceMaker();
		seqMaker.setAccession(accession);
		seqMaker.setId(accession);
		seqMaker.setSearchDatabase(this.fastaFileName);

		// TODO lookup YRC_NRSEQ and get the protein description

		DBSequenceType seqType = seqMaker.make();
		return seqType;
		
	}
	
	
	@Override
	public PeptideType getNextPeptide() throws MzidDataProviderException {
		
		if(!this.peptideIterationStarted) {
			
			this.index = 0; // set the index to 0
			this.peptideIterationStarted = true;
			
			this.peptideSequences = new HashSet<String>(resultIds.size());
		}
		
		for(;index < this.resultIds.size(); index++) {
			
			int resultId = resultIds.get(index);
			
			MsSearchResult psm = searchResDao.load(resultId);
			
			MsSearchResultPeptide resultPeptide = psm.getResultPeptide();
			
			String modifiedSeq = null;
			try {
				modifiedSeq = resultPeptide.getModifiedPeptide();
			} catch (ModifiedSequenceBuilderException e) {
				throw new MzidDataProviderException("There was an error building modified sequence for a peptide", e);
			}
			
			if(this.peptideSequences.contains(modifiedSeq))
				continue;
			
			else {
				
				peptideSequences.add(modifiedSeq);
				
				index++;
				
				return makePeptideType(resultPeptide, modifiedSeq);
			}
			
		}
		
		this.peptideSequences.clear(); // we are at the end of the list; clean up
		this.peptideSequences = null;
		this.peptideIterationStarted = false;
		return null;
		
	}

	private PeptideType makePeptideType(MsSearchResultPeptide resultPeptide,
			String modifiedSeq) throws MzidDataProviderException {
		
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
		for(MsResidueModification smod: staticResidueMods) {
			
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
		// List<MsResultTerminalMod> dynaTermMods = resultPeptide.getResultDynamicTerminalModifications();
		
		PeptideType peptideType = peptideMaker.make();
		
		return peptideType;
	}

	
	@Override
	public List<PeptideEvidenceType> getNextPeptideEvidenceSet() throws MzidDataProviderException {
		
		if(!this.peptideEvidenceIterationStarted) {
			
			this.index = 0; // set the index to 0
			this.peptideEvidenceIterationStarted = true;
			
			this.peptideSequences = new HashSet<String>(resultIds.size());
		}

		for(; index < this.resultIds.size(); index++) {
			
			int resultId = resultIds.get(index);
			
			MsSearchResult psm = searchResDao.load(resultId);
			
			MsSearchResultPeptide resultPeptide = psm.getResultPeptide();
			
			String modifiedSeq = null;
			try {
				modifiedSeq = resultPeptide.getModifiedPeptide();
			} catch (ModifiedSequenceBuilderException e) {
				throw new MzidDataProviderException("There was an error building modified sequence for a peptide", e);
			}
			
			if(this.peptideSequences.contains(modifiedSeq))
				continue;
			
			else {
				
				peptideSequences.add(modifiedSeq);
				
				List<MsSearchResultProtein> proteins = proteinDao.loadResultProteins(resultId);
    			
    			int evidenceCount = 1;
    			
    			List<PeptideEvidenceType> pevList = new ArrayList<PeptideEvidenceType>(proteins.size());
    			
    			for(MsSearchResultProtein protein: proteins) {
    				
    				PeptideEvidenceMaker pevMaker = new PeptideEvidenceMaker();
    				
    				pevMaker.setId(modifiedSeq+"_Ev"+evidenceCount++);
    				pevMaker.setPeptide_ref(modifiedSeq);
    				
    				// we use protein accession as the id for the protein (DBSequence element)
    				pevMaker.setDbSequence_ref(protein.getAccession());
    				
    				pevMaker.setPreResidue(String.valueOf(resultPeptide.getPreResidue()));
    				pevMaker.setPostResidue(String.valueOf(resultPeptide.getPostResidue()));
    				
    				PeptideEvidenceType pevT = pevMaker.make();
    				pevList.add(pevT);
    				
    			}
    			
    			index++;
    			
    			return pevList;
			}
			
		}

		this.peptideSequences.clear(); // we are at the end of the list; clean up
		this.peptideSequences = null;
		this.peptideEvidenceIterationStarted = false;
		return null;
		
	}
	

	public List<InputSpectraType> getInputSpectraList() {
		
		List<InputSpectraType> list = new ArrayList<InputSpectraType>();
        	
		for(String filename: this.runSearchIdFileNameMap.values()) {
			
			InputSpectraType spectra = new InputSpectraType();
			spectra.setSpectraDataRef(filename+this.runFileExtension);
			
			list.add(spectra);
        }
		
		return list;
	}
	
	
	public SpectrumIdentificationProtocolType getSpectrumIdentificationProtocol() throws MzidDataProviderException {
		
		SequestProtocolMaker protocolMaker = new SequestProtocolMaker(this.getUnimodRepository(), this.search);
		return protocolMaker.getProtocol();
	}
	
	public InputsType getInputs() throws MzidDataProviderException {
		
		InputsType inputs = new InputsType();
		
		
		// Source files(s)
		for(String filename: this.runSearchIdFileNameMap.values()) {
			SourceFile srcFile = new SourceFile();
			inputs.getSourceFile().add(srcFile);
			
			FileFormatType formatType = new FileFormatType();
			
			srcFile.setId(filename+this.searchFileExtension);
			srcFile.setLocation(this.originalSearchDirectory+File.separator+filename+this.searchFileExtension);
			
			if(this.searchFileFormat == SearchFileFormat.SQT_SEQ) {
				
				formatType.setCvParam(CvParamMaker.getInstance().make("MS:1001563", "Sequest SQT", CvConstants.PSI_CV));
			}
			else if(this.searchFileFormat == SearchFileFormat.PEPXML_SEQ) {
				formatType.setCvParam(CvParamMaker.getInstance().make("MS:1001421", "pepXML file", CvConstants.PSI_CV));
			}
			srcFile.setFileFormat(formatType);
		}
		
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
		for(String filename: this.runSearchIdFileNameMap.values()) {
			
			SpectraDataType spectraData = new SpectraDataType();
			inputs.getSpectraData().add(spectraData);
			
			SpectrumIDFormatType specIdFmt = new SpectrumIDFormatType();
			specIdFmt.setCvParam(CvParamMaker.getInstance().make("MS:1000776", "scan number only nativeID format", CvConstants.PSI_CV));
			spectraData.setSpectrumIDFormat(specIdFmt);
			
			FileFormatType formatType = new FileFormatType();
			
			String fullfilename = filename + runFileExtension;
			String spectraFile = this.originalSearchDirectory+File.separator+filename + runFileExtension;
			
			if(runFileFormat == RunFileFormat.MS2) {
				
				formatType.setCvParam(CvParamMaker.getInstance().make("MS:1001466", "MS2 file", CvConstants.PSI_CV));
			}
			else if(runFileFormat == RunFileFormat.CMS2) {
				formatType.setCvParam(CvParamMaker.getInstance().make("MS:1001466", "MS2 file", CvConstants.PSI_CV));
			}
			else if(runFileFormat == RunFileFormat.MZXML) {
				formatType.setCvParam(CvParamMaker.getInstance().make("MS:1000566", "ISB mzXML file", CvConstants.PSI_CV));
			}
			else
				throw new MzidDataProviderException("Unrecognized spectra file format: "+runFileFormat);
			
			
			spectraData.setId(fullfilename);
			spectraData.setLocation(spectraFile);
			spectraData.setFileFormat(formatType);
		}
		
		return inputs;
	}
	
	
	SpectrumIdentificationResultType makeSpectrumIdentificationResult(List<SequestSearchResult> sequestResults) throws MzidDataProviderException {
		
		if(sequestResults.size() == 0)
			return null;
		
		String filename = getFileNameForRunSearchId(sequestResults.get(0).getRunSearchId());
		filename += this.runFileExtension;
		
		int scanNumber = getScanNumber(sequestResults.get(0).getScanId());
		
		SpectrumIdentificationResultMaker specResultMaker = new SpectrumIdentificationResultMaker();
		specResultMaker.initSpectrumResult(filename, scanNumber);
		
		for(SequestSearchResult psm: sequestResults) {
			
			try {
				specResultMaker.addSequestResult(psm);
			} catch (ModifiedSequenceBuilderException e) {
				throw new MzidDataProviderException("Error building modified sequence for psm", e);
			}
		}
		
		SpectrumIdentificationResultType result = specResultMaker.getSpectrumResult();
		return result;
	}

	private int getScanNumber(int scanId) {
		
		MsScan scan = DAOFactory.instance().getMsScanDAO().load(scanId);
		return scan.getStartScanNum();
	}

	private String getFileNameForRunSearchId(int runSearchId) {
		
		return this.runSearchIdFileNameMap.get(runSearchId);
	}


	@Override
	public SpectrumIdentificationResultType getNextSpectrumIdentificationResult() throws MzidDataProviderException {
		
		if(!this.spectrumResultIterationStarted) {
			
			this.index = 0; // set the index to 0
			this.spectrumResultIterationStarted = true;
		}
		
		int lastScanId = -1;  // used for grouping search results into <SpectrumIdentificationResult> elements
		List<SequestSearchResult> sequestResults = new ArrayList<SequestSearchResult>();
		
		for(; index < this.resultIds.size(); index++) {
    		
			// read the PSMs
			int resultId = resultIds.get(index);
			SequestSearchResult result = searchResDao.load(resultId);

			if(result.getScanId() != lastScanId) {

				if(lastScanId != -1) {

					SpectrumIdentificationResultType spectrumResult = makeSpectrumIdentificationResult(sequestResults);
					
					return spectrumResult;
				}
			}

			lastScanId = result.getScanId();
			sequestResults.add(result);
		}
    			
		if(sequestResults.size() > 0) {
			
			SpectrumIdentificationResultType spectrumResult = makeSpectrumIdentificationResult(sequestResults);
			
			return spectrumResult;
			
		}
		else {
			this.spectrumResultIterationStarted = false;
			return null;
		}
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
