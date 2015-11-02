/**
 * PepXmlMascotFileReader.java
 * @author Vagisha Sharma
 * Oct 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser.pepxml;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.MascotPeptideProphetResultIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.MascotPeptideProphetResult;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.mascot.MascotSearchIn;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResultIn;
import org.yeastrc.ms.domain.search.mascot.impl.MascotResult;
import org.yeastrc.ms.domain.search.pepxml.mascot.PepXmlMascotSearchScanIn;
import org.yeastrc.ms.domain.search.pepxml.mascot.impl.PepXmlMascotSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.util.AminoAcidUtilsFactory;
import org.yeastrc.ms.util.BaseAminoAcidUtils;

/**
 * 
 */
public class PepXmlMascotFileReader extends PepXmlGenericFileReader<PepXmlMascotSearchScanIn, 
                                                                    MascotPeptideProphetResultIn,
                                                                    MascotSearchResultIn,
                                                                    MascotSearchIn>{

    @Override
    public MascotSearchIn getSearch() {
        
        MascotSearchIn search = new MascotSearchIn() {
            @Override
            public List<Param> getMascotParams() {
                return searchParams;
            }
            @Override
            public List<MsResidueModificationIn> getDynamicResidueMods() {
                return searchDynamicResidueMods;
            }
            @Override
            public List<MsTerminalModificationIn> getDynamicTerminalMods() {
                return searchDynamicTerminalMods;
            }
            @Override
            public List<MsEnzymeIn> getEnzymeList() {
                List<MsEnzymeIn> enzymes = new ArrayList<MsEnzymeIn>(1);
                enzymes.add(enzyme);
                return enzymes;
            }
            @Override
            public List<MsSearchDatabaseIn> getSearchDatabases() {
                List<MsSearchDatabaseIn> databases = new ArrayList<MsSearchDatabaseIn>(1);
                databases.add(searchDatabase);
                return databases;
            }
            @Override
            public List<MsResidueModificationIn> getStaticResidueMods() {
                return searchStaticResidueMods;
            }
            @Override
            public List<MsTerminalModificationIn> getStaticTerminalMods() {
                return searchStaticTerminalMods;
            }
            @Override
            public Date getSearchDate() {
                return null;
            }

            @Override
            public Program getSearchProgram() {
                return Program.MASCOT;
            }
            @Override
            public String getSearchProgramVersion() {
                return null;
            }
            @Override
            public String getServerDirectory() {
                return getFileDirectory();
            }};

            return search;
    }

    @Override
    protected MascotPeptideProphetResultIn initNewPeptideProphetResult() {
        return new MascotPeptideProphetResult();
    }

    @Override
    protected MascotSearchResultIn initNewSearchResult() {
        return new MascotResult();
    }

    @Override
    protected PepXmlMascotSearchScanIn initNewSearchScan() {
        return new PepXmlMascotSearchScan();
    }

    @Override
    protected void readProgramSpecificResult(MascotSearchResultIn searchResult) {
       
        for (int i = 0; i < reader.getAttributeCount(); i++) {

            String attrib = reader.getAttributeLocalName(i);
            String val = reader.getAttributeValue(i);
            if (attrib.equalsIgnoreCase("hit_rank"))
                searchResult.getMascotResultData().setRank(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("num_matched_ions"))
                searchResult.getMascotResultData().setMatchingIons(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("tot_num_ions"))
                searchResult.getMascotResultData().setPredictedIons(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("calc_neutral_pep_mass")) {
                // NOTE: We are storing M+H in the database
                searchResult.getMascotResultData().setCalculatedMass(new BigDecimal(val).add(BigDecimal.valueOf(BaseAminoAcidUtils.PROTON))); 
            }
        }
        
    }

    @Override
    protected void readProgramSpecificScore(MascotSearchResultIn searchResult,
            String name, String value) {
        
        if (name.equalsIgnoreCase("ionscore"))
            searchResult.getMascotResultData().setIonScore(new BigDecimal(value));
        else if (name.equalsIgnoreCase("identityscore"))
            searchResult.getMascotResultData().setIdentityScore(new BigDecimal(value));
        else if(name.equalsIgnoreCase("homologyscore"))
            searchResult.getMascotResultData().setHomologyScore(new BigDecimal(value));
        else if (name.equalsIgnoreCase("expect"))
            searchResult.getMascotResultData().setExpect(new BigDecimal(value));
        else if (name.equalsIgnoreCase("star"))
            searchResult.getMascotResultData().setStar(Integer.parseInt(value));
        
    }
    
    public static void main(String[] args) throws DataProviderException {
//        String file = "/Users/silmaril/WORK/UW/FLINT/mascot_test/090715_EPO-iT_80mM_HCD.pep.xml";
        String file = "/Users/silmaril/WORK/UW/HOOPMANN_DATA/F001861.pep.xml";
        PepXmlMascotFileReader reader = new PepXmlMascotFileReader();
        reader.open(file);
        System.out.println("PeptideProphet version: "+reader.getPeptideProphetVersion());
        System.out.println("PeptideProphetROC should be: "+reader.getPeptideProphetRoc());
        
        while(reader.hasNextRunSearch()) {
            System.out.println(reader.getRunSearchName());
            MsRunSearchIn rs = reader.getRunSearchHeader();
            System.out.println(rs.getSearchProgram());
            System.out.println(rs.getSearchFileFormat());
            
            int numScans = 0;
            int numResults = 0;
            while(reader.hasNextSearchScan()) {
                numScans++;
                PepXmlMascotSearchScanIn scan = reader.getNextSearchScan();
                for(MascotPeptideProphetResultIn res: scan.getScanResults()) {
                    numResults++;
                }
            }
            System.out.println("NumScans read: "+numScans);
            System.out.println("Num results: "+numResults);
            System.out.println();
        }
    }

    @Override
	protected double getMonoAAMass(char aa) {
    	return AminoAcidUtilsFactory.getAminoAcidUtils().monoMass(aa);
	}
}
