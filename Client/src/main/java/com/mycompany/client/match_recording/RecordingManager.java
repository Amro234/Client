/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client.match_recording;

import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 *
 * @author Mohamed_Ali
 */
public class RecordingManager {

    private final String recordingsPath = System.getProperty("user.home") + "/.tic_tac_toe/recordings";

    public void saveRecording(GameRecording recording, String username) {
        try {
            File dir = getRecordingsDirectory(username);
            if (!dir.exists())
                dir.mkdirs();

            String fileName = "game_" + System.currentTimeMillis() + ".json";
            File file = new File(dir, fileName);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(recording.toJSON().toString(4));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameRecording loadRecording(String filename, String username) throws Exception {
        File file = new File(getRecordingsDirectory(username), filename);
        String content = Files.readString(file.toPath());
        return GameRecording.fromJSON(new JSONObject(content));
    }

    public List<GameRecording> getAllRecordings(String username) {
        List<GameRecording> list = new ArrayList<>();
        File dir = getRecordingsDirectory(username);

        if (!dir.exists())
            return list;

        for (File file : dir.listFiles()) {
            try {
                String content = Files.readString(file.toPath());
                list.add(GameRecording.fromJSON(new JSONObject(content)));
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    private File getRecordingsDirectory(String username) {
        return new File(recordingsPath + "/" + username);
    }
public static void showToast(String message, Scene scene) {

    Label toast = new Label(message);
    toast.setStyle(
        "-fx-background-color: rgba(0,0,0,0.85);" +
        "-fx-text-fill: white;" +
        "-fx-padding: 10 18;" +
        "-fx-background-radius: 16;" +
        "-fx-font-size: 13;"
    );

    Popup popup = new Popup();
    popup.getContent().add(toast);
    popup.setAutoFix(true);
    popup.setAutoHide(true);

    
    toast.applyCss();
    toast.layout();

    Window window = scene.getWindow();

    double x = window.getX() + (scene.getWidth() / 2) - (toast.getWidth() / 2);
    double y = window.getY() + scene.getHeight() - 80;

    popup.show(window, x, y);

    FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toast);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);

    PauseTransition stay = new PauseTransition(Duration.seconds(1.8));

    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toast);
    fadeOut.setFromValue(1);
    fadeOut.setToValue(0);

    fadeOut.setOnFinished(e -> popup.hide());

    new SequentialTransition(fadeIn, stay, fadeOut).play();
}



}
