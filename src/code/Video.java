package code;

import database.DB;

public class Video {

    private int _ID;
    private String _NAME;
    private String _PATH;
    private String _CATEGORY;

    /**
     * Constructor
     * @param id ID of the video
     * @param name Name of the video
     * @param path Path to the video
     * @param category Category of the video
     */
    public Video(int id, String name, String path, String category){
        _ID = id;
        _NAME = name;
        _PATH = path;
        _CATEGORY = category;

        toggleDisplay();
        if(_ID == -1) this._ID = setupID();
    }

    /**
     * A method to get the ID of the Video
     * @return The int ID of the video
     */
    public int getID(){
        return _ID;
    }

    /**
     * A method to get the path of the Video
     * @return The String path of the video
     */
    public String getPath(){
        return _PATH;
    }

    /**
     * A method to get the display of the Video
     * @return String display for the Video
     */
    @Override
    public String toString(){
        return _ID + ". " + _NAME + "\n" + _CATEGORY;
    }

    /**
     * A method to toggle display of the video in the library
     */
    public void toggleDisplay(){
        if(!Controller._DISPLAYEDVIDEOS.contains(toString()))
            Controller._DISPLAYEDVIDEOS.add(toString());
        else
            Controller._DISPLAYEDVIDEOS.remove(toString());
    }

    /**
     * A method to get the expected ID of the video
     * according to the DB
     * @return int count of IDs from DB
     */
    private int setupID(){
        DB.selectSQL("SELECT count(fldVideoID) FROM tblVideo");

        int id = -1;
        do{
            String resultset = DB.getData();

            if(resultset.equals(DB.NOMOREDATA)) break;
            id = Integer.parseInt(resultset);
        }while(true);

        return id+1;
    }

    /**
     * Saves the Video data into the DB
     */
    public void save(){
        DB.insertSQL("INSERT INTO tblVideo (fldVideoID, fldName, fldPath, fldCategory) VALUES (" + _ID + ", " + _NAME + ", " + _PATH + ", " + _CATEGORY + ")");
    }

    /**
     * Deletes the Video data from the DB
     */
    public void delete(){
        DB.deleteSQL("DELETE FROM tblVideo WHERE fldVideoID=" + _ID);
    }

    /**
     * Allows for better searching. If the term
     * includes any of the information provided
     * for the Video, return true
     * @param search Search term
     * @return boolean if any term contains the search
     */
    public boolean compares(String search){
        if(search.contains("" + _ID)) return true;
        if(search.contains(_NAME)) return true;
        if(search.contains(_CATEGORY)) return true;

        return false;
    }

}
