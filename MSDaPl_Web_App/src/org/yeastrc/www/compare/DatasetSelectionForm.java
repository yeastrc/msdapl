/**
 * DatasetSelectionForm.java
 * @author Vagisha Sharma
 * Sep 6, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForm;

/**
 * 
 */
public class DatasetSelectionForm extends ActionForm {

    private List<ProteinferRunFormBean> allRuns = new ArrayList<ProteinferRunFormBean>();
    
    private boolean groupProteins = true;
    
    
    public boolean getGroupProteins() {
        return groupProteins;
    }

    public void setGroupProteins(boolean groupProteins) {
        this.groupProteins = groupProteins;
    }

    //-----------------------------------------------------------------------------
    // Protein inference datasets
    //-----------------------------------------------------------------------------
    public ProteinferRunFormBean getProteinferRun(int index) {
        while(index >= allRuns.size())
            allRuns.add(new ProteinferRunFormBean());
        return allRuns.get(index);
    }
    
    public void setProteinferRunList(List <ProteinferRunFormBean> piRuns) {
        this.allRuns = piRuns;
    }
    
    public List <ProteinferRunFormBean> getProteinferRunList() {
        return allRuns;
    }
    
    public List<Integer> getSelectedProteinferRunIds() {
        List<Integer> ids = new ArrayList<Integer>();
        for(ProteinferRunFormBean run: allRuns) {
            if(run != null && run.isSelected())
                ids.add(run.getRunId());
        }
        return ids;
    }
}
