package com.mycompany.client.core;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CustomAlertDialog {

    public static void show(Stage parentStage, String title, String message) {
        Platform.runLater(() -> {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UTILITY);
            dialog.setTitle(title);

            VBox container = new VBox(20);
            container.setAlignment(Pos.CENTER);
            container.setPadding(new Insets(30));
            container.setStyle(
                    "-fx-background-color: #2C3E50;" +
                            "-fx-background-radius: 10;");

            Label messageLabel = new Label(message);
            messageLabel.setStyle(
                    "-fx-font-size: 16px;" +
                            "-fx-text-fill: white;" +
                            "-fx-wrap-text: true;");
            messageLabel.setAlignment(Pos.CENTER);
            messageLabel.setMaxWidth(300);

            Button okBtn = new Button("OK");
            okBtn.setStyle(
                    "-fx-background-color: #3498DB;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 10 30;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;");
            okBtn.setOnMouseEntered(e -> okBtn.setStyle(
                    okBtn.getStyle() + "-fx-background-color: #2980B9;"));
            okBtn.setOnMouseExited(e -> okBtn.setStyle(
                    okBtn.getStyle().replace("-fx-background-color: #2980B9;", "") +
                            "-fx-background-color: #3498DB;"));
            okBtn.setOnAction(e -> dialog.close());

            container.getChildren().addAll(messageLabel, okBtn);

            Scene scene = new Scene(container);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialog.setScene(scene);
            dialog.setResizable(false);

            dialog.show();

            double centerX = parentStage.getX() + (parentStage.getWidth() / 2) - (dialog.getWidth() / 2);
            double centerY = parentStage.getY() + (parentStage.getHeight() / 2) - (dialog.getHeight() / 2);
            dialog.setX(centerX);
            dialog.setY(centerY);
        });
    }
}
