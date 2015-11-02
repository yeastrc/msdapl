/**
 * SearchParamMatcher.java
 * @author Vagisha Sharma
 * Oct 9, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.pepxml;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;

/**
 * 
 */
public class SearchParamMatcher {

    
    private StringBuilder errorMessage;
    
    private boolean enzymesMatch = false;
    private boolean databasesMatch = false;
    private boolean dynamicResidueModsMatch = false;
    private boolean staticResidueModsMatch = false;
    private boolean dynamicTerminalModsMatch = false;
    private boolean staticTerminalModsMatch = false;
    
    private static final Logger log = Logger.getLogger(SearchParamMatcher.class.getName());
    
    public boolean matchSearchParams(MsSearch search, MsSearchIn parsedSearch, String fileName) {
        
        errorMessage = new StringBuilder();
        
        // match enzyme information
        List<MsEnzyme> uploadedEnzymes = search.getEnzymeList();
        List<MsEnzymeIn> enzymes = parsedSearch.getEnzymeList();
        enzymesMatch = matchEnzymes(uploadedEnzymes, enzymes, fileName);
        
        // match database information
        List<MsSearchDatabase> uploadedDbs = search.getSearchDatabases();
        List<MsSearchDatabaseIn> databases = parsedSearch.getSearchDatabases();
        databasesMatch = matchDatabases(uploadedDbs, databases, fileName);
        
        // match dynamic residue modification information
        List<MsResidueModification> uploadedDynaResMods = search.getDynamicResidueMods();
        List<MsResidueModificationIn> dynaResMods = parsedSearch.getDynamicResidueMods();
        dynamicResidueModsMatch = matchResidueModifictions(uploadedDynaResMods, dynaResMods, fileName, true);
        
        // match static residue modification information
        List<MsResidueModification> uploadedStaticResMods = search.getStaticResidueMods();
        List<MsResidueModificationIn> staticResMods = parsedSearch.getStaticResidueMods();
        staticResidueModsMatch = matchResidueModifictions(uploadedStaticResMods, staticResMods, fileName, false);
        
        // match dynamic terminal modification information
        List<MsTerminalModification> uploadedDynaTermMods = search.getDynamicTerminalMods();
        List<MsTerminalModificationIn> dynaTermMods = parsedSearch.getDynamicTerminalMods();
        dynamicTerminalModsMatch = matchTerminalModifictions(uploadedDynaTermMods, dynaTermMods, fileName, true);
        
        // match dynamic terminal modification information
        List<MsTerminalModification> uploadedStaticTermMods = search.getStaticTerminalMods();
        List<MsTerminalModificationIn> dynaStaticMods = parsedSearch.getStaticTerminalMods();
        staticTerminalModsMatch = matchTerminalModifictions(uploadedStaticTermMods, dynaStaticMods, fileName, false);
        
        return enzymesMatch && databasesMatch &&
                dynamicResidueModsMatch && staticResidueModsMatch &&
                dynamicTerminalModsMatch && staticTerminalModsMatch;
    }
    
    private boolean matchTerminalModifictions(
            List<MsTerminalModification> uploadedTermMods,
            List<MsTerminalModificationIn> termMods, String fileName, boolean dynamic) {

        String type = dynamic ? "dynamic" : "static";
        
        if(uploadedTermMods.size() != uploadedTermMods.size()) {
            errorMessage.append("Number of uploaded "+type+" terminal modifications: "+uploadedTermMods.size()+
                    " do not match # found in file "+fileName+": "+termMods.size()+"\n");
            return false;
        }
        
        if(uploadedTermMods.size() == 0)
            return true;
        
        Collections.sort(uploadedTermMods, new Comparator<MsTerminalModification>() {
            public int compare(MsTerminalModification o1, MsTerminalModification o2) {
                int val = o1.getModifiedTerminal().compareTo(o2.getModifiedTerminal());
                if(val != 0) return val;
                return o1.getModificationMass().compareTo(o2.getModificationMass());
            }});
        Collections.sort(uploadedTermMods, new Comparator<MsTerminalModificationIn>() {
            public int compare(MsTerminalModificationIn o1, MsTerminalModificationIn o2) {
                int val = o1.getModifiedTerminal().compareTo(o2.getModifiedTerminal());
                if(val != 0) return val;
                return o1.getModificationMass().compareTo(o2.getModificationMass());
            }});
        
        for(int i = 0; i < uploadedTermMods.size(); i++) {
            if(!matchTerminalModification(uploadedTermMods.get(i), uploadedTermMods.get(i))) {
                errorMessage.append(type+" terminal modification mismatch in file: "+fileName+"\n");
                return false;
            }
        }
        return true;
    }
    
    private boolean matchTerminalModification(MsTerminalModification mod1, MsTerminalModificationIn mod2) {
        
        if(mod1.getModifiedTerminal() != mod2.getModifiedTerminal()) {
            errorMessage.append("Mismatch: uploaded modified terminal: "+mod1.getModifiedTerminal()+"; in file: "+mod2.getModifiedTerminal()+"\n");
            return false;
        }
        if(!mod1.getModificationMass().equals(mod2.getModificationMass())) {
            errorMessage.append("Mismatch: uploaded modified terminal: "+mod1.getModificationMass()+"; in file: "+mod2.getModificationMass()+"\n");
            return false;
        }
        return true;
    }


    private boolean matchResidueModifictions(
            List<MsResidueModification> uploadedResMods,
            List<MsResidueModificationIn> resMods, String fileName, boolean dynamic) {
        
        String type = dynamic ? "dynamic" : "static";
        
        if(uploadedResMods.size() != resMods.size()) {
            errorMessage.append("Number of uploaded "+type+" residue modifications: "+uploadedResMods.size()+
                    " do not match # found in file "+fileName+": "+resMods.size()+"\n");
            return false;
        }
        
        if(uploadedResMods.size() == 0)
            return true;
        
        Collections.sort(uploadedResMods, new Comparator<MsResidueModification>() {
            public int compare(MsResidueModification o1, MsResidueModification o2) {
                int val = Character.valueOf(o1.getModifiedResidue()).compareTo(o2.getModifiedResidue());
                if(val != 0) return val;
                return o1.getModificationMass().compareTo(o2.getModificationMass());
            }});
        Collections.sort(resMods, new Comparator<MsResidueModificationIn>() {
            public int compare(MsResidueModificationIn o1, MsResidueModificationIn o2) {
                int val = Character.valueOf(o1.getModifiedResidue()).compareTo(o2.getModifiedResidue());
                if(val != 0) return val;
                return o1.getModificationMass().compareTo(o2.getModificationMass());
            }});
        
        for(int i = 0; i < uploadedResMods.size(); i++) {
            if(!matchResidueModification(uploadedResMods.get(i), resMods.get(i))) {
               errorMessage.append(type+" residue modification mismatch in file: "+fileName+"\n");
               return false;
            }
        }
        return true;
    }
    
    private boolean matchResidueModification(MsResidueModification mod1, MsResidueModificationIn mod2) {
        
        if(mod1.getModifiedResidue() != mod2.getModifiedResidue()) {
            errorMessage.append("Mismatch: uploaded modified residue: "+mod1.getModifiedResidue()+"; in file: "+mod2.getModifiedResidue()+"\n");
            return false;
        }
        if(mod1.getModificationSymbol() != mod2.getModificationSymbol()) {
            errorMessage.append("Mismatch: uploaded modification symbol "+mod1.getModificationSymbol()+"; in file: "+mod2.getModificationSymbol()+"\n");
            return false;
        }
        if(Math.abs(mod1.getModificationMass().doubleValue() - mod2.getModificationMass().doubleValue()) > 0.05) {
            errorMessage.append("Mismatch: uploaded modification mass: "+mod1.getModificationMass()+"; in file: "+mod2.getModificationMass()+"\n");
            return false;
        }
        return true;
    }

    private boolean matchDatabases(List<MsSearchDatabase> uploadedDbs,
            List<MsSearchDatabaseIn> databases, String fileName) {
        
        if(uploadedDbs.size() != databases.size()) {
            errorMessage.append("Number of uploaded search databases: "+uploadedDbs.size()+
                    " do not match # databases in file "+fileName+": "+databases.size()+"\n");
            return false;
        }
        
        Collections.sort(uploadedDbs, new Comparator<MsSearchDatabase>() {
            public int compare(MsSearchDatabase o1, MsSearchDatabase o2) {
                return o1.getDatabaseFileName().compareTo(o2.getDatabaseFileName());
            }});
        Collections.sort(databases, new Comparator<MsSearchDatabaseIn>() {
            public int compare(MsSearchDatabaseIn o1, MsSearchDatabaseIn o2) {
                return o1.getDatabaseFileName().compareTo(o2.getDatabaseFileName());
            }});
        for(int i = 0; i < uploadedDbs.size(); i++) {
            if(!uploadedDbs.get(i).getDatabaseFileName().equals(databases.get(i).getDatabaseFileName())) {
                errorMessage.append("Database mismatch in file: "+fileName+"\n");
                errorMessage.append("Uploaded db name: "+uploadedDbs.get(i).getDatabaseFileName()+"\n");
                errorMessage.append("In file: "+databases.get(i).getDatabaseFileName()+"\n");
                return false;
            }
        }
        return true;
    }

    private boolean matchEnzymes(List<MsEnzyme> uploadedEnzymes,
            List<MsEnzymeIn> enzymes, String fileName) {
        
        if(uploadedEnzymes.size() != enzymes.size()) {
            errorMessage.append("Number of uploaded enzymes: "+uploadedEnzymes.size()+
                    " do not match # enzymes in file "+fileName+": "+enzymes.size()+"\n");
            return false;
        }
        
        if(uploadedEnzymes.size() == 0)
            return true;
        
        Collections.sort(uploadedEnzymes, new Comparator<MsEnzyme>() {
            public int compare(MsEnzyme o1, MsEnzyme o2) {
                return o1.getName().compareTo(o2.getName());
            }});
        Collections.sort(enzymes, new Comparator<MsEnzymeIn>() {
            public int compare(MsEnzymeIn o1, MsEnzymeIn o2) {
                return o1.getName().compareTo(o2.getName());
            }});
        for(int i = 0; i < uploadedEnzymes.size(); i++) {
            if(!matchEnzyme(uploadedEnzymes.get(i), enzymes.get(i))) {
                errorMessage.append("Enzyme mismatch in file: "+fileName+"\n");
                return false;
            }
        }
        return true;
    }
    
    private boolean matchEnzyme(MsEnzyme enzyme1, MsEnzymeIn enzyme2) {
        if(!enzyme1.getName().equalsIgnoreCase(enzyme2.getName())) {
            errorMessage.append("Mismatching enzymes: Enzyme names do not match\n");
            errorMessage.append("uploaded name: "+enzyme1.getName()+"; name in file: "+enzyme2.getName()+"\n");
            return false;
        }
        if(!enzyme1.getCut().equalsIgnoreCase(enzyme2.getCut())) {
            errorMessage.append("Mismatching enzymes: Enzyme cut does not match\n");
            errorMessage.append("uploaded  cut: "+enzyme1.getCut()+"; cut in file: "+enzyme2.getCut()+"\n");
            return false;
        }
        if(!enzyme1.getNocut().equalsIgnoreCase(enzyme2.getNocut())) {
            errorMessage.append("Mismatching enzymes: Enzyme nocut does not match\n");
            errorMessage.append("uploaded nocut: "+enzyme1.getCut()+"; nocut in file: "+enzyme2.getCut()+"\n");
            return false;
        }
        if(enzyme1.getSense() != enzyme2.getSense()) {
            errorMessage.append("Mismatching enzymes: Enzyme sense does not match\n");
            errorMessage.append("uploaded sense: "+enzyme1.getCut()+"; sense in file: "+enzyme2.getCut()+"\n");
            return false;
        }
        return true;
        
    }

    public String getErrorMessage() {
        return errorMessage.toString();
    }

    public void setErrorMessage(StringBuilder errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isDatabasesMatch() {
        return databasesMatch;
    }

    public void setDatabasesMatch(boolean databasesMatch) {
        this.databasesMatch = databasesMatch;
    }

    public boolean isEnzymesMatch() {
        return enzymesMatch;
    }

    public boolean isDynamicResidueModsMatch() {
        return dynamicResidueModsMatch;
    }

    public boolean isStaticResidueModsMatch() {
        return staticResidueModsMatch;
    }

    public boolean isDynamicTerminalModsMatch() {
        return dynamicTerminalModsMatch;
    }

    public boolean isStaticTerminalModsMatch() {
        return staticTerminalModsMatch;
    }
}
