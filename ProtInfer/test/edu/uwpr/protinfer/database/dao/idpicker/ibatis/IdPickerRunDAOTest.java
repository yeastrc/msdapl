package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import org.yeastrc.ms.domain.search.Program;

import junit.framework.TestCase;
import edu.uwpr.protinfer.ProteinInferenceProgram;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferDAOTestSuite;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;

public class IdPickerRunDAOTest extends TestCase {

    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.testInstance();
    private static final IdPickerRunDAO runDao = factory.getIdPickerRunDao(); 
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testSaveIdPickerRunSummary() {
        ProteinferDAOTestSuite.resetDatabase();
        
        IdPickerRun run = new IdPickerRun();
        run.setComments("some comments");
//        run.setStatus(ProteinferStatus.RUNNING);
        run.setProgram(ProteinInferenceProgram.IDPICKER);
        run.setInputGenerator(Program.PROLUCID);
        run.setNumUnfilteredProteins(2005);
        run.setNumUnfilteredPeptides(3110);
        
        int id = runDao.save(run);
        assertEquals(1, id);
        run.setId(id);
        runDao.saveIdPickerRunSummary(run);
        
        run = runDao.loadProteinferRun(1);
        assertNotNull(run);
        assertEquals(2005, run.getNumUnfilteredProteins());
        assertEquals(3110, run.getNumUnfilteredPeptides());
        assertEquals("some comments", run.getComments());
//        assertEquals(ProteinferStatus.RUNNING, run.getStatus());
        assertEquals(ProteinInferenceProgram.IDPICKER, run.getProgram());
    }
}
