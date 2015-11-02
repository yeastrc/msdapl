/**
 * MzMLWriter.java
 * @author Vagisha Sharma
 * Mar 14, 2011
 */
package org.yeastrc.ms.writer.mzml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.yeastrc.ms.domain.run.Peak;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.writer.mzml.jaxb.BinaryDataArrayListType;
import org.yeastrc.ms.writer.mzml.jaxb.BinaryDataArrayType;
import org.yeastrc.ms.writer.mzml.jaxb.CVListType;
import org.yeastrc.ms.writer.mzml.jaxb.CVParamType;
import org.yeastrc.ms.writer.mzml.jaxb.CVType;
import org.yeastrc.ms.writer.mzml.jaxb.DataProcessingListType;
import org.yeastrc.ms.writer.mzml.jaxb.DataProcessingType;
import org.yeastrc.ms.writer.mzml.jaxb.FileDescriptionType;
import org.yeastrc.ms.writer.mzml.jaxb.InstrumentConfigurationListType;
import org.yeastrc.ms.writer.mzml.jaxb.InstrumentConfigurationType;
import org.yeastrc.ms.writer.mzml.jaxb.MzMLType;
import org.yeastrc.ms.writer.mzml.jaxb.ParamGroupType;
import org.yeastrc.ms.writer.mzml.jaxb.PrecursorListType;
import org.yeastrc.ms.writer.mzml.jaxb.PrecursorType;
import org.yeastrc.ms.writer.mzml.jaxb.ProcessingMethodType;
import org.yeastrc.ms.writer.mzml.jaxb.ScanListType;
import org.yeastrc.ms.writer.mzml.jaxb.ScanType;
import org.yeastrc.ms.writer.mzml.jaxb.SelectedIonListType;
import org.yeastrc.ms.writer.mzml.jaxb.SoftwareListType;
import org.yeastrc.ms.writer.mzml.jaxb.SoftwareType;
import org.yeastrc.ms.writer.mzml.jaxb.SpectrumType;

/**
 * 
 */
public class MzMLWriter {

	private String filename;
	private String outputFile;
	
	private CVType MS_cv; 
	private CVType UO_cv;
	private SoftwareType software;
	private String dataProcessingRef = "MSDaPl_MzMLWriter_conversion";
	private int scanIndex;
	
	private BufferedWriter writer = null;
	private Marshaller marshaller = null;
	
	private static final String ENCODING = "UTF-8";
	private CVParamType MS_cv_msnspectrum;
	private CVParamType MS_cv_centroidspec;
	
	public MzMLWriter() {
		
		MS_cv = new CVType();
		MS_cv.setId("MS");
		MS_cv.setFullName("Proteomics Standards Initiative Mass Spectrometry Ontology");
		MS_cv.setVersion("2.33.1");
		MS_cv.setURI("http://psidev.cvs.sourceforge.net/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo");
		
		UO_cv = new CVType();
		UO_cv.setId("UO");
		UO_cv.setFullName("Unit Ontology");
		UO_cv.setVersion("11:02:2010");
		UO_cv.setURI("http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/phenotype/unit.obo");
		
		
		MS_cv_msnspectrum = new CVParamType();
		MS_cv_msnspectrum.setCvRef(MS_cv);
		MS_cv_msnspectrum.setAccession("MS:1000580");
		MS_cv_msnspectrum.setName("MSn spectrum");
		MS_cv_msnspectrum.setValue("");
		
		MS_cv_centroidspec = new CVParamType();
		MS_cv_centroidspec.setCvRef(MS_cv);
		MS_cv_centroidspec.setAccession("MS:1000127");
		MS_cv_centroidspec.setName("centroid spectrum");
		MS_cv_centroidspec.setValue("");
		
		software = new SoftwareType();
		software.setId("MSDaPl_MzMLWriter");
		software.setVersion("1.0");
		
		scanIndex = 0;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	
	public void start() throws MzMLWriterException {
		
		
		try {
			writer = new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			throw new MzMLWriterException("Error opening output file", e);
		}
		
		try {
			writer.write("<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>");
			writer.newLine();
			writer.write("<mzML xmlns=\"http://psi.hupo.org/ms/mzml\" ");
			writer.write("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
			writer.write("xsi:schemaLocation=\"http://psi.hupo.org/ms/mzml http://psidev.info/files/ms/mzML/xsd/mzML1.1.0.xsd\" ");
			writer.write("id=\""+this.filename+"\" ");
			writer.write("version=\"1.1.0\">");
			
			writer.newLine();
		} catch (IOException e) {
			throw new MzMLWriterException("Error writing to file", e);
		}
		
		
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(MzMLType.class);
			
			marshaller = jc.createMarshaller();
	        marshaller.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
	        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	        marshaller.setProperty( Marshaller.JAXB_ENCODING, ENCODING);
	        
	        CVListType cvl = getCVlist();
	        marshaller.marshal(cvl, writer);
	        writer.newLine();
	        
	        FileDescriptionType fdesc = getFileDescription();
	        marshaller.marshal(fdesc, writer);
	        writer.newLine();
	        
	        SoftwareListType swlist = getSoftwareList();
	        marshaller.marshal(swlist, writer);
	        writer.newLine();
	        
	        InstrumentConfigurationListType instrList = getInstrumentconfigurationList();
	        marshaller.marshal(instrList, writer);
	        writer.newLine();
	        
	        DataProcessingListType dataProcList = getDataProcessingList();
	        marshaller.marshal(dataProcList, writer);
	        writer.newLine();
	        
		} catch (JAXBException e) {
			throw new MzMLWriterException("Error marshalling data", e);
		} 
		catch (IOException e) {
			throw new MzMLWriterException("Error writing data", e);
		}
		
	}
	
	public void end() throws MzMLWriterException {
		
		try {
			writer.write("</mzML>");
			writer.newLine();
		} catch (IOException e) {
			throw new MzMLWriterException("Error writing to file", e);
		}
		
		if(writer != null) try {writer.close();} catch(IOException e){}
	}
	

	private CVListType getCVlist() {
		
		CVListType cvl = new CVListType();
		cvl.setCount(BigInteger.valueOf(2));

		cvl.getCv().add(MS_cv);
		cvl.getCv().add(UO_cv);

		return cvl;
	}
	
	private FileDescriptionType getFileDescription() {
		
		FileDescriptionType fdesc = new FileDescriptionType();
		ParamGroupType pgtype = new ParamGroupType();
		
		pgtype.getCvParam().add(MS_cv_msnspectrum);
		
		pgtype.getCvParam().add(MS_cv_centroidspec);
		
		// TODO what does this one mean?
		CVParamType cv = new CVParamType();
		cv.setCvRef(MS_cv);
		cv.setAccession("MS:1000776");
		cv.setName("scan number only nativeID format");
		cv.setValue("");
		pgtype.getCvParam().add(cv);
		
		fdesc.setFileContent(pgtype);
		return fdesc;
	}
	
	private SoftwareListType getSoftwareList() {
		
		SoftwareListType swl = new SoftwareListType();
		swl.setCount(BigInteger.ONE);
		
		swl.getSoftware().add(software);
		
		return swl;
	}
	
	private InstrumentConfigurationListType getInstrumentconfigurationList() {
		
		InstrumentConfigurationListType instrList = new InstrumentConfigurationListType();
		instrList.setCount(BigInteger.ONE);
		
		InstrumentConfigurationType instrument = new InstrumentConfigurationType();
		instrument.setId("IC");
		CVParamType cv = new CVParamType();
		cv.setCvRef(MS_cv);
		cv.setAccession("MS:1000031");
		cv.setName("instrument model");
		cv.setValue("");
		instrument.getCvParam().add(cv);
		instrList.getInstrumentConfiguration().add(instrument);
		
		return instrList;
	}
	
	private DataProcessingListType getDataProcessingList() {
		
		DataProcessingListType dplist = new DataProcessingListType();
		dplist.setCount(BigInteger.ONE);
		
		DataProcessingType dp = new DataProcessingType();
		dp.setId(dataProcessingRef);
		dplist.getDataProcessing().add(dp);
		
		ProcessingMethodType pmt = new ProcessingMethodType();
		pmt.setOrder(BigInteger.ZERO);
		pmt.setSoftwareRef(software);
		dp.getProcessingMethod().add(pmt);
		
		CVParamType cv = new CVParamType();
		cv.setCvRef(MS_cv);
		cv.setAccession("MS:1000544");
		cv.setName("Conversion to mzML");
		cv.setValue("");
		pmt.getCvParam().add(cv);
		
		
		return dplist;
	}
	
	public void startRun(int spectrumCount) throws MzMLWriterException {
		
		try {
			writer.write("<run id=\"_"+filename+"\" defaultInstrumentConfigurationRef=\"IC\">");
			writer.newLine();
			writer.write("<spectrumList count=\""+spectrumCount+"\" defaultDataProcessingRef=\""+dataProcessingRef+"\">");
			writer.newLine();
		} catch (IOException e) {
			throw new MzMLWriterException("Error writing to file", e);
		}
	}
	
	public void endRun() throws MzMLWriterException {
		try {
			writer.write("</spectrumList>");
			writer.newLine();
			writer.write("</run>");
			writer.newLine();
		} catch (IOException e) {
			throw new MzMLWriterException("Error writing to file", e);
		}
	}
	
	public void writeScan(MS2ScanIn scan) throws MzMLWriterException {
		
		SpectrumType spectrum = new SpectrumType();
		spectrum.setIndex(BigInteger.valueOf(scanIndex));
		spectrum.setDefaultArrayLength(scan.getPeakCount());
		
		scanIndex++;
		spectrum.setId("scan="+scan.getStartScanNum());
		
		double lowmz = Double.MAX_VALUE, highmz = 0, basepeak_mz = 0;
		float basepeak_int = 0, tic = 0;
		
		double[] mzarray = new double[scan.getPeakCount()];
		float[] intensityarray = new float[scan.getPeakCount()];
		int idx = 0;
		for(Peak peak: scan.getPeaks()) {
			
			double mz = peak.getMz();
			float intensity = peak.getIntensity();
			mzarray[idx] = mz;
			intensityarray[idx] = intensity;
			idx++;
			
			if(mz < lowmz)
				lowmz = mz;
			if(mz > highmz)
				highmz = mz;
			
			tic += intensity;
			if(intensity > basepeak_int) {
				basepeak_int = intensity;
				basepeak_mz = mz;
			}
		}
		
		spectrum.getCvParam().add(MS_cv_msnspectrum);
		spectrum.getCvParam().add(MS_cv_centroidspec);
		
		// ms level
		CVParamType cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000511");
		cvp.setName("ms level");
		cvp.setValue("2");
		spectrum.getCvParam().add(cvp);
		
		// lowest observed m/z
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000528");
		cvp.setName("lowest observed m/z");
		cvp.setValue(""+lowmz);
		spectrum.getCvParam().add(cvp);
		
		// highest observed m/z
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000527");
		cvp.setName("highest observed m/z");
		cvp.setValue(""+highmz);
		spectrum.getCvParam().add(cvp);
		
		// total ion current
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000285");
		cvp.setName("total ion current");
		cvp.setValue(""+tic);
		spectrum.getCvParam().add(cvp);
		
		// base peak m/z
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000504");
		cvp.setName("base peak m/z");
		cvp.setValue(""+basepeak_mz);
		spectrum.getCvParam().add(cvp);
		
		// base peak intensity
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000505");
		cvp.setName("base peak intensity");
		cvp.setValue(""+basepeak_int);
		spectrum.getCvParam().add(cvp);
		
		// retention time
		ScanListType scanlist = new ScanListType();
		scanlist.setCount(BigInteger.ONE);
		ScanType stype = new ScanType();
		scanlist.getScan().add(stype);
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000016");
		cvp.setName("scan start time");
		cvp.setValue(""+scan.getRetentionTime());
		cvp.setUnitCvRef(UO_cv);
		cvp.setUnitAccession("UO:0000010");
		cvp.setUnitName("second");
		stype.getCvParam().add(cvp);
		spectrum.setScanList(scanlist);
		
		// precursor information
		PrecursorListType precursorList = new PrecursorListType();
		precursorList.setCount(BigInteger.ONE);
		PrecursorType precursor = new PrecursorType();
		SelectedIonListType selectedIonList = new SelectedIonListType();
		selectedIonList.setCount(BigInteger.ONE);
		ParamGroupType paramGrp = new ParamGroupType();
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000744");
		cvp.setName("selected ion m/z");
		cvp.setValue(""+scan.getPrecursorMz());
		cvp.setUnitCvRef(MS_cv);
		cvp.setUnitAccession("MS:1000040");
		cvp.setUnitName("m/z");
		paramGrp.getCvParam().add(cvp);
		for(MS2ScanCharge scanCharge: scan.getScanChargeList()) {
			cvp = new CVParamType();
			cvp.setCvRef(MS_cv);
			cvp.setAccession("MS:1000633");
			cvp.setName("possible charge state");
			cvp.setValue(""+scanCharge.getCharge());
			paramGrp.getCvParam().add(cvp);
		}
		selectedIonList.getSelectedIon().add(paramGrp);
		precursor.setSelectedIonList(selectedIonList);
		
		// activation
		// TODO: don't have this information
		precursor.setActivation(new ParamGroupType());
		
		precursorList.getPrecursor().add(precursor);
		spectrum.setPrecursorList(precursorList);
		
		// write the peaks: m/z and intensity values
		BinaryDataArrayListType dataArrayList = new BinaryDataArrayListType();
		spectrum.setBinaryDataArrayList(dataArrayList);
		dataArrayList.setCount(BigInteger.valueOf(2));
		
		// peak m/z
		BinaryDataArrayType mzArray = new BinaryDataArrayType();
		dataArrayList.getBinaryDataArray().add(mzArray);
       
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000523");
		cvp.setName("64-bit float");
		cvp.setValue("");
		mzArray.getCvParam().add(cvp);
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000574");
		cvp.setName("zlib compression");
		cvp.setValue("");
		mzArray.getCvParam().add(cvp);
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000514");
		cvp.setName("m/z array");
		cvp.setValue("");
		cvp.setUnitCvRef(MS_cv);
		cvp.setUnitAccession("MS:1000040");
		cvp.setUnitName("m/z");
		mzArray.getCvParam().add(cvp);
		
		try {
			byte[] mzbytearr = PeakUtils.toByteArr(mzarray);
			byte[] bytes = PeakUtils.compress(mzbytearr);
			ByteBuffer buf = ByteBuffer.allocate(bytes.length);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.put(bytes);
			byte[] littleend = buf.array();
			mzArray.setBinary(new String(littleend));
			mzArray.setEncodedLength(BigInteger.valueOf(littleend.length));
			
		} catch (IOException e) {
			throw new MzMLWriterException("Error writing m/z array", e);
		}
		
		// peak intesities
		BinaryDataArrayType intArray = new BinaryDataArrayType();
		dataArrayList.getBinaryDataArray().add(intArray);
		
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000523");
		cvp.setName("32-bit float");
		cvp.setValue("");
		intArray.getCvParam().add(cvp);
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000574");
		cvp.setName("zlib compression");
		cvp.setValue("");
		intArray.getCvParam().add(cvp);
		cvp = new CVParamType();
		cvp.setCvRef(MS_cv);
		cvp.setAccession("MS:1000515");
		cvp.setName("intensity array");
		cvp.setValue("");
		cvp.setUnitCvRef(MS_cv);
		cvp.setUnitAccession("MS:1000131");
		cvp.setUnitName("number of counts");
		intArray.getCvParam().add(cvp);
		
		try {
			byte[] intbytearr = PeakUtils.toByteArr(intensityarray);
			byte[] bytes = PeakUtils.compress(intbytearr);
			ByteBuffer buf = ByteBuffer.allocate(bytes.length);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.put(bytes);
			byte[] littleend = buf.array();
			intArray.setBinary(new String(littleend));
			intArray.setEncodedLength(BigInteger.valueOf(littleend.length));
			
		} catch (IOException e) {
			throw new MzMLWriterException("Error writing intensity array", e);
		}
		
		
		try {
			marshaller.marshal(spectrum, writer);
			writer.newLine();
			
		} catch (JAXBException e) {
			throw new MzMLWriterException("Error marshalling spectrum data", e);
		} catch (IOException e) {
			throw new MzMLWriterException("Error writing to file", e);
		}
		
	}
	
	public static void main(String[] args) throws MzMLWriterException, DataProviderException, IOException {
		
		
		String file = "./resources/mzml/755GA_diluted_R1.small.ms2";
		Ms2FileReader reader = new Ms2FileReader();
		
		// open the file
		reader.open(file);
		reader.getRunHeader();
		
		MS2ScanIn scan = null;
		
        
		// read the spectrum count
        int spectrumCount = 0;
		while((scan = reader.getNextScan()) != null) {
			spectrumCount++;
		}
		// close the file
		reader.close();
		
		// open the file again, this time to write the scans
		reader = new Ms2FileReader();
		reader.open(file);
		reader.getRunHeader();
		
		MzMLWriter writer = new MzMLWriter();
        writer.setFilename("755GA_diluted_R1.small");
        writer.setOutputFile("./resources/mzml/myoutput.mzML");
        writer.start();
        writer.startRun(spectrumCount);
        
        // write the scans
        while((scan = reader.getNextScan()) != null) {
        	
        	writer.writeScan(scan);
		}
        
        writer.endRun();
        writer.end();
        
        // close the file
        reader.close();
		
        
//        double[] mz = new double [] {18.099047, 18.213415, 18.35326, 18.583954, 200.9, 700.8, 1234.567, 400.5, 500.6, 600.7, 700.8};
//        byte[] mzbytearr = PeakUtils.toByteArr(mz);
//        
//        
//        byte[] compressed = PeakUtils.compress(mzbytearr);
//        ByteBuffer buf = ByteBuffer.allocate(compressed.length);
//        buf.order(ByteOrder.LITTLE_ENDIAN);
//        buf.put(compressed);
//        buf.order(ByteOrder.LITTLE_ENDIAN);
//        System.out.println(new String(compressed));
//        System.out.println(new String(buf.array()));
//        double[] dcomp = null;
//		try {
//			dcomp = PeakUtils.decompress(Base64.decodeBase64(compressed));
//		} catch (DataFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        for(double d: dcomp)
//        	System.out.println(d);
	}

	
	
}
