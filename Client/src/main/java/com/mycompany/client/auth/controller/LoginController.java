
package com.mycompany.client.auth.controller;

import com.mycompany.client.auth.AuthClient.AuthResponse;
import com.mycompany.client.core.navigation.NavigationService;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author antoneos
 */
public class LoginController implements Initializable {

    @FXML
    private Button loginButtonItem;
    @FXML
    private Label createAnAccountItem;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordFieldVisible;
    @FXML
    private ImageView passwordToggleIcon;
    @FXML
    private Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = getPasswordValue();

        if (username.isEmpty()) {
            showDialog("Validation Error", "Username is required", "Please enter your username.");
            return;
        }

        if (password.isEmpty()) {
            showDialog("Validation Error", "Password is required", "Please enter your password.");
            return;
        }

        loginButtonItem.setDisable(true);
        loginButtonItem.setText("Logging in...");

        new Thread(() -> {
            try {
                AuthResponse response = com.mycompany.client.auth.AuthClient
                        .login(username, password);

                Platform.runLater(() -> {

                    navigateToMainMenu();
                });

            } catch (com.mycompany.client.auth.AuthClient.AuthException e) {
                Platform.runLater(() -> {
                    showDialog("Login Failed", "Authentication Error", e.getMessage());
                    loginButtonItem.setDisable(false);
                    loginButtonItem.setText("Enter Lobby");
                    passwordField.clear();
                    passwordFieldVisible.clear();
                });
            }
        }).start();
    }

    private String getPasswordValue() {
        if (passwordField.isVisible()) {
            return passwordField.getText();
        } else {
            return passwordFieldVisible.getText();
        }
    }

    private void showDialog(String title, String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateToMainMenu() {
        try {
            Parent root = NavigationService.loadFXML("gameLobby");
            NavigationService.replaceWith(root);
        } catch (IOException ex) {
            System.err.println("Error loading game lobby: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void onCreateAccount(MouseEvent event) {
        try {
            Parent root = NavigationService.loadFXML("sign");
            NavigationService.replaceWith(root);
        } catch (IOException ex) {
            System.getLogger(LoginController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void onTogglePasswordVisibility(MouseEvent event) {
        if (passwordField.isVisible()) {
            passwordFieldVisible.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordFieldVisible.setVisible(true);
            passwordFieldVisible.setManaged(true);
        } else {
            // Switch to hidden password
            passwordField.setText(passwordFieldVisible.getText());
            passwordFieldVisible.setVisible(false);
            passwordFieldVisible.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
        }
    }

}
