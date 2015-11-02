package org.yeastrc.ms.parser.sqtFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;


public class SQTFileReaderApp {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {

        String file = "./resources/ForTest.sqt";
        String outFile = "./resources/test.out";
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        
        SequestSQTFileReader reader = new SequestSQTFileReader();
        try {
            reader.open(file);
            SQTHeader header = reader.getSearchHeader();
            System.out.println(header.toString());
            writer.write(header.toString());
            writer.write("\n");
            while (reader.hasNextSearchScan()) {
                SequestSearchScan scan = reader.getNextSearchScan();
                System.out.println(scan.toString());
                writer.write(scan.toString());
                writer.write("\n");
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            reader.close();
        }
        writer.close();
    }

}
