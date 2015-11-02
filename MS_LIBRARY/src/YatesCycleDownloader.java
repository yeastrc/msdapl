

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;
import org.yeastrc.ms2.utils.Decompresser;

public class YatesCycleDownloader {

    private static final Logger  log = Logger.getLogger(YatesCycleDownloader.class);
    public static enum DATA_TYPE {MS2, SQT;};
    
    public boolean downloadMS2File(int cycleId, String outdir, String outFile) throws ClassNotFoundException, SQLException {
        return downloadFile(cycleId, outdir, outFile, DATA_TYPE.MS2);
    }
    
    public boolean downloadSQTFile(int cycleId, String outdir, String outFile) throws ClassNotFoundException, SQLException {
       return downloadFile(cycleId, outdir, outFile, DATA_TYPE.SQT);
    }
    
    public boolean downloadFile(int cycleId, String outdir, String outFile, DATA_TYPE type) throws ClassNotFoundException, SQLException {
        String sql = null;
        if (type == DATA_TYPE.MS2)
            sql = "SELECT data from tblYatesCycleMS2Data WHERE cycleID="+cycleId;
        if (type == DATA_TYPE.SQT)
            sql = "SELECT data from tblYatesCycleSQTData WHERE cycleID="+cycleId;
        
        Connection conn = getConnection();
        Statement statement = null;
        ResultSet rs = null;
        log.debug("CycleID is: "+cycleId+"; fileName: "+outFile);
        statement = conn.createStatement();
        rs = statement.executeQuery(sql);
        boolean downloaded = true;
        
        if (rs.next()) {
            byte[] bytes = rs.getBytes("data");
            InputStream instr = null;
            try {
                instr = Decompresser.getInstance().decompressString(bytes);
            }
            catch (ZipException e) {log.error("",e); downloaded = false;}
            catch (IOException e) {log.error("",e); downloaded = false;}
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(instr));
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(outdir+File.separator+outFile));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.write("\n");
                }
            }
            catch (IOException e) { log.error("", e); downloaded = false;}
            finally {
                try {
                    if (reader != null) reader.close();
                    if (writer != null) writer.close();
                }
                catch (IOException e) {}
            }
        }
        else { log.error("No "+type+" data found for cycleID: "+cycleId); downloaded = false; }
        
        rs.close();
        statement.close();
        conn.close();
        
        return downloaded;
        
    }
    
    public static void downloadFileFromYatesServer(String srcFilePath, String destFilePath) {
        
    }
    
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName( "com.mysql.jdbc.Driver" );
        String URL = "jdbc:mysql://localhost/yrc";
        return DriverManager.getConnection( URL, "root", "");
    }
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        String downloadDir = "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/test";
          String downloadDir = "/Users/vagisha/WORK/MS_LIBRARY/new_lib/test_resources/validData_dir/temp";
//        int cycleId = 10936;
//        String fileName = cycleId+"_"+"PARC_ep2_03_itms"; // Prolucid file
        
        
        int runId =  771;
        int cycleId = 5489;
        // 20389 |  2985
        String fileName = runId+"_"+cycleId;
        
        YatesCycleDownloader downloader = new YatesCycleDownloader();
        downloader.downloadMS2File(cycleId, downloadDir, fileName+".ms2");
        downloader.downloadSQTFile(cycleId, downloadDir, fileName+".sqt");
    }
}
