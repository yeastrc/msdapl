package org.yeastrc.www.proteinfer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.yeastrc.experiment.MascotResultPlus;
import org.yeastrc.experiment.SequestResultPlus;
import org.yeastrc.experiment.XtandemResultPlus;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.GenericSearchDAO.MassType;
import org.yeastrc.ms.dao.search.mascot.MascotSearchDAO;
import org.yeastrc.ms.dao.search.mascot.MascotSearchResultDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.xtandem.XtandemSearchDAO;
import org.yeastrc.ms.dao.search.xtandem.XtandemSearchResultDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.Peak;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchResult;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.util.BaseAminoAcidUtils;
import org.yeastrc.www.misc.HyperlinkedData;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.www.util.RoundingUtils;

public class ViewSpectrumAction extends Action {

	private static final Logger log = Logger.getLogger(ViewSpectrumAction.class);
	
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {


        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }


        // If we are viewing a result from a search algorithm (Sequest, Mascot, XTandem etc.) we will have either:
        // 1. scanID and runSearchResultID
        // OR 2. ms1scanID, precursorMz and runSearchId
        // If we are viewing a result from a peptide validation program (Percolator, PeptideProphet etc.) we will have either:
        // 1. scanID, runSearchResultID, runSearchAnalysisId
        // OR ms1scanID, precursorMz, runSearchAnalysisId
        // Case # 2 will happen when the user clicks on a precursor peak in a MS1 scan.
        
        int scanID = 0;
        int runSearchResultID = 0;
        int runSearchAnalysisID = 0;
        int runSearchID = 0;
        
        String runSearchAnalysisIDStr = request.getParameter("runSearchAnalysisID");
        
        try {runSearchAnalysisID = Integer.parseInt(runSearchAnalysisIDStr);}
    	catch(NumberFormatException e){}
    	
    	String runSearchIDStr = request.getParameter("runSearchID");
        
        try {runSearchID = Integer.parseInt(runSearchIDStr);}
    	catch(NumberFormatException e){}
    	
    	if(runSearchAnalysisID > 0)
    		request.setAttribute("runSearchAnalysisId", runSearchAnalysisID);
    	if(runSearchID > 0)
    		request.setAttribute("runSearchId", runSearchID);
        
        String scanIDStr = request.getParameter("scanID");
        String ms1scanIDStr = request.getParameter("ms1scanID");
        
        // Case 1: We know the ID of the scan requested by the user
        if(scanIDStr != null) {
        	
        	try {scanID = Integer.parseInt(scanIDStr);}
        	catch(NumberFormatException e){}

        	if(scanID == 0) {
        		ActionErrors errors = new ActionErrors();
        		errors.add("general", new ActionMessage("error.general.invalid.id", "scan: "+scanIDStr));
        		saveErrors( request, errors );
        		return mapping.findForward("Failure");
        	}

        	String runSearchResultIDStr = request.getParameter("runSearchResultID");
        	
        	try {runSearchResultID = Integer.parseInt(runSearchResultIDStr);}
        	catch(NumberFormatException e){}
        	
        	if(runSearchResultID == 0) {
        		ActionErrors errors = new ActionErrors();
        		errors.add("general", new ActionMessage("error.general.invalid.id", "runSearchResult: "+runSearchResultIDStr));
        		saveErrors( request, errors );
        		return mapping.findForward("Failure");
        	}
        	
        	if(runSearchID <= 0 && runSearchAnalysisID <= 0) {
        		MsSearchResult res = DAOFactory.instance().getMsSearchResultDAO().load(runSearchResultID);
        		request.setAttribute("runSearchId", res.getRunSearchId());
        	}
        }
        
        // Case 2: The user clicked on a MS1 peak and we have to first get the scanID of the closest MS2 scan
        else {
        	
        
        	int ms1ScanID = 0;
        	try {ms1ScanID = Integer.parseInt(ms1scanIDStr);}
        	catch(NumberFormatException e){}


        	if(ms1ScanID == 0) {
        		ActionErrors errors = new ActionErrors();
        		errors.add("general", new ActionMessage("error.general.invalid.id", "MS1 scan: "+scanIDStr));
        		saveErrors( request, errors );
        		return mapping.findForward("Failure");
        	}

        	String precursorMzStr = request.getParameter("precursorMz");
        	double precursorMz = -1;
        	try {precursorMz = Double.parseDouble(precursorMzStr);}
        	catch(NumberFormatException e){}
        	
        	if(precursorMz == -1) {
        		ActionErrors errors = new ActionErrors();
        		errors.add("general", new ActionMessage("error.general.errorMessage", "Invalid precursor m/z: "+precursorMzStr));
        		saveErrors( request, errors );
        		return mapping.findForward("Failure");
        	}

        	// Now we need to get a scanID, runSearchResultID based on the given information:
        	// MS1 scanID, m/z of a precursor peak and the runSearchAnalysisID;
        	// 1. Get the scanID of the closest in m/z to the given precursor m/z
        	scanID = getClosestScan(ms1ScanID, precursorMz);
        	
        	// 2. Now get the best result associated with this scan, given the experiment we are looking at
        	if(runSearchID > 0) {
        		runSearchResultID = getOneResultIdForSearch(scanID, runSearchID);
        	}
        	else if(runSearchAnalysisID > 0) {
        		runSearchResultID = getOneResultIdForAnalysis(scanID, runSearchAnalysisID);
        	}
        }
        
        if(runSearchResultID == 0) {
			ActionErrors errors = new ActionErrors();
    		errors.add("general", new ActionMessage("error.general.errorMessage", "No result found."));
    		saveErrors( request, errors );
    		return mapping.findForward("Failure");
		}
        
        boolean java = true;
        if(request.getParameter("java") == null) {
        	java = false;
        }
        if(java) {
        	try {
        		List<String> params = makeAppletParams(scanID, runSearchResultID, mapping, request);
        		if (params == null) {
        			// any errors have already been set. 
        			return mapping.findForward("Failure");
        		}
        		request.setAttribute("params", params);
        	}
        	catch(Exception e) {
        		ActionErrors errors = new ActionErrors();
        		errors.add("spectra", new ActionMessage("error.msdata.spectra.dataerror", e.getMessage()));
        		saveErrors( request, errors );
        		return mapping.findForward("Failure");
        	}
        }
        else {
        	JSONObject json = getJSONParams(scanID, runSearchResultID, request);
        	if(json != null) 
        		request.setAttribute("jsonParams", json);
        }
        
        
        // get other results for this scan
        // we have the scanID and the runSearchResultID
        // get the results for this scanID from the search results file represented by the runSearchID
        setOtherResultsForScan(scanID, runSearchResultID, request);
        
        
        return mapping.findForward("Success");
    
    }
    
    
	private int getOneResultIdForAnalysis(int scanID, int runSearchAnalysisID) {
		
		MsRunSearchAnalysisDAO rsaDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
		MsRunSearchAnalysis rsAnalysis = rsaDao.load(runSearchAnalysisID);
		if(rsAnalysis == null) {
			return 0;
		}
		
		MsSearchAnalysisDAO aDao = DAOFactory.instance().getMsSearchAnalysisDAO();
		MsSearchAnalysis analysis = aDao.load(rsAnalysis.getAnalysisId());
		
		if(analysis.getAnalysisProgram() == Program.PERCOLATOR) {
			PercolatorResultDAO presDao = DAOFactory.instance().getPercolatorResultDAO();
			List<Integer> resIds = presDao.loadIdsForRunSearchAnalysisScan(runSearchAnalysisID, scanID);
			if(resIds.size() > 0) {
				PercolatorResult res = presDao.loadForPercolatorResultId(resIds.get(0));
				return res.getId(); // this is the runSearchID
			}
		}
		else if(analysis.getAnalysisProgram() == Program.PEPTIDE_PROPHET) {
			PeptideProphetResultDAO presDao = DAOFactory.instance().getPeptideProphetResultDAO();
			List<Integer> resIds = presDao.loadIdsForRunSearchAnalysisScan(runSearchAnalysisID, scanID);
			if(resIds.size() > 0) {
				PeptideProphetResult res = presDao.loadForProphetResultId(resIds.get(0));
				return res.getId(); // this is the runSearchID
			}
		}
		// If we did not find a match return 0
		// TODO log this
		return 0;
	}
	
	private int getOneResultIdForSearch(int scanID, int runSearchID) {
		
		MsSearchResultDAO resDao = DAOFactory.instance().getMsSearchResultDAO();
		List<Integer> resIds = resDao.loadResultIdsForSearchScan(runSearchID, scanID);
		if(resIds.size() > 0)
			return resIds.get(0);
		return 0;
	}


	private int getClosestScan(int ms1ScanID, double precursorMz) {
		
		MsScanDAO msScanDao = DAOFactory.instance().getMsScanDAO();
		List<Integer> msmsScanIds = msScanDao.loadMS2ScanIdsForMS1Scan(ms1ScanID);
		
		int closestScanId = 0;
		double bestDiff = Double.MAX_VALUE;
    	if(msmsScanIds.size() > 0) {
    		for(Integer scanId: msmsScanIds) {
    			MsScan child = msScanDao.loadScanLite(scanId);
    			double d = Math.abs(child.getPrecursorMz().doubleValue() - precursorMz);
    			if(d < bestDiff) {
    				bestDiff = d;
    				closestScanId = scanId;
    			}
    		}
    	}
    	return closestScanId;
	}


	private void setOtherResultsForScan(int scanId, int runSearchResultId, HttpServletRequest request) {
        
        request.setAttribute("thisResult", runSearchResultId);
        MsSearchResultDAO resultDao = DAOFactory.instance().getMsSearchResultDAO();
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        
        MsSearchResult result = resultDao.load(runSearchResultId);
        
        int runSearchId = result.getRunSearchId();
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        MsRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        int searchId = runSearch.getSearchId();
        
        // get the resultIds
//        List<Integer> resultIds = resultDao.loadResultIdsForSearchScan(runSearchId, scanId);
        Set<Integer> resultIds = new HashSet<Integer>(resultDao.loadResultIdsForSearchScan(runSearchId, scanId));
        
        // get the search
        MsSearch search = searchDao.loadSearch(searchId);
        
        
        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        
        // load the results for the appropriate program
        if(search.getSearchProgram() == Program.SEQUEST || search.getSearchProgram() == Program.COMET) {
            
            TabularSequestResults tabRes = new TabularSequestResults();
            SequestSearchDAO seqSearchDao = DAOFactory.instance().getSequestSearchDAO();
            String eValValue = seqSearchDao.getSearchParamValue(searchId, "print_expect_score");
            if(eValValue != null && eValValue.trim().equals("1"))
                tabRes.useEValue();
            
            SequestSearchResultDAO seqDao = DAOFactory.instance().getSequestResultDAO();
            
            for(int resultId: resultIds) {
                SequestSearchResult sres = seqDao.load(resultId);
                MsScan scan = scanDao.load(sres.getScanId());
                boolean highlight = runSearchResultId == sres.getId() ? true : false;
                tabRes.addResult(new SequestResultPlus(sres, scan), highlight);
            }
            if(resultIds.size() > 0) {
                request.setAttribute("results", tabRes);
            }
        }
        
        if(search.getSearchProgram() == Program.TIDE) {
            
            TabularSequestResults tabRes = new TabularSequestResults();
            SequestSearchResultDAO seqDao = DAOFactory.instance().getSequestResultDAO();
            
            for(int resultId: resultIds) {
                SequestSearchResult sres = seqDao.load(resultId);
                MsScan scan = scanDao.load(sres.getScanId());
                boolean highlight = runSearchResultId == sres.getId() ? true : false;
                tabRes.addResult(new SequestResultPlus(sres, scan), highlight);
            }
            if(resultIds.size() > 0) {
                request.setAttribute("results", tabRes);
            }
        }
        
        else if(search.getSearchProgram() == Program.MASCOT) {
            
            TabularMascotResults tabRes = new TabularMascotResults();
            MascotSearchResultDAO masResDao = DAOFactory.instance().getMascotResultDAO();
            
            for(int resultId: resultIds) {
                MascotSearchResult sres = masResDao.load(resultId);
                MsScan scan = scanDao.load(sres.getScanId());
                boolean highlight = runSearchResultId == sres.getId() ? true : false;
                tabRes.addResult(new MascotResultPlus(sres, scan), highlight);
            }
            if(resultIds.size() > 0) {
                request.setAttribute("results", tabRes);
            }
        }
        
        else if(search.getSearchProgram() == Program.XTANDEM) {
            
            TabularXtandemResults tabRes = new TabularXtandemResults();
            XtandemSearchResultDAO xtResDao = DAOFactory.instance().getXtandemResultDAO();
            
            for(int resultId: resultIds) {
                XtandemSearchResult sres = xtResDao.load(resultId);
                MsScan scan = scanDao.load(sres.getScanId());
                boolean highlight = runSearchResultId == sres.getId() ? true : false;
                tabRes.addResult(new XtandemResultPlus(sres, scan), highlight);
            }
            if(resultIds.size() > 0) {
                request.setAttribute("results", tabRes);
            }
        }
        
        // TODO fix this 
//        else if (search.getSearchProgram() == Program.PROUCID) {
//            ProlucidSearchResultDAO plDao = DAOFactory.instance().getProlucidResultDAO();
//            List<ProlucidSearchResult> results = new ArrayList<ProlucidSearchResult>(resultIds.size());
//            for(int resultId: resultIds) {
//                ProlucidSearchResult pres = plDao.load(resultId);
//                results.add(pres);
//            }
//          if(results.size() > 0)
//              request.setAttribute("results", results);
//        }
    }
    
    private static class TabularSequestResults implements Tabular {

        
        private static SORT_BY[] columns = new SORT_BY[] {
            SORT_BY.MASS, 
            SORT_BY.CALC_MASS_SEQ,
            SORT_BY.CHARGE, 
            SORT_BY.RT, 
            SORT_BY.XCORR_RANK,
            SORT_BY.XCORR, 
            SORT_BY.DELTACN,
            SORT_BY.SP,
            SORT_BY.PEPTIDE
        };
        
        private int highlightedRow = -1;
        private boolean useEvalue = false;
        
        private final List<SequestResultPlus> results;
        
        private RoundingUtils rounder = RoundingUtils.getInstance();
        
        public TabularSequestResults() {
            this.results = new ArrayList<SequestResultPlus>();
        }
        
        public void useEValue() {
            this.useEvalue = true;
            columns = new SORT_BY[] {
                    SORT_BY.MASS, 
                    SORT_BY.CALC_MASS_SEQ,
                    SORT_BY.CHARGE, 
                    SORT_BY.RT, 
                    SORT_BY.XCORR_RANK,
                    SORT_BY.XCORR, 
                    SORT_BY.DELTACN,
                    SORT_BY.EVAL,
                    SORT_BY.PEPTIDE
                };
        }
        
        public void addResult(SequestResultPlus result, boolean highlight) {
            if(highlight)
                highlightedRow = results.size();
            results.add(result);
        }
        @Override
        public int columnCount() {
            return columns.length;
        }
        @Override
        public TableRow getRow(int index) {
            if(index >= results.size())
                return null;
            SequestResultPlus result = results.get(index);
            TableRow row = new TableRow();
            
            row.addCell(new TableCell(String.valueOf(rounder.roundFour(result.getObservedMass()))));
            row.addCell(new TableCell(String.valueOf(rounder.roundFour(result.getSequestResultData().getCalculatedMass())), null));
            row.addCell(new TableCell(String.valueOf(result.getCharge())));
            
            // Retention time
            BigDecimal temp = result.getRetentionTime();
            if(temp == null) {
                row.addCell(new TableCell("", null));
            }
            else
                row.addCell(new TableCell(String.valueOf(rounder.roundFour(temp)), null));
            
            row.addCell(new TableCell(String.valueOf(result.getSequestResultData().getxCorrRank()), null));
            row.addCell(new TableCell(String.valueOf(rounder.roundTwo(result.getSequestResultData().getxCorr())), null));
            row.addCell(new TableCell(String.valueOf(result.getSequestResultData().getDeltaCN()), null));
            if(!useEvalue)
                row.addCell(new TableCell(String.valueOf(rounder.roundTwo(result.getSequestResultData().getSp())), null));
            else {
                if(result.getSequestResultData().getEvalue() != null)
                    row.addCell(new TableCell(String.valueOf(rounder.roundFour(result.getSequestResultData().getEvalue())), null));
                else
                    row.addCell(new TableCell("NULL", null));
            }
            
            
            TableCell cell = new TableCell();
			try {
				String modifiedSequence = result.getResultPeptide().getFullModifiedPeptide(true); 
				// link to Java spectrum viewer
	        	HyperlinkedData javaLink = new HyperlinkedData("<span style='font-size:8pt;' title='Java Spectrum Viewer'>(J)</span>");
	        	String url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId()
	        	//+"&runSearchAnalysisID="+result.getRunSearchAnalysisId()
	        	+"&java=true";
	        	javaLink.setHyperlink(url, true);
	        	javaLink.setTargetName("spec_view_java");
	        	cell.addData(javaLink);
	        	
	        	HyperlinkedData jsLink = new HyperlinkedData("<span style='font-size:8pt;' title='JavaScript Spectrum Viewer'>(JS)</span>");
	        	url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId();
	        	//+"&runSearchAnalysisID="+result.getRunSearchAnalysisId();
	        	jsLink.setHyperlink(url, true);
	        	jsLink.setTargetName("spec_view_js");
	        	cell.addData(jsLink);
	        	
	        	if(result.getResultPeptide().hasDynamicModification())
	        		cell.addData("<span class=\"peptide\">"+modifiedSequence+"</span>");
	        	else
	        		cell.addData(modifiedSequence);
	        	
//				HyperlinkedData data = new HyperlinkedData(result.getResultPeptide().getFullModifiedPeptide(true));
//				String url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId();
//				data.setAbsoluteHyperlink(url, true);
//	            data.setTargetName("SPECTRUM_WINDOW");
//	            cell.addData(data);
			} catch (ModifiedSequenceBuilderException e) {
				log.error("Error building modified sequence", e);
				cell.addData("ERROR");
			}
            cell.setClassName("left_align");
            row.addCell(cell);
            
            if(highlightedRow == index)
                row.setRowHighighted(true);
            return row;
        }
        @Override
        public int rowCount() {
            return results.size();
        }
        @Override
        public List<TableHeader> tableHeaders() {
            List<TableHeader> headers = new ArrayList<TableHeader>(columns.length);
            for(SORT_BY col: columns) {
                TableHeader header = new TableHeader(col.getDisplayName(), col.name());
                headers.add(header);
                if(col.getTooltip() != null)
                	header.setTitle(col.getTooltip());
            }
            return headers;
        }
        @Override
        public void tabulate() {
            // nothing to do here
        }
    }
    
    
    private static class TabularMascotResults implements Tabular {

        
        private static SORT_BY[] columns = new SORT_BY[] {
            SORT_BY.MASS, 
            SORT_BY.CALC_MASS_SEQ,
            SORT_BY.CHARGE, 
            SORT_BY.RT, 
            SORT_BY.MASCOT_RANK,
            SORT_BY.ION_SCORE, 
            SORT_BY.IDENTITY_SCORE,
            SORT_BY.HOMOLOGY_SCORE,
            SORT_BY.MASCOT_EXPECT,
            SORT_BY.PEPTIDE
        };
        
        private int highlightedRow = -1;
        
        private RoundingUtils rounder = RoundingUtils.getInstance();
        
        private final List<MascotResultPlus> results;
        
        public TabularMascotResults() {
            this.results = new ArrayList<MascotResultPlus>();
        }
        
        public void addResult(MascotResultPlus result, boolean highlight) {
            if(highlight)
                highlightedRow = results.size();
            results.add(result);
        }
        @Override
        public int columnCount() {
            return columns.length;
        }
        @Override
        public TableRow getRow(int index) {
            if(index >= results.size())
                return null;
            MascotResultPlus result = results.get(index);
            TableRow row = new TableRow();
            
            row.addCell(new TableCell(String.valueOf(rounder.roundFour(result.getObservedMass()))));
            row.addCell(new TableCell(String.valueOf(rounder.roundFour(result.getMascotResultData().getCalculatedMass())), null));
            row.addCell(new TableCell(String.valueOf(result.getCharge())));
            
            // Retention time
            BigDecimal temp = result.getRetentionTime();
            if(temp == null) {
                row.addCell(new TableCell("", null));
            }
            else
                row.addCell(new TableCell(String.valueOf(rounder.roundFour(temp)), null));
            
            row.addCell(new TableCell(String.valueOf(result.getMascotResultData().getRank()), null));
            row.addCell(new TableCell(String.valueOf(rounder.roundFour(result.getMascotResultData().getIonScore())), null));
            row.addCell(new TableCell(String.valueOf(result.getMascotResultData().getIdentityScore()), null));
            row.addCell(new TableCell(String.valueOf(result.getMascotResultData().getHomologyScore()), null));
            row.addCell(new TableCell(String.valueOf(result.getMascotResultData().getExpect()), null));
            

            TableCell cell = new TableCell();
			try {
				
				String modifiedSequence = result.getResultPeptide().getFullModifiedPeptide(true); 
				// link to Java spectrum viewer
	        	HyperlinkedData javaLink = new HyperlinkedData("<span style='font-size:8pt;' title='Java Spectrum Viewer'>(J)</span>");
	        	String url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId()
	        	//+"&runSearchAnalysisID="+result.getRunSearchAnalysisId()
	        	+"&java=true";
	        	javaLink.setHyperlink(url, true);
	        	javaLink.setTargetName("spec_view_java");
	        	cell.addData(javaLink);
	        	
	        	HyperlinkedData jsLink = new HyperlinkedData("<span style='font-size:8pt;' title='JavaScript Spectrum Viewer'>(JS)</span>");
	        	url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId();
	        	//+"&runSearchAnalysisID="+result.getRunSearchAnalysisId();
	        	jsLink.setHyperlink(url, true);
	        	jsLink.setTargetName("spec_view_js");
	        	cell.addData(jsLink);
	        	
	        	if(result.getResultPeptide().hasDynamicModification())
	        		cell.addData("<span class=\"peptide\">"+modifiedSequence+"</span>");
	        	else
	        		cell.addData(modifiedSequence);
	        	
//				HyperlinkedData data = new HyperlinkedData(result.getResultPeptide().getFullModifiedPeptide(true));
//				String url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId();
//				data.setAbsoluteHyperlink(url, true);
//	            data.setTargetName("SPECTRUM_WINDOW");
//	            cell.addData(data);
			} catch (ModifiedSequenceBuilderException e) {
				log.error("Error building modified sequence", e);
				cell.addData("ERROR");
			}
            cell.setClassName("left_align");
            row.addCell(cell);
            
            if(highlightedRow == index)
                row.setRowHighighted(true);
            return row;
        }
        @Override
        public int rowCount() {
            return results.size();
        }
        @Override
        public List<TableHeader> tableHeaders() {
            List<TableHeader> headers = new ArrayList<TableHeader>(columns.length);
            for(SORT_BY col: columns) {
                TableHeader header = new TableHeader(col.getDisplayName(), col.name());
                headers.add(header);
                if(col.getTooltip() != null)
                	header.setTitle(col.getTooltip());
            }
            return headers;
        }
        @Override
        public void tabulate() {
            // nothing to do here
        }
    }
    
    private static class TabularXtandemResults implements Tabular {

        
        private static SORT_BY[] columns = new SORT_BY[] {
            SORT_BY.MASS, 
            SORT_BY.CALC_MASS_SEQ,
            SORT_BY.CHARGE, 
            SORT_BY.RT, 
            SORT_BY.XTANDEM_RANK,
            SORT_BY.HYPER_SCORE, 
            SORT_BY.NEXT_SCORE,
            SORT_BY.B_SCORE,
            SORT_BY.Y_SCORE,
            SORT_BY.XTANDEM_EXPECT,
            SORT_BY.PEPTIDE
        };
        
        private int highlightedRow = -1;
        
        private RoundingUtils rounder = RoundingUtils.getInstance();
        
        private final List<XtandemResultPlus> results;
        
        public TabularXtandemResults() {
            this.results = new ArrayList<XtandemResultPlus>();
        }
        
        public void addResult(XtandemResultPlus result, boolean highlight) {
            if(highlight)
                highlightedRow = results.size();
            results.add(result);
        }
        @Override
        public int columnCount() {
            return columns.length;
        }
        @Override
        public TableRow getRow(int index) {
            if(index >= results.size())
                return null;
            XtandemResultPlus result = results.get(index);
            TableRow row = new TableRow();
            
            row.addCell(new TableCell(String.valueOf(rounder.roundFour(result.getObservedMass()))));
            row.addCell(new TableCell(String.valueOf(rounder.roundFour(result.getXtandemResultData().getCalculatedMass())), null));
            row.addCell(new TableCell(String.valueOf(result.getCharge())));
            
            // Retention time
            BigDecimal temp = result.getRetentionTime();
            if(temp == null) {
                row.addCell(new TableCell("", null));
            }
            else
                row.addCell(new TableCell(String.valueOf(rounder.roundFour(temp)), null));
            
            row.addCell(new TableCell(String.valueOf(result.getXtandemResultData().getRank()), null));
            row.addCell(new TableCell(String.valueOf(result.getXtandemResultData().getHyperScore()), null));
            row.addCell(new TableCell(String.valueOf(result.getXtandemResultData().getNextScore()), null));
            row.addCell(new TableCell(String.valueOf(result.getXtandemResultData().getBscore()), null));
            row.addCell(new TableCell(String.valueOf(result.getXtandemResultData().getYscore()), null));
            row.addCell(new TableCell(String.valueOf(result.getXtandemResultData().getExpect()), null));
            
            
            TableCell cell = new TableCell();
			try {
				
				String modifiedSequence = result.getResultPeptide().getFullModifiedPeptide(true); 
				// link to Java spectrum viewer
	        	HyperlinkedData javaLink = new HyperlinkedData("<span style='font-size:8pt;' title='Java Spectrum Viewer'>(J)</span>");
	        	String url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId()
	        	//+"&runSearchAnalysisID="+result.getRunSearchAnalysisId()
	        	+"&java=true";
	        	javaLink.setHyperlink(url, true);
	        	javaLink.setTargetName("spec_view_java");
	        	cell.addData(javaLink);
	        	
	        	HyperlinkedData jsLink = new HyperlinkedData("<span style='font-size:8pt;' title='JavaScript Spectrum Viewer'>(JS)</span>");
	        	url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId();
	        	//+"&runSearchAnalysisID="+result.getRunSearchAnalysisId();
	        	jsLink.setHyperlink(url, true);
	        	jsLink.setTargetName("spec_view_js");
	        	cell.addData(jsLink);
	        	
	        	if(result.getResultPeptide().hasDynamicModification())
	        		cell.addData("<span class=\"peptide\">"+modifiedSequence+"</span>");
	        	else
	        		cell.addData(modifiedSequence);
	        	
//				HyperlinkedData data = new HyperlinkedData(result.getResultPeptide().getFullModifiedPeptide(true));
//				String url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId();
//				data.setAbsoluteHyperlink(url, true);
//	            data.setTargetName("SPECTRUM_WINDOW");
//	            cell.addData(data);
			} catch (ModifiedSequenceBuilderException e) {
				log.error("Error building modified sequence", e);
				cell.addData("ERROR");
			}
            cell.setClassName("left_align");
            row.addCell(cell);
            
            
            if(highlightedRow == index)
                row.setRowHighighted(true);
            return row;
        }
        @Override
        public int rowCount() {
            return results.size();
        }
        @Override
        public List<TableHeader> tableHeaders() {
            List<TableHeader> headers = new ArrayList<TableHeader>(columns.length);
            for(SORT_BY col: columns) {
                TableHeader header = new TableHeader(col.getDisplayName(), col.name());
                headers.add(header);
                if(col.getTooltip() != null)
                	header.setTitle(col.getTooltip());
            }
            return headers;
        }
        @Override
        public void tabulate() {
            // nothing to do here
        }
    }

    
    
    private List<String> makeAppletParams(int scanId, int runSearchResultId, ActionMapping mapping, HttpServletRequest request) 
        throws Exception {
        
        MsSearchResultDAO resultDao = DAOFactory.instance().getMsSearchResultDAO();
        MsSearchResult result = resultDao.load(runSearchResultId);
        int runSearchId = result.getRunSearchId();
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        MsRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        int searchId = runSearch.getSearchId();
        // get the search
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        
        
        MsSearchResultPeptide resPeptide = result.getResultPeptide();
        request.setAttribute("peptideSeq", resPeptide.getFullModifiedPeptide());
        
        
        MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
        String filename = runDao.loadFilenameForRun(runSearch.getRunId());

        MsScanDAO msScanDao = DAOFactory.instance().getMsScanDAO();
        MsScan scan = msScanDao.load(scanId);
        int scanNumber = scan.getStartScanNum();
        
        
        int charge = result.getCharge();
        BigDecimal massFrmScanCharge = null;
        // get the precursor mass for the given charge state for the MS2 tables if this was a MS2 run
        MsRun run = DAOFactory.instance().getMsRunDAO().loadRun(scan.getRunId());
        if(run.getRunFileFormat() == RunFileFormat.MS2 || run.getRunFileFormat() == RunFileFormat.CMS2) {
        
            // get the scan
          MS2Scan ms2scan = DAOFactory.instance().getMS2FileScanDAO().load(scanId);
            List<MS2ScanCharge> scChargeList = ms2scan.getScanChargeList();
            
            for (MS2ScanCharge sc: scChargeList) {
                if (sc.getCharge() == charge) {
                    massFrmScanCharge = sc.getMass();
                    break;
                }
            }
        }
        // If we did not find the mass, calculate it from what we have
        double mass = 0.0;
        if (massFrmScanCharge == null) {
            mass = (scan.getPrecursorMz().doubleValue() - BaseAminoAcidUtils.HYDROGEN) * charge + BaseAminoAcidUtils.HYDROGEN;
        }
        else
            mass = massFrmScanCharge.doubleValue();
        
        
        // things that are required to be in the request
        request.setAttribute("filename", filename);
        request.setAttribute("firstMass", Math.round(mass*100)/100.0);
        request.setAttribute("firstCharge", charge);
        request.setAttribute("scanNumber", scanNumber);
        request.setAttribute("database", search.getSearchDatabases().get(0).getDatabaseFileName());
        
        // parameters for the applet
        List <String>params = new ArrayList<String>();
        
        String Sq = resPeptide.getPeptideSequence();
        Sq = resPeptide.getPreResidue()+"."+Sq+"."+resPeptide.getPostResidue();
        params.add("<PARAM NAME=\"MatchSeq\" VALUE=\"" + Sq + "\">");
        params.add("<PARAM NAME=\"MatchModifiedSeq\" VALUE=\"" + resPeptide.getFullModifiedPeptide() + "\">");
        params.add("<PARAM NAME=\"PreMPlusH\" VALUE=\"" +mass + "\">");
        params.add("<PARAM NAME=\"PreZ\" VALUE=\"" + charge + "\">");
        
        // dynamic modifications (residue)
        List<MsResidueModification> dynaResMods = search.getDynamicResidueMods();
        Map<String, String> modMassResidueMap = new HashMap<String, String>();
        for (MsResidueModification mod: dynaResMods) {
            String chars = modMassResidueMap.get(mod.getModificationMass().toString());
            if (chars == null)
                chars = String.valueOf(mod.getModifiedResidue());
            else
                chars += mod.getModifiedResidue();
            modMassResidueMap.put(mod.getModificationMass().toString(), chars);
        }
        int i = 1;
        for (String massKey: modMassResidueMap.keySet()) {
            params.add("<PARAM NAME=\"dynamicmod_mass_"+(i)+"\" VALUE=\"" + massKey + "\">");
            params.add("<PARAM NAME=\"dynamicmod_residues_"+(i++)+"\" VALUE=\"" + modMassResidueMap.get(massKey) + "\">");
        }
        
        // dynamic modifications (terminal)
        List<MsTerminalModification> dynaTermMods = search.getDynamicTerminalMods();
        for(MsTerminalModification mod: dynaTermMods) {
            params.add("<PARAM NAME=\"dynamicmod_mass_"+(i)+"\" VALUE=\"" + mod.getModificationMass() + "\">");
            params.add("<PARAM NAME=\"dynamicmod_residues_"+(i++)+"\" VALUE=\"" + mod.getModifiedTerminal() + "\">");
        }
        
        
        // result dynamic residue modifications
        List<MsResultResidueMod> resultResMods = result.getResultPeptide().getResultDynamicResidueModifications();
        i = 1;
        for(MsResultResidueMod mod: resultResMods) {
            params.add("<PARAM NAME=\"modification_"+(i++)+"\" VALUE=\"" + (mod.getModifiedPosition()+1)+","+mod.getModificationMass() + "\">");
        }
        
        // result dynamic terminal modifications
        List<MsResultTerminalMod> resultTermMods = result.getResultPeptide().getResultDynamicTerminalModifications();
        i = 1;
        int peptideLength = result.getResultPeptide().getSequenceLength();
        for(MsResultTerminalMod mod: resultTermMods) {
            if(mod.getModifiedTerminal() == Terminal.NTERM)
                params.add("<PARAM NAME=\"modification_"+(i++)+"\" VALUE=\"1,"+mod.getModificationMass() + "\">");
            else if(mod.getModifiedTerminal() == Terminal.CTERM)
                params.add("<PARAM NAME=\"modification_"+(i++)+"\" VALUE=\""+peptideLength+","+mod.getModificationMass() + "\">");
        }
        
        // static residue modifications
        List<MsResidueModification> residueStaticMods = search.getStaticResidueMods();
        for (MsResidueModification mod: residueStaticMods) {
            params.add("<PARAM NAME=\"static_mod_"+mod.getModifiedResidue()+"\" VALUE=\"" + mod.getModificationMass().toString() + "\">");
        }
        
        // static terminal modifications
        List<MsTerminalModification> terminalStaticMods = search.getStaticTerminalMods();
        for(MsTerminalModification mod: terminalStaticMods) {
            if(mod.getModifiedTerminal() == Terminal.NTERM)
                params.add("<PARAM NAME=\"add_N_terminus\" VALUE=\"" + mod.getModificationMass().toString() + "\">");
            else if(mod.getModifiedTerminal() == Terminal.CTERM)
                params.add("<PARAM NAME=\"add_C_terminus\" VALUE=\"" + mod.getModificationMass().toString() + "\">");
        }
        
        
        // Need these values from the search parameters 
        MassType fragMassType = null;
        MassType parentMassType = null;
        if (search.getSearchProgram() == Program.SEQUEST || 
            //search.getSearchProgram() == Program.EE_NORM_SEQUEST ||
            search.getSearchProgram() == Program.PERCOLATOR) { // NOTE: Percolator in run on Sequest results so we will 
                                                               // look at the sequest parameters. 
            SequestSearchDAO seqSearchDao = DAOFactory.instance().getSequestSearchDAO();
            fragMassType = seqSearchDao.getFragmentMassType(searchId);
            parentMassType = seqSearchDao.getParentMassType(searchId);
        }
        if (search.getSearchProgram() == Program.TIDE) { 
                SequestSearchDAO seqSearchDao = DAOFactory.instance().getSequestSearchDAO();
                fragMassType = MassType.MONO;
                String val = seqSearchDao.getSearchParamValue(searchId, "MonoisotopicPrecursor");
                if(val.equals("1"))
                	parentMassType = MassType.MONO;
                else
                	parentMassType = MassType.AVG;
                	
            }
        else if(search.getSearchProgram() == Program.MASCOT) {
            MascotSearchDAO masSearchDao = DAOFactory.instance().getMascotSearchDAO();
            fragMassType = masSearchDao.getFragmentMassType(searchId);
            parentMassType = masSearchDao.getParentMassType(searchId);
        }
        else if(search.getSearchProgram() == Program.XTANDEM) {
            XtandemSearchDAO masSearchDao = DAOFactory.instance().getXtandemSearchDAO();
            fragMassType = masSearchDao.getFragmentMassType(searchId);
            parentMassType = masSearchDao.getParentMassType(searchId);
        }
        else if (search.getSearchProgram() == Program.PROLUCID) {
            ProlucidSearchDAO psearchDao = DAOFactory.instance().getProlucidSearchDAO();
            fragMassType = psearchDao.getFragmentMassType(searchId);
            parentMassType = psearchDao.getParentMassType(searchId);
        }
        if (fragMassType == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("spectra", new ActionMessage("error.msdata.spectra.massTypeError", searchId));
            saveErrors( request, errors );
            return null;
        }
        else {
            params.add("<PARAM NAME=\"AvgForFrag\" VALUE=\"" + (fragMassType == MassType.AVG ? "true" : "false") + "\">");
        }
        if (parentMassType == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("spectra", new ActionMessage("error.msdata.spectra.massTypeError"));
            saveErrors( request, errors );
            return null;
        }
        else {
            params.add("<PARAM NAME=\"AvgForParent\" VALUE=\"" + (parentMassType == MassType.AVG ? "true" : "false") + "\">"); 
        }
        
        // set the m/z intensity pairs
        List<String[]> peakList = scan.getPeaksString();
        i = 0;
        for (String[] peak: peakList) {
            params.add("<PARAM NAME=\"MZ" + i + "\" VALUE=\"" + peak[0] + "\">");
            params.add("<PARAM NAME=\"Int" +(i++) + "\" VALUE=\"" + peak[1] + "\">");
        }
        
        return params;
    }
    
    private JSONObject getJSONParams(int scanID, int runSearchResultId,
    		HttpServletRequest request) throws ModifiedSequenceBuilderException, IOException {
		
    	MsSearchResultDAO resultDao = DAOFactory.instance().getMsSearchResultDAO();
        MsSearchResult result = resultDao.load(runSearchResultId);
        
        JSONObject json = new JSONObject();
        
        // peptide sequence
        MsSearchResultPeptide resPeptide = result.getResultPeptide();
        json.put("sequence", resPeptide.getPeptideSequence());
        json.put("charge", Integer.valueOf(result.getCharge()));
        
        
        int runSearchId = result.getRunSearchId();
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        MsRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        
        // filename
        MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
        String filename = runDao.loadFilenameForRun(runSearch.getRunId());
        json.put("fileName", filename);
        
        // scanNum and precursor m/z
        MsScanDAO msScanDao = DAOFactory.instance().getMsScanDAO();
        MsScan scan = msScanDao.load(scanID);
        int scanNumber = scan.getStartScanNum();
        json.put("scanNum", Integer.valueOf(scanNumber));
        json.put("precursorMz", Double.valueOf(scan.getPrecursorMz().doubleValue()));
        
        // If we have a parent MS1 scan add peaks for that scan
        if(scan.getPrecursorScanId() != 0) {
        	
        	MsScan ms1scan = msScanDao.load(scan.getPrecursorScanId());
        	request.setAttribute("ms1ScanId", ms1scan.getId());
        	
        	// get a list of MS/MS scan IDs for this MS1 scan
        	List<Integer> msmsScanIds = msScanDao.loadMS2ScanIdsForMS1Scan(ms1scan.getId());
        	List<Double> precMzList = new ArrayList<Double>(msmsScanIds.size());
        	if(msmsScanIds.size() > 0) {
        		for(Integer scanId: msmsScanIds) {
        			MsScan child = msScanDao.loadScanLite(scanId);
        			precMzList.add(child.getPrecursorMz().doubleValue());
        		}
        	}
        	Collections.sort(precMzList);
        	
        	// set the m/z intensity pairs
            JSONArray jsonPeaks = new JSONArray();
            List<Peak> precPeaks = new ArrayList<Peak>();
            List<Peak> peakList = ms1scan.getPeaks();
            
            int i = 0;
            for (Peak peak: peakList) {
//            	if(peak.getMz() < premz - 5 || peak.getMz() > premz + 5)
//            		continue;
            	if(peak.getIntensity() < 1)
            		continue;
            	
            	JSONArray jpeak = new JSONArray();
            	jpeak.add(Double.valueOf(peak.getMz()));
            	jpeak.add(Float.valueOf(peak.getIntensity()));
            	jsonPeaks.add(jpeak);
            	
            	// check if this peak was slected for MS/MS fragmentation
            	// precursorMz value of MS/MS scans is not exactly the same as m/z of MS1 peaks (Why??)
            	int x = i;
            	for(; x < precMzList.size(); x++) {
            		if(precMzList.get(x) - 0.5 > peak.getMz())
            			break;
            		
            		// if this peak is close enough to the precursorMz of a MS/MS scan add it
            		// Later we will keeps only the ones that are closest.
            		if(Math.abs(peak.getMz() - precMzList.get(x)) < 0.5) {
                		precPeaks.add(peak);
                	}
            	}
            	i = x;
            }
            
            JSONArray closestPrecPeaks = new JSONArray();
            
            for(int k = 0; k < precMzList.size(); k++) {
            	Peak closest = null;
            	double bestDiff = Double.MAX_VALUE;
            	double precMz = precMzList.get(k);
            	for(Peak peak: precPeaks) {
            		
            		if(peak.getMz() < precMz - 0.5)
            			continue;
            		
            		if(peak.getMz() > precMz + 0.5)
            			break;
            		
            		double diff = Math.abs(peak.getMz() - precMz);
            		if(closest == null || diff < bestDiff) {
            			closest = peak;
            			bestDiff = diff;
            		}
            	}
            	
            	if(closest != null) {
            		JSONArray jpeak = new JSONArray();
                	jpeak.add(Double.valueOf(closest.getMz()));
                	jpeak.add(Float.valueOf(closest.getIntensity()));
                	closestPrecPeaks.add(jpeak);
            	}
            }
            
            json.put("ms1peaks", jsonPeaks);
            json.put("ms1scan", ms1scan.getStartScanNum()+" RT: "+
            		RoundingUtils.getInstance().roundOne(ms1scan.getRetentionTime()));
            if(precPeaks.size() > 0)
            	json.put("precursorPeaks", closestPrecPeaks);
            json.put("zoomMs1", "true");
        }
        
        int searchId = runSearch.getSearchId();
        // get the search
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        
        
        // static terminal modifications
        List<MsTerminalModification> terminalStaticMods = search.getStaticTerminalMods();
        for(MsTerminalModification mod: terminalStaticMods) {
            if(mod.getModifiedTerminal() == Terminal.NTERM) {
            	json.put("ntermMod", Double.valueOf(mod.getModificationMass().doubleValue()));
            }
            else if(mod.getModifiedTerminal() == Terminal.CTERM) {
            	json.put("ctermMod", Double.valueOf(mod.getModificationMass().doubleValue()));
            }
        }
        
        // result dynamic terminal modifications
        List<MsResultTerminalMod> resultTermMods = result.getResultPeptide().getResultDynamicTerminalModifications();
        int peptideLength = result.getResultPeptide().getSequenceLength();
        for(MsResultTerminalMod mod: resultTermMods) {
            if(mod.getModifiedTerminal() == Terminal.NTERM)
               json.put("ntermVarMod", Double.valueOf(mod.getModificationMass().doubleValue()));
            else if(mod.getModifiedTerminal() == Terminal.CTERM)
            	json.put("ctermVarMod", Double.valueOf(mod.getModificationMass().doubleValue()));
        }
        
        
        
        // static residue modifications
        JSONArray staticMods = new JSONArray();
        List<MsResidueModification> residueStaticMods = search.getStaticResidueMods();
        for (MsResidueModification mod: residueStaticMods) {
        	JSONObject jmod = new JSONObject();
        	jmod.put("aminoAcid", String.valueOf(mod.getModifiedResidue()));
        	jmod.put("modMass", Double.valueOf(mod.getModificationMass().doubleValue()));
        	staticMods.add(jmod);
        }
        json.put("staticMods", staticMods);
        
        
        // result dynamic residue modifications
        JSONArray varMods = new JSONArray();
        List<MsResultResidueMod> resultResMods = result.getResultPeptide().getResultDynamicResidueModifications();
        for(MsResultResidueMod mod: resultResMods) {
        	JSONObject jmod = new JSONObject();
        	jmod.put("index", Integer.valueOf(mod.getModifiedPosition() + 1));
        	jmod.put("modMass", Double.valueOf(mod.getModificationMass().doubleValue()));
        	jmod.put("aminoAcid", String.valueOf(mod.getModifiedResidue()));
        	varMods.add(jmod);
        }
        json.put("variableMods", varMods);
        
        
        // set the m/z intensity pairs
        JSONArray jsonPeaks = new JSONArray();
        List<Peak> peakList = scan.getPeaks();
        for (Peak peak: peakList) {
        	JSONArray jpeak = new JSONArray();
        	jpeak.add(Double.valueOf(peak.getMz()));
        	jpeak.add(Float.valueOf(peak.getIntensity()));
        	jsonPeaks.add(jpeak);
        }
        json.put("peaks", jsonPeaks);
        
        return json;
	}

}
