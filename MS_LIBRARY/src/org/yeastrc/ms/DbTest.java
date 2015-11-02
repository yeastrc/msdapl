package org.yeastrc.ms;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.sequest.impl.SequestResult;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestResultPeptideBuilder;


public class DbTest {

    private static char[] aa = new char[]{'G', 'A', 'S', 'P', 'V', 'T', 'C', 'L', 'I', 'X', 'N', 'O', 'B', 'D', 'Q', 'K', 'Z', 
        'E', 'M', 'H', 'F', 'R', 'Y', 'W'};

    private static Random rndgen = new Random( 19580427 );

    private SequestResultPeptideBuilder builder = SequestResultPeptideBuilder.instance();
    
    public static void main(String[] args) throws SQLException {
        DbTest test = new DbTest();
        
        DAOFactory.instance().getMsSearchResultDAO(); // load ibatis xml config files before we start measuring time.
        
        long s = System.currentTimeMillis();
//        test.emptyDbJDBC(200000);
        test.emptyDbIbatis(200000);
        long e = System.currentTimeMillis();

        System.out.println("time: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    }


    public void emptyDbIbatis(int numRecords) {

        int runSearchId = 1;;
        int scanId = 1;
        int charge = 1;
        double observedMass = 100.0;

        MsSearchResultDAO resDao = DAOFactory.instance().getMsSearchResultDAO();
        for (int i = 0; i < numRecords; i++) {

            MsSearchResultIn result = makeSearchResult(charge, observedMass);
            resDao.saveResultOnly(result, runSearchId, scanId);


            scanId = Math.max(1, (scanId + 1)%20000);
            if(scanId == 1)
                runSearchId++;
            charge = Math.max(1, (charge + 1) % 4);
            observedMass = Math.max(100.0, (observedMass + 25) % 1000.0);

        }
    }


    public void emptyDbJDBC (int numRecords) throws SQLException {

        /**
        +------------------+------------------+------+-----+---------+----------------+
        | id               | int(10) unsigned | NO   | PRI | NULL    | auto_increment | 
        | runSearchID      | int(10) unsigned | NO   | MUL | NULL    |                | 
        | scanID           | int(10) unsigned | NO   | MUL | NULL    |                | 
        | charge           | tinyint(4)       | NO   | MUL | NULL    |                | 
        | observedMass     | decimal(18,9)    | YES  |     | NULL    |                | 
        | peptide          | varchar(500)     | NO   | MUL | NULL    |                | 
        | preResidue       | char(1)          | YES  |     | NULL    |                | 
        | postResidue      | char(1)          | YES  |     | NULL    |                | 
        | validationStatus | 
         **/

        int runSearchId = 1;;
        int scanId = 1;
        int charge = 1;
        double observedMass = 100.0;

        String sql = "INSERT into msRunSearchResult (runSearchID, scanID, charge, observedMass, peptide, preResidue, postResidue) ";
        sql += "VALUES (?,?,?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement stmt = null;



        for (int i = 0; i < numRecords; i++) {

            try {
                conn = DAOFactory.instance().getConnection();
                stmt = conn.prepareStatement(sql);

                // create the result object
                MsSearchResultIn result = makeSearchResult(charge, observedMass);

                stmt.setInt(1, runSearchId);
                stmt.setInt(2, scanId);
                stmt.setInt(3, result.getCharge());
                stmt.setBigDecimal(4, result.getObservedMass());
                stmt.setString(5, result.getResultPeptide().getPeptideSequence());
                stmt.setString(6, String.valueOf(result.getResultPeptide().getPreResidue()));
                stmt.setString(7, String.valueOf(result.getResultPeptide().getPostResidue()));

                stmt.execute();

                scanId = Math.max(1, (scanId + 1)%20000);
                if(scanId == 1)
                    runSearchId++;
                charge = Math.max(1, (charge + 1) % 4);
                observedMass = Math.max(100.0, (observedMass + 25) % 1000.0);

            }
            finally {
                if(conn != null) try {
                    conn.close();
                }
                catch (SQLException e) {}
                if(stmt != null) try {
                    stmt.close();
                }
                catch (SQLException e) {}
            }    
        }

    }

    private SequestResult makeSearchResult(int charge,
            double observedMass) {
        SequestResult result = new SequestResult();
        result.setCharge(charge);
        result.setObservedMass(new BigDecimal(observedMass));
        
        String origSequence = generateResidue()+"."+generatePeptide()+"."+generateResidue();
        result.setOriginalPeptideSequence(origSequence);
        try {
            builder.build(origSequence, 
                    new ArrayList<MsResidueModificationIn>(0), 
                    new ArrayList<MsTerminalModificationIn>(0));
        }
        catch (SQTParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String generatePeptide() {
        // get a random number between 5 and 20;
        int random = rndgen.nextInt(15) + 5;
        return generatePeptide(random);
    }

    private String generatePeptide(int length) {
        StringBuilder buf = new StringBuilder();
        for(int i = 0; i < length; i++)
            buf.append(generateResidue());
        return buf.toString();
    }

    private char generateResidue() {
        return aa[rndgen.nextInt(aa.length)];
    }

    private static class TimeUtils {

        private TimeUtils() {}

        public static float timeElapsedSeconds(long start, long end) {
            if(end < start)
                return 0;
            return (end - start)/(1000.0f);
        }

        public static float timeElapsedMinutes(long start, long end) {
            if(end < start)
                return 0;
            return (end - start)/(1000.0f * 60.0f);
        }
    }


}
