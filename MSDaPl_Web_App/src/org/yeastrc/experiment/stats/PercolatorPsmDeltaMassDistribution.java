/**
 * PercolatorPsmDeltaMassDistribution.java
 * @author Vagisha Sharma
 * Oct 5, 2010
 */
package org.yeastrc.experiment.stats;

import java.util.Collections;
import java.util.List;

import org.yeastrc.ms.service.percolator.stats.Bin;
import org.yeastrc.ms.service.percolator.stats.PeptideMassDiffDistributionCalculator;
import org.yeastrc.www.util.RoundingUtils;

/**
 * 
 */
public class PercolatorPsmDeltaMassDistribution {

	private PeptideMassDiffDistributionCalculator calculator;
	
	public void setCalculator(PeptideMassDiffDistributionCalculator calculator) {
		this.calculator = calculator;
	}
	
	public double getMinDiff() {
		return RoundingUtils.getInstance().roundFour(calculator.getMinDiff());
	}

	public double getMaxDiff() {
		return RoundingUtils.getInstance().roundFour(calculator.getMaxDiff());
	}

	public double getMean() {
		return RoundingUtils.getInstance().roundFour(calculator.getMean());
	}

	public double getStdDev() {
		return RoundingUtils.getInstance().roundFour(calculator.getStdDev());
	}
	
	public int getTotalCount() {
		return calculator.getTotalCount();
	}
	
	public String getGoogleChartUrl() {
	
		StringBuilder buf = new StringBuilder();
		// http://chart.apis.google.com/chart?cht=bhs&chxt=x,y&chs=450x260&chco=008888
		// &chd=t:63.2,59.2,37.3,30.9,29.5,23.6,22.9,22.2,16.3,9.7,9.4,9.4,8.3,8.3,8.0
		// &chxl=1:|carbohydrate%20metabolic%20process|protein%20transport|response%20to%20stress|DNA%20metabolic%20process|catabolic%20process|regulation%20of%20biological%20process|translation|organelle%20organization|transport|nucleobase,%20nucleoside,%20nucleotide%20an...|cellular%20component%20organization|protein%20metabolic%20process|biosynthetic%20process|primary%20metabolic%20process|metabolic%20process
		// &chxr=0,0,70,5
		// &chds=0,70
		// &chbh=12
		// &chm=N**%,000000,0,-1,11
		
		buf.append("http://chart.apis.google.com/chart?cht=bhs&chxt=x,y&chs=450x260&chco=76A4FB");
		buf.append("&chbh=12");
		
		List<Bin> bins = calculator.getBins();
		Collections.sort(bins);
	
		buf.append("&chd=t:");
		String data = "";
		int maxCount = 0;
		for(Bin bin: bins) {
			data += ","+bin.getBinCount();
			if(bin.getBinCount() > maxCount)
				maxCount = bin.getBinCount();
		}
		if(data.length() > 0)
			data = data.substring(1);
		buf.append(data);
		
		buf.append("&chxl=1:");
		data = "";
		// labels go in reverse order of data; 
		for(int i = bins.size() - 1; i >= 0; i--) {
			Bin bin = bins.get(i);
			data += "|"+bin.getBinStart()+" - "+bin.getBinEnd();
		}
		buf.append(data);
		
		buf.append("&chds=0,"+maxCount);
		buf.append("&chxr=0,0,"+maxCount);
		
		return buf.toString();
	}
	
	public static void main(String[] args) {
		
		int analysisId = 97;
		double qvalue = 0.01;
		
		PeptideMassDiffDistributionCalculator calculator = new PeptideMassDiffDistributionCalculator(analysisId, qvalue, false);
		calculator.calculate();
		List<Bin> bins = calculator.getBins();
		System.out.println("Min: "+calculator.getMinDiff());
		System.out.println("Max: "+calculator.getMaxDiff());
		System.out.println("Mean: "+calculator.getMean());
		System.out.println("StdDev: "+calculator.getStdDev());
		int total = 0;
		for(Bin bin: bins) {
			System.out.println(bin);
			total += bin.getBinCount();
		}
		System.out.println("Total filtered results: "+total);
		
		PercolatorPsmDeltaMassDistribution result = new PercolatorPsmDeltaMassDistribution();
		result.setCalculator(calculator);
		System.out.println(result.getGoogleChartUrl());
	}
	
}
