/*
 * Decompresser.java
 * Created on Oct 25, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.ms2.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Nov 16, 2004
 */

public class Decompresser {

	// Prevent foreign instantiation
	private Decompresser() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static Decompresser getInstance() {
		return new Decompresser();
	}

	/**
	 * Takes a byte array, which is a binary representation of compressed text,
	 * and decompresses (inflates it) and returns back the uncompressed String as an InputStream.
	 * The GZIP compression algorithm is attempted first.
	 * If it fails, the Zlib compression algorithm is attempted.
	 * @param compressed The byte array of compressed data
	 * @throws IOException If an IO problem is encountered
	 * @throws ZipException If a Zip problem is encountered
	 * @return The uncompressed data as an InputStream
	 */
	public InputStream decompressString(byte[] compressed) throws IOException, ZipException {

		ByteArrayInputStream bin = new ByteArrayInputStream(compressed);
		
		try {
			return new GZIPInputStream(bin);
		}
		catch (Exception ze) {
			Inflater inflater = new Inflater(true);
			return new InflaterInputStream(bin, inflater);
		}
	}
	
}
