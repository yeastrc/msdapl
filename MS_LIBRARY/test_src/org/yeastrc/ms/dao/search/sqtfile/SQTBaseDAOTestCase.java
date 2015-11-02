package org.yeastrc.ms.dao.search.sqtfile;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsRunSearchDAOImplTest.MsRunSearchTest;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAOImplTest.SequestSearchTest;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.sequest.SequestSearchIn;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchIn;

public class SQTBaseDAOTestCase extends BaseDAOTestCase {

    protected SQTHeaderDAO sqtHeaderDao = DAOFactory.instance().getSqtHeaderDAO();
    protected SequestSearchResultDAO sequestResDao = DAOFactory.instance().getSequestResultDAO();
    protected SQTRunSearchDAO sqtRunSearchDao = DAOFactory.instance().getSqtRunSearchDAO();
    protected SequestSearchDAO sequestSearchDao = DAOFactory.instance().getSequestSearchDAO();
    protected SQTSearchScanDAO sqtSpectrumDao = DAOFactory.instance().getSqtSpectrumDAO();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected SequestSearchIn makeSequestSearch(boolean addSeqDb, boolean addStaticMods, boolean addDynaMods) {
        SequestSearchTest search = new SequestSearchTest();
        search.setSearchProgram(Program.SEQUEST);
        search.setAnalysisProgramVersion("1.0");
        search.setSearchDate(new Date(getTime("01/29/2008, 03:34 AM", true)));
        
        if (addSeqDb) {
            MsSearchDatabaseIn db1 = makeSequenceDatabase("serverAddress", "path1");
            MsSearchDatabaseIn db2 = makeSequenceDatabase("serverAddress", "path2");
            search.setSearchDatabases(Arrays.asList(new MsSearchDatabaseIn[]{db1, db2}));
        }

        if (addStaticMods) {
            MsResidueModificationIn mod1 = makeStaticResidueMod('C', "50.0");
            MsResidueModificationIn mod2 = makeStaticResidueMod('S', "80.0");
            search.setStaticResidueMods(Arrays.asList(new MsResidueModificationIn[]{mod1, mod2}));
            
            MsTerminalModificationIn tmod1 = makeStaticTerminalMod(Terminal.NTERM, "95.0");
            search.setStaticTerminalMods(Arrays.asList(new MsTerminalModificationIn[]{tmod1}));
        }

        if (addDynaMods) {
            MsResidueModificationIn dmod1 = makeDynamicResidueMod('A', "10.0", '*');
            MsResidueModificationIn dmod2 = makeDynamicResidueMod('B', "20.0", '#');
            MsResidueModificationIn dmod3 = makeDynamicResidueMod('C', "30.0", '@');
            search.setDynamicResidueMods(Arrays.asList(new MsResidueModificationIn[]{dmod1, dmod2, dmod3}));
            
            MsTerminalModificationIn tmod1 = makeDynamicTerminalMod(Terminal.CTERM, "79.9876", '^');
            MsTerminalModificationIn tmod2 = makeDynamicTerminalMod(Terminal.NTERM, "0.76543", '$');
            search.setDynamicTerminalMods(Arrays.asList(new MsTerminalModificationIn[]{tmod1, tmod2}));
        }
        
        List<Param> params = new ArrayList<Param>(10);
        for (int i = 0; i < 10; i++) {
            final int idx = i;
            Param param = new Param(){
                public String getParamName() {return "param_"+idx;}
                public String getParamValue() {return "value_"+idx;}
                };
           params.add(param);
        }
        search.setSequestParams(params);
        return search;
    }
    
    protected SQTRunSearchIn makeSQTRunSearch(boolean addHeaders) {

        SQTRunSearchTest runSearch = new SQTRunSearchTest();
        runSearch.setFileFormat(SearchFileFormat.SQT_SEQ);
        long startTime = getTime("01/29/2008, 03:34 AM", false);
        long endTime = getTime("01/29/2008, 06:21 AM", false);
        runSearch.setSearchDate(new Date(getTime("01/29/2008, 03:34 AM", true)));
        runSearch.setSearchDuration(searchTimeMinutes(startTime, endTime));

        if (addHeaders) {
            runSearch.addHeader(makeHeader("header_1", "value_1"));
            runSearch.addHeader(makeHeader("header_2", "value_2"));
        }
        return runSearch;
    }


    protected SQTHeaderItem makeHeader(final String name, final String value) {
        SQTHeaderItem h = new SQTHeaderItem() {
            public String getName() {
                return name;
            }
            public String getValue() {
                return value;
            }};
            return h;
    }
    
    public static final class SQTRunSearchTest extends MsRunSearchTest implements SQTRunSearchIn {

        private List<SQTHeaderItem> headers = new ArrayList<SQTHeaderItem>();

        public List<SQTHeaderItem> getHeaders() {
            return headers ;
        }

        public void setHeaders(List<SQTHeaderItem> headers) {
            this.headers = headers;
        }

        public void addHeader(SQTHeaderItem header) {
            headers.add(header);
        }
    }

}
