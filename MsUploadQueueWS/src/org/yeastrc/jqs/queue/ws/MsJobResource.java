package org.yeastrc.jqs.queue.ws;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.project.Project;
import org.yeastrc.www.user.User;


@Path("msjob")
@Produces("text/plain")
public class MsJobResource {

	@Context
    SecurityContext security;
	
	@GET
	@Path("{id}")
	public String getJobAsText(@PathParam("id") int jobId) {
	
		MsJob job = MsJobSearcher.getInstance().search(jobId);
		return job.toString();
	}
	
	@GET
	@Path("{id}")
	@Produces({"application/xml", "application/json"})
	public MsJob getJobAsXmlOrJson(@PathParam("id") int jobId) {
	
		return MsJobSearcher.getInstance().search(jobId);
	}
	
	@GET
	@Path("project/{id}")
	@Produces({"application/xml", "application/json"})
	public MsProject getProject(@PathParam("id") int projectId) {
		
		return ProjectSearcher.getInstance().search(projectId);
	}
	
	@GET
	@Path("checkAccess")
	public String checkProjectAccess(@QueryParam("projectId") int projectId, @QueryParam("userEmail") String userEmail) {
		
		Project project = ProjectSearcher.getInstance().getMsDaPlProject(projectId);
	
		User user = UserSearcher.getInstance().getUserWithEmail(userEmail);
		
		boolean access = project.checkAccess(user.getResearcher());
		if(access)
			return "Access allowed";
		else
			return "Access denied";
	}
	
	@GET
	@Path("checkFasta")
	public String checkFasta(@QueryParam("name") String name) {
		
		if(name == null || name.trim().length() == 0)
			throw new BadRequestException("Fasta file name not found in the request");
		
		try {
			int fastaDbId = NrSeqLookupUtil.getDatabaseId(name);
			if(fastaDbId == 0) {
				return "Not found";
			}
			else {
				return "Found";
			}
		}
		catch(Exception e) {
			throw new ServerErrorException("Fasta file lookup threw an error.  The error message was: "+e.getMessage());
		}
	}
	
	@GET
	@Path("status/{id}")
	@Produces("text/plain")
	public String getStatus(@PathParam("id") int jobId) {
	
		MsJob job = MsJobSearcher.getInstance().search(jobId);
		return job.getStatus();
	}
	
	
	@POST
	@Path("add")
	@Produces ({"text/plain"})
	@Consumes ({"application/xml", "application/json"})
	public String add(MsJob job) {
		
		int jobId = submitJob(job);
		return "Queued job. ID: "+jobId+"\n";
	}

	@POST
	@Path("hermie/add")
	@Produces ({"text/plain"})
	@Consumes ({"application/json"})
	/*
	 * This method is only to be used by the hermie pipeline.
	 * cURL example:
	 * curl -u hermie:<hermie_password> -X POST  -H 'Content-Type: application/json' -d '{"projectId":"24", "dataDirectory":"/test/data", "targetSpecies":"6239", "instrument":"LTQ", "comments":"upload test", "submitterName":"vsharma"}' http://localhost:8080/msdapl_queue/services/msjob/hermie/add
	 */
	public String addHermieJob(MsJob job) {
		job.setPipeline("MACCOSS");
		job.setDate(new Date());
		
		// If a submitterName is not part of the request, the submitter will be set to "hermie". 
		// We will not do project access check in this case.
		int jobId;
		if(job.getSubmitterName() == null && job.getUserEmail() == null)
			jobId = submitJob(job, false, false);
		else
			jobId = submitJob(job, true, true);
		
		return "Queued job. ID: "+jobId+"\n";	
	}
	
	@POST
	@Path("labkey/add")
	@Produces ({"text/plain"})
	@Consumes ({"application/json"})
	/*
	 * This method is only to be used by the labkey pipeline.
	 * cURL example:
	 * curl -u labkey:<labkey_password> -X POST  -H 'Content-Type: application/json' -d '{"projectId":"24", "dataDirectory":"/test/data", "targetSpecies":"6239", "instrument":"LTQ", "comments":"upload test", "submitterName":"vsharma"}' http://localhost:8080/msdapl_queue/services/msjob/hermie/add
	 */
	public String addLabkeyJob(MsJob job) {
		job.setPipeline("MACCOSS");
		job.setDate(new Date());
		
		// If a submitterName is not part of the request, the submitter will be set to "labkey". 
		// We will not do project access check in this case.
		int jobId;
		if(job.getSubmitterName() == null && job.getUserEmail() == null)
			jobId = submitJob(job, false, false);
		else
			jobId = submitJob(job, true, true);
		
		return "Queued job. ID: "+jobId+"\n";	
	}
	
	@POST
	@Path("add")
	@Produces ({"text/plain"})
	public String add(
			@QueryParam("projectId") Integer projectId,
			@QueryParam("dataDirectory") String dataDirectory,
			@QueryParam("pipeline") String pipeline,
			@QueryParam("date") Date date,
			@QueryParam("instrument") String instrument,
			@QueryParam("targetSpecies") Integer taxId,
			@QueryParam("comments") String comments,
			@QueryParam("remoteServer") String remoteServer
			) {
		
		MsJob job = new MsJob();
		job.setProjectId(projectId);
		job.setDataDirectory(dataDirectory);
		job.setRemoteServer(remoteServer);
		job.setPipeline(pipeline);
		job.setDate(date);
		job.setInstrument(instrument);
		job.setTargetSpecies(taxId);
		job.setComments(comments);
		System.out.println(job);
		
		Messenger messenger = new Messenger();
		int jobId = submitJob(job);
		if(jobId <= 0)
			return messenger.getMessagesString();
		else
			return "Queued job. ID: "+jobId+"\n";
	}

	private int submitJob(MsJob job) {
		return submitJob(job, true, false);
	}
	
	private int submitJob(MsJob job, boolean checkAccess, boolean sendEmailOnSuccess) {
		
		User user = null;
		
		// NotFoundException will be thrown if a user is not found
		if(job.getUserEmail() != null) {
			user = UserSearcher.getInstance().getUserWithEmail(job.getUserEmail());
		}
		else if(job.getSubmitterName() != null) {
			user = UserSearcher.getInstance().getUser(job.getSubmitterName());
		}
		else {
			user = UserSearcher.getInstance().getUser(security.getUserPrincipal().getName());
		}
		
		// Make sure the project exists.  NotFoundException will be throws if the project does
		// not exist;
		Project project = ProjectSearcher.getInstance().getMsDaPlProject(job.getProjectId());
		
		
		// Make sure the project exists and the reseacher is a member or an administrator
		if(checkAccess) {
		
			if(!project.checkAccess(user.getResearcher())) {
				throw new AccessDeniedException("Access denied. User ("+user.getUsername()+") does not have access to project ID "+job.getProjectId());
			}
		}
		
		Messenger messenger = new Messenger();
		MsJobSubmitter submitter = MsJobSubmitter.getInstance();
		int jobId = submitter.submit(job, user, messenger, checkAccess);
		if(jobId == MsJobSubmitter.BAD_REQUEST) { // data provided by the user was incorrect or incomplete
			throw new BadRequestException(messenger.getMessagesString());
		}
		else if(jobId == MsJobSubmitter.SAVE_ERROR) { // there was an error saving the job to database
			String err = messenger.getMessagesString();
			// 500 error
			throw new ServerErrorException(err);
		}
		
		// all went well, return the database ID of the newly created job.
		// But, first, send an email
		if(sendEmailOnSuccess) {
			Emailer emailer = Emailer.getInstance();
			emailer.emailJobQueued(job, user);
		}
		return jobId;
	}
	

	@DELETE
	@Path("delete/{id}")
	@Produces("text/plain")
	public String delete(@PathParam("id") Integer jobId) {
		
		String username = security.getUserPrincipal().getName();
		// NotFoundException will be thrown if a user is not found
		User user = UserSearcher.getInstance().getUser(username);
		
		// NotFoundException will be throws if the job is not found, or the job is not a MSJob.
		MSJob job = MsJobSearcher.getInstance().getMsDaPlJob(jobId);
		
		Messenger messenger = new Messenger();
		int status = MsJobDeleter.getInstance().delete(job, user, messenger);
		
		if(status == jobId)
			return "Job deleted. ID: "+jobId+"\n";
		
		else {
			if(status == MsJobDeleter.ACCESS_DENIED) // user does not have permissions to delete the job
				throw new AccessDeniedException(messenger.getMessagesString());

			else if(status == MsJobDeleter.COMPLETE_OR_RUNNING) // job is not in a deletion-friendly state
				throw new ConflictException(messenger.getMessagesString());

			else if(status == MsJobDeleter.DELETE_ERROR) // error deleting the job
				throw new ServerErrorException(messenger.getMessagesString());
			
			else 
				throw new ServerErrorException(messenger.getMessagesString());
		}
	}
	
	@POST
	@Path("retry/{id}")
	@Produces("text/plain")
	public String retry(@PathParam("id") Integer jobId) {
		
		String username = security.getUserPrincipal().getName();
		// NotFoundException will be thrown if a user is not found
		User user = UserSearcher.getInstance().getUser(username);
		
		// NotFoundException will be throws if the job is not found, or the job is not a MSJob.
		MSJob job = MsJobSearcher.getInstance().getMsDaPlJob(jobId);
		
		Messenger messenger = new Messenger();
		int status = MsJobUpdater.getInstance().retry(job, user, messenger);
		
		if(status == jobId)
			return "Job restarted. ID: "+jobId+"\n";
		
		else {
			if(status == MsJobUpdater.ACCESS_DENIED) // user does not have permissions to update the job
				throw new AccessDeniedException(messenger.getMessagesString());

			else if(status == MsJobUpdater.COMPLETE_OR_RUNNING) // job is either running or complete
				throw new ConflictException(messenger.getMessagesString());

			else if(status == MsJobUpdater.UPDATE_ERROR) // error re-queuing the job
				throw new ServerErrorException(messenger.getMessagesString());
			
			else 
				throw new ServerErrorException(messenger.getMessagesString());
		}
	}
	
	@POST
	@Path("labkey/retry/{id}")
	@Produces("text/plain")
	public String labkeyRetry(@PathParam("id") Integer jobId) {
		
		String username = security.getUserPrincipal().getName();
		// NotFoundException will be thrown if a user is not found
		User user = UserSearcher.getInstance().getUser(username);
		
		// NotFoundException will be throws if the job is not found, or the job is not a MSJob.
		MSJob job = MsJobSearcher.getInstance().getMsDaPlJob(jobId);
		
		Messenger messenger = new Messenger();
		int status = MsJobUpdater.getInstance().retry(job, user, messenger, false); // do not do user access check
		
		if(status == jobId)
			return "Job restarted. ID: "+jobId+"\n";
		
		else {
			if(status == MsJobUpdater.ACCESS_DENIED) // user does not have permissions to update the job (SHOULD NOT HAPPEN)
				throw new AccessDeniedException(messenger.getMessagesString());

			else if(status == MsJobUpdater.COMPLETE_OR_RUNNING) // job is either running or complete
				throw new ConflictException(messenger.getMessagesString());

			else if(status == MsJobUpdater.UPDATE_ERROR) // error re-queuing the job
				throw new ServerErrorException(messenger.getMessagesString());
			
			else 
				throw new ServerErrorException(messenger.getMessagesString());
		}
	}
}