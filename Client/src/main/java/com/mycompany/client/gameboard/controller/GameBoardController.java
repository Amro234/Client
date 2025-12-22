package com.mycompany.client.gameboard.controller;

import com.mycompany.client.gameboard.model.GameMode;
import com.mycompany.client.gameboard.model.Board;
import com.mycompany.client.gameboard.model.GameSession;
import com.mycompany.client.gameboard.model.ReplayGameSession;
import com.mycompany.client.gameboard.model.TwoPlayerSession;
import com.mycompany.client.match_recording.GameRecorder;
import com.mycompany.client.match_recording.GameRecording;
import com.mycompany.client.match_recording.RecordingManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

import com.mycompany.client.GameResultVideoManager.GameResultVideoManager;
import com.mycompany.client.gameboard.model.BoardMode;
import com.mycompany.client.core.navigation.NavigationService;
import com.mycompany.client.difficulty.Difficulty;

import javafx.animation.PauseTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import com.mycompany.client.gameboard.model.SinglePlayerSession;


public class GameBoardController implements GameSession.SessionListener {

    private BoardMode boardMode = BoardMode.NORMAL;

    private GameSession currentSession;
private boolean recordingStoppedManually = false;

    // UI Elements
    @FXML
    private StackPane cell00, cell01, cell02, cell10, cell11, cell12, cell20, cell21, cell22;
    private StackPane[][] cells;
    @FXML
    private Label label00, label01, label02, label10, label11, label12, label20, label21, label22;
    private Label[][] labels;

    @FXML
    private Button backButton;
    @FXML
    private Label turnIndicatorLabel;
    @FXML
    private Label player1ScoreLabel;
    @FXML
    private Label player2ScoreLabel;
    @FXML
    private Label drawsLabel;
    @FXML
    private Label timerLabel;
    private VBox player1Panel;
    private VBox player2Panel;
    @FXML
    private Label player1WinsLabel;
    @FXML
    private Label player2WinsLabel;
    @FXML
    private Label player1TurnLabel;
    @FXML
    private Label player2StatusLabel;
    @FXML
    private Label player1NameLabel;
    @FXML
    private Label player2NameLabel;

    private Timeline timer;
    private int timeRemaining = 20;
    private static final int TURN_TIME = 20;

    @FXML
    private Label matchLabel;
    @FXML
    private Button settingsButton;
    @FXML
    private Button menuButton;
    @FXML
    private Button recordGame;
    @FXML
    private Circle player1Avatar;
    @FXML
    private GridPane boardGrid;
    @FXML
    private Circle player2Avatar;

    // Recording
    private GameRecorder gameRecorder = new GameRecorder();
    private RecordingManager recordingManager = new RecordingManager();
    private boolean isRecordingEnabled = false;
    private Timeline recordingPulse;
    @FXML
    private Label replayStatusLabel;
    @FXML
    private Button replayRestartBtn;
    @FXML
    private Button replayPlayBtn;
    @FXML
    private Button replayPauseBtn;
    @FXML
    private Button replaySpeedBtn;
    private boolean isFastReplay = false;
    @FXML
    private Circle recordingIndicator;
    @FXML
private HBox recordingBox;



@FXML
private Label recordingLabel;


    

    public void initialize() {
        cells = new StackPane[][] {
                { cell00, cell01, cell02 },
                { cell10, cell11, cell12 },
                { cell20, cell21, cell22 }
        };

        labels = new Label[][] {
                { label00, label01, label02 },
                { label10, label11, label12 },
                { label20, label21, label22 }
        };

        attachEventHandlers();
        initializeTimer();
         updateRecordButtonUI(false);
    }

public void startNewGame(GameMode mode, Difficulty difficulty) {

    boardMode = BoardMode.NORMAL;
    updateUIForMode();

    if (mode == GameMode.SINGLE_PLAYER) {
        currentSession = new SinglePlayerSession(this, "Player 1", "Computer", difficulty);
    } else if (mode == GameMode.TWO_PLAYERS) {
        currentSession = new TwoPlayerSession(this, "Player 1", "Player 2");
    }

    updatePlayerNames();
    resetBoardUI();
    resetRecording();
    startTimer();
    updateTurnUI(true);
}


    private void attachEventHandlers() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int r = i;
                final int c = j;
                cells[i][j].setOnMouseClicked(e -> {
                    if (currentSession != null) {
                        currentSession.handleCellClick(r, c);
                    }
                });
            }
        }
    }

    @Override
    public void onBoardUpdate(int row, int col, char symbol) {
        if (labels != null && labels[row][col] != null) {
            labels[row][col].setText(String.valueOf(symbol));
            labels[row][col].getStyleClass().removeAll("cell-label-x", "cell-label-o");
            if (symbol == 'X') {
                labels[row][col].getStyleClass().add("cell-label-x");
            } else {
                labels[row][col].getStyleClass().add("cell-label-o");
            }
        }

        if (isRecordingEnabled) {
            gameRecorder.recordMove(row, col);
        }
    }

    @Override
    public void onGameEnd(Board.WinInfo winInfo) {
        stopTimer();
        stopTimer();

      if (isRecordingEnabled && !recordingStoppedManually) {

    String status;

    if (winInfo == null) {
        status = "DRAW";
    } else {
        status = (winInfo.winner == 'X') ? "WIN" : "LOSE";
    }

    gameRecorder.stopRecording(status);

    recordingManager.saveRecording(
            gameRecorder.getRecording(),
            currentSession.getPlayer1Name());

    isRecordingEnabled = false;
    stopRecordingIndicator();
    updateRecordButtonUI(false);
}


if (winInfo != null) {

    highlightWin(winInfo);

    boolean playerWon = winInfo.winner == 'X';

    if (currentSession instanceof SinglePlayerSession) {

        if (playerWon) {
            GameResultVideoManager.showWinVideo(
                () -> showPlayAgainDialog("You Win! 🎉")
            );
        } else {
            GameResultVideoManager.showLoseVideo(
                () -> showPlayAgainDialog("You Lost 💔")
            );
        }

    } else {
        // TWO PLAYERS
        String winnerName = playerWon
                ? currentSession.getPlayer1Name()
                : currentSession.getPlayer2Name();

        GameResultVideoManager.showWinVideo(
            () -> showPlayAgainDialog(winnerName + " Wins!")
        );
    }

} else {
    GameResultVideoManager.showDrawVideo(
        () -> showPlayAgainDialog("It's a Draw!")
    );
}


    }

    private void resetRecording() {
        isRecordingEnabled = false;
        gameRecorder = new GameRecorder();
    }

    @Override
    public void onTurnChange(boolean isPlayer1Turn) {
        updateTurnUI(isPlayer1Turn);
        resetTimer();
    }

    @Override
    public void onScoreUpdate(int p1, int p2, int draws) {
        if (player1ScoreLabel != null) {
            player1ScoreLabel.setText(String.valueOf(p1));
        }
        if (player2ScoreLabel != null) {
            player2ScoreLabel.setText(String.valueOf(p2));
        }
        if (drawsLabel != null) {
            drawsLabel.setText(String.valueOf(draws));
        }

        if (player1WinsLabel != null) {
            player1WinsLabel.setText(String.valueOf(p1));
        }
        if (player2WinsLabel != null) {
            player2WinsLabel.setText(String.valueOf(p2));
        }
    }

    // --- Private Helpers ---
    private void updatePlayerNames() {
        if (player1NameLabel != null) {
            player1NameLabel.setText(currentSession.getPlayer1Name());
        }
        if (player2NameLabel != null) {
            player2NameLabel.setText(currentSession.getPlayer2Name());
        }
    }

    private void updateTurnUI(boolean isPlayer1Turn) {
        if (isPlayer1Turn) {
            if (turnIndicatorLabel != null) {
                turnIndicatorLabel.setText("Your Turn");
            }
            if (player1TurnLabel != null) {
                player1TurnLabel.setText("Your Turn");
                player1TurnLabel.getStyleClass().removeAll("player-waiting-label", "player-turn-label-p2");
                player1TurnLabel.getStyleClass().add("player-turn-label-p1");
            }
            if (player2StatusLabel != null) {
                player2StatusLabel.setText("Waiting...");
                player2StatusLabel.getStyleClass().removeAll("player-turn-label-p1", "player-turn-label-p2");
                player2StatusLabel.getStyleClass().add("player-waiting-label");
            }
            if (player1Panel != null) {
                player1Panel.setStyle(
                        "-fx-border-color: #4A90E2; -fx-border-width: 2; -fx-background-radius: 24; -fx-border-radius: 24; -fx-background-color: white;");
            }
            if (player2Panel != null) {
                player2Panel.setStyle(
                        "-fx-border-color: #D0021B; -fx-border-width: 2; -fx-background-radius: 24; -fx-border-radius: 24; -fx-background-color: white;");
            }
        } else {
            if (turnIndicatorLabel != null) {
                turnIndicatorLabel.setText(currentSession.getPlayer2Name() + "'s Turn");
            }
            if (player1TurnLabel != null) {
                player1TurnLabel.setText("Waiting...");
                player1TurnLabel.getStyleClass().removeAll("player-turn-label-p1", "player-turn-label-p2");
                player1TurnLabel.getStyleClass().add("player-waiting-label");
            }
            if (player2StatusLabel != null) {
                player2StatusLabel.setText(currentSession.getPlayer2Name() + "'s Turn");
                player2StatusLabel.getStyleClass().removeAll("player-waiting-label", "player-turn-label-p1");
                player2StatusLabel.getStyleClass().add("player-turn-label-p2");
            }
            if (player1Panel != null) {
                player1Panel.setStyle(
                        "-fx-border-color: #D0021B; -fx-border-width: 2; -fx-background-radius: 24; -fx-border-radius: 24; -fx-background-color: white;");
            }
            if (player2Panel != null) {
                player2Panel.setStyle(
                        "-fx-border-color: #4A90E2; -fx-border-width: 2; -fx-background-radius: 24; -fx-border-radius: 24; -fx-background-color: white;");
            }
        }
    }

    private void resetBoardUI() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                labels[i][j].setText("");
                cells[i][j].getStyleClass().remove("winning-cell");
                if (!cells[i][j].getStyleClass().contains("game-cell")) {
                    cells[i][j].getStyleClass().add("game-cell");
                }
                labels[i][j].getStyleClass().removeAll("cell-label", "cell-label-x", "cell-label-o");
                labels[i][j].getStyleClass().add("cell-label");
            }
        }
    }

    private void highlightWin(Board.WinInfo win) {
        if (win.type == Board.WinType.HORIZONTAL) {
            for (int c = 0; c < 3; c++) {
                cells[win.index][c].getStyleClass().add("winning-cell");
            }
        } else if (win.type == Board.WinType.VERTICAL) {
            for (int r = 0; r < 3; r++) {
                cells[r][win.index].getStyleClass().add("winning-cell");
            }
        } else if (win.type == Board.WinType.DIAGONAL_MAIN) {
            cells[0][0].getStyleClass().add("winning-cell");
            cells[1][1].getStyleClass().add("winning-cell");
            cells[2][2].getStyleClass().add("winning-cell");
        } else if (win.type == Board.WinType.DIAGONAL_ANTI) {
            cells[0][2].getStyleClass().add("winning-cell");
            cells[1][1].getStyleClass().add("winning-cell");
            cells[2][0].getStyleClass().add("winning-cell");
        }
    }

    // --- Timer Handling ---
    private void initializeTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimerDisplay();

            if (timeRemaining <= 0) {
                if (currentSession != null) {
                    currentSession.forceSwitchTurn();
                }
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
    }

    private void startTimer() {
        timeRemaining = TURN_TIME;
        if (timerLabel != null) {
            timerLabel.getStyleClass().remove("timer-label-warning");
            if (!timerLabel.getStyleClass().contains("timer-label")) {
                timerLabel.getStyleClass().add("timer-label");
            }
        }
        updateTimerDisplay();
        timer.play();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void resetTimer() {
        stopTimer();
        startTimer();
    }

    private void updateTimerDisplay() {
        if (timerLabel != null) {
            int minutes = timeRemaining / 60;
            int seconds = timeRemaining % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

            // Force clean state
            timerLabel.getStyleClass().removeAll("timer-label", "timer-label-warning");

            if (timeRemaining <= 10) {
                timerLabel.getStyleClass().add("timer-label-warning");
            } else {
                timerLabel.getStyleClass().add("timer-label");
            }
        }
    }

    // --- Navigation ---
    @FXML
    public void handleBackButton() {
        stopTimer();
        if (currentSession instanceof ReplayGameSession) {
            ((ReplayGameSession) currentSession).stop();
        }
        NavigationService.goBack();
    }

    @FXML
    public void handleSettingsButton() {
        try {
            stopTimer();
            if (currentSession instanceof ReplayGameSession) {
                ((ReplayGameSession) currentSession).stop();
            }
            Parent root = NavigationService.loadFXML("settings");
            NavigationService.navigateTo(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleMenuButton() {
        try {
            stopTimer();
            if (currentSession instanceof ReplayGameSession) {
                ((ReplayGameSession) currentSession).stop();
            }
            Parent root = NavigationService.loadFXML("main-menu");
            NavigationService.navigateTo(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPlayAgainDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Game Over");

            if (message.contains("Draw")) {
                alert.setHeaderText("🤝 " + message + " 🤝");
                alert.setContentText("No winner this time! Want a rematch?");
            } else {
                alert.setHeaderText("🎉 " + message + " 🎉");
                alert.setContentText("Do you want to play again?");
            }

            ButtonType playAgainButton = new ButtonType("Play Again");
            ButtonType cancelButton = new ButtonType("Exit to Menu");

            alert.getButtonTypes().setAll(playAgainButton, cancelButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == playAgainButton) {
                    currentSession.resetGame();
                    resetBoardUI();
                    startTimer();
                } else {
                    handleBackButton();
                }
            });
        });
    }

@FXML
private void onRecording(ActionEvent event) {

    if (currentSession == null) {
        return;
    }

    // 🔴 START
    if (!isRecordingEnabled) {

        isRecordingEnabled = true;
        recordingStoppedManually = false;

        gameRecorder.startRecording(
                GameMode.TWO_PLAYERS,
                currentSession.getPlayer1Name(),
                currentSession.getPlayer2Name(),
                'X');

        startRecordingIndicator();
        updateRecordButtonUI(true);

        System.out.println("Recording started");
    }
    // ⛔ STOP
  else {
    // ⛔ Stop manually
    isRecordingEnabled = false;
    recordingStoppedManually = true;

    gameRecorder.stopRecording("CANCELLED");

    recordingManager.saveRecording(
            gameRecorder.getRecording(),
            currentSession.getPlayer1Name());

    stopRecordingIndicator();
    updateRecordButtonUI(false);

    RecordingManager.showToast(
        "Recording cancelled – match not saved",
        recordGame.getScene()
    );
}


}




    private void disableBoardInteraction() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j].setOnMouseClicked(e -> {
                });
            }
        }
    }

    public void startReplay(GameRecording recording) {
        isFastReplay = false;
        replaySpeedBtn.setText("Fast Speed");

        boardMode = BoardMode.REPLAY;
        updateUIForMode();

        stopTimer();
        resetBoardUI();
        disableBoardInteraction();

        replayStatusLabel.setVisible(false);
        replayStatusLabel.setManaged(false);

        currentSession = new ReplayGameSession(
                this,
                recording.playerName,
                recording.opponentPlayerName,
                recording);
        ((ReplayGameSession) currentSession).play();

    }

    @FXML
    private void onReplayPlay() {
        if (currentSession instanceof ReplayGameSession) {
            ReplayGameSession replay = (ReplayGameSession) currentSession;

            if (replay.isPlaying()) {
                replay.resume();
            } else {
                replay.play();
            }
        }
    }

    @FXML
    private void onReplayPause() {
        if (currentSession instanceof ReplayGameSession) {
            ((ReplayGameSession) currentSession).pause();
        }
    }

    @FXML
    private void onReplaySpeed() {

        if (!(currentSession instanceof ReplayGameSession)) {
            return;
        }

        ReplayGameSession replay = (ReplayGameSession) currentSession;

        if (!isFastReplay) {
            // Fast
            replay.setPlaybackSpeed(2.0);
            replaySpeedBtn.setText("🐢 Normal Speed");
            isFastReplay = true;
        } else {
            // Normal
            replay.setPlaybackSpeed(1.0);
            replaySpeedBtn.setText("⚡ Fast Speed");
            isFastReplay = false;
        }
    }

    @Override
    public void onReplayFinished() {
        Platform.runLater(() -> {
            replayStatusLabel.setText("▶ Replay Finished");
            replayStatusLabel.setVisible(true);
            replayStatusLabel.setManaged(true);
        });
    }

    @Override
    public void onReplayReset() {
        resetBoardUI();
        replayStatusLabel.setVisible(false);
        replayStatusLabel.setManaged(false);
    }

    private void updateUIForMode() {

        boolean isReplay = boardMode == BoardMode.REPLAY;

        // Replay buttons
        replayPlayBtn.setVisible(isReplay);
        replayPauseBtn.setVisible(isReplay);
        replaySpeedBtn.setVisible(isReplay);
        replayRestartBtn.setVisible(isReplay);

        replayPlayBtn.setManaged(isReplay);
        replayPauseBtn.setManaged(isReplay);
        replaySpeedBtn.setManaged(isReplay);
        replayRestartBtn.setManaged(isReplay);

        // Record
        recordGame.setVisible(!isReplay);
        recordGame.setManaged(!isReplay);

        // Timer & Turn
        timerLabel.setVisible(!isReplay);
        timerLabel.setManaged(!isReplay);

        turnIndicatorLabel.setVisible(!isReplay);
        turnIndicatorLabel.setManaged(!isReplay);
    }

    @FXML
    private void onReplayRestart(ActionEvent event) {
        isFastReplay = false;
        replaySpeedBtn.setText("⚡ Fast Speed");

        if (!(currentSession instanceof ReplayGameSession)) {
            return;
        }

        ReplayGameSession replay = (ReplayGameSession) currentSession;

        replay.stop();
        replay.reset();
        resetBoardUI();

        replayStatusLabel.setVisible(false);
        replayStatusLabel.setManaged(false);

        replay.play();
    }
private void startRecordingIndicator() {

    recordingBox.setVisible(true);
    recordingBox.setManaged(true);

    applyRecordingGlow();

    
    PauseTransition delay = new PauseTransition(Duration.millis(300));
    delay.setOnFinished(e -> startPulseAnimation());
    delay.play();
}

private void stopRecordingIndicator() {

    if (recordingPulse != null) {
        recordingPulse.stop();
    }

    recordingBox.setVisible(false);
    recordingBox.setManaged(false);

    recordingIndicator.setOpacity(1.0);
    recordingIndicator.setEffect(null);
}

private void applyRecordingGlow() {
    DropShadow glow = new DropShadow();
    glow.setRadius(8);
    glow.setColor(Color.web("#dc2626")); // Red glow
    recordingIndicator.setEffect(glow);
}
private void startPulseAnimation() {

    recordingPulse = new Timeline(
        new KeyFrame(Duration.ZERO, e -> {
            recordingIndicator.setOpacity(1.0);
            recordingLabel.setOpacity(1.0);
        }),
        new KeyFrame(Duration.seconds(0.6), e -> {
            recordingIndicator.setOpacity(0.4);
            recordingLabel.setOpacity(0.4);
        }),
        new KeyFrame(Duration.seconds(1.2), e -> {
            recordingIndicator.setOpacity(1.0);
            recordingLabel.setOpacity(1.0);
        })
    );

    recordingPulse.setCycleCount(Timeline.INDEFINITE);
    recordingPulse.play();
}
private void updateRecordButtonUI(boolean recording) {

    if (recording) {
        recordGame.setText("⏹ Stop Recording");
        recordGame.setStyle(
            "-fx-background-color: #7f1d1d;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 18;"
        );
    } else {
        recordGame.setText("⏺ Record");
        recordGame.setStyle(
            "-fx-background-color: #dc2626;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 18;"
        );
    }
}


}
