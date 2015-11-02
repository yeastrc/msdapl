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
 * Java class for philiusSequenceAnnotationWS complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="philiusSequenceAnnotationWS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hasSp" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="hasTm" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="segments" type="{http://ws.philius.yeastrc.org/}philiusSegmentWS" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sequence" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="spProbabilitySum" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="tmProbabilitySum" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="topologyConfidence" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="typeScore" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
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
@XmlType(name = "philiusSequenceAnnotationWS", propOrder = { "hasSp", "hasTm",
		"segments", "sequence", "spProbabilitySum", "tmProbabilitySum",
		"topologyConfidence", "type", "typeScore", "typeString" })
public class PhiliusSequenceAnnotationWS {

	protected boolean hasSp;
	protected boolean hasTm;
	@XmlElement(nillable = true)
	protected List<PhiliusSegmentWS> segments;
	protected String sequence;
	protected BigDecimal spProbabilitySum;
	protected BigDecimal tmProbabilitySum;
	protected BigDecimal topologyConfidence;
	protected int type;
	protected BigDecimal typeScore;
	protected String typeString;
	

	/**
	 * Gets the value of the hasSp property.
	 * 
	 */
	public boolean isHasSp() {
		return hasSp;
	}

	/**
	 * Sets the value of the hasSp property.
	 * 
	 */
	public void setHasSp(boolean value) {
		this.hasSp = value;
	}

	/**
	 * Gets the value of the hasTm property.
	 * 
	 */
	public boolean isHasTm() {
		return hasTm;
	}

	/**
	 * Sets the value of the hasTm property.
	 * 
	 */
	public void setHasTm(boolean value) {
		this.hasTm = value;
	}

	/**
	 * Gets the value of the segments property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the segments property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getSegments().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link PhiliusSegmentWS }
	 * 
	 * 
	 */
	public List<PhiliusSegmentWS> getSegments() {
		if (segments == null) {
			segments = new ArrayList<PhiliusSegmentWS>();
		}
		return this.segments;
	}

	/**
	 * Gets the value of the sequence property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * Sets the value of the sequence property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSequence(String value) {
		this.sequence = value;
	}

	/**
	 * Gets the value of the spProbabilitySum property.
	 * 
	 * @return possible object is {@link BigDecimal }
	 * 
	 */
	public BigDecimal getSpProbabilitySum() {
		return spProbabilitySum;
	}

	/**
	 * Sets the value of the spProbabilitySum property.
	 * 
	 * @param value
	 *            allowed object is {@link BigDecimal }
	 * 
	 */
	public void setSpProbabilitySum(BigDecimal value) {
		this.spProbabilitySum = value;
	}

	/**
	 * Gets the value of the tmProbabilitySum property.
	 * 
	 * @return possible object is {@link BigDecimal }
	 * 
	 */
	public BigDecimal getTmProbabilitySum() {
		return tmProbabilitySum;
	}

	/**
	 * Sets the value of the tmProbabilitySum property.
	 * 
	 * @param value
	 *            allowed object is {@link BigDecimal }
	 * 
	 */
	public void setTmProbabilitySum(BigDecimal value) {
		this.tmProbabilitySum = value;
	}

	/**
	 * Gets the value of the topologyConfidence property.
	 * 
	 * @return possible object is {@link BigDecimal }
	 * 
	 */
	public BigDecimal getTopologyConfidence() {
		return topologyConfidence;
	}

	/**
	 * Sets the value of the topologyConfidence property.
	 * 
	 * @param value
	 *            allowed object is {@link BigDecimal }
	 * 
	 */
	public void setTopologyConfidence(BigDecimal value) {
		this.topologyConfidence = value;
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
	 * Gets the value of the typeScore property.
	 * 
	 * @return possible object is {@link BigDecimal }
	 * 
	 */
	public BigDecimal getTypeScore() {
		return typeScore;
	}

	/**
	 * Sets the value of the typeScore property.
	 * 
	 * @param value
	 *            allowed object is {@link BigDecimal }
	 * 
	 */
	public void setTypeScore(BigDecimal value) {
		this.typeScore = value;
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
