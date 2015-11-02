/**
 * BinCalculator.java
 * @author Vagisha Sharma
 * Dec 30, 2010
 */
package org.yeastrc.ms.service.percolator.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 */
public class BinCalculator {

	private static BinCalculator instance = null;
	
	private BinCalculator() {}
	
	public static synchronized BinCalculator getInstance() {
		
		if(instance == null)
			instance = new BinCalculator();
		
		return instance;
	}
	
	public List<Bin> getBins(double minValue, double maxValue, int numBins) {
		
		double newMinValue = roundDown(minValue);
		double newMaxValue = roundUp(maxValue);
		
		double range = newMaxValue - newMinValue;
		
		double binSize = roundDown(range / (double) numBins);
		if(binSize < 0.5)
			binSize = 0.5;
		
		//System.out.println(newMinValue+" - "+newMaxValue+"; range: "+range+"; binSize: "+binSize);
		
		List<Bin> bins = new ArrayList<Bin>(numBins);
		// center the bins around 0;
		double ms = -Math.round((binSize / 2.0)*100.0)/100.0;
		double me = Math.round((binSize / 2.0)*100.0)/100.0;
		bins.add(new Bin(ms,me));
		
		for(double i = ms; i > newMinValue; i-= binSize) {
			double e = Math.round(i*100.0)/100.0;
			double s = Math.round((i-binSize)*100.0)/100.0;
			
			Bin bin = new Bin(s,e);
			bins.add(bin);
		}
		
		for(double i = me; i < newMaxValue; i += binSize) {
			double s = Math.round(i*100.0)/100.0;
			double e = Math.round((i+binSize)*100.0)/100.0;
			
			Bin bin = new Bin(s,e);
			bins.add(bin);
		}
		Collections.sort(bins);
		return bins;
		
	}
	
	private double roundUp(double value) {
		
		return round(value, true);
	}
	
	private double roundDown(double value) {
		
		return round(value, false);
	}
	
	private double round(double value, boolean roundUp) {
		
		int magnitude = getMagnitude(value);
		
		boolean negative = value < 0.0;
		
		if(value >= -1.0 && value <= 1.0) {
			value = round(value*10.0, roundUp);
			value = value/10.0;
		}
		else {
			double divisor = Math.pow(10, magnitude - 1)/2.0;
			
			double remainder = (value % divisor);
			
			if(remainder != 0) {
				if(negative)
					value = roundUp ? value - remainder : value - divisor - remainder;
				else
					value = roundUp ? value + divisor - remainder : value - remainder;

				value = Math.round(value * 100.0) / 100.0;
			}
			
		}
		
		return value;
	}
	
	private int getMagnitude(double value) {
		
		int divisor = 1;
		int magnitude = 0;
		value = Math.abs(value);
		while( (int)(value / divisor)  > 0) {
			divisor = divisor*10;
			magnitude++;
		}
		return magnitude;
	}
	
	public static void main(String[] args) {
		
		BinCalculator calculator = BinCalculator.getInstance();
		
		//System.out.println("magnitude for 0.6 "+calculator.getMagnitude(0.6)+" rounded UP: "+calculator.roundUp(0.6)+"; rounded DOWN: "+calculator.roundDown(0.6));
		//System.out.println("magnitude for 3.6 "+calculator.getMagnitude(3.6)+" rounded UP: "+calcultor.roundUp(3.6)+"; rounded DOWN: "+calculator.roundDown(3.6));
		//System.out.println("magnitude for 6.8 "+calculator.getMagnitude(6.8)+" rounded UP: "+calculator.roundUp(6.8)+"; rounded DOWN: "+calculator.roundDown(6.8));
		//System.out.println("magnitude for 26.8 "+calculator.getMagnitude(26.8)+" rounded UP: "+calculator.roundUp(26.8)+"; rounded DOWN: "+calculator.roundDown(26.8));
		//System.out.println("magnitude for 346.8 "+calculator.getMagnitude(346.8)+" rounded UP: "+calculator.roundUp(346.8)+"; rounded DOWN: "+calculator.roundDown(346.8));
		//System.out.println("magnitude for -2809.6110614515223 "+calculator.getMagnitude(-2809.6110614515223)+" rounded DOWN: "+calculator.roundDown(-2809.6110614515223)+"; rounded UP: "+calculator.roundUp(-2809.6110614515223));
		
		//printBins(calculator.getBins(-2809.6110614515223, 2788.820562369995, 15));
		//printBins(calculator.getBins(-3.8, 3.6, 15));
		printBins(calculator.getBins(-3.0003, 2.9817, 15));
	}
	
	private static void printBins(List<Bin> bins) {
		
		for(Bin bin: bins) {
			System.out.println(bin.getBinStart()+" - "+bin.getBinEnd());
		}
	}
	
}
