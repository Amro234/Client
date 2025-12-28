package com.mycompany.client;

import com.mycompany.client.core.navigation.NavigationService;
import com.mycompany.client.settings.manager.BackgroundMusicManager;
import com.mycompany.client.settings.manager.SoundEffectsManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // BackgroundMusicManager.init();
        SoundEffectsManager.init();

        // Load splash screen and create scene
        Scene scene = new Scene(NavigationService.loadFXML("splash"), 1280, 720);

        // Initialize NavigationService
        NavigationService.init(scene);

        scene.getStylesheets().addAll(
                getClass().getResource("/styles/customStyles.css").toExternalForm(),
                App.class.getResource("/styles/profilescreen.css").toExternalForm(),
                App.class.getResource("/styles/style.css").toExternalForm(),
                App.class.getResource("/styles/table.css").toExternalForm());

        stage.getIcons()
                .add(new javafx.scene.image.Image(getClass().getResourceAsStream("/assets/images/Main-logo.png")));
        stage.setTitle("Tic Tac Toe Pro");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(NavigationService.loadFXML(fxml));
    }

    public static void main(String[] args) {
        launch();
    }
}
