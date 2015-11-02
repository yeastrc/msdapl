package edu.uwpr.protinfer.idpicker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.parser.DataProviderException;

import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinHit;
import edu.uwpr.protinfer.infer.SearchSource;

public class IdPickerXmlParser {

    private Map<Integer, Protein> proteinList;
    private Map<Integer, PeptideHit> peptideHits;
    private List<SequestSearchResult> searchHits;
    XMLStreamReader reader = null;

    
    public IdPickerXmlParser() {
        proteinList = new HashMap<Integer, Protein>();
        peptideHits = new HashMap<Integer, PeptideHit>();
        searchHits = new ArrayList<SequestSearchResult>();
    }
    
    public List<SequestSearchResult> getAcceptedHits() {
        return searchHits;
    }
    
    public void readXml(String filePath) throws DataProviderException {  
        
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            InputStream input = new FileInputStream(filePath);
            reader = inputFactory.createXMLStreamReader(input);
            readXml(reader);
        }
        catch (FileNotFoundException e) {
            throw new DataProviderException("File not found: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
    }

    private void readXml(XMLStreamReader reader) throws XMLStreamException {
        int numTarget = 0;
        int numDecoy = 0;
        while(reader.hasNext()) {
            int evtType = reader.next();
            if (isStartProteinIndex(evtType)) {
                readProteins(reader);
            }
            else if (isStartPeptideIndex(evtType)) {
                readPeptides(reader);
            }
            else if (isStartSpectrumScan(evtType)) {
                if (readScanSearchResult(reader))
                    numTarget++;
                else
                    numDecoy++;
            }
        }
        System.out.println("Num target hits: "+numTarget+"; Num decoy hits: "+numDecoy);
    }

    public void close() {
        if(reader != null) try {
            reader.close();
        }
        catch (XMLStreamException e) {}
    }
    
    private boolean readScanSearchResult(XMLStreamReader reader) throws XMLStreamException {
        int scan = -1;
        int charge = -1;
        int peptideId = -1;
        double fdr = -1.0;
        SearchSource source = new SearchSource("idpicker.xml", 1);
        
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (reader.getAttributeLocalName(i).equalsIgnoreCase("scan")) {
                scan = Integer.parseInt(reader.getAttributeValue(i));
            }
            else if (reader.getAttributeLocalName(i).equalsIgnoreCase("z")) {
                charge = Integer.parseInt(reader.getAttributeValue(i));
            }
        }
        
        while (reader.hasNext()) {
            int evtType = reader.next();
            
            if (isEndSpectrumScan(evtType))
                break;
            if (isStartResult(evtType)) {
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    if (reader.getAttributeLocalName(i).equalsIgnoreCase("FDR")) {
                        fdr = new BigDecimal(reader.getAttributeValue(i)).doubleValue();
                    }
                }
            }
            else if (isStartResultPeptideId(evtType)) {
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    if (reader.getAttributeLocalName(i).equalsIgnoreCase("peptide")) {
                        peptideId = Integer.parseInt(reader.getAttributeValue(i));
                    }
                }
            }
        }
        PeptideHit pHit = peptideHits.get(peptideId);
        for(Protein prot: pHit.getProteinList()) {
            if (prot.getAccession().startsWith("rev_"))
                return false; // this is a hit to the decoy database;
        }
//        SequestSearchResultBean hit = new SequestSearchResultBean(source, scan, charge, pHit);
//        hit.getSpectrumMatch().setFdr(fdr);
//        searchHits.add(hit);
        return true; // this is a hit to the target database;
    }
    
    private void readPeptides(XMLStreamReader reader) throws XMLStreamException {
        int peptideCount = -1;
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (reader.getAttributeLocalName(i).equalsIgnoreCase("count")) {
                peptideCount = Integer.parseInt(reader.getAttributeValue(i));
                break;
            }
        }
        
        PeptideHit hit = null;
        int lastId = -1;
        while (reader.hasNext()) {
            int evtType = reader.next();
            if (isEndPeptideIndex(evtType))
                break;
            if (isEndPeptide(evtType)) {
                peptideHits.put(lastId, hit);
                continue;
            }
            if (isStartPeptide(evtType)) {
                int id = -1;
                String sequence = null;
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    if (reader.getAttributeLocalName(i).equalsIgnoreCase("id")) {
                        id = Integer.parseInt(reader.getAttributeValue(i));
                    }
                    else if (reader.getAttributeLocalName(i).equalsIgnoreCase("sequence")) {
                        sequence = reader.getAttributeValue(i);
                    }
                }
                String peptideKey = sequence;
                hit = new PeptideHit(new Peptide(sequence, peptideKey, id));
                lastId = id;
            }
            else if (isStartLocus(evtType)) {
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    if (reader.getAttributeLocalName(i).equalsIgnoreCase("id")) {
                        int proteinId = Integer.parseInt(reader.getAttributeValue(i));
                        hit.addProtein(proteinList.get(proteinId));
                    }
                }
            }
        }
        System.out.println("Number of peptides read: "+peptideHits.size());
    }

    private void readProteins(XMLStreamReader reader) throws XMLStreamException {
        int proteinCount = -1;
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (reader.getAttributeLocalName(i).equalsIgnoreCase("count")) {
                proteinCount = Integer.parseInt(reader.getAttributeValue(i));
                break;
            }
        }
        
        while (reader.hasNext()) {
            int evtType = reader.next();
            
            if (isEndProteinIndex(evtType))
                break;
            if (isStartProtein(evtType)) {
                int id = -1;
                String locus = null;
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    if (reader.getAttributeLocalName(i).equalsIgnoreCase("id")) {
                        id = Integer.parseInt(reader.getAttributeValue(i));
                    }
                    else if (reader.getAttributeLocalName(i).equalsIgnoreCase("locus")) {
                        locus = reader.getAttributeValue(i);
                    }
                }
                proteinList.put(id, new Protein(locus, id));
            }
        }
        System.out.println("Number of proteins read: "+proteinList.size());
    }

    private boolean isStartPeptideIndex(int evtType) {
        return (evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("peptideIndex"));
    }
    
    private boolean isEndPeptideIndex(int evtType) {
        return (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("peptideIndex"));
    }

    private boolean isStartProteinIndex(int evtType) {
        return (evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("proteinIndex"));
    }
    
    private boolean isEndProteinIndex(int evtType) {
        return (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("proteinIndex"));
    }
    
    private boolean isStartProtein(int evtType) {
        return (evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("protein"));
    }
    
    private boolean isStartPeptide(int evtType) {
        return (evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("peptide"));
    }
    
    private boolean isEndPeptide(int evtType) {
        return (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("peptide"));
    }
    
    private boolean isStartLocus(int evtType) {
        return (evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("locus"));
    }
    
    private boolean isStartSpectrumScan(int evtType) {
        return (evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("spectrum"));
    }
    
    private boolean isEndSpectrumScan(int evtType) {
        return (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("spectrum"));
    }
    
    private boolean isStartResult(int evtType) {
        return (evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("result"));
    }
    
    private boolean isStartResultPeptideId(int evtType) {
        return (evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("id"));
    }
    
    public static void main(String[] args) throws DataProviderException {
        String filePath = "TEST_DATA/for_vagisha/18mix/idpicker/interact.pep.xml";
        IdPickerXmlParser parser = new IdPickerXmlParser();
        parser.readXml(filePath);
        parser.close();
//        try {
//            printAcceptedHits("TEST_DATA/for_vagisha/18mix/idpicker/interact", parser.getAcceptedHits());
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    
//    private static void printAcceptedHits(String runName,
//            List<SequestHit> acceptedHits) throws IOException {
//        BufferedWriter writer = new BufferedWriter(new FileWriter(runName+".psm"));
//        for (SequestHit hit: acceptedHits) {
//            writer.write(hit.getScanNumber()+"\t"+hit.getCharge()+"\t"+hit.getXcorr()
//                    +"\t"+hit.getSpectrumMatch().getFdr());
//            writer.write("\t"+hit.getPeptideHit().getModifiedSequence());
//            StringBuilder buf = new StringBuilder();
//            for (ProteinHit p: hit.getPeptideHit().getProteinList()) {
//                buf.append(","+p.getAccession());
//            }
//            buf.deleteCharAt(0);
//            writer.write("\t"+buf.toString()+"\n");
//        }
//        writer.close();
//    }
}
