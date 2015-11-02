package org.yeastrc.www.philiusws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for philiusSPSegmentWS complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="philiusSPSegmentWS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="end" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="segmentID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="spSegmentType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="spSegmentTypeString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="start" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "philiusSPSegmentWS", propOrder = { "end", "id", "segmentID",
		"spSegmentType", "spSegmentTypeString", "start" })
public class PhiliusSPSegmentWS {

	protected int end;
	protected int id;
	protected int segmentID;
	protected int spSegmentType;
	protected String spSegmentTypeString;
	protected int start;

	/**
	 * Gets the value of the end property.
	 * 
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Sets the value of the end property.
	 * 
	 */
	public void setEnd(int value) {
		this.end = value;
	}

	/**
	 * Gets the value of the id property.
	 * 
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 */
	public void setId(int value) {
		this.id = value;
	}

	/**
	 * Gets the value of the segmentID property.
	 * 
	 */
	public int getSegmentID() {
		return segmentID;
	}

	/**
	 * Sets the value of the segmentID property.
	 * 
	 */
	public void setSegmentID(int value) {
		this.segmentID = value;
	}

	/**
	 * Gets the value of the spSegmentType property.
	 * 
	 */
	public int getSpSegmentType() {
		return spSegmentType;
	}

	/**
	 * Sets the value of the spSegmentType property.
	 * 
	 */
	public void setSpSegmentType(int value) {
		this.spSegmentType = value;
	}

	/**
	 * Gets the value of the spSegmentTypeString property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSpSegmentTypeString() {
		return spSegmentTypeString;
	}

	/**
	 * Sets the value of the spSegmentTypeString property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSpSegmentTypeString(String value) {
		this.spSegmentTypeString = value;
	}

	/**
	 * Gets the value of the start property.
	 * 
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Sets the value of the start property.
	 * 
	 */
	public void setStart(int value) {
		this.start = value;
	}

}
