package ed.mslib;

public class MzInt {

	public MzInt(double mz, float intensity){
		this.mz = mz;
		this.intensity = intensity;
	}
	
	private double mz;
	private float intensity;
	
	public double getmz(){return mz;}
	public float getint(){return intensity;}	
	
	public void setmz(double m){mz = m;}
	public void setint(float i){intensity = i;}
}
