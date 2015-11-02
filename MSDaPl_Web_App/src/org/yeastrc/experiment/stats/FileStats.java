package org.yeastrc.experiment.stats;

import org.yeastrc.experiment.File;
import org.yeastrc.www.util.RoundingUtils;

public class FileStats implements File, Comparable<FileStats>{

    private final String filename;
    private final int id;
    
    private int totalCount;
    private int goodCount;
    
    private double populationMin;
    private double populationMax;
    private double populationMean = -1.0;
    private double populationStandardDeviation;
    
    public FileStats(int id, String fileName) {
        this.id = id;
        this.filename = fileName;
    }
    
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }

    public void setPopulationMin(double populationMin) {
		this.populationMin = populationMin;
	}

	public void setPopulationMax(double populationMax) {
		this.populationMax = populationMax;
	}

	public void setPopulationMean(double populationMean) {
		this.populationMean = populationMean;
	}

	public void setPopulationStandardDeviation(double populationStandardDeviation) {
		this.populationStandardDeviation = populationStandardDeviation;
	}

	public double getPopulationMin() {
		return populationMin;
	}

	public double getPopulationMax() {
		return populationMax;
	}

	public double getPopulationMean() {
		return populationMean;
	}

	public double getPopulationStandardDeviation() {
		return populationStandardDeviation;
	}

	public double getPercentGoodCount() {
    	return RoundingUtils.getInstance().roundOne(((double)goodCount/(double)totalCount)*100.0);
    }
    
	public boolean getHasPopulationStats() {
		return this.populationMean != -1.0;
	}
	
    public String getGoogleChartUrl() {
    	
    	if(populationMean == -1)
    		return "";
    	
    	StringBuilder buf = new StringBuilder();
    	
    	double samplePercent = getPercentGoodCount();
    	boolean inRange = samplePercent >= (populationMean - populationStandardDeviation) && 
    					  samplePercent <= (populationMean + populationStandardDeviation);
    	        
    	//http://chart.apis.google.com/chart?cht=lxy:nda&chs=100x50&chco=add8e6,6495ed,add8e6,000000&chls=20,4,0|20,4,0|20,4,0|25,4,0
    	// &chd=t:1,3|0,0|3,15|0,0|14,30|0,0|6,7|0,0
    	// &chds=0,30&chxr=0,0,30&chm=d,ff0000,3,0,15,,::10
    	buf.append("http://chart.apis.google.com/chart?");
    	buf.append("cht=lxy:nda");
    	buf.append("&chs=100x15");
    	buf.append("&chco=add8e6,6495ed,add8e6");
    	if(inRange)
    		buf.append(",6495ed");
    	buf.append("&chls=15,4,0|15,4,0|15,4,0|15,4,0");
    	buf.append("&chma=10,10,0,0");
    	buf.append("&chd=t:");
    	buf.append(populationMin+","+(Math.max(0,(populationMean - populationStandardDeviation)))+"|0,0");
    	buf.append("|"+Math.max(0,(populationMean - populationStandardDeviation))+","+(populationMean + populationStandardDeviation)+"|0,0");
    	buf.append("|"+(populationMean + populationStandardDeviation)+","+populationMax+"|0,0");
    	buf.append("|"+samplePercent+","+(samplePercent+1)+"|0,0");
    	
    	// scale has to be specified for each series, otherwise marker gets cut off
    	buf.append("&chds=0,"+populationMax);
    	buf.append(",0,10");
    	buf.append(",0,"+populationMax);
    	buf.append(",0,10");
    	buf.append(",0,"+populationMax);
    	buf.append(",0,10");
    	buf.append(",0,"+populationMax);
    	buf.append(",0,10");
    	
    	
    	buf.append("&chxr=0,0,"+populationMax);
    	String color = "191970";
    	if(!inRange)
    		color = "ff4500";
    	buf.append("&chm=d,"+color+",3,0,12,,::8");
    	
    	return buf.toString();
    }
    
    public String getGoogleChartWithPinUrl() {
    	
    	if(populationMean == -1)
    		return "";
    	
    	StringBuilder buf = new StringBuilder();
    	
    	double samplePercent = getPercentGoodCount();
    	boolean inRange = samplePercent >= (populationMean - populationStandardDeviation) && 
    					  samplePercent <= (populationMean + populationStandardDeviation);
    	        
    	//http://chart.apis.google.com/chart?cht=lxy:nda&chs=100x50&chco=add8e6,6495ed,add8e6,000000&chls=20,4,0|20,4,0|20,4,0|25,4,0
    	// &chd=t:1,3|0,0|3,15|0,0|14,30|0,0|6,7|0,0
    	// &chds=0,30&chxr=0,0,30
    	// chem=y;s=map_xpin_letter_withshadow;d=pin,:(,ffa500;dp=0;ds=3;of=0,5
    	buf.append("http://chart.apis.google.com/chart?");
    	buf.append("cht=lxy:nda");
    	buf.append("&chs=300x100");
    	buf.append("&chco=add8e6,6495ed,add8e6");
    	if(inRange)
    		buf.append(",6495ed");
    	buf.append("&chls=10,4,0|10,4,0|10,4,0|10,4,0");
    	buf.append("&chma=10,10,0,0");
    	buf.append("&chd=t:");
    	buf.append(populationMin+","+(populationMean - populationStandardDeviation)+"|15,15");
    	buf.append("|"+(populationMean - populationStandardDeviation)+","+populationMean+","+
    			(populationMean + populationStandardDeviation)+"|15,15,15");
    	buf.append("|"+(populationMean + populationStandardDeviation)+","+populationMax+"|15,15");
    	buf.append("|"+samplePercent+","+(samplePercent+1)+"|15,15");
    	
    	// scale has to be specified for each series, otherwise pin gets cut off
    	buf.append("&chds=0,"+populationMax);
    	buf.append(",0,30");
    	buf.append(",0,"+populationMax);
    	buf.append(",0,30");
    	buf.append(",0,"+populationMax);
    	buf.append(",0,30");
    	buf.append(",0,"+populationMax);
    	buf.append(",0,30");
    	
    	buf.append("&chxr=0,0,"+populationMax);
    	String color = "6495ed";
    	if(!inRange)
    		color = "ff4500";
    	buf.append("&chem=y;s=map_xpin_letter_withshadow;d=pin,");
    	if(inRange)
    		buf.append(":),"+color);
    	else
    		buf.append(","+color);
    	buf.append(";dp=0;ds=3;of=0,5");
    	
    	buf.append("&chm=N*x*%,,1,,10,,l::-15|N*x*%,,0,0,10,,r::-15|N*x*%,,2,1,10,,l::-15");
    	
    	return buf.toString();
    }
    
    public String getFileName() {
        return filename;
    }
    
    public int getId() {
        return id;
    }

    @Override
    public int compareTo(FileStats o) {
        return filename.compareTo(o.filename);
    }
    
    public static void main(String[] args) {
    	FileStats stats = new FileStats(0, "dummy");
    	stats.setPopulationMin((int) Math.round(0.8963));
    	stats.setPopulationMax((int) Math.round(33.6445));
    	stats.setPopulationMean((int) Math.round(13.84433367));
    	stats.setPopulationStandardDeviation((int) Math.round(11.57843122));
    	
    	stats.setTotalCount(32764);
    	stats.setGoodCount(342);
    	
    	System.out.println(stats.getGoogleChartWithPinUrl());
    }
    
}
