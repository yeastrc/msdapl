package org.yeastrc.www.proteinfer.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.ProgramParam;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProgramParam.ParamMaker;
import org.yeastrc.ms.domain.protinfer.ProgramParam.TYPE;

public class ProgramParameters {

    private String programName;
    private String progDisplayName;
    private List<Param> paramList;
    
    public ProgramParameters() {
        paramList = new ArrayList<Param>();
    }
    
    public ProgramParameters(ProteinInferenceProgram program) {
        this.programName = program.name();
        this.progDisplayName = program.getDisplayName();
        this.paramList = new ArrayList<Param>(program.getProgramParams().length);
        for(ProgramParam p: program.getProgramParams())
            this.addParam(new Param(p));
    }
    
    public void removeParam(String paramName) {
    	Iterator<Param> iter = paramList.iterator();
    	while(iter.hasNext()) {
    		Param p = iter.next();
    		if(p.getName().equals(paramName))
    			iter.remove();
    	}
    }
    public String getProgramName() {
        return programName;
    }
    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramDisplayName() {
        return progDisplayName;
    }
    public void setProgramDisplayName(String progDisplayName) {
        this.progDisplayName = progDisplayName;
    }
    
    public List<Param> getParamList() {
        return paramList;
    }
    public void setParamList(List<Param> paramList) {
        this.paramList = paramList;
    }
    // to be used by struts indexed properties
    public Param getParam(int index) {
        while(index >= paramList.size())
            paramList.add(new Param());
        return paramList.get(index);
    }
    public void addParam(Param param) {
        paramList.add(param);
    }
    
    public static boolean validateParams(ProgramParameters params, StringBuilder errorMessage) {
        String programName = params.getProgramName();
        ProteinInferenceProgram piProgram = ProteinInferenceProgram.getProgramForName(programName);
        boolean valid = true;
        for(Param param: params.getParamList()) {
            ProgramParam progParam = piProgram.getParamForName(param.getName());
            if(progParam == null) {
                errorMessage.append("No parameter found with name: "+param.getDisplayName());
                return false;
            }
            if(!progParam.validate(param.getValue())) {
                errorMessage.append("Invalid value for param: "+param.getDisplayName()+"\n");
                valid = false;
            }
        }
        return valid;
    }
    
    public static void updateParamDefaults(ProgramParameters params) {
        String programName = params.getProgramName();
        ProteinInferenceProgram piProgram = ProteinInferenceProgram.getProgramForName(programName);
        for(Param param: params.getParamList()) {
            ProgramParam progParam = piProgram.getParamForName(param.getName());
            if(progParam != null) {
                param.updateDefaults(progParam);
            }
        }
    }
    
    public static final class Param {
        private String name;
        private String displayName;
        private String type;
        private String tooltip;
        private String notes;
        private String value;
        private String[] values; // used for radiobox / list options
        private boolean newOption = false;
        
        public boolean isNewOption() {
			return newOption;
		}

		public void setNewOption(boolean newOption) {
			this.newOption = newOption;
		}

		public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String[] getOptions() {
            return values;
        }
        
        public Param(){}
        
        public Param(ProgramParam param) {
            this.name = param.getName();
            this.value = param.getDefaultValue();
            
            setDefaults(param);
        }

        private void setDefaults(ProgramParam param) {
            this.displayName = param.getDisplayName();
            this.tooltip = param.getDescription();
            this.values = param.getValues();
            if(param.getType() == TYPE.BOOLEAN)
                this.type = "checkbox";
            else if(param.getType() == TYPE.CHOICE)
                this.type = "radio";
            else 
                this.type="text";
//            if(param.getName().equals(ParamMaker.makeRefreshPeptideProteinMatchParam().getName()) ||
//               param.getName().equals(ParamMaker.makeDoItoLSubstitutionParam().getName()) ||
//               param.getName().equals("peptide_qval_percolator") ||
//               param.getName().equals("peptide_pep_percolator")) {
//            	this.newOption = true;
//            }
        }
        
        public void updateDefaults(ProgramParam param) {
            setDefaults(param);
        }
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getDisplayName() {
            return displayName;
        }
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getTooltip() {
            return tooltip;
        }
        public void setTooltip(String tooltip) {
            this.tooltip = tooltip;
        }
    }
    
}
