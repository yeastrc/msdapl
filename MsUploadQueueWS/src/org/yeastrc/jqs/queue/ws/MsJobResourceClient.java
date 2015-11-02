/**
 * MsJobResourceClient.java
 * @author Vagisha Sharma
 * Sep 9, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.util.Date;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;



/**
 * 
 */
public class MsJobResourceClient {

	private static MsJobResourceClient instance = new MsJobResourceClient();
	
	//private final ClientConfig config;
	
	private MsJobResourceClient() {
			
//		config = new DefaultClientConfig();
//		try {
//			config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties());
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
			   
	}
	
	private String BASE_URI = "http://localhost:8080/msdapl_queue/services/msjob";
	
	public static MsJobResourceClient getInstance() {
		return instance;
	}
	
	/*
	 * Equivalent curl commands -- 
	 * TEXT OUTPUT: curl -i -X GET -H "Accept:text/plain" "http://localhost:8080/msdapl_queue/services/msjob/<jobId>"
	 * XML OUTPUT : curl -i -X GET -H "Accept:application/xml" "http://localhost:8080/msdapl_queue/services/msjob/<jobId>"
	 * JSON OUTPUT: curl -i -X GET -H "Accept:text/json" "http://localhost:8080/msdapl_queue/services/msjob/<jobId>"
	 */
	public void getJob(Integer jobId) {
		
		Client client = Client.create();
		
		WebResource webRes = client.resource(BASE_URI);
		ClientResponse response = webRes.path(String.valueOf(jobId)).accept("application/json").get(ClientResponse.class);
		Status status = response.getClientResponseStatus();
		if(status == Status.OK) {
			MsJob job = response.getEntity(MsJob.class);
			System.out.println(job);
		}
		else {
			System.err.println(response.getEntity(String.class));
		}
		
	}
	
	/*
	 * Equivalent curl command -- 
	 * curl -i -X GET -H "Accept:text/plain" "http://localhost:8080/msdapl_queue/services/msjob/status/<jobId>
	 */
	public void getJobStatus(Integer jobId) {
		
		Client client = Client.create();
		WebResource webRes = client.resource(BASE_URI);
		ClientResponse response = webRes.path("status/"+String.valueOf(jobId)).accept("text/plain").get(ClientResponse.class);
		Status status = response.getClientResponseStatus();
		if(status == Status.OK) {
			String jobStatus = response.getEntity(String.class);
			System.out.println(jobStatus);
		}
		else {
			System.err.println(response.getEntity(String.class));
		}
	}
	
	/*
	 * Equivalent curl commands -- 
	 * JSON INPUT: curl -u <username> -i -X POST -H "Accept:text/plain" -H 'Content-Type: application/json' -d '{"projectId":"24", "dataDirectory":"/test/data", "pipeline":"MACCOSS", "date":"2010-03-29"}' "http://localhost:8080/msdapl_queue/services/msjob/add"
	 */
	public int addJob(MsJob job, String username, String password) {
		
		Client client = Client.create();
		
		HTTPBasicAuthFilter authFilter = new HTTPBasicAuthFilter(username, password);
		client.addFilter(authFilter);
		
		WebResource webRes = client.resource(BASE_URI);
		// String data = "{\"projectId\":\"24\", \"dataDirectory\":\"test\", \"pipeline\":\"MACCOSS\", \"date\":\"2010-03-29\"}";
		
		ClientResponse response = webRes.path("add").type("application/xml").accept("text/plain").post(ClientResponse.class, job);
		Status status = response.getClientResponseStatus();
		if(status == Status.OK) {
			String jobId = response.getEntity(String.class);
			System.out.println(jobId);
			int idx = jobId.lastIndexOf("ID: ");
			return Integer.parseInt(jobId.substring(idx+4).trim());
		}
		else {
			System.err.println(status.getStatusCode()+": "+status.getReasonPhrase());
			System.err.println(response.getEntity(String.class));
			return 0;
		}
		
	}
	
	/*
	 * Equivalent curl commands -- 
	 * curl -u <username> -i -X POST -H "Accept:text/plain" "http://localhost:8080/msdapl_queue/services/msjob/add?projectId=24&dataDirectory=/data/test&pipeline=MACCOSS&date=09/24/10&instrument=LTQ&Id=9606&comments=some%20comments"
	 */
	public int addJobQueryParam(MsJob job, String username, String password) {

		Client client = Client.create();
		
		HTTPBasicAuthFilter authFilter = new HTTPBasicAuthFilter(username, password);
		client.addFilter(authFilter);
		
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("projectId", String.valueOf(job.getProjectId()));
		queryParams.add("dataDirectory", job.getDataDirectory());
		queryParams.add("pipeline", job.getPipeline());
		queryParams.add("date", "09/23/2010");
		
		//HttpHeaders.AUTHORIZATION
		
		WebResource webRes = client.resource(BASE_URI);
		ClientResponse response = webRes.path("add").queryParams(queryParams)
			// .accept("text/plain").
//			header(HttpHeaders.AUTHORIZATION, "Basic " 
//			        + new String(Base64.encode("user:password"), 
//			        Charset.forName("ASCII"))).
			.post(ClientResponse.class);
		Status status = response.getClientResponseStatus();
		if(status == Status.OK) {
			String jobId = response.getEntity(String.class);
			System.out.println(jobId);
			return Integer.parseInt(jobId);
		}
		else {
			System.err.println(status.getStatusCode()+": "+status.getReasonPhrase());
			System.err.println(response.getEntity(String.class));
			return 0;
		}

	}
	
	/*
	 * Equivalent curl command -- 
	 * curl -u <username> -i -X DELETE "http://localhost:8080/msdapl_queue/services/msjob/delete/<jobId>"
	 */
	public void delete(int jobId, String username, String password) {

		Client client = Client.create();
		
		HTTPBasicAuthFilter authFilter = new HTTPBasicAuthFilter(username, password);
		client.addFilter(authFilter);
		
		WebResource webRes = client.resource(BASE_URI);
		ClientResponse response = webRes.path("delete/"+String.valueOf(jobId)).delete(ClientResponse.class);
		Status status = response.getClientResponseStatus();
		if(status == Status.OK) {
			String resp = response.getEntity(String.class);
			System.out.println(resp);
		}
		else {
			System.err.println(status.getStatusCode()+": "+status.getReasonPhrase());
			System.err.println(response.getEntity(String.class));
		}

	}
	
	public static void main(String[] args) {
		
		MsJobResourceClient client = MsJobResourceClient.getInstance();
		
		String username = "test";
		String password = "user";
		
		MsJob job = new MsJob();
		job.setProjectId(24);
		job.setDataDirectory("/test/dir");
		job.setPipeline("TPP");
		job.setDate(new Date());
		int jobId = client.addJob(job, username, password);
		
		if(jobId > 0) {
			client.getJob(jobId);

			client.getJobStatus(jobId);

			client.delete(jobId, username, password);
		}
	}

}
