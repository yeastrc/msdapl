/**
 * MsJobSearcher.java
 * @author Vagisha Sharma
 * Sep 10, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.jobqueue.Job;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.www.user.User;

import com.sun.jersey.api.NotFoundException;

/**
 * 
 */
public class MsJobSearcher {

	private MsJobSearcher() {}
	
	private static MsJobSearcher instance = null;
	
	private static final Logger log = Logger.getLogger(MsJobSearcher.class);
	
	public static synchronized MsJobSearcher getInstance() {
		if(instance == null)
			instance = new MsJobSearcher();
		
		return instance;
	}
	
	public MSJob getMsDaPlJob(int jobId) {
		
		try {
			Job job = MSJobFactory.getInstance().getJob(jobId);
			
			if(job instanceof MSJob) {
				return (MSJob)job;
			}
			else {
				log.error("Job with ID: "+jobId+" is not a MSJob.");
				throw new NotFoundException("Job with ID: "+jobId+" was not found in the database");
			}
			
			
		} catch (SQLException e1) {
			log.error("Error searching for job ID "+jobId, e1);
			throw new ServerErrorException("Error searching for job ID "+jobId+"\n"+e1.getMessage());
			
		} catch (InvalidIDException e1) {
			log.error("Job with ID: "+jobId+" was not found in the database");
			throw new NotFoundException("Job with ID: "+jobId+" was not found in the database");
		}
	}

	public MsJob search(int jobId) {
		
		MSJob msJob = getMsDaPlJob(jobId);
		
		MsJob myJob = new MsJob();
		myJob.setId(msJob.getId());
		myJob.setProjectId(msJob.getProjectID());
		myJob.setPipeline(msJob.getPipeline().name());
		String dataDir = msJob.getServerDirectory();
		int idx = dataDir.indexOf(":");
		String server = null;
		if(idx != -1)
			server = dataDir.substring(0, idx);
		else
			server = dataDir;
		String dir = dataDir.substring(idx+1, dataDir.length());
		if(!server.equals("local"))
			myJob.setRemoteServer(server);
		myJob.setDataDirectory(dir);
		
		myJob.setSubmitterId(msJob.getSubmitter());
		
		// This will throw a NotFoundException if a user with the given ID was not found.
		User submitter = UserSearcher.getInstance().getUser(msJob.getSubmitter());
		myJob.setSubmitterName(submitter.getUsername());
		
		myJob.setDate(msJob.getRunDate());
		myJob.setTargetSpecies(msJob.getTargetSpecies());
		myJob.setComments(msJob.getComments());
		try {
			String instrument = InstrumentLookup.getInstance().nameForId(msJob.getInstrumentId());
			myJob.setInstrument(instrument);
		} catch (SQLException e) {
			log.error("Instrument lookup failed for instrument ID: "+msJob.getInstrumentId(), e);
			myJob.setInstrument("Instrument lookup failed");
		}
		
		myJob.setStatus(msJob.getStatusDescription());
		myJob.setLog(msJob.getLog());
		return myJob;
	}
}
