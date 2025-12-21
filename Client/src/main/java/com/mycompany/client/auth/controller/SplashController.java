package com.mycompany.client.auth.controller;

import com.mycompany.client.core.navigation.NavigationService;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;

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
            Parent root = NavigationService.loadFXML(fxmlName);
            NavigationService.navigateTo(root);
        } catch (IOException ex) {
            System.err.println("Error loading screen: " + fxmlName);
            ex.printStackTrace();
        }
    }
}
