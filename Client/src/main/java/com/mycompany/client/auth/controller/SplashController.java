package com.mycompany.client.auth.controller;

import com.mycompany.client.App;
import com.mycompany.client.auth.AuthClient;
import com.mycompany.client.auth.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SplashController implements Initializable {

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);

              

                Thread.sleep(500);
                    Platform.runLater(() -> navigateToScreen("main-menu"));
               
            } catch (InterruptedException e) {
                System.err.println("Splash screen interrupted: " + e.getMessage());
                Platform.runLater(() -> navigateToScreen("main-menu"));
            }
        }).start();
    }

    private void updateStatus(String message) {
        Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(message);
            }
            System.out.println("Splash...");
        });
    }

    private void navigateToScreen(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlName + ".fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            Scene scene = new Scene(root);

            scene.getStylesheets().addAll(
                    App.class.getResource("/styles/customStyles.css").toExternalForm(),
                    App.class.getResource("/css/profilescreen.css").toExternalForm(),
                    App.class.getResource("/css/style.css").toExternalForm(),
                    App.class.getResource("/css/table.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.err.println("Error loading screen: " + fxmlName);
            ex.printStackTrace();
        }
    }
}
