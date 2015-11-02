package edu.uwpr.protinfer.pepxml;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.yeastrc.ms.parser.DataProviderException;

import edu.uwpr.protinfer.infer.Protein;

public class InteractPepXmlFileReader extends PepxmlFileReader{

    public boolean hasNextRunSummary() throws DataProviderException  {
        if (reader == null)
            return false;
        try {
            while(reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if (reader.getLocalName().equalsIgnoreCase("msms_run_summary"))
                        return true;
                }
            }
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        return false;
    }
    
    public String getRunName() {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (reader.getAttributeLocalName(i).equalsIgnoreCase("base_name"))
                return reader.getAttributeValue(i);
        }
        return null;
    }
    
    public boolean hasNextScanSearchResult() throws DataProviderException  {
        if (reader == null)
            return false;
        try {
            while(reader.hasNext()) {
                
                int evtId = reader.next();
                if (evtId == XMLStreamReader.END_ELEMENT) {
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
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        return false;
    }
    
    public static void main(String[] args) throws DataProviderException, IOException {
        String filePath = "TEST_DATA/for_vagisha/18mix/interact.pep.xml";
        InteractPepXmlFileReader reader = new InteractPepXmlFileReader();
        reader.open(filePath, "rev_");
        
        
        while(reader.hasNextRunSummary()) {
            System.out.println("Results for run: "+reader.getRunName());
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
//                  if (proteins.size() != 1)
//                  System.out.println("Hit "+scanResult.getSpectrumString()+" has "+proteins.size()+" matching proteins");
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

//              System.out.println(scanResult.getSpectrumString()+": #hits: "+scanResult.getSearchHits().size());
                scanCount++;
            }
            System.out.println("\tNumber of spectrum_query elements: "+scanCount);
            System.out.println("\tTarget Hits: "+targetHitcount+"; Decoy Hits: "+decoyHitCount+"; Ambig. Hits: "+ambiHitCount);
            System.out.println("\tNumber of peptides found: "+reader.getPeptideHits().size());
            System.out.println("\tNumber of proteins found: "+reader.getProteinHits().size());
        }
        reader.close();
    }
}
