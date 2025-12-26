package com.mycompany.client.matches.ui;

import com.mycompany.client.matches.data.MatchData;
import com.mycompany.client.matches.data.MatchResult;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * A custom HBox component that displays a match card in the match history.
 */




public class MatchCard extends HBox {

    private final MatchData matchData;
    private Consumer<MatchData> onReplayRequested;
private Consumer<MatchData> onDeleteRequested;

    public MatchCard(MatchData matchData) {
        this.matchData = matchData;
        initializeCard();
    }

    
    public void setOnReplayRequested(Consumer<MatchData> handler) {
        this.onReplayRequested = handler;
    }

    private void initializeCard() {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(15);
        getStyleClass().add("match-card");
        setPadding(new Insets(16, 20, 16, 20));

        getChildren().addAll(
        createResultIcon(),
        createMatchInfo(),
        createSpacer(),
        createWatchReplayButton(),
        createDeleteButton()
);

    }

    private StackPane createResultIcon() {
        StackPane iconContainer = new StackPane();

        Circle background = new Circle(20);
        background.setFill(Color.valueOf(matchData.getResult().getBackgroundColor()));

        ImageView resultIcon = new ImageView();
        resultIcon.setFitWidth(20);
        resultIcon.setFitHeight(20);
        resultIcon.setPreserveRatio(true);

        try {
            resultIcon.setImage(
                new Image(getClass().getResourceAsStream(
                    matchData.getResult().getIconPath()
                ))
            );
        } catch (Exception ignored) {}

        iconContainer.getChildren().addAll(background, resultIcon);
        return iconContainer;
    }

    private VBox createMatchInfo() {
        VBox box = new VBox(4);

        HBox nameRow = new HBox(10);
        Label opponent = new Label("vs. " + matchData.getOpponent());
        opponent.getStyleClass().add("opponent-name");

        Label badge = new Label(matchData.getResult().getDisplayName());
        badge.getStyleClass().addAll(
                "result-badge",
                matchData.getResult().getBadgeStyleClass()
        );

        nameRow.getChildren().addAll(opponent, badge);

        Label date = new Label("📅 " + matchData.getDate() + " • " + matchData.getTime());
        date.getStyleClass().add("match-date");

        box.getChildren().addAll(nameRow, date);
        return box;
    }

    private Region createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

   private Button createWatchReplayButton() {

    Button btn = new Button("Watch Replay");
    btn.getStyleClass().add("watch-replay-btn");

    if (matchData.getResult() == MatchResult.CANCELLED) {
        btn.setDisable(true);
        btn.setText("Cancelled");
        btn.setOpacity(0.6);
        return btn;
    }

    btn.setOnAction(e -> {
        if (onReplayRequested != null) {
            onReplayRequested.accept(matchData);
        }
    });

    return btn;
}
public void setOnDeleteRequested(Consumer<MatchData> handler) {
    this.onDeleteRequested = handler;
}
private Button createDeleteButton() {

    Button btn = new Button("❌");

    btn.setStyle(
        "-fx-background-color: transparent;" +
        "-fx-text-fill: #dc2626;" +
        "-fx-font-size: 14px;" +
        "-fx-cursor: hand;"
    );

    btn.setOnMouseEntered(e ->
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #991b1b;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;" +
            "-fx-scale-x: 1.1;" +
            "-fx-scale-y: 1.1;"
        )
    );

    btn.setOnMouseExited(e ->
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #dc2626;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;"
        )
    );

    btn.setOnAction(e -> {
        if (onDeleteRequested != null) {
            onDeleteRequested.accept(matchData);
        }
    });

    return btn;
}


}
