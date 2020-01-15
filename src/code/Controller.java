package code;

import database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
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

    @FXML
    private Label videoTimestamp;

    @FXML
    private Label videoLength;

    @FXML
    private Slider volume;

    @FXML
    private Button library;
    @FXML
    private VBox sidemenu;

    /**
     * Method ran upon opening the program.
     */
    public void initialize(){
        // Load fonts
        Font.loadFont(Controller.class.getResourceAsStream("/display/fonts/OpenSans-Regular.ttf"), 16);
        Font.loadFont(Controller.class.getResourceAsStream("/display/fonts/OpenSans-Bold.ttf"), 16);

        //Constructs the components for PlayerManager
        PlayerManager.setComponents(mediaview, videoTimestamp, videoLength);

        //Sets up the ListViews that display Videos and Playlists
        _DISPLAYEDPLAYLISTS = FXCollections.observableArrayList();
        playlists.setItems(_DISPLAYEDPLAYLISTS);

        _DISPLAYEDVIDEOS = FXCollections.observableArrayList();
        videos.setItems(_DISPLAYEDVIDEOS);
        videos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        _CURRENTPLAYLIST = FXCollections.observableArrayList();
        currentPlaylist.setItems(_CURRENTPLAYLIST);
        currentPlaylist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //Sets up the Videos and Playlists from the DB
        _VIDEOS = new ArrayList<>();
        _PLAYLISTS = new ArrayList<>();

        setupVideos();
        setupPlaylists();

        //Sets up the volume property and fixes the video window size
        volume.valueProperty().addListener((observable, oldValue, newValue) -> PlayerManager.volumeVideo(newValue.doubleValue()/100));

        mediaview.fitWidthProperty().bind(center.widthProperty().subtract(100));
        mediaview.fitHeightProperty().bind(center.heightProperty().subtract(100));
    }

    @FXML
    private void handleButtonFile()
    {
        try {
            AnchorPane file = FXMLLoader.load(FileController.class.getResource("/display/FileView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add file");
            stage.setScene(new Scene(file));
            stage.show();
            FileController.init(stage);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Listener for when "Create Playlist" is clicked.
     * Upon click, verify that videos are selected.
     * If they are, enable visibility for the Name text field
     * so the user can pick out a name for the playlist.
     */
    @FXML
    private void createPlaylist(ActionEvent event) {
        if(mediaview.getMediaPlayer() != null) return;
        if(waitingForPlaylistName) return;

        selectedVideos = videos.getSelectionModel().getSelectedItems();
        if(selectedVideos.size() > 0){
            waitingForPlaylistName = true;
            playlistName.setVisible(true);
            playlistName.requestFocus();
        }else selectedVideos = null;
    }

    /**
     * Listens for when a name has been entered while creating
     * a playlist. Upon confirmation, add all the selected videos to
     * a List and then create a new playlist reference, in the app and DB.
     * Afterward, reset the GUI.
     */
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

        Playlist newPlaylist = new Playlist(-1, playlistName.getText(), playlistVideos);
        _DISPLAYEDPLAYLISTS.add(newPlaylist.toString());
        _PLAYLISTS.add(newPlaylist);
        newPlaylist.save();
        playlistName.setVisible(false);
        waitingForPlaylistName = false;
        videos.setVisible(false);
        currentPlaylist.setVisible(true);
        library.setStyle("-fx-text-fill: white;");
        playlists.getSelectionModel().select(newPlaylist.getID()-1);
        displayPlaylistVideos();
    }

    /**
     * Listener for any click on the "PLAY" button in the GUI.
     * There are three cases:
     * 1. A video is playing, so pass it to PlayerManager's handleInteraction
     * 2. The user is in Library, if so check if any videos are selected
     * and if so, go ahead and play the selected videos
     * 3. The user is in Playlist, if so check if any videos are selected
     * and if so, go ahead and play the selected videos. If none are, then
     * play the whole playlist.
     */
    @FXML
    private void videoInteract(ActionEvent event) {
        if(waitingForPlaylistName) return;

        if(mediaview.isVisible()){
            PlayerManager.handleInteraction();
        }else if(videos.isVisible()){
            //LIBRARY ACTIONS play all
            List<Video> selectedVideos = getSelectedVideos(videos);
            if(selectedVideos.size() == 0) return;

            videos.setVisible(false);
            currentPlaylist.setVisible(false);
            PlayerManager.play(selectedVideos);
        }else if(currentPlaylist.isVisible()){
            //PLAY PLAYLIST
            List<Video> selectedVideos = getSelectedVideos(currentPlaylist);

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

    /**
     * Changes the toString version of the Videos
     * back to their objects.
     * @param selection The ListView listing the Videos
     * @return A List of Video objects
     */
    private List<Video> getSelectedVideos(ListView<String> selection) {
        List<Video> selectedVideos = new ArrayList<>();
        for(String toString : selection.getSelectionModel().getSelectedItems()){
            for(Video video : _VIDEOS) {
                if (video.toString().equalsIgnoreCase(toString)) {
                    selectedVideos.add(video);
                    break;
                }
            }
        }

        return selectedVideos;
    }

    /**
     * A listener for when a Playlist is clicked on the GUI.
     * If clicked, stop any video and make the Playlist interface visible.
     * Then, find the Playlist object from its ID and then add all the
     * Videos into the ListView for display.
     */
    @FXML
    private void playlistInteract(MouseEvent mouseEvent) {
        displayPlaylistVideos();
    }

    private void displayPlaylistVideos()
    {
        if(waitingForPlaylistName) return;

        String playlistClicked = playlists.getSelectionModel().getSelectedItem();
        if(playlistClicked == null) return;
        PlayerManager.stopVideo();
        videos.setVisible(false);
        currentPlaylist.setVisible(true);
        library.setStyle("-fx-text-fill: #a8a8a8;");
        library.setOnMouseEntered(e -> library.setStyle("-fx-text-fill: green;"));
        library.setOnMouseExited(e -> library.setStyle("-fx-text-fill: #a8a8a8;"));


        int id = Integer.parseInt(playlistClicked.split(". ")[0]);
        Playlist playlist = null;
        for(Playlist p : _PLAYLISTS){
            if(p.getID() == id){
                playlist = p;
                break;
            }
        }

        _CURRENTPLAYLIST.clear();
        for(Video video : playlist.getVideos()){
            _CURRENTPLAYLIST.add(video.toString());
        }
    }

    /**
     * A listener for when "Library" is clicked in the GUI.
     * As the Library is set up on initialization, we only
     * have to stop any playing Video, hide the Playlist interface
     * and make the Video list interface visible.
     */
    @FXML
    private void requestLibrary(ActionEvent event) {
        if(waitingForPlaylistName) return;

        PlayerManager.stopVideo();
        currentPlaylist.setVisible(false);
        videos.setVisible(true);
        playlists.getSelectionModel().clearSelection();
        library.setStyle("-fx-text-fill: green;");
        library.setOnMouseEntered(null);
        library.setOnMouseExited(null);
    }

    /**
     * Listener for when something is typed into the search bar.
     * Upon typing, hide everything and make the Playlist view visible.
     * Then, go through all videos looking for the searched terms and add
     * to the Playlist ListView.
     */
    @FXML
    private void searched(KeyEvent event){
        if(waitingForPlaylistName) return;

        PlayerManager.stopVideo();
        videos.setVisible(false);
        currentPlaylist.setVisible(true);

        _CURRENTPLAYLIST.clear();

        String searchTerm = search.getText();
        for(Video video : _VIDEOS){
            if(video.compares(searchTerm)) _CURRENTPLAYLIST.add(video.toString());
        }
    }

    @FXML
    public void previous(ActionEvent event) {
        if(!mediaview.isVisible()) return;
        PlayerManager.previous();
    }

    @FXML
    private void skip(ActionEvent event) {
        if(!mediaview.isVisible()) return;
        PlayerManager.skip();
    }

    /**
     * The method provides the app with info from DB
     * about Playlists upon initialization.
     */
    private void setupPlaylists(){
        DB.selectSQL("SELECT fldPlaylistID FROM tblPlaylist");

        ArrayList<Integer> playlistID = new ArrayList<>();
        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }else{
                playlistID.add(Integer.valueOf(data));
            }
        } while(true);

        for (int i : playlistID)
        {
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
            _DISPLAYEDPLAYLISTS.add(newPlaylist.toString());
        }
    }

    /**
     * The method provides the app with info from DB
     * about Videos upon initialization.
     */
    private void setupVideos(){
        DB.selectSQL("SELECT fldVideoID FROM tblVideo");

        ArrayList<Integer> videoID = new ArrayList<>();
        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }else{
                videoID.add(Integer.valueOf(data));
            }
        } while(true);

        for (int i : videoID)
        {
            DB.selectSQL("SELECT fldName FROM tblVideo WHERE fldVideoID=" + i);
            String name = DB.getData();

            DB.selectSQL("SELECT fldPath FROM tblVideo WHERE fldVideoID=" + i);
            String path = DB.getData();

            DB.selectSQL("SELECT fldCategory FROM tblVideo WHERE fldVideoID=" + i);
            String category = DB.getData();

            Video newVideo = new Video(i, name, path, category);
            _VIDEOS.add(newVideo);
            _DISPLAYEDVIDEOS.add(newVideo.toString());
        }
    }

    @FXML
    private void deleteVideo()
    {
        List<Video> selectedVideos = getSelectedVideos(videos);

        for (Video video : selectedVideos)
        {
            _DISPLAYEDVIDEOS.remove(video.toString());
            video.delete();
            _VIDEOS.remove(video);
            for (Playlist playlist : _PLAYLISTS)
            {
                playlist.remove(video);
            }
        }
    }

    @FXML
    private void removeVideo()
    {
        List<Video> selectedVideos = getSelectedVideos(currentPlaylist);

        for(Playlist playlist : _PLAYLISTS)
        {
            if (playlist.toString().equalsIgnoreCase(playlists.getSelectionModel().getSelectedItem()))
            {
                for (Video video : selectedVideos)
                {
                    _CURRENTPLAYLIST.remove(video.toString());
                    playlist.remove(video);
                }
            }
        }

    }

    @FXML
    private void deletePlaylist()
    {
        for(Playlist playlist : _PLAYLISTS) {
            if (playlist.toString().equalsIgnoreCase(playlists.getSelectionModel().getSelectedItem())) {
                playlist.delete();
                _DISPLAYEDPLAYLISTS.remove(playlist.toString());
                _PLAYLISTS.remove(playlist);
                break;
            }
        }
        currentPlaylist.setVisible(false);
        videos.setVisible(true);
        playlists.getSelectionModel().clearSelection();
        library.setStyle("-fx-text-fill: green;");
    }
}
