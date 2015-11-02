package org.yeastrc.www.project;

public enum SORT_CLASS {

    SORT_ALPHA("sort-alpha"),
    SORT_INT("sort-int"),
    SORT_FLOAT("sort-float");
    
    private String cssClass;
    private SORT_CLASS(String cssClass) {
        this.cssClass = cssClass;
    }
    
    public String getCssClass() {
        return cssClass;
    }
}
