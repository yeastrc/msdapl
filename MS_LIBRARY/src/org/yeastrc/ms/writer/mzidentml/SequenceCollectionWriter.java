/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.yeastrc.ms.writer.mzidentml.jaxb.DBSequenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideEvidenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideType;

/**
 * SequenceCollectionWriter.java
 * @author Vagisha Sharma
 * Aug 2, 2011
 * 
 */
public class SequenceCollectionWriter {

	private BufferedWriter writer;
	private Marshaller marshaller;
	
	
	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void startCollection() throws IOException {
		
		writer.write("<SequenceCollection>");
		writer.newLine();
	}
	
	public void addSequence(DBSequenceType sequence) throws JAXBException, IOException {
		
		marshaller.marshal(sequence, writer);
		writer.newLine();
	}
	
	public void addPeptide(PeptideType peptide) throws JAXBException, IOException {
		
		marshaller.marshal(peptide, writer);
		writer.newLine();
	}
	
	public void addPeptideEvidence(PeptideEvidenceType peptideEv) throws JAXBException, IOException {
		
		marshaller.marshal(peptideEv, writer);
		writer.newLine();
	}
	
	public void endCollection() throws IOException {
		
		writer.write("</SequenceCollection>");
		writer.newLine();
	}
}
