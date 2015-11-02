/**
 * PercolatorRunScriptWriter.java
 * @author Vagisha Sharma
 * Dec 9, 2010
 */
package org.yeastrc.www.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.yeastrc.jobqueue.PercolatorJob;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.service.MsDataUploadProperties;
import org.yeastrc.ms.util.FileUtils;
import org.yeastrc.properties.ApplicationProperties;
import org.yeastrc.www.upload.PercolatorRunForm.PercolatorInputFile;

/**
 * 
 */
public class PercolatorResultDirectoryCreator {

	private static PercolatorResultDirectoryCreator instance;
	
	private PercolatorResultDirectoryCreator() {}
	
	public static synchronized PercolatorResultDirectoryCreator getInstance() {
	
		if(instance == null)
			instance = new PercolatorResultDirectoryCreator();
		return instance;
	}
	
	public void initForJob(PercolatorJob job) throws PercolatorExecutorException {
		
		
		// create the directory (proj<projectId>_expt<exptId>_<timestamp?)
		String baseResultDir = ApplicationProperties.getPercolatorResultDir();
		String resultDir = baseResultDir+File.separator+"proj"+job.getProjectID()+"_expt"+job.getExperimentID()+"_"+String.valueOf((new Date()).getTime());
		job.setResultDirectory(resultDir);
		
		File dir = new File(resultDir);
		boolean created = dir.mkdir();
		if(!created) {
			throw new PercolatorExecutorException("Error creating output directory: "+resultDir);
		}
		dir.setWritable(true,false);
		
		// make sure we have sequest and decoy files for this experiment
		int experimentId = job.getExperimentID();
		String exptDir = MsDataUploadProperties.getBackupDirectory()+File.separator+experimentId+File.separator+"sequest";
		if(!(new File(exptDir).exists())) {
			FileUtils.deleteFile(new File(resultDir));
			throw new PercolatorExecutorException("No sequest directory found for experiment: "+experimentId);
		}
		
		// create the input files for Percolator
		String realSqtFile = resultDir+File.separator+"realSqt.list";
		String randSqtFile = resultDir+File.separator+"randSqt.list";
		
		MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
		
		BufferedWriter writer = null;
		// write realSqt.list
		try {
			writer = new BufferedWriter(new FileWriter(realSqtFile));
		} catch (IOException e) {
			FileUtils.deleteFile(new File(resultDir));
			throw new PercolatorExecutorException("Error creating file: "+realSqtFile, e);
		}
		
		try {
			for(PercolatorInputFile percInput: job.getPercolatorInputFiles()) {

				String fileName = runSearchDao.loadFilenameForRunSearch(percInput.getRunSearchId());
				// make sure this file exists in the backup directory
				String exptFilePath = exptDir+File.separator+fileName+".sqt";
				if(!(new File(exptFilePath).exists())) {
					FileUtils.deleteFile(new File(resultDir));
					throw new PercolatorExecutorException("Sequest SQT does not exist in backup experiment directory: "+exptFilePath);
				}
				writer.write(exptFilePath+"\n");
			}
		}
		catch (RuntimeException e) {
			FileUtils.deleteFile(new File(resultDir));
			throw new PercolatorExecutorException("Error getting file name for runSearchId", e);
		} catch (IOException e) {
			FileUtils.deleteFile(new File(resultDir));
			throw new PercolatorExecutorException("Error writing to file: "+realSqtFile, e);
		}
		finally {
			if(writer != null)
				try {writer.close();} catch(IOException e){}
		}
		
		// write randSqt.list
		try {
			writer = new BufferedWriter(new FileWriter(randSqtFile));
		} catch (IOException e) {
			FileUtils.deleteFile(new File(resultDir));
			throw new PercolatorExecutorException("Error creating file: "+randSqtFile, e);
		}
		
		try {
			for(PercolatorInputFile percInput: job.getPercolatorInputFiles()) {

				String fileName = runSearchDao.loadFilenameForRunSearch(percInput.getRunSearchId());
				// make sure this file exists in the backup directory
				String exptFilePath = exptDir+File.separator+"decoy"+File.separator+fileName+".sqt";
				if(!(new File(exptFilePath).exists())) {
					FileUtils.deleteFile(new File(resultDir));
					throw new PercolatorExecutorException("Sequest decoy SQT does not exist in backup experiment directory: "+exptFilePath);
				}
				writer.write(exptFilePath+"\n");
			}
		}
		catch (RuntimeException e) {
			FileUtils.deleteFile(new File(resultDir));
			throw new PercolatorExecutorException("Error getting file name for runSearchId", e);
		} catch (IOException e) {
			FileUtils.deleteFile(new File(resultDir));
			throw new PercolatorExecutorException("Error writing to file: "+realSqtFile, e);
		}
		finally {
			if(writer != null)
				try {writer.close();} catch(IOException e){}
		}
		
		
		// write the perc.sh script
		String percShPath = resultDir+File.separator+"perc.sh";
		try {
			writer = new BufferedWriter(new FileWriter(percShPath));
		} catch (IOException e) {
			FileUtils.deleteFile(new File(resultDir));
			throw new PercolatorExecutorException("Error creating file: "+percShPath, e);
		}
		try {
			writer.write("#!/bin/sh");
			writer.write("\n\n");
			writer.write("#$ -S /bin/sh");
			writer.write("\n\n");
			writer.write("#$ -N perc.26545");
			writer.write("\n\n");
			writer.write("# Parallel Environment Request");
			writer.write("\n\n");
			writer.write("#$ -pe mpich 1");
			writer.write("\n\n");
			writer.write("export PATH=$PATH:/net/maccoss/vol2/software/bin");
			writer.write("\n\n");
			writer.write("cd "+resultDir);
			writer.write("\n\n");
			writer.write("which percolator;");
			writer.write("\n\n");
			writer.write("sqt2pin "+realSqtFile+" "+randSqtFile+" > psms.pin.xml");
			writer.write("\n\n");
			writer.write("percolator -v 2  -M -E psms.pin.xml -X combined-results.xml >> perc-messages 2> perc-stder && touch perc.26545.success");
			writer.write("\n");
		}
		catch (IOException e) {
			FileUtils.deleteFile(new File(resultDir));
			throw new PercolatorExecutorException("Error writing to file: "+percShPath, e);
		}
		finally {
			if(writer != null)
				try {writer.close();} catch(IOException e){}
		}
	}
}
