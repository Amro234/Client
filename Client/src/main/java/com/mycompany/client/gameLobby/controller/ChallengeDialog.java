package com.mycompany.client.gameLobby.controller;

import com.mycompany.client.core.notification.ToastNotification;
import com.mycompany.client.gameLobby.networking.GameLobbyClient;
import com.mycompany.client.gameLobby.networking.exception.GameLobbyException;
import com.mycompany.client.gameLobby.networking.model.challenge.ChallengeResponse;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ChallengeDialog {

    private Stage dialog;
    private final String challengerUsername;
    private final Stage parentStage;

    public ChallengeDialog(String challengerUsername, Stage parentStage) {
        this.challengerUsername = challengerUsername;
        this.parentStage = parentStage;
        createDialog();
    }

    private void createDialog() {
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Challenge Received");

        // Main container
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.setStyle(
                "-fx-background-color: #2C3E50;" +
                        "-fx-background-radius: 10;");

        // Image
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/assets/images/avatar1.png"));
            imageView.setImage(image);
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load challenge image: " + e.getMessage());
            // Continue without image
        }

        // Message label
        Label messageLabel = new Label(challengerUsername + " is challenging you!");
        messageLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-wrap-text: true;");
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setMaxWidth(300);

        // Buttons container
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        // Accept button
        Button acceptBtn = new Button("Accept");
        acceptBtn.setStyle(
                "-fx-background-color: #27AE60;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 30;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;");
        acceptBtn.setOnMouseEntered(e -> acceptBtn.setStyle(
                acceptBtn.getStyle() + "-fx-background-color: #229954;"));
        acceptBtn.setOnMouseExited(e -> acceptBtn.setStyle(
                acceptBtn.getStyle().replace("-fx-background-color: #229954;", "") +
                        "-fx-background-color: #27AE60;"));
        acceptBtn.setOnAction(e -> handleAccept());

        // Refuse button
        Button refuseBtn = new Button("Refuse");
        refuseBtn.setStyle(
                "-fx-background-color: #E74C3C;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 30;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;");
        refuseBtn.setOnMouseEntered(e -> refuseBtn.setStyle(
                refuseBtn.getStyle() + "-fx-background-color: #C0392B;"));
        refuseBtn.setOnMouseExited(e -> refuseBtn.setStyle(
                refuseBtn.getStyle().replace("-fx-background-color: #C0392B;", "") +
                        "-fx-background-color: #E74C3C;"));
        refuseBtn.setOnAction(e -> handleRefuse());

        buttonBox.getChildren().addAll(acceptBtn, refuseBtn);

        // Add all components to container
        if (imageView.getImage() != null) {
            container.getChildren().add(imageView);
        }
        container.getChildren().addAll(messageLabel, buttonBox);

        Scene scene = new Scene(container);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.setResizable(false);

        dialog.setOnCloseRequest(e -> {
            e.consume();
            handleRefuse();
        });
    }

    private void handleAccept() {
        dialog.close();

        try {
            GameLobbyClient.acceptChallenge();
            // We do not wait for a response here.
            // We rely on the GAME_STARTED notification to trigger navigation.
            // ToastNotification.success("Challenge accepted, waiting for game start...");

        } catch (GameLobbyException e) {
            ToastNotification.error("Failed to accept challenge: " + e.getMessage());
        }
    }

    private void handleRefuse() {
        dialog.close();

        try {
            ChallengeResponse response = GameLobbyClient.declineChallenge();

            if (response.isSuccess()) {
                ToastNotification.success(response.getMessage());
            } else {
                ToastNotification.error(response.getMessage());
            }
        } catch (GameLobbyException e) {
            ToastNotification.error("Failed to decline challenge: " + e.getMessage());
        }
    }

    public void show() {
        Platform.runLater(() -> {
            dialog.show();

            // Center dialog relative to parent stage
            double centerX = parentStage.getX() + (parentStage.getWidth() / 2) - (dialog.getWidth() / 2);
            double centerY = parentStage.getY() + (parentStage.getHeight() / 2) - (dialog.getHeight() / 2);
            dialog.setX(centerX);
            dialog.setY(centerY);
        });
    }
}
