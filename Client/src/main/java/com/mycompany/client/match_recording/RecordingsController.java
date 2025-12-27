/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.client.match_recording;

import com.mycompany.client.App;
import com.mycompany.client.core.CustomAlertDialog;
import com.mycompany.client.gameboard.controller.GameBoardController;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mohamed_Ali
 */
public class RecordingsController implements Initializable {

    @FXML
    private ListView<String> recordingsList;

    private final String recordingsPath = System.getProperty("user.home") + "/.tic_tac_toe/recordings";

    private final String username = "Player 1";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadRecordings();
    }

    private void loadRecordings() {

        File userDir = new File(recordingsPath + "/" + username);

        if (!userDir.exists() || !userDir.isDirectory()) {
            System.out.println("No recordings found for user");
            return;
        }

        File[] files = userDir.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null || files.length == 0) {
            System.out.println("No recording files");
            return;
        }

        for (File file : files) {
            recordingsList.getItems().add(file.getName());
        }

        System.out.println("Loaded " + files.length + " recordings");
    }

    @FXML
    private void onReplayClicked(ActionEvent event) {

        String selectedFile = recordingsList.getSelectionModel().getSelectedItem();

        if (selectedFile == null) {
            showAlert("Please select a recording first");
            return;
        }

        try {
            RecordingManager manager = new RecordingManager();
            GameRecording recording = manager.loadRecording(selectedFile, username);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mycompany/client/game_board.fxml"));
            Parent root = loader.load();

            GameBoardController controller = loader.getController();
            controller.startReplay(recording);

            Stage stage = (Stage) recordingsList.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        try {
            Stage stage = (Stage) recordingsList.getScene().getWindow();
            stage.setScene(
                    new javafx.scene.Scene(
                            javafx.fxml.FXMLLoader.load(
                                    App.class.getResource("main-menu.fxml"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Stage stage = (Stage) recordingsList.getScene().getWindow();
        CustomAlertDialog.show(stage, "Replay", message);
    }

}
