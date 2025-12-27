package com.mycompany.client.mainmenu;

import com.mycompany.client.core.navigation.NavigationService;
import com.mycompany.client.core.server.ServerConnection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
        dialog.setTitle("Connect to Server");
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);

        // Create UI components
        Label serverIpLabel = new Label("Server IP:");
        serverIpLabel.setStyle("-fx-font-size: 14px;");

        TextField serverIpField = new TextField();
        serverIpField.setPromptText("Enter server IP address");
        serverIpField.setPrefWidth(250);
        serverIpField.setText(ServerConnection.getServerHost()); // Default to current server

        Button connectButton = new Button("Connect");
        connectButton.setPrefWidth(100);
        connectButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");

        // Connect button action
        connectButton.setOnAction(e -> {
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
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Connection Failed");
                        alert.setHeaderText("Unable to establish connection");
                        alert.setContentText("Could not establish persistent connection to server.");
                        alert.showAndWait();
                    }
                } else {
                    // Show error dialog
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Connection Failed");
                    alert.setHeaderText("Unable to connect to server");
                    alert.setContentText("Could not connect to " + serverIp + ":" + "5000"
                            + "\nPlease check the server address and try again.");
                    alert.initOwner(dialog);
                    alert.showAndWait();
                }
            }
        });

        
     
        // Layout
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(serverIpLabel, serverIpField, connectButton);

        Scene dialogScene = new Scene(layout, 300, 150);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}
