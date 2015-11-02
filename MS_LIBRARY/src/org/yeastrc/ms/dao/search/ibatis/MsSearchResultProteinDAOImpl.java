/**
 * MsProteinMatchDAO.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.impl.SearchResultProteinBean;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSearchResultProteinDAOImpl extends BaseSqlMapDAO implements MsSearchResultProteinDAO {


    public MsSearchResultProteinDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MsSearchResultProtein> loadResultProteins(int resultId) {
        return queryForList("MsResultProtein.selectResultProteins", resultId);
    }
    
    
//    public List<MsSearchResultProtein> loadResultProteins(int resultId) {
//        
//        Connection conn = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//        
//        try {
//            
//            conn = super.getConnection();
//            String sql = "SELECT * FROM msProteinMatch WHERE resultID = ?";
//            stmt = conn.prepareStatement( sql );
//            stmt.setInt( 1, resultId );
//            rs = stmt.executeQuery();
//            
//            List<MsSearchResultProtein> proteinList = new ArrayList<MsSearchResultProtein>();
//            
//            while ( rs.next() ) {
//            
//                SearchResultProteinBean protein = new SearchResultProteinBean();
//                protein.setResultId(rs.getInt("resultID"));
//                protein.setAccession(rs.getString("accession"));
//                
//                proteinList.add(protein);
//            }
//            rs.close(); rs = null;
//            stmt.close(); stmt = null;
//            conn.close(); conn = null;
//            
//            return proteinList;
//            
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            
//            if (rs != null) {
//                try { rs.close(); rs = null; } catch (Exception e) { ; }
//            }
//
//            if (stmt != null) {
//                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
//            }
//            
//            if (conn != null) {
//                try { conn.close(); conn = null; } catch (Exception e) { ; }
//            }           
//        }
//        return null;
//    }
    
    public void save(MsSearchResultProteinIn resultProtein, int resultId) {
        save("MsResultProtein.insert", new SearchResultProteinBean(resultId, resultProtein.getAccession()));
    }
    
    @Override
    public void saveAll(List<MsSearchResultProtein> proteinMatchList) {
        if (proteinMatchList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MsSearchResultProtein match: proteinMatchList) {
            values.append(",(");
            values.append(match.getResultId() == 0 ? "NULL" : match.getResultId());
            values.append(",");
            boolean hasAcc = match.getAccession() != null;
            if (hasAcc) values.append("\"");
            values.append(match.getAccession());
            if (hasAcc) values.append("\"");
            values.append(")");
        }
        values.deleteCharAt(0);
        
        save("MsResultProtein.insertAll", values.toString());
    }
    
    public void delete(int resultId) {
        delete("MsResultProtein.deleteForResultId", resultId);
    }
    
    @Override
    public void disableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE msProteinMatch DISABLE KEYS";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
    }

    @Override
    public void enableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE msProteinMatch ENABLE KEYS";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
    }
}


