
package com.mycompany.client;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class SinglePlayerController implements Initializable {

    @FXML
    private VBox easyCard;

    @FXML
    private VBox mediumCard;

    @FXML
    private VBox hardCard;

    private VBox selectedCard = null;
    private Difficulty selectedDifficulty = null;

    @FXML
    private Label backToMenuLabel;
    @FXML
    private Button startGameButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // No default selection
    }

    @FXML
    private void onCardHover(MouseEvent event) {
        VBox card = (VBox) event.getSource();

        // Only apply hover effect if not selected
        if (card != selectedCard) {
            card.setStyle(card.getStyle() + "-fx-border-color: #3b82f6; -fx-border-width: 2;");
        }
    }

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
            selectedDifficulty = Difficulty.EASY;
        } else if (clickedCard == mediumCard) {
            selectedDifficulty = Difficulty.MEDIUM;
        } else if (clickedCard == hardCard) {
            selectedDifficulty = Difficulty.HARD;
        }

        System.out.println("Selected difficulty: " + selectedDifficulty);
    }

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

    @FXML
    private void onBackToMenu(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-menu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) easyCard.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading main menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Difficulty getSelectedDifficulty() {
        return selectedDifficulty;
    }

    @FXML
    private void handleStartGame(ActionEvent event) {
        if (selectedDifficulty == null) {
            System.out.println("Please select a difficulty level");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game_board.fxml"));
            Parent root = loader.load();

            GameBoardController controller = loader.getController();
            controller.startNewGame(GameMode.SINGLE_PLAYER, selectedDifficulty);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}