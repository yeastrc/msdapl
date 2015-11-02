package org.yeastrc.www.compare.dataset;

public enum DatasetColor {

    BLUE_1(3, 109, 217),
    RED_2(255, 3, 3),
    GREEN_3(133, 253, 3),
    TEAL_2(2, 149, 143),
    
    
    PURPLE(120, 2, 113),
    ORANGE(253, 121, 3),
    GREEN_1(3, 213, 37),
    PINK_2(154, 2, 109),
    
    
    BLUE_2(3, 3, 215),
    RED_1(186, 15, 2),
    BLUE_3(3, 207, 214),
    PINK_1(216, 3, 183),
    
    
    TEAL_1(2, 99, 149),
    GREEN_2(102, 142, 43),
    TEAL_3(2, 119, 115);
    
    public final int R;
    public final int G;
    public final int B;
    
    private DatasetColor(int r, int g, int b) {
        R = r;
        G = g;
        B = b;
    }
    
    public String hexValue() {
        String red = Integer.toHexString(R);
        if(red.length() == 1)
            red = "0"+red;
        String blue = Integer.toHexString(B);
        if(blue.length() == 1)
            blue = "0"+blue;
        String green = Integer.toHexString(G);
        if(green.length() == 1)
            green = "0"+green;
        return red+green+blue;
    }
    
    public static DatasetColor get(int index) {
        DatasetColor[] colorArr = DatasetColor.values();
        index = index % colorArr.length;
        return colorArr[index];
    }
    
}
