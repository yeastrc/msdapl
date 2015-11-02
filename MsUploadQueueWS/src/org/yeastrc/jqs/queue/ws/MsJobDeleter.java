/**
 * MsJobDeleter.java
 * @author Vagisha Sharma
 * Sep 24, 2010
 */
package org.yeastrc.jqs.queue.ws;

import org.yeastrc.jobqueue.JobDeleter;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;

/**
 * 
 */
public class MsJobDeleter {

	private MsJobDeleter() {}
	
	private static MsJobDeleter instance = null;
	
	public static final int ACCESS_DENIED = -1;
	public static final int COMPLETE_OR_RUNNING = -2;
	public static final int DELETE_ERROR = -3;
	
	public static synchronized MsJobDeleter getInstance() {
		if(instance == null)
			instance = new MsJobDeleter();
		return instance;
	}
	
	public int delete(MSJob msJob, User user, Messenger messenger) {
		
		// Does the user have permissions to delete this job? The user has to either be the job submitter
		// or an administrator
		Groups groups = Groups.getInstance();
		boolean access = false;
		if (groups.isMember(user.getResearcher().getID(), "administrators"))
			access = true;
		else if (user.getResearcher().getID() == msJob.getSubmitter()) 
			access = true;
		if(!access) {
			messenger.addError("User does not have permissions to delete job with ID: "+msJob.getId());
			return ACCESS_DENIED;
		}
		
		
		// Is the job in a delete-friendly state?
		if(msJob.getStatus() == JobUtils.STATUS_COMPLETE) {
			messenger.addError("Job with ID "+msJob.getId()+" is complete. It could not be deleted.");
			return COMPLETE_OR_RUNNING;
		}
		else if(msJob.getStatus() == JobUtils.STATUS_OUT_FOR_WORK) {
			messenger.addError("Job with ID "+msJob.getId()+" is running. It could not be deleted.");
			return COMPLETE_OR_RUNNING;
		}
		
		// delete the job
		try {
			if(JobDeleter.getInstance().deleteJob(msJob)) {
				return msJob.getId();
			}
			else {
				messenger.addError("Job with ID "+msJob.getId()+" is running. It could not be deleted.");
				return COMPLETE_OR_RUNNING;
			}
		} catch (Exception e) {
			messenger.addError("Job with ID "+msJob.getId()+" could not be deleted. The error message was: "+e.getMessage());
			return DELETE_ERROR;
		}
	}
}
