package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Main scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/presentation/MainView.fxml"));
        BorderPane root = (BorderPane) fxmlLoader.load();
        Scene main = new Scene(root);
        primaryStage.setMinWidth(1066);
        primaryStage.setMinHeight(739);
        // primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("hospital.png")));
        primaryStage.setScene(main);
        primaryStage.show();
        ((Controller) fxmlLoader.getController()).setStage(primaryStage);


    }

    public static void main(String[] args) {
        launch(args);
    }
}
