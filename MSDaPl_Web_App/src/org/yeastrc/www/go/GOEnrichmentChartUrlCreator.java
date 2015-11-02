/**
 * 
 */
package org.yeastrc.www.go;

import java.util.List;

import org.yeastrc.www.util.RoundingUtils;

/**
 * GOEnrichmentChartUrlCreator.java
 * @author Vagisha Sharma
 * Jun 11, 2010
 * 
 */
public class GOEnrichmentChartUrlCreator {

	private GOEnrichmentChartUrlCreator() {}
	
	public static String getPieChartUrl(GOEnrichmentOutput output, int maxSlices) {
		
		StringBuilder buf = new StringBuilder();
		buf.append("http://chart.apis.google.com/chart?cht=p&chs=800x300&chco=330088,BBBB00");
		
		String data = "";
		String labels = "";
		String legend= "";
		List<EnrichedGOTerm> enrichedTerms = output.getEnrichedTerms();
		
		int totalAnnot = 0;
		for(int i = 0; i < maxSlices; i++) {
			if(i >= enrichedTerms.size())
				break;
			EnrichedGOTerm term = enrichedTerms.get(i);
			totalAnnot += term.getNumAnnotatedProteins();
		}
		
		for(int i = 0; i < maxSlices; i++) {
			if(i >= enrichedTerms.size())
				break;
			EnrichedGOTerm term = enrichedTerms.get(i);
			if(term.getNumAnnotatedProteins() == 0)
				continue;
			
			int frac = (int)Math.round((term.getNumAnnotatedProteins() * 100.0) / totalAnnot);
			data += ","+frac;
			labels += "|"+term.getNumAnnotatedProteins()+" ("+frac+"%)";
			legend += "|"+term.getNumAnnotatedProteins()+" ("+term.getShortName()+")";
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
	
	public static double getPercent(double part, double all) {
		return (RoundingUtils.getInstance().roundOne((part*100.0) / all));
	}
	
	public static String getBarChartUrl(GOEnrichmentOutput output, int maxBars) {
		
		StringBuilder buf = new StringBuilder();
		buf.append("http://chart.apis.google.com/chart?cht=bhs&chxt=x,y&chs=450x260&chco=884488");
		
		String data = "";
		String labels = "";
		List<EnrichedGOTerm> enrichedTerms = output.getEnrichedTerms();
		
		double maxValue = 0.0;
		for(int i = 0; i < maxBars; i++) {
			if(i >= enrichedTerms.size())
				break;
			EnrichedGOTerm term = enrichedTerms.get(i);
			if(term.getNumAnnotatedProteins() == 0)
				continue;
			
			double val = getPercent((double)term.getNumAnnotatedProteins(), (double)output.getNumInputAnnotatedProteins());
			data += ","+val;
			labels = "|"+term.getShortName()+labels;
			maxValue = Math.max(maxValue, val);
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
