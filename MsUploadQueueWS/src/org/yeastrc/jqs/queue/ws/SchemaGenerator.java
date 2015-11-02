/**
 * SchemaGenerator.java
 * @author Vagisha Sharma
 * Sep 10, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * 
 */
public class SchemaGenerator {

	public static void main(String[] args) throws IOException, JAXBException {
		
		// specify where the generated XML schema will be created
		String schemaDir = "/Users/silmaril/WORK/UW/Workspaces/MyEclipse8.5/MSDaPlUploadQueue/";
		final File dir = new File(schemaDir);
		
		// create a JAXBContext for the MsJob class
		JAXBContext ctx = JAXBContext.newInstance(MsJob.class);
		// generate an XML schema from the annotated object model; create it
		// in the dir specified earlier under the default name, schema1.xsd
		ctx.generateSchema(new SchemaOutputResolver() {
			   @Override
			   public Result createOutput(String namespaceUri, String schemaName) throws IOException {
					return new StreamResult(new File(dir, "msjob_schema.xsd"));
			   }
		   });
	}
}
