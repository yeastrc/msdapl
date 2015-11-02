package org.yeastrc.jqs.queue.ws;

import java.util.Date;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class Test {

	public static void main(String[] args) throws JAXBException {

		String BASE_URI = "http://localhost:8080/msdapl_queue/services/msjob";
		Client client = Client.create();
		WebResource webRes = client.resource(BASE_URI);
		
		
		// Submit a upload job
		MsJob job = new MsJob();
		job.setSubmitterName("vsharma");
		job.setProjectId(123);
		job.setDataDirectory("/test/dir");
//		ErrorResponse response = webRes.path("add").type("application/xml").accept("application/xml").post(ErrorResponse.class, job);
//		String responseXml = response.getEntity(String.class);
//		JAXBContext ctx = JAXBContext.newInstance(Response.class);
//		Unmarshaller um = ctx.createUnmarshaller();
//		Response resp = (Response)(um.unmarshal(new StreamSource(new StringReader(responseXml))));
//		System.out.println(response.toString());
		
		job.setPipeline("TPP");
		job.setDate(new Date());
//		response = webRes.path("add").type("application/xml").accept("application/xml").post(Response.class, job);
//		System.out.println(response.toString());
		
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		queryParams.add("user", "vsharma");
		queryParams.add("projectId", "12");
		queryParams.add("dir", "/mydata/dir");
		queryParams.add("pipeline", "MACCOSS");
		queryParams.add("date", new Date().toString());
//		response = webRes.path("add2").queryParams(queryParams).accept("application/xml").post(ErrorResponse.class);
//		System.out.println(response.toString());
		
		
		// GET job by ID
		job = webRes.path("45").accept("application/xml").get(MsJob.class);
		System.out.println(job);
		
		
		job = webRes.path("87").accept("application/json").get(MsJob.class);
		System.out.println(job);
		
//		JAXBContext jc = JAXBContext.newInstance(MsJob.class);
//		Unmarshaller um = jc.createUnmarshaller();
//		
//		//um.setSchema(schema);
//		MsJob job = (MsJob)um.unmarshal(new StreamSource(new StringReader(cresp)));
//		System.out.println(job.getId());
//		System.out.println(job.getDataDirectory());
		
	}
}
