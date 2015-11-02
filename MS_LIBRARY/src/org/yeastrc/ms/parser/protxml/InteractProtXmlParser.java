package org.yeastrc.ms.parser.protxml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.protinfer.proteinProphet.Modification;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetGroup;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetParam;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptide;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptideIon;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetROC;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetROCPoint;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.parser.DataProviderException;

public class InteractProtXmlParser {

    private static final String PROTEIN_NAME = "protein_name";
    private static final String INDISTINGUISHABLE_PROTEIN = "indistinguishable_protein";
    private static final String PEPTIDE = "peptide";
    private static final String PROTEINPROPHET_DETAILS = "proteinprophet_details";
    private static final String PROGRAM_DETAILS = "program_details";
    private static final String PROTEIN = "protein";
    private static final String PROTEIN_GROUP = "protein_group";
    
    private String filePath;
    private XMLStreamReader reader = null;
    private String programName;
    private String programVersion;
    private java.util.Date date;
    
    private List<ProteinProphetParam> params;
    private List<String> inputFiles;
    private double initialMinPeptideProb = 0.01;
    private List<String> equivalentResidues;
    private static final Pattern equiResPattern = Pattern.compile("([A-Z])\\s\\->\\s([A-Z])\\s?");
    private ProteinProphetROC proteinProphetRoc;
    
    private static final Logger log = Logger.getLogger(InteractProtXmlParser.class.getName());
    
    public void open(String filePath) throws DataProviderException {
        
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            InputStream input = new FileInputStream(filePath);
            reader = inputFactory.createXMLStreamReader(input);
            readProteinSummaryHeader();
        }
        catch (FileNotFoundException e) {
            throw new DataProviderException("File not found: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        this.filePath = filePath;
    }
    
    private void readProteinSummaryHeader() throws XMLStreamException, DataProviderException {
        
        boolean inHeader = false;
        while(reader.hasNext()) {
            int evtType = reader.next();
            if(evtType == XMLStreamReader.END_ELEMENT && "protein_summary_header".equalsIgnoreCase(reader.getLocalName())) {
                return; // we have reached the end of the protein_summary_header
            }
            else if(evtType == XMLStreamReader.START_ELEMENT && "protein_summary_header".equalsIgnoreCase(reader.getLocalName())) {
                
                // pep xml files used as input
                String files = reader.getAttributeValue(null, "source_files");
                if(files != null) {
                    files.trim();
                    String[] tokens = files.split(",");
                    for(String file: tokens) {
                        if(inputFiles == null)  inputFiles = new ArrayList<String>();
                        this.inputFiles.add(file);
                    }
                }
                else {
                    throw new DataProviderException("No source_files attribute found");
                }
                if(inputFiles == null) {
                    throw new DataProviderException("No source files found");
                }
                inHeader = true;
                
                // minimum PeptideProphet probability cutoff for PSMs used 
                String initProb = reader.getAttributeValue(null, "initial_min_peptide_prob");
                if(initProb != null) {
                    try {this.initialMinPeptideProb = Double.valueOf(initProb);}
                    catch(NumberFormatException e) 
                    {throw new DataProviderException("Invalid value of attribute initial_min_peptide_prob: "+initProb);}
                }
                
                // equivalent residues -- residue_substitution_list="I -> L" 
                String equiResidues = reader.getAttributeValue(null, "residue_substitution_list");
                if(equiResidues != null) {
                    this.equivalentResidues = new ArrayList<String>();
                    Matcher m = equiResPattern.matcher(equiResidues);
                    StringBuilder buf;
                    while(m.find()) {
                        buf = new StringBuilder();
                        for(int i = 1; i <= m.groupCount(); i++) {
                            buf.append(m.group(i));
                        }
                        equivalentResidues.add(buf.toString());
                    }
                }
                
            }
            else if(inHeader == true && evtType == XMLStreamReader.START_ELEMENT  &&
                    PROGRAM_DETAILS.equalsIgnoreCase(reader.getLocalName())) {
                readProgramDetails();
            }
        }
        
    }
    
    private void readProgramDetails() throws XMLStreamException, DataProviderException {
        
        programName = reader.getAttributeValue(null, "analysis");
        programVersion = reader.getAttributeValue(null, "version");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            date = simpleDateFormat.parse(reader.getAttributeValue(null, "time"));
        }
        catch (ParseException e) {
            log.warn("Error parsing data in protxml file: "+reader.getAttributeValue(null, "time"));
//            throw new DataProviderException("Error parsing date", e);
        }
        readProteinProphetDetails();
    }

    public void close() {
        if (reader != null) try {
            reader.close();
        }
        catch (XMLStreamException e) {}
    }
    
    public String getFileName() {
        return new File(filePath).getName();
    }
    
    public boolean isProteinProphetFile() {
    	return (programName != null && programName.equalsIgnoreCase("proteinprophet"));
    }
    
    public String getProgramName() {
        return programName;
    }
    
    public String getProgramVersion() {
        return programVersion;
    }
    
    public java.util.Date getDate() {
        return date;
    }
    
    public List<ProteinProphetParam> getParams() {
        return params;
    }
    
    public ProteinProphetROC getProteinProphetRoc() {
        return proteinProphetRoc;
    }
    
    public double getMinInitialProbability() {
        return this.initialMinPeptideProb;
    }
    
    public List<String> getInputFiles() {
        return this.inputFiles;
    }
    
    public List<String> getEquivalentResidues() {
        return this.equivalentResidues;
    }
    
    private void readProteinProphetDetails() throws DataProviderException {
        
        this.params = new ArrayList<ProteinProphetParam>();
        
        List<ProteinProphetROCPoint> rocPoints = new ArrayList<ProteinProphetROCPoint>();
        this.proteinProphetRoc = new ProteinProphetROC();
        
        try {
            while(reader.hasNext()) {
                int evtType = reader.next();
                if (evtType == XMLStreamReader.END_ELEMENT && PROTEINPROPHET_DETAILS.equalsIgnoreCase(reader.getLocalName())) {
                    break;
                }
                
                if (evtType == XMLStreamReader.START_ELEMENT && PROTEINPROPHET_DETAILS.equalsIgnoreCase(reader.getLocalName())) {
                    
                    for(int i = 0; i < reader.getAttributeCount(); i++) {
                        String name = reader.getAttributeLocalName(i);
                        String value = reader.getAttributeValue(i);
                        params.add(new ProteinProphetParam(name, value));
                    }
                }
                
                else if(evtType == XMLStreamReader.START_ELEMENT 
                        && "protein_summary_data_filter".equalsIgnoreCase(reader.getLocalName())) {
                    ProteinProphetROCPoint rocPoint = new ProteinProphetROCPoint();
                    rocPoint.setMinProbability(Double.parseDouble(reader.getAttributeValue(null, "min_probability")));
                    rocPoint.setSensitivity(Double.parseDouble(reader.getAttributeValue(null, "sensitivity")));
                    rocPoint.setFalsePositiveErrorRate(Double.parseDouble(reader.getAttributeValue(null, "false_positive_error_rate")));
                    rocPoint.setNumCorrect(Integer.parseInt(reader.getAttributeValue(null, "predicted_num_correct")));
                    rocPoint.setNumIncorrect(Integer.parseInt(reader.getAttributeValue(null, "predicted_num_incorrect")));
                    rocPoints.add(rocPoint);
                }
            }
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading ProteinProphet params.", e);
        }
        proteinProphetRoc.setRocPoints(rocPoints);
    }
    
    public boolean hasNextProteinGroup() throws DataProviderException {
        
        if (reader == null)
            return false;
        try {
            while(reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if (PROTEIN_GROUP.equalsIgnoreCase(reader.getLocalName())) {
                        return true;
                    }
                }
            }
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        return false;
    }
    
    public ProteinProphetGroup getNextGroup() throws DataProviderException {
        
        if(reader == null)
            return null;
        
        int evtType = reader.getEventType();
        
        if (evtType != XMLStreamReader.START_ELEMENT || !PROTEIN_GROUP.equalsIgnoreCase(reader.getLocalName())) {
            throw new DataProviderException("Expected <protein_group> element found "+reader.getLocalName());
        }
        
        ProteinProphetGroup group = new ProteinProphetGroup();
        String probability = reader.getAttributeValue(null, "probability");
        String groupNumber = reader.getAttributeValue(null, "group_number");
        group.setProbability(Double.parseDouble(probability));
        group.setGroupNumber(Integer.parseInt(groupNumber));
        
        // parse the <protein> elements
        try {
            while(reader.hasNext()) {
                evtType = reader.next();
                
                if(evtType == XMLStreamReader.END_ELEMENT && PROTEIN_GROUP.equalsIgnoreCase(reader.getLocalName())) {
                    break;
                }
                
                if(evtType == XMLStreamReader.START_ELEMENT && PROTEIN.equalsIgnoreCase(reader.getLocalName())) {
                    
                    ProteinProphetProtein protein = readProtein(group);
                    readPeptides(protein);
                    group.addProtein(protein);
                }
            }
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading <protein> elements", e);
        }
        
        return group;
    }

    private void readPeptides(ProteinProphetProtein protein) throws XMLStreamException {
        
        // read the <peptide> elements for the protein
        // at this point we should be at the beginning of a <peptide> element
        int evtType;
        
        ProteinProphetProteinPeptideIon lastIon = null;
        
        Map<String, ProteinProphetProteinPeptide> map = new HashMap<String, ProteinProphetProteinPeptide>();
        
        while(reader.hasNext()) {
            
            evtType = reader.getEventType();
            if(evtType == XMLStreamReader.END_ELEMENT && PROTEIN.equalsIgnoreCase(reader.getLocalName())) {
                break;
            }
            
            if(evtType == XMLStreamReader.START_ELEMENT && PEPTIDE.equalsIgnoreCase(reader.getLocalName())) {
                
                String sequence = reader.getAttributeValue(null, "peptide_sequence");
                ProteinProphetProteinPeptide peptide = map.get(sequence);
                if(peptide == null) {
                    peptide = new ProteinProphetProteinPeptide();
                    peptide.setSequence(sequence);
                    peptide.setUniqueToProtein(reader.getAttributeValue(null, "is_nondegenerate_evidence").equalsIgnoreCase("Y"));
                    peptide.setNumEnzymaticTermini(Integer.parseInt(reader.getAttributeValue(null, "n_enzymatic_termini")));
                    
                    protein.addPeptide(peptide);
                    map.put(sequence, peptide);
                }
                
                lastIon = new ProteinProphetProteinPeptideIon();
                lastIon.setUnmodifiedSequence(peptide.getSequence());
                lastIon.setCharge(Integer.parseInt(reader.getAttributeValue(null, "charge")));
                lastIon.setIsContributingEvidence(reader.getAttributeValue(null, "is_contributing_evidence").equalsIgnoreCase("Y"));
                lastIon.setSpectrumCount(Integer.parseInt(reader.getAttributeValue(null, "n_instances")));
                lastIon.setInitialProbability(Double.parseDouble(reader.getAttributeValue(null, "initial_probability")));
                lastIon.setNspAdjProbability(Double.parseDouble(reader.getAttributeValue(null, "nsp_adjusted_probability")));
                lastIon.setNumSiblingPeptides(Double.parseDouble(reader.getAttributeValue(null, "n_sibling_peptides")));
                lastIon.setWeight(Double.parseDouble(reader.getAttributeValue(null, "weight")));
                lastIon.setModifiedSequence(sequence); // if this ion is modified the correct sequence will
                                                       // be set later.
                peptide.addIon(lastIon);
            }
            
            if(evtType == XMLStreamReader.START_ELEMENT && "modification_info".equalsIgnoreCase(reader.getLocalName())) {
                String modPeptide = reader.getAttributeValue(null, "modified_peptide");
                lastIon.setModifiedSequence(modPeptide);
                
                String modNtermMass = reader.getAttributeValue(null, "mod_nterm_mass");
                if(modNtermMass != null)
                	lastIon.addModification(new Modification(new BigDecimal(modNtermMass), Terminal.NTERM));
                String modCtermMass = reader.getAttributeValue(null, "mod_ctermMass");
                if(modCtermMass != null)
                	lastIon.addModification(new Modification(new BigDecimal(modCtermMass), Terminal.CTERM));
            }
            
            if(evtType == XMLStreamReader.START_ELEMENT && "mod_aminoacid_mass".equalsIgnoreCase(reader.getLocalName())) {
                int pos = Integer.parseInt(reader.getAttributeValue(null, "position"));
                BigDecimal mass = new BigDecimal(reader.getAttributeValue(null, "mass"));
                lastIon.addModification(new Modification(pos, mass));
            }
            
            reader.next();
        }
    }

    private ProteinProphetProtein readProtein(ProteinProphetGroup group) throws XMLStreamException {
        
        int numIndistinguishable = Integer.parseInt(reader.getAttributeValue(null, "n_indistinguishable_proteins"));
        
        ProteinProphetProtein protein = new ProteinProphetProtein(numIndistinguishable);
        protein.setProteinName(reader.getAttributeValue(null, PROTEIN_NAME));
        protein.setProbability(Double.parseDouble(reader.getAttributeValue(null, "probability")));
        protein.setSubsumingProteinEntry(reader.getAttributeValue(null, "subsuming_protein_entry"));
        String confidence = reader.getAttributeValue(null, "confidence");
        if(confidence != null)
            protein.setConfidence(Double.parseDouble(confidence));
        String coverage = reader.getAttributeValue(null, "percent_coverage");
        if(coverage != null)
            protein.setCoverage(Double.parseDouble(coverage));
        protein.setTotalSpectrumCount(Integer.parseInt(reader.getAttributeValue(null, "total_number_peptides")));
        String pctIds = reader.getAttributeValue(null, "pct_spectrum_ids");
        if(pctIds != null)
            protein.setPctSpectrumCount(Double.parseDouble(pctIds));
        
        if(numIndistinguishable > 1) {
            readIndistinguishableProteins(protein);
        }
        return protein;
    }

    private void readIndistinguishableProteins(ProteinProphetProtein protein) throws XMLStreamException {
        
        int evtType;
        // read the <indistinguishable_protein> elements
        while(reader.hasNext()) {
            evtType = reader.next();
            
            if(evtType == XMLStreamReader.END_ELEMENT && PROTEIN.equalsIgnoreCase(reader.getLocalName()))
                break;
            
            if(evtType == XMLStreamReader.START_ELEMENT && PEPTIDE.equalsIgnoreCase(reader.getLocalName()))
                break;
            
            if(evtType == XMLStreamReader.START_ELEMENT && INDISTINGUISHABLE_PROTEIN.equalsIgnoreCase(reader.getLocalName())) {
               String proteinName = reader.getAttributeValue(null, PROTEIN_NAME);
               protein.addIndistinguishableProteins(proteinName);
            }
        }
    }
}
