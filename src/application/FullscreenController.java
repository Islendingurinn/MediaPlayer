package application;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class FullscreenController
{
    private static Stage stage;
    private static Scene scene;
    private static MediaView mediaView;
    private static StackPane center;
    private static StackPane fullscreen;

    /**
     * The method to initialize the fullscreen mode
     * sets the scene and actions.
     * @param m The video MediaView
     * @param c The StackPane containing the MediaView
     */
    public static void init(MediaView m, StackPane c)
    {
        mediaView = m;
        center = c;
        try
        {
            fullscreen = FXMLLoader.load(FullscreenController.class.getResource("/presentation/FullscreenView.fxml"));
            Rectangle2D r = Screen.getPrimary().getBounds();
            scene = new Scene(fullscreen, r.getWidth(), r.getHeight());
            stage = new Stage();
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setOnHiding( event ->
            {
                onClose();
            } );

            /**
             * Fullscreen show top and mouse when mouse is moved
             */
            BorderPane top = (BorderPane) scene.lookup("#top");
            PauseTransition idle = new PauseTransition(Duration.seconds(2));
            idle.setOnFinished(e -> {
                scene.setCursor(Cursor.NONE);
                top.setVisible(false);
            });

            scene.setOnMouseMoved(event ->
            {
                idle.playFromStart();
                scene.setCursor(Cursor.DEFAULT);
                top.setVisible(true);
            });
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Show the MediaView in the fullscreen window
     */
    public static void show()
    {
        fullscreen.getChildren().add(mediaView);
        mediaView.toBack();
        mediaView.setFitHeight(fullscreen.getHeight());
        mediaView.setFitWidth(fullscreen.getWidth());
        stage.show();
    }

    /**
     * On Action close the fullscreen by request
     */
    @FXML
    private void closeFullscreen()
    {
        stage.close();
    }

    /**
     * A method to reset the window when
     * closing the fullscreen is requested
     */
    public static void onClose()
    {
        mediaView.fitWidthProperty().bind(center.widthProperty().subtract(100));
        mediaView.fitHeightProperty().bind(center.heightProperty().subtract(100));
        center.getChildren().add(mediaView);
        Controller.stage.show();
    }
}
