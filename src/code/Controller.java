package code;

import code.player.PlayerManager;
import code.playlist.Playlist;
import code.playlist.PlaylistManager;
import code.video.Video;
import database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaView;

import java.util.List;

public class Controller {

    public static ObservableList<String> _DISPLAYEDPLAYLISTS;
    private List<Playlist> _PLAYLISTS;
    private List<Video> _VIDEOS;

    @FXML
    private MediaView mediaview;

    @FXML
    private TextField search;

    @FXML
    private ListView<String> playlists;

    @FXML
    private Button play;

    @FXML
    private Label library;

    @FXML
    private Button last_added;

    @FXML
    private Button collection;

    @FXML
    private Button create_playlist;

    public void initialize(){
        PlayerManager.setMediaview(mediaview);
        PlayerManager.set();

        _DISPLAYEDPLAYLISTS = FXCollections.observableArrayList();
        playlists.setItems(_DISPLAYEDPLAYLISTS);

        //setupVideos();
        //setupPlaylists();
    }

    @FXML
    private void createPlaylist(ActionEvent event) {
        Playlist playlist = new Playlist(1);
    }

    @FXML
    private void videoInteract(ActionEvent event) {
        PlayerManager.handleInteraction();
    }

    @FXML
    private void playlistInteract(MouseEvent mouseEvent) {
        PlaylistManager.handleInteraction(playlists);
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
