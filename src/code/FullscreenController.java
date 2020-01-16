package code;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FullscreenController
{
    private static Stage stage;
    private static MediaView mediaView;
    private static StackPane center;
    private static StackPane fullscreen;

    public static void init(MediaView m, StackPane c)
    {
        try
        {
            mediaView = m;
            center = c;
            fullscreen = FXMLLoader.load(FullscreenController.class.getResource("/display/FullscreenView.fxml"));
            Rectangle2D r = Screen.getPrimary().getBounds();
            Scene scene = new Scene(fullscreen, r.getWidth(), r.getHeight());
            stage = new Stage();
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setOnHiding( event ->
            {
                onClose();
            } );
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void show()
    {
        fullscreen.getChildren().add(mediaView);
        mediaView.toBack();
        mediaView.setFitHeight(fullscreen.getHeight());
        mediaView.setFitWidth(fullscreen.getWidth());
        stage.show();
    }

    @FXML
    private void closeFullscreen()
    {
        stage.close();
    }

    public static void onClose()
    {
        mediaView.fitWidthProperty().bind(center.widthProperty().subtract(100));
        mediaView.fitHeightProperty().bind(center.heightProperty().subtract(100));
        center.getChildren().add(mediaView);
        Controller.stage.show();
    }
}
