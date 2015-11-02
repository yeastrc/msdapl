/**
 * JSONInstrumentUsageGetter.java
 * @author Vagisha Sharma
 * May 23, 2011
 */
package org.uwpr.www.instrumentlog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.uwpr.costcenter.InvoiceInstrumentUsage;
import org.uwpr.costcenter.InvoiceInstrumentUsageDAO;
import org.uwpr.instrumentlog.DateUtils;
import org.uwpr.instrumentlog.InstrumentColors;
import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.MsInstrumentUtils;
import org.uwpr.instrumentlog.UsageBlock;

/**
 * For use with fullcalendar
 */
public class JSONInstrumentUsageGetter {

	private JSONInstrumentUsageGetter() {}
	
	private static final JSONInstrumentUsageGetter instance = new JSONInstrumentUsageGetter();
	
	public static JSONInstrumentUsageGetter getInstance() {
		return instance;
	}
	
	/**
	 * Returned blocks will be editable only if they are for the given projectId
	 * Blocks for other projects will be marked as "busyTime".
	 * Block details (LabDirector, project name etc.) will not be added
	 * @param instrumentId
	 * @param projectId
	 * @param startDate
	 * @param endDate
	 */
	public JSONArray getForInstrumentProject(int instrumentId, int projectId, Date startDate, Date endDate) throws SQLException {
		
		return getForInstrumentProject(instrumentId, projectId, startDate, endDate, 
				true,   // blocks of other project will be gray in color
				false    // details will be made available for all blocks.
				);
	}
	
	/**
	 * Returns a list of blocks (all editable) with all details (LabDirector, project name etc.) added.
	 * @param instrumentId
	 * @param projectId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws SQLException
	 */
	public JSONArray getForInstrumentProjectForAdmin(int instrumentId, int projectId, Date startDate, Date endDate) throws SQLException {
		
		return getForInstrumentProject(instrumentId, projectId, startDate, endDate, 
				true,   // blocks of other project will be gray in color
				false   // details will be made available for ALL blocks
				);
	}

	public JSONArray getForInstrument(int instrumentId, Date startDate, Date endDate) throws SQLException {
		
		return getForInstrumentProject(instrumentId, 0, startDate, endDate,
				false,  // blocks of other project will NOT be gray in color
				false    // details will be made available for ALL blocks
				);
	}

	public JSONArray getForInstrumentForAdmin(int instrumentId, Date startDate, Date endDate) throws SQLException {
		
		return getForInstrumentProject(instrumentId, 0, startDate, endDate, 
				false,  // blocks of other project will NOT be gray in color
				false   // details will be made available for ALL blocks
				);
	}
	
	public JSONArray getForAdmin(Date startDate, Date endDate, Set<Integer> instrumentIds) throws SQLException {
		
		JSONArray eventArray = new JSONArray();
		
		// get a list of instruments
		List<MsInstrument> instruments = MsInstrumentUtils.instance().getMsInstruments();
		for(MsInstrument instrument: instruments) {
			if(instrumentIds != null && !instrumentIds.contains(instrument.getID()))
				continue;
			JSONArray instrEventArray = getForInstrumentForAdmin(instrument.getID(), startDate, endDate);
			eventArray.addAll(instrEventArray);
		}
		return eventArray;
	}
	
	public JSONArray get(Date startDate, Date endDate, Set<Integer> instrumentIds) throws SQLException {
		
		JSONArray eventArray = new JSONArray();
		
		// get a list of instruments
		List<MsInstrument> instruments = MsInstrumentUtils.instance().getMsInstruments();
		for(MsInstrument instrument: instruments) {
			if(instrumentIds != null && !instrumentIds.contains(instrument.getID()))
				continue;
			JSONArray instrEventArray = getForInstrument(instrument.getID(), startDate, endDate);
			eventArray.addAll(instrEventArray);
		}
		return eventArray;
	}

	/**
	 * Blocks for other projects will be marked as "busyTime" if markBusy is true.
	 * Block details (LabDirector, project name etc.) will be added if addBlockDetails is true.
	 */
	private JSONArray getForInstrumentProject(int instrumentId, int projectId, Date startDate, Date endDate,
			boolean useGrayForOtherProjects, boolean noDetailsForOtherProjects) throws SQLException {
		
		//Date startDate = DateUtils.getDate(1, month, year);
		//Date endDate = DateUtils.getDate(DateUtils.getDaysInMonth(month, year), month, year);
		
		String instrumentColor = InstrumentColors.getColor(instrumentId);
		instrumentColor = "#"+instrumentColor;
		
		List<UsageBlock> usageBlocks = MsInstrumentUtils.instance().getUsageBlocksForInstrument(instrumentId, startDate, endDate,
																								false // do not truncate blocks to fit 
																								);	  // start and end dates
		
		// sort the blocks by projectId
		Collections.sort(usageBlocks, new UsageBlockComparatorByProject());
		
		
		JSONArray eventArray = new JSONArray();
		
		List<UsageBlock> projectBlocks = new ArrayList<UsageBlock>();
		int lastProjectId = 0;
		
		for(UsageBlock block: usageBlocks) {
			
			if(block.getProjectID() != lastProjectId) {
				
				if(lastProjectId != 0) {
					
					List<JSONObject> events = getForProjectBlocks(projectBlocks, projectId, 
							instrumentColor, useGrayForOtherProjects, 
							noDetailsForOtherProjects);
					
					if(events != null)
						eventArray.addAll(events);
					
					
					projectBlocks = new ArrayList<UsageBlock>();
				}
			}
			
			lastProjectId = block.getProjectID();
			projectBlocks.add(block);

		}
		
		// last one
		if(projectBlocks.size() > 0) {
			List<JSONObject> events = getForProjectBlocks(projectBlocks, projectId, 
					instrumentColor, useGrayForOtherProjects, 
					noDetailsForOtherProjects);
			
			if(events != null)
				eventArray.addAll(events);
		}
		
		
		return eventArray;
	}
	
	private List<JSONObject> getForProjectBlocks(List<UsageBlock> projectBlocks, 
			int userProjectId, 
			String instrumentColor,
			boolean useGrayForOtherProjects,
			boolean noDetailsForOtherProjects) {
		
		if(projectBlocks == null || projectBlocks.size() == 0)
			return null;
		
		// sort blocks by start date
		Collections.sort(projectBlocks, new UsageBlockComparatorByStartDate());
		
		
		List<UsageBlock> contiguousBlocks = new ArrayList<UsageBlock>();
		
		Iterator<UsageBlock> iter = projectBlocks.iterator();
		
		List<JSONObject> jsonEvents = new ArrayList<JSONObject>();
		
		while(projectBlocks.size() > 0) {
			
			while(iter.hasNext()) {
				
				UsageBlock block = iter.next();
//				if(contiguousBlocks.size() > 0) {
//					
//					UsageBlock lastAddedBlock = contiguousBlocks.get(contiguousBlocks.size() - 1);
//					
//					if(lastAddedBlock.getEndDate().equals(block.getStartDate())) {
//						contiguousBlocks.add(block);
//						iter.remove();
//					}
//					
//					else if(lastAddedBlock.getEndDate().after(block.getStartDate())) {
//						continue;
//					}
//					else
//						break;
//				}
//				else {
					contiguousBlocks.add(block);
					iter.remove();
					break;
//				}
			}
			
			JSONObject event = getForContiguousBlocks(contiguousBlocks, userProjectId, 
					instrumentColor, useGrayForOtherProjects, noDetailsForOtherProjects);
			
			if(event != null)
				jsonEvents.add(event);
			
			contiguousBlocks = new ArrayList<UsageBlock>();
			
			iter = projectBlocks.iterator();
		}
		
		
		// last one
		if(contiguousBlocks.size() > 0) {
			
			JSONObject event = getForContiguousBlocks(contiguousBlocks, userProjectId, 
					instrumentColor, useGrayForOtherProjects, noDetailsForOtherProjects);
			
			if(event != null)
				jsonEvents.add(event);
		}
		
		return jsonEvents;
	}
	
	private JSONObject getForContiguousBlocks(List<UsageBlock> blocks, 
			int userProjectId, 
			String instrumentColor,
			boolean useGrayForOtherProjects,
			boolean noDetailsForOtherProjects) {
		
		if(blocks == null || blocks.size() == 0)
			return null;
		
		// ASSUME blocks are already sorted by start date
		Date sd = blocks.get(0).getStartDate();
		Date ed = blocks.get(blocks.size() - 1).getEndDate();


		int d = DateUtils.getDay(sd);
		int m = DateUtils.getMonth(sd);
		int y = DateUtils.getYear(sd);
		int sh = DateUtils.getHour(sd);
		int eh = DateUtils.getHour(ed);


		//String event_id = d+"_"+m+"_"+y+"_"+sh+"_"+eh;
		JSONObject event = new JSONObject();
		event.put("id", blocks.get(0).getID());
		event.put("projectId", Integer.valueOf(blocks.get(0).getProjectID()));
		event.put("instrumentId", Integer.valueOf(blocks.get(0).getInstrumentID()));
		event.put("title", String.valueOf(blocks.get(0).getProjectID()));
		event.put("start", sd.toString());
		event.put("end", ed.toString());
		event.put("allDay", Boolean.FALSE);
		event.put("editable", Boolean.FALSE);
		
		JSONArray blockEvents = new JSONArray();
		//String allIdString = "";
		InvoiceInstrumentUsageDAO invoiceInstrumentUsageDao = InvoiceInstrumentUsageDAO.getInstance();
		for(UsageBlock block: blocks) {
			JSONObject blockObject = new JSONObject();
			blockObject.put("id",block.getID());
			blockObject.put("label",block.getStartDateFormated()+" to "+block.getEndDateFormated());
			// If this block has already been billed it cannot be deleted or edited even by admins
			InvoiceInstrumentUsage billedBlock = null;
			try {
				billedBlock = invoiceInstrumentUsageDao.getInvoiceBlock(block.getID());
				if(billedBlock == null) {
					blockObject.put("editable", true);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			
			blockEvents.add(blockObject);
			
			//allIdString += ","+block.getID();
		}
		event.put("blocks", blockEvents);
		//event.put("all_ids", allIdString);

		event.put("borderColor", instrumentColor);

		if(blocks.get(0).getProjectID() != userProjectId && useGrayForOtherProjects) {
			event.put("backgroundColor", "gray");
		}
		else {
			event.put("backgroundColor", instrumentColor);
		}

		if(blocks.get(0).getProjectID() != userProjectId && noDetailsForOtherProjects) {
			event.put("hasDetails", Boolean.FALSE);

		}
		else {
			event.put("hasDetails", Boolean.TRUE);
			event.put("projectId", Integer.valueOf(blocks.get(0).getProjectID()));
			event.put("projectTitle", blocks.get(0).getProjectTitle());
			event.put("projectPI", blocks.get(0).getProjectPI());
		}

//		if(blocks.get(0).getProjectID() == userProjectId) {
//			event.put("projectId", Integer.valueOf(blocks.get(0).getProjectID()));
//			event.put("projectTitle", blocks.get(0).getProjectTitle());
//			event.put("projectPI", blocks.get(0).getProjectPI());
//		}
//		
//		else if(addAllBlockDetails) {
//			event.put("projectId", Integer.valueOf(blocks.get(0).getProjectID()));
//			event.put("projectTitle", blocks.get(0).getProjectTitle());
//			event.put("projectPI", blocks.get(0).getProjectPI());
//		}
		
		return event;
	}
}
