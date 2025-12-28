package com.mycompany.client.mainmenu;

import com.mycompany.client.App;
import com.mycompany.client.core.CustomAlertDialog;
import com.mycompany.client.core.navigation.NavigationService;
import com.mycompany.client.core.session.UserSession;
import com.mycompany.client.gameboard.controller.GameBoardController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import com.mycompany.client.settings.manager.SoundEffectsManager;

public class TwoPlayersSetupController {

    @FXML
    private TextField p1NameField;

    @FXML
    private TextField p2NameField;

    @FXML
    private void onStartClicked(ActionEvent event) {

        String p1Name = p1NameField.getText().trim();
        String p2Name = p2NameField.getText().trim();

        if (p1Name.isEmpty() || p2Name.isEmpty()) {
            showAlert("Validation Error", "Both player names are required!");
            return;
        }

        try {
            FXMLLoader loader = NavigationService.getFXMLLoader("game_board");
            Parent gameBoardRoot = loader.load();

            GameBoardController controller = loader.getController();
            controller.startLocalTwoPlayerGame(p1Name, p2Name);
            SoundEffectsManager.playClick();
            NavigationService.replaceWith(gameBoardRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        CustomAlertDialog.show((Stage) p1NameField.getScene().getWindow(), title, content);
    }

    @FXML
    private void onBackClicked(ActionEvent event) {
        NavigationService.goBack();
        SoundEffectsManager.playClick();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        onBackClicked(event);
    }
}
