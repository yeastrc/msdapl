/*
 * YatesSpectraMS2Parser.java
 * Created on Oct 21, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.yates;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.yates.YatesCycle;
import java.io.*;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 21, 2004
 */

public class YatesSpectraMS2Parser {

	private LinkedList masslist = new LinkedList();
	private LinkedList intlist = new LinkedList();
	
	/**
	 * @return Returns the intlist.
	 */
	public LinkedList getIntlist() {
		return intlist;
	}
	/**
	 * @return Returns the masslist.
	 */
	public LinkedList getMasslist() {
		return masslist;
	}

	/**
	 * Populated the two supplied LinkedLists with mass and charges to facilitate display of spectral data
	 * @param cycle The cycle containing the MS2 data
	 * @param Sc Mass
	 * @param Z Charge
	 * @param masslist
	 * @param intlist
	 * @throws Exception
	 */
	public void parseMS2Spectra(YatesCycle cycle, String Sc, String Z) throws Exception {
		/*
		 * Parse the DTA information
		 * read by line and put the first value in masslist[]
		 * put the second value in intlist[]
		 * masslist[] = the parent mass
		 * intlist[] = charge state
		 */
		BufferedReader br = null;
		
		
		try {
			br = new BufferedReader ( new InputStreamReader ( cycle.getMS2().getMS2Data() ) );
		} catch (Exception e) {
			throw new Exception ("There was an error reading the MS2 spectral data.  It most likely is not in the database." );
		}
		
		br.mark(4086);
		
		System.gc();
		
		// get the first line
		String line = br.readLine();
		
		try {
			if (line.startsWith("S") || line.startsWith("H")) {
				
				// New MS2 format
				int step = 0;				
				int Scint = 0;

				try {
					Scint = Integer.parseInt(Sc);
				} catch (Exception e) {
					throw new Exception("Error int value: " + Scint + " from line: " + line);
				}

				// Go back to the beginning of the data
				br.reset();

				// Loop through the data
				while ( (line = br.readLine()) != null) {
					
					if (step == 0) {
						if (!line.startsWith("S")) continue;
						
						String[] tmp = line.split("\\t");
						if (tmp.length != 4)
							throw new Exception ("Error parsing line: " + line);
						
						int tInt = 0;

						try {
							tInt = Integer.parseInt(tmp[1]);
						} catch (Exception e) {
							throw new Exception ("Error int value: " + tInt + " from line: " + line);
						}
						
						if (Scint != tInt) continue;
						
						else {
							// We found our Scan #
							line = br.readLine();		// go to next line
							
							// FIX FOR "I" LINES
							while (line.startsWith("I"))
								line = br.readLine();
							
							if (!line.startsWith("Z"))
								throw new Exception ("Didn't get Z or I line after S line.  Got: " + line);
							
							boolean foundZ = false;
							String[] Zarr = line.split("\\t");
							while (Zarr.length == 3) {
								if (String.valueOf(Zarr[1]).equals(Z)) {
									
									// We found our charge line!
									intlist.addLast(Zarr[1]);
									masslist.addLast(Zarr[2]);
									
									foundZ = true;
									step = 1;
									break;
								}
								else {
									line = br.readLine();
									Zarr = line.split("\\t");
								}
							}
							if (foundZ == false)
								throw new Exception ("Didn't find appropriate charge line...");
						}
						
					} else if (step == 1) {
						if (line.startsWith("Z")) continue;
						if (line.startsWith("D")) continue; 		// added 9/11/2007 - D lines are causing problems, what is it?
						if (line.startsWith("S")) break;
						
						String[] tmp = line.split("\\s");
						if (tmp.length < 2)
							throw new Exception ("Error parsing mass/charge line: " + line);
						
						// Add em
						masslist.addLast(tmp[0]);
						intlist.addLast(tmp[1]);
					}
				}	
			}
			else if (line.startsWith(":")) {
				
				// Old MS2 format
				Pattern pattern1 = Pattern.compile(":" + Sc + "\\.\\d+?\\." + Z);
				Pattern pattern2 = Pattern.compile(":" + Sc + "\\.\\d+?");
				
				double mZ = 0.0;
				
				int step = 0;
				
				// Go back to the beginning of the data
				br.reset();
				
				// Loop through the data
				while ( (line = br.readLine()) != null) {
					if (line == null) continue;
				 	if (line.equals("")) continue;
		
				 	// We're searching for the first step
				 	if (step == 0) {
				 		Matcher m = pattern1.matcher(line);
				 		if (!m.matches()) continue;
					
				 		line = br.readLine();
				 		String[] tmp = line.split("\\s");
				 		if (tmp.length >= 2) {
				 			masslist.addLast(tmp[0]);
				 			intlist.addLast(tmp[1]);
				 			
				 			mZ = Double.parseDouble(tmp[0]);
				 		} else {
				 			throw new Exception ("Couldn't parse first ION line...");
				 		}
				 		
				 		step = 1;
				 		continue;
				 	}
					
				 	// We're searching forst next step
				 	if (step == 1) {
				 		//Matcher m = pattern2.matcher(ms2[i]);
				 		//if (m.matches()) {
				 		if (line.startsWith(":")) {
				 			line = br.readLine();
				 			continue;
				 		} else {
				 		
				 			String[] tmp = line.split("\\s");
				 			if (tmp.length >= 2) {
				 				masslist.addLast(tmp[0]);
				 				intlist.addLast(tmp[1]);
				 			} else {
				 				throw new Exception ("Unrecognized line: " + line);
				 			}
				 		
				 			step = 2;
				 			continue;
				 		}
				 	}
				 	
				 	// Even next step.
				 	if (step == 2) {
				 		if (line.startsWith(":")) break;
				 		
				 		String[] tmp = line.split("\\s");
	
				 		if (Double.parseDouble(tmp[0]) > mZ) break;
				 		
				 		if (tmp.length >= 2) {
				 			masslist.addLast(tmp[0]);
				 			intlist.addLast(tmp[1]);
				 		}
				 		
				 		continue;
				 	}
				 }
				
				// Clean up a little
				pattern1 = null;
				pattern2 = null;
			}
			else {
				
				// Unknown MS2 format
				throw new Exception ("MS2 TYPE NOT SUPPORTED.  LINE WAS: '" + line + "'");
			}
		}
		finally {
			
			// Make sure to clean this up.
			br.close();
			br = null;
			
			System.gc();
		}
	}
}
