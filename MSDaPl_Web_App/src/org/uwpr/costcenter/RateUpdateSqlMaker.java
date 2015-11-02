/**
 * RateUpdateSqlMaker.java
 * @author Vagisha Sharma
 * Nov 16, 2011
 */
package org.uwpr.costcenter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 */
public class RateUpdateSqlMaker {

	
	// instrumentID for FT: 6
	private static final int FT_ID = 6;
	
	// instrumentID for OT1: 4
	private static final int OT1_ID = 4;
	
	// instrumentID for OT1: 5
	private static final int OT2_ID = 5;
	
	// instrumentID for LTQ: 2
	// private static final int LTQ_ID = 2; RETIRED INSTRUMENT
	
	// instrumentID for ETD: 3
	private static final int ETD_ID = 3;
	
	// instrumentID for TSQ_Access: 7
	private static final int TSQA_ID = 7;
	
	// instrumentID for TSQ_Vantage: 10
	private static final int TSQV_ID = 10;
	
	// instrumentID for Q Exactive: 11
	private static final int Q_ID = 11;
	
	
	
	// Update the instrumentRate table with a tab-delimited file of new rates.
	// 
	// Example:
	// ,FT,OT1/OT2,LTQ,ETD,TSQA,TSQV
	// hrs,total,total,total,total,total,total
	// 1,40,40,30,34,14,24
	// 2,80,80,60,68,28,48
	// 3,120,120,90,102,42,72
	// 4,140,140,100,120,50,80
	// 5,180,180,130,154,64,104
	// 6,220,220,160,188,78,120
	// 7,240,240,160,200,90,120
	// 8,240,240,160,200,90,120
	// 
	// Expected command-line args:
	// - Path to input file
	// - Path to output file
	// - rateTypeId
	public void updateRate(String inputFile, String outputFile, int rateTypeId) throws IOException {
		
		System.out.println("Input: "+inputFile);
		System.out.println("Output: "+outputFile);
		
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

		String line = reader.readLine(); // ,FT,OT1/OT2,Q,ETD,TSQA,TSQV
		line = reader.readLine(); // hrs,total,total,total,total,total,total


		while((line = reader.readLine()) != null) {

			String[] tokens = line.split(",");

			int i = 0;

			int hours = Integer.parseInt(tokens[i++]); // this should be the same as the id in the timeBlock table

			String ft_rate = tokens[i++];

			String ot1_rate = tokens[i++];

			String ot2_rate = ot1_rate;

			String q_rate = tokens[i++];

			String etd_rate = tokens[i++];

			String tsqa_rate = tokens[i++];

			String tsqv_rate = tokens[i++];
			
			// write the update statements
			writer.write("UPDATE instrumentRate SET fee = "+ft_rate+" WHERE instrumentID="+FT_ID+" AND blockID="+hours+" AND rateTypeID="+rateTypeId+" AND isCurrent=1;");
			writer.newLine();
			
			writer.write("UPDATE instrumentRate SET fee = "+ot1_rate+" WHERE instrumentID="+OT1_ID+" AND blockID="+hours+" AND rateTypeID="+rateTypeId+" AND isCurrent=1;");
			writer.newLine();
			
			writer.write("UPDATE instrumentRate SET fee = "+ot2_rate+" WHERE instrumentID="+OT2_ID+" AND blockID="+hours+" AND rateTypeID="+rateTypeId+" AND isCurrent=1;");
			writer.newLine();
			
			writer.write("UPDATE instrumentRate SET fee = "+q_rate+" WHERE instrumentID="+Q_ID+" AND blockID="+hours+" AND rateTypeID="+rateTypeId+" AND isCurrent=1;");
			writer.newLine();
			
			writer.write("UPDATE instrumentRate SET fee = "+etd_rate+" WHERE instrumentID="+ETD_ID+" AND blockID="+hours+" AND rateTypeID="+rateTypeId+" AND isCurrent=1;");
			writer.newLine();
			
			writer.write("UPDATE instrumentRate SET fee = "+tsqa_rate+" WHERE instrumentID="+TSQA_ID+" AND blockID="+hours+" AND rateTypeID="+rateTypeId+" AND isCurrent=1;");
			writer.newLine();
			
			writer.write("UPDATE instrumentRate SET fee = "+tsqv_rate+" WHERE instrumentID="+TSQV_ID+" AND blockID="+hours+" AND rateTypeID="+rateTypeId+" AND isCurrent=1;");
			writer.newLine();
			
		}
		
		reader.close();
		writer.close();

	}
	
	public static void main(String[] args) throws IOException {
		
		String curDir = System.getProperty("user.dir");
		System.out.println(curDir);
		
		RateUpdateSqlMaker updater = new RateUpdateSqlMaker();
		
//		int rateTypeId = 4; // UW FFS
//		String inputFile = "schema/uw_internal_ffs_total_rates.csv";
//		String outputFile = "schema/uw_internal_ffs_update.sql";
////		updater.updateRate(inputFile, outputFile, rateTypeId);
//		
//		rateTypeId = 6; // COMMERCIAL_FFS
//		inputFile = "schema/commercial_ffs_total_rates.csv";
//		outputFile = "schema/commercial_ffs_update.sql";
//		updater.updateRate(inputFile, outputFile, rateTypeId);
//		
//		rateTypeId = 5; // NON_PROFIT_FFS
//		inputFile = "schema/nonprofit_ffs_total_rates.csv";
//		outputFile = "schema/nonprofit_ffs_update.sql";
//		updater.updateRate(inputFile, outputFile, rateTypeId);
		
		// 07.31.12
		int rateTypeId;
		String inputFile;
		String outputFile;
		
		rateTypeId = 5; // 5 | NON_PROFIT_FFS
		inputFile = "schema/nonprofit_ffs_rates.07.31.12.csv";
		outputFile = "schema/nonprofit_ffs_rates.07.31.12_updates.sql";
		updater.updateRate(inputFile, outputFile, rateTypeId);
		
		rateTypeId = 6; // 6 | COMMERCIAL_FFS
		inputFile = "schema/commercial_ffs_rates.07.31.12.csv";
		outputFile = "schema/commercial_ffs_rates.07.31.12_updates.sql";
		updater.updateRate(inputFile, outputFile, rateTypeId);
		
	}
}
