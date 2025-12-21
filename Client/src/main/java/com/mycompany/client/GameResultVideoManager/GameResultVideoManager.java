/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client.GameResultVideoManager;

/**
 *
 * @author Mohamed_Ali
 */

import java.io.File;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GameResultVideoManager {

    private static final int WIN_VIDEOS_COUNT = 4;
    private static final int DRAW_VIDEOS_COUNT = 1;
    private static final int LOSE_VIDEOS_COUNT = 7;

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
        playVideo(false, onFinish);
    }

    public static void showDrawVideo(Runnable onFinish) {
        playVideo(true, onFinish);
    }

    private static void playVideo(boolean isDraw, Runnable onFinish) {
        Platform.runLater(() -> {
            try {
                int index = isDraw
                        ? randomIndex(DRAW_VIDEOS_COUNT)
                        : randomIndex(WIN_VIDEOS_COUNT);

                String path = isDraw
                        ? String.format(DRAW_VIDEO_TEMPLATE, index)
                        : String.format(WIN_VIDEO_TEMPLATE, index);

                File file = new File(path);
                if (!file.exists()) {
                    System.err.println("Video not found: " + path);
                    onFinish.run();
                    return;
                }

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle(isDraw ? "Draw 🤝" : "Winner 🎉");

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
                    onFinish.run();
                });

                VBox root = new VBox(10, view, skip);
                root.setStyle("-fx-alignment:center; -fx-padding:20");

                stage.setScene(new Scene(root));

                player.setOnEndOfMedia(() -> {
                    stage.close();
                    onFinish.run();
                });

                player.play();
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                onFinish.run();
            }
        });
    }
}

