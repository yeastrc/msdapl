/**
 * PepXmlUtils.java
 * @author Vagisha Sharma
 * Jun 30, 2011
 */
package org.yeastrc.ms.parser.pepxml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.parser.DataProviderException;

/**
 * 
 */
public class PepXmlUtils {

	private static PepXmlUtils instance;
	
	private PepXmlUtils() {}
	
	public static synchronized PepXmlUtils getInstance() {
		if(instance == null) {
			instance = new PepXmlUtils();
		}
		return instance;
	}
	
	// ---------------------------------------------------------------------------------
    // method to determine if a file is a pepXML file
    // ---------------------------------------------------------------------------------
	public boolean isPepXmlFile(String filePath) throws FileNotFoundException {
    	
    	// open the file read the name of the search program and close the file
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream inputStr = null;
        XMLStreamReader reader = null;
        
        try {
        	
        	inputStr = new FileInputStream(filePath);
            reader = inputFactory.createXMLStreamReader(inputStr);
            
            while(reader.hasNext()) {
            	
                int evtType = reader.next();
                
                // Read the first start element.  If it is <msms_pipeline_analysis> then 
                // this is a pepXML file, otherwise not.
                if(evtType == XMLStreamReader.START_ELEMENT ) {
                	if(reader.getLocalName().equalsIgnoreCase("msms_pipeline_analysis"))
                		return true;
                	else
                		return false;
                }
            }
        }
        catch (XMLStreamException e) {
            return false; // this must not be a XML file
        }
        finally {
            // close the file
            if (reader != null) {
                try {reader.close();}
                catch (XMLStreamException e) {}
            }

            if(inputStr != null) {
                try {inputStr.close();}
                catch(IOException e){}
            }
        }
        
        return false;
    }
	
	// ---------------------------------------------------------------------------------
    // method to get the search program used.  Looks in the first search_summary 
    // element
    // ---------------------------------------------------------------------------------
    public Program getSearchProgram(String filePath) throws DataProviderException {
        
        // open the file read the name of the search program and close the file
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream inputStr = null;
        XMLStreamReader reader = null;
        Program program = null;
        try {
            inputStr = new FileInputStream(filePath);
            reader = inputFactory.createXMLStreamReader(inputStr);
            
            while(reader.hasNext()) {
                int evtType = reader.next();
                if(evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("search_summary")){
                        break;
                }
                if(evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("search_summary")) {
                    String value = reader.getAttributeValue(null,"search_engine");
                    if(value != null) {
                        program = parseProgram(value);
                    }
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
            // close the file
            if (reader != null) {
                try {reader.close();}
                catch (XMLStreamException e) {}
            }

            if(inputStr != null) {
                try {inputStr.close();}
                catch(IOException e){}
            }
        }
        return program;
    }
    
    // ---------------------------------------------------------------------------------
    // method to get the search file format used.  Looks in the first search_summary 
    // element
    // ---------------------------------------------------------------------------------
    public SearchFileFormat getSearchFileType(String filePath) throws DataProviderException {
        Program program = getSearchProgram(filePath);
        if(program == Program.SEQUEST)
            return SearchFileFormat.PEPXML_SEQ;
        else if(program == Program.MASCOT)
            return SearchFileFormat.PEPXML_MASCOT;
        else if(program == Program.XTANDEM)
            return SearchFileFormat.PEPXML_XTANDEM;
        else if(program == Program.COMET)
            return SearchFileFormat.PEPXML_COMET;
        else
            return SearchFileFormat.UNKNOWN;
    }
    
    public static Program parseProgram(String value) {
        if("SEQUEST".equalsIgnoreCase(value))           return Program.SEQUEST;
        else if("MASCOT".equalsIgnoreCase(value))       return Program.MASCOT;
        else if ("X! Tandem".equalsIgnoreCase(value))   return Program.XTANDEM;
        else if("Comet".equalsIgnoreCase(value))		return Program.COMET;
        return null;
    }
}
