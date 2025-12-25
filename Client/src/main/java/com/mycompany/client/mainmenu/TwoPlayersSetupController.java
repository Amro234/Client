package com.mycompany.client.mainmenu;

import com.mycompany.client.App;
import com.mycompany.client.core.navigation.NavigationService;
import com.mycompany.client.gameboard.controller.GameBoardController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import java.io.IOException;

public class TwoPlayersSetupController {

    @FXML
    private TextField p1NameField;

    @FXML
    private TextField p2NameField;

    @FXML
    private void onStartClicked(ActionEvent event) {
        String p1Name = p1NameField.getText().trim();
        String p2Name = p2NameField.getText().trim();

        if (p1Name.isEmpty()) {
            showAlert("Validation Error", "Player 1 name is required!");
            return;
        }

        if (p2Name.isEmpty()) {
            showAlert("Validation Error", "Player 2 name is required!");
            return;
        }

        try {
            FXMLLoader loader = NavigationService.getFXMLLoader("game_board");
            Parent gameBoardRoot = loader.load();

            gameBoardRoot.getStylesheets().add(App.class.getResource("/styles/game_board.css").toExternalForm());

            GameBoardController controller = loader.getController();
            controller.startLocalTwoPlayerGame(p1Name, p2Name);

            NavigationService.navigateTo(gameBoardRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        NavigationService.goBack();
    }
}
