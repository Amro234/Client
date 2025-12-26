package com.mycompany.client.mainmenu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import javafx.scene.image.Image;
import com.mycompany.client.settings.manager.BackgroundMusicManager;
import com.mycompany.client.settings.manager.SettingsManager;
import com.mycompany.client.App;
import com.mycompany.client.auth.controller.LoginController;
import com.mycompany.client.core.navigation.NavigationService;
import com.mycompany.client.core.session.UserSession;
import com.mycompany.client.core.server.ServerConnection;

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
    private Label usernameLabel;

    @FXML
    private Label logoutLabel;

    @FXML
    private javafx.scene.image.ImageView soundIcon;

    @FXML
    private Label recordingsLabel;

    private Text titleText;

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
        if (usernameLabel != null) {
            usernameLabel.setCursor(Cursor.HAND);
        }
        if (logoutLabel != null) {
            logoutLabel.setCursor(Cursor.HAND);
        }
        if (soundIcon != null) {
            soundIcon.setCursor(Cursor.HAND);
        }
        if (recordingsLabel != null) {
            recordingsLabel.setCursor(Cursor.HAND);
        }

        // Set initial sound icon based on current master volume state
        if (soundIcon != null) {
            boolean isSoundOn = SettingsManager.isMasterVolumeOn();
            if (isSoundOn) {
                soundIcon.setImage(new Image(App.class.getResource("/assets/images/sound.png").toExternalForm()));
            } else {
                soundIcon.setImage(new Image(App.class.getResource("/assets/images/muted-sound.png").toExternalForm()));
            }
        }

        System.out.println("Main Menu loaded!");
    }

    @FXML
    private void onLogoutClicked(MouseEvent event) {
        // Clear session
        UserSession.getInstance().clearSession();

        // Disconnect from server
        ServerConnection.disconnect();

        // Reset UI
        if (usernameLabel != null) {
            usernameLabel.setText("👤 Guest");
        }
        if (logoutLabel != null) {
            logoutLabel.setVisible(false);
            logoutLabel.setManaged(false);
        }

        System.out.println("Logout successful - back to guest mode");
    }

    @FXML
    private void onSinglePlayerClicked(ActionEvent event) {
        try {
            System.out.println("Single Player clicked - navigating to difficulty selection");
            FXMLLoader loader = NavigationService.getFXMLLoader("difficulty");

            Parent singlePlayerRoot = loader.load();

            NavigationService.navigateTo(singlePlayerRoot);
        } catch (IOException e) {
            System.err.println("Error loading single-player.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onSettingsClicked(MouseEvent event) {
        try {

            Parent root = NavigationService.loadFXML("settings");
            NavigationService.navigateTo(root);
        } catch (IOException ex) {
            System.getLogger(LoginController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);

        }
    }

    @FXML
    private void onTwoPlayersClicked(ActionEvent event) {
        try {
            System.out.println("Two Players clicked - navigating to Setup Screen");
            Parent root = NavigationService.loadFXML("two_players_setup");
            NavigationService.navigateTo(root);
        } catch (IOException e) {
            System.err.println("Error loading two_players_setup.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onGuestClicked(MouseEvent event) {
        // try {
        // // FXMLLoader loader = new FXMLLoader(App.class.getResource("login.fxml"));
        // // Parent root = loader.load();
        // // Stage stage = (Stage) usernameLabel.getScene().getWindow();
        // // Scene scene = new Scene(root);
        // // stage.setScene(scene);
        // // stage.show();
        // } catch (IOException ex) {
        // System.getLogger(LoginController.class.getName()).log(System.Logger.Level.ERROR,
        // (String) null, ex);
        // }
    }

    @FXML
    private void onSoundClicked(MouseEvent event) {
        // Toggle the master volume state using SettingsManager
        boolean newState = !SettingsManager.isMasterVolumeOn();
        SettingsManager.setMasterVolumeOn(newState);

        // Update the BackgroundMusicManager volume
        if (newState) {
            // Sound enabled - restore volume
            BackgroundMusicManager.setVolume(SettingsManager.getMusicVolume() / 100.0);
            soundIcon.setImage(new Image(App.class.getResource("/assets/images/sound.png").toExternalForm()));
            System.out.println("Sound enabled!");
        } else {
            // Sound muted - set volume to 0
            BackgroundMusicManager.setVolume(0);
            soundIcon.setImage(new Image(App.class.getResource("/assets/images/muted-sound.png").toExternalForm()));
            System.out.println("Sound muted!");
        }

        // Save the settings
        SettingsManager.saveSettings();
    }

    @FXML
    private void onRecordingsClicked(MouseEvent event) {
        try {
            System.out.println("Recordings clicked - opening recordings list");

            Parent root = NavigationService.loadFXML("match_history");
            NavigationService.navigateTo(root);

        } catch (IOException e) {
            System.err.println("Error loading recordings.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onPlayOnlineClicked(ActionEvent event) {
        ServerConnectionDialog.show(playOnlineButton.getScene().getWindow());
    }

}
