/**
 * PepXmlGenericFileReader.java
 * @author Vagisha Sharma
 * Oct 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser.pepxml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.analysis.peptideProphet.GenericPeptideProphetResultIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROCPoint;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultPeptideBuilder;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.PeptideProphetResultData;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.general.impl.Enzyme;
import org.yeastrc.ms.domain.protinfer.proteinProphet.Modification;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.impl.ParamBean;
import org.yeastrc.ms.domain.search.impl.ResidueModification;
import org.yeastrc.ms.domain.search.impl.RunSearchBean;
import org.yeastrc.ms.domain.search.impl.SearchDatabase;
import org.yeastrc.ms.domain.search.impl.TerminalModification;
import org.yeastrc.ms.domain.search.pepxml.PepXmlSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.PepxmlDataProvider;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.util.AminoAcidUtilsFactory;
import org.yeastrc.ms.util.BaseAminoAcidUtils;

/**
 * 
 */
public abstract class PepXmlGenericFileReader <T extends PepXmlSearchScanIn<G, R>,
                                               G extends GenericPeptideProphetResultIn<R>, 
                                               R extends MsSearchResultIn,
                                               S extends MsSearchIn> 
    implements PepxmlDataProvider<T> {

    String pepXmlFilePath;
    private InputStream inputStr = null;
    XMLStreamReader reader = null;
    
    // these will be read once for the entire file
    boolean isTppFile = false;
    boolean refreshParserRun = false;
    boolean peptideProphetRun = false;
    private String peptideProphetVersion;
    private PeptideProphetROC peptideProphetRoc;
    
    // these will be read for each <msms_run_summary> element
    private Program searchProgram;
    List<MsResidueModificationIn> searchDynamicResidueMods;
    List<MsResidueModificationIn> searchStaticResidueMods;
    List<MsTerminalModificationIn> searchStaticTerminalMods;
    List<MsTerminalModificationIn> searchDynamicTerminalMods;
    List<Param> searchParams;
    Enzyme enzyme;
    SearchDatabase searchDatabase;
    private String currentRunSearchName = null;
    
    private Map<Character, BigDecimal> staticModMap;
    private Map<Character, BigDecimal> dynamicModMap;
    
    
    private PeptideProphetResultPeptideBuilder peptideResultBuilder;
    
    
    private boolean atFirstSpectrumQueryElement = false;
    
    private static final Logger log = Logger.getLogger(PepXmlGenericFileReader.class.getName());
    
    
    public void open(String filePath) throws DataProviderException {
        
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            inputStr = new FileInputStream(filePath);
            reader = inputFactory.createXMLStreamReader(inputStr);
            
            peptideResultBuilder = PeptideProphetResultPeptideBuilder.getInstance();
            
            readHeadersForPipelineAnalysis(reader);
        }
        catch (FileNotFoundException e) {
            throw new DataProviderException("File not found: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        this.pepXmlFilePath = filePath;
    }
    
    @Override
    public void close() {
        if (reader != null) {
            try {reader.close();}
            catch (XMLStreamException e) {}
        }
        
        if(inputStr != null) {
            try {inputStr.close();}
            catch(IOException e){}
        }
    }

    // -------------------------------------------------------------------------------------
    // PIPELINE_ANALYSIS
    // -------------------------------------------------------------------------------------
    void readHeadersForPipelineAnalysis(XMLStreamReader reader) throws XMLStreamException,
            DataProviderException {
        
        // TODO read date here
        // <msms_pipeline_analysis date="2009-09-14T16:38:30" 
        while(reader.hasNext()) {
            if(reader.next() == XMLStreamReader.START_ELEMENT) {
                
            	if(reader.getLocalName().equalsIgnoreCase("msms_pipeline_analysis")) {
            		this.isTppFile = true;
            	}
            	else if(reader.getLocalName().equalsIgnoreCase("analysis_summary")) {
                    // refresh parser analysis
                    if(reader.getAttributeValue(null,"analysis").equalsIgnoreCase("database_refresh")) {
                        refreshParserRun = true;
                    }
                    // peptide prophet analysis
                    else if(reader.getAttributeValue(null,"analysis").equalsIgnoreCase("peptideprophet")) {
                        peptideProphetRun = true;
                        readPeptideProphetAnalysisSummary(reader);
                    }
                }
                
                // we have come too far
                else if(reader.getLocalName().equalsIgnoreCase("msms_run_summary")) {
                  return;
              }
            }
        }
    }
    
    // -------------------------------------------------------------------------------------
    // PEPTIDE_PROPHET SUMMARY
    // -------------------------------------------------------------------------------------
    private void readPeptideProphetAnalysisSummary(XMLStreamReader reader) throws XMLStreamException {
        
        boolean inPPAnalysis = false;
        this.peptideProphetRoc = new PeptideProphetROC();
        
        while(reader.hasNext()) {
            int evtType = reader.next();
            if(evtType == XMLStreamReader.START_ELEMENT) {
                
                if (reader.getLocalName().equalsIgnoreCase("peptideprophet_summary")) {
                    this.peptideProphetVersion = reader.getAttributeValue(null, "version");
                    inPPAnalysis = true;
                }
                else if (reader.getLocalName().equalsIgnoreCase("roc_data_point") && inPPAnalysis) {
                    // <roc_data_point min_prob="0.99" sensitivity="0.4384" error="0.0024" num_corr="1123" num_incorr="3"/>
                    PeptideProphetROCPoint rocPoint = new PeptideProphetROCPoint();
                    rocPoint.setMinProbability(Double.parseDouble(reader.getAttributeValue(null, "min_prob")));
                    rocPoint.setSensitivity(Double.parseDouble(reader.getAttributeValue(null, "sensitivity")));
                    rocPoint.setError(Double.parseDouble(reader.getAttributeValue(null, "error")));
                    rocPoint.setNumCorrect(Integer.parseInt(reader.getAttributeValue(null, "num_corr")));
                    rocPoint.setNumIncorrect(Integer.parseInt(reader.getAttributeValue(null, "num_incorr")));
                    this.peptideProphetRoc.addRocPoint(rocPoint);
                }
                
            }
            else if(evtType == XMLStreamReader.END_ELEMENT) {
                // we have come to the end of what we need
                if(reader.getLocalName().equalsIgnoreCase("peptideprophet_summary")) {
                    break;
                }
            }
        }
    }
    
    
    String getFileDirectory() {
        return new File(this.pepXmlFilePath).getParent();
    }
    
    public boolean isRefreshParserRun() {
        return refreshParserRun;
    }
    
    public boolean isPeptideProphetRun() {
    	return peptideProphetRun;
    }
    
    public boolean isTPPFile() {
    	return this.isTppFile;
    }
    
    public String getPeptideProphetVersion() {
        return this.peptideProphetVersion;
    }
    
    public PeptideProphetROC getPeptideProphetRoc() {
        return peptideProphetRoc;
    }
    
    
    // -------------------------------------------------------------------------------------
    // RUN SEARCH
    // -------------------------------------------------------------------------------------
    @Override
    public boolean hasNextRunSearch() throws DataProviderException {
        if (reader == null)
            return false;
        try {
            while(reader.hasNext()) {
                int evtType = reader.getEventType();
                if (evtType == XMLStreamReader.START_ELEMENT) {
                    if (reader.getLocalName().equalsIgnoreCase("msms_run_summary")) {
                        
                        // get the name of the input file
                        currentRunSearchName = new File(reader.getAttributeValue(null, "base_name")).getName();
                        // System.out.println(currentRunSearchName);
                        
                        // re-initialize and read all summaries
                        readHeadersForRun(reader);
                        
                        return true;
                    }
                }
                reader.next();
            }
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+pepXmlFilePath, e);
        }
        return false;
    }
    
    @Override
    public String getRunSearchName() {
        return currentRunSearchName;
    }
    
    private void readHeadersForRun(XMLStreamReader reader) throws XMLStreamException,
        DataProviderException {
        
        initalizeFields();
        readEnzyme(reader);
        readRunSummary(reader);
    }

    private void initalizeFields() {
        
        searchProgram = null;
        searchParams = new ArrayList<Param>();
        searchDynamicResidueMods = new ArrayList<MsResidueModificationIn>();
        searchStaticResidueMods = new ArrayList<MsResidueModificationIn>();
        searchStaticTerminalMods = new ArrayList<MsTerminalModificationIn>();
        searchDynamicTerminalMods = new ArrayList<MsTerminalModificationIn>();
        
        staticModMap = new HashMap<Character, BigDecimal>();
        dynamicModMap = new HashMap<Character, BigDecimal>();
    }
    
    
    private void readEnzyme(XMLStreamReader reader) throws XMLStreamException {
        
        while(reader.hasNext()) {
            if(reader.next() == XMLStreamReader.END_ELEMENT) {
                if(reader.getLocalName().equalsIgnoreCase("sample_enzyme"))
                    return;
            }
            if(reader.next() == XMLStreamReader.START_ELEMENT) {
                if(reader.getLocalName().equalsIgnoreCase("sample_enzyme")) {
                    this.enzyme = new Enzyme();
                    enzyme.setName(reader.getAttributeValue(null,"name"));
                }
                else if(reader.getLocalName().equalsIgnoreCase("specificity")) {
                    enzyme.setCut(reader.getAttributeValue(null, "cut"));
                    enzyme.setNocut(reader.getAttributeValue(null, "no_cut"));
                    String sense = reader.getAttributeValue(null, "sense");
                    if(sense != null) {
                        if(sense.equals("C"))
                            enzyme.setSense(Sense.CTERM);
                        else
                            enzyme.setSense(Sense.NTERM);
                    }
                }
            }
        }
    }
    
    private void readRunSummary(XMLStreamReader reader) throws XMLStreamException, DataProviderException {
        
        while(reader.hasNext()) {
            if(reader.next() == XMLStreamReader.START_ELEMENT) {
                if(reader.getLocalName().equalsIgnoreCase("search_summary")) {
                    readRunSearchSummary(reader);
                }
                
                else if(reader.getLocalName().equalsIgnoreCase("analysis_timestamp")) {
                    // TODO are we interested in the contents of this element?
                }
                
                // we have come too far
                else if(reader.getLocalName().equalsIgnoreCase("spectrum_query")) {
                    atFirstSpectrumQueryElement = true;
                    return;
                }
            }
        }
    }
    
    
    private void readRunSearchSummary(XMLStreamReader reader) throws XMLStreamException, DataProviderException {
        
        // first read the attributes that tell us the name of the search program and
        // anything else we are interested in
        // search_engine="MASCOT" precursor_mass_type="monoisotopic" fragment_mass_type="monoisotopic"
        String value = reader.getAttributeValue(null,"search_engine");
        if(value != null) {
            this.searchProgram = PepXmlUtils.parseProgram(value);
        }
        value = reader.getAttributeValue(null,"precursor_mass_type");
        if(value != null) {
            Param param = new ParamBean("precursor_mass_type", value);
            searchParams.add(param);
        }
        
        value = reader.getAttributeValue(null,"fragment_mass_type");
        if(value != null) {
            Param param = new ParamBean("fragment_mass_type", value);
            searchParams.add(param);
        }
        
        // read other interesting elements within the search_summary element
        while(reader.hasNext()) {
            
            int evtType = reader.next();
            
            if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("search_summary")) {
                return;
            }
            
            else if(evtType == XMLStreamReader.START_ELEMENT) {
                if(reader.getLocalName().equalsIgnoreCase("search_database")) {
                    this.searchDatabase = new SearchDatabase();
                    searchDatabase.setServerPath(reader.getAttributeValue(null, "local_path"));
                }
                else if(reader.getLocalName().equalsIgnoreCase("enzymatic_search_constraint")) {
                    readMaxNumInternalCleavages(reader);
                    readMinEnzymaticTermini(reader);
                }
                else if (reader.getLocalName().equalsIgnoreCase("aminoacid_modification")) {
                    readResidueModification(reader);
                }
                else if (reader.getLocalName().equalsIgnoreCase("terminal_modification")) {
                    readTerminalModification(reader);
                }
                else if (reader.getLocalName().equalsIgnoreCase("parameter")) {
                    readParameters(reader);
                }
            }
        }
    }

    private void readMinEnzymaticTermini(XMLStreamReader reader)
            throws DataProviderException {
        String value;
        value = reader.getAttributeValue(null,"min_number_termini");
        try {
            Integer.parseInt(value);
            Param param = new ParamBean("min_number_termini", value);
            searchParams.add(param);
        }
        catch(NumberFormatException e) {
            throw new DataProviderException("Invalid value for min_number_termini: "+value, e);
        }
    }

    private void readMaxNumInternalCleavages(XMLStreamReader reader)
            throws DataProviderException {
        String value;
        value = reader.getAttributeValue(null,"max_num_internal_cleavages");
        if(value != null) {
            try {
                Integer.parseInt(value);
                Param param = new ParamBean("max_num_internal_cleavages", value);
                searchParams.add(param);
            }
            catch(NumberFormatException e) {
                throw new DataProviderException("Invalid value for max_num_internal_cleavages: "+value, e);
            }
        }
    }
    
    private void readResidueModification(XMLStreamReader reader) throws XMLStreamException {
        
        // <aminoacid_modification aminoacid="M" massdiff="15.9949" mass="147.0354" variable="Y" symbol="*"/>
        // <aminoacid_modification aminoacid="C" massdiff="57.0215" mass="160.0306" variable="N"/>
        
        String variable = reader.getAttributeValue(null, "variable");
        // dynamic modifications
        if("Y".equalsIgnoreCase(variable)) {
            String aa = reader.getAttributeValue(null, "aminoacid");
            String symbol = reader.getAttributeValue(null, "symbol");
            String massdiff = reader.getAttributeValue(null, "massdiff");
            ResidueModification mod = new ResidueModification();
            mod.setModificationMass(new BigDecimal(massdiff));
            if(symbol != null)
                mod.setModificationSymbol(symbol.charAt(0));
            mod.setModifiedResidue(aa.charAt(0));
            this.searchDynamicResidueMods.add(mod);
            this.dynamicModMap.put(aa.charAt(0), new BigDecimal(massdiff));
        }
        
        // static modifications
        else if("N".equalsIgnoreCase(variable)) {
            String aa = reader.getAttributeValue(null, "aminoacid");
            String massdiff = reader.getAttributeValue(null, "massdiff");
            String mass = reader.getAttributeValue(null, "mass");
            
            if(Double.parseDouble(mass) - Double.parseDouble(massdiff) == 0) {
            	// If mass == massDiff this is not really a static modification
                // This case happens in pepXML files generated with the Mascot to pepxml converter
                // Non-standard amino acid and their masses are stuck in the 
                // <aminoacid_modification> elements
                // Example: <aminoacid_modification aminoacid="X" mass="111.000000" massdiff="111.000000" variable="N"/>
            	// We will log a warning but save this as a static modification
            	log.warn("mass and massdiff have same value: "+aa+" mass: "+mass+" massdiff: "+massdiff);
            	if(AminoAcidUtilsFactory.getAminoAcidUtils().isAminoAcid(aa.charAt(0))) {
            		log.error("!!! mass and modmass same for a STANDARD AMINO ACID!!!");
            	}
            }
            ResidueModification mod = new ResidueModification();
            mod.setModificationMass(new BigDecimal(massdiff));
            mod.setModifiedResidue(aa.charAt(0));
            this.searchStaticResidueMods.add(mod);
            this.staticModMap.put(aa.charAt(0), new BigDecimal(massdiff));
        }
    }
    
    private void readTerminalModification(XMLStreamReader reader) throws XMLStreamException {
        
        // <terminal_modification terminus="n" mass="305.213185" massdiff="304.205353" variable="N" protein_terminus="N"/>
        
        String variable = reader.getAttributeValue(null, "variable");
        // dynamic modifications
        if("Y".equalsIgnoreCase(variable)) {
            String terminus = reader.getAttributeValue(null, "terminus");
            String massdiff = reader.getAttributeValue(null, "massdiff");
            TerminalModification mod = new TerminalModification();
            mod.setModificationMass(new BigDecimal(massdiff));
            if(terminus != null)
                mod.setModifiedTerminal(Terminal.instance(terminus.charAt(0)));
            this.searchDynamicTerminalMods.add(mod);
        }
        
        // static modifications
        else if("N".equalsIgnoreCase(variable)) {
            String terminus = reader.getAttributeValue(null, "terminus");
            String massdiff = reader.getAttributeValue(null, "massdiff");
            TerminalModification mod = new TerminalModification();
            mod.setModificationMass(new BigDecimal(massdiff));
            if(terminus != null)
                mod.setModifiedTerminal(Terminal.instance(terminus.charAt(0)));
            this.searchStaticTerminalMods.add(mod);
        }
    }
    
    private void readParameters(XMLStreamReader reader) throws XMLStreamException {
        
        String name = reader.getAttributeValue(null, "name");
        String value = reader.getAttributeValue(null, "value");
        Param param = new ParamBean(name, value);
        searchParams.add(param);
    }

    
    public Program getSearchProgram() {
        return this.searchProgram;
    }

    @Override
    public MsRunSearchIn getRunSearchHeader() throws DataProviderException {
        RunSearchBean runSearch = new RunSearchBean();
        if(this.searchProgram == Program.SEQUEST)
            runSearch.setSearchFileFormat(SearchFileFormat.PEPXML_SEQ);
        else if(this.searchProgram == Program.MASCOT)
            runSearch.setSearchFileFormat(SearchFileFormat.PEPXML_MASCOT);
        else if(this.searchProgram == Program.XTANDEM)
            runSearch.setSearchFileFormat(SearchFileFormat.PEPXML_XTANDEM);
        else if(this.searchProgram == Program.COMET)
            runSearch.setSearchFileFormat(SearchFileFormat.PEPXML_COMET);
        else
            throw new DataProviderException("Unknown search program for pepxml file: "+this.pepXmlFilePath);
        return runSearch;
    }
    
   

    // -------------------------------------------------------------------------------------
    // SEARCH SCAN
    // -------------------------------------------------------------------------------------
    @Override
    /**
     * Returns true if there is a spectrum_query element to be read
     */
    public boolean hasNextSearchScan() throws DataProviderException {
        if (reader == null)
            return false;
            
        try {
            while(reader.hasNext()) {
                
                if(!atFirstSpectrumQueryElement) {
                    reader.next();
                }
                atFirstSpectrumQueryElement = false;
                
                int evtId = reader.getEventType();
                if (evtId == XMLStreamReader.END_ELEMENT) {
                    // this is the end of one msms_run_summary
                    if (reader.getLocalName().equals("msms_run_summary"))  {
                        return false;
                    }
                }
                else if (evtId == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("spectrum_query")) {
                    return true;
                }
            }
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+pepXmlFilePath, e);
        }
        return false;
    }
    
    
    public T getNextSearchScan() throws DataProviderException {

        T scan = initNewSearchScan();
        readPepXmlSearchScan(scan);

        // read the search hits for this scan
        try {
            readHitsForScan(scan, searchDynamicResidueMods);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+pepXmlFilePath, e);
        }
        return (T) scan;
    }
    
    //-------------------------------------------------------------------------------------------
    // To be implemented by subclasses
    //-------------------------------------------------------------------------------------------
    public abstract S getSearch();
    
    protected abstract T initNewSearchScan();
    
    protected abstract G initNewPeptideProphetResult();
    
    protected abstract R initNewSearchResult();
    
    protected abstract void readProgramSpecificResult(R result);
    
    protected abstract void readProgramSpecificScore(R result, String name, String value);
    
    protected abstract double getMonoAAMass(char aa);
    
    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------
    
    
    // ---------------------------------------------------------------------------------------
    // read attributes for the <spectrum_query> element
    // ---------------------------------------------------------------------------------------
    void readPepXmlSearchScan(T scan) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrib = reader.getAttributeLocalName(i);
            String val = reader.getAttributeValue(i);
            if (attrib.equalsIgnoreCase("start_scan"))
                scan.setScanNumber(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("precursor_neutral_mass"))
                // NOTE: We store M+H in the database
                scan.setObservedMass(new BigDecimal(val).add(BigDecimal.valueOf(BaseAminoAcidUtils.PROTON)));
            else if (attrib.equalsIgnoreCase("assumed_charge"))
                scan.setCharge(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("retention_time_sec"))
                scan.setRetentionTime(new BigDecimal(val));
        }
    }
    
    private void readHitsForScan(T scanResult, List<MsResidueModificationIn> searchDynaResidueMods) 
        throws XMLStreamException, DataProviderException {

        while(reader.hasNext()) {
            int evtType = reader.next();
            if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("spectrum_query"))
                break;
            if (evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("search_hit")) {
                G hit = readSearchHit(scanResult, searchDynaResidueMods);
                scanResult.addSearchResult(hit);
            }
        }  
    }

    // ---------------------------------------------------------------------------------------
    // read contents for the <search_hit> element
    // ---------------------------------------------------------------------------------------
    private G readSearchHit(T scanResult,
            List<MsResidueModificationIn> searchDynaResidueMods) 
        throws XMLStreamException, DataProviderException {


        G hit = initNewPeptideProphetResult();
        
        int numMatchingProteins = 0;
        
        String peptideSeq = null;
        char preResidue = 0;
        char postResidue = 0;
        String prAcc = null;
        String prDescr = null;

        
        int numEnzymaticTermini = 0;

        R searchResult = initNewSearchResult();
        searchResult.setScanNumber(scanResult.getScanNumber());
        searchResult.setCharge(scanResult.getCharge());
        searchResult.setObservedMass(scanResult.getObservedMass());

        // read the attributes
        for (int i = 0; i < reader.getAttributeCount(); i++) {

            String attrib = reader.getAttributeLocalName(i);
            String val = reader.getAttributeValue(i);
            if (attrib.equalsIgnoreCase("peptide"))
                peptideSeq = val;
            else if (attrib.equalsIgnoreCase("peptide_prev_aa"))
                preResidue = Character.valueOf(val.charAt(0));
            else if (attrib.equalsIgnoreCase("peptide_next_aa"))
                postResidue = Character.valueOf(val.charAt(0));
            else if (attrib.equalsIgnoreCase("protein"))
                prAcc = val;
            else if (attrib.equalsIgnoreCase("protein_descr"))
                prDescr = val;
            else if (attrib.equalsIgnoreCase("num_tot_proteins"))
                numMatchingProteins = Integer.parseInt(val);
            else if(attrib.equalsIgnoreCase("num_tol_term")) 
                numEnzymaticTermini = Integer.parseInt(val);
        }
        
        readProgramSpecificResult(searchResult); // read in Sequest or Mascot specific scores
        
        DbLocus locus1 = new DbLocus(prAcc, prDescr);
        locus1.setNtermResidue(preResidue);
        locus1.setCtermResidue(postResidue);
        locus1.setNumEnzymaticTermini(numEnzymaticTermini);
        searchResult.addMatchingProteinMatch(locus1);
        
        
        List<Modification> resultModifications = new ArrayList<Modification>();
        
        // read other interesting nested elements
        while(reader.hasNext()) {
            int evtType = reader.next();
            if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("search_hit"))
                break;

            if (evtType == XMLStreamReader.START_ELEMENT) {

                // read the modification information
                if(reader.getLocalName().equalsIgnoreCase("modification_info")) {
                    resultModifications = readModifications(peptideSeq, reader);
                }

                // read the <alternative_protein> elements
                else if (reader.getLocalName().equalsIgnoreCase("alternative_protein")) {
                    DbLocus locus = readAlternativeProtein();
                    searchResult.addMatchingProteinMatch(locus);
                }
                // read the <search_score> elements
                else if (reader.getLocalName().equalsIgnoreCase("search_score")) {
                    String scoreType = reader.getAttributeValue(null, "name");
                    String scoreVal = reader.getAttributeValue(null, "value");
                    readProgramSpecificScore(searchResult, scoreType, scoreVal);
                }
                // read the <analysis_result> elemets
                else if (reader.getLocalName().equalsIgnoreCase("analysis_result")) {
                    String analysisProgram = reader.getAttributeValue(null, "analysis");
                    if(analysisProgram.equalsIgnoreCase("peptideprophet")) {
                        PeptideProphetResultDataIn ppRes = readPeptideProphetHitAnalysis(reader);
                        hit.setPeptideProphetResult(ppRes);
                    }
                }
            }

        } // end of parsing

        // set the result peptide
        MsSearchResultPeptide resultPeptide = peptideResultBuilder.buildResultPeptide(
                peptideSeq, preResidue, postResidue, resultModifications);
        searchResult.setResultPeptide(resultPeptide);
        

        if (numMatchingProteins != searchResult.getProteinMatchList().size()) {
//          log.warn("value of attribute num_tot_proteins("+numMatchingProteins+
//          ") does not match number of proteins("+searchResult.getProteinMatchList().size()+") found for this hit. "
//          +"Scan# "+scanResult.getScanNumber()+"; sequence: "+peptideSeq);
//          throw new DataProviderException("value of attribute num_tot_proteins("+numMatchingProteins+
//          ") does not match number of proteins("+seqRes.getProteinMatchList().size()+") found for this hit. "
//          +"Scan# "+scan.getScanNumber()+"; hit rank: "+seqRes.getSequestResultData().getxCorrRank());

        }

        hit.setSearchResult(searchResult);
        return hit;
    }
    
    // ---------------------------------------------------------------------------------
    // read contents of the <analysis_result analysis="peptideprophet"> element
    // ---------------------------------------------------------------------------------
    private PeptideProphetResultDataIn readPeptideProphetHitAnalysis(XMLStreamReader reader) throws NumberFormatException, XMLStreamException {

        PeptideProphetResultData ppRes = new PeptideProphetResultData();
        
        // read all the interesting children elements
        while(reader.hasNext()) {
            
            int evtType = reader.next();
            
            if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("analysis_result"))
                break;
            
            else if(evtType == XMLStreamReader.START_ELEMENT) {
                if (reader.getLocalName().equalsIgnoreCase("peptideprophet_result")) {
                    String probability = reader.getAttributeValue(null, "probability");
                    String allNttProb = reader.getAttributeValue(null, "all_ntt_prob");
                    ppRes.setAllNttProb(allNttProb);
                    ppRes.setProbability(Double.parseDouble(probability));
                }
                // read the <parameter> elements (PeptideProphet scores)
                else if (reader.getLocalName().equalsIgnoreCase("parameter")) {
                    String scoreType = reader.getAttributeValue(null, "name");
                    String scoreVal = reader.getAttributeValue(null, "value");
                    if (scoreType.equalsIgnoreCase("fval"))
                        ppRes.setfVal(Double.parseDouble(scoreVal));
                    else if (scoreType.equalsIgnoreCase("ntt"))
                        ppRes.setNumEnzymaticTermini(Integer.parseInt(scoreVal));
                    else if (scoreType.equalsIgnoreCase("nmc"))
                        ppRes.setNumMissedCleavages(Integer.parseInt(scoreVal));
                    else if (scoreType.equalsIgnoreCase("massd"))
                        ppRes.setMassDifference(Double.parseDouble(scoreVal));
                }
            }
        }
        return ppRes;
    }

    // ---------------------------------------------------------------------------------
    // read contents of <modification_info> element
    // ---------------------------------------------------------------------------------
    private List<Modification> readModifications(String peptideSeq, XMLStreamReader reader) throws XMLStreamException, DataProviderException {
       
        List<Modification> dynamicMods = new ArrayList<Modification>();
        
        // read any relevant attributes
        // String modifiedPeptide = reader.getAttributeValue(null, "modified_peptide");
        String modNtermMass = reader.getAttributeValue(null, "mod_nterm_mass");
        String modCtermMass = reader.getAttributeValue(null, "mod_ctermMass");
        
        // N-term modification
        if(modNtermMass != null) {
        	double mass = Double.parseDouble(modNtermMass);
        	mass -= BaseAminoAcidUtils.NTERM_MASS;
            // add this only if this is a dynamic terminal modification
        	boolean isStaticTermMod = false;
        	for(MsTerminalModificationIn mMod: this.searchStaticTerminalMods) {
        		if(mMod.getModifiedTerminal() != Terminal.NTERM)
        			continue;
        		double diff = mass - mMod.getModificationMass().doubleValue();
        		if(Math.abs(diff) <= 0.05) {
        			isStaticTermMod = true;
        			break;
        		}
        	}
        	
        	if(!isStaticTermMod) {
        		boolean foundMatch = false;
        		for(MsTerminalModificationIn mMod: this.searchDynamicTerminalMods) {
        			if(mMod.getModifiedTerminal() != Terminal.NTERM)
            			continue;
        			double diff = mass - mMod.getModificationMass().doubleValue();
            		if(Math.abs(diff) <= 0.05) {
        				dynamicMods.add(new Modification(mMod.getModificationMass(), Terminal.NTERM));
        				foundMatch = true;
        			}
        		}
        		if(!foundMatch)  {
        			throw new DataProviderException("No match found for dynamic N-term mod mass: "+modNtermMass);
        		}
        		
        	}
        }
        
        // C-term modification
        if(modCtermMass != null) {
            double mass = Double.parseDouble(modCtermMass);
            mass -= BaseAminoAcidUtils.CTERM_MASS;
            // add this only if this is a dynamic terminal modification
            boolean isStaticTermMod = false;
        	for(MsTerminalModificationIn mMod: this.searchStaticTerminalMods) {
        		if(mMod.getModifiedTerminal() != Terminal.CTERM)
        			continue;
        		double diff = mass - mMod.getModificationMass().doubleValue();
        		if(Math.abs(diff) <= 0.05) {
        			isStaticTermMod = true;
        			break;
        		}
        	}
        	if(!isStaticTermMod) {
        		boolean foundMatch = false;
        		for(MsTerminalModificationIn mMod: this.searchDynamicTerminalMods) {
        			if(mMod.getModifiedTerminal() != Terminal.CTERM)
            			continue;
        			double diff = mass - mMod.getModificationMass().doubleValue();
            		if(Math.abs(diff) <= 0.05) {
        				dynamicMods.add(new Modification(mMod.getModificationMass(), Terminal.CTERM));
        				foundMatch = true;
        			}
        		}
        		if(!foundMatch)  {
        			throw new DataProviderException("No match found for dynamic C-term mod mass: "+modCtermMass);
        		}
        	}
        }
        
        
        
        // read useful nested elements
        while(reader.hasNext()) {
            int evtType = reader.next();
            if(evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("modification_info"))
                break;
            if(evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("mod_aminoacid_mass")) {
                
                int pos = Integer.parseInt(reader.getAttributeValue(null, "position"));
                BigDecimal mass = new BigDecimal(reader.getAttributeValue(null, "mass"));
                
                // Add only if this is a dynamic residue modification  
                // this will also match it against the dynamic modifications used for the search
                Modification mMod = makeModification(peptideSeq.charAt(pos - 1), pos-1, mass);
                if(mMod != null) {
                    dynamicMods.add(mMod);
                }
            }
        }
        return dynamicMods;
    }

    private Modification makeModification(char modChar, int position, BigDecimal mass) throws DataProviderException {
        
        // the purpose is to figure out if this represents a dynamic modificaion (residue)
        // we also want to get the massDiff for this modification since the modification_info
        // element has mass_of_aminoacid + modification_mass. 
        
        double massDiff = getModMassDiff(modChar, mass, false);
        
        
        // If this mass difference and modChar match a static modification return null
        BigDecimal modMass = staticModMap.get(modChar);
        if(modMass != null && Math.abs(massDiff - modMass.doubleValue()) < 0.5) {
            return null; // this is a static modification
        }
        
        massDiff = getModMassDiff(modChar, mass, true);
        modMass = dynamicModMap.get(modChar);
        if(modMass != null) {
            if(Math.abs(massDiff - modMass.doubleValue()) < 0.5) {
                return new Modification(position, modMass);
            }
            else {
                throw new DataProviderException("Found a match for modified residue: "+modChar+
                        " but no match for mass: "+mass.doubleValue());
            }
        }
        throw new DataProviderException("No modification match found for : "+modChar+
                " and mass: "+mass.doubleValue());
    }
    
    protected final double getModMassDiff(char modChar, BigDecimal mass, boolean subtractStatic) {
        // modification mass in pepXml files are: mass_of_aminoacid + modification_mass
        // we need just the modification_mass
        double massDiff = mass.doubleValue() - getMonoAAMass(modChar); // AminoAcidUtils.monoMass(modChar);
        
        if(massDiff == mass.doubleValue()) {
        	// If massDiff is the same as mass it means we did not find mass for this amino acid
        	// This can happen for non-standard amino acids
        	// We will log a warning
        	log.warn("No mass found for amino acid: "+modChar);
        }
        // if this is amino acid has a static modification, subtract that 
        // A residue could have both static and dynamic modifications(??)
        if(subtractStatic) {
            BigDecimal staticModMass = staticModMap.get(modChar);
            if(staticModMass != null) {
                massDiff -= staticModMass.doubleValue();
            }
        }
        return massDiff;
    }
    
    // ---------------------------------------------------------------------------------
    // read attributes of <alternative_protein> element
    // ---------------------------------------------------------------------------------
    private DbLocus readAlternativeProtein() {
        String prAcc = null;
        String prDescr = null;
        char preResidue = 0;
        char postResidue = 0;
        int numEnzymaticTermini = 0;

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrib = reader.getAttributeLocalName(i);
            String val = reader.getAttributeValue(i);
            if (attrib.equalsIgnoreCase("protein"))
                prAcc = val;
            else if (attrib.equalsIgnoreCase("protein_descr"))
                prDescr = val;
            else if (attrib.equalsIgnoreCase("peptide_prev_aa"))
                preResidue = Character.valueOf(val.charAt(0));
            else if (attrib.equalsIgnoreCase("peptide_next_aa"))
                postResidue = Character.valueOf(val.charAt(0));
            else if (attrib.equalsIgnoreCase("num_tol_term")) 
                numEnzymaticTermini = Integer.parseInt(val);
        }
        DbLocus locus = new DbLocus(prAcc, prDescr);
        locus.setNtermResidue(preResidue);
        locus.setCtermResidue(postResidue);
        locus.setNumEnzymaticTermini(numEnzymaticTermini);
        return locus;
    }
   
}
