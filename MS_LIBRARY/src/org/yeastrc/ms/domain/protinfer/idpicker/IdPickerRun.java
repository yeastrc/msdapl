package org.yeastrc.ms.domain.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.GenericProteinferRun;

public class IdPickerRun extends GenericProteinferRun<IdPickerInput> {

    
    private List<IdPickerParam> params;
    
    public IdPickerRun() {
        super();
        params = new ArrayList<IdPickerParam>();
    }
    
    public List<IdPickerParam> getParams() {
        return params;
    }

    public List<IdPickerParam> getSortedParams() {
        Collections.sort(params, new Comparator<IdPickerParam>(){
            public int compare(IdPickerParam o1, IdPickerParam o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }});
        return params;
    }
    
    public void setParams(List<IdPickerParam> params) {
        this.params = params;
    }
    
    public IdPickerInput getInputSummaryForRunSearch(int runSearchId) {
        for(IdPickerInput input: this.getInputList()) {
            if(input.getInputId() == runSearchId) {
                return input;
            }
        }
        return null;
    }
}
