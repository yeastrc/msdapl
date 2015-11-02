import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.File;

/**
 * ParsimProtRunner.java
 * @author Vagisha Sharma
 * Apr 14, 2012
 */

/**
 * 
 */
public class ParsimProtRunner {

	public static void main(String[] args) throws Exception {
		
		CmdLineParser parser = new CmdLineParser();
		
		CmdLineParser.Option fastaFileOpt = parser.addStringOption('f', "fasta_file");
		CmdLineParser.Option percFileOpt = parser.addStringOption('x', "perc_xml");
		CmdLineParser.Option psmFileOpt = parser.addStringOption('p', "psms_file");
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
//        if(fastaFile == null) {
//        	System.err.println("Please specify an fasta file with the -f option.");
//        	printUsageAndExit();
//        }
        
        if(fastaFile != null && !(new File(fastaFile).exists())) {
        	System.err.println("Fasta file does not exist: "+fastaFile);
        	printUsageAndExit();
        }
        
        // PSMs or Percolator XML file
        String psmsFile = (String) parser.getOptionValue(psmFileOpt);
        String percFile = (String) parser.getOptionValue(percFileOpt);
        
        if(psmsFile == null && percFile == null) {
        	System.err.println("Please specify a PSMs file with the -p option or a Percolator file with the -x option.");
        	printUsageAndExit();
        }
        
        if(psmsFile != null && percFile != null) {
        	System.err.println("The options -p and -x cannot be used at the same time.");
        	printUsageAndExit();
        }
        
        if(psmsFile != null && !(new File(psmsFile).exists())) {
        	System.err.println("PSMs file does not exist: "+psmsFile);
        	printUsageAndExit();
        }
        
        if(percFile != null && !(new File(percFile).exists())) {
        	System.err.println("Percolator file does not exist: "+percFile);
        	printUsageAndExit();
        }
        
        // Output file
        String outputFile = (String) parser.getOptionValue(outputOpt);
        if(outputFile == null) {
        	System.err.println("Please specify an output file with the -o option.");
        	printUsageAndExit();
        }
       
        // min peptides per protein
        int minPept = Integer.parseInt((String)parser.getOptionValue(minPeptOpt, "1"));
        
        
        // Step 1. If we are given a Percolator file read the PSMs first.
        if(percFile != null) {
        	psmsFile = new File(percFile).getParent()+File.separator+"psms.txt";
        	PeptidePsmGetter psmGetter = new PeptidePsmGetter();
        	psmGetter.setQvalueCutoff(0.01);
        	psmGetter.setPepCutoff(1.0);
        	psmGetter.run(percFile, outputFile);
        }
        
        
        // Step 2. Read the PSMs file and create a file with peptide to protein mapping 
        //         for the unique peptide sequences in the input file
        if(fastaFile != null) {
        	PeptideProteinMapper mapper = new PeptideProteinMapper();
        	mapper.setFastaFile(fastaFile);
        	mapper.map(psmsFile, true /*filter unique peptides*/); // This will create a file <psmsFile>.peptide_to_protein_map.txt
        }
        
        String pepProtMapFile = psmsFile+".peptide_to_protein_map.txt";
        String pinferInputFile = psmsFile+".pinfer_input.txt";
        
        
        // Step 3. Create input file for the protein inference program
        ProteinInferInputMaker inputMaker = new ProteinInferInputMaker();
        inputMaker.makeProtInferInput(psmsFile, pepProtMapFile, pinferInputFile, true /*separate ions*/);
        
        
        // Step 4. Run protein inference
        ParsimoniousProteinFinder parsimProt = new ParsimoniousProteinFinder();
        parsimProt.setInputFile(pinferInputFile);
        parsimProt.setOutputFile(outputFile);
        parsimProt.setMinPeptides(minPept);
        parsimProt.getParsimoniousList();
		
	}
	
	private static void printUsageAndExit() {
		String usage = "\n\nUsage options:\n"+
		"-f <fasta_file> \n"+
		"-x <percolator_xml_outout> Only one of -x or -p should be used.\n"+
		"-p <psms_file> Format: Peptide charge PSM_qvalue file_scan\n"+
		"-o <output_file>\n"+
		"-n <min_peptides_per_protein_group> default 1"
		;
		
		System.out.println(usage);
        System.exit(1);
	}
}
