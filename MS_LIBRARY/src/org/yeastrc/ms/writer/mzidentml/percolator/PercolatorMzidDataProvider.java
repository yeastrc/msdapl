/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml.percolator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorParamsDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.parser.unimod.UnimodRepository;
import org.yeastrc.ms.parser.unimod.UnimodRepositoryException;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.writer.mzidentml.AnalysisSoftwareMaker;
import org.yeastrc.ms.writer.mzidentml.CvConstants;
import org.yeastrc.ms.writer.mzidentml.CvParamMaker;
import org.yeastrc.ms.writer.mzidentml.DbSequenceMaker;
import org.yeastrc.ms.writer.mzidentml.MsDataMzidDataProvider;
import org.yeastrc.ms.writer.mzidentml.MzidConstants;
import org.yeastrc.ms.writer.mzidentml.MzidDataProvider;
import org.yeastrc.ms.writer.mzidentml.MzidDataProviderException;
import org.yeastrc.ms.writer.mzidentml.PeptideEvidenceMaker;
import org.yeastrc.ms.writer.mzidentml.PeptideMaker;
import org.yeastrc.ms.writer.mzidentml.SpectrumIdentificationResultMaker;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CVParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.DBSequenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.FileFormatType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputSpectraType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputsType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ParamListType;
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
import org.yeastrc.ms.writer.mzidentml.sequest.SequestMzidDataProvider;

/**
 * PercolatorMzidWriter.java
 * @author Vagisha Sharma
 * Aug 16, 2011
 * 
 */
public class PercolatorMzidDataProvider implements MzidDataProvider {

	
	private DAOFactory daoFactory = DAOFactory.instance();
	private PercolatorResultDAO percResDao = daoFactory.getPercolatorResultDAO();
	private MsSearchResultProteinDAO proteinDao = daoFactory.getMsProteinMatchDAO();
	
	private int experimentId;
	private int searchAnalysisId;
	private MsSearch search;
	private MsSearchAnalysis analysis;
	
	
	// fasta database
	private String fastaFilePath;
	private String fastaFileName;
	
	// modifications
	// private List<MsResidueModification> dynamicResidueMods;
	private List<MsResidueModification> staticResidueMods;
	
	// files
	private Map<Integer, String> runSearchAnalysisIdFileNameMap;
	
	// file format (for Percolator analysis)
	private SearchFileFormat analysisFileFormat;
	private String analysisFileExtension;
	
	// file format (for spectra)
	private RunFileFormat runFileFormat;
	private String runFileExtension;
	
	
	// resultIds
	// !-- IMPORTANT --!
	// Result IDs are sorted by scan IDs
	private List<Integer> percolatorResultIds;
	
	private int index = 0;
	// flags to indicate if we have started iterating over the protein or peptide list
	private boolean dbsequenceIterationStarted = false;
	private boolean peptideIterationStarted = false;
	private boolean peptideEvidenceIterationStarted = false;
	private boolean spectrumResultIterationStarted = false;
	
	
	private List<String> proteinAccessions;
	private Set<String> peptideSequences;
	
	
	// mzIdWriter
	private MsDataMzidDataProvider dataProvider;
	
	private UnimodRepository unimodRepository;
	
	public void setExperimentId(int experimentId) {
		
		this.experimentId = experimentId;
	}
	
	public void setSearchAnalysisId(int searchAnalysisId) {
		
		this.searchAnalysisId = searchAnalysisId;
	}
	
	@Override
	public void setUnimodRepository(UnimodRepository repository) {
		this.unimodRepository = repository;
	}
	
	UnimodRepository getUnimodRepository() {
		return this.unimodRepository;
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
		if((Program.isSequest(this.search.getSearchProgram()))) {
			this.dataProvider = new SequestMzidDataProvider();
		}
		else {
			throw new MzidDataProviderException("Do not know how to handle search program: "+this.search.getSearchProgram());
		}
		
		
		// get the Percolator analysis
		List<Integer> analysisIds = daoFactory.getMsSearchAnalysisDAO().getAnalysisIdsForSearch(searchId);
		if(analysisIds.size() == 0) {
			throw new MzidDataProviderException("Experiment with ID: "+experimentId+" does not have any Percolator analyses");
		}
		if(this.searchAnalysisId != 0) {
			if(!analysisIds.contains(this.searchAnalysisId)) {
				throw new MzidDataProviderException("Given Percolator analysis ID is not found for experiment ID: "+experimentId);
			}
		}
		if(analysisIds.size() > 1 && this.searchAnalysisId == 0) {
			throw new MzidDataProviderException("Experiment with ID: "+experimentId+" has multiple Percolator analyses");
		}
		this.searchAnalysisId = analysisIds.get(0);
		
		// load the analysis
		this.analysis = daoFactory.getMsSearchAnalysisDAO().load(this.searchAnalysisId);
		
		
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
		// get the runSearchAnalysisIds for this experiment
    	List<Integer> runSearchAnalysisIds = daoFactory.getMsRunSearchAnalysisDAO().getRunSearchAnalysisIdsForAnalysis(this.analysis.getId());
    	this.percolatorResultIds = new ArrayList<Integer>();
    	for(int runSearchAnalysisId: runSearchAnalysisIds) {
    		List<Integer> idsForRunSearchAnalysis = daoFactory.getPercolatorResultDAO().loadIdsForRunSearchAnalysis(runSearchAnalysisId, null, sortCriteria);
    		percolatorResultIds.addAll(idsForRunSearchAnalysis);
    	}
    	
    	dataProvider.setExperimentId(experimentId);
    	dataProvider.setUnimodRepository(this.getUnimodRepository());
    	dataProvider.initializeFieldsBeforeWrite();
	}
	
	private void initializeFileNamesAndFileFormats() {
		
		MsRunSearchAnalysisDAO rsaDao = daoFactory.getMsRunSearchAnalysisDAO();
		MsRunSearchDAO rsDao = daoFactory.getMsRunSearchDAO();
		MsRunDAO runDao = daoFactory.getMsRunDAO();
		
		this.runSearchAnalysisIdFileNameMap = new HashMap<Integer, String>();
		
		// get the runSearchAnalysisIds for this Percolator analysis;
		// There is one runSearchAnalysisId for each input file (.sqt) analysed through Percolator in this experiment
		List<Integer> runSearchAnalysisIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(this.analysis.getId());
		
        	
		for(int runSearchAnalysisId: runSearchAnalysisIds) {
			
			String filename = rsaDao.loadFilenameForRunSearchAnalysis(runSearchAnalysisId);
			
			this.runSearchAnalysisIdFileNameMap.put(runSearchAnalysisId, filename);
			
			MsRunSearchAnalysis runSearchAnalysis = rsaDao.load(runSearchAnalysisId);
			
			this.analysisFileFormat = runSearchAnalysis.getAnalysisFileFormat();
			
			if(this.analysisFileFormat == SearchFileFormat.SQT_PERC) {
				this.analysisFileExtension = ".sqt";
			}
			else if(analysisFileFormat == SearchFileFormat.XML_PERC) {
				this.analysisFileExtension = ".xml";
			}
			
			int runSearchId = runSearchAnalysis.getRunSearchId();
			MsRunSearch runSearch = rsDao.loadRunSearch(runSearchId);
			
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
	
	@Override
	public
	AnalysisSoftwareListType getAnalysisSoftware() throws MzidDataProviderException {
		
		AnalysisSoftwareListType listType = dataProvider.getAnalysisSoftware();
		
		AnalysisSoftwareMaker softwareMaker = new AnalysisSoftwareMaker();
		
		// Add Percolator to the analysis software list
		AnalysisSoftwareType software = softwareMaker.makePercolatorAnalysisSoftware(this.analysis.getAnalysisProgramVersion());
		listType.getAnalysisSoftware().add(software);
		
		return listType;
	}

	
	public SpectrumIdentificationProtocolType getSpectrumIdentificationProtocol() throws MzidDataProviderException {
		
		SpectrumIdentificationProtocolType protocol = dataProvider.getSpectrumIdentificationProtocol();
		
		// Any Percolator options
		PercolatorParamsDAO paramDao = DAOFactory.instance().getPercoltorParamsDAO();
		List<PercolatorParam> params = paramDao.loadParams(this.analysis.getId());
		
		ParamListType paramList = protocol.getAdditionalSearchParams();
		if(paramList == null) {
			paramList = new ParamListType();
			protocol.setAdditionalSearchParams(paramList);
		}
		for(PercolatorParam param: params) {
			
			UserParamType userParam = new UserParamType();
			userParam.setName("Percolator_"+param.getParamName());
			userParam.setValue(param.getParamValue());
			
			paramList.getParamGroup().add(userParam);
		}
		
		return protocol;
	}

	@Override
	public String getFastaFileName() {
		return this.fastaFileName;
	}

	@Override
	public String getFastaFilePath() {
		return this.fastaFilePath;
	}

	@Override
	public List<InputSpectraType> getInputSpectraList() {
		
		List<InputSpectraType> list = new ArrayList<InputSpectraType>();
    	
		for(String filename: this.runSearchAnalysisIdFileNameMap.values()) {
			
			InputSpectraType spectra = new InputSpectraType();
			spectra.setSpectraDataRef(filename+this.runFileExtension);
			
			list.add(spectra);
        }
		
		return list;
	}

	@Override
	public InputsType getInputs() throws MzidDataProviderException {
		
		InputsType inputs = new InputsType();
		
		// We may have had a single file with all the Percolator results
		MsSearchAnalysis analysis = DAOFactory.instance().getMsSearchAnalysisDAO().load(this.analysis.getId());
		if(!StringUtils.isBlank(analysis.getFilename())) {
			
			SourceFile srcFile = new SourceFile();
			inputs.getSourceFile().add(srcFile);
			srcFile.setId(analysis.getFilename());
			
			/*
			[Term]
			 id: MS:1001040
			 name: intermediate analysis format
			 def: "Type of the source file, the  mzIdentML was created from." [PSI:PI]
			 is_a: MS:1001459 ! file format
			*/
			FileFormatType format = new FileFormatType();
			CVParamType param = CvParamMaker.getInstance().make("MS:1001040", "intermediate analysis format", "Percolator XML", CvConstants.PSI_CV);
			format.setCvParam(param);
			srcFile.setFileFormat(format);
			
			// TODO the location is not stored in the database! 
			srcFile.setLocation(analysis.getFilename());
		}
		// Otherwise .sqt files (one per Sequest .sqt) were uploaded
		else {
			// Source files(s)
			for(String filename: this.runSearchAnalysisIdFileNameMap.values()) {
				SourceFile srcFile = new SourceFile();
				inputs.getSourceFile().add(srcFile);


				srcFile.setId(filename+this.analysisFileExtension);
				
				// TODO the location is not stored in the database! 
				srcFile.setLocation(filename+this.analysisFileExtension);

				FileFormatType format = new FileFormatType();
				CVParamType param = CvParamMaker.getInstance().make("MS:1001040", "intermediate analysis format", "Percolator SQT", CvConstants.PSI_CV);
				format.setCvParam(param);
				srcFile.setFileFormat(format);
				
			}
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
		for(String filename: this.runSearchAnalysisIdFileNameMap.values()) {
			
			SpectraDataType spectraData = new SpectraDataType();
			inputs.getSpectraData().add(spectraData);
			
			SpectrumIDFormatType specIdFmt = new SpectrumIDFormatType();
			specIdFmt.setCvParam(CvParamMaker.getInstance().make("MS:1000776", "scan number only nativeID format", CvConstants.PSI_CV));
			spectraData.setSpectrumIDFormat(specIdFmt);
			
			FileFormatType formatType = new FileFormatType();
			
			String fullfilename = filename + runFileExtension;
			String spectraFile = this.search.getServerDirectory()+File.separator+filename + runFileExtension;
			
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

	@Override
	public DBSequenceType getNextSequence() {
		
		if(!this.dbsequenceIterationStarted) {
			this.index = 0;
			this.dbsequenceIterationStarted = true;
			
			Set<String> uniqAccessions = new HashSet<String>();
			
			for(int percResultId: percolatorResultIds) {
				
				PercolatorResult psm = percResDao.loadForPercolatorResultId(percResultId);
				
				List<MsSearchResultProtein> proteins = proteinDao.loadResultProteins(psm.getId());
				
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
			
			this.peptideSequences = new HashSet<String>(percolatorResultIds.size());
		}
		
		for(;index < this.percolatorResultIds.size(); index++) {
			
			int percResultId = percolatorResultIds.get(index);
			
			PercolatorResult psm = percResDao.loadForPercolatorResultId(percResultId);
			
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
	public List<PeptideEvidenceType> getNextPeptideEvidenceSet()
			throws MzidDataProviderException {
		
		if(!this.peptideEvidenceIterationStarted) {
			
			this.index = 0; // set the index to 0
			this.peptideEvidenceIterationStarted = true;
			
			this.peptideSequences = new HashSet<String>(percolatorResultIds.size());
		}

		for(; index < this.percolatorResultIds.size(); index++) {
			
			int percResultId = percolatorResultIds.get(index);
			
			PercolatorResult psm = percResDao.loadForPercolatorResultId(percResultId);
			
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
				
				List<MsSearchResultProtein> proteins = proteinDao.loadResultProteins(psm.getId());
    			
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


	@Override
	public SpectrumIdentificationResultType getNextSpectrumIdentificationResult()
			throws MzidDataProviderException {
		
		if(!this.spectrumResultIterationStarted) {
			
			this.index = 0; // set the index to 0
			this.spectrumResultIterationStarted = true;
		}
		
		int lastScanId = -1;  // used for grouping search results into <SpectrumIdentificationResult> elements
		List<PercolatorResult> sequestResults = new ArrayList<PercolatorResult>();
		
		for(; index < this.percolatorResultIds.size(); index++) {
    		
			// read the PSMs
			int percResultId = percolatorResultIds.get(index);
			
			PercolatorResult psm = percResDao.loadForPercolatorResultId(percResultId);
			
			if(psm.getScanId() != lastScanId) {

				if(lastScanId != -1) {

					SpectrumIdentificationResultType spectrumResult = makeSpectrumIdentificationResult(sequestResults);
					
					return spectrumResult;
				}
			}

			lastScanId = psm.getScanId();
			sequestResults.add(psm);
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
	
	SpectrumIdentificationResultType makeSpectrumIdentificationResult(List<PercolatorResult> percolatorResults) throws MzidDataProviderException {
		
		if(percolatorResults.size() == 0)
			return null;
		
		String filename = getFileNameForRunSearchAnalysisId(percolatorResults.get(0).getRunSearchAnalysisId());
		filename += this.runFileExtension;
		
		int scanNumber = getScanNumber(percolatorResults.get(0).getScanId());
		
		SpectrumIdentificationResultMaker specResultMaker = new SpectrumIdentificationResultMaker();
		specResultMaker.initSpectrumResult(filename, scanNumber, this.search.getSearchProgram());
		
		for(PercolatorResult psm: percolatorResults) {
			
			try {
				specResultMaker.addPercolatorResult(psm);
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

	private String getFileNameForRunSearchAnalysisId(int runSearchAnalysisId) {
		
		return this.runSearchAnalysisIdFileNameMap.get(runSearchAnalysisId);
	}
	

	@Override
	public String getSpectrumIdentificationId() {
		
		return dataProvider.getSpectrumIdentificationId()+"_"+MzidConstants.SPEC_IDENT_ID_PERC;
	}

	@Override
	public String getSpectrumIdentificationListId() {
		
		return MzidConstants.SPEC_ID_LIST_ID;
	}

	@Override
	public String getSpectrumIdentificationProtocolId() {
		
		return dataProvider.getSpectrumIdentificationProtocolId(); // +"_"+MzidConstants.PERCOLATOR_PROTOCOL_ID;
	}
	
	@Override
	public ProteinAmbiguityGroupType getNextProteinAmbiguityGroup()
			throws MzidDataProviderException {
		throw new UnsupportedOperationException("Percolator results do not contains protein-level analysis");
	}

	@Override
	public boolean hasProteinAnalysis() {
		return false;
	}

}
