package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProtein;

import com.ibatis.sqlmap.client.SqlMapClient;

public class IdPickerProteinDAO extends AbstractIdPickerProteinDAO<IdPickerProtein> {

    private static final String sqlMapNameSpace = "IdPickerProtein";
    
    public IdPickerProteinDAO(SqlMapClient sqlMap, ProteinferProteinDAO protDao) {
        super(sqlMap, protDao);
    }

    public IdPickerProtein loadProtein(int pinferProteinId) {
        return (IdPickerProtein) super.queryForObject(sqlMapNameSpace+".select", pinferProteinId);
    }
    
    public List<IdPickerProtein> loadProteins(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinsForProteinferRun", proteinferId);
    }
    
    public List<IdPickerProtein> loadIdPickerClusterProteins(int pinferId,int clusterLabel) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("clusterLabel", clusterLabel);
        return queryForList(sqlMapNameSpace+".selectProteinsForCluster", map);
    }
    
    public List<IdPickerProtein> loadIdPickerGroupProteins(int pinferId,int proteinGroupLabel) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("proteinGroupLabel", proteinGroupLabel);
        return queryForList(sqlMapNameSpace+".selectProteinsForGroup", map);
    }
    
//    public IdPickerProteinGroupSummary getIdPickerProteinGroupSummary(int pinferId, int proteinGroupId) {
//        List<IdPickerProtein> grpProteins = getIdPickerGroupProteins(pinferId, proteinGroupId);
//        IdPickerProteinGroupSummary summary = new IdPickerProteinGroupSummary(proteinGroupId);
//        if(grpProteins.size() == 0) 
//            return summary;
//        summary.setProteins(grpProteins);
//        int numPeptides = idpPeptDao.getPeptideIdsForProteinferProtein(grpProteins.get(0).getId()).size();
//        idpPeptDao.getP
//        
//    }
//    
//    public IdPickerProteinGroup getIdPickerProteinGroup(int pinferId, int groupId) {
//        List<IdPickerProtein> grpProteins = getIdPickerGroupProteins(pinferId, groupId);
//        IdPickerProteinGroup group = new IdPickerProteinGroup(groupId);
//        group.setProteins(grpProteins);
//        List<Integer> matchingPeptGrpIds = getMatchingPeptGroupIds(pinferId, groupId);
//        for(Integer peptGrpId: matchingPeptGrpIds) {
//            IdPickerPeptideGroup peptGrp = idpPeptDao.getIdPickerPeptideGroup(pinferId, peptGrpId);
//            group.addMatchingPeptideGroup(peptGrp);
//        }
//        return group;
//    }
//    
//    public List<IdPickerProteinGroup> getIdPickerProteinGroups(int pinferId) {
//        // get all the proteins
//        List<IdPickerProtein> allProteins = this.getProteins(pinferId);
//        // sort by groupID
//        Collections.sort(allProteins, new Comparator<IdPickerProtein>() {
//            public int compare(IdPickerProtein o1, IdPickerProtein o2) {
//                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
//            }});
//        
//        
//        List<IdPickerProteinGroup> groups = new ArrayList<IdPickerProteinGroup>(allProteins.get(allProteins.size() - 1).getGroupId());
//        int lastGrpId = -1;
//        IdPickerProteinGroup lastGrp = null;
//        for(IdPickerProtein protein: allProteins) {
//            if(protein.getGroupId() != lastGrpId) {
//                if(lastGrp != null) {
//                    // all all the peptide groups for this protein group
//                    List<Integer> matchingPeptGrpIds = getMatchingPeptGroupIds(pinferId, lastGrpId);
//                    for(Integer peptGrpId: matchingPeptGrpIds) {
//                        IdPickerPeptideGroup peptGrp = idpPeptDao.getIdPickerPeptideGroup(pinferId, peptGrpId);
//                        lastGrp.addMatchingPeptideGroup(peptGrp);
//                    }
//                    // add this to the list of groups
//                    groups.add(lastGrp);
//                }
//                lastGrp = new IdPickerProteinGroup(protein.getGroupId());
//                lastGrpId = protein.getGroupId();
//            }
//            lastGrp.addProtein(protein);
//        }
//        // add the last one
//        if(lastGrp != null) {
//            // all all the peptide groups for this protein group
//            List<Integer> matchingPeptGrpIds = getMatchingPeptGroupIds(pinferId, lastGrpId);
//            for(Integer peptGrpId: matchingPeptGrpIds) {
//                IdPickerPeptideGroup peptGrp = idpPeptDao.getIdPickerPeptideGroup(pinferId, peptGrpId);
//                lastGrp.addMatchingPeptideGroup(peptGrp);
//            }
//            // add this to the list of groups
//            groups.add(lastGrp);
//        }
//        
//        return groups;
//    }
    
    
    
//    public IdPickerCluster getIdPickerCluster(int pinferId, int clusterId) {
//        IdPickerCluster cluster = new IdPickerCluster(pinferId, clusterId);
//        List<Integer> protGrpIds = getGroupIdsForCluster(pinferId, clusterId);
//        Set<Integer> uniqPeptGrpIds = new HashSet<Integer>();
//        for(Integer protGrpId: protGrpIds) {
//            IdPickerProteinGroup protGrp = getIdPickerProteinGroup(pinferId, protGrpId);
//            cluster.addProteinGroup(protGrp);
//            for(IdPickerPeptideGroup peptGrp: protGrp.getMatchingPeptideGroups()) {
//                if(!uniqPeptGrpIds.contains(peptGrp.getGroupId())) {
//                    uniqPeptGrpIds.add(peptGrp.getGroupId());
//                    cluster.addPeptideGroup(peptGrp);
//                }
//            }
//        }
//        Collections.sort(cluster.getProteinGroups(), new Comparator<IdPickerProteinGroup>() {
//            public int compare(IdPickerProteinGroup o1, IdPickerProteinGroup o2) {
//                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
//            }});
//        
//        Collections.sort(cluster.getPeptideGroups(), new Comparator<IdPickerPeptideGroup>() {
//            public int compare(IdPickerPeptideGroup o1, IdPickerPeptideGroup o2) {
//                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
//            }});
//        return cluster;
//    }

 
}
