package ed.mslib;

import java.io.*;
import ed.javatools.BufferedRaf;
import ed.javatools.PrimitiveTools;
//import java.util.zip.*;	//jzip old
import com.jcraft.jzlib.*;

public class ReadMS2Comp implements ReadMS2Interface{
	
	public ReadMS2Comp(File file) throws FileNotFoundException, IOException{
		this.file = file;
		raf = new BufferedRaf(file,"r");
//		mzinflater = new Inflater();	//jzip old
//		intinflater = new Inflater();	//jzip old
		zs = new ZStream();
		readheader();
	}
	
	public MS2Scan getScan(int scannum) throws IOException{
		
		raf.seek(endofheader);
		MS2Scan ms2scan = null;
		while (true){
			ms2scan = readScan(scannum);
			if (ms2scan != null){
				return ms2scan;
			}
			if (lastscan == -1){
				return null;
			}
		}
	}
	
	public MS2Scan getNextScan() throws IOException{

		MS2Scan ms2scan = null;
		ms2scan = readScan(-1);
		if (lastscan == -1){	//if EOF, return null
			return null;
		}
		return ms2scan;		
	}

	private MS2Scan readScan(int scan) throws IOException{

		if (raf.length()-raf.getFilePointer()<56){ //unknown extra bytes at the end of file, so this is to notify that EOF reached
			lastscan = -1;
			return null;
		}
		
		int scan1 = raf.readLEInt();
		lastscan = scan1;

		int scan2 = raf.readLEInt();
		double fragmass = raf.readLEDouble();
		float rtime = raf.readLEFloat();
		float bpi = -1; //base peak intensity
		double bpm = -1;//base peak mass
		double convA = -1; //conversion factor A
		double convB = -1; //conversion factor B
		double tic = -1; //total ion current
		float iit = -1; //ion injection time
		int numez = 0;	//number of EZ lines
		
		if (version >= 2){		
			bpi = raf.readLEFloat();
			bpm = raf.readLEDouble();
			convA = raf.readLEDouble();
			convB = raf.readLEDouble();
			tic = raf.readLEDouble();
			iit = raf.readLEFloat();
		}

		int numz = raf.readLEInt();			//number of Z lines
		
		if (version >= 3){
			numez = raf.readLEInt();
		}
		
		int numdatapts = raf.readLEInt();	//number of mz-intensity data points

		//Read Z lines
		int[] chargearray = new int[numz];
		double[] mpharray = new double[numz];
		for (int i=0; i<numz; i++){
			chargearray[i] = raf.readLEInt();
			mpharray[i] = raf.readLEDouble();
		}
		
		//Read EZ lines
		int[] ezz = new int[numez];
		double[] ezmph = new double[numez];
		float[] ezrtime = new float[numez];
		float[] ezarea = new float[numez];
		for (int i=0; i<numez; i++){
			ezz[i] = raf.readLEInt();
			ezmph[i] = raf.readLEDouble();
			ezrtime[i] = raf.readLEFloat();
			ezarea[i] = raf.readLEFloat();
		}
		
		int mzlength = raf.readLEInt();
		int intlength = raf.readLEInt();
		
		MS2Scan result = null;
		
		if (scan==scan1 || scan == -1){ //if scan found or just want next scan, then read and uncomp data
		
			byte[] mzcomp = new byte[mzlength];		//read compressed mz data
			for (int i=0; i<mzlength; i++){
				mzcomp[i]=(byte)raf.read();
			}
			
			byte[] intcomp = new byte[intlength];	//read compressed intensity data
			for (int i=0; i<intlength; i++){
				intcomp[i]=(byte)raf.read();
			}
			
			//init 
			result = new MS2Scan();			
			result.setscan(scan1);
			result.setendscan(scan2);
			result.setprecursor((float)fragmass);
			result.addIfield("RTime\t"+rtime);
			if (version >= 2){
				result.addIfield("BPI\t"+bpi);
				result.addIfield("BPM\t"+bpm);
				result.addIfield("ConvA\t"+convA);
				result.addIfield("ConvB\t"+convB);
				result.addIfield("TIC\t"+tic);
				result.addIfield("IIT\t"+iit);
				for (int i=0; i<numez; i++){
					result.addezline(ezz[i],ezmph[i], ezrtime[i], ezarea[i]);
				}
			}
			
			for (int i=0; i<numz;i++){
				result.addchargemass(chargearray[i],mpharray[i]);
			}
			
//			mzinflater.reset();				//jzip old
//			intinflater.reset();			//jzip old
//			mzinflater.setInput(mzcomp);	//jzip old
//			intinflater.setInput(intcomp);	//jzip old
			
			byte[] mzb = new byte[8];
			byte[] intb = new byte[4];
			
			try {
				//read all data at once
				byte[] mzbbig = new byte[8*numdatapts];
				byte[] intbbig = new byte[4*numdatapts];
//				mzinflater.inflate(mzbbig); 	//jzip old
//				intinflater.inflate(intbbig);	//jzip old
				
				//jzlib				
				int err;
				zs.next_in = mzcomp;
				zs.next_in_index=0;
				zs.next_out = mzbbig;
				zs.next_out_index = 0;				
				err = zs.inflateInit();
				
				zs.avail_in = mzcomp.length;
				zs.avail_out = mzbbig.length;
				err = zs.inflate(JZlib.Z_FINISH);
				
				zs.next_in = intcomp;
				zs.next_in_index = 0;
				zs.next_out = intbbig;
				zs.next_out_index = 0;
				err = zs.inflateInit();
				
				zs.avail_in = intcomp.length;
				zs.avail_out = intbbig.length;
				err = zs.inflate(JZlib.Z_FINISH);
				//jzlib end
				
				//*****test debug
		/*		Inflater testinf = new Inflater();
				testinf.setInput(intcomp);
				byte[] tempb = new byte[4];
				
				System.out.println("*"+intcomp.length + " " + (numdatapts*4) + " " + testinf.getAdler());
				
				byte[] bx = PrimitiveTools.LEFloatTobyteArray(215.4f);
				System.out.println(bx[3]+" " + bx[2] + " " + bx[1] + " " +bx[0] + "*");
				
				for (int i=0; i< numdatapts; i++){
					System.out.print(testinf.inflate(tempb) + " ");
					testinf.inflate(tempb);
					System.out.println(PrimitiveTools.LEbyteArrayToFloat(tempb) + " " + testinf.getBytesWritten() + " " + testinf.getBytesRead());
					
					if(testinf.getBytesWritten() == 976){
						byte[] b2 = new byte[3];
						testinf.inflate(b2);
						System.out.println(b2[2] + " " +b2[1] + " " +b2[0]);
						byte[] b4 = new byte[4];
						b4[2] = b2[2];
						b4[1] = b2[1];
						b4[0] = b2[0];
						for (int ii=0; ii<256; ii++ ){
							b4[3] = (byte)ii;
							System.out.println(PrimitiveTools.LEbyteArrayToFloat(b4) + " " + (byte)ii);
						}
					}
				}
		*/		//*****endtest debug
				
				//then read data into smaller byte arrays and translate values
				for (int i=0; i<numdatapts; i++){
					for (int j=0; j<8; j++){
						mzb[j] = mzbbig[i*8 + j];
					}
					for (int j=0; j<4; j++){
						intb[j] = intbbig[i*4 + j];
					}
				
					double mz = PrimitiveTools.LEbyteArrayToDouble(mzb);
					float inten = PrimitiveTools.LEbyteArrayToFloat(intb);
					result.addscan(mz,inten);
				}
			
//			} catch (DataFormatException e) {
			} catch (Exception e){
				System.out.println("Error uncompressing data");
				e.printStackTrace();
				return null;
			}
			return result;
		}
		else{//skip ahead of data, if not reading
			raf.seek(raf.getFilePointer()+mzlength+intlength);
			return result; //return null
		}
	}
	
	/**
	 * read uncompressed file header
	 * @throws IOException
	 */
	 private void readheader() throws IOException{
		 raf.seek(0);
		 filetype = raf.readLEInt();
		 version = raf.readLEInt();

		 byte[] b = new byte[headerlength];
		 int counter = 0;
		 boolean lineEnd = false;

		 for (int i=0; i<headerlength; i++){
			 byte x = (byte)raf.read();

			 if (x == 0) { // end of valid data in a line
				 lineEnd = true;
				 continue;
			 }

			 if (lineEnd && i % 128 != 0) { // we have already read all the valid data in this line; skip over junk bytes
				 continue;
			 }
			 if (i % 128 == 0) { // beginning of a new line
				 lineEnd = false;
			 }
		            
			 if (x!=0){
				 b[counter] = x;
				 counter++;	
			 }
		 }

		 endofheader = raf.getFilePointer();
		 header = new String(b, 0, counter);
	}
	
	public String getheader(){return header;}
	public String getfilename(){
		return file.getName();
	}

	public void closeReader(){
		if (raf != null){
			try{
				raf.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	public int getversion(){return version;}
	public long getfiletype(){return filetype;}
	
	private String header;
	private File file;
	BufferedRaf raf;
	private int version;
	private long filetype;
	private ZStream zs;
//	private Inflater mzinflater;	//jzip old
//	private Inflater intinflater;	//jzip old
	private int lastscan=0;
	private long endofheader=0;
	private int headerlength = 2048;
	
}
