/**
 * ProteinProphetGroup.java
 * @author Vagisha Sharma
 * Jul 16, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ProteinProphetGroup {

    private double probability = -1.0;
    private int groupNumber = -1;
    private int proteinferId = 0;
    private int id;

    private List<ProteinProphetProtein> proteinList;
    
    
    public ProteinProphetGroup() {
        proteinList = new ArrayList<ProteinProphetProtein>();
    }
    
    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public List<ProteinProphetProtein> getProteinList() {
        return proteinList;
    }

    public void addProtein(ProteinProphetProtein protein) {
        this.proteinList.add(protein);
    }

    public int getProteinferId() {
        return proteinferId;
    }

    public void setProteinferId(int proteinferId) {
        this.proteinferId = proteinferId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
