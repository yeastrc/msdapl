/**
 * MsJobUpdater.java
 * @author Vagisha Sharma
 * Sep 24, 2010
 */
package org.yeastrc.jqs.queue.ws;

import org.yeastrc.jobqueue.JobResetter;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;

/**
 * 
 */
public class MsJobUpdater {

	private MsJobUpdater() {}
	
	private static MsJobUpdater instance = null;
	
	public static final int ACCESS_DENIED = -1;
	public static final int COMPLETE_OR_RUNNING = -2;
	public static final int UPDATE_ERROR = -3;
	
	public static synchronized MsJobUpdater getInstance() {
		if(instance == null)
			instance = new MsJobUpdater();
		return instance;
	}
	
	public int retry(MSJob msJob, User user, Messenger messenger) {
		return retry(msJob, user, messenger, true);
	}
	
	public int retry(MSJob msJob, User user, Messenger messenger, boolean checkAccess) {
		
		if(checkAccess) {
			// Does the user have permissions to re-queue this job? The user has to either be the job submitter
			// or an administrator
			Groups groups = Groups.getInstance();
			boolean access = false;
			if (groups.isMember(user.getResearcher().getID(), "administrators"))
				access = true;
			else if (user.getResearcher().getID() == msJob.getSubmitter()) 
				access = true;
			if(!access) {
				messenger.addError("User does not have permissions to resubmit job with ID: "+msJob.getId());
				return ACCESS_DENIED;
			}
		}
		
		// Can we re-queue the job?
		if(msJob.getStatus() == JobUtils.STATUS_COMPLETE) {
			messenger.addError("Job with ID "+msJob.getId()+" is complete. It could not be restarted.");
			return COMPLETE_OR_RUNNING;
		}
		else if(msJob.getStatus() == JobUtils.STATUS_OUT_FOR_WORK) {
			messenger.addError("Job with ID "+msJob.getId()+" is already running. It could not be restarted.");
			return COMPLETE_OR_RUNNING;
		}
		
		// set status of the job to "Queued"
		try {
			JobResetter.getInstance().resetJob(msJob);
			return msJob.getId();
		} catch (Exception e) {
			messenger.addError("Job with ID "+msJob.getId()+" could not be resubmitted. The error message was: "+e.getMessage());
			return UPDATE_ERROR;
		}
	}
}
