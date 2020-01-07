package code;

import code.player.PlayerManager;
import code.playlist.Playlist;
import code.video.Video;
import database.DB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.util.List;

public class Controller {

    private List<Playlist> _PLAYLISTS;
    private List<Video> _VIDEOS;

    @FXML
    private MediaView mediaview;

    public void initialize(){
        PlayerManager.setMediaview(mediaview);
        PlayerManager.set();

        //setupVideos();
        //setupPlaylists();
    }

    private boolean test = false;

    @FXML
    void buttonpress(ActionEvent event) {

        PlayerManager.stopVideo();

    }

    private void setupPlaylists(){
        DB.selectSQL("SELECT fldPlaylistID FROM tblPlaylist");

        do{
            String resultset = DB.getData();
            if(resultset.equals(DB.NOMOREDATA)) break;
            _PLAYLISTS.add(new Playlist(Integer.parseInt(resultset)));
        }while(true);
    }

    private void setupVideos(){
        DB.selectSQL("SELECT fldVideoID FROM tblVideo");

        do{
            String resultset = DB.getData();
            if(resultset.equals(DB.NOMOREDATA)) break;
            _VIDEOS.add(new Video(Integer.parseInt(resultset)));
        }while(true);
    }
}
