/**
 * ProteinProphetProteinGroup.java
 * @author Vagisha Sharma
 * Aug 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetGroup;

/**
 * 
 */
public class WProteinProphetProteinGroup {

    private List<WProteinProphetIndistProteinGroup> indistinguishableProteins;
    private ProteinProphetGroup proteinProphetGroup;
    
    public WProteinProphetProteinGroup(ProteinProphetGroup proteinProphetGroup,
            List<WProteinProphetIndistProteinGroup> groupProteins) {
        
        this.proteinProphetGroup = proteinProphetGroup;
        
       if(groupProteins != null)
           indistinguishableProteins = groupProteins;
       if(groupProteins == null)
           groupProteins = new ArrayList<WProteinProphetIndistProteinGroup>(0);
    }

    public double getGroupProbability() {
        return this.proteinProphetGroup.getProbability();
    }
    
    public int getProteinProphetGroupId() {
        return this.proteinProphetGroup.getId();
    }
    
    public int getProteinProphetGroupNumber() {
        return this.proteinProphetGroup.getGroupNumber();
    }
    
    public int getIndistinguishableProteinGroupCount() {
        return indistinguishableProteins.size();
    }
    
    public int getProteinCount() {
        int count = 0;
        for(WProteinProphetIndistProteinGroup iGroup: indistinguishableProteins)
            count += iGroup.getProteinCount();
        return count;
    }
    
    public List<WProteinProphetIndistProteinGroup> getIndistinguishableProteinGroups() {
        return indistinguishableProteins;
    }
}
