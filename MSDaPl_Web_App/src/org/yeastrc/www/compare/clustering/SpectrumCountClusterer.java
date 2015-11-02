/**
 * SpectrumCountClusterer.java
 * @author Vagisha Sharma
 * Apr 20, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare.clustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.util.FileUtils;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nr_seq.listing.ProteinListing;
import org.yeastrc.properties.ApplicationProperties;
import org.yeastrc.www.compare.ComparisonProtein;
import org.yeastrc.www.compare.DisplayColumns;
import org.yeastrc.www.compare.ProteinComparisonDataset;
import org.yeastrc.www.compare.ProteinGroupComparisonDataset;
import org.yeastrc.www.compare.clustering.ClusteringConstants.GRADIENT;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetProteinInformation;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;

/**
 * 
 */
public class SpectrumCountClusterer {

	private static SpectrumCountClusterer instance = null;
	
	private static final Logger log = Logger.getLogger(SpectrumCountClusterer.class.getName());
	
	private SpectrumCountClusterer() {}
	
	public static synchronized SpectrumCountClusterer getInstance() {
		if(instance == null)
			instance = new SpectrumCountClusterer();
		return instance;
	}
	
	public ProteinGroupComparisonDataset clusterProteinGroupComparisonDataset(ProteinGroupComparisonDataset grpComparison,
			ROptions rOptions, StringBuilder errorMesage, String dir) {
		
		long s = System.currentTimeMillis(); 
		
		// remove any sorting parameters
		grpComparison.setSortBy(null);
		grpComparison.setSortOrder(null);
		
		// reset the display columns so that all fields get initialized.
		grpComparison.setDisplayColumns(new DisplayColumns());
		
		for(ComparisonProteinGroup grpProtein: grpComparison.getProteinsGroups()) {
            for(ComparisonProtein protein: grpProtein.getProteins()) {
            	grpComparison.initializeProteinInfo(protein);
            }
        }
		long e = System.currentTimeMillis();
		log.info("Time to initialize protein info: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		
		// Create the output directory. If it exists already remove it 
		if(new File(dir).exists()) {
			log.info("Directory exists: "+dir+". DELETING....");
			FileUtils.deleteFile(new File(dir)); 
		}
		if(!(new File(dir).mkdir()))  {
			errorMesage.append("Error creating directory: "+dir);
			return null;
		}
		
		// Write the input file
		if(!writeInputFile(grpComparison, errorMesage, dir)) {
			return null;
		}
		
		// Write scripts and run R
		if(!writeScriptsAndRunR(rOptions, errorMesage, dir)) {
			return null;
		}
		
		log.info("Checking for output files");
		// make sure expected output files are present
		File output = new File(dir+File.separator+ClusteringConstants.INPUT_FILE_MOD); 
		if(!output.exists()) {
			errorMesage.append("Modified input file "+output.getAbsolutePath()+" does not exist");
			return null;
		}
		try {
			readModifiedSpectrumCounts(grpComparison, output);
			if(rOptions.isDoLog())
				grpComparison.setMinHeatMapSpectrumCount((float)rOptions.getValueForMissing());
			else
				grpComparison.setMinHeatMapSpectrumCount(0);
		}
		catch(IOException e1) {
			log.error("Error reading modified input file: "+output.getAbsolutePath(), e1);
			errorMesage.append("Error reading modified input file: "+output.getAbsolutePath());
			return null;
		}
		
		// This file contains the order of rows and columns after clustering
		output = new File(dir+File.separator+ClusteringConstants.OUTPUT_FILE);
		if(!output.exists()) {
			errorMesage.append("Output file "+output.getAbsolutePath()+" does not exist");
			return null;
		}
		
		List<Integer> datasetOrder;
		try {
			datasetOrder = readDatasetOrder(output);
		} catch (IOException e1) {
			log.error("Error reading output file: "+output.getAbsolutePath(), e1);
			errorMesage.append("Error reading output file: "+output.getAbsolutePath());
			return null;
		}
		
		List<Integer> groupOrder;
		try {
			groupOrder = readGroupOrder(output);
		} catch (IOException e1) {
			log.error("Error reading output file: "+output.getAbsolutePath(), e1);
			errorMesage.append("Error reading output file: "+output.getAbsolutePath());
			return null;
		}
		
		// reorder the protein groups
		ProteinGroupComparisonDataset orderedGrpComparison =  reorderComparison(grpComparison, datasetOrder, groupOrder);
		
		
		output = new File(dir+File.separator+ClusteringConstants.IMG_FILE);
		if(!output.exists()) {
			errorMesage.append("Output file "+output.getAbsolutePath()+" does not exist");
			return null;
		}
		
		// read the colors file
		List<String> colors = null;
		String colorsFile = rOptions.getGradientFile();
		output =  new File(dir+File.separator+colorsFile);
		if(!output.exists()) {
			errorMesage.append("Output file "+output.getAbsolutePath()+" does not exist");
			return null;
		}
		try {
			colors = readColors(output);
		}
		catch (IOException ex) {
			log.error("Error reading output file: "+output.getAbsolutePath(), ex);
			errorMesage.append("Error reading output file: "+output.getAbsolutePath());
			return null;
		}
		
		if(colors != null && colors.size() > 0) {
			String[] colArr = new String[colors.size()];
			colArr = colors.toArray(colArr);
			orderedGrpComparison.setSpectrumCountColors(colArr);
		}
		
		return orderedGrpComparison;
	}
	
	private void readModifiedSpectrumCounts(
			ProteinGroupComparisonDataset grpComparison, File output) throws IOException {
		
		List<ComparisonProteinGroup> proteinGrps = grpComparison.getProteinsGroups();
		int index = 0;
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(output));
			String line = null;
			while((line = reader.readLine()) != null) {
				String[] tokens = line.split("\\s+");
				float[] modCounts = new float[tokens.length];
				
				for(int i = 0; i < tokens.length; i++)
					modCounts[i] = Float.parseFloat(tokens[i]);
				
				ComparisonProteinGroup grp = proteinGrps.get(index);
				index++;
				for(ComparisonProtein protein: grp.getProteins()) {
					
					int dsIndex = 0;
					for(Dataset ds: grpComparison.getDatasets()) {
						
						DatasetProteinInformation dpi = protein.getDatasetProteinInformation(ds);
						if(dpi != null) {
							float modCount = modCounts[dsIndex];
							dpi.setHeatMapSpectrumCount(modCount);
						}
						dsIndex++;
					}
				}
			}
		} 
		finally {
			if(reader != null) try {reader.close();} catch(IOException e){}
		}
	}
	
	private void readModifiedSpectrumCounts(
			ProteinComparisonDataset comparison, File output) throws IOException {
		
		int index = 0;
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(output));
			String line = null;
			while((line = reader.readLine()) != null) {
				String[] tokens = line.split("\\s+");
				float[] modCounts = new float[tokens.length];
				
				for(int i = 0; i < tokens.length; i++)
					modCounts[i] = Float.parseFloat(tokens[i]);
				
				ComparisonProtein protein = comparison.getProteins().get(index);
				index++;
				
				int dsIndex = 0;
				for(Dataset ds: comparison.getDatasets()) {

					DatasetProteinInformation dpi = protein.getDatasetProteinInformation(ds);
					if(dpi != null) {
						float modCount = modCounts[dsIndex];
						dpi.setHeatMapSpectrumCount(modCount);
					}
					dsIndex++;
				}
			}
		} 
		finally {
			if(reader != null) try {reader.close();} catch(IOException e){}
		}
	}


	private boolean writeScriptsAndRunR(ROptions rOptions,
			StringBuilder errorMesage, String dir) {
		// write the R script
		String rscriptPath = null;
		log.info("Writing script file");
		try {
			rscriptPath = writeRScript(dir, rOptions);
			
		} catch (IOException e1) {
			log.error("Error writing R script: "+e1.getMessage(), e1);
			errorMesage.append("Error writing R script: "+e1.getMessage());
			return false;
		}
		
		// write the runner script
		String runScriptPath = null;
		log.info("Writing runner script");
		try {
			runScriptPath = writeShScript(dir, rscriptPath);
		} catch (IOException e1) {
			log.error("Error writing runner script: "+e1.getMessage(), e1);
			errorMesage.append("Error writing runner script: "+e1.getMessage());
			return false;
		}
		
		// run  R
		return runR(errorMesage, runScriptPath);
	}
	
	public ProteinComparisonDataset clusterProteinComparisonDataset(
			ProteinComparisonDataset comparison, ROptions rOptions, StringBuilder errorMesage, String dir) {
		
		long s = System.currentTimeMillis(); 
		
		// remove any sorting parameters
		comparison.setSortBy(null);
		comparison.setSortOrder(null);
		
		// reset the display columns so that all fields get initialized.
		comparison.setDisplayColumns(new DisplayColumns());
		
		for(ComparisonProtein protein: comparison.getProteins()) {
			comparison.initializeProteinInfo(protein);
        }
		long e = System.currentTimeMillis();
		log.info("Time to initialize protein info: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		
		// Create the output directory. If it exists already remove it 
		if(new File(dir).exists()) {
			log.info("Directory exists: "+dir+". DELETING....");
			FileUtils.deleteFile(new File(dir)); 
		}
		if(!(new File(dir).mkdir()))  {
			errorMesage.append("Error creating directory: "+dir);
			return null;
		}
		
		// Write the input file
		if(!writeInputFile(comparison, errorMesage, dir)) {
			return null;
		}
		
		// Write scripts and run R
		if(!writeScriptsAndRunR(rOptions, errorMesage, dir)) {
			return null;
		}
		
		log.info("Checking for output files");
		// make sure expected output files are present
		File output = new File(dir+File.separator+ClusteringConstants.INPUT_FILE_MOD); 
		if(!output.exists()) {
			errorMesage.append("Modified input file "+output.getAbsolutePath()+" does not exist");
			return null;
		}
		try {
			readModifiedSpectrumCounts(comparison, output);
			if(rOptions.isDoLog())
				comparison.setMinHeatMapSpectrumCount((float)rOptions.getValueForMissing());
			else
				comparison.setMinHeatMapSpectrumCount(0);
		}
		catch(IOException e1) {
			log.error("Error reading modified input file: "+output.getAbsolutePath(), e1);
			errorMesage.append("Error reading modified input file: "+output.getAbsolutePath());
			return null;
		}
		
		// This file contains the order of rows and columns after clustering
		output = new File(dir+File.separator+ClusteringConstants.OUTPUT_FILE);
		if(!output.exists()) {
			errorMesage.append("Output file "+output.getAbsolutePath()+" does not exist");
			return null;
		}
		
		List<Integer> datasetOrder;
		try {
			datasetOrder = readDatasetOrder(output);
		} catch (IOException e1) {
			log.error("Error reading output file: "+output.getAbsolutePath(), e1);
			errorMesage.append("Error reading output file: "+output.getAbsolutePath());
			return null;
		}
		
		List<Integer> proteinOrder;
		try {
			proteinOrder = readGroupOrder(output);
		} catch (IOException e1) {
			log.error("Error reading output file: "+output.getAbsolutePath(), e1);
			errorMesage.append("Error reading output file: "+output.getAbsolutePath());
			return null;
		}
		
		// reorder the protein groups
		ProteinComparisonDataset orderedComparison =  reorderComparison(comparison, datasetOrder, proteinOrder);
		
		
		output = new File(dir+File.separator+ClusteringConstants.IMG_FILE);
		if(!output.exists()) {
			errorMesage.append("Output file "+output.getAbsolutePath()+" does not exist");
			return null;
		}
		
		// read the colors file
		List<String> colors = null;
		String colorsFile = rOptions.getGradientFile();
		output =  new File(dir+File.separator+colorsFile);
		if(!output.exists()) {
			errorMesage.append("Output file "+output.getAbsolutePath()+" does not exist");
			return null;
		}
		try {
			colors = readColors(output);
		}
		catch (IOException ex) {
			log.error("Error reading output file: "+output.getAbsolutePath(), ex);
			errorMesage.append("Error reading output file: "+output.getAbsolutePath());
			return null;
		}
		
		if(colors != null && colors.size() > 0) {
			String[] colArr = new String[colors.size()];
			colArr = colors.toArray(colArr);
			orderedComparison.setSpectrumCountColors(colArr);
		}
		
		return orderedComparison;
	}
	
	public static List<String> readColors(File output) throws IOException {
		
		List<String> colors = new ArrayList<String>(256);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(output));
			String line = null;
			while((line = reader.readLine()) != null) {
				colors.add(line.trim());
			}
		}
		finally {
			if(reader != null) try {reader.close();} catch(IOException e){}
		}
		return colors;
	}

	private boolean runR(StringBuilder errorMesage, String scriptPath) {
		log.info("Executing script");
		Runtime rt = Runtime.getRuntime();
		Process process = null;
		try {
			//String cmdline = "/net/pr/vol1/ProteomicsResource/bin/R --vanilla --slave --file="+scriptPath;
			String cmdline = "sh "+scriptPath;
			log.info(cmdline);
			process = rt.exec(cmdline);
			// any error message?
            StreamGobbler errorGobbler = new 
                StreamGobbler(process.getErrorStream(), "ERROR");            
            
            // any output?
            StreamGobbler outputGobbler = new 
                StreamGobbler(process.getInputStream(), "OUTPUT");
            

            // kick them off
            outputGobbler.start();
            errorGobbler.start();
            
            
			int exitVal = process.waitFor();
			
			if(exitVal != 0) {
				errorMesage.append("Error running R -- exit value of "+exitVal+ " was returned.");
				return false;
			}
		} catch (IOException e1) {
			errorMesage.append("Error executing script: "+scriptPath+"\n"+e1.getMessage());
			return false;
		} catch (InterruptedException ex) {
			errorMesage.append("Error executing script: "+scriptPath+"\n"+ex.getMessage());
			return false;
		}
		return true;
	}

	private boolean writeInputFile(ProteinGroupComparisonDataset grpComparison,
			StringBuilder errorMesage, String dir) {
		String fileName = dir+File.separator+ClusteringConstants.INPUT_FILE;
		log.info("Writing input file: "+fileName);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			
			// write the IDs of the protein inferences being compared
			writer.write("Name");
			for(Dataset dataset: grpComparison.getDatasets()) {
				String dsString = getDatasetString(dataset);
				writer.write("\t"+dsString);
			}
			writer.write("\n");
			
			// write the results so that we can cluster them
			for(ComparisonProteinGroup grpProtein: grpComparison.getProteinsGroups()) {
				
				// We only need to get the spectrum counts for one protein in this dataset
				ComparisonProtein prot = grpProtein.getProteins().get(0);
				
				String rowname = "";// grpProtein.getGroupId()+"_";
				ProteinListing listing = prot.getProteinListing();
				// Can't use common-names since two or more proteins can have the same common name
				rowname += listing.getFastaAccessions().get(0);
				
				writer.write(rowname);
				
				
				for(Dataset dataset: grpComparison.getDatasets()) {
					DatasetProteinInformation dpi = prot.getDatasetProteinInformation(dataset);
					if(dpi != null)
						writer.write("\t"+dpi.getNormalizedSpectrumCount());
					else
						writer.write("\t0");
				}
				writer.write("\n");
			}
		}
		catch(IOException ex) {
			errorMesage.append("Error creating input file: "+fileName+"\n"+ex.getMessage());
			return false;
		}
		finally {
			if(writer != null) try{writer.close();}catch(IOException ex){}
		}
		
		return true;
	}

	private String getDatasetString(Dataset dataset) {
		String dsString = "ID"+dataset.getDatasetId();
		String name = dataset.getDatasetName();
		if(name != null && name.length() > 0) {
			dsString += "_"+name;
		}
		return dsString;
	}
	
	private boolean writeInputFile(ProteinComparisonDataset grpComparison,
			StringBuilder errorMesage, String dir) {
		String fileName = dir+File.separator+ClusteringConstants.INPUT_FILE;
		log.info("Writing input file: "+fileName);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			
			// write the IDs of the protein inferences being compared
			writer.write("Name");
			for(Dataset dataset: grpComparison.getDatasets()) {
				String dsString = getDatasetString(dataset);
				writer.write("\t"+dsString);
			}
			writer.write("\n");
			
			// write the results so that we can cluster them
			for(ComparisonProtein prot: grpComparison.getProteins()) {
				
				String rowname = "";// grpProtein.getGroupId()+"_";
				ProteinListing listing = prot.getProteinListing();
				// Can't use common-names since two or more proteins can have the same common name
				rowname += listing.getFastaAccessions().get(0);
				
				writer.write(rowname);
				
				
				for(Dataset dataset: grpComparison.getDatasets()) {
					DatasetProteinInformation dpi = prot.getDatasetProteinInformation(dataset);
					if(dpi != null)
						writer.write("\t"+dpi.getNormalizedSpectrumCount());
					else
						writer.write("\t0");
				}
				writer.write("\n");
			}
		}
		catch(IOException ex) {
			errorMesage.append("Error creating input file: "+fileName+"\n"+ex.getMessage());
			return false;
		}
		finally {
			if(writer != null) try{writer.close();}catch(IOException ex){}
		}
		
		return true;
	}
	
	private ProteinGroupComparisonDataset reorderComparison(ProteinGroupComparisonDataset grpComparison,
			List<Integer> datasetOrder, List<Integer> groupOrder) {
		
		
		log.info("Reordering ProteinGroupComparisonDataset");
		long s = System.currentTimeMillis();
		
		ProteinGroupComparisonDataset clustered = new ProteinGroupComparisonDataset();
		
		// reorder the datasets
		List<Dataset> orderedDS = new ArrayList<Dataset>(grpComparison.getDatasetCount());
		List<? extends Dataset> originalDS = grpComparison.getDatasets();
		for(int i = 0; i < datasetOrder.size(); i++) {
			orderedDS.add(originalDS.get(datasetOrder.get(i) - 1)); // R returns 1-based indexes
		}
		clustered.setDatasets(orderedDS);
		
		// reorder the groups
		List<ComparisonProteinGroup> originalGrps = grpComparison.getProteinsGroups();
		for(int i = groupOrder.size() - 1; i >= 0; i--) {
			clustered.addProteinGroup(originalGrps.get(groupOrder.get(i) - 1)); // R returns 1-based indexes
		}
		
		clustered.setProteinsInitialized(true);
		clustered.setMinHeatMapSpectrumCount(grpComparison.getMinHeatMapSpectrumCount()); // This should be before initSummary
		clustered.initSummary();
		clustered.setSortBy(null);
		clustered.setSortOrder(null);
		
		long e = System.currentTimeMillis();
		log.info("Reordered ProteinGroupComparisonDataset in "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		return clustered;
		
	}
	
	private ProteinComparisonDataset reorderComparison(ProteinComparisonDataset comparison,
			List<Integer> datasetOrder, List<Integer> proteinOrder) {
		
		
		log.info("Reordering ProteinComparisonDataset");
		long s = System.currentTimeMillis();
		
		ProteinComparisonDataset clustered = new ProteinComparisonDataset();
		
		// reorder the datasets
		List<Dataset> orderedDS = new ArrayList<Dataset>(comparison.getDatasetCount());
		List<? extends Dataset> originalDS = comparison.getDatasets();
		for(int i = 0; i < datasetOrder.size(); i++) {
			orderedDS.add(originalDS.get(datasetOrder.get(i) - 1)); // R returns 1-based indexes
		}
		clustered.setDatasets(orderedDS);
		
		// reorder the proteins
		List<ComparisonProtein> originalProteins = comparison.getProteins();
		for(int i = proteinOrder.size() - 1; i >= 0; i--) {
			clustered.addProtein(originalProteins.get(proteinOrder.get(i) - 1)); // R returns 1-based indexes
		}
		
		clustered.setProteinsInitialized(true);
		clustered.setMinHeatMapSpectrumCount(comparison.getMinHeatMapSpectrumCount());
		clustered.initSummary();
		clustered.setSortBy(null);
		clustered.setSortOrder(null);
		
		long e = System.currentTimeMillis();
		log.info("Reordered ProteinComparisonDataset in "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		return clustered;
		
	}

	private List<Integer> readDatasetOrder(File output) throws IOException {
		return readOrder(output, 2);
	}
	
	private List<Integer> readGroupOrder(File output) throws IOException {
		return readOrder(output, 1);
	}
	
	private List<Integer> readOrder(File output, int readLine) throws IOException {
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(output));
			String line = reader.readLine();
			if(readLine == 2)
				line = reader.readLine();
			
			String[] tokens = line.trim().split(",");
			List<Integer> order = new ArrayList<Integer>(tokens.length);
			for(String tok: tokens)
				order.add(Integer.parseInt(tok));
			return order;
		}
		finally {
			if(reader != null) try{reader.close();}catch(IOException e){}
		}
		
	}
	
	private String writeShScript(String dir, String pathToRScript) throws IOException {
		
		//ApplicationProperties.load(); // load properties
		
		String file = dir+File.separator+ClusteringConstants.SH_SCRIPT;
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write("#!/bin/sh\n");
			// read this: http://www.uni-koeln.de/rrzk/server/documentation/modules.html
			// writer.write(". /etc/profile.d/modules.sh\n");
			// writer.write("module load modules modules-init modules-gs R\n");
			// writer.write("/net/gs/vol3/software/bin/R --vanilla --slave --file="+pathToRScript+"\n");
			// writer.write("R --vanilla --slave --file="+pathToRScript+"\n");
			writer.write(ApplicationProperties.getPathToR()+" --vanilla --slave --file="+pathToRScript+"\n");
		}
		finally {
			if(writer != null) try{writer.close();}catch(IOException e){}
		}
		return file;
	}

	private String writeRScript(String dir, ROptions rinfo) throws IOException {
		
		String file = dir+File.separator+ClusteringConstants.R_SCRIPT;
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write("library(gplots)\n");
			writer.write("test=read.table(\""+dir+File.separator+ClusteringConstants.INPUT_FILE+"\", header=T)\n");
			writer.write("test_sc=test[,-1]\n");
			if(rinfo.isDoLog()) {
				writer.write("test_sc=log"+rinfo.getLogBase()+"(test_sc)\n");
				if(rinfo.getValueForMissing() != Double.MIN_VALUE) {
					writer.write("test_sc[test_sc == -Inf] <- "+rinfo.getValueForMissing()+"\n");
				}
				else {
					writer.write("test_sc[test_sc == -Inf] <- -1.0\n");
				}
			}
//			else if(rinfo.getValueForMissing() != Double.MIN_VALUE) {
//				writer.write("test_sc[test_sc == 0] <- "+rinfo.getValueForMissing()+"\n");
//			}
			
			writer.write("rownames(test_sc) <- test[,1]\n");
			// writer.write("source(\"http://faculty.ucr.edu/~tgirke/Documents/R_BioCond/My_R_Scripts/my.colorFct.R\")\n");
			
			if(rinfo.isScaleRows()) {
				writer.write("test_sc <- t(scale(t(test_sc)))\n");
			}
			
			writer.write("write(t(as.matrix(test_sc)), file=\""+dir+File.separator+ClusteringConstants.INPUT_FILE_MOD+"\", sep=\" \", length(colnames(test_sc)))\n");
			// get my colors
			writer.write("all_sc = unmatrix(as.matrix(test_sc))\n");
			writer.write("all_sc = sort(all_sc)\n");
			writer.write("m = length(which(all_sc <= mean(all_sc)))\n");
			if(rinfo.isScaleRows())
				writer.write("n_high = 255 * 0.5\n");
			else
				writer.write("n_high = 255 * (m/length(all_sc))\n");
			writer.write("n_low = 255 - n_high\n");
			
			// write the colors used for the heatmap
			// red-green gradient
			writer.write("grad_rg = c(hsv(0.25 , 1, seq(1,0,length=n_low)), hsv(1 , 1, seq(0,1,length=n_high)))\n");
			writer.write("write(grad_rg, file=\""+dir+File.separator+ClusteringConstants.COLORS_RG+"\", append=FALSE, 1)\n");
			
			
			// blue-yellow gradient
			writer.write("grad_by = c(hsv(0.67 , 1, seq(1,0,length=n_low)), hsv(0.17 , 1, seq(0,1,length=n_high)))\n");
			writer.write("write(grad_by, file=\""+dir+File.separator+ClusteringConstants.COLORS_BY+"\", append=FALSE, 1)\n");
			
			if(rinfo.getGradient() == GRADIENT.BY) 
				writer.write("my_cols = grad_by\n");
			else
				writer.write("my_cols = grad_rg\n");
			
			
			String devType = ClusteringConstants.IMG_FILE.substring(ClusteringConstants.IMG_FILE.lastIndexOf(".")+1);
			writer.write(devType+"(\""+dir+File.separator+ClusteringConstants.IMG_FILE+"\")\n");
			writer.write("options(expressions=10000)\n");
			//writer.write("hm <- heatmap.2(as.matrix(test_sc), cexCol=1.0, col=my.colorFct(), scale=\"none\", margins=c(5,10)");
			writer.write("hm <- heatmap.2(as.matrix(test_sc), cexCol=0.8, col=my_cols, scale=\"none\", margins=c(8,10), trace=\"none\" ");
			if(rinfo.numCols <= 2 || !rinfo.isClusterColumns()) {
				writer.write(", Colv=NA ");
			}
			double cexRow = 1.0/Math.log10((double)rinfo.numRows);
			cexRow = Math.round(cexRow*10000.0) / 10000.0;
			cexRow = Math.max(0.05, cexRow);
			writer.write(", cexRow="+cexRow+" ");
			writer.write(")\n");
			writer.write("dev.off()\n");
			// write the index of protein groups after clustering
			writer.write("write(hm$rowInd, file=\""+dir+File.separator+ClusteringConstants.OUTPUT_FILE+"\", sep=\",\", append=FALSE, length(hm$rowInd))\n");
			// write the index of the datasets after clustering
			writer.write("write(hm$colInd, file=\""+dir+File.separator+ClusteringConstants.OUTPUT_FILE+"\", sep=\",\", append=TRUE, length(hm$colInd))\n");
		}
		finally {
			if(writer != null) try{writer.close();}catch(IOException e){}
		}
		return file;
	}
	
	
	public static final class ROptions {
		int numRows;
		int numCols;
		boolean doLog = false;
		int logBase = 10;
		double valueForMissing = -1.0; // only used for log scale
		boolean scaleRows = true;
		boolean clusterColumns = false;
		GRADIENT gradient = GRADIENT.BY;
		
		public int getNumRows() {
			return numRows;
		}
		public void setNumRows(int numRows) {
			this.numRows = numRows;
		}
		public int getNumCols() {
			return numCols;
		}
		public void setNumCols(int numCols) {
			this.numCols = numCols;
		}
		public boolean isDoLog() {
			return doLog;
		}
		public void setDoLog(boolean doLog) {
			this.doLog = doLog;
		}
		public int getLogBase() {
			return logBase;
		}
		public void setLogBase(int logBase) {
			this.logBase = logBase;
		}
		public double getValueForMissing() {
			return valueForMissing;
		}
		public void setValueForMissing(double valueForMissing) {
			this.valueForMissing = valueForMissing;
		}
		public boolean isScaleRows() {
			return scaleRows;
		}
		public void setScaleRows(boolean scaleRows) {
			this.scaleRows = scaleRows;
		}
		public boolean isClusterColumns() {
			return clusterColumns;
		}
		public void setClusterColumns(boolean clusterColumns) {
			this.clusterColumns = clusterColumns;
		}
		public GRADIENT getGradient() {
			return gradient;
		}
		public void setGradient(GRADIENT gradient) {
			this.gradient = gradient;
		}
		public String getGradientFile() {
			return this.gradient == GRADIENT.BY ? 
					ClusteringConstants.COLORS_BY : ClusteringConstants.COLORS_RG;
		}
	}
}
