package edu.uwpr.protinfer.database.dao.ibatis;

import java.sql.Date;

import org.yeastrc.ms.domain.search.Program;

import junit.framework.TestCase;
import edu.uwpr.protinfer.ProteinInferenceProgram;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferDAOTestSuite;
import edu.uwpr.protinfer.database.dto.ProteinferRun;

public class ProteinferRunDAOTest extends TestCase {

    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.testInstance();
    private static final ProteinferRunDAO runDao = factory.getProteinferRunDao();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testSaveNewProteinferRun() {
        ProteinferDAOTestSuite.resetDatabase();
        
        ProteinferRun run1 = new ProteinferRun();
        run1.setProgram(ProteinInferenceProgram.IDPICKER);
        run1.setInputGenerator(Program.SEQUEST);
        run1.setComments("some comments for sequest input");
        int id1 = runDao.save(run1);
        assertEquals(1, id1);
        
        ProteinferRun run2 = new ProteinferRun();
        run2.setProgram(ProteinInferenceProgram.PROTINFER_PERC);
        run2.setInputGenerator(Program.PERCOLATOR);
        int id2 = runDao.save(run2);
        assertEquals(2, id2);
        
        
        ProteinferRun run1_db = runDao.loadProteinferRun(id1);
        assertEquals(1, run1_db.getId());
        assertEquals(ProteinInferenceProgram.IDPICKER, run1_db.getProgram());
        assertEquals(Program.SEQUEST, run1_db.getInputGenerator());
        assertEquals(0, run1_db.getInputList().size());
        assertEquals("some comments for sequest input", run1_db.getComments());
        assertNull(run1_db.getDate());
        
        ProteinferRun run2_db = runDao.loadProteinferRun(id2);
        assertEquals(2, run2_db.getId());
        assertEquals(ProteinInferenceProgram.PROTINFER_PERC, run2_db.getProgram());
        assertEquals(Program.PERCOLATOR, run2_db.getInputGenerator());
        assertEquals(0, run2_db.getInputList().size());
        assertNull(run2_db.getComments());
        assertNull(run2_db.getDate());
        
    }

    
    public final void testUpdateProteinferRun() {
        ProteinferRun run = runDao.loadProteinferRun(2);
        assertNull(run.getDate());
        assertNull(run.getComments());
        run.setDate(new Date(System.currentTimeMillis()));
        run.setComments("Adding comments");
        
        runDao.update(run);
        ProteinferRun run_db = runDao.loadProteinferRun(2);
        assertNotNull(run_db.getDate());
        assertEquals("Adding comments", run_db.getComments());
    }
}
