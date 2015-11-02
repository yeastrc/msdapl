/**
 * InstrumentColors.java
 * @author Vagisha Sharma
 * May 21, 2009
 * @version 1.0
 */
package org.uwpr.instrumentlog;

/**
 * 
 */
public class InstrumentColors {

    private InstrumentColors() {}
    
    public static final String[] INSTRUMENT_COLORS = new String[] {
        "e0c240",   // dirty yellow
        "228B22	",   	// forest green
        "D2691E	",  // chocolate
        "00CED1",  	// turquoise
        "7FFF00",   // chartreuse
        "1E90FF",       // dodger blue
        "ffa500",  // amber
        "006400",  // dark green
        "9932CC",  // Dark Orchid
        "20b2aa",  // light teal
        "dd4477",  // pink
        "FF4500",  // OrangeRed (http://en.wikipedia.org/wiki/Web_colors)
        "4682B4",  // SteelBlue (http://en.wikipedia.org/wiki/Web_colors)
        "9370DB",   	// medium purple
        "FFD700",   	// gold
        "FF4500",   	// orange red
        "00BFFF",   // deep sky blue
        "DC143C",   // Crimson
        "8b008b",   // purple
        "EE82EE",   // violet
        "800080",   // purple
        "9ACD32"  	// lime green
        };

    // green #8CBF40
    // blue #3366CC
    public static String getColor(int instrumentId) {
        return INSTRUMENT_COLORS[instrumentId % INSTRUMENT_COLORS.length];
    }
}
