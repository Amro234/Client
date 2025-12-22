package com.mycompany.client.GameResultVideoManager;

import java.io.File;
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

public class GameResultVideoManager {

    private static final int WIN_VIDEOS_COUNT = 7;
    private static final int DRAW_VIDEOS_COUNT = 1;
    private static final int LOSE_VIDEOS_COUNT = 8;

    private static final String WIN_VIDEO_TEMPLATE =
            "src/main/resources/videos/win/game_winner_%d.mp4";
    private static final String DRAW_VIDEO_TEMPLATE =
            "src/main/resources/videos/draw/game_draw_%d.mp4";
    private static final String LOSE_VIDEO_TEMPLATE =
            "src/main/resources/videos/lose/game_loser_%d.mp4";

    private static int randomIndex(int max) {
        return 1 + (int) (Math.random() * max);
    }

    

    public static void showWinVideo(Runnable onFinish) {
        playVideo(
                String.format(WIN_VIDEO_TEMPLATE, randomIndex(WIN_VIDEOS_COUNT)),
                "Winner 🎉",
                onFinish
        );
    }

    public static void showLoseVideo(Runnable onFinish) {
        playVideo(
                String.format(LOSE_VIDEO_TEMPLATE, randomIndex(LOSE_VIDEOS_COUNT)),
                "You Lost 💔",
                onFinish
        );
    }

    public static void showDrawVideo(Runnable onFinish) {
        playVideo(
                String.format(DRAW_VIDEO_TEMPLATE, randomIndex(DRAW_VIDEOS_COUNT)),
                "Draw 🤝",
                onFinish
        );
    }



    private static void playVideo(String path, String title, Runnable onFinish) {

        Platform.runLater(() -> {
            try {
                File file = new File(path);
                if (!file.exists()) {
                    safeFinish(onFinish);
                    return;
                }

                Stage stage = new Stage();
                stage.setTitle(title);

                MediaPlayer player = new MediaPlayer(
                        new Media(file.toURI().toString())
                );

                MediaView view = new MediaView(player);
                view.setFitWidth(600);
                view.setFitHeight(400);
                view.setPreserveRatio(true);

                Button skip = new Button("Skip ⏭");
                skip.setOnAction(e -> {
                    player.stop();
                    stage.close();
                });

                VBox root = new VBox(10, view, skip);
                root.setStyle("-fx-alignment:center; -fx-padding:20");

                stage.setScene(new Scene(root));

              
                stage.setOnHidden(e -> safeFinish(onFinish));

                player.setOnEndOfMedia(stage::close);

                stage.show();
                player.play();

            } catch (Exception e) {
                e.printStackTrace();
                safeFinish(onFinish);
            }
        });
    }

   

    private static void safeFinish(Runnable onFinish) {
        PauseTransition pause = new PauseTransition(Duration.millis(80));
        pause.setOnFinished(e -> Platform.runLater(onFinish));
        pause.play();
    }
}
