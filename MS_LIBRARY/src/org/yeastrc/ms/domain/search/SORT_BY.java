/**
 * SORT_BY.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;


public enum SORT_BY {
    
    ID("ID", "id"),
    SCAN("Scan", "startScanNumber"), 
    CHARGE("Charge", "charge"), 
    MASS("Obs. Mass", "observedMass"), 
    RT("RT", "retentionTime", "Retention Time"), 
    AREA("Area", "area"),
    PEPTIDE("Peptide", "peptide"),
    PROTEIN("Protein", "accession"),
    FILE_SEARCH("File", "runSearchID"),
    FILE_ANALYSIS("File", "runSearchAnalysisID"),

    // Prolucid specific
    PRIMARY_SCORE_RANK("PrimaryScoreRank", "PrimaryScoreRank"),
    PRIMARY_SCORE("PrimaryScore", "PrimaryScore"),

    // Sequest specific
    XCORR("XCorr", "XCorr"),
    XCORR_RANK("XCr", "XCorrRank", "XCorr Rank"),
    SP("Sp", "sp"),
    DELTACN("dCN", "deltaCN", "DeltaCN"),
    EVAL("e-value", "evalue"),
    CALC_MASS_SEQ("Calc. Mass", "calculatedMass", "Calculated Mass"),
    
    // Mascot specific
    ION_SCORE("IonScore", "ionScore"),
    IDENTITY_SCORE("IdScore", "identityScore"),
    HOMOLOGY_SCORE("HomolScore", "homologyScore"),
    MASCOT_EXPECT("Expect", "expect"),
    STAR("Star", "star"),
    MASCOT_RANK("Rank", "rank"),
    
    // Xtandem specific
    HYPER_SCORE("HyperScore", "hyperscore"),
    NEXT_SCORE("NextScore", "nextscore"),
    B_SCORE("B-Score", "bscore"),
    Y_SCORE("Y-Score", "yscore"),
    XTANDEM_EXPECT("Expect", "expect"),
    XTANDEM_RANK("Rank", "rank"),
    
    // Percolator specific
    QVAL("qvalue", "qvalue"),
    PVAL("pvalue", "pvalue"),
    PEP("PEP", "pep", "Posterior Error Probability"),
    QVAL_PEPT("qvalue(P)", "qvalue", "Peptide-level qvalue"),
    PEP_PEPT("PEP(P)", "pep", "Peptide-level Posterior Error Probability"),
    DS("DS", "discriminantScore", "Discriminant Score"),
    P_RT("Predict. RT", "predictedRetentionTime", "Predicted Retention Time"),
    NUM_PSM("#PSMs", "numPsms"),

    // PeptideProphet specific
    PEPTP_PROB("Probability", "probability");
    
    private String displayName;
    private String columnName;
    private String tooltip;

    private SORT_BY(String displayName, String columnName, String tooltip) {
        this.displayName = displayName;
        this.columnName = columnName;
        this.tooltip = tooltip;
    }
    
    private SORT_BY(String displayName, String columnName) {
       this(displayName, columnName, null);
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    public String getTooltip() {
    	return tooltip;
    }

    public static SORT_BY getSortByForName(String name) {
        if(name == null)
            return null;
        SORT_BY[] arr = SORT_BY.values();
        for(SORT_BY sortBy: arr) {
            if(sortBy.name().equalsIgnoreCase(name)) 
                return sortBy;
        }
        return null;
    }

    public static SORT_BY defaultSortBy() {
        return ID;
    }
    
    public static boolean isScanRelated(SORT_BY sortBy) {
        return sortBy == SCAN || sortBy == RT;
    }
    
    public static boolean isSearchRelated(SORT_BY sortBy) {
        return sortBy == CHARGE || sortBy == MASS || sortBy == PEPTIDE;
    }
    
    public static boolean isPercolatorRelated(SORT_BY sortBy) {
        return sortBy == QVAL || sortBy == PEP || sortBy == P_RT;
    }
    
    public static boolean isSequestRelated(SORT_BY sortBy) {
        return sortBy == XCORR || sortBy == DELTACN || sortBy == SP || sortBy == EVAL || sortBy == CALC_MASS_SEQ;
    }
    
    public static boolean isMascotRelated(SORT_BY sortBy) {
        return sortBy == CALC_MASS_SEQ || sortBy == ION_SCORE || sortBy == IDENTITY_SCORE || sortBy == HOMOLOGY_SCORE || sortBy == MASCOT_EXPECT || sortBy == STAR;
    }
    
    public static boolean isXtandemRelated(SORT_BY sortBy) {
        return sortBy == HYPER_SCORE || sortBy == NEXT_SCORE || sortBy == B_SCORE || sortBy == Y_SCORE || sortBy == MASCOT_EXPECT;
    }
    
    public static boolean isPeptideProphetRelated(SORT_BY sortBy) {
        return sortBy == PEPTP_PROB;
    }
}
