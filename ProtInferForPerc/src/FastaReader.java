import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * FastaReader.java
 * @author Vagisha Sharma
 * Apr 5, 2012
 */

/**
 * 
 */
public class FastaReader {

	private BufferedReader reader = null;
	private String currentLine;
	
	public FastaReader (String filePath) throws IOException {
		
		reader = new BufferedReader(new FileReader(filePath));
		currentLine = reader.readLine();
	}
	
	public boolean hasNext() throws IOException {
		
		return currentLine != null && currentLine.startsWith(">");
	}
	
	public FastaProtein getNext() throws IOException {
		
		if(currentLine == null || !currentLine.startsWith(">"))
		{
			throw new IOException("Invalid line while reading protein: "+currentLine);
		}
		
		FastaProtein protein = new FastaProtein();
		String header = currentLine.substring(1); // remove ">"
		String[] parts = header.split("\\s+",2);
		
		String[] accParts = parts[0].split("\\cA");
		protein.accessions = new HashSet<String>(Arrays.asList(accParts));
		if(parts.length > 1) {
			protein.description = parts[1];
		}
		
		String sequence = "";
		while((currentLine = reader.readLine()) != null) {
			
			if(currentLine.startsWith(">"))
				break;
			
			sequence += currentLine.trim();
		}
		protein.setSequence(sequence);
		
		return protein;
	}
	
	public void close() {
		if(reader != null) try {reader.close();} catch(IOException e){}
	}
	
	public static final class FastaProtein {
		
		private Set<String> accessions;
		private String description;
		private String sequence;
		
		public Set<String> getAccessions() {
			return accessions;
		}
		public void setAccessions(Set<String> accessions) {
			this.accessions = accessions;
		}
		public String getAccessionString() {
			
			StringBuilder buf = new StringBuilder();
			
			for(String acc: accessions) {
				buf.append(acc);
				buf.append(",");
			}
			if(buf.length() > 0) {
				buf.deleteCharAt(buf.length() - 1); // remove last comma
			}
			
			return buf.toString();
		}
		public String getHeader() {
			
			StringBuilder buf = new StringBuilder();
			
			for(String acc: accessions) {
				buf.append(acc);
				buf.append(",");
			}
			if(buf.length() > 0) {
				buf.deleteCharAt(buf.length() - 1); // remove last comma
			}
			
			buf.append(" ");
			if(description != null)
				buf.append(description);
			
			return buf.toString();
		}
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getSequence() {
			return sequence;
		}
		public void setSequence(String sequence) {
			this.sequence = sequence;
		}
		public String getSequence(int lineLength) {
			
			if(sequence == null)
				return "";
			
			int start = 0;
			StringBuilder buf = new StringBuilder();
			while(start < sequence.length()) {
				
				int end = Math.min(sequence.length(), start+lineLength);
				buf.append(sequence.substring(start, end));
				buf.append("\n");
				
				start = end;
			}
			return buf.toString();
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		String file = "/Users/silmaril/Desktop/genn_worm_data/c_elegans.WS229.protein.fa";
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/silmaril/Desktop/genn_worm_data/temp.fa"));
		FastaReader reader = new FastaReader(file);
		
		while(reader.hasNext())
		{
			FastaProtein protein = reader.getNext();
			writer.write(protein.getHeader());
			writer.newLine();
			writer.write(protein.getSequence(60));
		}
		writer.close();
		reader.close();
	}
}
