/**
 * PepXmlBaseFileReader.java
 * @author Vagisha Sharma
 * Oct 7, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser.pepxml;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.BasePeptideProphetResultIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.SequestPeptideProphetResultIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.BasePeptideProphetResult;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.impl.SearchResult;
import org.yeastrc.ms.domain.search.pepxml.PepXmlBaseSearchScanIn;
import org.yeastrc.ms.domain.search.pepxml.impl.PepXmlBaseSearchScan;
import org.yeastrc.ms.domain.search.pepxml.sequest.PepXmlSequestSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.util.AminoAcidUtilsFactory;

/**
 * 
 */
public class PepXmlBaseFileReader extends PepXmlGenericFileReader<PepXmlBaseSearchScanIn, 
                                                                    BasePeptideProphetResultIn,
                                                                    MsSearchResultIn,
                                                                    MsSearchIn>{

    @Override
    public MsSearchIn getSearch() {
        
        MsSearchIn search = new MsSearchIn() {
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
    protected BasePeptideProphetResultIn initNewPeptideProphetResult() {
        return new BasePeptideProphetResult();
    }

    @Override
    protected MsSearchResultIn initNewSearchResult() {
        return new SearchResult();
    }

    @Override
    protected PepXmlBaseSearchScanIn initNewSearchScan() {
        return new PepXmlBaseSearchScan();
    }

    @Override
    protected void readProgramSpecificResult(MsSearchResultIn result) {
        // nothing to do here
    }

    @Override
    protected void readProgramSpecificScore(MsSearchResultIn result,
            String name, String value) {
        // nothing to do here
    }

    public static void main(String[] args) throws DataProviderException {
        String file = "/Users/silmaril/WORK/UW/FLINT/Jimmy_Test/interact-default.pep.xml";
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
