/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.client;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * FXML Controller class
 *
 * @author Mohamed_Ali
 */
public class GameLobbyController implements Initializable {

    @FXML
    private Button logOutBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
     

    @FXML
    private void onLogOutPressed(ActionEvent event) {
         showConfirmationDialog("Info", "Are you sure you want to leave the app?");
    }
    
    
    private void showConfirmationDialog(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(message);
    alert.setContentText("Choose your option:");

    ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(okButton, cancelButton);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == okButton) {
        showGoodbyeMessage();
}
}

private void showGoodbyeMessage() {
    Alert goodbyeAlert = new Alert(Alert.AlertType.INFORMATION);
    goodbyeAlert.setTitle("Goodbye");
    goodbyeAlert.setHeaderText("Thank you for using the app!");
    goodbyeAlert.setContentText("We hope to see you again soon.");
    goodbyeAlert.showAndWait();

  
    Platform.exit();
}
}
