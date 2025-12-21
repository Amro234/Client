/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.client.profile;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author Mohamed_Ali
 */
public class ProfileScreenController implements Initializable {

    @FXML
    private Label usernameLabel;
    @FXML
    private Button editProfileButton;
    @FXML
    private Button changeBadgeButton;
    @FXML
    private TextArea bioTextArea;
    @FXML
    private Button editBioButton;
    @FXML
    private Button shareProfileButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Label totalGamesLabel;
    @FXML
    private Label winRateLabel;
    @FXML
    private Label winsLabel;
    @FXML
    private Label lossesLabel;
    @FXML
    private Button viewAllButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void updateStats(int totalGames, double winRate, int wins, int losses) {
        totalGamesLabel.setText(String.valueOf(totalGames));
        winRateLabel.setText(String.format("%.0f%%", winRate));
        winsLabel.setText(String.valueOf(wins));
        lossesLabel.setText(String.valueOf(losses));
    }

    public void updateProfile(String username, String bio) {
        usernameLabel.setText(username);
        bioTextArea.setText(bio);
    }
}
