package code;

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
    private static ListView<String> library;
    //private Media media; // The video file new Media w/ path
    private static boolean _PAUSE = false;
    private static double volume = 1;
    private static Label videoLength;

    public static void setComponents(MediaView mv, ListView<String> videos, Label vL){
        mediaview = mv;
        library = videos;
        videoLength = vL;
    }

    /*public static Media load(){
        String absolutepath = new File("src/display/videos/video_example.mp4").getAbsolutePath();
        return new Media(new File(absolutepath).toURI().toString());
    }

    public static void loadVideo(String name){
        database.DB.selectSQL("SELECT fldVideoID FROM tblVideo WHERE fldName=" + name);
        String resultset = database.DB.getData();




        if(resultset.equals(database.DB.NOMOREDATA)); //TODO: ERROR
        loadVideo(Integer.parseInt(resultset));
    }

    public static Media loadVideo(int id){
        database.DB.selectSQL("SELECT fldPath FROM tblVideo where fldVideoID=" + id);
        String resultset = database.DB.getData();

        if(resultset.equals(database.DB.NOMOREDATA)); //TODO: ERROR

        return new Media(new File(resultset).toURI().toString());
    }

    public static void set(StackPane center){
        mediaview.setVisible(true);
        library.setVisible(false);
        mediaplayer = new MediaPlayer(load());
        mediaview.setMediaPlayer(mediaplayer);

        mediaplayer.setAutoPlay(true);

        mediaview.fitWidthProperty().bind(center.widthProperty().subtract(50));
        mediaview.fitHeightProperty().bind(center.heightProperty().subtract(50));
    }

    public static void setVideo(int id){
        mediaplayer = new MediaPlayer(loadVideo(id));
        mediaview.setMediaPlayer(mediaplayer);

        mediaplayer.setAutoPlay(true);

        DoubleProperty width = mediaview.fitWidthProperty();
        DoubleProperty height = mediaview.fitHeightProperty();

        width.bind(Bindings.selectDouble(mediaview.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaview.sceneProperty(), "height"));
    }*/

    public static void handleInteraction(){
        if(mediaview.getMediaPlayer() == null){
            //TODO: Play selected video / playlist
        }else if(_PAUSE){
            resumeVideo();
            _PAUSE = !_PAUSE;
        }else{
            pauseVideo();
            _PAUSE = !_PAUSE;
        }


    }

    public static void resumeVideo(){
        mediaplayer.play();
    }

    public static void pauseVideo(){
        mediaplayer.pause();
    }

    public static void stopVideo(){
        if(mediaview.getMediaPlayer() == null) return;

        mediaplayer.stop();
        mediaview.setMediaPlayer(null);
        _PAUSE = false;
        library.setVisible(true);
        mediaview.setVisible(false);
    }

    public static void volumeVideo(double value)
    {
        volume = value;

        if(mediaview.getMediaPlayer() == null) return;

        mediaplayer.setVolume(volume);
    }

    public static void play(List<Video> videos){
        //if(videos.size() == 0) stopVideo();
        try {
            Video video = videos.get(0);
            Media file = new Media(new File(video.getPath()).toURI().toString());
            mediaplayer = new MediaPlayer(file);

            Duration minutes = mediaplayer.getTotalDuration();
            System.out.println(Double.valueOf(minutes.toMinutes()));

            System.out.println("Playing: " + video.toString());

            mediaplayer.setOnReady(() ->
            {
                videoLength.setText(mediaplayer.getCurrentTime().toString());
                System.out.println("Duration: "+file.getDuration().toMinutes());

                // play
                mediaview.setVisible(true);
                mediaplayer.setVolume(volume);
                mediaplayer.play();
            });

            mediaplayer.setOnEndOfMedia(() -> {
                videos.remove(0);
                play(videos);
            });

            mediaview.setMediaPlayer(mediaplayer);
        }catch(IndexOutOfBoundsException ex){
            stopVideo();
        }
    }
}
