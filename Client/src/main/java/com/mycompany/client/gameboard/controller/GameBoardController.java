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
import javafx.scene.layout.HBox;

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
            currentSession = new SinglePlayerSession(this, "You", "Computer", difficulty);
        }

        updatePlayerNames();
        resetBoardUI();
        resetRecording();
        startTimer();
        updateTurnUI(true);
    }

    public void startLocalTwoPlayerGame(String p1Name, String p2Name) {
        boardMode = BoardMode.NORMAL;
        updateUIForMode();

        currentSession = new TwoPlayerSession(this, p1Name, p2Name);

        updatePlayerNames();
        resetBoardUI();
        resetRecording();
        startTimer();
        updateTurnUI(true);
    }

    public void startOnlineGame(String opponentName, String mySymbol) {
        boardMode = BoardMode.NORMAL;
        updateUIForMode();

        currentSession = new com.mycompany.client.gameboard.model.ClientOnlineSession(
                this, "You", opponentName, mySymbol);

        // UI Setup
        if ("X".equals(mySymbol)) {
            player1NameLabel.setText("You (X)");
            player2NameLabel.setText(opponentName + " (O)");
        } else {
            player1NameLabel.setText(opponentName + " (X)");
            player2NameLabel.setText("You (O)");
        }

        resetBoardUI();
        resetRecording();
        startTimer();
        updateTurnUI(true); // Always X starts
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
                            () -> showPlayAgainDialog("You WIN "));

                } else {
                    GameResultVideoManager.showLoseVideo(
                            () -> showPlayAgainDialog("You Lost 💔"));

                }

            } else if (currentSession instanceof com.mycompany.client.gameboard.model.ClientOnlineSession) {
                // Online Session
                boolean iAmX = ((com.mycompany.client.gameboard.model.ClientOnlineSession) currentSession).getMySymbol()
                        .equals("X");
                boolean iWon = (iAmX && playerWon) || (!iAmX && !playerWon);

                if (iWon) {
                    GameResultVideoManager.showWinVideo(
                            () -> showOnlineGameEndDialog("You Win!", true));
                } else {
                    GameResultVideoManager.showLoseVideo(
                            () -> showOnlineGameEndDialog("You Lost", false));
                }
            } else {
                // TWO PLAYERS
                String winnerName = playerWon
                        ? currentSession.getPlayer1Name()
                        : currentSession.getPlayer2Name();

                GameResultVideoManager.showWinVideo(
                        () -> showPlayAgainDialog(winnerName + " Wins!"));
            }

        } else {
            // Draw
            if (currentSession instanceof com.mycompany.client.gameboard.model.ClientOnlineSession) {
                GameResultVideoManager.showDrawVideo(
                        () -> showOnlineGameEndDialog("It's a Draw!", false));
            } else {
                GameResultVideoManager.showDrawVideo(
                        () -> showPlayAgainDialog("It's a Draw!"));
            }
        }

    }

    private Alert activeDialog;
    private boolean isGameEnded = false;

    private void closeActiveDialog() {
        if (activeDialog != null) {
            activeDialog.setResult(ButtonType.CLOSE);
            activeDialog.close();
            activeDialog = null;
        }
    }

    // --- Online Game End Dialog with Rematch ---
    private void showOnlineGameEndDialog(String title, boolean isWin) {
        Platform.runLater(() -> {
            isGameEnded = true;
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(title);
            alert.setContentText("What would you like to do?");

            ButtonType lobbyButton = new ButtonType("Back to Lobby");
            ButtonType rematchButton = new ButtonType("Rematch");

            alert.getButtonTypes().setAll(rematchButton, lobbyButton);

            activeDialog = alert;

            alert.showAndWait().ifPresent(response -> {
                if (response == lobbyButton) {
                    handleBackButton(); // Disconnects session and goes back
                } else if (response == rematchButton) {
                    if (currentSession instanceof com.mycompany.client.gameboard.model.ClientOnlineSession) {
                        ((com.mycompany.client.gameboard.model.ClientOnlineSession) currentSession).requestRematch();
                        // Show waiting tooltip or toast?
                        com.mycompany.client.match_recording.RecordingManager.showToast("Rematch request sent...",
                                backButton.getScene());
                        // Keep dialog open? No, effectively standard flows might suggest waiting.
                        // But actually, showing "Waiting for opponent..." would be better.
                        // For now we just close or keep it?
                        // The user said: "when one send play again close play again to the second"
                        // This implies the Sender is waiting. The current flow closes the dialog for
                        // sender?
                        // Actually showAndWait closes it.
                    }
                }
                activeDialog = null;
            });
        });
    }

    @Override
    public void onRematchRequested() {
        Platform.runLater(() -> {
            closeActiveDialog(); // Close "Game Over" dialog if open

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Rematch Request");
            alert.setHeaderText("Opponent wants a rematch!");
            alert.setContentText("Do you accept?");

            ButtonType acceptBtn = new ButtonType("Accept");
            ButtonType declineBtn = new ButtonType("Decline");

            alert.getButtonTypes().setAll(acceptBtn, declineBtn);

            activeDialog = alert;

            alert.showAndWait().ifPresent(response -> {
                if (currentSession instanceof com.mycompany.client.gameboard.model.ClientOnlineSession) {
                    com.mycompany.client.gameboard.model.ClientOnlineSession onlineSession = (com.mycompany.client.gameboard.model.ClientOnlineSession) currentSession;

                    if (response == acceptBtn) {
                        onlineSession.acceptRematch();
                        isGameEnded = false;
                    } else {
                        onlineSession.declineRematch();
                        handleBackButton();
                    }
                }
                activeDialog = null;
            });
        });
    }

    @Override
    public void onRematchDeclined() {
        Platform.runLater(() -> {
            closeActiveDialog();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rematch Declined");
            alert.setHeaderText("Opponent declined rematch.");
            alert.showAndWait();
            handleBackButton();
        });
    }

    public void onOpponentLeft(String status) {
        Platform.runLater(() -> {
            closeActiveDialog();

            if (isGameEnded) {
                // Game over, opponent left -> Just go back
                com.mycompany.client.match_recording.RecordingManager.showToast("Opponent left the session.",
                        backButton.getScene());
                handleBackButton();
            } else {
                // Game in progress -> Win by default logic
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Game Over");
                if ("OPPONENT_DISCONNECTED".equals(status)) {
                    alert.setHeaderText("Opponent Disconnected!");
                } else {
                    alert.setHeaderText("Opponent Left the Game!");
                }
                alert.setContentText("You win by default.");
                alert.showAndWait();
                handleBackButton();
            }
        });
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
        Platform.runLater(() -> {
            if (player1ScoreLabel != null)
                player1ScoreLabel.setText(String.valueOf(p1));
            if (player2ScoreLabel != null)
                player2ScoreLabel.setText(String.valueOf(p2));
            if (drawsLabel != null)
                drawsLabel.setText(String.valueOf(draws));
            if (player1WinsLabel != null)
                player1WinsLabel.setText(String.valueOf(p1));
            if (player2WinsLabel != null)
                player2WinsLabel.setText(String.valueOf(p2));
        });
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
                if (currentSession != null) {
                    turnIndicatorLabel.setText(currentSession.getPlayer1Name() + "'s Turn");
                } else {
                    turnIndicatorLabel.setText("Player 1 Turn");
                }
            }
            if (player1TurnLabel != null) {
                player1TurnLabel.setText("Turn");
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
                if (currentSession != null) {
                    turnIndicatorLabel.setText(currentSession.getPlayer2Name() + "'s Turn");
                }
            }
            if (player1TurnLabel != null) {
                player1TurnLabel.setText("Waiting...");
                player1TurnLabel.getStyleClass().removeAll("player-turn-label-p1", "player-turn-label-p2");
                player1TurnLabel.getStyleClass().add("player-waiting-label");
            }
            if (player2StatusLabel != null) {
                player2StatusLabel.setText("Turn");
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

    @FXML
    public void handleBackButton() {
        stopTimer();
        if (currentSession != null) {
            currentSession.stop();
        }

        if (currentSession instanceof com.mycompany.client.gameboard.model.ClientOnlineSession) {
            try {
                // Determine CSS files to load? GameLobby usually doesn't apply dynamic CSS args
                // in NavigationService calls in other places?
                // Looking at GameLobbyController, it seems standard.
                // NavigationService.loadFXML("gameLobby") should be enough if styles are
                // attached in FXML or default.
                // But wait, does gameLobby.fxml have stylesheets?
                // Usually good to just loadFXML.
                Parent root = NavigationService.loadFXML("gameLobby");
                NavigationService.goBackAndReplace(root);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                // Fallback
                NavigationService.goBack();
            }
        } else {
            NavigationService.goBack();
        }
    }

    @FXML
    public void handleSettingsButton() {
        try {
            // We NO LONGER stop the timer or session here.
            // This allows users to change settings (like mute music) without ending the
            // game.
            // For online games, the match continues in the background.
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
            if (currentSession != null) {
                currentSession.stop();
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
        if (!isRecordingEnabled) {
            isRecordingEnabled = true;
            recordingStoppedManually = false;
            gameRecorder.startRecording(GameMode.TWO_PLAYERS, currentSession.getPlayer1Name(),
                    currentSession.getPlayer2Name(), 'X');
            startRecordingIndicator();
            updateRecordButtonUI(true);
        } else {
            isRecordingEnabled = false;
            recordingStoppedManually = true;
            gameRecorder.stopRecording("CANCELLED");
            stopRecordingIndicator();
            updateRecordButtonUI(false);
            RecordingManager.showToast("Recording cancelled", recordGame.getScene());
        }
    }

    // Replay methods (omitted for brevity, can be copied from existing or left as
    // is if not changing)
    public void startReplay(GameRecording recording) {
        isFastReplay = false;
        replaySpeedBtn.setText("Fast Speed");
        boardMode = BoardMode.REPLAY;
        updateUIForMode();
        stopTimer();
        resetBoardUI();
        currentSession = new ReplayGameSession(this, recording.playerName, recording.opponentPlayerName, recording);
        ((ReplayGameSession) currentSession).play();
    }

    @FXML
    private void onReplayPlay() {
        if (currentSession instanceof ReplayGameSession)
            ((ReplayGameSession) currentSession).play();
    }

    @FXML
    private void onReplayPause() {
        if (currentSession instanceof ReplayGameSession)
            ((ReplayGameSession) currentSession).pause();
    }

    @FXML
    private void onReplaySpeed() {
        ReplayGameSession s = (ReplayGameSession) currentSession;
        if (s == null)
            return;
        if (isFastReplay) {
            s.setPlaybackSpeed(1.0);
            isFastReplay = false;
            replaySpeedBtn.setText("⚡ Fast Speed");
        } else {
            s.setPlaybackSpeed(2.0);
            isFastReplay = true;
            replaySpeedBtn.setText("🐢 Normal Speed");
        }
    }

    @FXML
    private void onReplayRestart(ActionEvent e) {
        startReplay(((ReplayGameSession) currentSession).getRecording());
    }

    @Override
    public void onReplayFinished() {
        Platform.runLater(() -> {
            replayStatusLabel.setVisible(true);
        });
    }

    @Override
    public void onSessionReset() {
        resetBoardUI();
        replayStatusLabel.setVisible(false);
    }

    private void updateUIForMode() {
        boolean isReplay = boardMode == BoardMode.REPLAY;
        replayPlayBtn.setVisible(isReplay);
        replayPauseBtn.setVisible(isReplay);
        replaySpeedBtn.setVisible(isReplay);
        replayRestartBtn.setVisible(isReplay);
        replayPlayBtn.setManaged(isReplay);
        replayPauseBtn.setManaged(isReplay);
        replaySpeedBtn.setManaged(isReplay);
        replayRestartBtn.setManaged(isReplay);
        recordGame.setVisible(!isReplay);
        recordGame.setManaged(!isReplay);
        timerLabel.setVisible(!isReplay);
        timerLabel.setManaged(!isReplay);
        turnIndicatorLabel.setVisible(!isReplay);
        turnIndicatorLabel.setManaged(!isReplay);
    }

    private void startRecordingIndicator() {
        recordingBox.setVisible(true);
        recordingBox.setManaged(true);
    }

    private void stopRecordingIndicator() {
        recordingBox.setVisible(false);
        recordingBox.setManaged(false);
    }

    private void updateRecordButtonUI(boolean recording) {
        recordGame.setText(recording ? "⏹ Stop" : "⏺ Record");
    }
}
