/**
 * MzMplusHConverter.java
 * @author Vagisha Sharma
 * Jan 26, 2011
 */


/**
 * 
 */
public class MzMplusHConverter {
	
	private static final double MASS_PROTON = 1.007276;
	
	private MzMplusHConverter() {}
	
	public static double toMz(double mplusH, int charge) {
		return (mplusH + ((charge-1) * MASS_PROTON)) / (double) charge;
	}
	
	public static double toMplusH(double mz, int charge) {
		
		return (mz * charge) - ((charge -1) * MASS_PROTON);
	}
	
	public static void main(String[] args) {
		
		System.out.println("mph: "+1036.85272351+"; m/z: "+toMz(1036.85272351, 2));
		System.out.println("mph: "+1554.77544702+"; m/z: "+toMz(1554.77544702, 3));
		System.out.println("");
		System.out.println("m/z: "+518.93+"; mph: "+toMplusH(518.93, 2));
		System.out.println("m/z: "+518.93+"; mph: "+toMplusH(518.93, 3));
		System.out.println("");
		System.out.println("mph: "+2493.96+"; charge: 2; m/z: "+toMz(2493.96, 2));
		System.out.println("mph: "+3740.11+"; charge: 3; m/z: "+toMz(3740.11, 3));
		System.out.println("");
		// Z Ê Ê ÊÊ2077.46 2
		// Z Ê Ê ÊÊ3115.76 3
		System.out.println("mph: "+2077.46+"; charge: 2; m/z: "+toMz(2077.46, 2));
		System.out.println("mph: "+3115.76+"; charge: 3; m/z: "+toMz(3115.76, 3));
		
		/*
		 * > S     4398    4398    1129.7486
> Z     2       2258.4900
> Z     3       3387.2314

		 */
		System.out.println("mph: "+3387.2314+"; charge: 3; m/z: "+toMz(3387.2314, 3));
		
		System.out.println(""+toMz(1355.6464, 3	));
	}
}
