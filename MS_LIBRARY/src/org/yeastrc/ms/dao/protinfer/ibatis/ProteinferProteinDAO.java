package org.yeastrc.ms.dao.protinfer.ibatis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.GenericProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.GenericProteinferProtein;
import org.yeastrc.ms.domain.protinfer.ProteinUserValidation;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class ProteinferProteinDAO extends BaseSqlMapDAO implements GenericProteinferProteinDAO<ProteinferProtein> {

    private static final String sqlMapNameSpace = "ProteinferProtein";

    public ProteinferProteinDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int save(GenericProteinferProtein<?> protein) {
        return saveAndReturnId(sqlMapNameSpace+".insert", protein);
    }
    
    @Override
    public int update(GenericProteinferProtein<?> protein) {
        return update(sqlMapNameSpace+".update", protein);
    }
    
    private boolean proteinPeptideMatchExists(int proteinId, int peptideId) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("proteinId", proteinId);
        map.put("peptideId", peptideId);
        int count = (Integer)queryForObject(sqlMapNameSpace+".checkProteinPeptideMatch", map);
        return count > 0;
    }
    
    public void saveProteinferProteinPeptideMatch(int pinferProteinId, int pinferPeptideId) {
        
        if(proteinPeptideMatchExists(pinferProteinId, pinferPeptideId))
            return;
        Map<String, Integer> map = new HashMap<String, Integer>(4);
        map.put("pinferProteinId", pinferProteinId);
        map.put("pinferPeptideId", pinferPeptideId);
        super.save(sqlMapNameSpace+".insertPeptideProteinMatch", map);
    }
    
    public void updateUserAnnotation(int pinferProteinId, String annotation) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("annotation", annotation);
        map.put("pinferProteinId", pinferProteinId);
        super.update(sqlMapNameSpace+".updateUserAnnotation", map);
    }
    
    public void updateUserValidation(int pinferProteinId, ProteinUserValidation validation) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        String validationStr = validation == null ? null : String.valueOf(validation.getStatusChar());
        map.put("userValidation", validationStr);
        map.put("pinferProteinId", pinferProteinId);
        super.update(sqlMapNameSpace+".updateUserValidation", map);
    }
    
    public ProteinferProtein loadProtein(int pinferProteinId) {
        return (ProteinferProtein) super.queryForObject(sqlMapNameSpace+".select", pinferProteinId);
    }
    
    @Override
    public ProteinferProtein loadProtein(int proteinferId, int nrseqProteinId) {
        Map<String, Integer> map = new HashMap<String, Integer>(4);
        map.put("pinferId", proteinferId);
        map.put("nrseqProteinId", nrseqProteinId);
        return (ProteinferProtein) super.queryForObject(sqlMapNameSpace+".selectProteinForNrseqId", map);
    }
    
    public List<Integer> getProteinferProteinIds(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinIdsForProteinferRun", proteinferId);
    }
    
    public List<ProteinferProtein> loadProteins(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinsForProteinferRun", proteinferId);
    }
    
    public List<Integer> getProteinIdsForNrseqIds(int proteinferId, ArrayList<Integer> nrseqIds) {
        if(nrseqIds == null || nrseqIds.size() == 0)
            return new ArrayList<Integer>(0);
        
        StringBuilder buf = new StringBuilder("(");
        for(int id: nrseqIds) {
            buf.append(id+",");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.append(")");
        
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("pinferId", proteinferId);
        map.put("nrseqIds", buf.toString());
        
        return queryForList(sqlMapNameSpace+".selectProteinIdsForNrseqIds", map);
    }
    
    public List<Integer> getNrseqIdsForRun(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectNrseqIdsForProteinferRun", proteinferId);
    }
    
    public int getPeptideCountForProtein(int nrseqId, List<Integer> pinferIds) {
        if(pinferIds == null || pinferIds.size() == 0)
            return 0;
        
        StringBuilder buf = new StringBuilder("(");
        for(int id: pinferIds) {
            buf.append(id+",");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.append(")");
        
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("nrseqId", nrseqId);
        map.put("pinferIds", buf.toString());
        
        Integer count = (Integer)queryForObject(sqlMapNameSpace+".peptideCountForProteinInRuns", map);
        if(count != null)
            return count;
        return 0;
    }
    
    public List<String> getPeptidesForProtein(int nrseqId, List<Integer> pinferIds) {
        if(pinferIds == null || pinferIds.size() == 0)
            return new ArrayList<String>(0);
        
        StringBuilder buf = new StringBuilder("(");
        for(int id: pinferIds) {
            buf.append(id+",");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.append(")");
        
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("nrseqId", nrseqId);
        map.put("pinferIds", buf.toString());
        
        return queryForList(sqlMapNameSpace+".peptidesForProteinInRuns", map);
    }
    
    public List<Integer> getProteinsForPeptide(int pinferId, String peptide, boolean exactMatch) {
        
        String peptideStr = exactMatch ? peptide : "%"+peptide+"%";
        Map<String, Object> map = new HashMap<String, Object>(6);
        if(exactMatch)
            map.put("exact", exactMatch);
        map.put("pinferId", pinferId);
        map.put("peptide", peptideStr);
        
        return queryForList(sqlMapNameSpace+".proteinsForPeptideInRun", map);
    }
    
    public int getProteinCount(int proteinferId) {
       return (Integer) queryForObject(sqlMapNameSpace+".selectProteinCountForProteinferRun", proteinferId); 
    }
    
    public void delete(int pinferProteinId) {
        super.delete(sqlMapNameSpace+".delete", pinferProteinId);
    }
    
    /**
     * Type handler for converting between ProteinUserValidation and SQL's CHAR type.
     */
    public static final class UserValidationTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToUserValidation(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            ProteinUserValidation validation = (ProteinUserValidation) parameter;
            if (validation == null)
                //setter.setNull(java.sql.Types.CHAR);
                setter.setString(String.valueOf(ProteinUserValidation.UNVALIDATED.getStatusChar()));
            else
                setter.setString(String.valueOf(validation.getStatusChar()));
        }

        public Object valueOf(String s) {
            return stringToUserValidation(s);
        }
        
        private ProteinUserValidation stringToUserValidation(String validationStr) {
            if (validationStr == null)
                return ProteinUserValidation.UNVALIDATED;
            if (validationStr.length() != 1)
                throw new IllegalArgumentException("Cannot convert "+validationStr+" to ProteinUserValidation");
            ProteinUserValidation userValidation = ProteinUserValidation.getStatusForChar(Character.valueOf(validationStr.charAt(0)));
            if (userValidation == null)
                throw new IllegalArgumentException("Invalid ProteinUserValidation value: "+validationStr);
            return userValidation;
        }
    }
    
}
