/*
 * Edward Hsieh
 * 
 */
package ed.javatools;

import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.EOFException;

/**
 * Methods that read directly into a byte array disrupt the file pointer!
 *
 * @author Ed
 */
public class BufferedRaf extends RandomAccessFile {

	public BufferedRaf(File file, String mode) throws
		FileNotFoundException {
		super(file, mode);
		bufferlength = 65536;
		bytebuffer = new byte[bufferlength];
		maxread = 0;
		buffpos = 0;
		sb = new StringBuilder("0");
	}
	
	private byte[] bytebuffer;
	private int bufferlength;
	private int maxread;
	private int buffpos;
	
	private StringBuilder sb;

	public int getbuffpos() {
		return buffpos;
	}

	@Override
	public int read() throws IOException {
		if (buffpos >= maxread) {
			maxread = readchunk();
			if (maxread == -1) {
				return -1;
			}
		}
		buffpos++;
		return bytebuffer[buffpos - 1];
	}
	
	public int read(byte[] b) throws IOException{
		return this.read(b, 0, b.length);
	}
	
	public int read(byte[] b, int off, int len) throws IOException {
		int count=0;
		byte temp;
		for (int i=0; i<len; i++, off++){
			temp = (byte) this.read();
			if (temp == -1){
				return -1;
			}
			b[off] = temp;
			count++;
		}
		return count;
	}
	
	
	public String readLine2() throws IOException {
		sb.delete(0, sb.length());
		int c = -1;
		boolean eol = false;

		while (!eol) {
			switch (c = read()) {
				case -1:
				case '\n':
					eol = true;
					break;
				case '\r':
					eol = true;
					long cur = getFilePointer();
					if ((read()) != '\n') {
						seek(cur);
					}
					break;
				default:
					sb.append((char) c);
					break;
			}
		}

		if ((c == -1) && (sb.length() == 0)) {
			return null;
		}
		return sb.toString();
	}
	 
	@Override
	public long getFilePointer() throws IOException {
		return super.getFilePointer() + buffpos;
	}

	@Override
	public void seek(long pos) throws IOException {
		if (maxread != -1 && pos < (super.getFilePointer() + maxread) && pos > super.getFilePointer()) {
			Long diff = (pos - super.getFilePointer());
			if (diff < Integer.MAX_VALUE) {
				buffpos = diff.intValue();
			} else {
				throw new IOException("something wrong w/ seek");
			}
		} else {
			buffpos = 0;
			super.seek(pos);
			maxread = readchunk();
		}
	}

	private int readchunk() throws IOException {
		long pos = super.getFilePointer() + buffpos;
		super.seek(pos);
		int read = super.read(bytebuffer);
		super.seek(pos);
		buffpos = 0;
		return read;
	}
	
	/**
	 * reads 4 bytes, in Little Endian order, returns int
	 * @return int value of 4 bytes in little endian order
	 * @throws IOException
	 */
	public int readLEInt() throws IOException,EOFException{
		byte[] b = new byte[4];
		for (int i=0; i<4; i++){
			b[i] = (byte)read();
			if ((b[i] & 0xff) ==-1){
				throw new EOFException();
			}
		}
			
		return (((b[3] & 0xff) <<24) | ((b[2] & 0xff) << 16) | ((b[1] & 0xff) << 8) | (b[0] & 0xff) );
	}
	
	public double readLEDouble() throws IOException, EOFException{
		byte[] b = new byte[8];
		for (int i=0; i<8; i++){
			b[i] = (byte)read();
			if ((b[i] & 0xff) ==-1){
				throw new EOFException();
			}
		}
		
		long temp = 0;
		temp = (((long)(b[7] & 0xff) << 56) | ((long)(b[6] & 0xff) << 48) | ((long)(b[5] & 0xff) << 40) | ((long)(b[4] & 0xff)<<32) | 
				((long)(b[3] & 0xff) << 24) | ((long)(b[2] & 0xff) << 16) | ((long)(b[1] & 0xff) << 8) | (long)(b[0] & 0xff) <<0);
		return Double.longBitsToDouble(temp);
	}
	
	public float readLEFloat() throws IOException, EOFException{
		byte[] b = new byte[4];
		for (int i=0; i<4; i++){
			b[i] = (byte)read();
			if ((b[i] & 0xff) ==-1){
				throw new EOFException();
			}
		}

		int temp = (((b[3] & 0xff) << 24) | ((b[2] & 0xff) << 16) | ((b[1] & 0xff) << 8) | (b[0] & 0xff) ); 
		return Float.intBitsToFloat(temp);
	}
	
}
