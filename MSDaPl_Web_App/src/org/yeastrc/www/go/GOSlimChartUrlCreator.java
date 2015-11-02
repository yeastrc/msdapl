/**
 * 
 */
package org.yeastrc.www.go;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.bio.go.slim.GOSlimAnalysis;
import org.yeastrc.bio.go.slim.GOSlimTermResult;
import org.yeastrc.www.util.RoundingUtils;

/**
 * GOSlimChartUrlCreator.java
 * @author Vagisha Sharma
 * May 26, 2010
 * 
 */
public class GOSlimChartUrlCreator {

	private GOSlimChartUrlCreator() {}
	
	public static String getPieChartUrl(GOSlimAnalysis analysis, int maxSlices, boolean useGroupCounts) {
		
		StringBuilder buf = new StringBuilder();
		buf.append("http://chart.apis.google.com/chart?cht=p&chs=800x300&chco=003388,BBBB00");
		
		String data = "";
		String labels = "";
		String legend= "";
		List<GOSlimTermResult> slimTerms = analysis.getTermNodesMinusRootNodes();
		
		if(useGroupCounts) {
			
			Collections.sort(slimTerms, new Comparator<GOSlimTermResult>() {
				@Override
				public int compare(GOSlimTermResult o1, GOSlimTermResult o2) {
					return Integer.valueOf(o2.getAnnotatedGroupCount()).compareTo(o1.getAnnotatedGroupCount());
				}
			});
		}
		else {
			
			Collections.sort(slimTerms, new Comparator<GOSlimTermResult>() {
				@Override
				public int compare(GOSlimTermResult o1, GOSlimTermResult o2) {
					return Integer.valueOf(o2.getAnnotatedProteinCount()).compareTo(o1.getAnnotatedProteinCount());
				}
			});
		}
		
		int totalAnnotCount = 0;
		// Look at the top "maxSlices" terms
		for(int i = 0; i < maxSlices; i++) {
			if(i >= slimTerms.size())
				break;
			GOSlimTermResult term = slimTerms.get(i);
			if(useGroupCounts)
				totalAnnotCount += term.getAnnotatedGroupCount();
			else
				totalAnnotCount += term.getAnnotatedProteinCount();
		}
		
		for(int i = 0; i < maxSlices; i++) {
			if(i >= slimTerms.size())
				break;
			GOSlimTermResult term = slimTerms.get(i);
			
			if(term.getAnnotatedProteinCount() == 0)
				continue;
			
			int annotCountForTerm = 0;
			if(useGroupCounts)
				annotCountForTerm = term.getAnnotatedGroupCount();
			else
				annotCountForTerm = term.getAnnotatedProteinCount();
			
			int frac = (int)Math.round((annotCountForTerm * 100.0) / (double)totalAnnotCount);
			data += ","+frac;
			labels += "|"+annotCountForTerm+" ("+frac+"%)";
			legend += "|"+annotCountForTerm+" ("+term.getShortName()+")";
		}
		
		if(data.length() > 0)
			data = data.substring(1); // remove first comma
		data = "&chd=t:"+data;
		
		if(labels.length() > 0)
			labels = labels.substring(1); // remove the first comma
		labels = "&chl="+labels;
		
		if(legend.length() > 0)
			legend = legend.substring(1); // remove the first comma
		legend = "&chdl="+legend;
		
		buf.append(data);
		buf.append(labels);
		buf.append(legend);
		
		return buf.toString();
	}
	
	public static String getBarChartUrl(GOSlimAnalysis analysis, int maxBars, boolean useGroupCounts) {
		
		StringBuilder buf = new StringBuilder();
		buf.append("http://chart.apis.google.com/chart?cht=bhs&chxt=x,y&chs=450x260&chco=008888");
		
		String data = "";
		String labels = "";
		List<GOSlimTermResult> slimTerms = analysis.getTermNodesMinusRootNodes();
		
		
		if(useGroupCounts) {
			
			Collections.sort(slimTerms, new Comparator<GOSlimTermResult>() {
				@Override
				public int compare(GOSlimTermResult o1, GOSlimTermResult o2) {
					return Integer.valueOf(o2.getAnnotatedGroupCount()).compareTo(o1.getAnnotatedGroupCount());
				}
			});
		}
		else {
			
			Collections.sort(slimTerms, new Comparator<GOSlimTermResult>() {
				@Override
				public int compare(GOSlimTermResult o1, GOSlimTermResult o2) {
					return Integer.valueOf(o2.getAnnotatedProteinCount()).compareTo(o1.getAnnotatedProteinCount());
				}
			});
		}

		double maxValue = 0.0;
		for(int i = 0; i < maxBars; i++) {
			if(i >= slimTerms.size())
				break;
			GOSlimTermResult term = slimTerms.get(i);
			if(term.getAnnotatedProteinCount() == 0)
				continue;
			
			double annotPercForTerm = 0;
			if(useGroupCounts) {
				int totalGroups = analysis.getTotalProteinGroupCount();
				annotPercForTerm = (RoundingUtils.getInstance().roundOne(((double)term.getAnnotatedGroupCount()*100.0) / (double)totalGroups));
				
			}
			else {
				int totalProteins = analysis.getTotalProteinCount();
				annotPercForTerm = (RoundingUtils.getInstance().roundOne(((double)term.getAnnotatedProteinCount()*100.0) / (double)totalProteins));
			}
			
			
			
			data += ","+annotPercForTerm;
			labels = "|"+term.getShortName()+labels;
			maxValue = Math.max(maxValue, annotPercForTerm	);
		}
		
		if(data.length() > 0)
			data = data.substring(1); // remove first comma
		data = "&chd=t:"+data;
		
		labels = "&chxl=1:"+labels;
		
		
		buf.append(data);
		buf.append(labels);
		
		int maxV = (int) Math.ceil(maxValue);
		maxV = maxV + (10 - maxV%10);
		
		int step = getStep(maxV);
		
		buf.append("&chxr=0,0,"+maxV+","+step);
		buf.append("&chds=0,"+maxV);
		buf.append("&chbh=12"); // width of bars
		buf.append("&chm=N**%,000000,0,-1,11"); // labels for each bar
		
		return buf.toString();
	}
	
	private static int getStep(int maxValue) {
		int step = Math.min(10, maxValue / 10);
		int[] steps = {10,5,2,1};
		int diff = Integer.MAX_VALUE;
		int bstep = step;
		for(int s: steps) {
			if(Math.abs(s - step) < diff) {
				diff = Math.abs(s - step);
				bstep = s;
			}
		}
		return bstep;
	}
}
