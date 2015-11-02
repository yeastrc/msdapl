package ed.javatools;

public class PrimitiveTools {
	
	public static Double byteArrayToDouble(byte[] b){
		long temp = 0;
		temp = (((long)(b[0] & 0xff) << 56) | ((long)(b[1] & 0xff) << 48) | ((long)(b[2] & 0xff) << 40) | ((long)(b[3] & 0xff)<<32) | 
				((long)(b[4] & 0xff) << 24) | ((long)(b[5] & 0xff) << 16) | ((long)(b[6] & 0xff) << 8) | (long)(b[7] & 0xff) <<0);
		return Double.longBitsToDouble(temp);		
	}
	
	public static Float byteArrayToFloat(byte[] b){
		int temp = (((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16) | ((b[2] & 0xff) << 8) | (b[3] & 0xff) ); 
		return Float.intBitsToFloat(temp);
	}
	
	public static Double LEbyteArrayToDouble(byte[] b){
		long temp = 0;
		temp = (((long)(b[7] & 0xff) << 56) | ((long)(b[6] & 0xff) << 48) | ((long)(b[5] & 0xff) << 40) | ((long)(b[4] & 0xff)<<32) | 
				((long)(b[3] & 0xff) << 24) | ((long)(b[2] & 0xff) << 16) | ((long)(b[1] & 0xff) << 8) | (long)(b[0] & 0xff) <<0);
		return Double.longBitsToDouble(temp);		
	}
	
	public static Float LEbyteArrayToFloat(byte[] b){
		int temp = (((b[3] & 0xff) << 24) | ((b[2] & 0xff) << 16) | ((b[1] & 0xff) << 8) | (b[0] & 0xff) ); 
		return Float.intBitsToFloat(temp);
	}

	public static byte[] LEFloatTobyteArray(float f){
		int temp = Float.floatToIntBits(f);
		byte[] b = new byte[4];
		b[0] = (byte)((temp )>>>0);
		b[1] = (byte)((temp )>>>8);
		b[2] = (byte)((temp )>>>16);
		b[3] = (byte)((temp )>>>24);
		return b;
	}
	
}
