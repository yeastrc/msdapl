package org.yeastrc.www.philiusws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the org.yeastrc.www.philiusws package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _IsJobDoneResponse_QNAME = new QName(
			"http://ws.philius.yeastrc.org/", "isJobDoneResponse");
	private final static QName _GetResults_QNAME = new QName(
			"http://ws.philius.yeastrc.org/", "getResults");
	private final static QName _GetResultsResponse_QNAME = new QName(
			"http://ws.philius.yeastrc.org/", "getResultsResponse");
	private final static QName _PhiliusWSException_QNAME = new QName(
			"http://ws.philius.yeastrc.org/", "PhiliusWSException");
	private final static QName _SubmitSequenceResponse_QNAME = new QName(
			"http://ws.philius.yeastrc.org/", "submitSequenceResponse");
	private final static QName _SubmitSequence_QNAME = new QName(
			"http://ws.philius.yeastrc.org/", "submitSequence");
	private final static QName _IsJobDone_QNAME = new QName(
			"http://ws.philius.yeastrc.org/", "isJobDone");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: org.yeastrc.www.philiusws
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link IsJobDone }
	 * 
	 */
	public IsJobDone createIsJobDone() {
		return new IsJobDone();
	}

	/**
	 * Create an instance of {@link PhiliusSequenceAnnotationWS }
	 * 
	 */
	public PhiliusSequenceAnnotationWS createPhiliusSequenceAnnotationWS() {
		return new PhiliusSequenceAnnotationWS();
	}

	/**
	 * Create an instance of {@link IsJobDoneResponse }
	 * 
	 */
	public IsJobDoneResponse createIsJobDoneResponse() {
		return new IsJobDoneResponse();
	}

	/**
	 * Create an instance of {@link PhiliusSPSegmentWS }
	 * 
	 */
	public PhiliusSPSegmentWS createPhiliusSPSegmentWS() {
		return new PhiliusSPSegmentWS();
	}

	/**
	 * Create an instance of {@link GetResults }
	 * 
	 */
	public GetResults createGetResults() {
		return new GetResults();
	}

	/**
	 * Create an instance of {@link PhiliusWSException }
	 * 
	 */
	public PhiliusWSException createPhiliusWSException() {
		return new PhiliusWSException();
	}

	/**
	 * Create an instance of {@link PhiliusSegmentWS }
	 * 
	 */
	public PhiliusSegmentWS createPhiliusSegmentWS() {
		return new PhiliusSegmentWS();
	}

	/**
	 * Create an instance of {@link GetResultsResponse }
	 * 
	 */
	public GetResultsResponse createGetResultsResponse() {
		return new GetResultsResponse();
	}

	/**
	 * Create an instance of {@link SubmitSequenceResponse }
	 * 
	 */
	public SubmitSequenceResponse createSubmitSequenceResponse() {
		return new SubmitSequenceResponse();
	}

	/**
	 * Create an instance of {@link SubmitSequence }
	 * 
	 */
	public SubmitSequence createSubmitSequence() {
		return new SubmitSequence();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link IsJobDoneResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.philius.yeastrc.org/", name = "isJobDoneResponse")
	public JAXBElement<IsJobDoneResponse> createIsJobDoneResponse(
			IsJobDoneResponse value) {
		return new JAXBElement<IsJobDoneResponse>(_IsJobDoneResponse_QNAME,
				IsJobDoneResponse.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link GetResults }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.philius.yeastrc.org/", name = "getResults")
	public JAXBElement<GetResults> createGetResults(GetResults value) {
		return new JAXBElement<GetResults>(_GetResults_QNAME, GetResults.class,
				null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link GetResultsResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.philius.yeastrc.org/", name = "getResultsResponse")
	public JAXBElement<GetResultsResponse> createGetResultsResponse(
			GetResultsResponse value) {
		return new JAXBElement<GetResultsResponse>(_GetResultsResponse_QNAME,
				GetResultsResponse.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link PhiliusWSException }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.philius.yeastrc.org/", name = "PhiliusWSException")
	public JAXBElement<PhiliusWSException> createPhiliusWSException(
			PhiliusWSException value) {
		return new JAXBElement<PhiliusWSException>(_PhiliusWSException_QNAME,
				PhiliusWSException.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link SubmitSequenceResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.philius.yeastrc.org/", name = "submitSequenceResponse")
	public JAXBElement<SubmitSequenceResponse> createSubmitSequenceResponse(
			SubmitSequenceResponse value) {
		return new JAXBElement<SubmitSequenceResponse>(
				_SubmitSequenceResponse_QNAME, SubmitSequenceResponse.class,
				null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link SubmitSequence }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.philius.yeastrc.org/", name = "submitSequence")
	public JAXBElement<SubmitSequence> createSubmitSequence(SubmitSequence value) {
		return new JAXBElement<SubmitSequence>(_SubmitSequence_QNAME,
				SubmitSequence.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link IsJobDone }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://ws.philius.yeastrc.org/", name = "isJobDone")
	public JAXBElement<IsJobDone> createIsJobDone(IsJobDone value) {
		return new JAXBElement<IsJobDone>(_IsJobDone_QNAME, IsJobDone.class,
				null, value);
	}

}
