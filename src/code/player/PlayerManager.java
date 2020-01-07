package code.player;

import database.DB;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;

public class PlayerManager {

    private static MediaView mediaview; //setmediaplayer(player)
    private static MediaPlayer mediaplayer; //new with media
    //private Media media; // The video file new Media w/ path

    public static void setMediaview(MediaView mv){
        mediaview = mv;
    }

    public static Media load(){
        String absolutepath = new File("src/display/videos/video_example.mp4").getAbsolutePath();
        return new Media(new File(absolutepath).toURI().toString());
    }

    public static void loadVideo(String name){
        DB.selectSQL("SELECT fldVideoID FROM tblVideo WHERE fldName=" + name);
        String resultset = DB.getData();

        if(resultset.equals(DB.NOMOREDATA)); //TODO: ERROR
        loadVideo(Integer.parseInt(resultset));
    }

    public static Media loadVideo(int id){
        DB.selectSQL("SELECT fldPath FROM tblVideo where fldVideoID=" + id);
        String resultset = DB.getData();

        if(resultset.equals(DB.NOMOREDATA)); //TODO: ERROR

        return new Media(new File(resultset).toURI().toString());
    }

    public static void set(){
        mediaplayer = new MediaPlayer(load());
        mediaview.setMediaPlayer(mediaplayer);

        mediaplayer.setAutoPlay(true);

        DoubleProperty width = mediaview.fitWidthProperty();
        DoubleProperty height = mediaview.fitHeightProperty();

        width.bind(Bindings.selectDouble(mediaview.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaview.sceneProperty(), "height"));
    }

    public static void setVideo(int id){
        mediaplayer = new MediaPlayer(loadVideo(id));
        mediaview.setMediaPlayer(mediaplayer);

        mediaplayer.setAutoPlay(true);

        DoubleProperty width = mediaview.fitWidthProperty();
        DoubleProperty height = mediaview.fitHeightProperty();

        width.bind(Bindings.selectDouble(mediaview.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaview.sceneProperty(), "height"));
    }

    public static void playVideo(){
        mediaplayer.play();
    }

    public static void pauseVideo(){
        mediaplayer.pause();
    }

    public static void stopVideo(){
        mediaplayer.stop();
        mediaview.setMediaPlayer(null);
    }
}
