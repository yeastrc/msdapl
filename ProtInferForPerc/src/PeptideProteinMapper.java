/**
 * PeptideProteinMapper.java
 * @author Vagisha Sharma
 * May 22, 2011
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class PeptideProteinMapper {

	private Map<String, IdProtein> seqProteinNameMap = new HashMap<String, IdProtein>();
	
	public void map(String peptideFilePath) throws Exception {
		map(peptideFilePath, false);
	}
	public void map(String peptideFilePath, boolean findUniq) throws Exception {
		
		System.out.println("Mapping peptides...");
		
		Set<String> uniqPeptides = null;
		if(findUniq)
			uniqPeptides = new HashSet<String>();
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			
			reader = new BufferedReader(new FileReader(peptideFilePath));
			writer = new BufferedWriter(new FileWriter(peptideFilePath+".peptide_to_protein_map.txt"));
			
			writer.write("Peptide\tProtein\tUnique");
			writer.newLine();
			
			String line = null;
			int count = 0;
			while((line = reader.readLine()) != null) {
				
				if(line.toLowerCase().startsWith("peptide"))
					continue;
				
				String peptide = line.split("\\s+")[0].trim();
				
				if(findUniq && uniqPeptides.contains(peptide))
					continue;
				
				if(findUniq)
					uniqPeptides.add(peptide);
				
				count++;
				if(count%100 == 0)
					System.out.println("peptides mapped: "+count);
				
				// List of proteins matching this peptide
				List<IdProtein> proteins = getProteinsForPeptide(peptide);
				
				if(proteins.size() == 0) {
					System.out.println("No matches found for peptide "+peptide);
					throw new Exception("No matches found for peptide "+peptide);
				}
				
				int uniq = proteins.size() == 1 ? 1 : 0;
				for(IdProtein protein: proteins) {
					
					writer.write(peptide+"\t"+protein.getAccessionString()+"\t"+uniq);
					writer.newLine();
				}
			}
			System.out.println("peptides mapped: "+count);
		}
		finally {
			if(reader != null) try {reader.close();} catch(IOException e){}
			if(writer != null) try {writer.close();} catch(IOException e){}
		}
	}
	
	
	private List<IdProtein> getProteinsForPeptide(String peptide) {
		
		List<IdProtein> proteins = new ArrayList<IdProtein>();
		
		for(String sequence: this.seqProteinNameMap.keySet()) {
			
			if(sequence.contains(peptide)) {
				
				IdProtein protein = seqProteinNameMap.get(sequence);
				proteins.add(protein);
			}
		}
		return proteins;
	}
	
	private static final class IdProtein {
		
		private int id;
		private Set<String> accessions;
		
		public String getAccessionString() {
			
			StringBuilder buf = new StringBuilder();
			for(String prot: accessions) {
				buf.append(",");
				buf.append(prot);
			}
			buf.deleteCharAt(0); // remove first comma
			return buf.toString();
		}
	}

	public void setFastaFile(String fastaFile) throws IOException {
	
		System.out.println("Reading proteins...");
		this.seqProteinNameMap = new HashMap<String, IdProtein>();
		
		FastaReader reader = null;
		try 
		{
			reader = new FastaReader(fastaFile);
			int count = 1;
			while(reader.hasNext()) 
			{
				FastaReader.FastaProtein protein = reader.getNext();
				
				if(addProtein(protein.getAccessions(), protein.getSequence(), this.seqProteinNameMap, count)) {
					count++;
					if(count % 1000 == 0)
						System.out.println("Proteins read: "+count);
				}
			}
		}
		finally 
		{
			if(reader != null) reader.close();
		}
	}

	private boolean addProtein(Set<String> accessionSet, String sequence,
			Map<String, IdProtein> seqProteinNameMap, int count) {
		
		if(seqProteinNameMap.containsKey(sequence)) {
			IdProtein protein = seqProteinNameMap.get(sequence);
			protein.accessions.addAll(accessionSet);
			seqProteinNameMap.put(sequence, protein);
			return false;
		}
		else {
			IdProtein protein = new IdProtein();
			protein.id = count;
			protein.accessions = accessionSet;
			seqProteinNameMap.put(sequence, protein);
			return true;
		}
	}

	public static void main(String[] args) throws Exception {
		
		String fastaFilePath = "/Users/silmaril/Desktop/genn_worm_data/PES-WS229/wormpep229-AG1201-phg-orf-LaDeana-ecoli-contam-gennfix-vsharma.fasta.nr"; // args[0];
		String peptideFilePath = "/Users/silmaril/Desktop/genn_worm_data/PES-WS229/peptides.uniq.txt"; // args[1];
		
		PeptideProteinMapper mapper = new PeptideProteinMapper();
		mapper.setFastaFile(fastaFilePath);
		
		mapper.map(peptideFilePath);
	}
	
}
