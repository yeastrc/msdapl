package edu.uwpr.protinfer.pepxml;

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

import org.yeastrc.ms.parser.DataProviderException;

import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinHit;

public class PepxmlFileReader {

    String filePath;
    XMLStreamReader reader = null;
    String decoyPrefix = null;
    private Map<String, PeptideHit> peptideHits;
    private Map<String, Protein> proteinList;
    private int peptideHitId = 0;
    private int proteinHitId = 0;
    
    public void open(String filePath, String decoyPrefix) throws DataProviderException {
        
        this.decoyPrefix = decoyPrefix;
        
        initMaps();
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            InputStream input = new FileInputStream(filePath);
            reader = inputFactory.createXMLStreamReader(input);
        }
        catch (FileNotFoundException e) {
            throw new DataProviderException("File not found: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        
        
        this.filePath = filePath;
    }
    
    private void initMaps() {
        
        peptideHitId = 0;
        proteinHitId = 0;
        if (peptideHits != null)
            peptideHits.clear();
        else
            peptideHits = new HashMap<String, PeptideHit>();
        if (proteinList != null)
            proteinList.clear();
        else
            proteinList = new HashMap<String, Protein>();
    }
    
    public void close() {
        if (reader != null) try {
            reader.close();
        }
        catch (XMLStreamException e) {}
    }
    
    public List<PeptideHit> getPeptideHits() {
        List<PeptideHit> hits = new ArrayList<PeptideHit>(peptideHits.size());
        hits.addAll(peptideHits.values());
        return hits;
    }
    
    public List<Protein> getProteinHits() {
        List<Protein> hits = new ArrayList<Protein>(proteinList.size());
        hits.addAll(proteinList.values());
        return hits;
    }
    
    public boolean hasNextScanSearchResult() throws DataProviderException  {
        if (reader == null)
            return false;
        try {
            while(reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if (reader.getLocalName().equalsIgnoreCase("spectrum_query"))
                        return true;
                }
            }
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        return false;
    }
    
    public ScanSearchResult getNextSearchScan() throws DataProviderException {
        ScanSearchResult scanResult = new ScanSearchResult();
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrib = reader.getAttributeLocalName(i);
            String val = reader.getAttributeValue(i);
            if (attrib.equalsIgnoreCase("spectrum"))
                scanResult.setSpectrumString(val);
            else if (attrib.equalsIgnoreCase("start_scan"))
                scanResult.setStartScan(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("end_scan"))
                scanResult.setEndScan(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("precursor_neutral_mass"))
                scanResult.setPrecursorNeutralMass(new BigDecimal(val));
            else if (attrib.equalsIgnoreCase("assumed_charge"))
                scanResult.setAssumedCharge(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("retention_time_sec"))
                scanResult.setRetentionTime(Float.parseFloat(val));
        }
        // read the search hits for this scan
        try {
            readHitsForScan(scanResult);
        }
        catch (NumberFormatException e) {
            throw new DataProviderException("Error parsing number in file: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        return scanResult;
    }

    private void readHitsForScan(ScanSearchResult scanResult) throws XMLStreamException, DataProviderException {
        
        while(reader.hasNext()) {
            int evtType = reader.next();
            if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("spectrum_query"))
                break;
            if (evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("search_hit")) {
                SequestSearchHit hit = readSearchHit(scanResult.getStartScan());
                scanResult.addSearchHit(hit);
            }
        }  
    }
    
    private SequestSearchHit readSearchHit(int scanNumber) throws XMLStreamException, DataProviderException {
        
        SequestSearchHit hit = new SequestSearchHit();
        String peptideSeq = null;
        List<ProteinHit> proteinAccList = new ArrayList<ProteinHit>();
        int numMatchingProteins = -1;
        
        char preResidue = 0;
        char postResidue = 0;
        String prAcc = null;
        
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrib = reader.getAttributeLocalName(i);
            String val = reader.getAttributeValue(i);
            if (attrib.equalsIgnoreCase("hit_rank"))
                hit.setXcorrRank(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("peptide"))
                peptideSeq = val;
            else if (attrib.equalsIgnoreCase("peptide_prev_aa"))
                preResidue = Character.valueOf(val.charAt(0));
            else if (attrib.equalsIgnoreCase("peptide_next_aa"))
                postResidue = Character.valueOf(val.charAt(0));
            else if (attrib.equalsIgnoreCase("protein"))
                prAcc = val;
            else if (attrib.equalsIgnoreCase("num_tot_proteins"))
                numMatchingProteins = Integer.parseInt(val);
            else if (attrib.equalsIgnoreCase("num_matched_ions"))
                hit.setNumMatchedIons(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("tot_num_ions"))
                hit.setNumPredictedIons(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("calc_neutral_pep_mass"))
                hit.setCalcNeutralMass(new BigDecimal(val));
        }
        proteinAccList.add(new ProteinHit(new Protein(prAcc, -1), preResidue, postResidue));
        
        
        while(reader.hasNext()) {
            int evtType = reader.next();
            if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("search_hit"))
                break;
            if (evtType == XMLStreamReader.START_ELEMENT) {
                if (reader.getLocalName().equalsIgnoreCase("alternative_protein")) {
                    preResidue = 0;
                    postResidue = 0;
                    prAcc = null;
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        String attrib = reader.getAttributeLocalName(i);
                        String val = reader.getAttributeValue(i);
                        if (attrib.equalsIgnoreCase("protein"))
                            prAcc = val;
                        else if (attrib.equalsIgnoreCase("peptide_prev_aa"))
                            preResidue = Character.valueOf(val.charAt(0));
                        else if (attrib.equalsIgnoreCase("peptide_next_aa"))
                            postResidue = Character.valueOf(val.charAt(0));
                    }
                    proteinAccList.add(new ProteinHit(new Protein(prAcc, -1), preResidue, postResidue));
                }
                else if (reader.getLocalName().equalsIgnoreCase("search_score")) {
                    String scoreType = reader.getAttributeValue(null, "name");
                    String scoreVal = reader.getAttributeValue(null, "value");
                    if (scoreType.equalsIgnoreCase("xcorr"))
                        hit.setXcorr(new BigDecimal(scoreVal));
                    else if (scoreType.equalsIgnoreCase("deltacn"))
                        hit.setDeltaCn(new BigDecimal(scoreVal));
                    else if (scoreType.equalsIgnoreCase("spscore"))
                        hit.setSpScore(new BigDecimal(scoreVal));
                    else if (scoreType.equalsIgnoreCase("sprank"))
                        hit.setSpRank(Integer.parseInt(scoreVal));
                }
            }
        }
        if (numMatchingProteins != proteinAccList.size())
            throw new DataProviderException("value of attribute num_matched_ions("+numMatchingProteins+
                    ") does not match number of proteins("+proteinAccList.size()+") found for this hit. "
                    +"Scan# "+scanNumber+"; hit rank: "+hit.getXcorrRank());
        
        // check if the peptide for this hit was seen before
        PeptideHit peptideHit = peptideHits.get(peptideSeq);
        if (peptideHit == null) {
            // if not create a new PeptideHit
            peptideHit = new PeptideHit(new Peptide(peptideSeq, peptideSeq, peptideHitId++));
            // add the protein hits
            for (ProteinHit ph: proteinAccList) {
                Protein prot = proteinList.get(ph.getAccession());
                if (prot == null) {
                    prot = new Protein(ph.getAccession(), proteinHitId++);
                    // is this a decoy protein
                    if (decoyPrefix != null && prot.getAccession().startsWith(decoyPrefix))
                        prot.setDecoy();
                    proteinList.put(ph.getAccession(), prot);
                }
                peptideHit.addProtein(prot);
            }
            peptideHits.put(peptideSeq, peptideHit);
        }
        else {
            // make sure the matching proteins are the same as the ones we found in this search_hit element
            List<Protein> prots = peptideHit.getProteinList();
            if (prots.size() != proteinAccList.size()) {
                throw new DataProviderException("Number of proteins previously seen for this peptide do not match the number found in the current search_hit element");
            }
            for (ProteinHit ph: proteinAccList) {
                boolean found = false;
                for (Protein prot: prots) {
                    if (ph.getAccession().equalsIgnoreCase(prot.getAccession())) {
                        found = true;
                        break;
                    }
                }
                if (found)  break;
                
                throw new DataProviderException("Protein with accession: "+ph+
                        " not found in the existing list of proteins for this peptide."+
                        " Scan#: "+scanNumber+"; hit rank: "+hit.getXcorrRank());
            }
        }
        hit.setPeptide(peptideHit);
        return hit;
    }
    
    public static void main(String[] args) throws DataProviderException, IOException {
        String filePath = "TEST_DATA/for_vagisha/18mix/interact.pep.xml";
        PepxmlFileReader reader = new PepxmlFileReader();
        reader.open(filePath, "rev_");
        int scanCount = 0;
        int targetHitcount = 0;
        int decoyHitCount = 0;
        int ambiHitCount = 0;
        
        while(reader.hasNextScanSearchResult()) {
            ScanSearchResult scanResult = reader.getNextSearchScan();
            if (scanResult.getSearchHits().size() != 1)
                System.out.println("Scan has "+scanResult.getSearchHits().size()+" hits!!!");
            for (SequestSearchHit hit: scanResult.getSearchHits()) {
                List<Protein> proteins = hit.getProteins();
//                if (proteins.size() != 1)
//                    System.out.println("Hit "+scanResult.getSpectrumString()+" has "+proteins.size()+" matching proteins");
                boolean target = false;
                boolean decoy = false;
                for (Protein prot: proteins) {
                    if (prot.isDecoy())
                        decoy = true;
                    else
                        target = true;
                }
                if (target && !decoy)   targetHitcount++;
                if (decoy && !target)   decoyHitCount++;
                if (decoy && target)    ambiHitCount++;
            }
            
//            System.out.println(scanResult.getSpectrumString()+": #hits: "+scanResult.getSearchHits().size());
            scanCount++;
        }
        reader.close();
        
        System.out.println("Number of spectrum_query elements: "+scanCount);
        System.out.println("Target Hits: "+targetHitcount+"; Decoy Hits: "+decoyHitCount+"; Ambig. Hits: "+ambiHitCount);
        System.out.println("Number of peptides found: "+reader.getPeptideHits().size());
        System.out.println("Number of proteins found: "+reader.getProteinHits().size());
    }
}

