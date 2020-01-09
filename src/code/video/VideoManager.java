package code.video;

import code.Controller;
import database.DB;
import javafx.scene.control.ListView;

import java.util.List;

public class VideoManager {

    private static ListView<String> videos;

    public static void setVideos(ListView<String> v){
        videos = v;
    }

    public static void addVideo(String name, String path, String category){

        DB.insertSQL("INSERT INTO tblVideo (fldName, fldPath, fldCategory) VALUES (" + name + ", " + path + ", " + category + ")");

    }

    public static void removeVideo(String name){
        DB.deleteSQL("DELETE FROM tblVideo WHERE fldName=" + name);
    }

    public static void removeVideo(int id){
        DB.deleteSQL("DELETE FROM tblVideo WHERE fldVideoID="+id);
    }

    public static void showVideos(){
        // List<Video> videos = Controller._VIDEOS;
        videos.setVisible(true);
    }

    public static void hideVideos(){
        videos.setVisible(false);
    }

    //TODO: WHEN CLICKING VIDEOS IN LIBRARY
    //TODO: Not much really happens except:
    //TODO: PLAY BUTTON --> PLAY SELECTION
    //TODO: CREATE PLAYLIST --> CREATES PLAYLIST WITH SELECTION
    public static void handleInteraction(ListView<String> videos) {
        videos.getSelectionModel().getSelectedItems();
    }
}
