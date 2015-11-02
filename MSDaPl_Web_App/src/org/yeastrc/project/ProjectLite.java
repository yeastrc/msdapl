/**
 * ProjectLite.java
 * @author Vagisha Sharma
 * Mar 24, 2009
 * @version 1.0
 */
package org.yeastrc.project;

/**
 * 
 */
public class ProjectLite {

    // The id this Project has in the database
    int id;
    String title;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
}
