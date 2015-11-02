package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinAndGroupLabel;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.ProteinGroupCoverageSorter.ProteinGroupCoverage;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.ProteinGroupNSAFSorter.ProteinGroupNsaf;
import org.yeastrc.ms.domain.protinfer.GenericProteinferProtein;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.ProteinUserValidation;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.idpicker.GenericIdPickerProtein;

import com.ibatis.sqlmap.client.SqlMapClient;

public abstract class AbstractIdPickerProteinDAO <P extends GenericIdPickerProtein<?>> 
    extends BaseSqlMapDAO
    implements GenericIdPickerProteinDAO<P> {

    private static final String sqlMapNameSpace = "IdPickerProtein";
    
    private final ProteinferProteinDAO protDao;
    
    public AbstractIdPickerProteinDAO(SqlMapClient sqlMap, ProteinferProteinDAO protDao) {
        super(sqlMap);
        this.protDao = protDao;
    }

    public int save(GenericProteinferProtein<?> protein) {
        return protDao.save(protein);
    }
    
    public int update(GenericProteinferProtein<?> protein) {
        return protDao.update(protein);
    }
    
    public int updateIdPickerProtein(GenericIdPickerProtein<?> protein) {
        int updated = update(protein);
        if(updated > 0)
            return update(sqlMapNameSpace+".updateIdPickerProtein", protein);
        return 0;
    }
    
    public int updateIdPickerProteinOnly(GenericIdPickerProtein<?> protein) {
    	return update(sqlMapNameSpace+".updateIdPickerProtein", protein);
    }
    
    public int updateProteinSubsetValue(int proteinferId, boolean isSubset) {
    	Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("piProteinID", proteinferId);
        map.put("isSubset", isSubset);
        return super.update(sqlMapNameSpace+".updateProteinSubsetValue", map);
    }
    
    public int saveIdPickerProtein(GenericIdPickerProtein<?> protein) {
        int proteinId = save(protein);
        protein.setId(proteinId);
        save(sqlMapNameSpace+".insert", protein); // save entry in the IDPicker table
        return proteinId;
    }
    
    public boolean proteinPeptideGrpAssociationExists(int pinferId, int proteinGrpLabel, int peptideGrpLabel) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("pinferId", pinferId);
        map.put("proteinGroupLabel", proteinGrpLabel);
        map.put("peptideGroupLabel", peptideGrpLabel);
        int count = (Integer)queryForObject(sqlMapNameSpace+".checkGroupAssociation", map);
        return count > 0;
    }
    
    public void saveProteinPeptideGroupAssociation(int pinferId, int proteinGrpLabel, int peptideGrpLabel) {
        
        if(proteinPeptideGrpAssociationExists(pinferId, proteinGrpLabel, peptideGrpLabel))
            return;
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("pinferId", pinferId);
        map.put("proteinGroupLabel", proteinGrpLabel);
        map.put("peptideGroupLabel", peptideGrpLabel);
        save(sqlMapNameSpace+".insertGroupAssociation", map);
    }
    
    public void saveSubsetProtein(int subsetProteinId, int superProteinId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(6);
        map.put("subsetProteinId", subsetProteinId);
        map.put("superProteinId", superProteinId);
        save(sqlMapNameSpace+".insertSubsetSuperProtein", map);
    }
    
    public List<Integer> getGroupLabelsForCluster(int pinferId, int clusterLabel) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("clusterLabel", clusterLabel);
        return super.queryForList(sqlMapNameSpace+".selectProtGrpLabelsForCluster", map);
    }
    
    public List<Integer> getClusterLabels(int pinferId) {
       return queryForList(sqlMapNameSpace+".selectClusterLabelsForPinfer", pinferId); 
    }
    
    @Override
    public ProteinferProtein loadProtein(int proteinferId, int nrseqProteinId) {
        return protDao.loadProtein(proteinferId, nrseqProteinId);
    }
    
    
    public List<Integer> getProteinIdsForNrseqIds(int proteinferId, ArrayList<Integer> nrseqIds) {
        return protDao.getProteinIdsForNrseqIds(proteinferId, nrseqIds);
    }
    
    public int getFilteredParsimoniousProteinCount(int proteinferId) {
        return (Integer) queryForObject(sqlMapNameSpace+".selectParsimProteinCountForProteinferRun", proteinferId); 
    }
    
    @Override
    public void delete(int pinferProteinId) {
        protDao.delete(pinferProteinId);
    }

    @Override
    public int getProteinCount(int proteinferId) {
        return protDao.getProteinCount(proteinferId);
    }

    @Override
    public List<Integer> getProteinferProteinIds(int proteinferId) {
        return protDao.getProteinferProteinIds(proteinferId);
    }
    
    @Override
    public List<Integer> getNrseqIdsForRun(int proteinferId) {
        return protDao.getNrseqIdsForRun(proteinferId);
    }
    
    public List<Integer> getIdPickerGroupProteinIds(int pinferId, int proteinGroupLabel) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("proteinGroupLabel", proteinGroupLabel);
        return queryForList(sqlMapNameSpace+".selectProteinIdsForGroup", map);
    }
    
    @Override
    public int getPeptideCountForProtein(int nrseqId, List<Integer> pinferIds) {
        return protDao.getPeptideCountForProtein(nrseqId, pinferIds);
    }
    
    @Override
    public List<String> getPeptidesForProtein(int nrseqId, List<Integer> pinferIds) {
        return protDao.getPeptidesForProtein(nrseqId, pinferIds);
    }
    
    @Override
    public List<Integer> getProteinsForPeptide(int pinferId, String peptide, boolean exactMatch) {
        return protDao.getProteinsForPeptide(pinferId, peptide, exactMatch); 
    }
    
    @Override
    public void updateUserAnnotation(int pinferProteinId, String annotation) {
        protDao.updateUserAnnotation(pinferProteinId, annotation);
    }

    @Override
    public void updateUserValidation(int pinferProteinId,
            ProteinUserValidation validation) {
        protDao.updateUserValidation(pinferProteinId, validation);
    }
    
    public void saveProteinferProteinPeptideMatch(int pinferProteinId,
            int pinferPeptideId) {
       protDao.saveProteinferProteinPeptideMatch(pinferProteinId, pinferPeptideId);
    }
    
    public int getIdPickerGroupCount(int pinferId) {
    	
       return getIdPickerGroupCount(pinferId, false, false);
    }
    
    public int getIdPickerParsimoniousGroupCount(int pinferId) {

    	return getIdPickerGroupCount(pinferId, true, false);
    }
    
    public int getIdPickerNonSubsetGroupCount(int pinferId) {

    	return getIdPickerGroupCount(pinferId, false, true);
    }
    
    private int getIdPickerGroupCount(int pinferId, boolean parsimonious, boolean isNotSubset) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        if(parsimonious) {
            map.put("isParsimonious", 1);
        }
        if(isNotSubset) {
            map.put("isSubset", 0);
        }
        return (Integer)queryForObject(sqlMapNameSpace+".selectGroupCount", map);
    }
    
    // -----------------------------------------------------------------------------------
    // COVERAGE
    // -----------------------------------------------------------------------------------
    public List<Integer> sortProteinIdsByCoverage(int pinferId, boolean groupProteins, SORT_ORDER sortOrder) {
        return proteinIdsByCoverage(pinferId, 0.0, 100.0, groupProteins, sortOrder);
    }
    
    private List<Integer> proteinIdsByCoverage(int pinferId, 
            double minCoverage, double maxCoverage,
            boolean groupProteins, SORT_ORDER sortOrder) {
        
        Map<String, Number> map = new HashMap<String, Number>(10);
        map.put("pinferId", pinferId);
        map.put("minCoverage", minCoverage);
        map.put("maxCoverage", maxCoverage);
        
        boolean sort = sortOrder != null;
        
        if(!groupProteins || (groupProteins && !sort)) {
            if(sort)    map.put("sort", 1);
            return queryForList(sqlMapNameSpace+".filterByCoverage", map);
        }
        else { // group proteins and sort
            // List of protein IDs along with their groupLabel and coverage (sorted by group, then coverage).
            List<ProteinGroupCoverage> prGrC = queryForList(sqlMapNameSpace+".filterProteinGroupCoverage", map);
            
            ProteinGroupCoverageSorter.getInstance().sort(prGrC, sortOrder);
            
            List<Integer> proteinIds = new ArrayList<Integer>(prGrC.size());
            
            // return the list of protein IDs in the sorted order obove.
            for(ProteinGroupCoverage pgc: prGrC)
                proteinIds.add(pgc.getProteinId());
            return proteinIds;
        }
    }
    
	private List<Integer> nrseqIdsByCoverage(int pinferId, 
            double minCoverage, double maxCoverage) {
        
        if(minCoverage == 0 && maxCoverage == 100.0) {
            return getNrseqIdsForRun(pinferId);
        }
        Map<String, Number> map = new HashMap<String, Number>(10);
        map.put("pinferId", pinferId);
        map.put("minCoverage", minCoverage);
        map.put("maxCoverage", maxCoverage);
        
        return queryForList(sqlMapNameSpace+".filterNrseqIdsByCoverage", map);
    }
    
    // -----------------------------------------------------------------------------------
    // NSAF
    // -----------------------------------------------------------------------------------
    public List<Integer> sortProteinsByNSAF(int pinferId, boolean groupProteins, SORT_ORDER sortOrder) {
        
        return proteinIdsByNSAF(pinferId, 0.0, groupProteins, sortOrder);
    }
    
    private List<Integer> proteinIdsByNSAF(int pinferId, double minNsaf, boolean groupProteins, SORT_ORDER sortOrder) {
        
        Map<String, Number> map = new HashMap<String, Number>(6);
        map.put("pinferId", pinferId);
        map.put("nsaf", minNsaf);
        
        boolean sort = sortOrder != null;
        
        if (!groupProteins || (groupProteins && !sort)) {
            if(sort)    map.put("sort", 1);
            return queryForList(sqlMapNameSpace+".filterByNsaf", map);
        }
        else { // group proteins and sort
            // List of protein IDs along with their groupLabel and NSAF (sorted by group, then NSAF).
            List<ProteinGroupNsaf> prGrN = queryForList(sqlMapNameSpace+".filterProteinGroupNSAF", map);
            ProteinGroupNSAFSorter.getInstance().sort(prGrN, sortOrder);
            
            List<Integer> proteinIds = new ArrayList<Integer>(prGrN.size());
            
            // return the list of protein IDs in the sorted order obove.
            for(ProteinGroupNsaf pgc: prGrN)
                proteinIds.add(pgc.getProteinId());
            return proteinIds;
        }
    }
    
    // -----------------------------------------------------------------------------------
    // VALIDATION STATUS
    // -----------------------------------------------------------------------------------
    public List<Integer> sortProteinIdsByValidationStatus(int pinferId) {
        return proteinIdsByValidationStatus(pinferId, new ArrayList<ProteinUserValidation>(0), true);
    }
    
    private List<Integer> proteinIdsByValidationStatus(int pinferId,
            List<ProteinUserValidation> validationStatus, boolean sort) {
        Map<String, Object> map = new HashMap<String, Object>(6);
        map.put("pinferId", pinferId);
        if(validationStatus != null && validationStatus.size() > 0) {
            String vs = "";
            for(ProteinUserValidation v: validationStatus)
                vs += ",\'"+v.getStatusChar()+"\'";
            if(vs.length() > 0) vs = vs.substring(1); // remove first comma
            vs = "("+vs+")";
            map.put("validationStatus", vs);
        }
        if(sort)    map.put("sort", 1);
        return queryForList(sqlMapNameSpace+".filterByValidationStatus", map);
    }
    
    private List<Integer> nrseqIdsByValidationStatus(int pinferId,
            List<ProteinUserValidation> validationStatus) {
        
        Map<String, Object> map = new HashMap<String, Object>(6);
        map.put("pinferId", pinferId);
        if(validationStatus != null && validationStatus.size() > 0) {
            String vs = "";
            for(ProteinUserValidation v: validationStatus)
                vs += ",\'"+v.getStatusChar()+"\'";
            if(vs.length() > 0) vs = vs.substring(1); // remove first comma
            vs = "("+vs+")";
            map.put("validationStatus", vs);
        }
        return queryForList(sqlMapNameSpace+".filterNrseqIdsByValidationStatus", map);
    }
    
    // -----------------------------------------------------------------------------------
    // SPECTRUM COUNT
    // -----------------------------------------------------------------------------------
    public List<Integer> sortProteinIdsBySpectrumCount(int pinferId, boolean groupProteins) {
        return proteinIdsBySpectrumCount(pinferId, 1, Integer.MAX_VALUE, true, groupProteins);
    }
    
    private List<Integer> nrseqIdsBySpectrumCount(int pinferId, int minSpecCount, int maxSpecCount) {
        
        // If we are NOT filtering anything just return all the protein Ids
        // for this protein inference run
        if(minSpecCount <= 1 && maxSpecCount == Integer.MAX_VALUE) {
            return getNrseqIdsForRun(pinferId); // get both parsimonious and non-parsimonious
        }
        
        Map<String, Number> map = new HashMap<String, Number>(10);
        map.put("pinferId", pinferId);
        map.put("minSpectra", minSpecCount);
        map.put("maxSpectra", maxSpecCount);
        return queryForList(sqlMapNameSpace+".filterNrseqIdsBySpecCount", map);
    }
    
    private List<Integer> proteinIdsBySpectrumCount(int pinferId, int minSpecCount, int maxSpecCount,
            boolean sort, boolean groupProteins) {
        
        // If we are NOT filtering anything AND NOT sorting on spectrum count just return all the protein Ids
        // for this protein inference run
        if(minSpecCount <= 1 && maxSpecCount == Integer.MAX_VALUE && !sort) {
            return protDao.getProteinferProteinIds(pinferId);
        }
        
        Map<String, Number> map = new HashMap<String, Number>(10);
        map.put("pinferId", pinferId);
        map.put("minSpectra", minSpecCount);
        map.put("maxSpectra", maxSpecCount);
        if(sort)                    map.put("sort", 1);
        if(sort && groupProteins)   map.put("groupProteins", 1);
        return queryForList(sqlMapNameSpace+".filterBySpecCount", map);
    }
    
    // -----------------------------------------------------------------------------------
    // PEPTIDE CHARGE STATE
    // -----------------------------------------------------------------------------------
    private List<Integer> proteinIdsByPeptideChargeState(int pinferId,
            List<Integer> chargeStates, int chargeGreaterThan) {
        Map<String, Object> map = new HashMap<String, Object>(6);
        map.put("pinferId", pinferId);
        if(chargeStates != null && chargeStates.size() > 0) {
            String cs = "";
            for(Integer c: chargeStates)
                cs += ","+c;
            if(cs.length() > 0) cs = cs.substring(1); // remove first comma
            cs = "("+cs+")";
            map.put("chargeStates", cs);
        }
        if(chargeGreaterThan != -1) {
            map.put("chargeGreaterThan", chargeGreaterThan);
        }
        return queryForList(sqlMapNameSpace+".filterByChargeStates", map);
    }
    
    // -----------------------------------------------------------------------------------
    // PEPTIDE COUNT
    // -----------------------------------------------------------------------------------
    public List<Integer> sortProteinIdsByPeptideCount(int pinferId, PeptideDefinition peptideDef, boolean groupProteins) {
        return proteinIdsByPeptideCount(pinferId, 1, Integer.MAX_VALUE, peptideDef, true, groupProteins, 
        		true,true, // get both parsimoniious and non-parsimonious
        		true,true, // get both subset and non-subset
        		false);
    }
    
    public List<Integer> sortProteinIdsByUniquePeptideCount(int pinferId, PeptideDefinition peptideDef, boolean groupProteins) {
        return proteinIdsByPeptideCount(pinferId, 0, Integer.MAX_VALUE, peptideDef, true, groupProteins, 
        		true,true, // get both parsimoniious and non-parsimonious
        		true,true, // get both subset and non-subset 
        		true);
    }
    
    private List<Integer> proteinIdsByUniquePeptideCount(int pinferId, 
            int minUniqPeptideCount, int maxUniqPeptideCount, 
            PeptideDefinition peptDef,
            boolean sort, boolean groupProteins, 
            boolean parsimonious, boolean nonParsimonious, 
            boolean subset, boolean nonSubset) {
        return proteinIdsByPeptideCount(pinferId, 
                minUniqPeptideCount, maxUniqPeptideCount, 
                peptDef, sort, groupProteins, parsimonious, nonParsimonious, subset, nonSubset, true);
    }
    
    private List<Integer> proteinIdsByAllPeptideCount(int pinferId, 
            int minUniqPeptideCount, int maxUniqPeptideCount,
            PeptideDefinition peptDef,
            boolean sort, boolean groupProteins, 
            boolean parsimonious, boolean nonParsimonious, 
            boolean subset, boolean nonSubset) {
        return proteinIdsByPeptideCount(pinferId, 
                minUniqPeptideCount, maxUniqPeptideCount,
                peptDef, sort, groupProteins, parsimonious, nonParsimonious, subset, nonSubset, false);
    }
    
    private List<Integer> nrseqIdsByUniquePeptideCount(int pinferId, 
            int minUniqPeptideCount, int maxUniqPeptideCount, 
            PeptideDefinition peptDef, 
            boolean parsimonious, boolean nonParsimonious, 
            boolean subset, boolean nonSubset) {
        return nrseqIdsByPeptideCount(pinferId, 
                minUniqPeptideCount, maxUniqPeptideCount, 
                peptDef, parsimonious, nonParsimonious, subset, nonSubset, true);
    }
    
    private List<Integer> nrseqIdsByAllPeptideCount(int pinferId, 
            int minUniqPeptideCount, int maxUniqPeptideCount,
            PeptideDefinition peptDef,
            boolean parsimonious, boolean nonParsimonious, 
            boolean subset, boolean nonSubset) {
        return nrseqIdsByPeptideCount(pinferId, 
                minUniqPeptideCount, maxUniqPeptideCount,
                peptDef, parsimonious, nonParsimonious, subset, nonSubset, false);
    }
    
    private List<Integer> proteinIdsByPeptideCount(int pinferId, int minPeptideCount, int maxPeptideCount,
            PeptideDefinition peptDef,
            boolean sort, boolean groupProteins, 
            boolean parsimonious, boolean nonParsimonious, 
            boolean subset, boolean nonSubset, 
            boolean uniqueToProtein) {
        
    	
    	if((!parsimonious && !nonParsimonious) || (!subset && !nonSubset))
    		return new ArrayList<Integer>(0);
    	
        // If we are NOT filtering anything AND NOT sorting on peptide count just return all the protein Ids
        // for this protein inference run
        if(!uniqueToProtein) {
            if(minPeptideCount <= 1 && maxPeptideCount == Integer.MAX_VALUE && !sort) {
                return getIdPickerProteinIds(pinferId, parsimonious, nonParsimonious, subset, nonSubset);
            }
        }
        else {
            if(minPeptideCount <= 0 && maxPeptideCount == Integer.MAX_VALUE && !sort) {
            	return getIdPickerProteinIds(pinferId, parsimonious, nonParsimonious, subset, nonSubset);
            }
        }
        
        Map<String, Number> map = new HashMap<String, Number>(12);
        map.put("pinferId", pinferId);
        map.put("minPeptides", minPeptideCount);
        map.put("maxPeptides", maxPeptideCount);
        if(uniqueToProtein) map.put("uniqueToProtein", 1);
        if(parsimonious && !nonParsimonious)      map.put("isParsimonious", 1);
        if(!parsimonious && nonParsimonious)      map.put("isParsimonious", 0);
        if(nonSubset && !subset)				  map.put("isSubset", 0);
        if(!nonSubset && subset)				  map.put("isSubset", 1);
        if(sort)                    map.put("sort", 1);
        if(sort && groupProteins)   map.put("groupProteins", 1);
        
        List<Integer> proteinIds = null;
        // peptide uniquely defined by sequence
        if(!peptDef.isUseCharge() && !peptDef.isUseMods()) {
            proteinIds = queryForList(sqlMapNameSpace+".filterByPeptideCount_S", map);
        }
        // peptide uniquely defined by sequence, mods and charge
        if(peptDef.isUseCharge() && peptDef.isUseMods()) {
            proteinIds = queryForList(sqlMapNameSpace+".filterByPeptideCount_SMC", map);
        }
        
        boolean isParsimonious = parsimonious && !nonParsimonious;
        boolean isNotSubset = !subset && nonSubset;
        
        // peptide uniquely defined by sequence and charge
        if(peptDef.isUseCharge() && !peptDef.isUseMods()) {
            proteinIds = peptideIdsByPeptideCount_SM_OR_SC(pinferId, minPeptideCount, maxPeptideCount,
                    sort, groupProteins, isParsimonious, isNotSubset, uniqueToProtein, "charge");
        }
        // peptide uniquely defined by sequence and mods
        if(peptDef.isUseMods() && !peptDef.isUseCharge()) {
            proteinIds = peptideIdsByPeptideCount_SM_OR_SC(pinferId, minPeptideCount, maxPeptideCount,
                    sort, groupProteins, isParsimonious, isNotSubset, uniqueToProtein, "modificationStateID");
        }
        
        return proteinIds;
    }
    
    private List<Integer> nrseqIdsByPeptideCount(int pinferId, int minPeptideCount, int maxPeptideCount,
            PeptideDefinition peptDef,
            boolean parsimonious, boolean nonParsimonious, 
            boolean subset, boolean nonSubset,
            boolean uniqueToProtein) {
        
    	if((!parsimonious && !nonParsimonious) || (!subset && !nonSubset))
    		return new ArrayList<Integer>(0);
    	
    	
        // If we are NOT filtering anything just return all the nrseq protein Ids
        // for this protein inference run
        if(!uniqueToProtein) {
            if(minPeptideCount <= 1 && maxPeptideCount == Integer.MAX_VALUE) {
                return getNrseqProteinIds(pinferId, parsimonious, nonParsimonious, subset, nonSubset);
            }
        }
        else {
            if(minPeptideCount <= 0 && maxPeptideCount == Integer.MAX_VALUE) {
                return getNrseqProteinIds(pinferId, parsimonious, nonParsimonious, subset, nonSubset);
            }
        }
        
        Map<String, Number> map = new HashMap<String, Number>(12);
        map.put("pinferId", pinferId);
        map.put("minPeptides", minPeptideCount);
        map.put("maxPeptides", maxPeptideCount);
        if(uniqueToProtein) map.put("uniqueToProtein", 1);
        if(parsimonious && !nonParsimonious)      map.put("isParsimonious", 1);
        if(!parsimonious && nonParsimonious)      map.put("isParsimonious", 0);
        if(nonSubset && !subset)				  map.put("isSubset", 0);
        if(!nonSubset && subset)				  map.put("isSubset", 1);
        
        List<Integer> proteinIds = null;
        // peptide uniquely defined by sequence
        if(!peptDef.isUseCharge() && !peptDef.isUseMods()) {
            proteinIds = queryForList(sqlMapNameSpace+".filterNrseqIdsByPeptideCount_S", map);
        }
        // peptide uniquely defined by sequence, mods and charge
        if(peptDef.isUseCharge() && peptDef.isUseMods()) {
            proteinIds = queryForList(sqlMapNameSpace+".filterNrseqIdsByPeptideCount_SMC", map);
        }
        
        return proteinIds;
    }
    
    private List<Integer> peptideIdsByPeptideCount_SM_OR_SC(int pinferId,
            int minPeptideCount, int maxPeptideCount, 
            boolean sort, boolean groupProteins,
            boolean isParsimonious, boolean isNotSubset,
            boolean uniqueToProtein, String ionTableColumn) {
        
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // first create a temporary table
        String sql = "CREATE TEMPORARY TABLE ion_temp (piPeptideID INT UNSIGNED NOT NULL, piIonID INT UNSIGNED NOT NULL)";
        try {
           conn = super.getConnection();
           stmt  = conn.createStatement();
           stmt.executeUpdate(sql);
           
           stmt.close();
           
           stmt = conn.createStatement();
           sql = "INSERT INTO ion_temp "+
                  "(SELECT pept.id, ion.id FROM msProteinInferIon AS ion, msProteinInferPeptide AS pept "+
                  "WHERE pept.piRunID="+pinferId+
                  " AND pept.id = ion.piPeptideID GROUP BY pept.id, ion."+ionTableColumn+")";
           stmt.executeUpdate(sql);
           
           stmt.close();
           
           // add index on temp table
           sql = "ALTER TABLE ion_temp ADD INDEX (piPeptideID)";
           stmt = conn.createStatement();
           stmt.executeUpdate(sql);
           stmt.close();
           
           
           // now run the query we are interested in
           sql = prepareSql(pinferId, isParsimonious, isNotSubset,
                   uniqueToProtein, sort, groupProteins);
           pstmt = conn.prepareStatement(sql);
           pstmt.setInt(1, pinferId);
           pstmt.setInt(2, minPeptideCount);
           pstmt.setInt(3, maxPeptideCount);
           
           List<Integer> proteinIds = new ArrayList<Integer>();
           rs = pstmt.executeQuery();
           while(rs.next()) {
               proteinIds.add(rs.getInt("id"));
           }
           pstmt.close();
           
           // drop the temporary table
           stmt = conn.createStatement();
           sql = "DROP TABLE ion_temp";
           stmt.execute(sql);
           
           return proteinIds;
        }
        catch (SQLException e) {
            log.error("Failed in method peptideIdsByPeptideCount_SC_OR_SM", e);
            throw new RuntimeException("Failed in method peptideIdsByPeptideCount_SC_OR_SM", e);
        }
        finally {
             try {
                if(rs != null)      rs.close();
                if(stmt != null)    stmt.close();
                if(pstmt != null)   pstmt.close();
                if(conn != null)    conn.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            
        }
    }

    private String prepareSql(int pinferId, boolean isParsimonious, boolean isNotSubset, boolean uniqueToProtein, 
            boolean sort, boolean groupProteins) {
        
        String sql = "SELECT prot.id, count(ion.piIonID) AS cnt "+
                    "FROM (msProteinInferProtein AS prot, IDPickerProtein as idpProt, msProteinInferProteinPeptideMatch as m) "+
                    "LEFT JOIN ( msProteinInferPeptide AS pept, ion_temp AS ion  ) "+
                    "ON  (m.piPeptideID = pept.id AND pept.id = ion.piPeptideID ";
                    if(uniqueToProtein)
                        sql += "AND pept.uniqueToProtein = 1 ";
                    sql += ") ";
                    
                    sql += "WHERE prot.piRunID = ? "+
                    "AND prot.id = idpProt.piProteinID "+
                    "AND prot.id = m.piProteinID ";
        if(isParsimonious)
            sql += "AND idpProt.isParsimonious = 1 ";
        if(isNotSubset)
        	sql += "AND idpProt.isSubset = 0 ";
        sql += "GROUP BY prot.id HAVING cnt BETWEEN ? AND ?";
        if(sort) {
            sql += "ORDER BY cnt ";
            if(groupProteins) {
                sql += ", idpProt.proteinGroupLabel ";
            }
            sql += "DESC";
        }
       return sql;
    }
    
    public List<Integer> getIdPickerParsimoniousProteinIds(int pinferId) {
    	
        return getIdPickerProteinIds(pinferId, 
        							true,  	// parsimonious
        							false,	// non-parsimonious
        							true,	// subset
        							true	// non-subset
        							);
    }
    
    public List<Integer> getIdPickerNonSubsetProteinIds(int pinferId) {
    	return getIdPickerProteinIds(pinferId, 
									true,  	// parsimonious
									true,	// non-parsimonious
									false,	// subset
									true	// non-subset
									);
    }
    
    
    private List<Integer> getIdPickerProteinIds(int pinferId, 
    		boolean parsimonious, boolean nonParsimonious,
    		boolean subset, boolean nonSubset) {
    	
    	if((!parsimonious && !nonParsimonious) || 
    		(!subset && !nonSubset))
    		return new ArrayList<Integer>(0);
    	
        Map<String, Number> map = new HashMap<String, Number>(4);
        map.put("pinferId", pinferId);
        if(parsimonious && !nonParsimonious)      map.put("isParsimonious", 1);
        if(!parsimonious && nonParsimonious)      map.put("isParsimonious", 0);
        if(nonSubset && !subset)				  map.put("isSubset", 0);
        if(!nonSubset && subset)				  map.put("isSubset", 1);
        return queryForList(sqlMapNameSpace+".idPickerProteinIds", map);
    }
    
    
    public  List<Integer> getParsimoniousNrseqProteinIds(int pinferId) {
    	return getNrseqProteinIds(pinferId, true, false, true, true); // return only parsimonious
    }
    
    public  List<Integer> getNonParsimoniousNrseqProteinIds(int pinferId) {
    	return getNrseqProteinIds(pinferId, false, true, true, true); // return only non-parsimonious
    }
    
    public  List<Integer> getNonSubsetNrseqProteinIds(int pinferId) {
    	return getNrseqProteinIds(pinferId, true, true, false, true); // return only non-subset
    }
    
    public List<Integer> getSubsetNrseqProteinIds(int pinferId) {
    	return getNrseqProteinIds(pinferId, true, true, true, false); // return only subset
    }
    
    private List<Integer> getNrseqProteinIds(int pinferId, boolean parsimonious, boolean nonParsimonious, 
    		boolean subset, boolean nonSubset) {
        
    	if((!parsimonious && !nonParsimonious) || 
        		(!subset && !nonSubset))
        		return new ArrayList<Integer>(0);
        
        Map<String, Number> map = new HashMap<String, Number>(4);
        map.put("pinferId", pinferId);
        if(parsimonious && !nonParsimonious)            map.put("isParsimonious", 1);
        else if(!parsimonious && nonParsimonious)       map.put("isParsimonious", 0);
        
        if(nonSubset && !subset)					map.put("isSubset", 0);
        if(!nonSubset && subset)					map.put("isSubset", 1);
        
        return queryForList(sqlMapNameSpace+".idPickerNrseqProteinIds", map);
    }
    
    @Override
    public boolean isNrseqProteinGrouped(int pinferId, int nrseqId) {
        ProteinferProtein protein = this.loadProtein(pinferId, nrseqId);
        return isProteinGrouped(protein.getId());
    }

    @Override
    public boolean isProteinGrouped(int pinferProteinId) {
        P idpProt = this.loadProtein(pinferProteinId);
        int proteinGroupLabel = idpProt.getProteinGroupLabel();
        return (this.getIdPickerGroupProteinIds(idpProt.getProteinferId(), proteinGroupLabel).size() > 1);
    }
    
    /**
     * Returns a map of proteinIds and proteinGroupLabels.
     * Keys in the map are proteinIds and Values are proteinGroupLabels
     */
    public Map<Integer, Integer> getProteinGroupLabels(int pinferId) {
        Map<String, Number> map = new HashMap<String, Number>(8);
        map.put("pinferId", pinferId);
        List<ProteinAndGroupLabel> protGrps = queryForList(sqlMapNameSpace+".selectProteinAndGroupLabels", map);
        
        Map<Integer, Integer> protGrpmap = new HashMap<Integer, Integer>((int) (protGrps.size() * 1.5));
        for(ProteinAndGroupLabel pg: protGrps) {
            protGrpmap.put(pg.getProteinId(), pg.getGroupLabel());
        }
        return protGrpmap;
    }
    
    // -----------------------------------------------------------------------------------
    // PROTEIN CLUSTER
    // -----------------------------------------------------------------------------------
    public List<Integer> sortProteinIdsByCluster(int pinferId) {
        return queryForList(sqlMapNameSpace+".proteinIdsByCluster", pinferId);
    }

    public List<Integer> sortProteinIdsByGroup(int pinferId) {
        return queryForList(sqlMapNameSpace+".proteinIdsByGroup", pinferId);
    }
    
    // -----------------------------------------------------------------------------------
    // FILTER AND SORT PROTEIN IDS
    // -----------------------------------------------------------------------------------
    @Override
    public List<Integer> getFilteredSortedProteinIds(int pinferId, ProteinFilterCriteria filterCriteria) {
        
        // Get a list of protein ids filtered by sequence coverage
        SORT_ORDER sortOrder = filterCriteria.getSortBy() == SORT_BY.COVERAGE ? 
        		 				filterCriteria.getSortOrder() :
        		 				null;
        List<Integer> ids_cov = proteinIdsByCoverage(pinferId, 
                filterCriteria.getCoverage(), filterCriteria.getMaxCoverage(),
                filterCriteria.isGroupProteins(), sortOrder);
        
        // Get a list of protein ids filtered by spectrum count
        boolean sort = filterCriteria.getSortBy() == SORT_BY.NUM_SPECTRA;
        List<Integer> ids_spec_count = proteinIdsBySpectrumCount(pinferId, 
                filterCriteria.getNumSpectra(), filterCriteria.getNumMaxSpectra(),
                sort, filterCriteria.isGroupProteins());
        
        // Get a list of protein ids filtered by peptide count
        sort = filterCriteria.getSortBy() == SORT_BY.NUM_PEPT;
        List<Integer> ids_pept = proteinIdsByAllPeptideCount(pinferId, 
                                            filterCriteria.getNumPeptides(), filterCriteria.getNumMaxPeptides(),
                                            filterCriteria.getPeptideDefinition(),
                                            sort, 
                                            filterCriteria.isGroupProteins(), 
                                            filterCriteria.getParsimonious(),
                                            filterCriteria.getNonParsimonious(),
                                            filterCriteria.getSubset(),
                                            filterCriteria.getNonSubset()
                                            );
        
        // Get a list of protein ids filtered by UNIQUE peptide count
        List<Integer> ids_uniq_pept = null;
        if(filterCriteria.getNumUniquePeptides() == 0  &&
           filterCriteria.getNumMaxUniquePeptides() == Integer.MAX_VALUE
           && filterCriteria.getSortBy() != SORT_BY.NUM_UNIQ_PEPT) {
            ids_uniq_pept = ids_pept;
        }
        else {
            sort = filterCriteria.getSortBy() == SORT_BY.NUM_UNIQ_PEPT;
            ids_uniq_pept = proteinIdsByUniquePeptideCount(pinferId, 
                                               filterCriteria.getNumUniquePeptides(),
                                               filterCriteria.getNumMaxUniquePeptides(),
                                               filterCriteria.getPeptideDefinition(),
                                               sort,
                                               filterCriteria.isGroupProteins(),
                                               filterCriteria.getParsimonious(),
                                               filterCriteria.getNonParsimonious(),
                                               filterCriteria.getSubset(),
                                               filterCriteria.getNonSubset()
                                               );
        }
        
        
        // If the user is filtering on validation status 
        List<Integer> ids_validation_status = null;
        sort = filterCriteria.getSortBy() == SORT_BY.VALIDATION_STATUS;
        if(filterCriteria.getValidationStatus().size() > 0 || sort) {
            ids_validation_status = proteinIdsByValidationStatus(pinferId, filterCriteria.getValidationStatus(),
                                                                 sort);
        }
        
        // If the user if filtering on peptide charge states
        List<Integer> ids_chargeStates = null;
        if(filterCriteria.getChargeStates().size() > 0 || filterCriteria.getChargeGreaterThan() != -1) {
            ids_chargeStates = proteinIdsByPeptideChargeState(pinferId, filterCriteria.getChargeStates(),
                    filterCriteria.getChargeGreaterThan());
        }
        
        // If the user wants to exclude indistinguishable protein groups get the protein IDs that are not
        // in a indistinguishable protein group.
        List<Integer> notInGroup = null;
        if(filterCriteria.isExcludeIndistinGroups()) {
            notInGroup = proteinsNotInGroup(pinferId);
        }
        
        // If the user is only interested in proteins with a certain peptide
        List<Integer> peptideMatches = null;
        if(filterCriteria.getPeptide() != null) {
            peptideMatches = this.getProteinsForPeptide(pinferId, filterCriteria.getPeptide(), filterCriteria.getExactPeptideMatch());
        }
        
        
        // get the set of common ids; keep the order of ids returned from the query
        // that returned sorted results
        if(filterCriteria.getSortBy() == SORT_BY.COVERAGE) {
            Set<Integer> others = combineLists(ids_spec_count, ids_pept, ids_uniq_pept, ids_validation_status, notInGroup, 
                    peptideMatches, ids_chargeStates);
            return getCommonIds(ids_cov, others);
        }
        else if (filterCriteria.getSortBy() == SORT_BY.NUM_SPECTRA) {
            Set<Integer> others = combineLists(ids_cov, ids_pept, ids_uniq_pept, ids_validation_status, notInGroup, 
                    peptideMatches, ids_chargeStates);
            return getCommonIds(ids_spec_count, others);
        }
        else if (filterCriteria.getSortBy() == SORT_BY.NUM_PEPT) {
            Set<Integer> others = combineLists(ids_spec_count, ids_cov, ids_uniq_pept, ids_validation_status, notInGroup, 
                    peptideMatches, ids_chargeStates);
            return getCommonIds(ids_pept, others);
        }
        else if (filterCriteria.getSortBy() == SORT_BY.NUM_UNIQ_PEPT) {
            Set<Integer> others = combineLists(ids_spec_count, ids_pept, ids_cov, ids_validation_status, notInGroup, 
                    peptideMatches, ids_chargeStates);
            return getCommonIds(ids_uniq_pept, others);
        }
        else if (filterCriteria.getSortBy() == SORT_BY.GROUP_ID) {
            Set<Integer> others = combineLists(ids_spec_count, ids_pept, ids_cov, ids_uniq_pept, ids_validation_status, notInGroup, 
                    peptideMatches, ids_chargeStates);
            List<Integer> idsbyGroup = sortProteinIdsByGroup(pinferId);
            return getCommonIds(idsbyGroup, others);
        }
        else if (filterCriteria.getSortBy() == SORT_BY.CLUSTER_ID) {
            Set<Integer> others = combineLists(ids_spec_count, ids_pept, ids_cov, ids_uniq_pept, ids_validation_status, notInGroup, 
                    peptideMatches, ids_chargeStates);
            List<Integer> idsbyCluster = sortProteinIdsByCluster(pinferId);
            return getCommonIds(idsbyCluster, others);
        }
        else if(filterCriteria.getSortBy() == SORT_BY.VALIDATION_STATUS) {
            Set<Integer> others = combineLists(ids_spec_count, ids_pept, ids_cov, ids_uniq_pept, notInGroup, 
                    peptideMatches, ids_chargeStates);
            return getCommonIds(ids_validation_status, others);
        }
        else {
            Set<Integer> combineLists = combineLists(ids_cov, ids_spec_count, ids_pept, ids_uniq_pept, ids_validation_status, notInGroup, 
                    peptideMatches, ids_chargeStates);
            return new ArrayList<Integer>(combineLists);
        }
    }
    
    public List<Integer> proteinsNotInGroup(int pinferId) {
        return queryForList(sqlMapNameSpace+".proteinsNotInGroup", pinferId);
    }
    
    // -----------------------------------------------------------------------------------
    // FILTER NRSEQ PROTEIN IDS 
    // -----------------------------------------------------------------------------------
    @Override
    public List<Integer> getFilteredNrseqIds(int pinferId, ProteinFilterCriteria filterCriteria) {
        
        // Get a list of nrseq ids filtered by sequence coverage
        List<Integer> ids_cov = nrseqIdsByCoverage(pinferId, 
                filterCriteria.getCoverage(), filterCriteria.getMaxCoverage());
        
        // Get a list of nrseq ids filtered by spectrum count
        List<Integer> ids_spec_count = nrseqIdsBySpectrumCount(pinferId, 
                filterCriteria.getNumSpectra(), filterCriteria.getNumMaxSpectra());
        
        // Get a list of nrseq ids filtered by peptide count
        List<Integer> ids_pept = nrseqIdsByAllPeptideCount(pinferId, 
                                            filterCriteria.getNumPeptides(), filterCriteria.getNumMaxPeptides(),
                                            filterCriteria.getPeptideDefinition(),
                                            filterCriteria.getParsimonious(),
                                            filterCriteria.getNonParsimonious(),
                                            filterCriteria.getSubset(),
                                            filterCriteria.getNonSubset()
                                            );
        
        // Get a list of nrseq ids filtered by UNIQUE peptide count
        List<Integer> ids_uniq_pept = null;
        if(filterCriteria.getNumUniquePeptides() == 0  &&
           filterCriteria.getNumMaxUniquePeptides() == Integer.MAX_VALUE) {
            ids_uniq_pept = ids_pept;
        }
        else {
            ids_uniq_pept = nrseqIdsByUniquePeptideCount(pinferId, 
                                               filterCriteria.getNumUniquePeptides(),
                                               filterCriteria.getNumMaxUniquePeptides(),
                                               filterCriteria.getPeptideDefinition(),
                                               filterCriteria.getParsimonious(),
                                               filterCriteria.getNonParsimonious(),
                                               filterCriteria.getSubset(),
                                               filterCriteria.getNonSubset());
        }
        
        // If the user is filtering on validation status 
        List<Integer> ids_validation_status = null;
        if(filterCriteria.getValidationStatus().size() > 0) {
            ids_validation_status = nrseqIdsByValidationStatus(pinferId, filterCriteria.getValidationStatus());
        }
        
        // get the set of common ids;
        Set<Integer> combineLists = combineLists(ids_cov, ids_spec_count, ids_pept, ids_uniq_pept, ids_validation_status);
        return new ArrayList<Integer>(combineLists);
    }
    

    private final List<Integer> getCommonIds(List<Integer> ordered, Set<Integer> others) {
        Iterator<Integer> iter = ordered.iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            if(!others.contains(id))
                iter.remove();
        }
        return ordered;
    }
    
    private final Set<Integer> combineLists(List<Integer>...lists) {
        
        int numValidLists = 0;
        int count = 0;
        for(List<Integer> list: lists) {
            if(list == null)    continue;
            numValidLists++;
            count = Math.min(count, list.size());
        }
        Map<Integer, Integer> idCount = new HashMap<Integer, Integer>((int) (count*1.5));
        for(List<Integer> list: lists) {
            if(list == null)    continue;
            for(int id: list) {
                Integer c = idCount.get(id);
                if(c == null)   idCount.put(id, 1);
                else            idCount.put(id, ++c);
            }
        }
        Set<Integer> set = new HashSet<Integer>((int) (count*1.5));
        for(int id: idCount.keySet()) {
            if(idCount.get(id) == numValidLists)    set.add(id);
        }
        return set;
    }
}
