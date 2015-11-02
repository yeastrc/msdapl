/**
 * TimeUtils.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.util;

/**
 * 
 */
public class TimeUtils {

    private TimeUtils() {}
    
    public static float timeElapsedSeconds(long start, long end) {
        if(end < start)
            return 0;
        return (end - start)/(1000.0f);
    }
    
    public static float timeElapsedMinutes(long start, long end) {
        if(end < start)
            return 0;
        return (end - start)/(1000.0f * 60.0f);
    }
}
