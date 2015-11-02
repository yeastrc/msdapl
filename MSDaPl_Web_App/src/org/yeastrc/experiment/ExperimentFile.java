/**
 * ExperimentFile.java
 * @author Vagisha Sharma
 * Apr 13, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

/**
 * 
 */
public class ExperimentFile implements File {

    private int id;
    private String fileName;
    private boolean selected = false;
    
    
    public ExperimentFile() {}
    
    public ExperimentFile(int id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public int getId() {
        return id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
