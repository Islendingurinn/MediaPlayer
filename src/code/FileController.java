package code;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class FileController
{
    @FXML
    private static Stage stage;

    @FXML
    private TextField name;

    @FXML
    private TextField category;

    @FXML
    private TextField fileInput;

    String path = "";

    @FXML
    public static void init(Stage newStage)
    {
        stage = newStage;
    }
    @FXML
    private void openFIle()
    {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("mp4 files (*.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);
        path = file.toString();
        fileInput.setText(path);
    }
    @FXML
    private void save()
    {
        Video video = new Video(-1, name.getText(), path, category.getText());
        video.save();
        Controller._DISPLAYEDVIDEOS.add(video.toString());
        Controller._VIDEOS.add(video);
        stage.hide();
    }
}
