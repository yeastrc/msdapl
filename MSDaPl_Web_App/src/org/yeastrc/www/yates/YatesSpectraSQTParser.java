/*
 * YatesSpectraSQTParser.java
 * Created on Oct 21, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.yates;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.yates.YatesCycle;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 21, 2004
 */

public class YatesSpectraSQTParser {

	private LinkedList diffmod = new LinkedList();
	private LinkedList staticmod = new LinkedList();
	private String AvgForParent = "TRUE";
	private String AvgForFrag = "FALSE";
	private String dbase = null;
	private LinkedList Reference = new LinkedList();
	private LinkedList SequestLines = new LinkedList();
	
	/**
	 * @return Returns the avgForFrag.
	 */
	public String getAvgForFrag() {
		return AvgForFrag;
	}
	/**
	 * @return Returns the avgForParent.
	 */
	public String getAvgForParent() {
		return AvgForParent;
	}
	/**
	 * @return Returns the dbase.
	 */
	public String getDbase() {
		return dbase;
	}
	/**
	 * @return Returns the diffmod.
	 */
	public LinkedList getDiffmod() {
		return diffmod;
	}
	/**
	 * @return Returns the staticmod.
	 */
	public LinkedList getStaticmod() {
		return staticmod;
	}
	/**
	 * @return Returns the reference.
	 */
	public LinkedList getReference() {
		return Reference;
	}
	/**
	 * @return Returns the sequestLines.
	 */
	public LinkedList getSequestLines() {
		return SequestLines;
	}

	/**
	 * Parse the SEQUEST data for the given cycle, Sc and Z to facilitate spectra display
	 * @param cycle
	 * @param Sc
	 * @param Z
	 * @throws Exception
	 */
	public void parseSQTData(YatesCycle cycle, String Sc, String Z) throws Exception {	
		BufferedReader br = new BufferedReader ( new InputStreamReader ( cycle.getSQT().getSQTData() ) );
		br.mark(102400);
		
		System.gc();

		// get the first line
		String line = br.readLine();

		try {
			// For the newer SQT header format:
			if (line.startsWith("H")) {
				String PrecursorMasses = "";
				String FragmentMasses = "";
				
				while (line.startsWith("H")) {
					String[] tmp = line.split("\\t");
				
					if (tmp[1].equals("DiffMod"))
						diffmod.addLast(tmp[2]);
					else if (tmp[1].equals("StaticMod"))
						staticmod.addLast(tmp[2]);			
					else if (tmp[1].equals("Database"))
						dbase = tmp[2];
					else if (tmp[1].equals("PrecursorMasses"))
						PrecursorMasses = tmp[2];	
					else if (tmp[1].equals("FragmentMasses"))
						FragmentMasses = tmp[2];
					
					line = br.readLine();
				}
			
				if (FragmentMasses.equals("AVG"))
					AvgForFrag = "TRUE";
	
				if (PrecursorMasses.equals("MONO")) 
					AvgForParent = "FALSE";
			} else {
	
				// Attempt the old-school SQT header format parse
				Pattern p1 = Pattern.compile("AVG\\/AVG");
				Pattern p2 = Pattern.compile("AVG\\/MONO");
				Pattern p3 = Pattern.compile("MONO\\/AVG");
				Pattern p4 = Pattern.compile("MONO\\/MONO");
				Pattern p5 = Pattern.compile(".+?amino acids.+?proteins.+?, (\\S+?)");
				Pattern p6 = Pattern.compile("\\((\\S+?\\s[\\+-]\\S+?)\\)");
				Pattern p7 = Pattern.compile("(\\S+?=\\S+?)");
				
				while (line != null && !line.startsWith("S")) {
					Matcher m = p1.matcher(line);
					if (m.matches()) {
						AvgForParent = "TRUE";
						AvgForFrag = "TRUE";
					}
					
					m = p2.matcher(line);
					if (m.matches()) {
						AvgForParent = "TRUE";
						AvgForFrag = "FALSE";
					}
					
					m = p3.matcher(line);
					if (m.matches()) {
						AvgForParent = "FALSE";
						AvgForFrag = "TRUE";
					}
					
					m = p4.matcher(line);
					if (m.matches()) {
						AvgForParent = "FALSE";
						AvgForFrag = "FALSE";
					}
					
					m = p5.matcher(line);
					if (m.matches()) {
						dbase = m.group(1);
					}
					
					m = p6.matcher(line);
					if (m.matches()) {
						diffmod.addLast(m.group(1));
						while (m.find()) {
							diffmod.addLast(m.group(1));
						}
					}
					
					m = p7.matcher(line);
					if (m.matches()) {
						staticmod.addLast(m.group(1));
						while (m.find()) {
							staticmod.addLast(m.group(1));
						}
					}
					
					m = null;
					line = br.readLine();
				}
				p1 = null; p2 = null; p3 = null; p4 = null; p5 = null; p6 = null; p7 = null;
			}
	
			// Uh oh, we couldn't parse the SQT data headers
			if (dbase == null) {
				throw new Exception ("Unable to parse SEQUEST data file headers.");
			}
			
			// Done parsing headers, parse the rest
			int step = 0;
			int r = 0;
			int lo = 0;
			int store = 0;
			
			Pattern p1 = Pattern.compile("S\\s+?" + Sc + "\\s+?\\d+?\\s+?" + Z);
			Pattern p2 = Pattern.compile("M\\s+?(\\d+?)\\s+?(\\d+?)\\s+?(\\S+?)\\s+?(\\S+?)\\s+?(\\S+?)\\s+?(\\S+?)\\s+?(\\d+?)\\s+?(\\d+?)\\s+?(\\S\\.\\S+?\\.\\S)\\s+?(\\S+?)");
			Pattern p3 = Pattern.compile("^L\\s+?(\\S+?)");
			
			br.reset();
			
			while ( (line = br.readLine()) != null) {
				if (step == 0) {
					if (!p1.matcher(line).matches()) continue;
					else {
						step = 1;
						continue;
					}
				}
				
				if (step == 1) {
					if (line.startsWith("S")) break;
					
					Matcher m1 = p2.matcher(line);
					if (m1.matches()) {
						SequestLine sl = new SequestLine();
						
	                    String rank_sp = m1.group(1) + "/" + m1.group(2);
	                    sl.setRank_sp(rank_sp);

	                    sl.setMplusHplus(m1.group(3));
	                    sl.setDeltCn(m1.group(4));
	                    sl.setXCorr(m1.group(5));
	                    sl.setSp(m1.group(6));
	                    
	                    String ion = m1.group(7) + "/" + m1.group(8);
	                    sl.setIons(ion);
	                    
	                    sl.setPeptide(m1.group(9));
	                    sl.setManual(m1.group(10));
	                    
	                    SequestLines.addLast(sl);

	                    r++;
	                    continue;
					}
					Matcher m2 = p3.matcher(line);
					if (m2.matches()) {
						String templocus = m2.group(1);
						if (lo + 1 == r) {
							Reference.addLast(templocus);
							lo++;
						}
						
						continue;
					}				
				}
			}
	
			// clean up some
			p1 = null; p2 = null; p3 = null;	

		} finally {
			System.gc();
		}
	}
	
	public class SequestLine {
		private String rank_sp;
		private String MplusHplus;
		private String deltCn;
		private String XCorr;
		private String Sp;
		private String Ions;
		private String peptide;
		private String manual;
			
		/**
		 * @return Returns the deltCn.
		 */
		public String getDeltCn() {
			return deltCn;
		}
		/**
		 * @param deltCn The deltCn to set.
		 */
		public void setDeltCn(String deltCn) {
			this.deltCn = deltCn;
		}
		/**
		 * @return Returns the ions.
		 */
		public String getIons() {
			return Ions;
		}
		/**
		 * @param ions The ions to set.
		 */
		public void setIons(String ions) {
			Ions = ions;
		}
		/**
		 * @return Returns the manual.
		 */
		public String getManual() {
			return manual;
		}
		/**
		 * @param manual The manual to set.
		 */
		public void setManual(String manual) {
			this.manual = manual;
		}
		/**
		 * @return Returns the mplusHplus.
		 */
		public String getMplusHplus() {
			return MplusHplus;
		}
		/**
		 * @param mplusHplus The mplusHplus to set.
		 */
		public void setMplusHplus(String mplusHplus) {
			MplusHplus = mplusHplus;
		}
		/**
		 * @return Returns the peptide.
		 */
		public String getPeptide() {
			return peptide;
		}
		/**
		 * @param peptide The peptide to set.
		 */
		public void setPeptide(String peptide) {
			this.peptide = peptide;
		}
		/**
		 * @return Returns the rank_sp.
		 */
		public String getRank_sp() {
			return rank_sp;
		}
		/**
		 * @param rank_sp The rank_sp to set.
		 */
		public void setRank_sp(String rank_sp) {
			this.rank_sp = rank_sp;
		}
		/**
		 * @return Returns the sp.
		 */
		public String getSp() {
			return Sp;
		}
		/**
		 * @param sp The sp to set.
		 */
		public void setSp(String sp) {
			Sp = sp;
		}
		/**
		 * @return Returns the xCorr.
		 */
		public String getXCorr() {
			return XCorr;
		}
		/**
		 * @param corr The xCorr to set.
		 */
		public void setXCorr(String corr) {
			XCorr = corr;
		}
	}
}
