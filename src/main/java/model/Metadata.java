package model;

import java.util.ArrayList;
import java.util.Date;

public class Metadata {
    
    private Date creationDate;
    
    private ArrayList<String> folderContent;

    public Metadata(ArrayList<String> folderContent) {
        this.folderContent = folderContent;
        
        creationDate = new Date();
    }
    
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public ArrayList<String> getFolderContent() {
        return folderContent;
    }

    public void setFolderContent(ArrayList<String> folderContent) {
        this.folderContent = folderContent;
    }
    
    public String toString() {
        String log = creationDate.toString() + "/r/n";
        
        for(String file : getFolderContent()) {
            log.concat(file + "/r/n");
        }
        
        return log;
    }
}
