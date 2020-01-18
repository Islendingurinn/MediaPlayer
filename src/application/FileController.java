package application;

import domain.Video;
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

    /**
     * Method run on initialization
     * @param newStage
     */
    public static void init(Stage newStage)
    {
        stage = newStage;
    }

    /**
     * Method to open the file browser and look for
     * mp4 files. Then, save the path.
     */
    @FXML
    private void openFile()
    {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("mp4 files (*.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);
        if(file == null)
        {
            fileInput.clear();
        }
        else
        {
            path = file.toString();
            fileInput.setText(path);
        }
    }

    /**
     * Use the path to save and implement the video
     * into the code and into the DB.
     */
    @FXML
    private void save()
    {
        File file = new File(fileInput.getText());
        String filetype = file.toString();
        filetype = filetype.substring(filetype.lastIndexOf(".") + 1, filetype.length());

        if(file.isFile() && filetype.equalsIgnoreCase("mp4") && !name.getText().isEmpty() && !category.getText().isEmpty())
        {
            Video video = new Video(-1, name.getText(), path, category.getText());
            video.save();
            Controller._DISPLAYEDVIDEOS.add(video.toString());
            Controller._VIDEOS.add(video);
            stage.hide();
        }
        else
        {
            fileInput.clear();
        }
    }

    /**
     * Method run on close request
     */
    @FXML
    private void close()
    {
        stage.hide();
    }
}
