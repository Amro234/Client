package com.mycompany.client.GameResultVideoManager;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class GameResultVideoManager {

    private static final java.util.List<String> WIN_VIDEOS = java.util.Arrays.asList(
            "/videos/win/game_winner_1.mp4",
            "/videos/win/game_winner_2.mp4",
            "/videos/win/game_winner_3.mp4",
            "/videos/win/game_winner_4.mp4",
            "/videos/win/game_winner_5.mp4",
            "/videos/win/game_winner_6.mp4",
            "/videos/win/game_winner_7.mp4");
    private static final java.util.List<String> DRAW_VIDEOS = java.util.Arrays.asList(
            "/videos/draw/game_draw_1.mp4");
    private static final java.util.List<String> LOSE_VIDEOS = java.util.Arrays.asList(
            "/videos/lose/game_loser_1.mp4",
            "/videos/lose/game_loser_2.mp4",
            "/videos/lose/game_loser_3.mp4",
            "/videos/lose/game_loser_4.mp4",
            "/videos/lose/game_loser_5.mp4",
            "/videos/lose/game_loser_6.mp4",
            "/videos/lose/game_loser_7.mp4",
            "/videos/lose/game_loser_8.mp4");

    private static final java.util.Random random = new java.util.Random();

    private static Stage activeVideoStage = null;
    private static PopUpGameController activeController = null;
    private static boolean callbackCanceled = false;

    public static boolean isActive() {
        return activeVideoStage != null;
    }

    private static String getRandomPath(java.util.List<String> paths) {
        if (paths == null || paths.isEmpty())
            return null;
        return paths.get(random.nextInt(paths.size()));
    }

    public static void closeActiveVideoAndCancelCallback() {
        Platform.runLater(() -> {
            callbackCanceled = true;
            if (activeController != null) {
                activeController.stopVideo();
                activeController = null;
            }
            if (activeVideoStage != null) {
                activeVideoStage.close();
                activeVideoStage = null;
            }
        });
    }

    public static void showWinVideo(Stage parentStage, Runnable onPlayAgain, Runnable onExit) {
        playVideo(parentStage, getRandomPath(WIN_VIDEOS), "You Won! 🎉", onPlayAgain, onExit);
    }

    public static void showLoseVideo(Stage parentStage, Runnable onPlayAgain, Runnable onExit) {
        playVideo(parentStage, getRandomPath(LOSE_VIDEOS), "Game Over 💔", onPlayAgain, onExit);
    }

    public static void showDrawVideo(Stage parentStage, Runnable onPlayAgain, Runnable onExit) {
        playVideo(parentStage, getRandomPath(DRAW_VIDEOS), "It's a Draw! 🤝", onPlayAgain, onExit);
    }

    private static void playVideo(Stage parentStage, String path, String title, Runnable onPlayAgain, Runnable onExit) {
        Platform.runLater(() -> {
            try {
                callbackCanceled = false;

                FXMLLoader loader = new FXMLLoader(
                        GameResultVideoManager.class.getResource("/com/mycompany/client/game_result_popup.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);

                Stage stage = new Stage();
                stage.initOwner(parentStage);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);

                PopUpGameController controller = loader.getController();
                activeController = controller;
                activeVideoStage = stage;

                controller.setPopupStage(stage);
                controller.setPopupStatusMsg(title, path);

                // Configure Play Again button
                if (onPlayAgain != null) {
                    controller.setPlayAgainVisablility(true);
                    controller.setPlayAgainBtnFunc(() -> {
                        callbackCanceled = true; // Prevent the onExit callback from showing the second dialog
                        onPlayAgain.run();
                        stage.close();
                    });
                } else {
                    controller.setPlayAgainVisablility(false);
                }

                // Use a wrapper to ensure the callback is executed only once
                final boolean[] callbackExecuted = { false };
                Runnable safeOnExit = () -> {
                    if (!callbackExecuted[0]) {
                        callbackExecuted[0] = true;
                        if (!callbackCanceled && onExit != null) {
                            onExit.run();
                        }
                    }
                };

                // The stage's onHidden event will be our single point of truth for the callback
                stage.setOnHidden(e -> {
                    activeVideoStage = null;
                    if (activeController != null) {
                        activeController.stopVideo();
                        activeController = null;
                    }
                    safeOnExit.run();
                });

                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                if (onExit != null)
                    onExit.run();
            }
        });
    }
}
