package code;

import database.DB;

import java.util.List;

public class Playlist {

    private int _ID;
    private String _NAME;
    private List<Video> _VIDEOS;

    public Playlist(int id, String name, List<Video> videos){
        this._ID = id;
        this._NAME = name;
        this._VIDEOS = videos;
    }

    public int getID(){
        return _ID;
    }

    @Override
    public String toString(){
        return _ID + ". " + _NAME;
    }

    public List<Video> getVideos(){
        return _VIDEOS;
    }

    private void toggleDisplay(){
        if(Controller._DISPLAYEDPLAYLISTS.contains(toString()))
            Controller._DISPLAYEDPLAYLISTS.add(toString());
        else
            Controller._DISPLAYEDPLAYLISTS.remove(toString());
    }

    private void save(){
        DB.insertSQL("INSERT INTO tblPlaylist (fldPlaylistID, fldName) VALUES (" + _ID + ", " + _NAME + ")");

        for(Video video : _VIDEOS){
            DB.insertSQL("INSERT INTO tblMapping (fldPlaylistID, fldVideoID) VALUES (" + _ID + ", " + video.getID() + ")");
        }
    }

    private void delete(){
        DB.deleteSQL("DELETE FROM tblPlaylist WHERE fldPlaylistID=" + _ID);
        DB.deleteSQL("DELETE FROM tblMapping WHERE fldPlaylistID=" + _ID);
    }

    private void add(Video video){
        _VIDEOS.add(video);
        DB.insertSQL("INSERT INTO tblMapping (fldPlaylistID, fldVideoID) VALUES (" + _ID + ", " + video.getID() + ")");
        //TODO: CHECK?
    }

    private void remove(Video video){
        _VIDEOS.remove(video);
        DB.deleteSQL("DELETE FROM tblMapping WHERE fldPlaylistID=" + _ID + " AND fldVideoID=" + video.getID());
    }
}
