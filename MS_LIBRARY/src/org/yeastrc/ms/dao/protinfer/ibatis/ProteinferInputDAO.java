package org.yeastrc.ms.dao.protinfer.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.GenericProteinferInputDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferInput;
import org.yeastrc.ms.domain.protinfer.ProteinferInput.InputType;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class ProteinferInputDAO extends BaseSqlMapDAO implements GenericProteinferInputDAO<ProteinferInput>{

    private static final String sqlMapNameSpace = "ProteinferInput";
    
    public ProteinferInputDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public List<ProteinferInput> loadProteinferInputList(int pinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinferInputList", pinferId);
    }
    
    public List<Integer> loadInputIdsForProteinferRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectRunSearchIds", pinferId);
    }
    
    public int saveProteinferInput(ProteinferInput input) {
        return saveAndReturnId(sqlMapNameSpace+".saveProteinferInput", input);
    }
    
    public void deleteProteinferInput(int pinferId) {
        super.delete(sqlMapNameSpace+".delete", pinferId);
    }
    
    /**
     * Type handler for converting between ProteinferStatus and SQL's CHAR type.
     */
    public static final class InputTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToInputType(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            InputType inputType = (InputType) parameter;
            if (inputType == null)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(String.valueOf(inputType.getShortName()));
        }

        public Object valueOf(String s) {
            return stringToInputType(s);
        }
        
        private InputType stringToInputType(String inputTypeStr) {
            if (inputTypeStr == null)
                throw new IllegalArgumentException("String representing InputType cannot be null");
            InputType inputType = InputType.getInputTypeForChar(inputTypeStr.charAt(0));
            if (inputType == null)
                throw new IllegalArgumentException("Invalid InputTypevalue: "+inputTypeStr);
            return inputType;
        }
    }
}
