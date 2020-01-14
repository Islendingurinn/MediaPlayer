package code;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

public class PlayerManager {

    private static MediaView mediaview; //setmediaplayer(player)
    private static MediaPlayer mediaplayer; //new with media
    private static boolean _PAUSE = false;
    private static double volume = 1;
    private static Label videoTimestamp;
    private static Label videoLength;

    private static List<Video> playlist;
    private static Video previousVideo;

    /**
     * Sets the components required for the MediaPlayer
     * @param mv The MediaView
     * @param vL The Label VideoLength
     */
    public static void setComponents(MediaView mv, Label vT, Label vL){
        mediaview = mv;
        videoTimestamp = vT;
        videoLength = vL;
    }

    /**
     * If the PLAY button is pressed while viewing
     * a video. If the video is paused, resume, else pause.
     */
    public static void handleInteraction(){
        if(_PAUSE){
            resumeVideo();
            _PAUSE = !_PAUSE;
        }else{
            pauseVideo();
            _PAUSE = !_PAUSE;
        }
    }

    /**
     * Method for resuming the Video playing
     */
    public static void resumeVideo(){
        mediaplayer.play();
    }

    /**
     * Method for pausing the Video playing
     */
    public static void pauseVideo(){
        mediaplayer.pause();
    }

    /**
     * A method to stop playing a Video.
     * If no Video is played, return, we're done.
     * Else, stop the video and reset the MediaPlayer.
     * Reset the Pause, and change the visibility to the MediaView.
     */
    public static void stopVideo(){
        if(mediaview.getMediaPlayer() == null) return;

        mediaplayer.stop();
        mediaview.setMediaPlayer(null);
        mediaview.setVisible(false);
        _PAUSE = false;
    }

    /**
     * A method to change the volume of the Video
     * @param value New volume value
     */
    public static void volumeVideo(double value)
    {
        volume = value;

        if(mediaview.getMediaPlayer() == null) return;

        mediaplayer.setVolume(volume);
    }

    /**
     * A method to play a list of Videos.
     * Load the video, initiate the MediaPlayer.
     * Set up the display and then play.
     * Then set the Player to run the method again
     * on Video end, but without the current Video.
     * @param videos List of Videos to play
     */
    public static void play(List<Video> videos){
        try {
            playlist = videos;
            Video video = videos.get(0);
            Media file = new Media(new File(video.getPath()).toURI().toString());
            mediaplayer = new MediaPlayer(file);

            mediaplayer.setOnReady(() ->
            {
                // play
                mediaview.setVisible(true);
                mediaplayer.setVolume(volume);
                mediaplayer.play();

                videoTimestamp.textProperty().bind(
                    Bindings.createStringBinding(() -> {
                        Duration time = mediaplayer.getCurrentTime();
                        return String.format("%4d:%02d:%02d", (int) time.toHours(), (int) time.toMinutes() % 60, (int)time.toSeconds() % 60);
                    },
                    mediaplayer.currentTimeProperty()));

                Duration time = mediaplayer.getStopTime();
                videoLength.setText(String.format("%4d:%02d:%02d", (int) time.toHours(), (int) time.toMinutes() % 60, (int)time.toSeconds() % 60));
            });

            mediaplayer.setOnEndOfMedia(() -> {
                videos.remove(0);
                play(videos);
            });

            mediaview.setMediaPlayer(mediaplayer);
        }catch(IndexOutOfBoundsException ex){
            stopVideo();
        }catch(NullPointerException ex){
            stopVideo();
        }
    }

    public static void previous(){
        if(previousVideo == null) stopVideo();

        playlist.add(0, previousVideo);
        play(playlist);
    }

    public static void skip(){
        if(!(previousVideo == null)) playlist.remove(0);
        play(playlist);
    }
}
