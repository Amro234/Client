package com.mycompany.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import javafx.stage.Modality;

public class MainMenuController {

    @FXML
    private Button singlePlayerButton;

    @FXML
    private Button twoPlayersButton;

    @FXML
    private Button playOnlineButton;

    @FXML
    private Label settingsLabel;

    @FXML
    private Label guestLabel;

    @FXML
    private Label soundLabel;

    @FXML
    private Label recordingsLabel;

    @FXML
    private Text titleText;

    @FXML
    public void initialize() {
        if (titleText != null) {
            Font font = Font.font("Inter", FontWeight.BOLD, 32);
            titleText.setFont(font);
        }

        // Set cursor programmatically for all elements
        if (singlePlayerButton != null) {
            singlePlayerButton.setCursor(Cursor.HAND);
        }
        if (twoPlayersButton != null) {
            twoPlayersButton.setCursor(Cursor.HAND);
        }
        if (playOnlineButton != null) {
            playOnlineButton.setCursor(Cursor.HAND);
        }
        if (settingsLabel != null) {
            settingsLabel.setCursor(Cursor.HAND);
        }
        if (guestLabel != null) {
            guestLabel.setCursor(Cursor.HAND);
        }
        if (soundLabel != null) {
            soundLabel.setCursor(Cursor.HAND);
        }
        if (recordingsLabel != null) {
            recordingsLabel.setCursor(Cursor.HAND);
        }

        System.out.println("Main Menu loaded!");
        System.out.println("Cursors set programmatically");
    }

    @FXML
    private void onSettingsClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("settings.fxml"));
            Parent root = loader.load();

            Stage settingsStage = new Stage();
            settingsStage.setTitle("Settings");
            settingsStage.initOwner(settingsLabel.getScene().getWindow()); 
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.setScene(new Scene(root));
            settingsStage.show();
        } catch (IOException ex) {
            System.getLogger(LoginController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);

        }
    }

    @FXML
    private void onTwoPlayersClicked(ActionEvent event) {
        try {
            System.out.println("Two Players clicked - navigating to Game Board");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game_board.fxml"));
            Parent gameBoardRoot = loader.load();
            
            Stage stage = (Stage) twoPlayersButton.getScene().getWindow();
            Scene scene = new Scene(gameBoardRoot);
            scene.getStylesheets().add(App.class.getResource("/styles/game_board.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading game_board.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
