/**
 * PepXmlSequestFileReader.java
 * @author Vagisha Sharma
 * Oct 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser.pepxml;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.SequestPeptideProphetResultIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.SequestPeptideProphetResult;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.pepxml.sequest.PepXmlSequestSearchScanIn;
import org.yeastrc.ms.domain.search.pepxml.sequest.impl.PepXmlSequestSearchScan;
import org.yeastrc.ms.domain.search.sequest.SequestSearchIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.impl.SequestResult;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.util.AminoAcidUtilsFactory;
import org.yeastrc.ms.util.BaseAminoAcidUtils;

/**
 * 
 */
public class PepXmlSequestFileReader extends PepXmlGenericFileReader<PepXmlSequestSearchScanIn, 
                                                                    SequestPeptideProphetResultIn,
                                                                    SequestSearchResultIn,
                                                                    SequestSearchIn> {

    private boolean parseEvalue = false;
    
    public void setParseEvalue(boolean parseEvalue) {
        this.parseEvalue = parseEvalue;
    }
    
    @Override
    public SequestSearchIn getSearch() {
        SequestSearchIn search = new SequestSearchIn() {
            @Override
            public List<Param> getSequestParams() {
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
                return Program.SEQUEST;
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
    protected SequestPeptideProphetResultIn initNewPeptideProphetResult() {
        return new SequestPeptideProphetResult();
    }

    @Override
    protected SequestSearchResultIn initNewSearchResult() {
        return new SequestResult();
    }

    @Override
    protected PepXmlSequestSearchScanIn initNewSearchScan() {
        return new PepXmlSequestSearchScan();
    }

    @Override
    protected void readProgramSpecificResult(SequestSearchResultIn searchResult) {
        
        for (int i = 0; i < reader.getAttributeCount(); i++) {

            String attrib = reader.getAttributeLocalName(i);
            String val = reader.getAttributeValue(i).trim();
            if (attrib.equalsIgnoreCase("hit_rank"))
                searchResult.getSequestResultData().setxCorrRank(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("num_matched_ions"))
                searchResult.getSequestResultData().setMatchingIons(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("tot_num_ions"))
                searchResult.getSequestResultData().setPredictedIons(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("calc_neutral_pep_mass")) {
                // NOTE: We are storing M+H in the database
                searchResult.getSequestResultData().setCalculatedMass(new BigDecimal(val).add(BigDecimal.valueOf(BaseAminoAcidUtils.PROTON))); 
            }
        }
    }

    @Override
    protected void readProgramSpecificScore(SequestSearchResultIn searchResult,
            String name, String value) {
        
        if (name.equalsIgnoreCase("xcorr"))
            searchResult.getSequestResultData().setxCorr(new BigDecimal(value));
        else if (name.equalsIgnoreCase("deltacn"))
            searchResult.getSequestResultData().setDeltaCN(new BigDecimal(value));
        else if(name.equalsIgnoreCase("deltacnstar"))
            searchResult.getSequestResultData().setDeltaCNstar(new BigDecimal(value));
        else if (name.equalsIgnoreCase("spscore")) {
            if(!this.parseEvalue)
                searchResult.getSequestResultData().setSp(new BigDecimal(value));
            else
                searchResult.getSequestResultData().setEvalue(Double.valueOf(value));
        }
        else if (name.equalsIgnoreCase("sprank"))
            searchResult.getSequestResultData().setSpRank(Integer.parseInt(value));
        
    }
    
    public static void main(String[] args) throws DataProviderException {
        String file = "/Users/silmaril/WORK/UW/FLINT/Jimmy_Test/M_102908_Y_Lys_ETD_EPI_contol.pep.xml";
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
		return AminoAcidUtilsFactory.getSequestAminoAcidUtils().monoMass(aa);
	}
}
