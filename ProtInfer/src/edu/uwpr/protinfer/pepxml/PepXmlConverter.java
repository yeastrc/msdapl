/**
 * PepXmlConverter.java
 * @author Vagisha Sharma
 * Feb 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.pepxml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.GenericSearchDAO.MassType;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.util.AminoAcidUtilsFactory;
import org.yeastrc.ms.util.BaseAminoAcidUtils;

/**
 * 
 */
public abstract class PepXmlConverter <T extends MsSearchResult> {

    
    static final DAOFactory daofactory = DAOFactory.instance();
    static final MsRunDAO runDao = daofactory.getMsRunDAO();
    static final MsScanDAO scanDao = daofactory.getMsScanDAO();
    static final MsRunSearchDAO runSearchDao = daofactory.getMsRunSearchDAO();
    static final MsSearchResultDAO resultDao = daofactory.getMsSearchResultDAO();
    static final SQTSearchScanDAO sqtScanDao = daofactory.getSqtSpectrumDAO();
    
    private static final Logger log = Logger.getLogger(PepXmlConverter.class);
    
    private MassType fragmentMassType;
    private int spectrumQueryIndex  = 1;
    
    XMLStreamWriter initDocument(String outfile) throws XMLStreamException, FileNotFoundException {
        OutputStream out = new FileOutputStream(outfile);
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(out, "UTF-8");
        
        writeHeaders(writer);
        return writer;
    }
    
    private void writeHeaders(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartDocument("UTF-8", "1.0");
        newLine(writer);
        writer.writeDTD("<?xml-stylesheet type=\"text/xsl\" href=\"http://regis-web.systemsbiology.net/pepXML_std.xsl\"?>");
        newLine(writer);
    }
    
    //-------------------------------------------------------------------------------------------
    // ms_ms_pipeline_analysis
    //-------------------------------------------------------------------------------------------
    void startMsmsPipelineAnalysis(XMLStreamWriter writer, String outFilePath) throws XMLStreamException {
        writer.writeStartElement("msms_pipeline_analysis");
        
        writer.writeAttribute("date", getXMLDate(new Date()));
        writer.writeAttribute("summary_xml", outFilePath);
        
//        xmlns="http://regis-web.systemsbiology.net/pepXML" 
//        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
//        xsi:schemaLocation="http://regis-web.systemsbiology.net/pepXML /net/pr/vol1/ProteomicsResource/bin/TPP/bin/20080417-TPP_v3.5.3/schema/pepXML_v110.xsd"
        writer.writeAttribute("xmlns", "http://regis-web.systemsbiology.net/pepXML");
        writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        writer.writeAttribute("xsi:schemaLocation", "http://regis-web.systemsbiology.net/pepXML /net/pr/vol1/ProteomicsResource/bin/TPP/bin/20080417-TPP_v3.5.3/schema/pepXML_v110.xsd");
        newLine(writer);
    }
    
    final String getXMLDate(java.util.Date date) {
        DatatypeFactory df;
        try {
            df = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            log.error("Error converting to XML date.",e);
            throw new IllegalArgumentException("Error converting to XML date.",e);
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        XMLGregorianCalendar calendar = df.newXMLGregorianCalendar(gc);
        return calendar.toXMLFormat();
    }
    
    void endMsmsPipelineAnalysis(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
        newLine(writer);
        writer.writeEndDocument();
        writer.close();
    }
    
    //-------------------------------------------------------------------------------------------
    // ms_ms_run_summary
    //-------------------------------------------------------------------------------------------
    void startMsmsRunSummary(MsRun run, XMLStreamWriter writer) throws XMLStreamException {

        String basefile = run.getFileName();
        writer.writeStartElement("msms_run_summary");
        writer.writeAttribute("base_name", basefile);

        writer.writeAttribute("raw_data_type", "."+run.getRunFileFormat().name().toLowerCase());
        writer.writeAttribute("raw_data", "."+run.getRunFileFormat().name().toLowerCase());

        newLine(writer);
    }
    
    void endMsmsRunSummary(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
        newLine(writer);
    }
    
    //-------------------------------------------------------------------------------------------
    // enzymes
    //-------------------------------------------------------------------------------------------
    void writeEnzymes(MsSearch search, XMLStreamWriter writer)
            throws XMLStreamException {
        // write enzymes used
        List<MsEnzyme> enzymes = search.getEnzymeList();
        if (enzymes.size() > 0) {
            for(MsEnzyme enz: enzymes) {
                writer.writeStartElement("sample_enzyme");
                writer.writeAttribute("name", enz.getName());
                newLine(writer);
                // <specificity cut="KR" no_cut="P" sense="C"/>
                writer.writeStartElement("specificity");
                writer.writeAttribute("cut", enz.getCut());
                if(enz.getNocut() != null && enz.getNocut().length() > 0)
                    writer.writeAttribute("no_cut", enz.getNocut());
                writer.writeAttribute("sense", enz.getSense().getShortVal() == Sense.NTERM.getShortVal() ? "N" : "C");
                writer.writeEndElement();
                newLine(writer);

                writer.writeEndElement();
                newLine(writer);
            }
        }
        else {
            writer.writeStartElement("sample_enzyme");
            writer.writeAttribute("name","NONE");
            writer.writeEndElement();
            newLine(writer);
        }
    }
    
    
    //-------------------------------------------------------------------------------------------
    // search_summary
    //-------------------------------------------------------------------------------------------
    void writeSearchSummary(MsSearch search, XMLStreamWriter writer, String basefile)
            throws XMLStreamException {
        
        // search summary
        startSearchSummary(search, writer, basefile);
        
        // search database
        writeSearchDatabase(search, writer);
        
        // write enzymatic search constraint
        writeEnzymaticSearchConstraints(search, writer);
        
        // dynamic modifications
        writeModifications(search, writer);
        
        // subclass can write any program specif parameters
        writeProgramSpecificParams(search.getId(), writer);
        
        endSearchSummary(writer);
    }

    private void writeModifications(MsSearch search, XMLStreamWriter writer)
            throws XMLStreamException {
        //<aminoacid_modification aminoacid="M" massdiff="15.9990" mass="147.1916" variable="Y" symbol="*"/>
        List<MsResidueModification> dynamods = search.getDynamicResidueMods();
        for(MsResidueModification mod: dynamods) {
            writeResidueModification(mod, true, writer);
        }
        //<aminoacid_modification aminoacid="C" massdiff="57.0210" mass="160.1598" variable="N"/>
        List<MsResidueModification> staticmods = search.getStaticResidueMods();
        for(MsResidueModification mod: staticmods) {
            writeResidueModification(mod, false, writer);
        }
        
        // dynamic terminal modification
        List<MsTerminalModification> dynaTermMods = search.getDynamicTerminalMods();
        for(MsTerminalModification mod: dynaTermMods) {
            writeTerminalModification(mod, true, writer);
        }
        
        // dynamic residue modification
        List<MsTerminalModification> staticTermMods = search.getStaticTerminalMods();
        for(MsTerminalModification mod: staticTermMods) {
            writeTerminalModification(mod, false, writer);
        }
        
    }
    
    private void writeResidueModification(MsResidueModification mod, boolean dynamic, XMLStreamWriter writer) throws XMLStreamException {
        
        writer.writeStartElement("aminoacid_modification");
        writer.writeAttribute("aminoacid", String.valueOf(mod.getModifiedResidue()));
        
        double massDiff = mod.getModificationMass().doubleValue();
        String massDiffStr = massDiff < 0 ? "-"+massDiff : "+"+massDiff;
        writer.writeAttribute("massdiff", massDiffStr);
        
        double aaMass = massDiff;
        BaseAminoAcidUtils aaUtils = AminoAcidUtilsFactory.getAminoAcidUtils();
        if(fragmentMassType == MassType.AVG)
            aaMass += aaUtils.avgMass(mod.getModifiedResidue());
        else
            aaMass += aaUtils.monoMass(mod.getModifiedResidue());
        writer.writeAttribute("mass", String.valueOf(aaMass));
        
        if(dynamic)
            writer.writeAttribute("variable", "Y");
        else
            writer.writeAttribute("variable", "N");
        
        if(mod.getModificationSymbol() != MsResidueModification.EMPTY_CHAR) {
            writer.writeAttribute("symbol", String.valueOf(mod.getModificationSymbol()));
        }
        writer.writeEndElement();
        newLine(writer);
    }
    
    private void writeTerminalModification(MsTerminalModification mod, boolean dynamic, XMLStreamWriter writer) throws XMLStreamException {
        
        writer.writeStartElement("terminal_modification");
        
        //from pepXML schema: n for N-terminus, c for C-terminus
        writer.writeAttribute("terminus", String.valueOf(mod.getModifiedTerminal().toChar()).toLowerCase());
        
        double massDiff = mod.getModificationMass().doubleValue();
        String massDiffStr = massDiff < 0 ? "-"+massDiff : "+"+massDiff;
        writer.writeAttribute("massdiff", massDiffStr);
        
        // TODO from pepXML schema: Mass difference with respect to unmodified terminus
        // Not sure how to calculate this
        writer.writeAttribute("mass", massDiffStr);
        
        if(dynamic)
            writer.writeAttribute("variable", "Y");
        else
            writer.writeAttribute("variable", "N");
        
        if(mod.getModificationSymbol() != MsResidueModification.EMPTY_CHAR) {
            writer.writeAttribute("symbol", String.valueOf(mod.getModificationSymbol()));
        }
        writer.writeEndElement();
        newLine(writer);
    }

    private void writeEnzymaticSearchConstraints(MsSearch search,
            XMLStreamWriter writer) throws XMLStreamException {
        if(search.getEnzymeList().size() > 0) {
            MsEnzyme enzyme = search.getEnzymeList().get(0);
            writer.writeStartElement("enzymatic_search_constraint");
            writer.writeAttribute("enzyme", enzyme.getName());
            String maxNumIntClv = getMaxNumInternalClevages(search.getId());
            if(maxNumIntClv != null)
                writer.writeAttribute("max_num_internal_cleavages", maxNumIntClv);
            String minNumTermini = getMinNumTermini(search.getId());
            if(minNumTermini != null)
                writer.writeAttribute("min_number_termini", minNumTermini);
            writer.writeEndElement();
            newLine(writer);
        }
    }
    
    private void writeSearchDatabase(MsSearch search,
            XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("search_database");
        List<MsSearchDatabase> dbs = search.getSearchDatabases();
        writer.writeAttribute("local_path", dbs.get(0).getDatabaseFileName());
        writer.writeAttribute("type", "AA");
        writer.writeEndElement();
        newLine(writer);
    }

    private void startSearchSummary(MsSearch search, XMLStreamWriter writer, String basefile)
            throws XMLStreamException {
        writer.writeStartElement("search_summary");
        writer.writeAttribute("base_name", basefile);
        writer.writeAttribute("search_engine", search.getSearchProgram().toString());
        
        // mass type used for the search
        String pmt = getPrecursorMassType(search.getId()) == MassType.MONO ? "monoisotopic" : "average";
        writer.writeAttribute("precursor_mass_type", pmt);
        this.fragmentMassType = getFragmentMassType(search.getId());
        pmt = fragmentMassType == MassType.MONO ? "monoisotopic" : "average";
        
        writer.writeAttribute("fragment_mass_type", pmt);
        writer.writeAttribute("search_id", String.valueOf(search.getId()));
        
        //out_data_type="out" out_data=".tgz"
        writer.writeAttribute("out_data_type", "out");
        writer.writeAttribute("out_data", ".tgz");
        newLine(writer);
    }
    
    private void endSearchSummary(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
        newLine(writer);
    }
    
    //-------------------------------------------------------------------------------------------
    // spectrum_query
    //-------------------------------------------------------------------------------------------
    void writeResultsForScan(List<T> results, List<MsResidueModification> staticMods, String basefile, XMLStreamWriter writer) throws XMLStreamException {
        
        if(results.size() == 0) {
            log.warn("No search results found");
            return;
        }
        
        // sort results by charge
        Collections.sort(results, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return Integer.valueOf(o1.getCharge()).compareTo(o2.getCharge());
            }});
        
        // get the scan
        MsScan scan = scanDao.load(results.get(0).getScanId());
        
        int lastCharge = -1;
        List<T> resForScanCharge = new ArrayList<T>();
        for(T result: results) {
            if(result.getCharge() != lastCharge) {
                if(resForScanCharge.size() > 0) {
                    writeResultsForScanCharge(resForScanCharge, staticMods, scan, writer, basefile);
                }
                resForScanCharge.clear();
                lastCharge = result.getCharge();
            }
            resForScanCharge.add(result);
        }
        writeResultsForScanCharge(resForScanCharge, staticMods, scan, writer, basefile);
    }
    
    private void writeResultsForScanCharge(List<T> resForScanCharge, List<MsResidueModification> staticMods, MsScan scan, XMLStreamWriter writer, 
            String filename) 
        throws XMLStreamException {
        
        if(resForScanCharge.size() == 0)
            return;
        
        
        startSpectrumQuery(resForScanCharge, scan, writer, filename);
        
        // sort results by rank
        sortResultsByRank(resForScanCharge);
        
        for(T result: resForScanCharge) {
            
            writeSearchResult(result, resForScanCharge, staticMods, scan, writer);
        }
        writer.writeEndElement(); // spectrum_query
        newLine(writer);
    }

    private void writeSearchResult(T result, List<T> resForScanCharge, List<MsResidueModification> staticMods,
            MsScan scan, XMLStreamWriter writer) throws XMLStreamException {
        
        MsSearchResultPeptide peptide = result.getResultPeptide();
        List<MsSearchResultProtein> proteins = result.getProteinMatchList();
//        // accession strings may be separated by ^A; Calculate the number of proteins
//        int numProteins = 0;
//        for(MsSearchResultProtein protein: proteins) {
//            numProteins += protein.getAccession().split("\\cA").length;
//        }
        writer.writeStartElement("search_result");
        writer.writeStartElement("search_hit");
        writer.writeAttribute("hit_rank", String.valueOf(getResultRankInList(resForScanCharge, result)));
        writer.writeAttribute("peptide", peptide.getPeptideSequence());
        writer.writeAttribute("peptide_prev_aa", String.valueOf(peptide.getPreResidue()));
        writer.writeAttribute("peptide_next_aa", String.valueOf(peptide.getPostResidue()));
        writer.writeAttribute("protein", proteins.get(0).getAccession());
        writer.writeAttribute("num_tot_proteins", String.valueOf(proteins.size()));
        
        //double peptNeutralMass = calculatePeptideNeutralMass(peptide, staticMods);
        double peptNeutralMass = getNeutralMass(getCalculatedPeptideMassPlusH(result));
        writer.writeAttribute("calc_neutral_pep_mass", String.valueOf(peptNeutralMass));
        
        
        double massdiff = result.getObservedMass().doubleValue() - getCalculatedPeptideMassPlusH(result);
        writer.writeAttribute("massdiff", String.valueOf(massdiff));
        newLine(writer);
        
        
        // write all the other proteins
        writeAlternativeProteins(proteins, writer);
        
        // write modifications
        writeModificationInfo(peptide, staticMods, writer);
        
        // write program specific scores
        writeScores(result, writer);
        
        
        writer.writeEndElement(); // search_hit
        writer.writeEndElement(); // search_result
        newLine(writer);
    }
    
    private double calculatePeptideNeutralMass(MsSearchResultPeptide peptide, List<MsResidueModification> staticMods) {
        double peptNeutralMass = 0;
        if(fragmentMassType == MassType.AVG) {
            peptNeutralMass = 0; // TODO FIX THIS AminoAcidUtilsFactory.getAminoAcidUtils().avgMassPeptide(peptide.getPeptideSequence());
        }
        else {
            peptNeutralMass = 0; // TODO FIX THIS AminoAcidUtilsFactory.getAminoAcidUtils().monoMassPeptide(peptide.getPeptideSequence());
        }
        // get the dynamic mods
        List<MsResultResidueMod> resultDynaMods = peptide.getResultDynamicResidueModifications();
        for(MsResultResidueMod mod: resultDynaMods) {
            peptNeutralMass += mod.getModificationMass().doubleValue();
        }
        
        // get the static mods
        Map<Character, Double> staticModMap = new HashMap<Character, Double>();
        for(MsResidueModification mod: staticMods) {
            staticModMap.put(mod.getModifiedResidue(), mod.getModificationMass().doubleValue());
        }
        String seq = peptide.getPeptideSequence();
        for(int i = 0; i < seq.length(); i++) {
            if(staticModMap.containsKey(seq.charAt(i)))
                peptNeutralMass += staticModMap.get(seq.charAt(i)).doubleValue();
        }
        peptNeutralMass += (BaseAminoAcidUtils.HYDROGEN*2 + BaseAminoAcidUtils.OXYGEN);
        return Math.round(peptNeutralMass*1000000)/1000000.0;
    }

    
    private void writeModificationInfo(MsSearchResultPeptide peptide, List<MsResidueModification> staticMods, XMLStreamWriter writer) throws XMLStreamException {
        
        // get the dynamic mods
        List<MsResultResidueMod> resultDynaMods = peptide.getResultDynamicResidueModifications();
        
        // get the static mods
        Map<Integer, Double> resultStaticMods = new HashMap<Integer, Double>();
        String seq = peptide.getPeptideSequence();
        for(MsResidueModification mod: staticMods) {
            char modifiedAA = mod.getModifiedResidue();
            double mass = getModifiedAminoAcidMass(mod.getModificationMass().doubleValue(), modifiedAA);
            int s = 0;
            int idx = -1;
            while((idx = seq.indexOf(modifiedAA, s)) != -1) {
                resultStaticMods.put(idx, mass);
                s = idx+1;
            }
        }
        
        // If there are no modifications don't write anything
        if(resultDynaMods.size() == 0 && resultStaticMods.size() == 0)
            return;
        
        writer.writeStartElement("modification_info");
        //writer.writeAttribute("modified_peptide", peptide.getModifiedPeptide());
        newLine(writer);
        
        for(MsResultResidueMod mod: resultDynaMods) {
            writer.writeStartElement("mod_aminoacid_mass");
            writer.writeAttribute("position", String.valueOf(mod.getModifiedPosition()+1));
            double modMass = getModifiedAminoAcidMass(mod.getModificationMass().doubleValue(), seq.charAt(mod.getModifiedPosition()));
            writer.writeAttribute("mass", String.valueOf(modMass));
            writer.writeEndElement();
            newLine(writer);
        }
        
        for(Integer pos: resultStaticMods.keySet()) {
            writer.writeStartElement("mod_aminoacid_mass");
            writer.writeAttribute("position", String.valueOf(pos+1));
            writer.writeAttribute("mass", String.valueOf(resultStaticMods.get(pos)));
            writer.writeEndElement();
            newLine(writer);
        }
        
        writer.writeEndElement();
        newLine(writer);
    }
    
    private double getModifiedAminoAcidMass(double massDiff, char aa) {
        double mass = massDiff;
        if(fragmentMassType == MassType.AVG)
            mass += AminoAcidUtilsFactory.getAminoAcidUtils().avgMass(aa);
        else
            mass += AminoAcidUtilsFactory.getAminoAcidUtils().monoMass(aa);
        return Math.round(mass*1000000)/1000000.0;
    }
    
    private void writeAlternativeProteins(List<MsSearchResultProtein> proteins,
            XMLStreamWriter writer) throws XMLStreamException {
        for(int i = 1; i < proteins.size(); i++) {
            String[] accessionStrings = proteins.get(i).getAccession().split("\\cA");
            for(String acc: accessionStrings) {
                writer.writeStartElement("alternative_protein");
                writer.writeAttribute("protein", acc);
                //writer.writeAttribute("peptide_prev_aa", String.valueOf(peptide.getPreResidue()));
                //writer.writeAttribute("peptide_next_aa", String.valueOf(peptide.getPostResidue()));
                writer.writeEndElement();
                newLine(writer);
            }
        }
    }

    private void startSpectrumQuery(List<T> resForScanCharge, MsScan scan,
            XMLStreamWriter writer, String filename)
            throws XMLStreamException {
        
        int scanNumber = scan.getStartScanNum();
        int charge = resForScanCharge.get(0).getCharge();
        double neutralMass = getNeutralMass(resForScanCharge.get(0).getObservedMass().doubleValue());
        
        BigDecimal rt = scan.getRetentionTime();
        
        writer.writeStartElement("spectrum_query");
        String spectrum = filename+"."+scanNumber+"."+scanNumber+"."+charge;
        writer.writeAttribute("spectrum", spectrum);
        writer.writeAttribute("start_scan", String.valueOf(scanNumber));
        writer.writeAttribute("end_scan", String.valueOf(scanNumber));
        writer.writeAttribute("precursor_neutral_mass", String.valueOf(neutralMass)); 
        writer.writeAttribute("assumed_charge", String.valueOf(charge));
        if(rt != null)
            writer.writeAttribute("retention_time_sec", rt.toString());
        writer.writeAttribute("index", String.valueOf(this.spectrumQueryIndex));
        spectrumQueryIndex++;
        newLine(writer);
    }
    
    // TODO Is this correct?
    private double getNeutralMass(double mPlusH) {
        return mPlusH - BaseAminoAcidUtils.PROTON;
    }
    
    //-------------------------------------------------------------------------------------------
    // TO BE IMPLEMENTED BY SUBCLASSES
    //-------------------------------------------------------------------------------------------
    abstract double getCalculatedPeptideMassPlusH(T result);
    
    abstract MassType getPrecursorMassType(int searchId);
    
    abstract MassType getFragmentMassType(int searchId);
    
    abstract String getMaxNumInternalClevages(int id);
    
    abstract String getMinNumTermini(int id);
    
    abstract void writeProgramSpecificParams(int id, XMLStreamWriter writer) throws XMLStreamException;
    
    abstract void sortResultsByRank(List<T> results);
    
    abstract int getResultRankInList(List<T> resultList, T result);
    
    abstract void writeScores(T result, XMLStreamWriter writer) throws XMLStreamException;
    
    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------
    
    void newLine(XMLStreamWriter writer ) throws XMLStreamException {
        writer.writeCharacters("\n");
    }
    
}
