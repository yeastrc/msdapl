package org.yeastrc.www.philiusws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.0
 * <p>
 * An example of how this class may be used:
 * 
 * <pre>
 * PhiliusPredictorService service = new PhiliusPredictorService();
 * PhiliusPredictorDelegate portType = service.getPhiliusPredictorPort();
 * portType.getResultsAsync(...);
 * </pre>
 * 
 * </p>
 * 
 */
@WebServiceClient(name = "PhiliusPredictorService", targetNamespace = "http://ws.philius.yeastrc.org/", wsdlLocation = "http://www.yeastrc.org/philius/servlet/PhiliusPredictorPort?wsdl")
public class PhiliusPredictorService extends Service {

	private final static URL PHILIUSPREDICTORSERVICE_WSDL_LOCATION;
	private final static Logger logger = Logger
			.getLogger(org.yeastrc.www.philiusws.PhiliusPredictorService.class
					.getName());

	static {
		URL url = null;
		try {
			URL baseUrl;
			baseUrl = org.yeastrc.www.philiusws.PhiliusPredictorService.class
					.getResource(".");
			url = new URL(baseUrl,
					"http://www.yeastrc.org/philius/servlet/PhiliusPredictorPort?wsdl");
		} catch (MalformedURLException e) {
			logger
					.warning("Failed to create URL for the wsdl Location: 'http://www.yeastrc.org/philius/servlet/PhiliusPredictorPort?wsdl', retrying as a local file");
			logger.warning(e.getMessage());
		}
		PHILIUSPREDICTORSERVICE_WSDL_LOCATION = url;
	}

	public PhiliusPredictorService(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public PhiliusPredictorService() {
		super(PHILIUSPREDICTORSERVICE_WSDL_LOCATION, new QName(
				"http://ws.philius.yeastrc.org/", "PhiliusPredictorService"));
	}

	/**
	 * 
	 * @return returns PhiliusPredictorDelegate
	 */
	@WebEndpoint(name = "PhiliusPredictorPort")
	public PhiliusPredictorDelegate getPhiliusPredictorPort() {
		return super.getPort(new QName("http://ws.philius.yeastrc.org/",
				"PhiliusPredictorPort"), PhiliusPredictorDelegate.class);
	}

}
