/**
 * 
 */
package org.yeastrc.ms.parser.unimod;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.yeastrc.ms.parser.unimod.jaxb.UnimodT;

/**
 * UnimodParser.java
 * @author Vagisha Sharma
 * Aug 2, 2011
 * 
 */
public class UnimodParser {

	public final String unimod_xml = "./resources/unimod/unimod.xml";
	
	public UnimodT read() throws FileNotFoundException, JAXBException {
		
		InputStream is = null;
		
		try{
            is = new FileInputStream(unimod_xml);
            
            JAXBContext jc = JAXBContext.newInstance( UnimodT.class );
            Unmarshaller um = jc.createUnmarshaller();
            
            UnimodT unimod = (UnimodT) um.unmarshal(is);

            return unimod;

        }
        finally {
        	if(is != null) try {is.close();} catch(IOException e){}
        }
	}

//	public <T> T unmarshal( Class<T> docClass, InputStream inputStream ) throws JAXBException {
//		
//		String packageName = docClass.getPackage().getName();
//		JAXBContext jc = JAXBContext.newInstance( packageName );
//		Unmarshaller u = jc.createUnmarshaller();
//		JAXBElement<T> doc = (JAXBElement<T>)u.unmarshal( inputStream );
//		return doc.getValue();
//		
//	}

}
