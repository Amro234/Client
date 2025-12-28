package com.mycompany.client.GameResultVideoManager;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.mycompany.client.settings.manager.SoundEffectsManager;

public class GameResultVideoManager {

    private static final int WIN_VIDEOS_COUNT = 7;
    private static final int DRAW_VIDEOS_COUNT = 1;
    private static final int LOSE_VIDEOS_COUNT = 8;

    private static final String WIN_VIDEO_TEMPLATE = "/videos/win/game_winner_%d.mp4";
    private static final String DRAW_VIDEO_TEMPLATE = "/videos/draw/game_draw_%d.mp4";
    private static final String LOSE_VIDEO_TEMPLATE = "/videos/lose/game_loser_%d.mp4";

    // Track active video dialog
    private static Stage activeVideoStage = null;
    private static MediaPlayer activePlayer = null;
    private static boolean callbackCanceled = false;

    public static boolean isActive() {
        return activeVideoStage != null;
    }

    private static int randomIndex(int max) {
        return 1 + (int) (Math.random() * max);
    }

    /**
     * Closes any active video dialog and cancels its callback.
     * This should be called when a rematch is accepted to prevent
     * the old game over dialog from appearing.
     */
    public static void closeActiveVideoAndCancelCallback() {
        Platform.runLater(() -> {
            callbackCanceled = true;
            if (activePlayer != null) {
                activePlayer.stop();
                activePlayer.dispose();
                activePlayer = null;
            }
            if (activeVideoStage != null) {
                activeVideoStage.close();
                activeVideoStage = null;
            }
        });
    }

    public static void showWinVideo(Runnable onFinish) {
        playVideo(
                String.format(WIN_VIDEO_TEMPLATE, randomIndex(WIN_VIDEOS_COUNT)),
                "Winner 🎉",
                onFinish);
    }

    public static void showLoseVideo(Runnable onFinish) {
        playVideo(
                String.format(LOSE_VIDEO_TEMPLATE, randomIndex(LOSE_VIDEOS_COUNT)),
                "You Lost 💔",
                onFinish);
    }

    public static void showDrawVideo(Runnable onFinish) {
        playVideo(
                String.format(DRAW_VIDEO_TEMPLATE, randomIndex(DRAW_VIDEOS_COUNT)),
                "Draw 🤝",
                onFinish);
    }

    private static void playVideo(String path, String title, Runnable onFinish) {

        Platform.runLater(() -> {
            try {
                // Reset cancellation flag for new video
                callbackCanceled = false;

                var videoUrl = GameResultVideoManager.class.getResource(path);
                if (videoUrl == null) {
                    System.err.println("Video not found: " + path);
                    safeFinish(onFinish);
                    return;
                }

                Stage stage = new Stage();
                stage.setTitle(title);

                MediaPlayer player = new MediaPlayer(
                        new Media(videoUrl.toExternalForm()));

                // Track active video
                activeVideoStage = stage;
                activePlayer = player;

                MediaView view = new MediaView(player);
                view.setFitWidth(600);
                view.setFitHeight(400);
                view.setPreserveRatio(true);

                Button skip = new Button("Skip ⏭");
                skip.setOnAction(e -> {
                    SoundEffectsManager.playClick();
                    player.stop();
                    stage.close();
                });

                VBox root = new VBox(10, view, skip);
                root.setStyle("-fx-alignment:center; -fx-padding:20");

                stage.setScene(new Scene(root));

                stage.setOnHidden(e -> {
                    // Clear active references
                    activeVideoStage = null;
                    activePlayer = null;

                    // Only execute callback if not canceled
                    if (!callbackCanceled) {
                        safeFinish(onFinish);
                    } else {
                        // Reset flag for next video
                        callbackCanceled = false;
                    }
                });

                player.setOnEndOfMedia(stage::close);

                stage.show();
                player.play();

            } catch (Exception e) {
                e.printStackTrace();
                // Clear active references on error
                activeVideoStage = null;
                activePlayer = null;
                if (!callbackCanceled) {
                    safeFinish(onFinish);
                }
            }
        });
    }

    private static void safeFinish(Runnable onFinish) {
        PauseTransition pause = new PauseTransition(Duration.millis(80));
        pause.setOnFinished(e -> Platform.runLater(onFinish));
        pause.play();
    }
}
