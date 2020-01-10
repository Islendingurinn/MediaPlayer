package code;

import code.Controller;
import database.DB;

import java.io.File;

public class Video {

    private int _ID;
    private String _NAME;
    private String _PATH;
    private String _CATEGORY;

    public Video(int id, String name, String path, String category){
        _ID = id;
        _NAME = name;
        _PATH = path;
        _CATEGORY = category;

        if(_ID == -1) setupID();
    }

    public int getID(){
        return _ID;
    }

    public String getPath(){
        return _PATH;
    }

    @Override
    public String toString(){
        return _ID + ". " + _NAME + "\n" + _CATEGORY;
    }

    public void toggleDisplay(){
        if(Controller._DISPLAYEDVIDEOS.contains(toString()))
            Controller._DISPLAYEDVIDEOS.add(toString());
        else
            Controller._DISPLAYEDVIDEOS.remove(toString());
    }

    private int setupID(){
        DB.selectSQL("SELECT count(fldVideoID) IN tblVideo");
        return Integer.parseInt(DB.getData());
    }

    public void save(){
        DB.insertSQL("INSERT INTO tblVideo (fldVideoID, fldName, fldPath, fldCategory) VALUES (" + _ID + ", " + _NAME + ", " + _PATH + ", " + _CATEGORY + ")");
    }

    public void delete(){
        DB.deleteSQL("DELETE FROM tblVideo WHERE fldVideoID=" + _ID);
    }

    public boolean compares(String search){
        if(search.contains("" + _ID)) return true;
        if(search.contains(_NAME)) return true;
        if(search.contains(_CATEGORY)) return true;

        return false;
    }

}
