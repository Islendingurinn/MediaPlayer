package code;

import database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
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

        _VIDEOS = new ArrayList<>();
        _PLAYLISTS = new ArrayList<>();

        setupVideos();
        setupPlaylists();

        System.out.println(_VIDEOS.size());
    }

    @FXML
    private void createPlaylist(ActionEvent event) {
        if(mediaview.getMediaPlayer() != null) return;

        selectedVideos = videos.getSelectionModel().getSelectedItems();
        if(selectedVideos.size() > 0){
            waitingForPlaylistName = true;
            playlistName.setVisible(true);
            playlistName.requestFocus();
            System.out.println("creating");
        }else selectedVideos = null;
    }

    @FXML
    private void onPlaylistName(ActionEvent event){
        List<Video> playlistVideos = new ArrayList<>();
        for(String toString : selectedVideos){
            for(Video video : _VIDEOS){
                if(video.toString().equalsIgnoreCase(toString)){
                    playlistVideos.add(video);
                    System.out.println("Videos: " + playlistVideos.size());
                    break;
                }
            }
        }

        Playlist newPlaylist = new Playlist(-1, playlistName.getText(), playlistVideos);
        newPlaylist.save();
        playlistName.setVisible(false);

        _PLAYLISTS.clear();
        _DISPLAYEDPLAYLISTS.clear();
        setupPlaylists();
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
            currentPlaylist.setVisible(false);
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

        _CURRENTPLAYLIST.clear();
        System.out.println("Playlist: " + playlist);
        System.out.println("Size: " + playlist.getVideos().size());
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

    @FXML
    private void searched(KeyEvent event){
        PlayerManager.stopVideo();
        videos.setVisible(false);
        currentPlaylist.setVisible(true);

        _CURRENTPLAYLIST.clear();

        String searchTerm = search.getText();
        for(Video video : _VIDEOS){
            if(video.compares(searchTerm)) _CURRENTPLAYLIST.add(video.toString());
        }
    }

    private void setupPlaylists(){

        DB.selectSQL("SELECT count(fldPlaylistID) FROM tblPlaylist");
        int nrValues = Integer.parseInt(DB.getData());

        for (int i = 1; i <= nrValues; i++) {
            DB.selectSQL("SELECT fldName FROM tblPlaylist WHERE fldPlaylistID=" + i);
            String name = DB.getData();

            List<Video> playlistVideos = new ArrayList<>();
            DB.selectSQL("SELECT fldVideoID FROM tblMapping WHERE fldPlaylistID=" + i);
            do{
                String mappingset = DB.getData();
                if(mappingset.equals(DB.NOMOREDATA)) break;

                int videoID = Integer.parseInt(mappingset);
                for(Video video : _VIDEOS){
                    if(video.getID() == videoID) playlistVideos.add(video);
                }
            }while(true);

            Playlist newPlaylist = new Playlist(i, name, playlistVideos);
            _PLAYLISTS.add(newPlaylist);
            System.out.println("end");
        }


    }

    private void setupVideos(){
        DB.selectSQL("SELECT fldVideoID FROM tblVideo");

        do{
            String resultset = DB.getData();
            if(resultset.equals(DB.NOMOREDATA)) break;

            int id = Integer.parseInt(resultset);

            DB.selectSQL("SELECT fldName FROM tblVideo WHERE fldVideoID=" + id);
            String name = DB.getData();

            DB.selectSQL("SELECT fldPath FROM tblVideo WHERE fldVideoID=" + id);
            String path = DB.getData();

            DB.selectSQL("SELECT fldCategory FROM tblVideo WHERE fldVideoID=" + id);
            String category = DB.getData();

            Video newVideo = new Video(id, name, path, category);
            _VIDEOS.add(newVideo);
        }while(true);
    }
}
