package org.yeastrc.ms.dao.analysis.percolator.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorParamsDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorParamBean;

public class PercolatorParamsDAOImplTest extends BaseDAOTestCase {

    private static final PercolatorParamsDAO paramsDao = DAOFactory.instance().getPercoltorParamsDAO();
    
    protected void setUp() throws Exception {
        super.setUp();
        BaseDAOTestCase.resetDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testLoadParams() {
        PercolatorParamBean param = new PercolatorParamBean();
        param.setParamName("param1");
        param.setParamValue("value1");
        paramsDao.saveParam(param, 123);
        
        param = new PercolatorParamBean();
        param.setParamName("param2");
        param.setParamValue("value2");
        paramsDao.saveParam(param, 123);
        
        param = new PercolatorParamBean();
        param.setParamName("param3");
        param.setParamValue("value3");
        paramsDao.saveParam(param, 123);
        
        List<PercolatorParam> params = paramsDao.loadParams(123);
        assertNotNull(params);
        assertEquals(3, params.size());
        
        // parameters should be returned in the order in which they were inserted
        assertEquals("param1", params.get(0).getParamName());
        assertEquals("value1", params.get(0).getParamValue());
        assertEquals("param2", params.get(1).getParamName());
        assertEquals("value2", params.get(1).getParamValue());
        assertEquals("param3", params.get(2).getParamName());
        assertEquals("value3", params.get(2).getParamValue());
        
    }

    public final void testSaveParam() {
        PercolatorParamBean bean = new PercolatorParamBean();
        bean.setParamName("some param");
        bean.setParamValue("some value");
        try {
            paramsDao.saveParam(bean, 123);
        }
        catch(Exception e) {
            fail("Error saving valid param");
        }
    }

}
