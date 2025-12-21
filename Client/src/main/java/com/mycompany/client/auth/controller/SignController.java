/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.client.auth.controller;

import com.mycompany.client.auth.AuthClient.AuthResponse;
import com.mycompany.client.core.navigation.NavigationService;
import com.mycompany.client.core.session.UserSession;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
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
public class SignController implements Initializable {

    @FXML
    private Label navigateLoginItem;
    @FXML
    private Button signupButtonItem;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField passwordFieldVisible;
    @FXML
    private TextField confirmPasswordFieldVisible;
    @FXML
    private ImageView passwordToggleIcon;
    @FXML
    private ImageView confirmPasswordToggleIcon;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onNavigateToLogin(MouseEvent event) {
        try {
            Parent root = NavigationService.loadFXML("login");
            NavigationService.replaceWith(root);
        } catch (IOException ex) {
            System.err.println("Error loading login screen: " + ex.getMessage());

        }
    }

    @FXML
    private void onCreateAccount(ActionEvent event) {
        // Get input values
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = getPasswordValue();
        String confirmPassword = getConfirmPasswordValue();

        // Validate username
        if (username.isEmpty()) {
            showDialog("Validation Error", "Username is required", "Please enter a username.");
            return;
        }

        // Validate email
        if (email.isEmpty()) {
            showDialog("Validation Error", "Email is required", "Please enter your email address.");
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            showDialog("Validation Error", "Invalid email format", "Please enter a valid email address.");
            return;
        }

        // Validate password
        if (password.isEmpty()) {
            showDialog("Validation Error", "Password is required", "Please enter a password.");
            return;
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            showDialog("Validation Error", "Confirm password is required", "Please confirm your password.");
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showDialog("Validation Error", "Passwords do not match", "Please make sure both passwords are the same.");
            return;
        }

        // Disable button to prevent double-click
        signupButtonItem.setDisable(true);
        signupButtonItem.setText("Creating Account...");

        // Perform registration in background thread
        new Thread(() -> {
            try {
                // Call server to register
                AuthResponse response = com.mycompany.client.auth.AuthClient
                        .register(username, email, password);
                UserSession.getInstance().setCurrentUser(response.getUser());
                // Registration successful - update UI on JavaFX thread
                Platform.runLater(() -> {
                    navigateToMainMenu();

                });

            } catch (com.mycompany.client.auth.AuthClient.AuthException e) {
                // Registration failed - show error on JavaFX thread
                Platform.runLater(() -> {
                    showDialog("Registration Failed", "Error", e.getMessage());
                    signupButtonItem.setDisable(false);
                    signupButtonItem.setText("Sign Up & Play");
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

    private String getConfirmPasswordValue() {
        if (confirmPasswordField.isVisible()) {
            return confirmPasswordField.getText();
        } else {
            return confirmPasswordFieldVisible.getText();
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(emailRegex, email);
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
    private void onTogglePasswordVisibility(MouseEvent event) {
        if (passwordField.isVisible()) {
            // Switch to visible password
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

    @FXML
    private void onToggleConfirmPasswordVisibility(MouseEvent event) {
        if (confirmPasswordField.isVisible()) {
            // Switch to visible password
            confirmPasswordFieldVisible.setText(confirmPasswordField.getText());
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            confirmPasswordFieldVisible.setVisible(true);
            confirmPasswordFieldVisible.setManaged(true);
        } else {
            // Switch to hidden password
            confirmPasswordField.setText(confirmPasswordFieldVisible.getText());
            confirmPasswordFieldVisible.setVisible(false);
            confirmPasswordFieldVisible.setManaged(false);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
        }
    }

}
