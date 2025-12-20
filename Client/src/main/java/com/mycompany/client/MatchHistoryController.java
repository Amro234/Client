/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.client;

import com.mycompany.client.gameboard.controller.GameBoardController;
import com.mycompany.client.matches.data.FilterType;
import com.mycompany.client.matches.data.MatchData;
import com.mycompany.client.matches.data.MatchResult;
import com.mycompany.client.matches.ui.MatchCard;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import match_recording.GameRecording;
import match_recording.RecordingManager;

/**
 * FXML Controller class
 *
 * @author eslam
 */




public class MatchHistoryController implements Initializable {

    @FXML private VBox matchListContainer;
    @FXML private TextField searchField;
    @FXML private ToggleButton allGamesBtn, winsBtn, lossesBtn, drawsBtn;

    private ArrayList<MatchData> allMatches;
    private FilterType currentFilter = FilterType.ALL;

    private final String username = "Player 1";
    private final String recordingsPath =
            System.getProperty("user.home") + "/.tic_tac_toe/recordings";

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // ✅ تحميل الداتا مرة واحدة صح
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
        if (files == null) return matches;

        for (File file : files) {
            try {
                GameRecording rec =
                        manager.loadRecording(file.getName(), username);

                MatchResult result =
                        "WIN".equals(rec.getStatus()) ? MatchResult.VICTORY :
                        "DRAW".equals(rec.getStatus()) ? MatchResult.DRAW :
                        MatchResult.DEFEAT;

                matches.add(
                    new MatchData(
                        rec.opponentPlayerName,
                        result,
                        rec.date,                  // تاريخ حقيقي
                        rec.time,                  // وقت حقيقي
                        rec.getSteps().size(),
                        file.getName()             // اسم ملف الريكورد
                    )
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return matches;
    }

    private void setupFilterButtons() {
        allGamesBtn.setOnAction(e -> applyFilter(allGamesBtn, FilterType.ALL));
        winsBtn.setOnAction(e -> applyFilter(winsBtn, FilterType.VICTORY));
        lossesBtn.setOnAction(e -> applyFilter(lossesBtn, FilterType.DEFEAT));
        drawsBtn.setOnAction(e -> applyFilter(drawsBtn, FilterType.DRAW));
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
                (obs, oldVal, newVal) -> filterAndDisplayMatches()
        );
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
    try {
        RecordingManager manager = new RecordingManager();
        GameRecording recording =
                manager.loadRecording(match.getRecordingFileName(), username);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/mycompany/client/game_board.fxml")
        );

        Parent root = loader.load();
        GameBoardController controller = loader.getController();
        controller.startReplay(recording);

        Stage stage = (Stage) matchListContainer.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void displayMatches(List<MatchData> matches) {
    matchListContainer.getChildren().clear();

    for (MatchData match : matches) {
        MatchCard card = new MatchCard(match);

        // 🔥 هنا السحر
        card.setOnReplayRequested(this::openReplay);

        matchListContainer.getChildren().add(card);
    }
}

}

