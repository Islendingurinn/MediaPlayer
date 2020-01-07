package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private MediaView mv;
    private MediaPlayer mp;
    private Media me;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String path = new File("src/media/vid.mp4").getAbsolutePath();//path is just an example
        me = new Media(new File(path).toURI().toString());
        mp = new MediaPlayer(me);//instance of media player
        mv.setMediaPlayer(mp);//calling this method using mv
        mp.setAutoPlay(true);//this play video with boolean argument
        //ratio of video size
        DoubleProperty width = mv.fitWidthProperty();
        DoubleProperty height = mv.fitHeightProperty();
        //calling binding method which helps to preserve ratio of video
        width.bind(Bindings.selectDouble(mv.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mv.sceneProperty(), "height"));

    }

    //method for other functionality like play, pause and stop
    public void play(ActionEvent event) {
        mp.play();
    }
        public void pause(ActionEvent event){
            mp.pause();
        }
            public void stop(ActionEvent event){
                mp.stop();
            }

        }

