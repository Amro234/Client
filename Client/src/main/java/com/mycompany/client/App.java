package com.mycompany.client;

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
        // Load the Inter and Roboto fonts from your fonts folder
       Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-VariableFont_opsz,wght.ttf"), 14);
       //Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-Italic-VariableFont_opsz,wght.ttf"), 14);
       //Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-VariableFont_wdth,wght.ttf"), 14);
       //Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Italic-VariableFont_wdth,wght.ttf"), 14);

        Parent root = loadFXML("main-menu");

        scene = new Scene(root, 1280, 720);

        scene.getStylesheets().add(
                getClass().getResource("/styles/customStyles.css").toExternalForm()
        );

        
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
