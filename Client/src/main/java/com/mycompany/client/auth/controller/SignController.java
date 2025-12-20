/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.client.auth.controller;

import com.mycompany.client.App;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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
            FXMLLoader loader = new FXMLLoader(App.class.getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) navigateLoginItem.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.getLogger(SignController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

    }

    @FXML
    private void onCreateAccount(ActionEvent event) {
        // Validation or logic will go here
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
