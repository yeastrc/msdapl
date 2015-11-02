/**
 * DistributionPlotter.java
 * @author Vagisha Sharma
 * Dec 8, 2009
 * @version 1.0
 */
package org.yeastrc.experiment.stats;

import java.io.IOException;

/**
 * 
 */
public class DistributionPlotter {

    
	private static DistributionPlotter instance = null;
	private DistributionPlotter() {}
	
	public static synchronized DistributionPlotter getInstance() {
		if(instance == null)
			instance = new DistributionPlotter();
		return instance;
	}
	
    public String plotGoogleChartForPSM_RTDistribution(int analysisId, double qValCutoff) {
        
        PsmRetTimeDistributionGetter dg = new PsmRetTimeDistributionGetter(analysisId, qValCutoff);
        PsmRetTimeDistribution distr = dg.getDistribution();
        return plotGoogleChartForPSM_RTDistribution(distr);
    }
    
    public String plotGoogleChartForPSM_RTDistribution(PsmRetTimeDistribution result) {
        
        int[] distr = result.getFilteredPsmDistribution();
        int[] allDistr = result.getAllPsmDistribution();
        
        double maxRT = result.getMaxRT();
        int maxPsmCount = result.getMaxPsmCount();
        
        String url = "http://chart.apis.google.com/chart?";
        url += "cht=bvs";                        // vertical stacked bar chart
        url += "&chbh=a";                        // adjust bar widths to fit in chart
        url += "&chs=750x400";                  // chart size
        //   url += "&chs=500x325";                  // chart size
        // url += "&chdl="+"PSMs with qvalue<="+result.getScoreCutoff()+"|All Percolator PSMs"; // legend
        url += "&chdl="+result.getChartLegend();
        url += "&chdlp=t";  // place the legend at the top
        url += "&chg=10,10"; // grid; x-axis and y-axis step size = 10
        url += "&chf=c,s,EFEFEF"; // fill chart area; solid fill gray
        url += "&chxt=x,y,x,y";                 // display both x and y labels
        url += "&chxl=2:|Retention Time|3:|PSMs|"; // main labels for the x and y axes
        url += "&chxp=2,50|3,50";                 // position of the main x and y labels
        int xstep = (int) (maxRT / 10);
        int ystep = (int) (maxPsmCount / 10);
        url += "&chxr=0,0,"+maxRT+","+xstep+"|1,0,"+maxPsmCount+","+ystep;        // x and y axis labels
        url += "&chds=0,"+(maxPsmCount+10)+",0,"+(maxPsmCount+10);    // data range
        url += "&chco=FF0000,0000FF";
        
        url += "&chd=t:";
        // plot the filtered numbers first
        String series = "";
        for(int i = 0; i < distr.length; i++) {
            series+=","+distr[i];
        }
        if(series.length() > 0)
            series = series.substring(1);
        
        url+=series;
        url+= "|";
        
        series = "";
        // now plot the rest of the numbers
        for(int i = 0; i < allDistr.length; i++) {
            // we are drawing a stacked bar chart. subtract the filtered numbers first
            series+=","+(allDistr[i] - distr[i]); 
        }
        if(series.length() > 0)
            series = series.substring(1);
        
        url+=series;
        return url;
    }
    
    public String plotGoogleChartForScan_RTDistribution(int analysisId, double qValCutoff) {
        
    	SpectraRetTimeDistributionGetter dg = new SpectraRetTimeDistributionGetter(analysisId, qValCutoff);
        SpectraRetTimeDistribution distr = dg.getDistribution();
        return plotGoogleChartForScan_RTDistribution(distr);
    }
    
    public String plotGoogleChartForScan_RTDistribution(SpectraRetTimeDistribution result) {
        
        int[] distr = result.getFilteredSpectraDistribution();
        int[] allDistr = result.getAllSpectraDistribution();
        
        double maxRT = result.getMaxRT();
        int maxSpectraCount = result.getMaxSpectraCount();
        
        String url = "http://chart.apis.google.com/chart?";
        url += "cht=bvs";                        // vertical stacked bar chart
        url += "&chbh=a";                        // adjust bar widths to fit in chart
        url += "&chs=750x400";                  // chart size
        //      url += "&chs=500x325";                  // chart size
        // url += "&chdl="+"MS/MS scans with good results (qvalue<="+result.getScoreCutoff()+")|All MS/MS Scans"; // legend
        url += "&chdl="+result.getChartLegend(); // legend
        url += "&chdlp=t";  // place the legent at the top
        url += "&chg=10,10"; // grid; x-axis and y-axis step size = 10
        url += "&chf=c,s,EFEFEF"; // fill chart area; solid fill gray
        url += "&chxt=x,y,x,y";                 // display both x and y labels
        url += "&chxl=2:|Retention Time|3:|Scans|"; // main labels for the x and y axes
        url += "&chxp=2,50|3,50";                 // position of the main x and y labels
        int xstep = (int) (maxRT / 10);
        int ystep = (int) (maxSpectraCount / 10);
        url += "&chxr=0,0,"+maxRT+","+xstep+"|1,0,"+maxSpectraCount+","+ystep;        // x and y axis labels
        url += "&chds=0,"+(maxSpectraCount+10)+",0,"+(maxSpectraCount+10);    // data range
        url += "&chco=008000,800080";
        
        url += "&chd=t:";
        // plot the filtered numbers first
        String series = "";
        for(int i = 0; i < distr.length; i++) {
            series+=","+distr[i];
        }
        if(series.length() > 0)
            series = series.substring(1);
        
        url+=series;
        url+= "|";
        
        series = "";
        // now plot the rest of the numbers
        for(int i = 0; i < allDistr.length; i++) {
            // we are drawing a stacked bar chart. subtract the filtered numbers first
            series+=","+(allDistr[i] - distr[i]); 
        }
        if(series.length() > 0)
            series = series.substring(1);
        
        url+=series;
        return url;
    }
    
//    public BufferedImage plotJFree(int analysisId) {
//       
//        RetTimePSMDistributionCalculator dg = new RetTimePSMDistributionCalculator(analysisId);
//        dg.calculate(0.01);
//        int[] distr = dg.getFilteredPsmDistribution();
//        int[] allDistr = dg.getAllPsmDistribution();
//        
//        XYSeries series = new XYSeries("Filtered PSMs");
//        XYSeries allSeries = new XYSeries("All PSMs");
//        
//        for(int i = 0; i < distr.length; i++) {
//            System.out.println(i+"\t"+distr[i]+"\t"+allDistr[i]);
//            double binMid = (i * dg.getBinSize());
//            System.out.println("Bin Mid: "+binMid);
//            series.add(binMid, distr[i]);
//            allSeries.add(binMid, allDistr[i]);
//        }
//        
//        XYSeriesCollection dataset = new XYSeriesCollection(series);
//        dataset.addSeries(allSeries);
//        JFreeChart chart = ChartFactory.createXYLineChart("PSM distribution", "RT", "# PSMs", 
//                dataset, PlotOrientation.VERTICAL, true, false, false);
//        
//        return chart.createBufferedImage(500, 300);
//    }
    
    
    public static void main(String[] args) throws IOException {
        
        // int analysisId = 91;
        int analysisId = 145;
        
        String googleUrl = new DistributionPlotter().plotGoogleChartForPSM_RTDistribution(analysisId, 0.01);
        System.out.println(googleUrl);
        
        googleUrl = new DistributionPlotter().plotGoogleChartForScan_RTDistribution(analysisId, 0.01);
        System.out.println(googleUrl);
        
        
//        RetTimeDistributionCalculator dg = new RetTimeDistributionCalculator(analysisId);
//        dg.calculate(0.01);
//        System.out.println("# Bins: "+dg.getNumBins());
//        System.out.println("Bin size: "+dg.getBinSize());
//        System.out.println("Distribution: ");
//        int[] distr = dg.getFilteredPsmDistribution();
//        System.out.println("Length of distribution: "+distr.length);
//        int[] allDistr = dg.getAllPsmDistribution();
//        
//        XYSeries series = new XYSeries("Filtered PSMs");
//        XYSeries allSeries = new XYSeries("All PSMs");
//        
//        double halfBin = dg.getBinSize() / 2.0;
//        
//        for(int i = 0; i < distr.length; i++) {
//            System.out.println(i+"\t"+distr[i]+"\t"+allDistr[i]);
//            double binMid = (i * dg.getBinSize()) + halfBin;
//            System.out.println("Bin Mid: "+binMid);
//            series.add(binMid, distr[i]);
//            allSeries.add(binMid, allDistr[i]);
//        }
//        
//        XYSeriesCollection dataset = new XYSeriesCollection(series);
//        dataset.addSeries(allSeries);
//        
//        JFreeChart chart = ChartFactory.createXYLineChart("PSM distribution", "RT", "# PSMs", 
//                dataset, PlotOrientation.VERTICAL, true, false, false);
//        
//        ChartUtilities.saveChartAsJPEG(new File("/Users/vagisha/Desktop/chart.jpeg"), chart, 500, 300);
//        
//        BufferedImage img = chart.createBufferedImage(500, 300);
        
    }
}
