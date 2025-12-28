package com.mycompany.client.mainmenu;

import com.mycompany.client.core.CustomAlertDialog;
import com.mycompany.client.core.navigation.NavigationService;
import com.mycompany.client.core.server.ServerConnection;
import com.mycompany.client.settings.manager.SoundEffectsManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class ServerConnectionDialog {

    public static void show(Window owner) {
        // Create a dialog stage
        Stage dialog = new Stage();
        CustomAlertDialog.registerStage(dialog);
        dialog.setTitle("Connect to Server");
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(javafx.stage.StageStyle.UTILITY);

        // Create UI components
        Label serverIpLabel = new Label("Server IP:");
        serverIpLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");

        TextField serverIpField = new TextField();
        serverIpField.setPromptText("Enter server IP address");
        serverIpField.setPrefWidth(250);
        serverIpField.setStyle(
                "-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5;");
        serverIpField.setText(ServerConnection.getServerHost()); // Default to current server

        Button connectButton = new Button("Connect");
        connectButton.setPrefWidth(120);

        String buttonStyle = "-fx-background-color: #3498DB;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;";

        connectButton.setStyle(buttonStyle);

        connectButton.setOnMouseEntered(e -> connectButton.setStyle(
                buttonStyle.replace("#3498DB", "#2980B9")));
        connectButton.setOnMouseExited(e -> connectButton.setStyle(buttonStyle));

        // Connect button action
        connectButton.setOnAction(e -> {
            SoundEffectsManager.playClick();
            String serverIp = serverIpField.getText().trim();
            if (!serverIp.isEmpty()) {
                System.out.println("Connecting to server: " + serverIp);

                // Update server host
                ServerConnection.setServerHost(serverIp);

                // Test connection
                if (ServerConnection.testConnection()) {
                    System.out.println("Connection successful!");

                    // Close dialog first (important: modal dialogs block the main window)
                    dialog.close();

                    // Establish persistent connection
                    if (ServerConnection.connect()) {
                        // Navigate to login view using NavigationService
                        try {
                            System.out.println("Loading login.fxml...");
                            Parent root = NavigationService.loadFXML("login");
                            System.out.println("login.fxml loaded successfully: " + root);

                            System.out.println("Calling NavigationService.navigateTo...");
                            NavigationService.navigateTo(root);
                            System.out.println("Navigation call completed");

                        } catch (IOException ex) {
                            System.err.println("Error loading login.fxml: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    } else {

                        CustomAlertDialog.show(dialog, "Connection Failed",
                                "Could not establish persistent connection to server.");
                    }
                } else {
                    // Show error dialog
                    CustomAlertDialog.show(dialog, "Connection Failed",
                            "Could not connect to " + serverIp + ":" + "5000"
                                    + "\nPlease check the server address and try again.");
                }
            }
        });

        // Layout
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle(
                "-fx-background-color: #2C3E50; -fx-border-color: #34495E; -fx-border-width: 2;");
        layout.getChildren().addAll(serverIpLabel, serverIpField, connectButton);

        Scene dialogScene = new Scene(layout);
        dialogScene.setFill(javafx.scene.paint.Color.web("#2C3E50"));
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.show();
    }
}
