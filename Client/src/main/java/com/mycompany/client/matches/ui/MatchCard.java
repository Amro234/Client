package com.mycompany.client.matches.ui;

import com.mycompany.client.matches.data.MatchData;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * A custom HBox component that displays a match card in the match history.
 */
public class MatchCard extends HBox {

    private final MatchData matchData;
    private Button watchReplayBtn;

    public MatchCard(MatchData matchData) {
        this.matchData = matchData;
        initializeCard();
    }

    private void initializeCard() {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(15);
        getStyleClass().add("match-card");
        setPadding(new Insets(16, 20, 16, 20));

        // Build card components
        getChildren().addAll(
                createResultIcon(),
                createMatchInfo(),
                createSpacer(),
                createScoreSection(),
                createWatchReplayButton());
    }

    private ImageView createResultIcon() {
        ImageView resultIcon = new ImageView();
        resultIcon.setFitWidth(40);
        resultIcon.setFitHeight(40);
        resultIcon.setPreserveRatio(true);

        try {
            Image image = new Image(getClass().getResourceAsStream(matchData.getResult().getIconPath()));
            resultIcon.setImage(image);
        } catch (Exception e) {
            System.err.println("Could not load icon: " + matchData.getResult().getIconPath());
        }

        return resultIcon;
    }

    private VBox createMatchInfo() {
        VBox matchInfo = new VBox(4);

        // Opponent name with result badge
        HBox nameRow = new HBox(10);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        Label opponentLabel = new Label("vs. " + matchData.getOpponent());
        opponentLabel.getStyleClass().add("opponent-name");

        Label resultBadge = new Label(matchData.getResult().getDisplayName());
        resultBadge.getStyleClass().addAll("result-badge", matchData.getResult().getBadgeStyleClass());

        nameRow.getChildren().addAll(opponentLabel, resultBadge);

        // Date and time row
        HBox dateRow = new HBox(8);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        Label dateLabel = new Label("📅 " + matchData.getDate() + "  •  " + matchData.getTime());
        dateLabel.getStyleClass().add("match-date");

        dateRow.getChildren().add(dateLabel);

        matchInfo.getChildren().addAll(nameRow, dateRow);
        return matchInfo;
    }

    private Region createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private VBox createScoreSection() {
        VBox scoreSection = new VBox(2);
        scoreSection.setAlignment(Pos.CENTER);

        Label scoreLabel = new Label("SCORE");
        scoreLabel.getStyleClass().add("score-header");

        Label scoreValue = new Label(matchData.getScore() + " pts");
        scoreValue.getStyleClass().add("score-value");

        scoreSection.getChildren().addAll(scoreLabel, scoreValue);
        return scoreSection;
    }

    private Button createWatchReplayButton() {
        watchReplayBtn = new Button("Watch Replay");
        watchReplayBtn.getStyleClass().add("watch-replay-btn");

        ImageView playIcon = new ImageView();
        playIcon.setFitWidth(14);
        playIcon.setFitHeight(14);
        playIcon.setPreserveRatio(true);

        try {
            playIcon.setImage(new Image(getClass().getResourceAsStream("/assets/images/play_button.png")));
        } catch (Exception e) {
            // Use text fallback if icon not found
        }

        watchReplayBtn.setGraphic(playIcon);
        watchReplayBtn.setOnAction(e -> onWatchReplay());

        return watchReplayBtn;
    }

    private void onWatchReplay() {
        System.out.println("Watching replay for match vs " + matchData.getOpponent());
        // TODO: Implement replay functionality
    }

    public MatchData getMatchData() {
        return matchData;
    }

    public Button getWatchReplayButton() {
        return watchReplayBtn;
    }
}
