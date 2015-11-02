/**
 * ProteinProphetParams.java
 * @author Vagisha Sharma
 * Jul 16, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;


/**
 * 
 */
public class ProteinProphetParam {

    private int id;
    private int pinferId;
    private String name;
    private String value;
    
    public ProteinProphetParam() {}
    
    public ProteinProphetParam(String name, String value){
        this.name = name;
        this.value = value;
    }
    
    public ProteinProphetParam (String name, String value, int pinferId) {
        this(name, value);
        this.pinferId = pinferId;
    }
    
    public String toString() {
        return "Name: "+name+"; Value: "+value;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public int getProteinferId() {
        return pinferId;
    }
    public void setProteinferId(int pinferId) {
        this.pinferId = pinferId;
    }
}
