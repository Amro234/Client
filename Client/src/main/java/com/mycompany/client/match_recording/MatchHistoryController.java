/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.client.match_recording;

import com.mycompany.client.core.CustomAlertDialog;
import com.mycompany.client.core.navigation.NavigationService;
import com.mycompany.client.core.session.UserSession;
import com.mycompany.client.gameboard.controller.GameBoardController;
import com.mycompany.client.matches.data.FilterType;
import com.mycompany.client.matches.data.MatchData;
import com.mycompany.client.matches.data.MatchResult;
import com.mycompany.client.matches.ui.MatchCard;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.mycompany.client.settings.manager.SoundEffectsManager;

/**
 * FXML Controller class
 *
 * @author eslam
 */

public class MatchHistoryController implements Initializable {

    @FXML
    private VBox matchListContainer;
    @FXML
    private TextField searchField;
    @FXML
    private ToggleButton allGamesBtn, winsBtn, lossesBtn, drawsBtn;

    private ArrayList<MatchData> allMatches;
    private FilterType currentFilter = FilterType.ALL;

    // private final String username = "Player 1";
    private final String recordingsPath = System.getProperty("user.home") + "/.tic_tac_toe/recordings";
    @FXML
    private Button backBtn;
    private String username;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        username = UserSession.getInstance().getUsername();

        allMatches = loadMatchesFromRecordings();
        setupFilterButtons();
        setupSearch();
        displayMatches(allMatches);
    }

    private ArrayList<MatchData> loadMatchesFromRecordings() {

        ArrayList<MatchData> matches = new ArrayList<>();
        RecordingManager manager = new RecordingManager();

        File userDir = new File(recordingsPath + "/" + username);
        if (!userDir.exists() || !userDir.isDirectory()) {
            return matches;
        }

        File[] files = userDir.listFiles((d, n) -> n.endsWith(".json"));
        if (files == null)
            return matches;

        for (File file : files) {
            try {
                GameRecording rec = manager.loadRecording(file.getName(), username);

                MatchResult result;

                switch (rec.getStatus()) {
                    case "WIN":
                        result = MatchResult.VICTORY;
                        break;
                    case "DRAW":
                        result = MatchResult.DRAW;
                        break;
                    case "LOSE":
                        result = MatchResult.DEFEAT;
                        break;
                    case "CANCELLED":
                        result = MatchResult.CANCELLED;
                        break;
                    default:
                        continue; // safety
                }

                matches.add(
                        new MatchData(
                                rec.opponentPlayerName,
                                result,
                                rec.date,
                                rec.time,
                                rec.getSteps().size(),
                                file.getName()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return matches;
    }

    private void setupFilterButtons() {
        allGamesBtn.setOnAction(e -> {
            SoundEffectsManager.playClick();
            applyFilter(allGamesBtn, FilterType.ALL);
        });
        winsBtn.setOnAction(e -> {
            SoundEffectsManager.playClick();
            applyFilter(winsBtn, FilterType.VICTORY);
        });
        lossesBtn.setOnAction(e -> {
            SoundEffectsManager.playClick();
            applyFilter(lossesBtn, FilterType.DEFEAT);
        });
        drawsBtn.setOnAction(e -> {
            SoundEffectsManager.playClick();
            applyFilter(drawsBtn, FilterType.DRAW);
        });
    }

    private void applyFilter(ToggleButton activeBtn, FilterType filter) {
        setActiveFilter(activeBtn);
        currentFilter = filter;
        filterAndDisplayMatches();
    }

    private void setActiveFilter(ToggleButton activeBtn) {
        ToggleButton[] buttons = { allGamesBtn, winsBtn, lossesBtn, drawsBtn };
        for (ToggleButton btn : buttons) {
            btn.getStyleClass().remove("filter-btn-active");
            btn.setSelected(false);
        }
        activeBtn.getStyleClass().add("filter-btn-active");
        activeBtn.setSelected(true);
    }

    private void setupSearch() {
        searchField.textProperty().addListener(
                (obs, oldVal, newVal) -> filterAndDisplayMatches());
    }

    private void filterAndDisplayMatches() {

        String searchText = searchField.getText();

        List<MatchData> filtered = allMatches.stream()
                .filter(m -> currentFilter.matches(m.getResult()))
                .filter(m -> m.matchesSearch(searchText))
                .collect(Collectors.toList());

        displayMatches(filtered);
    }

    private void openReplay(MatchData match) {
        SoundEffectsManager.playClick();
        try {
            RecordingManager manager = new RecordingManager();
            GameRecording recording = manager.loadRecording(match.getRecordingFileName(), username);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mycompany/client/game_board.fxml"));

            Parent root = loader.load();
            GameBoardController controller = loader.getController();
            controller.startReplay(recording);

            // NavigationService
            NavigationService.navigateTo(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayMatches(List<MatchData> matches) {

        matchListContainer.getChildren().clear();

        for (MatchData match : matches) {

            MatchCard card = new MatchCard(match);

            card.setOnReplayRequested(this::openReplay);
            card.setOnDeleteRequested(this::deleteSingleMatch);

            matchListContainer.getChildren().add(card);
        }
    }

    private void deleteSingleMatch(MatchData match) {
        Stage stage = (Stage) matchListContainer.getScene().getWindow();
        CustomAlertDialog.showConfirmation(stage, "Delete Recording", "Delete this match?", "This recording will be permanently deleted.", () -> {
            SoundEffectsManager.playClick();

            File file = new File(
                    recordingsPath + "/" + username + "/" + match.getRecordingFileName());

            if (file.exists()) {
                file.delete();
            }

            allMatches.remove(match);
            filterAndDisplayMatches();

            RecordingManager.showToast(
                    "❌ Recording deleted",
                    matchListContainer.getScene());
        });
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SoundEffectsManager.playClick();
        NavigationService.goBack();
    }

    @FXML
    private void onDeleteAllRecords(ActionEvent event) {
        SoundEffectsManager.playClick();

        if (allMatches == null || allMatches.isEmpty()) {
            RecordingManager.showToast(
                    "No recordings to delete",
                    matchListContainer.getScene());
            return;
        }

        Stage stage = (Stage) matchListContainer.getScene().getWindow();
        CustomAlertDialog.showConfirmation(stage, "Delete All Recordings", "Are you sure?", "This will permanently delete all recorded matches.", () -> {
            SoundEffectsManager.playClick();

            RecordingManager manager = new RecordingManager();
            manager.deleteAllRecordings(username);

            allMatches.clear();
            matchListContainer.getChildren().clear();

            RecordingManager.showToast(
                    "✅ All recordings deleted successfully",
                    matchListContainer.getScene());
        });
    }

}
