package com.mycompany.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.Cursor;

public class MainMenuController {
    
    // Style constants to avoid duplication
    private static final String BUTTON_DEFAULT_STYLE = 
            "-fx-background-color: #ffffff; -fx-text-fill: #2c3e50; " +
            "-fx-font-size: 16; -fx-font-weight: bold;" +
            "-fx-background-radius: 25;" +
            "-fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0, 0, 1);";
    
    private static final String BUTTON_HOVER_STYLE = 
            "-fx-background-color: #0093cb; -fx-text-fill: white; " +
            "-fx-font-size: 16; -fx-font-weight: bold;" +
            "-fx-background-radius: 25;" +
            "-fx-border-color: #0093cb; -fx-border-width: 2; -fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0, 0, 1);";
    
    
    @FXML
    private Button singlePlayerButton;
    
    @FXML
    private Button twoPlayersButton;
    
    @FXML
    private Button playOnlineButton;
    
    
    private PauseTransition hoverDelay;
    private boolean isHovering = false;

    @FXML
    private Text titleText;

    @FXML
    public void initialize() {
        if (titleText != null) {
            Font font = Font.font("Inter", FontWeight.BOLD, 32);
            titleText.setFont(font);
        }
        
        // Set cursor programmatically for all elements
        if (singlePlayerButton != null) singlePlayerButton.setCursor(Cursor.HAND);
        if (twoPlayersButton != null) twoPlayersButton.setCursor(Cursor.HAND);
        if (playOnlineButton != null) playOnlineButton.setCursor(Cursor.HAND);
        
        
        System.out.println("Main Menu loaded!");
        System.out.println("Cursors set programmatically");
    }

    // Single Player Hover Events
    @FXML
    private void onSinglePlayerHover(MouseEvent event) {
        System.out.println("Single Player HOVER");
        isHovering = true;
        
        if (hoverDelay != null) {
            hoverDelay.stop();
        }
        
        hoverDelay = new PauseTransition(Duration.millis(150));
        hoverDelay.setOnFinished(e -> {
            if (isHovering) {
                Button btn = (Button) event.getSource();
                btn.setStyle(BUTTON_HOVER_STYLE);
            }
        });
        hoverDelay.play();
    }
    
    @FXML
    private void onSinglePlayerExit(MouseEvent event) {
        System.out.println("Single Player EXIT");
        isHovering = false;
        
        if (hoverDelay != null) {
            hoverDelay.stop();
        }
        
        Button btn = (Button) event.getSource();
        btn.setStyle(BUTTON_DEFAULT_STYLE);
    }
    
    // Two Players Hover Events
    @FXML
    private void onTwoPlayersHover(MouseEvent event) {
        System.out.println("Two Players HOVER");
        isHovering = true;
        
        if (hoverDelay != null) {
            hoverDelay.stop();
        }
        
        hoverDelay = new PauseTransition(Duration.millis(150));
        hoverDelay.setOnFinished(e -> {
            if (isHovering) {
                Button btn = (Button) event.getSource();
                btn.setStyle(BUTTON_HOVER_STYLE);
            }
        });
        hoverDelay.play();
    }
    
    @FXML
    private void onTwoPlayersExit(MouseEvent event) {
        System.out.println("Two Players EXIT");
        isHovering = false;
        
        if (hoverDelay != null) {
            hoverDelay.stop();
        }
        
        Button btn = (Button) event.getSource();
        btn.setStyle(BUTTON_DEFAULT_STYLE);
    }
    
    // Play Online Hover Events
    @FXML
    private void onPlayOnlineHover(MouseEvent event) {
        System.out.println("Play Online HOVER");
        isHovering = true;
        
        if (hoverDelay != null) {
            hoverDelay.stop();
        }
        
        hoverDelay = new PauseTransition(Duration.millis(150));
        hoverDelay.setOnFinished(e -> {
            if (isHovering) {
                Button btn = (Button) event.getSource();
                btn.setStyle(BUTTON_HOVER_STYLE);
            }
        });
        hoverDelay.play();
    }
    
    @FXML
    private void onPlayOnlineExit(MouseEvent event) {
        System.out.println("Play Online EXIT");
        isHovering = false;
        
        if (hoverDelay != null) {
            hoverDelay.stop();
        }
        
        Button btn = (Button) event.getSource();
        btn.setStyle(BUTTON_DEFAULT_STYLE);
    }
}