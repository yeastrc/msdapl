/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml.percolator;

import org.yeastrc.ms.writer.mzidentml.MzIdentMlWriterException;
import org.yeastrc.ms.writer.mzidentml.MzidWriter;

/**
 * PercolatorMzidWriterApp.java
 * @author Vagisha Sharma
 * Aug 16, 2011
 * 
 */
public class PercolatorMzidWriterApp {

	public static void main(String[] args) throws MzIdentMlWriterException {
		
		// int experimentId = Integer.parseInt(args[0]);
		int experimentId = 105;
		int searchAnalysisId = 112;
		
		PercolatorMzidDataProvider dataProvider = new PercolatorMzidDataProvider();
		dataProvider.setExperimentId(experimentId);
		dataProvider.setSearchAnalysisId(searchAnalysisId);
		
		MzidWriter writer = new MzidWriter();
		writer.setOutputFilePath("/Users/vagisha/WORK/MSDaPl_data/two-ms2/sequest/expt105_perc.mzid");
		writer.setDataProvider(dataProvider);
		
		writer.start();
		writer.end();
		
		// To validate against schema:
		// xmllint --noout --schema /Users/vagisha/Desktop/mzIdentML/svn/schema/mzIdentML1.1.0.xsd expt105.mzid
	}
}
