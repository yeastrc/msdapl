/**
 * 
 */
package org.yeastrc.ms.parser.barista;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestResultPeptideBuilder;

/**
 * BaristaXmlFileReader.java
 * @author Vagisha Sharma
 * Jul 25, 2011
 * 
 */
public class BaristaXmlFileReader {

	private InputStream inputStr = null;
    private XMLStreamReader reader = null;
    
    private String filePath;
    
    public String namespace = null;
    
    private Program searchProgram;
    private List<? extends MsResidueModificationIn> searchDynamicResidueMods;
    
    public BaristaXmlFileReader() {
    	searchDynamicResidueMods = new ArrayList<MsResidueModificationIn>();
    }
    
    public void setSearchProgram(Program program) {
    	this.searchProgram = program;
    }
    
    public void open(String filePath) throws DataProviderException {
        
		this.filePath = filePath;
		
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            inputStr = new FileInputStream(filePath);
            reader = inputFactory.createXMLStreamReader(inputStr);
            
            // check if this is a Barista file
        }
        catch (FileNotFoundException e) {
            throw new DataProviderException("File not found: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
    }

	public void close() {
		if (reader != null) {
            try {reader.close();}
            catch (XMLStreamException e) {}
        }
        
        if(inputStr != null) {
            try {inputStr.close();}
            catch(IOException e){}
        }
	}
    
    public static boolean isBaristaXml(String filePath) throws DataProviderException {
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader reader = null;
        try {
        	InputStream inputStr = new FileInputStream(filePath);
        	reader = inputFactory.createXMLStreamReader(inputStr);
        	while(reader.hasNext()) {
    			
    			int evtType = reader.next();
    			if(evtType == XMLStreamReader.START_ELEMENT) {
    				if(reader.getLocalName().equalsIgnoreCase("barista_output")) {
    					return true;
    				}
    				else
    					return false;
    			}
        	}
        }
        catch (FileNotFoundException e) {
            throw new DataProviderException("File not found: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        finally {
        	if(reader != null) try {reader.close();} catch (XMLStreamException e) {}
        }
        
        return false;
	}

    // ---------------------------------------------------------------------------------
    // <psm> elements are contained in a <psms> element
    // ---------------------------------------------------------------------------------
    public boolean hasNextPsm() throws DataProviderException {
		
		if (reader == null)
            return false;
		
		try {
			while(reader.hasNext()) {
                
				int evtType = reader.next();
                
                if (evtType == XMLStreamReader.END_ELEMENT) {
                    // this is the end of the <psms> element
                    if (reader.getLocalName().equals("psms"))  {
                        return false;
                    }
                }
                
                if (evtType == XMLStreamReader.START_ELEMENT) {
                    if (reader.getLocalName().equals("psm"))  {
                    	
                    	return true;
                    }
                }
			}
		}
		catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
		return false;
	}
    
    public BaristaXmlPsmResult getNextPsm() throws DataProviderException {
		
		/*
		 
		 <psms>
 			<psm p:psm_id=481815>
  			<q_value>0</q_value>
  			<score>2.62124</score>
  			<scan>22795</scan>
  			<charge>3</charge>
  			<precursor_mass>2684.42</precursor_mass>
  			<peptide_seq n ="R" c="Y" seq="IPLNSVSHLSIDGDVVLNHVQWGGK"/>
  			<file_name>sequest/31Mar2011-PES-fract5-GF1F5-totalN2-01.sqt</file_name>
 		</psm>

		 */
    	BaristaXmlPsmResult result = new BaristaXmlPsmResult();
    	
    	// We are at the <psm> element; First read the psm_id attribute
		String id = reader.getAttributeValue(namespace, "psm_id");
		if(id == null)
			id = reader.getAttributeValue(null, "psm_id");
		if(id == null) 
			throw new DataProviderException("psm_id attribute is required for parsing <psm> result");
		
		try {
			result.setBaristaId(Integer.parseInt(id));
		}
		catch(NumberFormatException e) {
			throw new DataProviderException("Error parsing value of psm_id attribute: "+id);
		}
		catch(IllegalArgumentException e) {
			throw new DataProviderException("Could not parse psm_id attribute: "+id);
		}
		
		
		String seq = null;
		char nterm = '-';
		char cterm = '-';
		
		try {
			while (reader.hasNext()) {
				int evtType = reader.next();
				
				if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("psm"))
					break;
				
				if (evtType == XMLStreamReader.START_ELEMENT) {
					
					if(reader.getLocalName().equalsIgnoreCase("q_value")) {
						String qval = reader.getElementText();
						try {
							result.setQvalue(Double.parseDouble(qval));
						}
						catch(NumberFormatException e) {
							throw new DataProviderException("Error parsing value in element <q_value>: "+qval+" for psm_id: "+result.getBaristaId());
						}
					}
					else if(reader.getLocalName().equalsIgnoreCase("score")) {
						String score = reader.getElementText();
						try {
							result.setScore(Double.parseDouble(score));
						}
						catch(NumberFormatException e) {
							throw new DataProviderException("Error parsing value in element <score>: "+score+" for psm_id: "+result.getBaristaId());
						}
					}
					else if(reader.getLocalName().equalsIgnoreCase("scan")) {
						String scan = reader.getElementText();
						try {
							result.setScanNumber(Integer.parseInt(scan));
						}
						catch(NumberFormatException e) {
							throw new DataProviderException("Error parsing value in element <scan>: "+scan+" for psm_id: "+result.getBaristaId());
						}
					}
					else if(reader.getLocalName().equalsIgnoreCase("charge")) {
						String charge = reader.getElementText();
						try {
							result.setCharge(Integer.parseInt(charge));
						}
						catch(NumberFormatException e) {
							throw new DataProviderException("Error parsing value in element <charge>: "+charge+" for psm_id: "+result.getBaristaId());
						}
					}
					else if(reader.getLocalName().equalsIgnoreCase("precursor_mass")) {
						String exptMass = reader.getElementText();
						try {
							result.setObservedMass(new BigDecimal(exptMass));
						}
						catch(NumberFormatException e) {
							throw new DataProviderException("Error parsing value in element <precursor_mass>: "+exptMass+" for psm_id: "+result.getBaristaId());
						}
					}
					else if(reader.getLocalName().equalsIgnoreCase("file_name")) {
						String fileName = reader.getElementText();
						
						try {
							fileName = new File(fileName).getName();
							int idx = fileName.indexOf(".sqt");
							if(idx != -1) {
								fileName = fileName.substring(0, idx);
							}
							result.setFile(fileName);
						}
						catch(Exception e) {
							throw new DataProviderException("Error parsing value in element <file_name> for psm_id: "+result.getBaristaId());
						}
					}
					else if(reader.getLocalName().equalsIgnoreCase("peptide_seq")) {
						
						seq = reader.getAttributeValue(null, "seq");
						if(seq == null || seq.length() == 0) 
							throw new DataProviderException("seq attribute is required for parsing <peptide> element");
						
						String nt = reader.getAttributeValue(null, "n");
						if(nt == null || nt.length() == 0) 
							throw new DataProviderException("n attribute is required for parsing <peptide> element");
						nterm = nt.charAt(0);
						
						String ct = reader.getAttributeValue(null, "c");
						if(ct == null || ct.length() == 0) 
							throw new DataProviderException("c attribute is required for parsing <peptide> element");
						cterm = ct.charAt(0);
					}
				}
			}
		}
		catch(XMLStreamException e) {
			throw new DataProviderException("Error reading result for psm: "+id, e);
		}
    
		// parse the peptide sequence
		// NOTE: We assume that the sequence represents the modified sequence (Sequest-style)
        try {

            MsSearchResultPeptide resultPeptide = null;
            
            if(searchProgram == Program.SEQUEST )
                resultPeptide = SequestResultPeptideBuilder.instance().build(
                        nterm+"."+seq+"."+cterm, this.searchDynamicResidueMods, null);
            
            else
                throw new DataProviderException("Cannot parse peptide string for search program: "+searchProgram);
            
            result.setResultPeptide(resultPeptide);
        }
        catch(SQTParseException e) {
            throw new DataProviderException("Error building peptide result: "+e.getMessage());
        }
        
		if(!result.isComplete()) {
			throw new DataProviderException("Incomplete Barista psm result: "+result.toString());
		}
		return result;
	}
    
    
    // ---------------------------------------------------------------------------------
    // <peptide> elements are contained in a <peptides> element
    // ---------------------------------------------------------------------------------
    public boolean hasNextPeptide() throws DataProviderException {
		
		if (reader == null)
            return false;
		
		try {
			while(reader.hasNext()) {
                
				int evtType = reader.next();
                
                if (evtType == XMLStreamReader.END_ELEMENT) {
                	// this is the end of the <peptides> element
                    if (reader.getLocalName().equals("peptides"))  {
                        return false;
                    }
                }
                
                if (evtType == XMLStreamReader.START_ELEMENT) {
                    if (reader.getLocalName().equals("peptide"))  {
                    	
                    	return true;
                    }
                }
			}
		}
		catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
		return false;
	}
    
    public BaristaXmlPeptideResult getNextPeptide() throws DataProviderException {
    	
    	/*
    	 <peptides>
 			<peptide p:peptide_id="IPLNSVSHLSIDGDVVLNHVQWGGK">
  				<q_value>0</q_value>
  				<score>2.40234</score>
  				<main_psm_id>481815</main_psm_id>
  				<psm_ids>
   					<psm_id>3484</psm_id>
   					<psm_id>18539</psm_id>
   					<psm_id>18604</psm_id>
   					<psm_id>18617</psm_id>
   				</psm_ids>
  				<protein_ids>
   					<protein_id>ZK892.1.T1:</protein_id>
   					<protein_id>ZK892.1.T3:</protein_id>
   				</protein_ids>
 			</peptide>
    	 */
    	
    	BaristaXmlPeptideResult result = new BaristaXmlPeptideResult();
    	
    	String sequence = reader.getAttributeValue(namespace, "peptide_id");
		if(sequence == null) 
			throw new DataProviderException("peptide_id attribute is required for parsing <peptide> result");
		
		// parse the peptide sequence
		// NOTE: We assume that the sequence represents the modified sequence (Sequest-style)
        try {

            MsSearchResultPeptide resultPeptide = null;
            
            if(searchProgram == Program.SEQUEST )
                resultPeptide = SequestResultPeptideBuilder.instance().build(
                        "-."+sequence+".-",  // NOTE: we don't have n and c term residue information for peptides
                        this.searchDynamicResidueMods, null);
            
            else
                throw new DataProviderException("Cannot parse peptide string for search program: "+searchProgram);
            
            result.setResultPeptide(resultPeptide);
        }
        catch(SQTParseException e) {
            throw new DataProviderException("Error building peptide result: "+e.getMessage());
        }
        
        try {
			
			while (reader.hasNext()) {
				int evtType = reader.next();
				
				if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("peptide"))
					break;
				
				if (evtType == XMLStreamReader.START_ELEMENT) {
					
					if(reader.getLocalName().equalsIgnoreCase("q_value")) {
						String qval = reader.getElementText();
						try {
							result.setQvalue(Double.parseDouble(qval));
						}
						catch(NumberFormatException e) {
							throw new DataProviderException("Error parsing value in element <q_value>: "+qval+" for peptide: "+sequence);
						}
					}
					else if(reader.getLocalName().equalsIgnoreCase("score")) {
						String score = reader.getElementText();
						try {
							result.setScore(Double.parseDouble(score));
						}
						catch(NumberFormatException e) {
							throw new DataProviderException("Error parsing value in element <score>: "+score+" for peptide: "+sequence);
						}
					}
					else if(reader.getLocalName().equalsIgnoreCase("main_psm_id")) {
						String id = reader.getElementText();
						try {
							result.setMainBaristaPsmId(Integer.parseInt(id));
						}
						catch(NumberFormatException e) {
							throw new DataProviderException("Error parsing value in element <main_psm_id>: "+id+" for peptide: "+sequence);
						}
					}
					
					else if(reader.getLocalName().equalsIgnoreCase("psm_ids")) {
						
						while(reader.hasNext()) {
							
							evtType = reader.next();
							
							if(evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("psm_ids"))
								break;
							
							if(evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("psm_id")) {
								String psmid = reader.getElementText();
								try {
									result.addBaristaPsmIds(Integer.parseInt(psmid));
								}
								catch(NumberFormatException e) {
									throw new DataProviderException("Error parsing value in element <psm_id>: "+psmid+" for peptide: "+sequence);
								}
							}
						}
					}
					
					else if(reader.getLocalName().equalsIgnoreCase("protein_ids")) {
						
						while(reader.hasNext()) {
							
							evtType = reader.next();
							
							if(evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("protein_ids"))
								break;
							
							if(evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("protein_id")) {
								String proteinName = reader.getElementText();
								try {
									result.addProtein(proteinName);
								}
								catch(NumberFormatException e) {
									throw new DataProviderException("Error parsing value in element <protein_id>: "+proteinName+" for peptide: "+sequence);
								}
							}
						}
					}
				}
			}
			
        }
		catch(XMLStreamException e) {
			throw new DataProviderException("Error reading result for peptide: "+sequence, e);
		}
		
		if(!result.isComplete()) {
			throw new DataProviderException("Incomplete Barista peptide result: "+result.toString());
		}
		return result;
		
    }
    
    
    // ---------------------------------------------------------------------------------
    // <protein_group> elements are contained in a <proteins> element
    // ---------------------------------------------------------------------------------
    public boolean hasNextProteinGroup() throws DataProviderException {
		
		if (reader == null)
            return false;
		
		try {
			while(reader.hasNext()) {
                
				int evtType = reader.next();
                
                if (evtType == XMLStreamReader.END_ELEMENT) {
                	// this is the end of the <proteins> element
                    if (reader.getLocalName().equals("proteins"))  {
                        return false;
                    }
                }
                
                if (evtType == XMLStreamReader.START_ELEMENT) {
                    if (reader.getLocalName().equals("protein_group"))  {
                    	
                    	return true;
                    }
                }
			}
		}
		catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
		return false;
	}
    
    public BaristaXmlProteinGroupResult getNextProteinGroup() throws DataProviderException {
    	
    	/*
 		<protein_group p:group_id=1>
  			<q_value>0</q_value>
  			<score>39.6827</score>
  			<protein_ids>
   				<protein_id>K02F2.2:^AK02F2.2.T4:^AK02F2.2.T3:^AK02F2.2</protein_id>
   				<protein_id>K02F2.2.T5:</protein_id> 
  			</protein_ids>
  			<peptide_ids>
   				<peptide_id>FGTPQEYK</peptide_id>
   				<peptide_id>ATDVMLAGK</peptide_id>
   				<peptide_id>HVILLAEGR</peptide_id>
   				<peptide_id>ANIIVTTTGCK</peptide_id>
   			</peptide_ids>
 		</protein_group>
 		
    	 */
    	
    	BaristaXmlProteinGroupResult result = new BaristaXmlProteinGroupResult();
    	
    	String groupId = reader.getAttributeValue(namespace, "group_id");
		if(groupId == null) 
			throw new DataProviderException("group_id attribute is required for parsing <protein_group> result");
		
		try {
			result.setBaristaGroupId(Integer.parseInt(groupId));
		}
		catch(NumberFormatException e) {
			throw new DataProviderException("Error parsing value of group_id attribute: "+groupId);
		}
		catch(IllegalArgumentException e) {
			throw new DataProviderException("Could not parse group_id attribute: "+groupId);
		}
		
        try {
			
			while (reader.hasNext()) {
				int evtType = reader.next();
				
				if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("protein_group"))
					break;
				
				if (evtType == XMLStreamReader.START_ELEMENT) {
					
					if(reader.getLocalName().equalsIgnoreCase("q_value")) {
						String qval = reader.getElementText();
						try {
							result.setQvalue(Double.parseDouble(qval));
						}
						catch(NumberFormatException e) {
							throw new DataProviderException("Error parsing value in element <q_value>: "+qval+" for protein_group: "+groupId);
						}
					}
					else if(reader.getLocalName().equalsIgnoreCase("score")) {
						String score = reader.getElementText();
						try {
							result.setScore(Double.parseDouble(score));
						}
						catch(NumberFormatException e) {
							throw new DataProviderException("Error parsing value in element <score>: "+score+" for protein_group: "+groupId);
						}
					}
					
					else if(reader.getLocalName().equalsIgnoreCase("protein_ids")) {
						
						while(reader.hasNext()) {
							
							evtType = reader.next();
							
							if(evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("protein_ids"))
								break;
							
							if(evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("protein_id")) {
								String proteinName = reader.getElementText();
								try {
									result.addProtein(proteinName);
								}
								catch(NumberFormatException e) {
									throw new DataProviderException("Error parsing value in element <protein_id>: "+proteinName+" for protein_group: "+groupId);
								}
							}
						}
					}
					
					else if(reader.getLocalName().equalsIgnoreCase("peptide_ids")) {
						
						while(reader.hasNext()) {
							
							evtType = reader.next();
							
							if(evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("peptide_ids"))
								break;
							
							if(evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("peptide_id")) {
								String peptide = reader.getElementText();
								result.addPeptide(peptide);
							}
						}
					}
					
					
				}
			}
			
        }
		catch(XMLStreamException e) {
			throw new DataProviderException("Error reading result for protein_group: "+groupId, e);
		}
		
		if(!result.isComplete()) {
			throw new DataProviderException("Incomplete Barista protein group result: "+result.toString());
		}
		return result;
		
    	
    }
}
