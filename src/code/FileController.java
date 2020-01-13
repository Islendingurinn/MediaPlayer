package code;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileController
{
    @FXML
    private static Stage stage;

    @FXML
    private AnchorPane main;

    public void initialize()
    {

    }
    @FXML
    public static void init(Stage newStage)
    {
        stage = newStage;
    }
    @FXML
    private void openFile()
    {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("mp4 files (*.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);
        System.out.println(file.toString());
        String path = file.toString();

        Video video = new Video(-1, "test", path, "test");
        video.save();
        Controller._VIDEOS.add(video);
    }
}
