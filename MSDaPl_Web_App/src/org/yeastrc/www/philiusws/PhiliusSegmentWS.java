package org.yeastrc.www.philiusws;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for philiusSegmentWS complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="philiusSegmentWS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="end" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sequenceID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sp" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="spSegments" type="{http://ws.philius.yeastrc.org/}philiusSPSegmentWS" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="start" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="typeConfidence" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="typeString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "philiusSegmentWS", propOrder = { "end", "id", "sequenceID",
		"sp", "spSegments", "start", "type", "typeConfidence", "typeString" })
public class PhiliusSegmentWS {

	protected int end;
	protected int id;
	protected int sequenceID;
	protected boolean sp;
	@XmlElement(nillable = true)
	protected List<PhiliusSPSegmentWS> spSegments;
	protected int start;
	protected int type;
	protected BigDecimal typeConfidence;
	protected String typeString;

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
	 * Gets the value of the sequenceID property.
	 * 
	 */
	public int getSequenceID() {
		return sequenceID;
	}

	/**
	 * Sets the value of the sequenceID property.
	 * 
	 */
	public void setSequenceID(int value) {
		this.sequenceID = value;
	}

	/**
	 * Gets the value of the sp property.
	 * 
	 */
	public boolean isSp() {
		return sp;
	}
	
	public boolean isTransMembraneHelix() {
		return this.type == 3;
	}
	
	public boolean isCytoplasmic() {
		return type == 2;
	}
	
	public boolean isNonCytoplasmic() {
		return type == 1;
	}

	/**
	 * Sets the value of the sp property.
	 * 
	 */
	public void setSp(boolean value) {
		this.sp = value;
	}

	/**
	 * Gets the value of the spSegments property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the spSegments property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getSpSegments().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link PhiliusSPSegmentWS }
	 * 
	 * 
	 */
	public List<PhiliusSPSegmentWS> getSpSegments() {
		if (spSegments == null) {
			spSegments = new ArrayList<PhiliusSPSegmentWS>();
		}
		return this.spSegments;
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

	/**
	 * Gets the value of the type property.
	 * 
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 * 
	 */
	public void setType(int value) {
		this.type = value;
	}

	/**
	 * Gets the value of the typeConfidence property.
	 * 
	 * @return possible object is {@link BigDecimal }
	 * 
	 */
	public BigDecimal getTypeConfidence() {
		return typeConfidence;
	}

	/**
	 * Sets the value of the typeConfidence property.
	 * 
	 * @param value
	 *            allowed object is {@link BigDecimal }
	 * 
	 */
	public void setTypeConfidence(BigDecimal value) {
		this.typeConfidence = value;
	}

	/**
	 * Gets the value of the typeString property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTypeString() {
		return typeString;
	}

	/**
	 * Sets the value of the typeString property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTypeString(String value) {
		this.typeString = value;
	}

}
