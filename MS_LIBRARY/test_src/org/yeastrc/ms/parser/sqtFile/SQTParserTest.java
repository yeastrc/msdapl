package org.yeastrc.ms.parser.sqtFile;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;

public class SQTParserTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParseScan() {
//      String line = "S  00016\t00016\t1\t0 \t shamu046\t 742.52000\t 0.0\t0.0 \t0";
        String line = "S       01718   01718   1       0       node0269        993.88000        0.0    0.0     0";
        SQTFileReader reader = getSQTFileReader();
        try {
            reader.parseScan(line);
        }
        catch (DataProviderException e) {
            fail("Valid scan line");
            e.printStackTrace();
        }

    }


    public void testParseLocus() {
        SQTFileReader reader = getSQTFileReader();

        String locus = "H\tName\tValue";
        try {reader.parseLocus(locus); fail("Not a 'L' line");}
        catch(DataProviderException e){}

        locus = "L";
        try {reader.parseLocus(locus); fail("Invalid 'L' line");}
        catch(DataProviderException e){}

        locus = "L locus ";
        try {
            DbLocus loc = reader.parseLocus(locus);
            assertEquals("locus", loc.getAccession());
            assertNull(loc.getDescription());
        }
        catch (DataProviderException e) {
            fail("Exception parsing valid locus line");
            e.printStackTrace();
        }

        locus = "L locus description for locus";
        try {
            DbLocus loc = reader.parseLocus(locus);
            assertEquals("locus", loc.getAccession());
            assertEquals("description for locus", loc.getDescription());
        }
        catch (DataProviderException e) {
            fail("Exception parsing valid locus line");
            e.printStackTrace();
        }

        locus = "L       Placeholder satisfying DTASelect";
        try {
            DbLocus loc = reader.parseLocus(locus);
            assertEquals("Placeholder", loc.getAccession());
            assertEquals("satisfying DTASelect", loc.getDescription());
        }
        catch (DataProviderException e) {
            fail("Exception parsing valid locus line");
            e.printStackTrace();
        }

        locus = "L       ORFP:YKL160W";
        try {
            DbLocus loc = reader.parseLocus(locus);
            assertEquals("ORFP:YKL160W", loc.getAccession());
            assertNull(loc.getDescription());
        }
        catch (DataProviderException e) {
            fail("Exception parsing valid locus line");
            e.printStackTrace();
        }
    }

    public void testGetSearchFileType () throws IOException {
        Reader reader = new StringReader(percolatorHeader());
        assertEquals(SearchFileFormat.SQT_PERC, SQTFileReader.getSearchFileType("dummy", reader));

        reader = new StringReader(prolucidHeader1());
        assertEquals(SearchFileFormat.SQT_PLUCID, SQTFileReader.getSearchFileType("dummy", reader));

        reader = new StringReader(prolucidHeader2());
        assertEquals(SearchFileFormat.SQT_PLUCID, SQTFileReader.getSearchFileType("dummy", reader));
        
        reader = new StringReader(unrecognizedHeader());
        assertEquals(SearchFileFormat.UNKNOWN, SQTFileReader.getSearchFileType("dummy", reader));

        reader = new StringReader(sequestHeader());
        assertEquals(SearchFileFormat.SQT_SEQ, SQTFileReader.getSearchFileType("dummy", reader));

//        reader = new StringReader(normSequestHeader());
//        assertEquals(SearchFileFormat.SQT_NSEQ, SQTFileReader.getSearchFileType("dummy", reader));
    }

    private String percolatorHeader() {
        StringBuilder buf = new StringBuilder();
        buf.append("H       file massaged by\n");
        buf.append("H       Percolator v 1.01, Build Date Apr  2 2007 14:51:13\n");
        buf.append("H       Copyright (c) 2006-7 University of Washington. All rights reserved.\n");
        buf.append("H       Written by Lukas K<E4>ll (lukall@u.washington.edu) in the\n");
        buf.append("H       Department of Genome Science at the University of Washington.\n");
        buf.append("H       Issued command:\n");
        buf.append("H       percolator -v 0 -d -o /home/xhyi/otherlabs/Merz/07/pipeline/percolator/outputSqt.list /home/xhyi/otherlabs/Merz/07/pipeline/percolator/realSqt.list /home/xhyi/otherlabs/Merz/07/pipeline/percolator/randSqt.list\n");
        buf.append("H       Started Wed Jul 11 10:30:29 2007 on Processing took 1460 cpu seconds or 1468 seconds wall time\n");
        buf.append("H       InputFile: /home/xhyi/otherlabs/Merz/07/pipeline/sequest/062707-cortner-GSTAP15b.sqt\n");
        buf.append("H       OutputFile: /home/xhyi/otherlabs/Merz/07/pipeline/percolator/062707-cortner-GSTAP15b.sqt\n");
        buf.append("H       Output from percolator are put into the M-lines:\n");
        buf.append("H       6th field is relpace the percolator score and\n");
        buf.append("H       7th field is relpace the negative percolator q-value\n");
        buf.append("H       The q-value is negated to be able to set a upper limit with DTASelect\n");
        buf.append("H       SQTGenerator SEQUEST\n");
        buf.append("H       SQTGeneratorVersion     2.7\n");
        buf.append("H       Comment SEQUEST was written by J Eng and JR Yates, III\n");
        buf.append("H       Comment SEQUEST ref. J. Am. Soc. Mass Spectrom., 1994, v. 4, p. 976\n");
        buf.append("H       Comment SEQUEST ref. Eng,J.K.; McCormack A.L.; Yates J.R.\n");
        buf.append("H       Comment SEQUEST is licensed to Finnigan Corp.\n");
        buf.append("H       Comment Paralellization Program is run_ms2\n");
        buf.append("H       Comment run_ms2 was written by Rovshan Sadygov\n");
        buf.append("H       StartTime 07/09/2007, 10:28 AM\n");
        buf.append("S       00644   00644   2       0       maccoss008      1036.81 7365.9  287.5   158505\n");
        buf.append("M       1       5       1034.11     -0  -2.26273        -0.0942063       12     20          N.TNISSANAGAK.A     U\n");
        buf.append("L       YHR206W\n");
        buf.append("M       600     600     1       -3.419  -10     -1      0       0       I.AMINVALI.D    U\n");
        return buf.toString();
    }


    private String prolucidHeader1() {
        StringBuilder buf = new StringBuilder();
        buf.append("H       SQTGenerator    ProLuCID\n");
        buf.append("H       SQTGeneratorVersion     0.1\n");
        buf.append("H       Database        /bluefish/people-a/alisark/dbase/worm/WormBase_C-elegans_na_12-17-2006_con_reversed.fasta\n");
        buf.append("H       PrecursorMasses mono\n");
        buf.append("H       FragmentMasses  mono\n");
        buf.append("S       16970   16970   2       16530   bluefish0249    1602.8047       9823.00 0.1210  1205782\n");
        buf.append("M       1       12      1602.8049       0.0000  2.9482  3.988   22      48      M.(42.01056)AEQAAEQMLTVLEK.T    U\n");
        buf.append("L       F28B3.8\n");
        return buf.toString();
    }

    private String prolucidHeader2() {
        StringBuilder buf = new StringBuilder();
        buf.append("H       SQTGeneratorVersion     0.1\n");
        buf.append("H       Database        /bluefish/people-a/alisark/dbase/worm/WormBase_C-elegans_na_12-17-2006_con_reversed.fasta\n");
        buf.append("H       PrecursorMasses mono\n");
        buf.append("H       FragmentMasses  mono\n");
        buf.append("H       SQTGenerator    ProLuCID\n");
        buf.append("S       16970   16970   2       16530   bluefish0249    1602.8047       9823.00 0.1210  1205782\n");
        buf.append("M       1       12      1602.8049       0.0000  2.9482  3.988   22      48      M.(42.01056)AEQAAEQMLTVLEK.T    U\n");
        buf.append("L       F28B3.8\n");
        return buf.toString();
    }

    private String unrecognizedHeader() {
        StringBuilder buf = new StringBuilder();
        buf.append("H       SQTGenerator   \n");
        buf.append("H       SQTGeneratorVersion     0.1\n");
        buf.append("H       Database        /bluefish/people-a/alisark/dbase/worm/WormBase_C-elegans_na_12-17-2006_con_reversed.fasta\n");
        buf.append("H       PrecursorMasses mono\n");
        buf.append("H       FragmentMasses  mono\n");
        buf.append("S       16970   16970   2       16530   bluefish0249    1602.8047       9823.00 0.1210  1205782\n");
        buf.append("M       1       12      1602.8049       0.0000  2.9482  3.988   22      48      M.(42.01056)AEQAAEQMLTVLEK.T    U\n");
        buf.append("L       F28B3.8\n");
        return buf.toString();
    }

    private String sequestHeader() {
        StringBuilder buf = new StringBuilder();
        buf.append("H       SQTGenerator SEQUEST\n");
        buf.append("H       SQTGeneratorVersion     3.0\n");
        buf.append("H       Comment SEQUEST was written by J Eng and JR Yates, III\n");
        buf.append("H       Comment SEQUEST ref. J. Am. Soc. Mass Spectrom., 1994, v. 4, p. 976\n");
        buf.append("H       Comment SEQUEST ref. Eng,J.K.; McCormack A.L.; Yates J.R.\n");
        buf.append("H       Comment SEQUEST is licensed to Finnigan Corp.\n");
        buf.append("H       Comment Paralellization Program is run_ms2\n");
        buf.append("H       Comment run_ms2 was written by Rovshan Sadygov\n");
        buf.append("H       StartTime 05/05/2008, 11:26 PM\n");
        buf.append("H       EndTime 05/05/2008, 11:34 PM\n");
        buf.append("H       Database        /garibaldi/people-b/applications/yates/dbase/EBI-IPI_Arabidopsis_3.11_12-06-2005_reversed.fasta\n");
        buf.append("H       DBSeqLength     28288090\n");
        buf.append("H       DBLocusCount    68280\n");
        buf.append("H       PrecursorMasses AVG\n");
        buf.append("H       FragmentMasses  MONO\n");
        buf.append("H       Alg-PreMassTol  \n");
        buf.append("H       Alg-FragMassTol 0.0\n");
        buf.append("H       Alg-XCorrMode   0\n");
        buf.append("H       StaticMod       C=160.160\n");
        buf.append("H       Alg-DisplayTop  5\n");
        buf.append("H       Alg-IonSeries   0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0\n");
        buf.append("H       EnzymeSpec      No_Enzyme\n");
        buf.append("S       00006   00006   1       1       node0001        775.86000       3093.3  30.1    885786n");
        buf.append("M         1      59      774.86642      0.0000   0.6990   2.932   3     10        Q.AMYNEF.S    Un");
        return buf.toString();
    }

    private String normSequestHeader() {
        StringBuilder buf = new StringBuilder();
        buf.append("H       SQTGenerator    EE-normalized SEQUEST\n");
        buf.append("H       Comment SEQUEST was written by J Eng and JR Yates, III\n");
        buf.append("H       Comment SEQUEST ref. J. Am. Soc. Mass Spectrom., 1994, v. 4, p. 976\n");
        buf.append("H       Comment SEQUEST ref. Eng,J.K.; McCormack A.L.; Yates J.R.\n");
        buf.append("H       Comment SEQUEST is licensed to Finnigan Corp.\n");
        buf.append("H       Comment Normalized SEQUEST ref. MacCoss,M.J.; Wu C.C; Yates J.R.\n");
        buf.append("H       Comment Normalized SEQUEST ref. Anal. Chem. 2002, v. 74, p. 5593\n");
        buf.append("H       Comment Paralellization Program is run_ms2\n");
        buf.append("H       Comment run_ms2 was written by Rovshan Sadygov\n");
        buf.append("H       StartTime 03/26/2005, 01:51 PM\n");
        buf.append("H       EndTime 03/26/2005, 05:41 PM\n");
        buf.append("H       Database        /home/maccoss/dbase/SGD/SGD-orf-040204-rand.fasta\n");
        buf.append("H       DBSeqLength     6076840\n");
        buf.append("H       DBLocusCount    13468\n");
        buf.append("H       PrecursorMasses AVG\n");
        buf.append("H       FragmentMasses  MONO\n");
        buf.append("H       Alg-PreMassTol  3.000\n");
        buf.append("H       Alg-FragMassTol 0.0\n");
        buf.append("H       Alg-XCorrMode   1\n");
        buf.append("H       StaticMod       C=160.139\n");
        buf.append("H       Alg-DisplayTop  5\n");
        buf.append("H       Alg-IonSeries   0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0\n");
        buf.append("H       EnzymeSpec      No_Enzyme\n");
        buf.append("S       00002   00002   1       1       node010 422.02  1281.0  2.2     319348\n");
        buf.append("M         1       3      423.533        0.0000   0.2379   66.7    2      6          L.PLPP.S    U\n");
        buf.append("L       YAR068W\n");
        return buf.toString();
    }
    
    private SQTFileReader getSQTFileReader() {
        return new SQTFileReader(){

            @Override
            protected SQTSearchScanIn nextSearchScan()
                    throws DataProviderException {
                throw new UnsupportedOperationException();
            }};
    }
}
