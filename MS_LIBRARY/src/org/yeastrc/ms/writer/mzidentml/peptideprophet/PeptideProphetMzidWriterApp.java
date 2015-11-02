/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml.peptideprophet;

import org.yeastrc.ms.writer.mzidentml.MzIdentMlWriterException;
import org.yeastrc.ms.writer.mzidentml.MzidWriter;

/**
 * PercolatorMzidWriterApp.java
 * @author Vagisha Sharma
 * Aug 16, 2011
 * 
 */
public class PeptideProphetMzidWriterApp {

	public static void main(String[] args) throws MzIdentMlWriterException {
		
		// int experimentId = Integer.parseInt(args[0]);
		int experimentId = 113;
		// int searchAnalysisId = 141;
		
		PeptideProphetMzidDataProvider dataProvider = new PeptideProphetMzidDataProvider();
		dataProvider.setExperimentId(experimentId);
		// dataProvider.setSearchAnalysisId(searchAnalysisId);
		
		MzidWriter writer = new MzidWriter();
		writer.setOutputFilePath("/Users/vagisha/WORK/MSDaPl_data/two-ms2/sequest/expt113_prophet.mzid");
		writer.setDataProvider(dataProvider);
		
		writer.start();
		writer.end();
		
		// To validate against schema:
		// xmllint --noout --schema /Users/vagisha/Desktop/mzIdentML/svn/schema/mzIdentML1.1.0.xsd expt113_prophet.mzid
	}
}
