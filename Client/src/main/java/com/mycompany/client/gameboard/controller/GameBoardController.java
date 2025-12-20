package com.mycompany.client.gameboard.controller;

import com.mycompany.client.Difficulty;
import com.mycompany.client.gameboard.model.GameMode;
import com.mycompany.client.gameboard.model.Board;
import com.mycompany.client.gameboard.model.GameSession;
import com.mycompany.client.gameboard.model.ReplayGameSession;
import com.mycompany.client.gameboard.model.TwoPlayerSession;
import java.io.File;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import match_recording.GameRecorder;
import match_recording.GameRecording;
import match_recording.RecordingManager;
import com.mycompany.client.GameResultVideoManager.GameResultVideoManager;
import com.mycompany.client.gameboard.model.SinglePlayerSession;

public class GameBoardController implements GameSession.SessionListener {

    private GameSession currentSession;

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
    }

    public void startNewGame(GameMode mode, Difficulty difficulty) {
        if (mode == GameMode.SINGLE_PLAYER) {
            currentSession = new SinglePlayerSession(this, "Player 1", "Computer",difficulty);
            
        } else if (mode == GameMode.TWO_PLAYERS) {
            currentSession = new TwoPlayerSession(this, "Player 1", "Player 2");
        } else if (mode == GameMode.ONLINE) {
        
        } else if (mode == GameMode.REPLAY) {
          
        } else {
            currentSession = new TwoPlayerSession(this, "Player 1", "Player 2");
        }

        updatePlayerNames();
        resetBoardUI();
        resetRecording();
        startTimer();
        updateTurnUI(true); // Always start with P1
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
      if (isRecordingEnabled) {
        String status = (winInfo == null) ? "DRAW" : "WIN";

        gameRecorder.stopRecording(status);
        recordingManager.saveRecording(
                gameRecorder.getRecording(),
                currentSession.getPlayer1Name()
        );

        isRecordingEnabled = false;
    }

  if (winInfo != null) {
    highlightWin(winInfo);
    String winnerName = (winInfo.winner == 'X')
            ? currentSession.getPlayer1Name()
            : currentSession.getPlayer2Name();

    GameResultVideoManager.showWinVideo(
            () -> showPlayAgainDialog(winnerName + " Wins!")
    );

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
    }

    // --- Private Helpers ---

    private void updatePlayerNames() {
        if (player1NameLabel != null)
            player1NameLabel.setText(currentSession.getPlayer1Name());
        if (player2NameLabel != null)
            player2NameLabel.setText(currentSession.getPlayer2Name());
    }

    private void updateTurnUI(boolean isPlayer1Turn) {
        if (isPlayer1Turn) {
            if (turnIndicatorLabel != null)
                turnIndicatorLabel.setText("Your Turn");
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
            if (player1Panel != null)
                player1Panel.setStyle(
                        "-fx-border-color: #4A90E2; -fx-border-width: 2; -fx-background-radius: 24; -fx-border-radius: 24; -fx-background-color: white;");
            if (player2Panel != null)
                player2Panel.setStyle(
                        "-fx-border-color: #D0021B; -fx-border-width: 2; -fx-background-radius: 24; -fx-border-radius: 24; -fx-background-color: white;");
        } else {
            if (turnIndicatorLabel != null)
                turnIndicatorLabel.setText(currentSession.getPlayer2Name() + "'s Turn");
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
            if (player1Panel != null)
                player1Panel.setStyle(
                        "-fx-border-color: #D0021B; -fx-border-width: 2; -fx-background-radius: 24; -fx-border-radius: 24; -fx-background-color: white;");
            if (player2Panel != null)
                player2Panel.setStyle(
                        "-fx-border-color: #4A90E2; -fx-border-width: 2; -fx-background-radius: 24; -fx-border-radius: 24; -fx-background-color: white;");
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
            for (int c = 0; c < 3; c++)
                cells[win.index][c].getStyleClass().add("winning-cell");
        } else if (win.type == Board.WinType.VERTICAL) {
            for (int r = 0; r < 3; r++)
                cells[r][win.index].getStyleClass().add("winning-cell");
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
                stopTimer();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
    }

    private void startTimer() {
        timeRemaining = TURN_TIME;
        updateTimerDisplay();
        timer.play();
    }

    private void stopTimer() {
        if (timer != null)
            timer.stop();
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

            if (timeRemaining <= 10) {
                timerLabel.getStyleClass().remove("timer-label");
                timerLabel.getStyleClass().add("timer-label-warning");
            } else {
                timerLabel.getStyleClass().remove("timer-label-warning");
                timerLabel.getStyleClass().add("timer-label");
            }
        }
    }

    // --- Navigation ---
    @FXML
    public void handleBackButton() {
        try {
            stopTimer();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/client/main-menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSettingsButton() {
    }

    @FXML
    public void handleMenuButton() {
        handleBackButton();
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
        System.out.println("No active game to record");
        return;
    }

    
    if (isRecordingEnabled) {
        System.out.println("Recording already started");
        return;
    }

    isRecordingEnabled = true;

    gameRecorder.startRecording(
            GameMode.TWO_PLAYERS,
            currentSession.getPlayer1Name(),
            currentSession.getPlayer2Name(),
            'X' 
    );

    System.out.println("Recording started successfully");
}



private void disableBoardInteraction() {
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            cells[i][j].setOnMouseClicked(e -> {});
        }
    }
}

public void startReplay(GameRecording recording) {

    resetBoardUI();
    disableBoardInteraction();

    currentSession = new ReplayGameSession(
            this,
            recording.playerName,
            recording.opponentPlayerName,
            recording
    );
((ReplayGameSession) currentSession).play();
  
}

@FXML
private void onReplayPlay() {
    if (currentSession instanceof ReplayGameSession) {
        ((ReplayGameSession) currentSession).play();
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
    if (currentSession instanceof ReplayGameSession) {
        ((ReplayGameSession) currentSession).setPlaybackSpeed(2.0);
        ((ReplayGameSession) currentSession).stop();
        ((ReplayGameSession) currentSession).play();
    }
}


}
