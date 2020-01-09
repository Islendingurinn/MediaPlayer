package code;

import code.player.PlayerManager;
import code.playlist.Playlist;
import code.playlist.PlaylistManager;
import code.video.Video;
import database.DB;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;

import java.util.List;

public class Controller {

    public static ObservableList<String> _DISPLAYEDPLAYLISTS;
    private List<Playlist> _PLAYLISTS;
    private List<Video> _VIDEOS;

    @FXML
    private MediaView mediaView;

    @FXML
    public StackPane center;

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
        PlayerManager.setMediaview(mediaView);
        PlayerManager.set(center);

        _DISPLAYEDPLAYLISTS = FXCollections.observableArrayList();
        playlists.setItems(_DISPLAYEDPLAYLISTS);

        //setupVideos();
        //setupPlaylists();

        //playlists.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
