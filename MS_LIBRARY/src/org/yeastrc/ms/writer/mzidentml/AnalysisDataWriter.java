/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.ms.writer.mzidentml.jaxb.ProteinAmbiguityGroupType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationResultType;

/**
 * AnalysisDataWriter.java
 * @author Vagisha Sharma
 * Aug 3, 2011
 * 
 */
public class AnalysisDataWriter {

	private BufferedWriter writer;
	private Marshaller marshaller;
	
	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}
	
	public void start() throws IOException {
		
		writer.write("<AnalysisData>");
		writer.newLine();
	}
	
	public void startSpectrumIdentificationList(String id) throws IOException {
		
		if(StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("id given for SpectrumIdentificationList cannot be blank");
		}
		
		writer.write("<SpectrumIdentificationList id=\""+id+"\">");
		writer.newLine();
	}
	
	public void addSpectrumIdentificationResult(SpectrumIdentificationResultType spectrumResult) throws JAXBException, IOException {
		
		marshaller.marshal(spectrumResult, writer);
		writer.newLine();
	}
	
	public void endSpectrumIdentificationList() throws IOException {
		
		writer.write("</SpectrumIdentificationList>");
		writer.newLine();
	}

	public void end() throws IOException {
		
		writer.write("</AnalysisData>");
		writer.newLine();
	}
	
	public void startProteinDetectionList(String id) throws IOException {
		if(StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("id given for ProteinDetectionList cannot be blank");
		}
		
		writer.write("<ProteinDetectionList id=\""+id+"\">");
		writer.newLine();
	}
	
	public void addProteinAmbiguityGroup(ProteinAmbiguityGroupType proteinGroup) throws JAXBException, IOException {
		
		marshaller.marshal(proteinGroup, writer);
		writer.newLine();
	}

	public void endProteinDetectionList() throws IOException {
		
		writer.write("</ProteinDetectionList>");
		writer.newLine();
	}
}
