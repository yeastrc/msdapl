/**
 * ProteinInferenceRerunner.java
 * @author Vagisha Sharma
 * Apr 8, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.job;

import org.apache.log4j.Logger;
import org.yeastrc.jobqueue.JobDeleter;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;


/**
 * 
 */
public class ProteinInferenceRerunner {

	private static final Logger log = Logger.getLogger("protinferRerunLog");
	
	private ProteinInferenceRerunner () {}
	
	/**
	 * Returns the id for the new job
	 * @param pinferId
	 * @param deleteOriginal
	 * @return
	 * @throws Exception
	 */
	public static int reRun(int pinferId, boolean deleteOriginal) throws Exception {
		
		ProteinferJob job = ProteinInferJobSearcher.getInstance().getJobForPiRunId(pinferId);
		if(job == null) {
			log.error("Could not find protein inference job for piRunID: "+pinferId);
			return -1;
		}
		
		IdPickerRun run = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
		if(run == null) {
			log.error("Could not find protein inference run for piRunID: "+pinferId);
			return -1;
		}
		
		run.setId(0);
		run.setProgramVersion(ProteinInferenceProgram.protInferVersion); // update to current version
		
		log.info("Re-running piRunID: "+pinferId+"; Delete original: "+deleteOriginal);
		int jobId = ProteinferJobSaver.instance().submitIdPickerJob(job.getSubmitter(), run);
		log.info("QUEUED JOB: "+jobId);
		
		if(deleteOriginal) {
			
			log.info("DELETE OLD JOB: "+job.getId());
			
	        boolean deleted = JobDeleter.getInstance().deleteJob(job);
	        
	        // If the entry in jobQueue was not deleted stop here.
	        if(deleted) {
	        	
	        	// now delete the protein inference run from the mass spec database.
		        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
		        try {
		        	log.info("DELETE OLD PI RUN: "+pinferId);
		            fact.getProteinferRunDao().delete(pinferId);
		        }
		        catch(Exception e) {
		        	log.error("IdPicker run could not be deleted; piRunID: "+pinferId, e);
		        }
	        }
	        else {
	        	log.error("Protein inference job could not be deleted; jobID: "+job.getId());
	        }
	        
		}
		return jobId;
	}
}
