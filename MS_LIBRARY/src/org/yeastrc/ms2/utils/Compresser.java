/*
 * Compresser.java
 * Created on Oct 25, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.ms2.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import java.io.*;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 25, 2004
 */

public class Compresser {

	// Private constructor to prevent foreign instantiation
	private Compresser() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static Compresser getInstance() {
		return new Compresser();
	}

	/**
	 * Compress the text in the given File into a byte array using GZip
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public byte[] compressFile(File file) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		GZIPOutputStream out = new GZIPOutputStream(bout);
		
		int bytesRead = 0;
		FileInputStream fis = new FileInputStream ( file );
		
		while ( bytesRead != -1 ) {
			byte[] readBytes = new byte[8172];
			bytesRead = fis.read(readBytes);
			
			if (bytesRead > 0)
				if (bytesRead != 8172)
					out.write(readBytes, 0, bytesRead);
				else
					out.write(readBytes);
			else
				break;
		}
		
		fis.close(); fis = null;
		out.flush(); out.close();
		bout.flush(); bout.close();

		return bout.toByteArray();
	}
	
	/**
	 * Take a string and use GZIP compression to produce an array
	 * of bytes of the compressed data.
	 * @param str The string to compress
	 * @return The binary, compressed data as an array of bytes
	 * @throws Exception If there is a problem.
	 */
	public byte[] compressString(String str) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		GZIPOutputStream out = new GZIPOutputStream(bout);
		
		// Major bug in java.lang.String.getBytes()
		// If the length is > 16777216 bytes and length%4 == 1 then it will throw
		// a buffer exception.  Get around this bug via this hack.
		try {
			out.write (str.getBytes());
		} catch (java.nio.BufferOverflowException e) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(str.length());
			int incr = 16777216;
			int end = incr;
			int start = 0;
			
			if (end > str.length())
				end = str.length();
			
			while (end <= str.length()) {
				bos.write(str.substring(start, end).getBytes());
				
				if (end == str.length()) break;
				
				start = end;
				end += incr;
				if (end > str.length())
					end = str.length();
			}
						
			out.write(bos.toByteArray());
			//bos.writeTo(out);
			bos.close();
			bos = null;
			System.gc();
		} finally {
			str = "";
			System.gc();
		}

		out.flush();
		out.close();

		bout.flush();
		bout.close();

		return bout.toByteArray();
	}	

	/**
	 * Take a StringBuffer and use GZIP compression to produce an array
	 * of bytes of the compressed data.
	 * @param str The string to compress
	 * @return The binary, compressed data as an array of bytes
	 * @throws Exception If there is a problem.
	 */
	public byte[] compressStringBuffer(StringBuffer str) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		GZIPOutputStream out = new GZIPOutputStream(bout);
		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(str.length());
			int incr = 16777216;
			int end = incr;
			int start = 0;
			
			if (end > str.length())
				end = str.length();
			
			while (end <= str.length()) {
				bos.write(str.substring(start, end).getBytes());
				
				if (end == str.length()) break;
				
				start = end;
				end += incr;
				if (end > str.length())
					end = str.length();
			}
						
			out.write(bos.toByteArray());
			bos.close();
			bos = null;
		} finally {
			System.gc();
		}

		out.flush();
		out.close();

		bout.flush();
		bout.close();

		return bout.toByteArray();
	}
}
