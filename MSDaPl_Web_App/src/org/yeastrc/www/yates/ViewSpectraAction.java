/*
 * ViewSpectraAction.java
 * Created on Oct 20, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.yates;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.GenericSearchDAO.MassType;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.util.PeakConverter;
import org.yeastrc.project.Project;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesCycle;
import org.yeastrc.yates.YatesCycleFactory;
import org.yeastrc.yates.YatesPeptide;
import org.yeastrc.yates.YatesResult;
import org.yeastrc.yates.YatesRun;


/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 20, 2004
 */

public class ViewSpectraAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		// The peptide we're viewing
		int pepID;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		YatesPeptide yp = null;
		YatesRun run = null;
		YatesResult result = null;
		try {
			String strID = request.getParameter("id");

			if (strID == null || strID.equals("")) {
				ActionErrors errors = new ActionErrors();
				errors.add("yates", new ActionMessage("error.yates.peptide.invalidid"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}

			pepID = Integer.parseInt(strID);

			// Load our peptide
			yp = new YatesPeptide();
			yp.load(pepID);

			result = new YatesResult();
			result.load(yp.getResultID());
			run = (YatesRun)(result.getRun());

			Project project = run.getProject();
			if (!project.checkReadAccess(user.getResearcher())) {
				
				// This user doesn't have access to this project.
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noaccess"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("yates", new ActionMessage("error.project.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		} catch (InvalidIDException iie) {
			ActionErrors errors = new ActionErrors();
			errors.add("yates", new ActionMessage("error.yates.peptide.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		} catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}

		request.setAttribute("run", run);
		request.setAttribute("result", result);
		request.setAttribute("peptide", yp);		

		
		// Determine if the YatesPeptide has a searchID and scanID
		if (yp.getSearchId() == 0 || yp.getScanId() == 0) {
		    // no search and / or scan ids are associated with this run.  This means that the data is not in the msData database, 
		    // either because it could not be uploaded or because this is legacy data (pre msData).
		    // If it is old try to look it up in the YatesCycle tables.  
		    try {
		        List<String> params = useYatesCycleData(run, yp, mapping, request);
		        if (params == null) {
		            // any errors have already been set.
	                return mapping.findForward("Failure");
		        }
		        request.setAttribute("params", params);
		    }
		    catch(Exception e) {
		        ActionErrors errors = new ActionErrors();
	            errors.add("spectra", new ActionMessage("error.yates.spectra.general", e.getMessage()));
	            saveErrors( request, errors );
	            return mapping.findForward("Failure");
		    }
		}
		else {
		    // We should have this data in the msData tables!
		    try {
		        List<String> params = useMsData(run, yp, mapping, request);
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
		
		// clean up
		run = null;
		result = null;
        yp = null;
        
		// Do a garbage collection now, to clean up.
		System.gc();
		
		return mapping.findForward("Success");
	}

	private List<String> useMsData(YatesRun run, YatesPeptide resPeptide,
            ActionMapping mapping, HttpServletRequest request) throws IOException {
	    
        String Sq = resPeptide.getSequence();
        String filename = resPeptide.getFilename();

        String[] fields = filename.split("\\.");
        // charge
        String Z = fields[fields.length - 1];

        // Scan Number
        String Sc = fields[fields.length - 3];

        // Dat file
        String Da = fields[0];
        
        
        int scanId = resPeptide.getScanId();
        int searchId = resPeptide.getSearchId();
        
        // get the scan
        MS2ScanDAO scanDao = DAOFactory.instance().getMS2FileScanDAO();
        MS2Scan scan = scanDao.load(scanId);
        
        // get the precursor mass for the given charge state
        List<MS2ScanCharge> scChargeList = scan.getScanChargeList();
        BigDecimal massFrmScanCharge = null;
        int charge = Integer.parseInt(Z);
        for (MS2ScanCharge sc: scChargeList) {
            if (sc.getCharge() == charge) {
                massFrmScanCharge = sc.getMass();
                break;
            }
        }
        // If we did not find the mass, calculate it from what we have
        double mass = 0.0;
        if (massFrmScanCharge == null) {
            mass = (scan.getPrecursorMz().doubleValue() - 1.00794) * charge + 1.00794;
        }
        else
            mass = massFrmScanCharge.doubleValue();
        
        // get the search
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        
        
        // things that are required to be in the request
        request.setAttribute("firstMass", mass);
        request.setAttribute("firstCharge", charge);
        request.setAttribute("datfile", Da);
        request.setAttribute("scanNumber", Sc);
        request.setAttribute("database", search.getSearchDatabases().get(0).getDatabaseFileName());
        
        // parameters for the applet
        List <String>params = new ArrayList<String>();
        
        params.add("<PARAM NAME=\"MatchSeq\" VALUE=\"" + Sq + "\">");
        params.add("<PARAM NAME=\"PreMPlusH\" VALUE=\"" +mass + "\">");
        params.add("<PARAM NAME=\"PreZ\" VALUE=\"" + charge + "\">");
        
        // dynamic modifications mod1, mod2, etc; mod1residues, mod2residues etc
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
            params.add("<PARAM NAME=\"mod"+(i)+"\" VALUE=\"" + massKey + "\">");
            params.add("<PARAM NAME=\"mod"+(i++)+"residues\" VALUE=\"" + modMassResidueMap.get(massKey) + "\">");
        }
        
        
        // static residue modifications
        List<MsResidueModification> residueStaticMods = search.getStaticResidueMods();
        for (MsResidueModification mod: residueStaticMods) {
            params.add("<PARAM NAME=\"static_mod_"+mod.getModifiedResidue()+"\" VALUE=\"" + mod.getModificationMass().toString() + "\">");
        }
        
        
        // Need these values from the search parameters 
        MassType fragMassType = null;
        MassType parentMassType = null;
        if (search.getSearchProgram() == Program.SEQUEST) {
//            || search.getSearchProgram() == Program.EE_NORM_SEQUEST) {
            SequestSearchDAO seqSearchDao = DAOFactory.instance().getSequestSearchDAO();
            fragMassType = seqSearchDao.getFragmentMassType(searchId);
            parentMassType = seqSearchDao.getParentMassType(searchId);
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
            errors.add("spectra", new ActionMessage("error.msdata.spectra.massTypeError", searchId));
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

    private List<String> useYatesCycleData(YatesRun run, YatesPeptide resPeptide, ActionMapping mapping, HttpServletRequest request) throws Exception {

	    String Dr = run.getDirectoryName();
	    String Sq = resPeptide.getSequence();
	    String filename = resPeptide.getFilename();

	    String[] fields = filename.split("\\.");
	    String Z = fields[fields.length - 1];

	    // Scan Number
	    String Sc = fields[fields.length - 3];

	    // Dat file
	    String Da = fields[0];

	    // clean
	    fields = null;

	    // Get our cycle
	    YatesCycle cycle = YatesCycleFactory.getInstance().getCycle(Da, run);
	    if (cycle == null) {
	        ActionErrors errors = new ActionErrors();
	        errors.add("spectra", new ActionMessage("error.yates.spectra.cyclenotfound"));
	        saveErrors( request, errors );
	        return null;
	    }


	    String Sd = ".";
	    String directory_filebase = Da + "/" + Da;
	    String fullpath = Dr + "/" + directory_filebase + "." + Sc + ".*." + Z;
	    directory_filebase = null;

	    String SqMod = "";

	    // Parse the DTA information
	    YatesSpectraMS2Parser ms2parser = null;
	    try {
	        ms2parser = new YatesSpectraMS2Parser();
	        ms2parser.parseMS2Spectra(cycle, Sc, Z);
	    } catch (Exception e) {
	        ActionErrors errors = new ActionErrors();
	        errors.add("spectra", new ActionMessage("error.yates.spectra.ms2error", e.getMessage()));
	        saveErrors( request, errors );
	        return null;
	    }

	    // Parse the SQT information
	    YatesSpectraSQTParser sqtparser = null;
	    try {
	        sqtparser = new YatesSpectraSQTParser();
	        sqtparser.parseSQTData(cycle, Sc, Z);
	    } catch (Exception e) {
	        ActionErrors errors = new ActionErrors();
	        errors.add("spectra", new ActionMessage("error.yates.spectra.sqterror", e.getMessage()));
	        saveErrors( request, errors );
	        return null;
	    }


	    // Set up some values to send to the View Page
	    LinkedList masslist = ms2parser.getMasslist();
	    LinkedList intlist = ms2parser.getIntlist();
	    List <String> params = new LinkedList<String>();

	    request.setAttribute("firstMass", masslist.getFirst());
	    request.setAttribute("firstCharge", intlist.getFirst());
	    request.setAttribute("datfile", Da);
	    request.setAttribute("scanNumber", Sc);
	    request.setAttribute("database", sqtparser.getDbase());


	    // SET UP THE PARAM LIST TO PASS TO THE APPLET ON THE VIEW PAGE
	    params.add("<PARAM NAME=\"MatchSeq\" VALUE=\"" + Sq + "\">");
	    params.add("<PARAM NAME=\"PreMPlusH\" VALUE=\"" + masslist.removeFirst() + "\">");
	    params.add("<PARAM NAME=\"PreZ\" VALUE=\"" + intlist.removeFirst() + "\">");

	    // Handle masses and charges
	    Iterator iter = masslist.iterator();
	    int i = 0;
	    Object[] masses = masslist.toArray();
	    Object[] ints = intlist.toArray();
	    while (iter.hasNext()) {
	        i++;
	        params.add("<PARAM NAME=\"MZ" + i + "\" VALUE=\"" + (String)(masses[i-1]) + "\">");
	        params.add("<PARAM NAME=\"Int" +i + "\" VALUE=\"" + (String)(ints[i-1]) + "\">");
	        iter.next();
	    }
	    masses = null;
	    ints = null;

	    // Handle static mod
	    LinkedList staticmod = sqtparser.getStaticmod();
	    iter = staticmod.iterator();
	    i = 1;
	    while (iter.hasNext()) {
	        String smod = (String)(iter.next());
	        String[] tmp = smod.split("=");
	        if (tmp.length < 2) continue;

	        String smr = tmp[0];
	        String smm = tmp[1];
	        params.add("<PARAM NAME=\"static_mod_"+smr+"\" VALUE=\"" +smm+ "\">");
	        
//	        params.add("<PARAM NAME=\"SMM" + i + "\" VALUE=\"" + smm + "\">");
//	        params.add("<PARAM NAME=\"SMR" + i + "\" VALUE=\"" + smr + "\">");

	        i++;
	    }
	    staticmod = null;


	    
	    // Handle diff mods
	    LinkedList diffmod = sqtparser.getDiffmod();
	    iter = diffmod.iterator();
	    i = 1;
	    while (iter.hasNext()) {
	        String dmod = (String)(iter.next());
	        String[] tmp = dmod.split("=");
	        String dmm = null;
	        String dms = null;

	        if (tmp.length == 2) {

	            // New SEQUEST format
	            dmm = tmp[0];
	            // last character is the modification symbol, remove it
	            dmm = dmm.substring(0, dmm.length() -1);
	            dms = tmp[1];
	        } else {

	            // Old SEQUEST format
	            Pattern p = Pattern.compile("[A-Z0-9]+?(\\S)\\s([\\+\\-]\\d+?\\.\\d+)");
	            Matcher m = p.matcher(dmod);
	            if (m.matches()) {
	                dmm = m.group(1);
	                dms = m.group(2);
	            }
	        }

	        if (dmm != null && dms != null) {
	            params.add("<PARAM NAME=\"mod"+(i)+"\" VALUE=\"" + dms + "\">");
	            params.add("<PARAM NAME=\"mod"+(i++)+"residues\" VALUE=\"" + dmm + "\">");       
//	            params.add("<PARAM NAME=\"DMM" + i + "\" VALUE=\"" + dmm + "\">");
//	            params.add("<PARAM NAME=\"DMS" + i + "\" VALUE=\"" + dms + "\">");
	        }	
	    }
	     

//	    params.add("<PARAM NAME=\"CPepMod\" VALUE=\"0.0\">\n");
//	    params.add("<PARAM NAME=\"NPepMod\" VALUE=\"0.0\">\n");
//	    params.add("<PARAM NAME=\"CProtMod\" VALUE=\"0.0\">\n");
//	    params.add("<PARAM NAME=\"NProtMod\" VALUE=\"0.0\">\n");
	    params.add("<PARAM NAME=\"AvgForFrag\" VALUE=\"" + sqtparser.getAvgForFrag() + "\">");
	    params.add("<PARAM NAME=\"AvgForParent\" VALUE=\"" + sqtparser.getAvgForParent() + "\">");		

	    /*
			List seqMatches = sqtparser.getSequestLines();
			iter = seqMatches.iterator();
			int check;
			i = 0;
			while (iter.hasNext()) {
				String thePeptide = ((YatesSpectraSQTParser.SequestLine)iter.next()).getPeptide();
				String nakedPeptide = thePeptide;
				int whichPeptide = 0;
				Pattern p = Pattern.compile("\\.(\\S+?)\\.");
				Matcher m = p.matcher(nakedPeptide);
				if (m.matches())
					nakedPeptide = m.group(1);

				String nakedSq = Sq;
				nakedSq = nakedSq.replaceAll("\\W", "");
				nakedPeptide = nakedPeptide.replaceAll("\\W", "");

				if (nakedPeptide.equals("nakedSq")) {
					check = i + 1;
					params.add("<PARAM NAME=\"MatchSeq\" VALUE=\"" + thePeptide + "\">");
					break;
				}
				i++;
			}
	     */
	    

	    // Clean up
	    masslist = null;
	    intlist = null;
	    sqtparser = null;
	    ms2parser = null;
	    // seqMatches = null;
	    iter = null;

	    return params;
	}

}