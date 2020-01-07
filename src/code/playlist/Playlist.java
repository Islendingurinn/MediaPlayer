package code.playlist;

import database.DB;

import java.util.List;

public class Playlist {

    private int _ID;
    private List<Integer> videos;

    public Playlist(int _ID){
        this._ID = _ID;

        load();
    }

    private void load(){
        videos.clear();
        DB.selectSQL("SELECT fldVideoID FROM tblMapping WHERE fldPlaylistID=" + this._ID);

        do{
            String resultset = DB.getData();
            if(resultset.equals(DB.NOMOREDATA)) break;
            videos.add(Integer.parseInt(resultset));
        }while(true);
    }

    public List<Integer> getVideos(){
        return this.videos;
    }
}
