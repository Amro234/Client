/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.client;

import javafx.fxml.FXML;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author Amro Mohamed
 */
public class MainMenuController {
    @FXML
    private Text titleText; // We'll add fx:id to FXML
    
    @FXML
    public void initialize() {
        // Apply bold Inter font programmatically
        if (titleText != null) {
            Font font = Font.font("Inter", FontWeight.BOLD, 32);
            titleText.setFont(font);
        }
        
        System.out.println("Main Menu loaded!");
    }
}
