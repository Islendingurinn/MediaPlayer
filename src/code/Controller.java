package code;

import database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    public static ObservableList<String> _DISPLAYEDPLAYLISTS;
    public static ObservableList<String> _DISPLAYEDVIDEOS;
    public static ObservableList<String> _CURRENTPLAYLIST;
    public static List<Playlist> _PLAYLISTS;
    public static List<Video> _VIDEOS;

    private List<String> selectedVideos;
    private boolean waitingForPlaylistName = false;

    @FXML
    private MediaView mediaview;

    @FXML
    public StackPane center;

    @FXML
    private ListView<String> playlists;

    @FXML
    private TextField search;

    @FXML
    private TextField playlistName;

    @FXML
    private ListView<String> videos;

    @FXML
    private ListView<String> currentPlaylist;

    public void initialize(){
        PlayerManager.setComponents(mediaview, videos);

        _DISPLAYEDPLAYLISTS = FXCollections.observableArrayList();
        playlists.setItems(_DISPLAYEDPLAYLISTS);

        _DISPLAYEDVIDEOS = FXCollections.observableArrayList();
        videos.setItems(_DISPLAYEDVIDEOS);
        videos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        _CURRENTPLAYLIST = FXCollections.observableArrayList();
        currentPlaylist.setItems(_CURRENTPLAYLIST);

        //setupVideos();
        //setupPlaylists();
    }

    @FXML
    private void createPlaylist(ActionEvent event) {
        if(mediaview.getMediaPlayer() != null) return;

        selectedVideos = videos.getSelectionModel().getSelectedItems();
        if(selectedVideos.size() > 0){
            waitingForPlaylistName = true;
            playlistName.setVisible(true);
        }else selectedVideos = null;
    }

    @FXML
    private void onPlaylistName(ActionEvent event){
        List<Video> playlistVideos = new ArrayList<>();
        for(String toString : selectedVideos){
            for(Video video : _VIDEOS){
                if(video.toString().equalsIgnoreCase(toString)){
                    playlistVideos.add(video);
                    break;
                }
            }
        }

        //TODO:
        Playlist newPlaylist = new Playlist(1, playlistName.getText(), playlistVideos);
        playlistName.setVisible(false);
    }

    @FXML
    private void videoInteract(ActionEvent event) {

        if(mediaview.isVisible()){
            PlayerManager.handleInteraction();
        }else if(videos.isVisible()){
            //LIBRARY ACTIONS play all
            List<Video> selectedVideos = new ArrayList<>();
            for(String toString : videos.getSelectionModel().getSelectedItems()){
                for(Video video : _VIDEOS) {
                    if (video.toString().equalsIgnoreCase(toString)) {
                        selectedVideos.add(video);
                        break;
                    }
                }
            }

            if(selectedVideos.size() == 0) return;
            videos.setVisible(false);
            PlayerManager.play(selectedVideos);
        }else if(currentPlaylist.isVisible()){
            //PLAY PLAYLIST
            List<Video> selectedVideos = new ArrayList<>();
            for(String toString : currentPlaylist.getSelectionModel().getSelectedItems()){
                for(Video video : _VIDEOS) {
                    if (video.toString().equalsIgnoreCase(toString)) {
                        selectedVideos.add(video);
                        break;
                    }
                }
            }

            if(selectedVideos.size() == 0){
                for(String toString : currentPlaylist.getItems()){
                    for(Video video : _VIDEOS) {
                        if (video.toString().equalsIgnoreCase(toString)) {
                            selectedVideos.add(video);
                            break;
                        }
                    }
                }
            }

            currentPlaylist.setVisible(false);
            _CURRENTPLAYLIST.clear();
            PlayerManager.play(selectedVideos);
        }
    }

    @FXML
    private void playlistInteract(MouseEvent mouseEvent) {
        String playlistClicked = playlists.getSelectionModel().getSelectedItem();
        if(playlistClicked == null) return;
        PlayerManager.stopVideo();
        videos.setVisible(false);
        currentPlaylist.setVisible(true);

        int id = Integer.parseInt(playlistClicked.split(". ")[0]);
        Playlist playlist = null;
        for(Playlist p : _PLAYLISTS){
            if(p.getID() == id){
                playlist = p;
                break;
            }
        }

        for(Video video : playlist.getVideos()){
            _CURRENTPLAYLIST.add(video.toString());
        }
    }

    @FXML
    private void requestLibrary(ActionEvent event) {
        PlayerManager.stopVideo();
        currentPlaylist.setVisible(false);
        videos.setVisible(true);
    }

    private void setupPlaylists(){
        DB.selectSQL("SELECT fldPlaylistID FROM tblPlaylist");

        do{
            String resultset = DB.getData();
            if(resultset.equals(DB.NOMOREDATA)) break;
            //_PLAYLISTS.add(new Playlist(Integer.parseInt(resultset)));
        }while(true);
    }

    private void setupVideos(){
        DB.selectSQL("SELECT fldVideoID FROM tblVideo");

        do{
            String resultset = DB.getData();
            if(resultset.equals(DB.NOMOREDATA)) break;
            //_VIDEOS.add(new Video(Integer.parseInt(resultset)));
        }while(true);
    }
}
