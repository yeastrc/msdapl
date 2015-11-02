import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.File;

/**
 * ParsimProtRunner.java
 * @author Vagisha Sharma
 * Apr 9, 2012
 */

/**
 * 
 */
public class ParsimProtRunnerForPepInput {

	public static void main(String[] args) throws Exception {
		
		CmdLineParser parser = new CmdLineParser();
		
		CmdLineParser.Option fastaFileOpt = parser.addStringOption('f', "fasta_file");
		CmdLineParser.Option peptideFileOpt = parser.addStringOption('p', "peptides_file");
		CmdLineParser.Option outputOpt = parser.addStringOption('o', "output");
		CmdLineParser.Option minPeptOpt = parser.addStringOption('n', "min_peptides_per_group");
		
		// parse command line options
        try { parser.parse(args); }
        catch (IllegalOptionValueException e) {
            System.err.println(e.getMessage());
            printUsageAndExit();
        }
        catch (UnknownOptionException e) {
            System.err.println(e.getMessage());
            printUsageAndExit();
        }
        
        // Fasta file
        String fastaFile = (String) parser.getOptionValue(fastaFileOpt);
        if(fastaFile == null) {
        	System.err.println("Please specify an fasta file with the -f option.");
        	printUsageAndExit();
        }
        
        if(!(new File(fastaFile).exists())) {
        	System.err.println("Fasta file does not exist: "+fastaFile);
        	printUsageAndExit();
        }
        
        // Peptides file
        String peptidesFile = (String) parser.getOptionValue(peptideFileOpt);
        if(peptidesFile == null) {
        	System.err.println("Please specify a peptides file with the -p option.");
        	printUsageAndExit();
        }
        
        if(!(new File(fastaFile).exists())) {
        	System.err.println("Peptides file does not exist: "+peptidesFile);
        	printUsageAndExit();
        }
        
        // Output file
        String outputFile = (String) parser.getOptionValue(outputOpt);
        if(outputFile == null) {
        	System.err.println("Please specify an output file with the -o option.");
        	printUsageAndExit();
        }
       
        // min peptides per protein
        int minPept = (Integer)parser.getOptionValue(minPeptOpt, 1);
        
        
        PeptideProteinMapper mapper = new PeptideProteinMapper();
        mapper.setFastaFile(fastaFile);
        mapper.map(peptidesFile); // This will create a file <peptideFile>..peptide_to_protein_map.txt
        
        
        ParsimoniousProteinFinder parsimProt = new ParsimoniousProteinFinder();
        parsimProt.setInputFile(peptidesFile+".peptide_to_protein_map.txt");
        parsimProt.setOutputFile(outputFile);
        parsimProt.setMinPeptides(minPept);
        parsimProt.getParsimoniousList();
		
	}
	
	private static void printUsageAndExit() {
		String usage = "\n\nUsage options:\n"+
		"-f <fasta_file> \n"+
		"-p <peptides_file>\n"+
		"-o <output_file>\n"+
		"-n <min_peptides_per_protein_group> default 1"
		;
		
		System.out.println(usage);
        System.exit(1);
	}
}
