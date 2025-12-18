package com.mycompany.client;

import com.mycompany.client.backgroundAudio.BackgroundMusicManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        BackgroundMusicManager.init();

        scene = new Scene(loadFXML("gameLobby"), 1280, 720);

        scene.getStylesheets().addAll(
                getClass().getResource("/styles/customStyles.css").toExternalForm(),
               
                App.class.getResource("/css/profilescreen.css").toExternalForm(),
                App.class.getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/mycompany/client/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
