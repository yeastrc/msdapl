/**
 * HyperlinkedData.java
 * @author Vagisha Sharma
 * Sep 1, 2010
 */
package org.yeastrc.www.misc;

/**
 * 
 */
public class HyperlinkedData extends Data {

	private String hyperlink;
    private boolean absoluteHyperlink = false;
    private boolean newWindow = false;
    private String targetName = null;
    
    public HyperlinkedData(String data) {
    	super(data);
    }
	
    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
    
    
    public String getHyperlink() {
        return hyperlink;
    }
    
    /**
     * If the full url is http://localhost:8080/viewProject.do?ID=20
     * the value of the <code>hyperlink</code> parameter should be
     * viewProject.do?ID=20
     * @param hyperlink
     */
    public void setHyperlink(String hyperlink) {
        this.setHyperlink(hyperlink, false, false);
    }
    
    public void setHyperlink(String hyperlink, boolean newWindow) {
        setHyperlink(hyperlink, false, newWindow);
    }
    
    public void setHyperlink(String hyperlink, boolean absolute, boolean newWindow) {
        this.hyperlink = hyperlink;
        this.newWindow = newWindow;
        this.absoluteHyperlink = absolute;
    }
    
    public boolean isAbsoluteHyperlink() {
        return this.absoluteHyperlink;
    }
    
    public void setAbsoluteHyperLink(String hyperlink) {
        this.setHyperlink(hyperlink, true, false);
    }
    
    public void setAbsoluteHyperlink(String hyperlink, boolean newWindow) {
        setHyperlink(hyperlink, true, newWindow);
    }
    
    public boolean openLinkInNewWindow() {
        return this.newWindow;
    }
}
