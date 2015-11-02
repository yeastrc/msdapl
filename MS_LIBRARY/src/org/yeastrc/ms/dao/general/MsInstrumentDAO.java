package org.yeastrc.ms.dao.general;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.domain.general.MsInstrument;

public interface MsInstrumentDAO {

    public abstract int saveInstrument(MsInstrument instrument);
    
    public abstract void updateInstrument(MsInstrument instrument);

    public abstract void deleteInstrument(int instrumentId);

    public abstract MsInstrument load(int id);

    public abstract List<MsInstrument> loadAllInstruments() throws SQLException;

}