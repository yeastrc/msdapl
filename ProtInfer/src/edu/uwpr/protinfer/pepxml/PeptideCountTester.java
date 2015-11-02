package edu.uwpr.protinfer.pepxml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.parser.DataProviderException;

import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.util.StringUtils;

public class PeptideCountTester {

    public static void main(String[] args) throws DataProviderException, IOException {
        
        String dir = "TEST_DATA/for_vagisha/human/top10hits";
        String fastaDb = "TEST_DATA/for_vagisha/human/ipi.HUMAN.fasta.20080402.for_rev";
//        String filePath = dir+File.separator+"interact.pep.xml";
        String filePath = dir+File.separator+"interact-human.pep.xml";
        
        InteractPepXmlFileReader reader = new InteractPepXmlFileReader();
        reader.open(filePath, "rev_");


        Set<String> peptidesFound = new HashSet<String>();
        // read in the fasta file
        System.out.println("Reading fasta file");
        Map<String, String> fastaProteins = FastaFileReader.readFastaProteins(fastaDb );
        System.out.println("Number of proteins in fasta file: "+fastaProteins.size());
        
        
        while(reader.hasNextRunSummary()) {
            String runName = new File(reader.getRunName()).getName();
            System.out.println("Results for run: "+reader.getRunName());
            int scanCount = 0;
            int targetHitcount = 0;
            int decoyHitCount = 0;
            int ambiHitCount = 0;
            
            Map<String, ProteinInfo> fwdProtCoverage = new HashMap<String, ProteinInfo>();
            Map<String, ProteinInfo> revProtCoverage = new HashMap<String, ProteinInfo>();
            
            int numHits = 0;
            while(reader.hasNextScanSearchResult()) {
                
                ScanSearchResult scanResult = reader.getNextSearchScan();
                
                List<SequestSearchHit> topDecoyAndTargetHits = getTopSearchHits(scanResult);
                numHits += topDecoyAndTargetHits.size();
                
                if (topDecoyAndTargetHits.size() == 2) {
                    boolean foundTarget = !topDecoyAndTargetHits.get(0).isDecoyHit() || !topDecoyAndTargetHits.get(1).isDecoyHit();
                    boolean foundDecoy = topDecoyAndTargetHits.get(0).isDecoyHit() || topDecoyAndTargetHits.get(1).isDecoyHit();
                    if (!foundTarget || !foundDecoy ) {
                        System.out.println("Did not find both target and decoy");
                    }
                }
                
                for (SequestSearchHit hit: topDecoyAndTargetHits) {
                    
                    List<Protein> proteins = hit.getProteins();
                    boolean target = false;
                    boolean decoy = false;
                    for (Protein prot: proteins) {
                        
                        if (prot.isDecoy()) {
                            decoy = true;
                            // have we seen this protein before? 
                            ProteinInfo pinfo = revProtCoverage.get(prot.getAccession());
                            if (pinfo == null) {
                                pinfo = new ProteinInfo();
                            }
                            pinfo.spectrumCount++;
                            pinfo.peptides.add(hit.getPeptide().getPeptide().getPeptideSequence());
                            revProtCoverage.put(prot.getAccession(), pinfo);
                        }
                        else {
                            
                            target = true;
                            // have we seen this protein before? 
                            ProteinInfo pinfo = fwdProtCoverage.get(prot.getAccession());
                            if (pinfo == null) {
                                pinfo = new ProteinInfo();
                            }
                            pinfo.spectrumCount++;
                            pinfo.peptides.add(hit.getPeptide().getPeptide().getPeptideSequence());
                            fwdProtCoverage.put(prot.getAccession(), pinfo);
                        }
                    }
                    peptidesFound.add(hit.getPeptide().getPeptide().getPeptideSequence());
                    
                    if (target && !decoy)   targetHitcount++;
                    if (decoy && !target)   decoyHitCount++;
                    if (decoy && target)    ambiHitCount++;
                }
                scanCount++;
            }
            
            calculateProteinCoverage(fastaProteins, fwdProtCoverage);
            calculateProteinCoverage(fastaProteins, revProtCoverage);
            
            System.out.println("\tNumber of spectrum_query elements: "+scanCount);
            System.out.println("\tNumber of total hits: "+numHits);
            System.out.println("\tTarget Hits: "+targetHitcount+"; Decoy Hits: "+decoyHitCount+"; Ambig. Hits: "+ambiHitCount);
            System.out.println("\tNumber of peptides found: "+reader.getPeptideHits().size());
            System.out.println("\tNumber of proteins found: "+reader.getProteinHits().size());
            
            System.out.println("Number of proteins identified FWD: "+fwdProtCoverage.size());
            System.out.println("Number of proteins identified REV: "+revProtCoverage.size());
            
            printStats(dir+File.separator+runName+"_F.stats.txt", fwdProtCoverage);
            printStats(dir+File.separator+runName+"_R.stats.txt", revProtCoverage);
        }
        
        reader.close();
    }

    private static List<SequestSearchHit> getTopSearchHits(ScanSearchResult scanResult) {
        // find the top target and decoy hits.
        List<SequestSearchHit> twoHits = new ArrayList<SequestSearchHit>(2);
        boolean foundTargetHit = false;
        boolean foundDecoyHit = false;
        
        for (SequestSearchHit hit: scanResult.getSearchHits()) {
            if (hit.isDecoyHit()) {
                if(!foundDecoyHit) {
                    twoHits.add(hit);
                    foundDecoyHit = true;
                }
            }
            else if (!foundTargetHit) {
                twoHits.add(hit);
                foundTargetHit = true;
            }
             
            if (foundTargetHit && foundDecoyHit)
                break;
        }
        return twoHits;
    }
    
    private static void calculateProteinCoverage(Map<String, String> fastaProteins, Map<String, ProteinInfo> protCoverage) {
        
        for (String accession: protCoverage.keySet()) {
            
            Set<String> peptides = protCoverage.get(accession).peptides;
            List<String> peptList = new ArrayList<String>(peptides.size());
            peptList.addAll(peptides);
            int coveredLength = StringUtils.getCoveredSequenceLength(fastaProteins.get(accession), peptList);
            
            String protein = fastaProteins.get(accession);
            ProteinInfo pinfo = protCoverage.get(accession);
            pinfo.sequenceLen = protein.length();
            pinfo.lenCovered = coveredLength;
        }
    }
    
    
    private static void printStats(String file, Map<String, ProteinInfo> protCoverage) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("Accession\tHitCount\tPeptideCount\tProteinLength\tCoverage\n");
            for (String key: protCoverage.keySet()) {
                ProteinInfo pinfo = protCoverage.get(key);
                writer.write(key+"\t"+pinfo.peptides.size()+"\t"+pinfo.spectrumCount+"\t"+pinfo.sequenceLen+"\t"+pinfo.lenCovered+"\n");
            }
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static class ProteinInfo {
        int spectrumCount = 0;
        int sequenceLen;
        int lenCovered;
        Set<String> peptides = new HashSet<String>();
    }
}

