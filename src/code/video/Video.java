package code.video;

import code.Controller;
import database.DB;

import java.io.File;

public class Video {

    private int _ID;
    private String _NAME;
    private String _PATH;
    private String _CATEGORY;

    public Video(int _ID){
        this._ID = _ID;

        load();
    }

    private void load(){
        DB.selectSQL("SELECT fldName FROM tblVideo WHERE fldVideoID=" + _ID);
        _NAME = DB.getData();

        DB.selectSQL("SELECT fldPath FROM tblVideo WHERE fldVideoID=" + _ID);
        _PATH = DB.getData();
        if(!(new File(_PATH)).exists()) _PATH = "N/A";

        DB.selectSQL("SELECT fldCategory FROM tblVideo WHERE fldVideoID=" + _ID);
        _CATEGORY = DB.getData();

        Controller._DISPLAYEDVIDEOS.add(_NAME);
    }
}
