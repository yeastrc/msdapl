/**
 * PeptideAAFrequencyPlotUrl.java
 * @author Vagisha Sharma
 * Mar 1, 2011
 */
package org.yeastrc.ms.service.stats;

import java.util.HashSet;
import java.util.Set;

import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResult;
import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResultBuilder;
import org.yeastrc.ms.domain.general.EnzymeFactory;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.util.BaseAminoAcidUtils;

/**
 * 
 */
public class PeptideAAFrequencyGooglePlotUrlBuilder {

	private PeptideAAFrequencyGooglePlotUrlBuilder() {}
	
	public static String getUrl(PeptideTerminalAAResult result) {
		
		StringBuilder buf = new StringBuilder();
		// Example: 
		// https://chart.googleapis.com/chart?cht=bhg&chs=400x300&chbh=r,0.1,0.3&chco=4d89f9,00B88A&chd=t:0.4,60.2,0.6,36.4|0.5,61.2,0.2,35.2&chxt=x,y,x&chxl=1:|%28Arginine%29+R|%28Methionine%29+M|%28Lysine%29+K|%28Phenyl-ananine%29+F|2:|Frequency+%28%%29|&chxp=2,50&chtt=Amino+Acid+Frequency+%28Top+Three%29&chdl=N-term+-1|C-term&chdlp=t&chm=N,000000,0,,10|N,000000,1,,10&chf=c,s,EFEFEF
		buf.append("https://chart.googleapis.com/chart?");
		buf.append("cht=bhg"); // horizontal bar chart (grouped)
		buf.append("&");
		buf.append("chs=400x300"); // chart size
		buf.append("&");
		buf.append("chbh=r,0.1,0.3"); // bar width and spacing
		buf.append("&");
		buf.append("chco=4d89f9,00B88A"); // chart colors
		
		// start chart data
		buf.append("&");
		buf.append("chd=t:");
		
		BaseAminoAcidUtils aaUtils = new BaseAminoAcidUtils();
		char[] aaArr = aaUtils.getAminoAcidChars();
		Set<Character> topThree = new HashSet<Character>();
		
		
		MsEnzyme enzyme = result.getEnzyme();
		if(enzyme.getSense() == Sense.CTERM) {
			
			Set<Character> topThreeNtermMinusOne = result.getTopThreeAminoAcidsNtermMinusOne();
			Set<Character> topThreeCterm = result.getTopThreeAminoAcidsCterm();
			topThree.addAll(topThreeNtermMinusOne);
			topThree.addAll(topThreeCterm);
		}
		else if(enzyme.getSense() == Sense.NTERM) {
			
			// TODO
		}
		
		StringBuilder series1 = new StringBuilder();
		StringBuilder series2 = new StringBuilder();
		
		
		if(enzyme.getSense() == Sense.CTERM) {
			
			for(char aa: aaArr) {
				
				if(!topThree.contains(aa))
					continue;
				
				series1.append(",");
				series1.append(result.getNtermMinusOneFreqForAA(aa));
				
				series2.append(",");
				series2.append(result.getCtermFreqForAA(aa));
			}
		}
		else if(enzyme.getSense() == Sense.NTERM) {
			
			// TODO
		}
		
		series1.deleteCharAt(0); // remove the first comma
		series2.deleteCharAt(0); // remove the first comma
		
		buf.append(series1);
		buf.append("|");
		buf.append(series2);
		
		buf.append("&");
		buf.append("chxt=x,y,x"); // draw axis labels; the second x is for x-axis title
		buf.append("&");
		buf.append("chxl=1:|");
		
		for(int i = aaArr.length - 1; i >= 0; i--) {
			
			if(!topThree.contains(aaArr[i]))
				continue;
			
			buf.append("("+aaUtils.getFullName(aaArr[i])+")+"+aaArr[i]+"|");
		}
		
		buf.append("2:|Frequency+(%)|");
		buf.append("&");
		buf.append("chxp=2,50"); // position of the x-axis title
		
		buf.append("&");
		buf.append("chtt=Amino+Acid+Frequency"); // chart title
		buf.append("&");
		buf.append("chdl=N-term+-1|C-term"); // legend
		buf.append("&");
		buf.append("chdlp=t"); // place legend at the bottom of the chart
		buf.append("&");
		buf.append("chm=N,000000,0,,10|N,000000,1,,10"); // markers on each bar
		buf.append("&");
		buf.append("chf=c,s,EFEFEF"); // chart background fill
		//buf.append("&");
		//buf.append("chg=10,25");
		
		
		return buf.toString();
	}
	
	public static void main(String[] args) {
		
        PeptideTerminalAAResultBuilder builder = new PeptideTerminalAAResultBuilder(0, EnzymeFactory.getTrypsin());
        builder.setTotalResultCount(124056);
        builder.setScoreCutoff(0.01);
        builder.setScoreType("PERC_PSM_QVAL");
        
		builder.setNtermMinusOneCount('D', 235);
		builder.setNtermMinusOneCount('E', 66);
		builder.setNtermMinusOneCount('F', 512);
		builder.setNtermMinusOneCount('G', 225);
		builder.setNtermMinusOneCount('A', 366);
		builder.setNtermMinusOneCount('C', 130);
		builder.setNtermMinusOneCount('L', 225);
		builder.setNtermMinusOneCount('M', 762);
		builder.setNtermMinusOneCount('N', 603);
		builder.setNtermMinusOneCount('H', 230);
		builder.setNtermMinusOneCount('I', 24);
		builder.setNtermMinusOneCount('K', 74676);
		builder.setNtermMinusOneCount('T', 75);
		builder.setNtermMinusOneCount('W', 18);
		builder.setNtermMinusOneCount('V', 25);
		builder.setNtermMinusOneCount('Q', 80);
		builder.setNtermMinusOneCount('P', 3);
		builder.setNtermMinusOneCount('S',62);
		builder.setNtermMinusOneCount('R',45162);
		builder.setNtermMinusOneCount('Y',324);
		
		builder.setCtermCount('D', 227);
		builder.setCtermCount('E', 171);
		builder.setCtermCount('F', 602);
		builder.setCtermCount('G', 109);
		builder.setCtermCount('A', 445);
		builder.setCtermCount('C', 102);
		builder.setCtermCount('L', 254);
		builder.setCtermCount('M', 254);
		builder.setCtermCount('N', 522);
		builder.setCtermCount('H', 485);
		builder.setCtermCount('I', 189);
		builder.setCtermCount('K', 75879);
		builder.setCtermCount('T', 31);
		builder.setCtermCount('W', 48);
		builder.setCtermCount('V', 254);
		builder.setCtermCount('Q', 135);
		builder.setCtermCount('P', 15);
		builder.setCtermCount('S',60);
		builder.setCtermCount('R',43700);
		builder.setCtermCount('Y',348);
		
		
		String url = PeptideAAFrequencyGooglePlotUrlBuilder.getUrl(builder.getResult());
		
		System.out.println(url);
	}
}
