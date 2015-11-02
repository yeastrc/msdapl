import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms2.data.DTAPeptide;
import org.yeastrc.ms2.data.DTAPeptideLoader;
import org.yeastrc.ms2.data.DTAPeptideSaver;


public class YatesTablesUtils {

    public static final Logger log = Logger.getLogger(YatesTablesUtils.class);
    
    private YatesTablesUtils() {}
    
    public static List<Integer> getAllYatesRunIds(String queryQualifier) {
        
        Connection connect = null;
        Statement statement = null;
        ResultSet rs = null;
        
        // get a list of runIds from tblYatesCycles
        // String sql = "SELECT distinct runID FROM tblYatesCycles ORDER BY runID DESC limit 10";
        String sql = "SELECT distinct runID FROM tblYatesCycles "+queryQualifier;
        try {
            connect = getYRCConnection();
            statement = connect.createStatement();
            rs = statement.executeQuery(sql);
            List<Integer> yatesRunIds = new ArrayList<Integer>();
            while(rs.next()) {
                yatesRunIds.add(rs.getInt("runID"));
            }
            return yatesRunIds;
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<Integer>(0);
        }
        catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<Integer>(0);
        }
        finally {
            try {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
                if (connect != null) connect.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static List<YatesCycle> getCyclesForRun(int runId) {
        
        Connection connect = null;
        Statement statement = null;
        ResultSet rs = null;
        
        try {
            connect = getYRCConnection();
            // get a list of runIds from tblYatesCycles
            String sql = "SELECT runID, cycleID, cycleFileName FROM tblYatesCycles where runID="+runId;
            statement = connect.createStatement();
            rs = statement.executeQuery(sql);
            List<YatesCycle> cycles = new ArrayList<YatesCycle>();
            while(rs.next()) {
                cycles.add(new YatesCycle(rs.getInt("runID"), rs.getInt("cycleID"), rs.getString("cycleFileName")));
            }
            return cycles;
        }
        catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<YatesCycle>(0);
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<YatesCycle>(0);
        }
        finally {
            try {
                rs.close();
                statement.close();
                connect.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Integer> getYatesResultPeptideIds(int yatesRunId) {
        Connection connect = null;
        Statement statement = null;
        ResultSet rs = null;
        
        try {
            connect = getYRCConnection();
            // get a list of runIds from tblYatesCycles
            String sql = "select pep.id from tblYatesRun as run, tblYatesRunResult as res, tblYatesResultPeptide as pep where run.id = "+
                            yatesRunId+
                            " and run.id = res.runID and res.id =  pep.resultID";

            statement = connect.createStatement();
            rs = statement.executeQuery(sql);
            List<Integer> idList = new ArrayList<Integer>();
            while(rs.next()) {
                idList.add(rs.getInt("id"));
            }
            return idList;
        }
        catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<Integer>(0);
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<Integer>(0);
        }
        finally {
            try {
                rs.close();
                statement.close();
                connect.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } 
    }
    
    public static DTAPeptide loadDTAPeptide(int id) throws Exception {
        Connection connect = null;
        
        try {
            connect = getYRCConnection();
            return DTAPeptideLoader.getInstance().load(id, connect);
            
        }
        catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        finally {
            try {
                connect.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } 
    }
    
    public static void updateDTAPeptide(DTAPeptide peptide) throws Exception {
        Connection connect = null;
        
        try {
            connect = getYRCConnection();
            DTAPeptideSaver.getInstance().update(peptide, connect);
            
        }
        catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        finally {
            try {
                connect.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } 
    }
    
    public static List<Job> getAllJobs() {
        Connection connect = null;
        Statement statement = null;
        ResultSet rs = null;
        
        try {
            connect = getJobQueueConnection();
            // get a list of runIds from tblYatesCycles
            String sql = "select j.id, j.submitDate, j.lastUpdate, msj.groupID, msj.serverDirectory "+
                         "from tblJobs as j, tblMSJobs as msj where j.id = msj.jobID "+
                         "ORDER BY j.id DESC";
            statement = connect.createStatement();
            rs = statement.executeQuery(sql);
            List<Job> jobs = new ArrayList<Job>();
            while(rs.next()) {
                Job job = new Job();
                job.jobId = rs.getInt("id");
                job.groupId = rs.getInt("groupID");
                job.submitDate = rs.getDate("submitDate");
                job.lastChangedDate = rs.getDate("lastUpdate");
                job.dataDir = rs.getString("serverDirectory");
                jobs.add(job);
            }
            return jobs;
        }
        catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<Job>(0);
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<Job>(0);
        }
        finally {
            try {
                rs.close();
                statement.close();
                connect.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static Connection getJobQueueConnection() throws ClassNotFoundException, SQLException {
        Class.forName( "com.mysql.jdbc.Driver" );
        String URL = "jdbc:mysql://localhost/YRC_JOB_QUEUE";
        return DriverManager.getConnection( URL, "root", "");
    }
    
    public static Connection getYRCConnection() throws ClassNotFoundException, SQLException {
        Class.forName( "com.mysql.jdbc.Driver" );
        String URL = "jdbc:mysql://localhost/yrc";
        return DriverManager.getConnection( URL, "root", "");
    }
    
    public static final class YatesCycle {
        public int runId;
        public String cycleName;
        public int cycleId;

        public YatesCycle(int runId, int cycleId, String cycleName) {
            this.runId = runId;
            this.cycleId = cycleId;
            this.cycleName = cycleName;
        }
    }
    
    public static final class Job {
        private int jobId;
        private int groupId;
        private Date submitDate;
        private Date lastChangedDate;
        private String dataDir;
        
        public int getJobId() {
            return jobId;
        }
        public int getGroupId() {
            return groupId;
        }
        public Date getSubmitDate() {
            return submitDate;
        }
        public Date getLastChangedDate() {
            return lastChangedDate;
        }
        public String getDataDir() {
            return dataDir;
        }
        
    }
}
