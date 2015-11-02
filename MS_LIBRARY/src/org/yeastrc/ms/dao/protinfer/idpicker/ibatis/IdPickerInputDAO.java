package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.GenericProteinferInputDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferInputDAO;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;

import com.ibatis.sqlmap.client.SqlMapClient;

public class IdPickerInputDAO extends BaseSqlMapDAO implements GenericProteinferInputDAO<IdPickerInput> {

    private static final String sqlMapNameSpace = "IdPickerInput";
    private final ProteinferInputDAO pinferInputDao;
    
    public IdPickerInputDAO(SqlMapClient sqlMap, ProteinferInputDAO inputDao) {
        super(sqlMap);
        this.pinferInputDao = inputDao;
    }

    public List<IdPickerInput> loadProteinferInputList(int pinferId) {
        return queryForList(sqlMapNameSpace+".selectIdPickerInputList", pinferId);
    }
    
    public int saveProteinferInput(IdPickerInput input) {
        int inputId = pinferInputDao.saveProteinferInput(input);
        input.setId(inputId);
        save(sqlMapNameSpace+".saveIdPickerInput", input);
        return inputId;
    }
    
    public void updateIdPickerInputSummary(IdPickerInput input) {
        update(sqlMapNameSpace+".updateIdPickerInput", input);
    }
    
    public void saveIdPickerInputList(List<IdPickerInput> inputList) {
        if(inputList.size() == 0)
            return;
        StringBuilder buf = new StringBuilder();
        for(IdPickerInput input: inputList) {
            buf.append(",("+input.getId()+",");
            if(input.getNumTargetHits() == -1)
                buf.append("NULL,");
            else
                buf.append(input.getNumTargetHits()+",");
            if(input.getNumDecoyHits() == -1)
                buf.append("NULL,");
            else
                buf.append(input.getNumDecoyHits()+",");
            if(input.getNumFilteredTargetHits() == -1)
                buf.append("NULL)");
            else
               buf.append(input.getNumFilteredTargetHits()+",");
            if(input.getNumFilteredDecoyHits() == -1)
                buf.append("NULL)");
            else
               buf.append(input.getNumFilteredDecoyHits()+")");
        }
        buf.deleteCharAt(0);
//        System.out.println(buf.toString());
        super.save(sqlMapNameSpace+".saveIdPickerInputList", buf.toString());
    }

    @Override
    public void deleteProteinferInput(int pinferId) {
        pinferInputDao.deleteProteinferInput(pinferId);
    }

    @Override
    public List<Integer> loadInputIdsForProteinferRun(int pinferId) {
        return pinferInputDao.loadInputIdsForProteinferRun(pinferId);
    }
}
