/**
 * PeakCompresser.java
 * @author Vagisha Sharma
 * Mar 20, 2011
 */
package org.yeastrc.ms.writer.mzml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.codec.binary.Base64;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.Peak;

/**
 * 
 */
public class PeakUtils {

	private static final int BUF_SIZE = 2048;
	
	public static String compressMz(MsScan scan) throws IOException {
		
		byte[] mzarr = getMzArray(scan);
		return new String(compress(mzarr));
	}
	
	public static String compressIntensity(MsScan scan) throws IOException {
		
		byte[] intarr = getIntensityArray(scan);
		return new String(compress(intarr));
	}

	public static byte[] compress(byte[] mzarr) throws IOException {
		
		Deflater deflater = new Deflater();
		deflater.setInput(mzarr);
		deflater.finish();
		
		ByteArrayOutputStream baos = null;
		
		byte[] buf = new byte[BUF_SIZE];
		byte[] compressed;
		
		try {
			baos = new ByteArrayOutputStream();
			
			int count = 0;
			while((count = deflater.deflate(buf)) != 0) {
				baos.write(buf, 0, count);
			}
			
			compressed = baos.toByteArray();
		}
		finally {
			if(baos != null) try {baos.close();} catch(IOException e){}
		}
		
		//System.out.println("uncompressed length: "+mzarr.length);
		//System.out.println("compressed length: "+compressed.length);
		
		return Base64.encodeBase64(compressed);
	}
	
	public static double[] decompress(String data) throws IOException {
		
		// Decode from Base64
		byte[] compressed = Base64.decodeBase64(data);
		
		try {
			return decompress(compressed);
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static double[] decompress(byte[] compressed)
			throws DataFormatException, IOException {
		
		// Decompress data
		Inflater inflater = new Inflater();
		inflater.setInput(compressed, 0, compressed.length);
		
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream(compressed.length);
			
			byte[] buf = new byte[BUF_SIZE];
			int count = 0;
			while((count = inflater.inflate(buf)) != 0) {
				baos.write(buf, 0, count);
				break;
			}
			inflater.end();
		}
		finally {
			if(baos != null) try {baos.close();} catch(IOException e){}
		}
		
		
		byte[] uncompressed = baos.toByteArray();
		//System.out.println("Length of uncompressed: "+uncompressed.length);
		
		// convert decompressed byte array to double array
		return toDoubleArray(uncompressed);
	}

	public static double[] toDoubleArray(byte[] input) throws IOException {
		
		double[] dblArr = new double[input.length/8];
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new ByteArrayInputStream(input));
			double value; 
			int i = 0;
			while(true) {
				try {
					value = dis.readDouble();
				}
				catch(EOFException eof) { break;} 
				
				dblArr[i] = value;
				i++;
			}
		}
		
		finally {
			if(dis != null) try {dis.close();} catch(IOException e){}
		}
		return dblArr;
	}
	
	
	public static byte[] toByteArr (double[] input) throws IOException {
		
		ByteArrayOutputStream bos = null;
		DataOutputStream dos = null;

		try {
			bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);
			for(double value: input) {
				dos.writeDouble(value);
			}
			dos.flush();
		}
		finally {
			if(dos != null) dos.close();
			if(bos != null) bos.close();
		}
		byte [] data = bos.toByteArray();
		return data;
	}
	
	public static byte[] toByteArr (float[] input) throws IOException {
		
		ByteArrayOutputStream bos = null;
		DataOutputStream dos = null;

		try {
			bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);
			for(float value: input) {
				dos.writeFloat(value);
			}
			dos.flush();
		}
		finally {
			if(dos != null) dos.close();
			if(bos != null) bos.close();
		}
		byte [] data = bos.toByteArray();
		return data;
	}
	
	public static byte[] getMzArray(MsScan scan) throws IOException {
            
		ByteArrayOutputStream bos = null;
		DataOutputStream dos = null;

		List<Peak> peaks = scan.getPeaks();
		try {
			bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);
			for(Peak peak: peaks) {
				dos.writeDouble(peak.getMz());
			}
			dos.flush();
		}
		finally {
			if(dos != null) dos.close();
			if(bos != null) bos.close();
		}
		byte [] data = bos.toByteArray();
		return data;
	}
	
	public static byte[] getIntensityArray(MsScan scan) throws IOException {
        
		ByteArrayOutputStream bos = null;
		DataOutputStream dos = null;

		List<Peak> peaks = scan.getPeaks();
		try {
			bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);
			for(Peak peak: peaks) {
				dos.writeDouble(peak.getIntensity());
			}
			dos.flush();
		}
		finally {
			if(dos != null) dos.close();
			if(bos != null) bos.close();
		}
		byte [] data = bos.toByteArray();
		return data;
	}
}
