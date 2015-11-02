package edu.uwpr.protinfer.pepxml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastaFileReader {

    private static final Pattern pattern = Pattern.compile(">(\\S+)\\s+.*");
    
    private FastaFileReader() {}
    
    public static final Map<String, String> readFastaProteins(String fastaFile) {
        
        BufferedReader reader = null;
        Map<String, String> proteins = new HashMap<String, String>();
        try {
            reader = new BufferedReader(new FileReader(fastaFile));
            String line = reader.readLine();
            StringBuilder protein = new StringBuilder();
            String accession = null;
            while(line != null) {
                if (line.startsWith(">")) {
                    if (accession != null) {
                        proteins.put(accession, protein.toString());
                        protein = new StringBuilder();
                    }
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        accession = matcher.group(1);
                    }
                    if (accession == null) {
                        System.out.println("Accession cannot be null");
                        System.exit(1);
                    }
                }
                else if (line.trim().length() != 0) {
                    protein.append(line.trim());
                }
                line = reader.readLine();
            }
            // put the last one in
            proteins.put(accession, protein.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) try {
                reader.close();
            }
            catch (IOException e) {}
        }
        return proteins;
    }
}
