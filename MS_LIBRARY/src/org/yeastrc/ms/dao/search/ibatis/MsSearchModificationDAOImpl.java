/**
 * MsSearchModDAOImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsResultTerminalModIds;
import org.yeastrc.ms.domain.search.MsResultResidueModIds;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.impl.ResultTerminalModIds;
import org.yeastrc.ms.domain.search.impl.ResultResidueModIds;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 */
public class MsSearchModificationDAOImpl extends BaseSqlMapDAO implements MsSearchModificationDAO {

    public MsSearchModificationDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public List<MsResidueModification> loadStaticResidueModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectStaticResidueModsForSearch", searchId);
    }

    public void saveStaticResidueMod(MsResidueModification mod) {
        save("MsSearchMod.insertStaticResidueMod", mod);
    }

    public void deleteStaticResidueModsForSearch(int searchId) {
        delete("MsSearchMod.deleteStaticResidueModsForSearch", searchId);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public List<MsResidueModification> loadDynamicResidueModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectDynamicResidueModsForSearch", searchId);
    }

    public int saveDynamicResidueMod(MsResidueModification mod) {
        return saveAndReturnId("MsSearchMod.insertDynamicResidueMod", mod);
    }

    public void deleteDynamicResidueModsForSearch(int searchId) {
        delete("MsSearchMod.deleteDynamicResidueModsForSearch", searchId);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public List<MsTerminalModification> loadStaticTerminalModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectStaticTerminalModsForSearch", searchId);
    }

    public void saveStaticTerminalMod(MsTerminalModification mod) {
        save("MsSearchMod.insertStaticTerminalMod", mod);
    }

    public void deleteStaticTerminalModsForSearch(int searchId) {
        delete("MsSearchMod.deleteStaticTerminalModsForSearch", searchId);
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public  List<MsTerminalModification> loadDynamicTerminalModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectDynamicTerminalModsForSearch", searchId);
    }

    public  int saveDynamicTerminalMod(MsTerminalModification mod) {
        return saveAndReturnId("MsSearchMod.insertDynamicTerminalMod", mod);
    }

    public  void deleteDynamicTerminalModsForSearch(int searchId) {
        delete("MsSearchMod.deleteDynamicTerminalModsForSearch", searchId);
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC RESIDUE) associated with a search result
    //-------------------------------------------------------------------------------------------
    public List<MsResultResidueMod> loadDynamicResidueModsForResult(
            int resultId) {
        return queryForList("MsSearchMod.selectDynamicResidueModsForResult", resultId);
    }

    @Override
    public int loadMatchingDynamicResidueModId(MsResidueModification mod) {
        Integer modId = (Integer)queryForObject("MsSearchMod.selectMatchingDynaResModId", mod);
        if (modId == null)
            return 0;
        return modId;
    }
    
    public void saveDynamicResidueModForResult(int resultId,
            int modificationId, int modifiedPosition) {
        ResultResidueModIds modDb = new ResultResidueModIds(resultId, modificationId, modifiedPosition);
        this.saveDynamicResidueModForResult(modDb);
    }
    
    public void saveDynamicResidueModForResult(MsResultResidueModIds modIdentifier) {
        save("MsSearchMod.insertResultDynamicResidueMod", modIdentifier);
    }
    
    public void saveAllDynamicResidueModsForResult(List<MsResultResidueModIds> modList) {
        if (modList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MsResultResidueModIds mod: modList) {
            values.append(",(");
            values.append(mod.getResultId() == 0 ? "NULL" : mod.getResultId());
            values.append(",");
            values.append(mod.getModificationId() == 0 ? "NULL" : mod.getModificationId());
            values.append(",");
            values.append(mod.getModifiedPosition());
            values.append(")");
        }
        values.deleteCharAt(0);
        save("MsSearchMod.insertAllResultDynamicResidueMods", values.toString());
    }

    public void deleteDynamicResidueModsForResult(int resultId) {
        delete("MsSearchMod.deleteDynamicResidueModsForResult", resultId);
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC TERMINAL) associated with a search result
    //-------------------------------------------------------------------------------------------
    public List<MsResultTerminalMod> loadDynamicTerminalModsForResult(
            int resultId) {
        return queryForList("MsSearchMod.selectDynamicTerminalModsForResult", resultId);
    }

    @Override
    public int loadMatchingDynamicTerminalModId(
            MsTerminalModification mod) {
        Integer modId = (Integer)queryForObject("MsSearchMod.selectMatchingDynaTermModId", mod);
        if (modId == null)
            return 0;
        return modId;
    }
    
    public void saveDynamicTerminalModForResult(int resultId, int modificationId) {
        ResultTerminalModIds modDb = new ResultTerminalModIds(resultId, modificationId);
        this.saveDynamicTerminalModForResult(modDb);
    }
    
    public void saveDynamicTerminalModForResult(MsResultTerminalModIds modIdentifier) {
        save("MsSearchMod.insertResultDynamicTerminalMod", modIdentifier);
    }
    
    public void saveAllDynamicTerminalModsForResult(List<MsResultTerminalModIds> modList) {
        if (modList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MsResultTerminalModIds mod: modList) {
            values.append(",(");
            values.append(mod.getResultId() == 0 ? "NULL" : mod.getResultId());
            values.append(",");
            values.append(mod.getModificationId() == 0 ? "NULL" : mod.getModificationId());
            values.append(")");
        }
        values.deleteCharAt(0);
        save("MsSearchMod.insertAllResultDynamicTerminalMods", values.toString());
    }

    public void deleteDynamicTerminalModsForResult(int resultId) {
        delete("MsSearchMod.deleteDynamicTerminalModsForResult", resultId);
    }

    /**
     * Type handler for converting between Java's Character and SQL's CHAR type.
     */
    public static final class CharTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToChar(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            Character status = (Character) parameter;
            if (status == null || status.charValue() == 0)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(status.toString());
        }

        public Object valueOf(String s) {
            return stringToChar(s);
        }
        
        private Character stringToChar(String charStr) {
            // if charStr is NULL the value (\u0000) will be used for modificationSymbol
            if (charStr == null)
                return Character.valueOf('\u0000');
            if (charStr.length() != 1)
                throw new IllegalArgumentException("Cannot convert \""+charStr+"\" to Character");
            return Character.valueOf(charStr.charAt(0));
        }
    }
    
    /**
     * Type handler for converting between MsTerminalModification.Terminal and SQL's CHAR type.
     */
    public static final class TerminalTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToTerminal(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            Terminal terminal = (Terminal) parameter;
            if (terminal == null)
                throw new IllegalArgumentException("Terminal value for terminal modification cannot be null");
//                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(String.valueOf(terminal.toChar()));
        }

        public Object valueOf(String s) {
            return stringToTerminal(s);
        }
        
        private Terminal stringToTerminal(String termStr) {
            if (termStr == null)
                throw new IllegalArgumentException("String representing MsTerminalModification.Terminal cannot be null");
            if (termStr.length() != 1)
                throw new IllegalArgumentException("Cannot convert "+termStr+" to Terminal");
            Terminal term = Terminal.instance(Character.valueOf(termStr.charAt(0)));
            if (term == null)
                throw new IllegalArgumentException("Invalid Terminal value: "+termStr);
            return term;
        }
    }

}
