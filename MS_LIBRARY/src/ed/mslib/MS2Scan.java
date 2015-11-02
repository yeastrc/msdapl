package ed.mslib;

import java.util.*;

public class MS2Scan implements java.lang.Cloneable {

	public MS2Scan(){
		charges = new ArrayList<Integer>();
		masses = new ArrayList<Double>();
		dfield = new ArrayList<String>();
		ifield = new ArrayList<String>();
		mzint = new ArrayList<MzInt>();
		ezlines = new ArrayList<EZline>();
	}
	
	private int scan;
	private int endscan;
	private float precursor = -1;
	private float rtime = -1;
	private float bpi = -1; //base peak intensity
	private double bpm = -1;//base peak mass
	private double convA = -1; //conversion factor A
	private double convB = -1; //conversion factor B
	private double tic = -1; //total ion current
	private float iit = -1; //ion injection time
	
	/**
	 * Z FIELD - charge
	 */
	private List<Integer> charges;
	/**
	 * Z FIELD - mass (M+H)
	 */
	private List<Double> masses;
	
	/**
	 * D FIELD - Charge dependent data
	 */
	private List<String> dfield;
	
	/**
	 * I FIELD - Charge independent data
	 */
	private List<String> ifield;
	
	/**
	 * list of mz/intensity data objects
	 */
	private List<MzInt> mzint;
	
	/**
	 * list of EZline objects
	 */
	private List<EZline> ezlines;
	
	public int getscan(){return scan;}
	public void setscan(int sc){scan = sc;}
	
	public int getendscan(){return endscan;}
	public void setendscan(int endsc){endscan = endsc;}
	
	public float getprecursor(){return precursor;}
	public void setprecursor(float pre){precursor = pre;}
	
	/**
	 * Z FIELD
	 * Add Charge and Mass to List
	 * @param chrg
	 * @param mass
	 */
	public void addchargemass(int chrg, double mass){
		charges.add(chrg);
		masses.add(mass);
	}

	/**
	 * Z FIELD
	 * Get charge from List at position
	 * @param position
	 * @return
	 */
	public int getcharge(int position){
		return charges.get(position);
	}
	/**
	 * Z FIELD
	 * Get mass from List at Position
	 * @param position
	 * @return
	 */
	public double getmass(int position){
		return masses.get(position);
	}
	
	public void addezline(int charge, double mph, float rtime, float area){
		ezlines.add(new EZline(charge, mph, rtime, area));
		addIfield("EZ\t" + charge+ "\t"+ mph+"\t"+ rtime+"\t"+ area);
	}
	public EZline getezline(int index){
		return ezlines.get(index);
	}
	public int numez(){
		return ezlines.size();
	}
	
	
	/**
	 * D FIELD
	 * add String to dfield List
	 * @param d
	 */
	public void addDField(String d){
		dfield.add(d);
	}
	/**
	 * D FIELD
	 * get String from dfield List
	 * @param position
	 * @return
	 */
	public String getDField(int position){
		return dfield.get(position);
	}
	
	public int getDFieldSize(){
		return dfield.size();
	}
	
	/**
	 * I FIELD
	 * add String to ifield List
	 * @param i string to add
	 */
	public void addIfield(String i){
		ifield.add(i);
	}
	/**
	 * I FIELD
	 * get String from ifield List
	 * @param position
	 * @return string from ifield List
	 */
	public String getIfield(int position){
		return ifield.get(position);
	}
	public int numberOfILines(){
		return ifield.size();
	}
	
	/**
	 * Mass/Intensity data
	 * Add m/z and intensity to Mass/Intensity data List
	 * @param masscharge
	 * @param inten
	 */
	public void addscan(double masscharge, float inten){
		mzint.add(new MzInt(masscharge, inten));
	}
	/**
	 * Mass/Intensity data
	 * Get m/z from data List at position
	 * @param position
	 * @return
	 */
	public double getmz(int position){
		return mzint.get(position).getmz();
	}
	/**
	 * Mass/Intensity data
	 * Get intensity from data List at position
	 * @param position
	 * @return
	 */
	public float getintensity(int position){
		return mzint.get(position).getint();
	}
	
	public MzInt getmzint(int position){
		return mzint.get(position);
	}
	public List<MzInt> getmzintlist(){
		return mzint;
	}
	public int getdatasize(){
		return mzint.size();
	}
	
	/**
	 * Z FIELD - Charge
	 * @return Z FIELD Charge List
	 */
	public List<Integer> getchargeslist(){return charges;}
	
	/**
	 * Z FIELD - Mass (M+H)
	 * @return Z Field Mass List
	 */
	public List<Double> getmasseslist(){return masses;}
	
	public void setmzintlist(List<MzInt> list){mzint = list;}
	
	public float getRTime(){
		if (rtime == -1){
			String temp = getIfieldValue("RTime");
			if (temp == ""){
				rtime = 0.0f;
			}else{
				rtime = Float.parseFloat(temp);
			}
		}
		return rtime;
	}
	
	public float getBPI(){
		if (bpi == -1){
			String temp = getIfieldValue("BPI");
			if (temp == ""){
				bpi = 0.0f;
			}else{
				bpi = Float.parseFloat(temp);
			}
		}
		return bpi;
	}
	
	public double getBPM(){
		if (bpm == -1){
			String temp = getIfieldValue("BPM");
			if (temp == ""){
				bpm = 0.0;
			}else{
				bpm = Double.parseDouble(temp);
			}
		}
		return bpm;
	}
	
	public double getConvA(){
		if (convA == -1){
			String temp = getIfieldValue("ConvA");
			if (temp == ""){
				convA = 0.0;
			}
			else{
				convA = Double.parseDouble(temp);
			}
			
		}
		return convA;
	}
	
	public double getConvB(){
		if (convB == -1){
			String temp = getIfieldValue("ConvB");
			if (temp == ""){
				convB = 0.0;
			}
			else{
				convB = Double.parseDouble(temp);
			}
		}
		return convB;
	}
	
	public double getTIC(){
		if (tic == -1){
			String temp = getIfieldValue("TIC");
			if (temp == ""){
				tic = 0.0;
			}else{
				tic = Double.parseDouble(temp);
			}
		}
		return tic;
	}
	
	public float getIIT(){
		if (iit == -1){
			String temp = getIfieldValue("IIT");
			if (temp == ""){
				iit = 0.0f;
			}else{
				iit = Float.parseFloat(temp);
			}
		}
		return iit;
	}
	
	/**
	 * method to get a value from I lines. Looks for value, and returns the token that occurs after.
	 */
	private String getIfieldValue(String value){
		for (int i=0; i<ifield.size(); i++){
			StringTokenizer st = new StringTokenizer(ifield.get(i));
			while (st.hasMoreTokens()){
				String token = st.nextToken();
				if (token.equals(value)){
					if (value.equals("EZ")){
						String x = "";
						while (st.hasMoreTokens()){
							x += st.nextToken() + "\t";
						}
						return x;
					}
					return st.nextToken();
				}
			}
		}
		return "";
	}
	
	/**
	 * class that holds data from EZ lines.
	 * @author Ed
	 *
	 */
	public class EZline{
		public EZline(int charge, double mph, float rtime, float area){
			this.charge = charge;
			this.mph = mph;
			this.rtime = rtime;
			this.area = area;
		}
		int charge;
		double mph;
		float rtime;
		float area;
	}
	
	public void outputall(){	
		if (precursor != -1){
			System.out.println("S\t"+ scan + "\t" + endscan + "\t" + precursor);
			for (int i=0; i<ifield.size();i++){
				System.out.println("I\t"+ifield.get(i));
			}
			/*
			for (int i=0; i<numez(); i++){
				System.out.println("I\tEZ\t"+ezlines.get(i).charge + "\t" + ezlines.get(i).mph+"\t"+
						ezlines.get(i).rtime+"\t"+ezlines.get(i).area);
			}
			*/
			for (int i=0; i<charges.size(); i++){
				System.out.println("Z\t" + charges.get(i) + "\t" + masses.get(i));
				if (dfield.size() != 0 && dfield.get(i) != ""){
					System.out.println("D\t" + dfield.get(i));
				}
			}
			for (int i=0; i<mzint.size(); i++){
				System.out.println("" + mzint.get(i).getmz() + "\t" + mzint.get(i).getint());
			}
			System.out.println();
		}		
	}
	
	/**
	 * calls cloneNoData, then also adds mz/int data
	 */
	public MS2Scan clone(){
		MS2Scan result = cloneNoData();
		for (int m=0; m<mzint.size(); m++){
			result.addscan(mzint.get(m).getmz(), mzint.get(m).getint());
		}
		return result;
	}
	
	/**
	 * Makes new MS2Scan, copies all data except mz/int data.
	 * @return
	 */
	public MS2Scan cloneNoData(){
		MS2Scan result = new MS2Scan();
		result.setscan(this.scan);
		result.setendscan(this.endscan);
		result.setprecursor(this.precursor);
		/* not needed as ifields include this
		result.bpi = this.bpi;
		result.bpm = this.bpm;
		result.convA = this.convA;
		result.convB = this.convB;
		result.iit = this.iit;
		result.rtime = this.rtime;
		result.tic = this.tic;
		*/
		for (int c=0; c<charges.size(); c++){
			result.addchargemass(charges.get(c), masses.get(c));
		}
		for (int d=0; d<dfield.size(); d++){
			result.addDField(dfield.get(d));
		}
		for (int i=0; i<ifield.size(); i++){
			result.addIfield(ifield.get(i));
		}
		return result;
	}
	
}
