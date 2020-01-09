package code.playlist;

import code.Controller;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private int _ID;
    private String _NAME;
    private List<Integer> _VIDEOS;

    public Playlist(int _ID){
        this._ID = _ID;
        this._VIDEOS = new ArrayList<>();

        load();
    }

    private void load(){
        _VIDEOS.clear();

        //DB.selectSQL("SELECT fldName FROM tblPlaylist WHERE fldPlaylistID=" + this._ID);
        //this._NAME = DB.getData();
        this._NAME = "Test Playlist";
        Controller._DISPLAYEDPLAYLISTS.add(_NAME);

        /*DB.selectSQL("SELECT fldVideoID FROM tblMapping WHERE fldPlaylistID=" + this._ID);
        do{
            String resultset = DB.getData();
            if(resultset.equals(DB.NOMOREDATA)) break;
            _VIDEOS.add(Integer.parseInt(resultset));
        }while(true);*/
    }

    private void unload(){
        _VIDEOS.clear();
        Controller._DISPLAYEDPLAYLISTS.remove(_NAME);
    }

    private void save(){
        PlaylistManager.createPlaylist(_NAME, _VIDEOS);
    }

    private void delete(){
        PlaylistManager.deletePlaylist(_ID);
        unload();
    }

    private void add(int videoID){
        this._VIDEOS.add(videoID);
        PlaylistManager.addVideoToPlaylist(_ID, videoID);
        //TODO: CHECK?
    }

    private void remove(int videoID){
        this._VIDEOS.remove(videoID);
        PlaylistManager.removeVideoFromPlaylist(_ID, videoID);
    }

    public List<Integer> getVideos(){
        return this._VIDEOS;
    }
}
