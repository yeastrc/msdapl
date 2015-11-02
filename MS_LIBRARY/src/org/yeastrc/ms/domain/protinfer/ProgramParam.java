/**
 * ProgramParam.java
 * @author Vagisha Sharma
 * Feb 7, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer;


/**
 * 
 */
public final class ProgramParam {

    public static enum TYPE {STRING, DOUBLE, INTEGER, BOOLEAN, CHOICE};
    public static enum SCORE {XCorr, DeltaCN, PrimaryScore};
    
    private String name;
    private String displayName;
    private String description;
    private String defaultValue;
    private TYPE type = TYPE.DOUBLE;
    private String[] values;

    private ParamValidator validator = null;
    
    ProgramParam(TYPE type, String name, String displayName, 
            String defaultVal, String[] values,
            String description) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.defaultValue = defaultVal;
        if(values != null)
            this.values = values;
        else
            values = new String[0];
    }
    
    public void setValidator(ParamValidator validator) {
        this.validator = validator;
    }
    
    public ParamValidator getValidator() {
        return validator;
    }

    public String getName() {
        return name;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getDescription() {
        return description;
    }

    public TYPE getType() {
        return this.type;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String[] getValues() {
        return this.values;
    }
    
    public boolean validate(String value) {
        if(validator == null)
            return true;
        return validator.validate(value);
    }
    
    public static class ParamMaker {
        
        public static ProgramParam makeMaxFDRParam() {
            ProgramParam param = new ProgramParam(TYPE.DOUBLE, 
                  "maxFDR", "Max. FDR", 
                  "0.05", null,
                  "This parameter sets the maximum FDR that the score of each peptide identification must meet");
            DoubleValidator validator = new DoubleValidator();
            validator.minVal = 0.0;
            validator.maxVal = 1.0;
            param.setValidator(validator);
            return param;
        }
        
        public static ProgramParam makeFDRScoreParam(SCORE defaultScore, SCORE[] options) {
            String[] opts = new String[options.length];
            for(int i = 0; i < options.length; i++)
                opts[i] = options[i].name();
            
            return new ProgramParam(TYPE.CHOICE, 
                  "useScore", "Use Score", 
                  defaultScore.name(), opts,
                  "This parameter sets the score that will be used for calculating FDR");
        }
        
        public static ProgramParam makeFDRFormulaParam() {
            return new ProgramParam(TYPE.CHOICE,
                  "FDRFormula", "FDR Formula",
                  "2R/(F+R)", new String[]{"2R/(F+R)", "R/F"},
                  "Formula used for calculating FDR. R = # decoy hits; F = # target hits");
        }
        
        public static ProgramParam makeDecoyPrefixParam() {
            ProgramParam param = new ProgramParam(TYPE.STRING, 
                  "decoyPrefix", "Decoy Prefix", 
                  "Reverse_", null,
                  "Prefix used to identify decoy protein accessions");
            param.setValidator(new ParamValidator() {
                public boolean validate(String value) {
                    return (value != null && value.length() > 0);
                }
                public boolean allowsNull() {
                    return false;
                }});
            return param;
        }
        
        public static ProgramParam makePeptideDefParam() {
            return new ProgramParam(TYPE.CHOICE,
                  "PeptDef", "Peptide Definition",
                  "Sequence + Modifications + Charge", new String[]{"Sequence", 
                                           "Sequence + Modifications", 
                                           "Sequence + Charge",
                                           "Sequence + Modifications + Charge"},
                  "These options determine what uniquely defines a peptide");
        }
        
        public static ProgramParam makeMinPeptParam() {
            ProgramParam param = new ProgramParam(TYPE.INTEGER, "minPept", "Min. Peptides", "1", null, 
                    "Minimum number of peptides required for a protein(group) to be included in the analysis.");
            IntegerValidator validator = new IntegerValidator();
            validator.minVal = 0;
            param.setValidator(validator);
            return param;
        }
        
        public static ProgramParam makeMinUniqPeptParam() {
            ProgramParam param = new ProgramParam(TYPE.INTEGER, "minUniqePept", "Min. Unique Peptides", "0", null, 
                    "Minimum number of unique peptides required for a protein(group) to be included in the analysis.");
            IntegerValidator validator = new IntegerValidator();
            validator.minVal = 0;
            param.setValidator(validator);
            return param;
        }

        public static ProgramParam makeMinCoverageParam() {
            ProgramParam param = new ProgramParam(TYPE.DOUBLE, "coverage", "Min. Coverage(%)", "0", null, 
                    "Minimum sequence coverage required for a protein to be included in the analysis.");
            DoubleValidator validator = new DoubleValidator();
            validator.minVal = 0.0;
            validator.maxVal = 100.0;
            param.setValidator(validator);
            return param;
        }

        public static ProgramParam makeMinPeptLengthParam() {
            ProgramParam param = new ProgramParam(TYPE.INTEGER, "minPeptLen", "Min. Peptide Length", "5", null, 
                    "Minimum length required for a peptide to be included in the analysis.");
            IntegerValidator validator = new IntegerValidator();
            validator.minVal = 0;
            param.setValidator(validator);
            return param;
        }
//      new ProgramParam(TYPE.INTEGER, "minPeptSpectra", "Min. Spectra / peptide", "1", null, "Minimum number of spectrum matches required for a peptide to be included in the analysis."),
        public static ProgramParam makeRemoveAmbigSpectraParam() {
            return new ProgramParam(TYPE.BOOLEAN, 
              "removeAmbigSpectra", "Remove Ambiguous Spectra", 
              "true", null,
              "If checked, spectra with > 1 peptide spectrum matches (PSMs), that pass through the filter criteria, will be removed from analysis.");
        }
        
        public static ProgramParam makeRefreshPeptideProteinMatchParam() {
            return new ProgramParam(TYPE.BOOLEAN, 
              "refreshPeptideProteinMatches", "Refresh Protein Matches", 
              "true", null,
              "If checked, protein matches for peptides will be re-calculated. Otherwise matches reported in SQT files will be used.");
        }
        
        public static ProgramParam makeDoItoLSubstitutionParam() {
            return new ProgramParam(TYPE.BOOLEAN, 
              "doItoLSubstitution", "Allow I/L substitutions", 
              "false", null,
              "If checked, I/L substitution will be allowed when determining protein matches for peptides. Ignored if \"Refresh Peptide Protein Matches\" is unchecked.");
        }
        
        public static ProgramParam makeRemoveAsterisksParam() {
            return new ProgramParam(TYPE.BOOLEAN, 
              "removeAsterisks", "Remove Asterisks (*)", 
              "false", null,
              "If checked, '*' characters are removed from sequences before determining protein matches for peptides. This option should be checked for data searched with versions of Sequest that ignored '*' characters in protein sequences");
        }
        
        public static ProgramParam makeCalculateAllNsafParam() {
            return new ProgramParam(TYPE.BOOLEAN, 
              "calculateAllNsaf", "Calculate NSAF for all proteins", 
              "false", null,
              "If checked, NSAF will be calculated for both parsimonious and non-parsimonious proteins. " +
              "By default, NSAF is calculated only for parsimonious proteins, and a value of -1 is reported for non-parsimonious proteins.");
        }
    }
    
    public static abstract class ParamValidator {
        public abstract boolean validate(String value);
        public abstract boolean allowsNull();
    }
    
    public static class DoubleValidator extends ParamValidator {
        private double minVal = Double.MIN_VALUE;
        private double maxVal = Double.MAX_VALUE;
        private boolean allowNull = false;
        public void setMinVal(double minVal) {this.minVal = minVal;}
        public double getMinVal() {return this.minVal;}
        public double getMaxVal() {return this.maxVal;}
        public void setMaxVal(double maxVal) {this.maxVal = maxVal;}
        public boolean validate(String value) {
            double val = 0;
            try {
                val = Double.parseDouble(value);
            }
            catch(NumberFormatException e) {return false;}
            return val >= minVal && val <= maxVal;
        }
        public boolean allowsNull() {
            return allowNull;
        }
        public void setAllowsNull(boolean allowNull) {
            this.allowNull = allowNull;
        }
    }
    
    public static class IntegerValidator extends ParamValidator {
        private int minVal = Integer.MIN_VALUE;
        private int maxVal = Integer.MAX_VALUE;
        private boolean allowNull = false;
        public void setMinVal(int minVal) {this.minVal = minVal;}
        public void setMaxVal(int maxVal) {this.maxVal = maxVal;}
        public int getMinVal() {return this.minVal;}
        public int getMaxVal() {return this.maxVal;}
        public boolean validate(String value) {
            int val = 0;
            try {
                val = Integer.parseInt(value);
            }
            catch(NumberFormatException e) {return false;}
            return val >= minVal && val <= maxVal;
        }
        public boolean allowsNull() {
            return allowNull;
        }
        public void setAllowsNull(boolean allowNull) {
            this.allowNull = allowNull;
        }
    }
}


