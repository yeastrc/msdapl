//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.01 at 02:54:01 PM PDT 
//


package org.yeastrc.ms.writer.mzidentml.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The role that a Contact plays in an organization or with respect to the associating class. A Contact may have several Roles within scope, and as such,
 * associations to ContactRole allow the use of a Contact in a certain manner. Examples
 * might include a provider, or a data analyst. 
 * 
 * <p>Java class for ContactRoleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContactRoleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Role" type="{http://psidev.info/psi/pi/mzIdentML/1.1}RoleType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="contact_ref" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContactRoleType", propOrder = {
    "role"
})
public class ContactRoleType {

    @XmlElement(name = "Role", required = true)
    protected RoleType role;
    @XmlAttribute(name = "contact_ref", required = true)
    protected String contactRef;

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link RoleType }
     *     
     */
    public RoleType getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoleType }
     *     
     */
    public void setRole(RoleType value) {
        this.role = value;
    }

    /**
     * Gets the value of the contactRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactRef() {
        return contactRef;
    }

    /**
     * Sets the value of the contactRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactRef(String value) {
        this.contactRef = value;
    }

}
