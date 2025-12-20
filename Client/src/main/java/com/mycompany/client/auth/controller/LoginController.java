
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onLogin(ActionEvent event) {
        // Validation or logic will go here
    }

    @FXML
    private void onCreateAccount(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("sign.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) createAnAccountItem.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.getLogger(LoginController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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

}
