package org.yeastrc.ms.domain.search;

public enum Program {

    SEQUEST("SEQUEST"),
    EE_NORM_SEQUEST("EE-normalized SEQUEST"),
    NORM_SEQUEST("SEQUEST-NORM"),
    COMET("Comet"),
    MASCOT("MASCOT"),
    XTANDEM("XTANDEM"),
    TIDE("Tide"),
    PERCOLATOR("Percolator"),
    PEPTIDE_PROPHET("PeptideProphet"),
    PROLUCID("ProLuCID"),
    PEPPROBE("PEP_PROBE"),
    BIBLIOSPEC("BiblioSpec"),
    UNKNOWN("Unknown");
 
    private String displayName;
    
    private Program(String displayName) {
        this.displayName = displayName;
    }
    
    public String displayName() {
        return displayName;
    }
    
    public static Program programForFileFormat(SearchFileFormat format) {
        if (SearchFileFormat.SQT_SEQ == format)
            return Program.SEQUEST;
        else if (SearchFileFormat.SQT_EENSEQ == format)
            return Program.EE_NORM_SEQUEST;
        else if (SearchFileFormat.SQT_NSEQ == format)
            return Program.NORM_SEQUEST;
        else if (SearchFileFormat.SQT_PLUCID == format)
            return Program.PROLUCID;
        else if (SearchFileFormat.SQT_TIDE == format)
            return Program.TIDE;
        else if (SearchFileFormat.SQT_PERC == format)
            return Program.PERCOLATOR;
        else if (SearchFileFormat.SQT_PPROBE == format)
            return Program.PEPPROBE;
        else if (SearchFileFormat.SQT_BIBLIO == format)
            return Program.BIBLIOSPEC;
        else if (SearchFileFormat.PEPXML == format)
            return Program.PEPTIDE_PROPHET;
        else if (SearchFileFormat.PEPXML_SEQ == format)
            return Program.SEQUEST;
        else if (SearchFileFormat.PEPXML_MASCOT == format)
            return Program.MASCOT;
        else if (SearchFileFormat.PEPXML_XTANDEM == format)
            return Program.XTANDEM;
        else
            return Program.UNKNOWN;
    }
    
    public static Program instance(String prog) {
    	
    	Program[] programs = values();
    	for(Program program: programs) {
    		
    		if(program.name().equalsIgnoreCase(prog))
    			return program;
    	}
    	
    	return Program.UNKNOWN;
    }
    
    public static boolean isSearchProgram(Program program) {
        if(Program.isSequest(program) ||
           program == MASCOT ||
           program == XTANDEM ||
           program == PROLUCID || program == PEPPROBE||
           program == BIBLIOSPEC)
            return true;
        return false;
    }
    
    public static boolean isAnalysisProgram(Program program) {
        if(program == PERCOLATOR || program == PEPTIDE_PROPHET)
            return true;
        return false;
    }
    
    public static boolean isSequest(Program program) {
    	return (program == SEQUEST || program == EE_NORM_SEQUEST || program == NORM_SEQUEST);
    }
}
