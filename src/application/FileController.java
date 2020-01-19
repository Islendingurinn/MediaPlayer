package application;

import domain.Video;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        if (file == null)
        {
            fileInput.clear();
        }
        else
        {
            fileInput.setText(file.toString());
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
        String source = file.toString();
        String dest = System.getProperty("user.dir") + "\\src\\presentation\\media" + source.substring(source.lastIndexOf("\\"));
        String filetype = source.substring(source.lastIndexOf(".") + 1, source.length());

        String fileInputPath = fileInput.getText();
        for (Video video : Controller._VIDEOS)
        {
            String videoPath = video.getPath();
            if (fileInputPath.substring(fileInputPath.lastIndexOf("\\")).equalsIgnoreCase(videoPath.substring(video.getPath().lastIndexOf("\\"))))
            {
                fileInput.setText("");
            }
            if (name.getText().equalsIgnoreCase(video.get_NAME()))
            {
                name.setText("");
            }
        }

        if (file.isFile() && filetype.equalsIgnoreCase("mp4") && !name.getText().isEmpty() && !category.getText().isEmpty() && !fileInput.getText().isEmpty())
        {
            try
            {
                Files.copy(Paths.get(source), Paths.get(dest));
                Video video = new Video(-1, name.getText(), dest, category.getText());
                video.save();
                Controller._DISPLAYEDVIDEOS.add(video.toString());
                Controller._VIDEOS.add(video);
                stage.hide();
            }
            catch (IOException ex)
            {
                System.err.format("I/O Error when copying file");
            }
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
