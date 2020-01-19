package application;

import database.DB;
import de.jensd.fx.glyphs.GlyphsStack;
import domain.PlayerManager;
import domain.Playlist;
import domain.Video;
import domain.View;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller
{
    public static Stage stage;
    public static ObservableList<String> _DISPLAYEDPLAYLISTS;
    public static ObservableList<String> _DISPLAYEDVIDEOS;
    public static ObservableList<String> _CURRENTPLAYLIST;
    public static List<Playlist> _PLAYLISTS;
    public static List<Video> _VIDEOS;
    public PlayerManager playerManager;
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
    private Slider time;

    @FXML
    private Button library;

    @FXML
    private ListView<String> miniPlaylist;

    @FXML
    private ContextMenu libraryMenu;

    @FXML
    private GlyphsStack stack;

    /**
     * Method ran upon opening the program.
     */
    public void initialize()
    {
        // Load fonts
        Font.loadFont(Controller.class.getResourceAsStream("/presentation/fonts/OpenSans-Regular.ttf"), 16);
        Font.loadFont(Controller.class.getResourceAsStream("/presentation/fonts/OpenSans-Bold.ttf"), 16);

        FullscreenController.init(mediaview, center);

        //Sets up the ListViews that display Videos and Playlists
        _DISPLAYEDPLAYLISTS = FXCollections.observableArrayList();
        playlists.setItems(_DISPLAYEDPLAYLISTS);
        miniPlaylist.setItems(_DISPLAYEDPLAYLISTS);

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

        // Sets up the volume property listener
        volume.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            playerManager.volumeVideo(newValue.doubleValue()/100);
            if (newValue.doubleValue() == 0)
            {
                stack.getChildren().get(0).setOpacity(0);
                stack.getChildren().get(1).setOpacity(1);
            }
            else if (oldValue.doubleValue() == 0 && newValue.doubleValue() > 0)
            {
                stack.getChildren().get(0).setOpacity(1);
                stack.getChildren().get(1).setOpacity(0);
            }
        });

        // Bind mediaview fit property to center property
        mediaview.fitWidthProperty().bind(center.widthProperty().subtract(100));
        mediaview.fitHeightProperty().bind(center.heightProperty().subtract(100));

        // Constructs the components for PlayerManager
        playerManager = new PlayerManager(mediaview, time, videoTimestamp, videoLength);
    }

    /**
     * Set which window should be visible;
     * whether that's video, library or playlist.
     * @param view
     */
    public void setVisible(View view)
    {
        mediaview.setVisible(false);
        videos.setVisible(false);
        currentPlaylist.setVisible(false);

        switch (view)
        {
            case LIBRARY:
                videos.setVisible(true);
                break;
            case PLAYLIST:
                currentPlaylist.setVisible(true);
                break;
            case VIDEO:
                mediaview.setVisible(true);
                break;
        }
    }

    @FXML
    private void volumeToggle()
    {
        if (volume.getValue() == 0)
        {
            volume.setValue(100);
            stack.getChildren().get(0).setOpacity(1);
            stack.getChildren().get(1).setOpacity(0);
        }
        else
        {
            volume.setValue(0);
            stack.getChildren().get(0).setOpacity(0);
            stack.getChildren().get(1).setOpacity(1);
        }
    }

    /**
     * A method to handle showing the file manager window that
     * opens when the user wants to add videos.
     */
    @FXML
    private void handleButtonFile()
    {
        try
        {
            AnchorPane file = FXMLLoader.load(FileController.class.getResource("/presentation/FileView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add file");
            Scene scene = new Scene(file);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initOwner(Controller.stage);
            stage.show();
            FileController.init(stage);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * A method to set the stage
     * @param stage stage to set
     */
    public void setStage(Stage stage)
    {
        Controller.stage = stage;
    }

    /**
     * A method to handle wanting to put the
     * video into fullscreen.
     */
    @FXML
    private void handleButtonFullscreen()
    {
        stage.hide();
        mediaview.fitHeightProperty().unbind();
        mediaview.fitWidthProperty().unbind();
        FullscreenController.show();
    }

    /**
     * Listener for when "Create Playlist" is clicked.
     * Upon click, verify that videos are selected.
     * If they are, enable visibility for the Name text field
     * so the user can pick out a name for the playlist.
     */
    @FXML
    private void createPlaylist(ActionEvent event)
    {
        if (mediaview.getMediaPlayer() != null) return;
        if (waitingForPlaylistName) return;

        selectedVideos = videos.getSelectionModel().getSelectedItems();
        if (selectedVideos.size() > 0)
        {
            waitingForPlaylistName = true;
            playlistName.setVisible(true);
            playlistName.requestFocus();
        }
        else selectedVideos = null;
    }

    /**
     * Listens for when a name has been entered while creating
     * a playlist. Upon confirmation, add all the selected videos to
     * a List and then create a new playlist reference, in the app and DB.
     * Afterward, reset the GUI.
     */
    @FXML
    private void onPlaylistName(ActionEvent event)
    {
        List<Video> playlistVideos = new ArrayList<>();
        for (String toString : selectedVideos)
        {
            for (Video video : _VIDEOS)
            {
                if (video.toString().equalsIgnoreCase(toString))
                {
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
        library.setStyle("-fx-text-fill: white;");
        playlists.getSelectionModel().select(newPlaylist.getID()-1);
        setVisible(View.PLAYLIST);
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
    private void videoInteract(ActionEvent event)
    {
        if (waitingForPlaylistName) return;

        if (mediaview.isVisible())
        {
            playerManager.handleInteraction();
        }
        else if (videos.isVisible())
        {
            List<Video> selectedVideos = getSelectedVideos(videos);
            if (selectedVideos.size() == 0)
            {
                selectedVideos = _VIDEOS;
            }
            setVisible(View.VIDEO);
            playerManager.play(selectedVideos, videos);
        }
        else if (currentPlaylist.isVisible())
        {
            List<Video> selectedVideos = getSelectedVideos(currentPlaylist);
            if (selectedVideos.size() == 0)
            {
                String playlist = playlists.getSelectionModel().getSelectedItem();
                selectedVideos = getSelectedPlaylist(playlist).getVideos();
            }
            setVisible(View.VIDEO);
            playerManager.play(selectedVideos, currentPlaylist);
        }
    }

    /**
     * Method to handle clicking "Stop" button
     */
    @FXML
    private void stopVideo(ActionEvent event)
    {
        if (mediaview.isVisible())
        {
            playerManager.stopVideo();
        }
    }

    /**
     * A method to get the selected playlist from the
     * display name
     * @param toString The display name
     * @return the Playlist object
     */
    private Playlist getSelectedPlaylist(String toString)
    {
        for (Playlist pl : _PLAYLISTS)
        {
            if (pl.toString().equalsIgnoreCase(toString))
                return pl;
        }

        return null;
    }

    /**
     * Changes the toString version of the Videos
     * back to their objects.
     * @param selection The ListView listing the Videos
     * @return A List of Video objects
     */
    private List<Video> getSelectedVideos(ListView<String> selection)
    {
        List<Video> selectedVideos = new ArrayList<>();
        for (String toString : selection.getSelectionModel().getSelectedItems())
        {
            for (Video video : _VIDEOS)
            {
                if (video.toString().equalsIgnoreCase(toString))
                {
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
    private void playlistInteract(MouseEvent mouseEvent)
    {
        displayPlaylistVideos();
    }

    /**
     * A method for adding videos to a playlist when
     * the ListView is clicked. The method first
     * gets the selected videos and playlist, then
     * adds it before changing the visibility to
     * the playlist.
     */
    @FXML
    private void addToPlaylist(MouseEvent mouseEvent)
    {
        List<Video> selectedVideos = getSelectedVideos(videos);

        if (selectedVideos.size() > 0)
        {
            Playlist playlist = getSelectedPlaylist(miniPlaylist.getSelectionModel().getSelectedItem());

            for (Video video : selectedVideos)
            {
                playlist.add(video);
            }

            setVisible(View.PLAYLIST);
            playlists.getSelectionModel().select(miniPlaylist.getSelectionModel().getSelectedIndex());
            libraryMenu.hide();
            library.setStyle("-fx-text-fill: #a8a8a8;");

            _CURRENTPLAYLIST.clear();
            for (Video video : playlist.getVideos())
            {
                _CURRENTPLAYLIST.add(video.toString());
            }
        }
    }

    /**
     * A method to display the videos in a playlist
     * when a playlist is clicked in the ListView.
     */
    private void displayPlaylistVideos()
    {
        if (waitingForPlaylistName) return;

        int id = playlists.getSelectionModel().getSelectedIndex();
        if (id < 0) return;

        Playlist playlistClicked = _PLAYLISTS.get(id);
        playerManager.stopVideo();
        setVisible(View.PLAYLIST);

        library.setStyle("-fx-text-fill: #a8a8a8;");
        library.setOnMouseEntered(e -> library.setStyle("-fx-text-fill: green;"));
        library.setOnMouseExited(e -> library.setStyle("-fx-text-fill: #a8a8a8;"));

        Playlist playlist = null;
        for (Playlist p : _PLAYLISTS)
        {
            if (p.getID() == playlistClicked.getID())
            {
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
    private void requestLibrary(ActionEvent event)
    {
        if (waitingForPlaylistName) return;

        playerManager.stopVideo();
        setVisible(View.LIBRARY);
        videos.getSelectionModel().clearSelection();
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
        if (waitingForPlaylistName) return;

        playerManager.stopVideo();
        setVisible(View.PLAYLIST);

        _CURRENTPLAYLIST.clear();
        String searchTerm = search.getText();
        for (Video video : _VIDEOS)
        {
            if (video.compares(searchTerm)) _CURRENTPLAYLIST.add(video.toString());
        }
    }

    /**
     * Method to handle when "skip to previous"
     * button is pressed.
     */
    @FXML
    public void previous(ActionEvent event)
    {
        if (!mediaview.isVisible()) return;
        playerManager.previous();
    }

    /**
     * Method to handle when "skip to next"
     * button is pressed.
     */
    @FXML
    private void skip(ActionEvent event)
    {
        if (!mediaview.isVisible()) return;
        playerManager.skip();
    }

    /**
     * The method provides the app with info from DB
     * about Playlists upon initialization.
     */
    private void setupPlaylists()
    {
        DB.selectSQL("SELECT fldPlaylistID FROM tblPlaylist");

        ArrayList<Integer> playlistID = new ArrayList<>();
        do
        {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA))
            {
                break;
            }
            else
            {
                playlistID.add(Integer.valueOf(data));
            }
        } while (true);

        for (int i : playlistID)
        {
            DB.selectSQL("SELECT fldName FROM tblPlaylist WHERE fldPlaylistID=" + i);
            String name = DB.getData();

            List<Video> playlistVideos = new ArrayList<>();
            DB.selectSQL("SELECT fldVideoID FROM tblMapping WHERE fldPlaylistID=" + i);
            do
            {
                String mappingset = DB.getData();
                if (mappingset.equals(DB.NOMOREDATA)) break;

                int videoID = Integer.parseInt(mappingset);
                for (Video video : _VIDEOS)
                {
                    if (video.getID() == videoID) playlistVideos.add(video);
                }
            }
            while (true);

            Playlist newPlaylist = new Playlist(i, name, playlistVideos);
            _PLAYLISTS.add(newPlaylist);
            _DISPLAYEDPLAYLISTS.add(newPlaylist.toString());
        }
    }

    /**
     * The method provides the app with info from DB
     * about Videos upon initialization.
     */
    private void setupVideos()
    {
        DB.selectSQL("SELECT fldVideoID FROM tblVideo");

        ArrayList<Integer> videoID = new ArrayList<>();
        do
        {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA))
            {
                break;
            }
            else
            {
                videoID.add(Integer.valueOf(data));
            }
        } while (true);

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

    /**
     * A method to handle clicking delete for a
     * video. Removes the reference in the code & DB.
     */
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

    /**
     * A method to handle clicking remove from playlist
     * for a video. Removes the reference in the code & DB.
     */
    @FXML
    private void removeVideo()
    {
        List<Video> selectedVideos = getSelectedVideos(currentPlaylist);

        for (Playlist playlist : _PLAYLISTS)
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

    /**
     * A method to handle clicking delete for a
     * playlist. Removes the reference in the code & DB.
     */
    @FXML
    private void deletePlaylist()
    {
        for (Playlist playlist : _PLAYLISTS)
        {
            if (playlist.toString().equalsIgnoreCase(playlists.getSelectionModel().getSelectedItem()))
            {
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
