/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.client;

import com.mycompany.client.matches.data.FilterType;
import com.mycompany.client.matches.data.MatchData;
import com.mycompany.client.matches.data.MatchResult;
import com.mycompany.client.matches.ui.MatchCard;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

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
    private ToggleButton allGamesBtn;

    @FXML
    private ToggleButton winsBtn;

    @FXML
    private ToggleButton lossesBtn;

    @FXML
    private ToggleButton drawsBtn;

    private ArrayList<MatchData> allMatches;
    private FilterType currentFilter = FilterType.ALL;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        allMatches = createDummyData();
        setupFilterButtons();
        setupSearch();
        displayMatches(allMatches);
    }

    private ArrayList<MatchData> createDummyData() {
        ArrayList<MatchData> matches = new ArrayList<>();
        matches.add(new MatchData("PlayerTwo", MatchResult.VICTORY, "Oct 24, 2023", "14:30 PM", 5));
        matches.add(new MatchData("TicTacMaster", MatchResult.DEFEAT, "Oct 23, 2023", "09:15 AM", 2));
        matches.add(new MatchData("CasualGamer_99", MatchResult.DRAW, "Oct 22, 2023", "18:45 PM", 4));
        matches.add(new MatchData("Guest_User12", MatchResult.VICTORY, "Oct 20, 2023", "11:20 AM", 3));
        matches.add(new MatchData("ProPlayer_X", MatchResult.VICTORY, "Oct 19, 2023", "16:00 PM", 7));
        matches.add(new MatchData("NewbieLearner", MatchResult.DEFEAT, "Oct 18, 2023", "20:30 PM", 1));
        matches.add(new MatchData("ChampionKing", MatchResult.DRAW, "Oct 17, 2023", "13:45 PM", 6));
        matches.add(new MatchData("RandomUser42", MatchResult.VICTORY, "Oct 16, 2023", "10:00 AM", 4));
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
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterAndDisplayMatches());
    }

    private void filterAndDisplayMatches() {
        String searchText = searchField.getText();

        List<MatchData> filteredMatches = allMatches.stream()
                .filter(match -> currentFilter.matches(match.getResult()))
                .filter(match -> match.matchesSearch(searchText))
                .collect(Collectors.toList());

        displayMatches(filteredMatches);
    }

    private void displayMatches(List<MatchData> matches) {
        matchListContainer.getChildren().clear();

        for (MatchData match : matches) {
            matchListContainer.getChildren().add(new MatchCard(match));
        }
    }
}
