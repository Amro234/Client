/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author antoneos
 */
public class SignController implements Initializable {

    @FXML
    private Label navigateLogin;
    @FXML
    private Button signUpButton;
    @FXML
    private HBox navigateToLoign;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void NavigateLogin(MouseEvent event) {
              try {
            App.setRoot("login");
        } catch (IOException ex) {
            System.getLogger(LoginController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
              
    }

  

    @FXML
    private void SignUpButton(ActionEvent event) {
                try {
            App.setRoot("login");
        } catch (IOException ex) {
            System.getLogger(LoginController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void NavigateToLogin(MouseEvent event) {        try {
            App.setRoot("login");
        } catch (IOException ex) {
            System.getLogger(LoginController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
}
