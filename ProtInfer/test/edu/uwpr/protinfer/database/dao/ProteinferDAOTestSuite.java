package edu.uwpr.protinfer.database.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferPeptideDAOTest;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAOTest;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferSpectrumMatchDAOTest;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerPeptideDAOTest;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinDAOTest;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerRunDAOTest;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerSpectrumMatchDAOTest;

public class ProteinferDAOTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.uwpr.protinfer.database.dao");
        //$JUnit-BEGIN$
        suite.addTestSuite(ProteinferSpectrumMatchDAOTest.class);
        suite.addTestSuite(IdPickerSpectrumMatchDAOTest.class);
        suite.addTestSuite(ProteinferPeptideDAOTest.class);
        suite.addTestSuite(IdPickerPeptideDAOTest.class);
        suite.addTestSuite(IdPickerProteinDAOTest.class);
        suite.addTestSuite(ProteinferRunDAOTest.class);
        suite.addTestSuite(IdPickerRunDAOTest.class);
//        suite.addTestSuite(ProteinferProteinDAOTest.class);
        //$JUnit-END$
        return suite;
    }
    
    public static void resetDatabase() {
        System.out.println("Resetting database");
        String script = "test/resetDatabase.sh";
        try {
            Process proc = Runtime.getRuntime().exec("sh "+script);
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String line = reader.readLine();
            while(line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
            reader.close();
            proc.waitFor();
            System.out.println("Exit code: "+proc.exitValue());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
