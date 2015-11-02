package ed.mslib;

import java.io.IOException;

public interface ReadMS2Interface {
	public MS2Scan getScan(int scannumber) throws IOException;
	public MS2Scan getNextScan() throws IOException;
	
	/**
	 * should remove header tags if present
	 * @return
	 */
	public String getheader();
	public String getfilename();
	public void closeReader();
	public int getversion();
}
