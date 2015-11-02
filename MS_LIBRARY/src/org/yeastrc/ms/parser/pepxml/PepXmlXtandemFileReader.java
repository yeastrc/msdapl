/**
 * PepXmlXtandemFileReader.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser.pepxml;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.SequestPeptideProphetResultIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.XtandemPeptideProphetResultIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.XtandemPeptideProphetResult;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.pepxml.sequest.PepXmlSequestSearchScanIn;
import org.yeastrc.ms.domain.search.pepxml.xtandem.PepXmlXtandemSearchScanIn;
import org.yeastrc.ms.domain.search.pepxml.xtandem.impl.PepXmlXtandemSearchScan;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchIn;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchResultIn;
import org.yeastrc.ms.domain.search.xtandem.impl.XtandemResult;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.util.AminoAcidUtilsFactory;
import org.yeastrc.ms.util.BaseAminoAcidUtils;

/**
 * 
 */
public class PepXmlXtandemFileReader extends PepXmlGenericFileReader<PepXmlXtandemSearchScanIn, 
                                                                        XtandemPeptideProphetResultIn,
                                                                        XtandemSearchResultIn,
                                                                        XtandemSearchIn>{
    

    @Override
    public XtandemSearchIn getSearch() {
        
        XtandemSearchIn search = new XtandemSearchIn() {
            @Override
            public List<Param> getXtandemParams() {
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
                return Program.XTANDEM;
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
    protected XtandemPeptideProphetResultIn initNewPeptideProphetResult() {
        return new XtandemPeptideProphetResult();
    }

    @Override
    protected XtandemSearchResultIn initNewSearchResult() {
        return new XtandemResult();
    }

    @Override
    protected PepXmlXtandemSearchScanIn initNewSearchScan() {
        return new PepXmlXtandemSearchScan();
    }

    @Override
    protected void readProgramSpecificResult(XtandemSearchResultIn searchResult) {
       
        for (int i = 0; i < reader.getAttributeCount(); i++) {

            String attrib = reader.getAttributeLocalName(i);
            String val = reader.getAttributeValue(i);
            if (attrib.equalsIgnoreCase("hit_rank"))
                searchResult.getXtandemResultData().setRank(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("num_matched_ions"))
                searchResult.getXtandemResultData().setMatchingIons(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("tot_num_ions"))
                searchResult.getXtandemResultData().setPredictedIons(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("calc_neutral_pep_mass")) {
                // NOTE: We are storing M+H in the database
                searchResult.getXtandemResultData().setCalculatedMass(new BigDecimal(val).add(BigDecimal.valueOf(BaseAminoAcidUtils.PROTON))); 
            }
        }
        
    }

    @Override
    protected void readProgramSpecificScore(XtandemSearchResultIn searchResult,
            String name, String value) {
        
        if (name.equalsIgnoreCase("hyperscore"))
            searchResult.getXtandemResultData().setHyperScore(new BigDecimal(value));
        else if (name.equalsIgnoreCase("nextscore"))
            searchResult.getXtandemResultData().setNextScore(new BigDecimal(value));
        else if(name.equalsIgnoreCase("bscore"))
            searchResult.getXtandemResultData().setBscore(new BigDecimal(value));
        else if(name.equalsIgnoreCase("yscore"))
            searchResult.getXtandemResultData().setYscore(new BigDecimal(value));
        else if (name.equalsIgnoreCase("expect"))
            searchResult.getXtandemResultData().setExpect(new BigDecimal(value));
        
    }
    
    public static void main(String[] args) throws DataProviderException {
        String file = "/Users/silmaril/WORK/UW/FLINT/xtandem_test/000.pep.xml";
        PepXmlSequestFileReader reader = new PepXmlSequestFileReader();
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
                PepXmlSequestSearchScanIn scan = reader.getNextSearchScan();
                for(SequestPeptideProphetResultIn res: scan.getScanResults()) {
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
