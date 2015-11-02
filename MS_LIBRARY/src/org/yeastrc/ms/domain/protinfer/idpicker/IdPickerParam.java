package org.yeastrc.ms.domain.protinfer.idpicker;

public class IdPickerParam {

    private int id;
    private int pinferId;
    private String name;
    private String value;
    
    public IdPickerParam() {}
    
    public IdPickerParam(String name, String value){
        this.name = name;
        this.value = value;
    }
    
    public IdPickerParam (String name, String value, int pinferId) {
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
