package edu.uwpr.protinfer.pepxml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InteractFileSplitter {

    public static void main(String[] args) throws IOException {
        
        // base_name="/net/pr/vol1/ProteomicsResource/search/engj/20080416-FastXcorrPaper/fig4/human/orig-semi/000"
        Pattern pattern = Pattern.compile(".*base_name=\"(\\S+)\".*");
//        String s = "<msms_run_summary base_name=\"/net/pr/vol1/ProteomicsResource/search/engj/20080416-FastXcorrPaper/fig4/human/orig-semi/000\" msManufacturer=\"Thermo Finnigan\" msModel=\"LCQ Deca XP\" msIonization=\"NSI\" msMas";
        
        String firstLine = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        
        String dir = "TEST_DATA/for_vagisha/human";
        String filePath = dir+File.separator+"interact.pep.xml";
        
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine();
        BufferedWriter writer = null;
        while(line != null) {
            if (line.startsWith("<msms_run_summary")) {
                if (writer != null) {
                    writer.close();
                }
                
                Matcher m = pattern.matcher(line);
                String file = null;
                if (m.matches()) {
                    file = new File(m.group(1)).getName();
                }
                writer = new BufferedWriter(new FileWriter(dir+File.separator+file+".interact.xml"));
            }
            if (writer != null)
                writer.write(line+"\n");
            line = reader.readLine();
        }
        
        writer.write("</msms_run_summary>\n");
        if (writer != null)
            writer.close();
        reader.close();
    }
}
