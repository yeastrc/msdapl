import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.percolator.PercolatorXmlFileReader;
import org.yeastrc.ms.parser.percolator.PercolatorXmlPeptideResult;
import org.yeastrc.ms.parser.percolator.PercolatorXmlResult;


public class PeptidePsmGetter {

	private double qvalCutoff = 0.01;
	private double pepCutoff = 1.0;
	
	public void setQvalueCutoff(double cutoff) {
		this.qvalCutoff = cutoff;
	}
	
	public void setPepCutoff(double cutoff) {
		this.pepCutoff = cutoff;
	}
	
	public void run(String percFile, String outputFile) throws IOException, DataProviderException {
		
		Set<String> peptides = getPeptides(percFile);
		getPsmsForPeptides(percFile, peptides, outputFile);
	}
	
	public Set<String> getPeptides(String percFile) throws DataProviderException {
		
		System.out.println("Reading peptides...");
		
		Set<String> peptides = new HashSet<String>();
		
		PercolatorXmlFileReader percReader = new PercolatorXmlFileReader();
		percReader.setReadDecoyResults(false);
		percReader.setSearchProgram(Program.SEQUEST);
		
		percReader.open(percFile);

		// skip over the PSMs
		while(percReader.hasNextPsm()) {
			percReader.getNextPsm(); // consume the PSM
		}

		// read the peptides above the given thresholds
		while(percReader.hasNextPeptide()) {

			PercolatorXmlPeptideResult result = (PercolatorXmlPeptideResult) percReader.getNextPeptide();
			if(result.isDecoy())
				continue;
			if(result.getQvalue() > this.qvalCutoff)
				continue;
			if(result.getPosteriorErrorProbability() > this.pepCutoff)
				continue;

			String peptide = result.getResultPeptide().getPeptideSequence();
			peptides.add(peptide);
		}
		
		System.out.println(peptides.size()+" percolator peptides at qvalue <= "+qvalCutoff+", PEP <= "+pepCutoff);
		
		return peptides;
	}
	
	public void getPsmsForPeptides(String percFile, Set<String> peptides, String outputFile) throws IOException, DataProviderException {
		
		System.out.println("Reading PSMs...");
		
		// Read the percolator file and print all PSMs for the given peptides, above the given score thresholds
		PercolatorXmlFileReader percReader = new PercolatorXmlFileReader();
		percReader.setReadDecoyResults(false);
		percReader.setSearchProgram(Program.SEQUEST);
		
		BufferedWriter writer = null;
		
		int psmCount = 0;
		
		try {
			percReader.open(percFile);
			writer = new BufferedWriter(new FileWriter(outputFile));
			
			// read the psms
			while(percReader.hasNextPsm()) {
				PercolatorXmlResult result = (PercolatorXmlResult) percReader.getNextPsm();
				
				if(result.isDecoy())
					continue;
				
				if(result.getQvalue() > qvalCutoff)
					continue;
				
				if(result.getPosteriorErrorProbability() > pepCutoff)
					continue;
				
				String peptide = result.getResultPeptide().getPeptideSequence();
				if(!peptides.contains(peptide))
					continue;
				
				psmCount++;
				
				writer.write(peptide);
				writer.write("\t");
				writer.write(""+result.getCharge());
				writer.write("\t");
				writer.write(String.valueOf(result.getQvalue()));
				writer.write("\t");
				writer.write(result.getFileName()+"_"+result.getScanNumber());
				writer.write("\t");
				
				writer.newLine();
			}
		}
		finally {
			percReader.close();
			if(writer != null) try {writer.close();} catch(IOException e){}
		}
		System.out.println(psmCount+" PSMs at qvalue <= "+qvalCutoff+", PEP <= "+pepCutoff);
	}
	
	public static void main(String[] args) throws IOException, DataProviderException {
		
		String percFile = args[0]; // "/Users/silmaril/Desktop/genn_worm_data/PES-WS229/TEST/combined-results.perc.xml";
		String outFile = args[1]; // "/Users/silmaril/Desktop/genn_worm_data/PES-WS229/TEST/PeptidePsmGetter.out";
		
		PeptidePsmGetter getter = new PeptidePsmGetter();
		getter.setQvalueCutoff(0.01);
		getter.setPepCutoff(1.0);
		getter.run(percFile, outFile);
	}
}
