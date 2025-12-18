package com.mycompany.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.Cursor;
import javafx.scene.control.Label;

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

}
