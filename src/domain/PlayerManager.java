package domain;

import de.jensd.fx.glyphs.GlyphsStack;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayerManager
{
    private ListView listView;
    private MediaView mediaview;
    private static MediaPlayer mediaplayer;
    private Label videoTimestamp;
    private Label videoLength;
    private List<Video> playlist;
    private GlyphsStack playStack;
    private Duration duration;
    private Slider time;
    private int playingVideo = 0;
    private boolean _PAUSE = false;
    private double volume = 1;

    /**
     * Sets the components required for the MediaPlayer
     * @param mediaView The MediaView
     * @param vL The Label VideoLength
     */
    public PlayerManager(MediaView mediaView, Slider slider, Label vT, Label vL, GlyphsStack playStack)
    {
        this.playStack = playStack;
        this.mediaview = mediaView;
        this.time = slider;
        this.videoTimestamp = vT;
        this.videoLength = vL;
    }

    /**
     * If the PLAY button is pressed while viewing
     * a video. If the video is paused, resume, else pause.
     */
    public void handleInteraction()
    {
        if (_PAUSE)
        {
            playStack.getChildren().get(0).setVisible(false);
            playStack.getChildren().get(1).setVisible(true);
            resumeVideo();
            _PAUSE = !_PAUSE;
        }
        else
        {
            playStack.getChildren().get(0).setVisible(true);
            playStack.getChildren().get(1).setVisible(false);
            pauseVideo();
            _PAUSE = !_PAUSE;
        }
    }

    /**
     * Method for resuming the Video playing
     */
    public void resumeVideo()
    {
        mediaplayer.play();
    }

    /**
     * Method for pausing the Video playing
     */
    public void pauseVideo()
    {
        mediaplayer.pause();
    }

    /**
     * A method to stop playing a Video.
     * If no Video is played, return, we're done.
     * Else, stop the video and reset the MediaPlayer.
     * Reset the Pause, and change the visibility to the MediaView.
     */
    public void stopVideo()
    {
        if (mediaview.getMediaPlayer() == null) return;
        mediaplayer.stop();
        mediaplayer.dispose();
        mediaplayer = null;
        mediaview.setMediaPlayer(null);
        mediaview.setVisible(false);
        _PAUSE = false;
        videoTimestamp.textProperty().unbind();
        videoLength.textProperty().unbind();
        videoTimestamp.setText("");
        videoLength.setText("");
        listView.setVisible(true);
        playingVideo = 0;
        playStack.getChildren().get(0).setVisible(true);
        playStack.getChildren().get(1).setVisible(false);
    }

    /**
     * A method to change the volume of the Video
     * @param value New volume value
     */
    public void volumeVideo(double value)
    {
        volume = value;

        if(mediaview.getMediaPlayer() == null) return;

        mediaplayer.setVolume(volume);
    }

    //
    private void updateValues()
    {
        duration = mediaplayer.getMedia().getDuration();
        if (mediaplayer.getMedia().getDuration() != null && time != null && duration != null)
        {
            Platform.runLater(() ->
            {
                Duration currentTime = mediaplayer.getCurrentTime();
                time.setDisable(duration.isUnknown());
                if (!time.isDisabled() && duration.greaterThan(Duration.ZERO) && !time.isValueChanging())
                {
                    time.setValue(currentTime.divide(duration).toMillis() * 100.0);
                }
            });
        }
    }

    /**
     * A method to play a list of Videos.
     * Load the video, initiate the MediaPlayer.
     * Set up the display and then play.
     * Then set the Player to run the method again
     * on Video end, but without the current Video.
     * @param videos List of Videos to play
     */
    public void play(List<Video> videos, ListView listView){
        try
        {
            this.listView = listView;
            playlist = new ArrayList<>(videos);
            Video video = videos.get(playingVideo);
            Media file = new Media(new File(video.getPath()).toURI().toString());
            mediaplayer = new MediaPlayer(file);

            mediaplayer.setOnReady(() ->
            {
                // play video
                mediaplayer.setVolume(volume);
                mediaplayer.play();

                // Bind timestamp label to mediaplayer current time
                videoTimestamp.textProperty().bind(Bindings.createStringBinding(() ->
                {
                    Duration time = mediaplayer.getCurrentTime();
                    return String.format("%4d:%02d:%02d", (int) time.toHours(), (int) time.toMinutes() % 60, (int) time.toSeconds() % 60);
                },
                mediaplayer.currentTimeProperty()));

                // Set length label to mediaplayer stop time
                Duration time = mediaplayer.getStopTime();
                videoLength.setText(String.format("%4d:%02d:%02d", (int) time.toHours(), (int) time.toMinutes() % 60, (int) time.toSeconds() % 60));
            });

            // Slider value listener
            time.valueProperty().addListener(ov ->
            {
                if (time.isValueChanging())
                {
                    // multiply duration by percentage calculated by slider position
                    if (duration!=null)
                    {
                        mediaplayer.seek(duration.multiply(time.getValue() / 100.0));
                    }
                    updateValues();
                }
            });

            // Mediaplayer time listener
            mediaplayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> updateValues());

            mediaplayer.setOnEndOfMedia(() -> {
                playingVideo++;
                play(videos, listView);
            });

            mediaview.setMediaPlayer(mediaplayer);
        }
        catch(IndexOutOfBoundsException ex)
        {
            playingVideo = 0;
            stopVideo();
        }
        catch(NullPointerException ex)
        {
            playingVideo = 0;
            stopVideo();
        }
    }

    /**
     * A method to handle playing the
     * previous video. If there's none,
     * stop the video player.
     */
    public void previous()
    {
        playingVideo--;
        play(playlist, listView);
    }

    /**
     * A method to handle playing the
     * next video. If there's a past video
     * then remove it from the playlist.
     */
    public void skip()
    {
        playingVideo++;
        play(playlist, listView);
    }
}