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

        if(this._ID == -1) this._ID = setupID();
        toggleDisplay();
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

    public void toggleDisplay(){
        if(!Controller._DISPLAYEDPLAYLISTS.contains(toString()))
            Controller._DISPLAYEDPLAYLISTS.add(toString());
        else
            Controller._DISPLAYEDPLAYLISTS.remove(toString());
    }

    private int setupID(){
        DB.selectSQL("SELECT count(fldPlaylistID) FROM tblPlaylist");
        //String resultset = DB.getData();

        int id = -1;
        do{
            String resultset = DB.getData();
            System.out.println(resultset);
            if(resultset.equals(DB.NOMOREDATA)) break;
            id = Integer.parseInt(resultset);
        }while(true);

        return id+1;
    }

    public void save(){
        DB.insertSQL("INSERT INTO tblPlaylist (fldPlaylistID, fldName) VALUES (" + _ID + ", '" + _NAME + "')");

        for(Video video : _VIDEOS){
            DB.insertSQL("INSERT INTO tblMapping (fldVideoID, fldPlaylistID) VALUES (" + video.getID() + ", " + _ID + ")");
        }
    }

    private void delete(){
        DB.deleteSQL("DELETE FROM tblPlaylist WHERE fldPlaylistID=" + _ID);
        DB.deleteSQL("DELETE FROM tblMapping WHERE fldPlaylistID=" + _ID);
    }

    public void add(Video video){
        _VIDEOS.add(video);
        DB.insertSQL("INSERT INTO tblMapping (fldPlaylistID, fldVideoID) VALUES (" + _ID + ", " + video.getID() + ")");
        //TODO: CHECK?
    }

    public void remove(Video video){
        _VIDEOS.remove(video);
        DB.deleteSQL("DELETE FROM tblMapping WHERE fldPlaylistID=" + _ID + " AND fldVideoID=" + video.getID());
    }
}
