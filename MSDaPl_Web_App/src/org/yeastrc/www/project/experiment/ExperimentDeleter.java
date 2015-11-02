/**
 * ExperimentDeleter.java
 * @author Vagisha Sharma
 * Dec 16, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.jobqueue.JobDeleter;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.www.proteinfer.job.ProteinInferJobSearcher;
import org.yeastrc.www.proteinfer.job.ProteinferJob;

/**
 * 
 */
public class ExperimentDeleter {

    private List<Integer> deletionList;
    private volatile boolean running = false;
    
    private static ExperimentDeleter instance;
    
    private static final Logger log = Logger.getLogger(ExperimentDeleter.class.getName());
    
    private ExperimentDeleter() {
        deletionList = new ArrayList<Integer>();
    }
    
    public synchronized static ExperimentDeleter getInstance() {
        if(instance == null)
            instance = new ExperimentDeleter();
        return instance;
    }
    
    public synchronized void addExperimentId(int experimentId, int projectId, boolean unlinkFirst) throws SQLException {
        if(!deletionList.contains(experimentId)) {
            deletionList.add(experimentId);
            log.info("Got request to delete experiment ID: "+experimentId+" for project ID: "+projectId);
        }
        else {
            log.info("Experiment ID: "+experimentId+" already in deletion queue");
            return;
        }
        
        if(unlinkFirst) {
            log.info("Unlinking project from experiment ID: "+experimentId);
            // unlink the project and experiment
            ProjectExperimentDAO.instance().deleteProjectExperiment(experimentId);
        }
        
        if(!running && deletionList.size() > 0) {
            deleteExperiments();
        }
    }
    
    private void deleteExperiments() {
        
        Thread deleter = new Thread(new Deleter());
        deleter.start();
    }
    
    private class Deleter implements Runnable {
        
        @Override
        public void run() {
            running = true;
            while(deletionList.size() > 0) {
                Integer experimentId = deletionList.get(0);
                try {
                    deleteExperiment(experimentId);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                deletionList.remove(experimentId);
            }
            running = false;
        }
        
        private void deleteExperiment(int experimentId) throws InterruptedException {
            
            log.info("Deleting experiment ID: "+experimentId);
            
            // delete all protein inferences for this experiment
//            List<Integer> piRunIds = ProteinInferJobSearcher.instance().getProteinferIdsForMsExperiment(experimentId);
//            for(int piRunId: piRunIds)
//                deleteProteinInference(piRunId);
            
            // delete all analyses for this experiment
            
            // delete all searches for this experiment
            
            log.info("Deleted experiment ID: "+experimentId);
        }
        
        private void deleteProteinInference(int piRunId) {
           
            log.info("\tDeleting protein inference ID: "+piRunId);
            
            ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
            ProteinferRun run = runDao.loadProteinferRun(piRunId);
            if(ProteinInferenceProgram.isIdPicker(run.getProgram())) {
                // If this is a ProteinInference run delete the corresponding job
                ProteinferJob job = ProteinInferJobSearcher.getInstance().getJobForPiRunId(piRunId);
                if(job != null) {
                    try {
                        JobDeleter.getInstance().deleteJob(job);
                    }
                    catch (Exception e) {
                        log.error("\tError deleting job for protein inference ID: "+piRunId, e);
                    }
                }
            }
            try {
                runDao.delete(piRunId);
                log.info("\tDeleted protein inference ID: "+piRunId);
            }
            catch(Exception e) {
                log.error("\tError deleting protein inference ID: "+piRunId, e);
            }
        }
    }
}
