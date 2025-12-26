/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package com.mycompany.client.difficulty;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.mycompany.client.core.navigation.NavigationService;
import com.mycompany.client.core.session.UserSession;
import com.mycompany.client.gameboard.controller.GameBoardController;
import com.mycompany.client.gameboard.model.GameMode;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class DifficultyController implements Initializable {

    @FXML
    private VBox easyCard;

    @FXML
    private VBox mediumCard;

    @FXML
    private VBox hardCard;

    private VBox selectedCard = null;
    private String selectedDifficulty = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // No default selection
    }

    /**
     * Handle mouse hover on cards
     */
    @FXML
    private void onCardHover(MouseEvent event) {
        VBox card = (VBox) event.getSource();

        // Only apply hover effect if not selected
        if (card != selectedCard) {
            card.setStyle(card.getStyle() + "-fx-border-color: #3b82f6; -fx-border-width: 2;");
        }
    }

    /**
     * Handle mouse exit from cards
     */
    @FXML
    private void onCardExit(MouseEvent event) {
        VBox card = (VBox) event.getSource();

        // Remove hover effect if not selected
        if (card != selectedCard) {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                    "-fx-border-color: #e5e7eb; -fx-border-radius: 8; " +
                    "-fx-border-width: 1; -fx-padding: 20; -fx-cursor: hand;");
        }
    }

    /**
     * Handle card click selection
     */
    @FXML
    private void onCardClick(MouseEvent event) {
        VBox clickedCard = (VBox) event.getSource();

        // Deselect previous card
        if (selectedCard != null) {
            setCardSelected(selectedCard, false);
        }

        // Select new card
        selectedCard = clickedCard;
        setCardSelected(clickedCard, true);

        // Determine which difficulty was selected
        if (clickedCard == easyCard) {
            selectedDifficulty = "Easy";
        } else if (clickedCard == mediumCard) {
            selectedDifficulty = "Medium";
        } else if (clickedCard == hardCard) {
            selectedDifficulty = "Hard";
        }

        System.out.println("Selected difficulty: " + selectedDifficulty);
    }

    /**
     * Set card selected state
     */
    private void setCardSelected(VBox card, boolean selected) {
        if (selected) {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                    "-fx-border-color: #3b82f6; -fx-border-radius: 8; " +
                    "-fx-border-width: 2; -fx-padding: 20; -fx-cursor: hand;");
        } else {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                    "-fx-border-color: #e5e7eb; -fx-border-radius: 8; " +
                    "-fx-border-width: 1; -fx-padding: 20; -fx-cursor: hand;");
        }
    }

    /**
     * Navigate back to main menu
     */
    @FXML
    private void onBackToMenu(MouseEvent event) {
        // Parent root = NavigationService.loadFXML("main_menu");
        // Set root of current scene to preserve window size and stylesheets
        NavigationService.goBack();
    }

    /**
     * Handle start game button click
     */

@FXML
private void handleStartGame(ActionEvent event) {

    if (selectedDifficulty == null) {
        return;
    }

    if (!UserSession.getInstance().isLoggedIn()) {
        UserSession.getInstance().setGuestUsername("Player");
    }

    try {
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/com/mycompany/client/game_board.fxml"));
        Parent root = loader.load();

        GameBoardController controller = loader.getController();

        switch (selectedDifficulty) {
            case "Easy":
                controller.startNewGame(GameMode.SINGLE_PLAYER, Difficulty.EASY);
                break;
            case "Medium":
                controller.startNewGame(GameMode.SINGLE_PLAYER, Difficulty.MEDIUM);
                break;
            case "Hard":
                controller.startNewGame(GameMode.SINGLE_PLAYER, Difficulty.HARD);
                break;
        }

        NavigationService.navigateTo(root);

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    /**
     * Get the selected difficulty
     */
    public String getSelectedDifficulty() {
        return selectedDifficulty;
    }
}
