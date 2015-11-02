import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uwpr.protinfer.idpicker.SubsetProteinFinder;
import edu.uwpr.protinfer.infer.GraphBuilder;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.graph.BipartiteGraph;
import edu.uwpr.protinfer.infer.graph.ConnectedComponentFinder;
import edu.uwpr.protinfer.infer.graph.GraphCollapser;
import edu.uwpr.protinfer.infer.graph.InvalidVertexException;
import edu.uwpr.protinfer.infer.graph.PeptideVertex;
import edu.uwpr.protinfer.infer.graph.ProteinVertex;
import edu.uwpr.protinfer.infer.graph.SetCoverFinder;

/**
 * PasimoniousProteins.java
 * @author Vagisha Sharma
 * Sep 8, 2010
 */

/**
 * 
 */
public class ParsimoniousProteinFinder {

	private String inputFile;
	private String outputFile;
	
	private int minPept = 1;
	
	private Map<String, InferredProtein<DummySpectrumMatch>> proteinMap;
	private Map<String, PeptideEvidence<DummySpectrumMatch>> peptideMap;
	
	public ParsimoniousProteinFinder() {}
	
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	
	public void setMinPeptides(int minPept) {
		this.minPept = minPept;
	}

	public void getParsimoniousList() throws Exception {
		
		// read the file 
		proteinMap = new HashMap<String, InferredProtein<DummySpectrumMatch>>();
		peptideMap = new HashMap<String, PeptideEvidence<DummySpectrumMatch>>();
		
		System.out.println("Reading file....");
		List<InferredProtein<DummySpectrumMatch>> proteinList = getProteins(inputFile);
		System.out.println("\tFound "+proteinMap.size()+" unique proteins");
		System.out.println("\tFound "+peptideMap.size()+" unique peptides");
		
		if(minPept > 1) {
			
			Iterator<InferredProtein<DummySpectrumMatch>> iterator = proteinList.iterator();
			while(iterator.hasNext())
			{
				InferredProtein<DummySpectrumMatch> protein = iterator.next();
				if(protein.getPeptideEvidenceCount() < minPept)
					iterator.remove();
			}
			System.out.println("\tNumber of proteins at min "+minPept+" peptides: "+proteinList.size());
		}
		
		// build the graph
		System.out.println("Building graph...");
		GraphBuilder builder = new GraphBuilder();
		BipartiteGraph<ProteinVertex, PeptideVertex> graph = builder.buildGraph(proteinList);
		
		// collapse the graph
		System.out.println("Collapsing graph...");
		GraphCollapser<ProteinVertex, PeptideVertex> collapser = new GraphCollapser<ProteinVertex, PeptideVertex>();
		try {
			collapser.collapseGraph(graph);
		} catch (InvalidVertexException e) {
			System.err.println("Error Collapsing graph");
			e.printStackTrace();
			throw e;
		}
		System.out.println("\tAfter merging indistinguishable proteins: "+graph.getLeftVertices().size()+" protein groups");
		
		// mark unique peptides
        markUniquePeptides(graph);
        
        // set the protein and peptide group ids.
        int groupId = 1;
        for(ProteinVertex vertex: graph.getLeftVertices()) {
            for(Protein prot: vertex.getProteins()) {
                prot.setProteinGroupLabel(groupId);
            }
            groupId++;
        }
        groupId = 1;
        for(PeptideVertex vertex: graph.getRightVertices()) {
            for(Peptide pept: vertex.getPeptides()) {
                pept.setPeptideGroupLabel(groupId);
            }
            groupId++;
        }
        
		// get connected components
		ConnectedComponentFinder compFinder = new ConnectedComponentFinder();
        compFinder.findAllConnectedComponents(graph);
		
		// find a set cover
        System.out.println("Finding parsimonious proteins...");
		SetCoverFinder<ProteinVertex, PeptideVertex> coverFinder = new SetCoverFinder<ProteinVertex, PeptideVertex>();
		List<ProteinVertex> cover = coverFinder.getGreedySetCover(graph);
		
		for (ProteinVertex vertex: cover) 
            vertex.setAccepted(true);
		
		int numParsimProt = 0;
        for(InferredProtein<DummySpectrumMatch> prot: proteinList) 
            if(prot.getProtein().isAccepted())  numParsimProt++;
        
//		int numParsimProt = 0;
//		for(ProteinVertex v: cover) {
//			numParsimProt += v.getMemberCount();
//		}
		System.out.println("\tFound "+cover.size()+" parsimonious protein groups ("+numParsimProt+" proteins)");
		
		
		// Mark subset proteins
		System.out.println("Marking subset proteins...");
		SubsetProteinFinder subsetFinder = new SubsetProteinFinder();
		subsetFinder.markSubsetProteins(proteinList);
        
        int subsetCount = 0;
        int subsetGrpCount = 0;
        Set<Integer> grpIdSeen = new HashSet<Integer>();
        for(InferredProtein<DummySpectrumMatch> prot: proteinList) {
        	if(prot.getProtein().isSubset()) {
        		if(prot.getProtein().isAccepted()) {
        			throw new Exception("Protein cannot be both parsimonious and subset!. "+prot.getAccession());
        		}
        		subsetCount++;
        	}
//        	else if(!prot.getProtein().isAccepted()) {
//        		log.info("NOT parsimonious AND NOT subset: "+prot.getAccession());
//        	}
        	if(grpIdSeen.contains(prot.getProteinGroupLabel()))
        		continue;
        	grpIdSeen.add(prot.getProteinGroupLabel());
        	if(prot.getProtein().isSubset())
        		subsetGrpCount++;
        }
        System.out.println("\tSubset Groups: "+subsetGrpCount+"; Subset Proteins: "+subsetCount);
        
		
		//print
		printCover(cover, outputFile);
		
	}
	
	private void markUniquePeptides(BipartiteGraph<ProteinVertex, PeptideVertex> graph) {

		for(ProteinVertex v: graph.getLeftVertices()) {

			List<PeptideVertex> peptList = graph.getAdjacentVerticesL(v);
			for(PeptideVertex pept: peptList) {
				if(graph.getAdjacentVerticesR(pept).size() == 1) {
					// mark all the peptides in this vertex as unique
					for(Peptide p: pept.getPeptides())
						p.markUnique(true);
				}
			}
		}
	}
	
	private void printCover(List<ProteinVertex> cover, String outputFile) {
	
		BufferedWriter writer = null;

		if(outputFile != null)
			try {
				writer = new BufferedWriter(new FileWriter(outputFile));
			} catch (IOException e) {
				System.err.println("Error opening file for writing: "+outputFile);
				e.printStackTrace();
				System.exit(1);
			}

			try {
				for (ProteinVertex v: cover) {

					Protein p = v.getProteins().get(0);
					InferredProtein<DummySpectrumMatch> prot = proteinMap.get(p.getAccession());

					if(prot.getPeptides().size() < minPept) {
						continue;
					}
					
					List<String> labels = new ArrayList<String>();
					for(Protein pr: v.getProteins()) {
						labels.add(pr.getAccession());
					}
					Collections.sort(labels);
					String proteinGrpLabel = "";
					for(String label: labels) {
						proteinGrpLabel += ","+label;
					}
					proteinGrpLabel = proteinGrpLabel.substring(1);
					
					for(PeptideEvidence<DummySpectrumMatch> ev: prot.getPeptides()) {
						
						int uniq = ev.getPeptide().isUniqueToProtein() ? 1 : 0;
						if(writer == null) 
							System.out.println(uniq+"\t"+ ev.getPeptide().getPeptideSequence()+"\t"+proteinGrpLabel);
						else
							writer.write(uniq+"\t"+ ev.getPeptide().getPeptideSequence()+"\t"+proteinGrpLabel+"\n");
					} 
				}
			} catch (IOException e) {
				System.err.println("Error writing to output file");
				e.printStackTrace();
				System.exit(1);
			}
			finally {
				if(writer != null) try{writer.close();}catch(IOException e){}
			}
	}


	private List<InferredProtein<DummySpectrumMatch>> getProteins(String file) {
		
		List<InferredProtein<DummySpectrumMatch>> proteinList = new ArrayList<InferredProtein<DummySpectrumMatch>>();
		
		BufferedReader reader = null;
		
		int proteinId = 1;
		int peptideId = 1;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			
			while((line = reader.readLine()) != null) {
				
				if(line.trim().length() == 0)
					continue;
				
				if(line.startsWith("locusID") || line.startsWith("Protein"))
					continue;
				
				String[] tokens = line.split("\\s+");
				if(tokens.length < 2) {
					System.err.println("Invalid line: "+line);
					System.exit(1);
				}
				String peptide = tokens[0];
				String protein = tokens[1];
				
				InferredProtein<DummySpectrumMatch> prot = proteinMap.get(protein);
				if(prot == null) {
					Protein p = new Protein(protein, proteinId);
					prot = new InferredProtein<DummySpectrumMatch>(p);
					proteinMap.put(protein, prot);
					proteinList.add(prot);
					proteinId++;
				}
				
				PeptideEvidence<DummySpectrumMatch> pept = peptideMap.get(peptide);
				if(pept == null) {
					Peptide p = new Peptide(peptide, peptide, peptideId);
					
//					int uniq = tokens.length > 2 ? Integer.parseInt(tokens[2]) : 0;
//					if(uniq == 1)
//						p.markUnique(true);
					
					pept = new PeptideEvidence<DummySpectrumMatch>(p);
					peptideMap.put(peptide, pept);
					peptideId++;
				}
				
				prot.addPeptideEvidence(pept);
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} 
		catch (IOException e) {
			System.err.println("Error reading file");
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			if(reader != null) try {reader.close();} catch (IOException e){}
		}
		
		return proteinList;
	}
	
	
	public static void main(String[] args) throws Exception {
		
		CmdLineParser parser = new CmdLineParser();
		
		CmdLineParser.Option inputOpt = parser.addStringOption('i', "input");
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
        
        // Input file
        String inputFile = (String) parser.getOptionValue(inputOpt);
        if(inputFile == null) {
        	System.err.println("Please specify an input file with the -i option.");
        	printUsageAndExit();
        }
        
        if(!(new File(inputFile).exists())) {
        	System.err.println("Input file does not exist: "+inputFile);
        	printUsageAndExit();
        }
        
        // Output file
        String outputFile = (String) parser.getOptionValue(outputOpt);
       
        int minPept = (Integer)parser.getOptionValue(minPeptOpt, 1);
        
        ParsimoniousProteinFinder parsimProt = new ParsimoniousProteinFinder();
        parsimProt.setInputFile(inputFile);
        parsimProt.setOutputFile(outputFile);
        parsimProt.setMinPeptides(minPept);
        parsimProt.getParsimoniousList();
        
	}
	
	private static void printUsageAndExit() {
		String usage = "\n\nUsage options:\n"+
		"-i <input_file> \n"+
		"-o <output_file>\n"+
		"-n <min_peptides_per_protein_group>"
		;
		
		System.out.println(usage);
        System.exit(1);
	}
}
