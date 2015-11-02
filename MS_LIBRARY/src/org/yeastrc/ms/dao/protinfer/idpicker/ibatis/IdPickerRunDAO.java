package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.GenericProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.domain.protinfer.GenericProteinferRun;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.domain.search.Program;

import com.ibatis.sqlmap.client.SqlMapClient;

public class IdPickerRunDAO extends BaseSqlMapDAO implements GenericProteinferRunDAO<IdPickerInput, IdPickerRun> {

    private static final String sqlMapNameSpace = "IdPickerRun";
    
    private final ProteinferRunDAO runDao;
    
    public IdPickerRunDAO(SqlMapClient sqlMap, ProteinferRunDAO runDao) {
        super(sqlMap);
        this.runDao = runDao;
    }

    @Override
    public IdPickerRun loadProteinferRun(int proteinferId) {
        return (IdPickerRun) queryForObject(sqlMapNameSpace+".select", proteinferId);
    }
    
    @Override
    public void delete(int pinferId) {
        runDao.delete(pinferId);
    }

    @Override
    public List<Integer> loadProteinferIdsForInputIds(List<Integer> inputIds) {
    	
    	if(inputIds.size() == 0) 
            return new ArrayList<Integer>(0);
        
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        for(Integer id: inputIds) {
            buf.append(id+",");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.append(")");
        
        return super.queryForList(sqlMapNameSpace+".selectIdpickerIdsForInputIds", buf.toString());
    }
    
    
    @Override
    public List<Integer> loadProteinferIdsForInputIds(List<Integer> inputIds, Program inputGenerator) {
    	
    	if(inputIds.size() == 0) 
            return new ArrayList<Integer>(0);
        
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        for(Integer id: inputIds) {
            buf.append(id+",");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.append(")");
        
        Map<String, String> map = new HashMap<String, String>(4);
        map.put("inputGenerator", inputGenerator.name());
        map.put("inputIds", buf.toString());
        
        return super.queryForList(sqlMapNameSpace+".selectIdpickerIdsForInputIdsProgram", map);
    }
    

    @Override
    public List<Integer> loadSearchIdsForProteinferRun(int pinferId) {
        return runDao.loadSearchIdsForProteinferRun(pinferId);
    }
    
    @Override
    public int save(GenericProteinferRun<?> run) {
        return runDao.save(run);
    }

    @Override
    public void update(GenericProteinferRun<?> run) {
        runDao.update(run);
    }

    @Override
    public int getMaxProteinHitCount(int proteinferId) {
        return runDao.getMaxProteinHitCount(proteinferId);
    }

	@Override
	public List<Integer> loadProteinferIdsForExperiment(int experimentId) {
		return runDao.loadProteinferIdsForExperiment(experimentId);
	}
}
