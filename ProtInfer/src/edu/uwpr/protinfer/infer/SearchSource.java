package edu.uwpr.protinfer.infer;

public class SearchSource {

    private String fileName;
    private int id; // this can be a database id
    
    public SearchSource(String fileName) {
        this.fileName = fileName;
        this.id = -1;
    }
    
    public SearchSource(String fileName, int id) {
        this.fileName = fileName;
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public int getId() {
        return id;
    }
}
