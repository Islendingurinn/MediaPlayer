package code.video;

import database.DB;

public class VideoManager {

    public void addVideo(String name, String path, String category){

        DB.insertSQL("INSERT INTO tblVideo (fldName, fldPath, fldCategory) VALUES (" + name + ", " + path + ", " + category + ")");

    }

    public void removeVideo(String name){
        DB.deleteSQL("DELETE FROM tblVideo WHERE fldName=" + name);
    }

    public void removeVideo(int id){
        DB.deleteSQL("DELETE FROM tblVideo WHERE fldVideoID="+id);
    }
}
