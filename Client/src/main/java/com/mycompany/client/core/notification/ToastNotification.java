package com.mycompany.client.core.notification;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public class ToastNotification {

    public enum Type {
        SUCCESS("#4CAF50"),
        ERROR("#F44336"),
        WARNING("#FFC107");

        private final String color;

        Type(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    /**
     * Display a toast notification
     * 
     * @param message The message to display
     * @param type    The type of notification (SUCCESS, ERROR, WARNING)
     * @param owner   The owner window to position relative to (null to auto-detect
     *                focused window)
     */
    public static void show(String message, Type type, Window owner) {
        Platform.runLater(() -> {
            // Auto-detect focused window if owner is null
            Window finalOwner = owner;
            if (finalOwner == null) {
                finalOwner = Stage.getWindows().stream()
                        .filter(Window::isFocused)
                        .findFirst()
                        .orElse(null);
            }

            Stage toastStage = new Stage();
            toastStage.initStyle(StageStyle.TRANSPARENT);
            toastStage.setAlwaysOnTop(true);
            if (finalOwner != null) {
                toastStage.initOwner(finalOwner);
            }

            Label toastLabel = new Label(message);
            toastLabel.setStyle(
                    "-fx-background-color: " + type.getColor() + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-padding: 15px 25px;" +
                            "-fx-font-size: 14px;" +
                            "-fx-background-radius: 10;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 2);");

            StackPane root = new StackPane(toastLabel);
            root.setStyle("-fx-background-color: transparent;");
            root.setAlignment(Pos.CENTER);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            toastStage.setScene(scene);

            toastStage.show();

            // Position relative to owner window or screen
            if (finalOwner != null) {
                double centerX = finalOwner.getX() + (finalOwner.getWidth() - toastStage.getWidth()) / 2;
                double topY = finalOwner.getY() + 80;
                toastStage.setX(centerX);
                toastStage.setY(topY);
            } else {
                double centerX = (javafx.stage.Screen.getPrimary().getVisualBounds().getWidth() - toastStage.getWidth())
                        / 2;
                toastStage.setX(centerX);
                toastStage.setY(100);
            }

            // Fade in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            // Auto-hide after 3 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> toastStage.close());
                fadeOut.play();
            });

            fadeIn.setOnFinished(e -> delay.play());
            fadeIn.play();
        });
    }

    /**
     * Show a success toast (screen-centered)
     */
    public static void success(String message) {
        show(message, Type.SUCCESS, null);
    }

    /**
     * Show a success toast (relative to owner window)
     */
    public static void success(String message, Window owner) {
        show(message, Type.SUCCESS, owner);
    }

    /**
     * Show an error toast (screen-centered)
     */
    public static void error(String message) {
        show(message, Type.ERROR, null);
    }

    /**
     * Show an error toast (relative to owner window)
     */
    public static void error(String message, Window owner) {
        show(message, Type.ERROR, owner);
    }

    /**
     * Show a warning toast (screen-centered)
     */
    public static void warning(String message) {
        show(message, Type.WARNING, null);
    }

    public static void warning(String message, Window owner) {
        show(message, Type.WARNING, owner);
    }

    /**
     * Show a warning toast (relative to owner window)
     */

}
