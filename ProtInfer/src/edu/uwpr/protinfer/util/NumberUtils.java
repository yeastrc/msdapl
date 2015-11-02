package edu.uwpr.protinfer.util;

public class NumberUtils {

    private NumberUtils() {}
    
    public static double round(double value, int decimalPlaces) {
        return (Math.round((value*Math.pow(10, decimalPlaces))))/(Math.pow(10, decimalPlaces));
    }
}
