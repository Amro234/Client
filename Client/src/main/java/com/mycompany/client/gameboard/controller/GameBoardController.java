package com.mycompany.client.gameboard.controller;

import com.mycompany.client.Difficulty;
import com.mycompany.client.gameboard.model.GameMode;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GameBoardController {

    private GameMode gameMode = GameMode.TWO_PLAYERS;
    private Difficulty difficulty;
    private char playerCharacter = 'X';
    private char[][] board = new char[3][3];
    private boolean isPlayer1Turn = true;
    private boolean gameOver = false;
    private int player1Wins = 0;
    private int player2Wins = 0;
    private int draws = 0;
    private Timeline timer;
    private int timeRemaining = 20;
    private static final int TURN_TIME = 20;

    @FXML
    private StackPane cell00, cell01, cell02;
    @FXML
    private StackPane cell10, cell11, cell12;
    @FXML
    private StackPane cell20, cell21, cell22;
    private StackPane[][] cells;

    @FXML
    private Label label00, label01, label02;
    @FXML
    private Label label10, label11, label12;
    @FXML
    private Label label20, label21, label22;
    private Label[][] labels;

    @FXML
    private Button backButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button menuButton;

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
    @FXML
    private GridPane boardGrid;

    @FXML
    private VBox player1Panel;
    @FXML
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

    @FXML
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

        initializeBoard();
        attachEventHandlers();
        initializeTimer();
        updateUI();
        startTimer();
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
                labels[i][j].setText("");

                cells[i][j].getStyleClass().remove("winning-cell");
                if (!cells[i][j].getStyleClass().contains("game-cell")) {
                    cells[i][j].getStyleClass().add("game-cell");
                }

                // Fix for inconsistent cell dimensions
                labels[i][j].getStyleClass().removeAll("cell-label", "cell-label-x", "cell-label-o");
                labels[i][j].getStyleClass().add("cell-label");
            }
        }
        gameOver = false;
        isPlayer1Turn = true;
    }

    private void attachEventHandlers() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int r = i;
                final int c = j;
                cells[i][j].setOnMouseClicked(e -> handleCellClick(r, c));
            }
        }
    }

    @FXML
    public void handleCellClick(int row, int col) {
        if (gameOver || board[row][col] != ' ') {
            return;
        }

        if (gameMode == GameMode.SINGLE_PLAYER && !isPlayer1Turn) {
            return;
        }
        if (gameMode == GameMode.ONLINE && !isPlayer1Turn) {
            return;
        }
        if (gameMode == GameMode.REPLAY) {
            return;
        }

        char symbol;
        if (isPlayer1Turn) {
            symbol = playerCharacter;
        } else {
            symbol = (playerCharacter == 'X' ? 'O' : 'X');
        }

        board[row][col] = symbol;
        labels[row][col].setText(String.valueOf(symbol));

        labels[row][col].getStyleClass().removeAll("cell-label-x", "cell-label-o");
        if (symbol == 'X') {
            labels[row][col].getStyleClass().add("cell-label-x");
        } else {
            labels[row][col].getStyleClass().add("cell-label-o");
        }

        WinInfo win = checkWin(row, col, symbol);
        if (win != null) {
            gameOver = true;
            highlightWin(win);
            if (isPlayer1Turn) {
                player1Wins++;
            } else {
                player2Wins++;
            }
            updateScores();
            updateUI();
            stopTimer();
            showPlayAgainDialog((isPlayer1Turn ? "Player 1" : "Player 2") + " Wins!");

            if (gameMode == GameMode.ONLINE)
                handleOnlineMove(row + "," + col);
        } else if (isBoardFull()) {
            gameOver = true;
            draws++;
            updateScores();
            updateUI();
            stopTimer();
            showPlayAgainDialog("Draw!");

            if (gameMode == GameMode.ONLINE)
                handleOnlineMove(row + "," + col);
        } else {
            if (gameMode == GameMode.ONLINE)
                handleOnlineMove(row + "," + col);

            isPlayer1Turn = !isPlayer1Turn;
            resetTimer();
            updateUI();

            if (gameMode == GameMode.SINGLE_PLAYER && !isPlayer1Turn && !gameOver) {
                handleAITurn();
            }
        }
    }

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
        // Placeholder
    }

    @FXML
    public void handleMenuButton() {
        handleBackButton();
    }

    private WinInfo checkWin(int row, int col, char symbol) {
        if (board[row][0] == symbol && board[row][1] == symbol && board[row][2] == symbol)
            return new WinInfo(WinType.HORIZONTAL, row);

        if (board[0][col] == symbol && board[1][col] == symbol && board[2][col] == symbol)
            return new WinInfo(WinType.VERTICAL, col);

        if (row == col && board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol)
            return new WinInfo(WinType.DIAGONAL_MAIN, -1);

        if (row + col == 2 && board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol)
            return new WinInfo(WinType.DIAGONAL_ANTI, -1);

        return null;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == ' ')
                    return false;
        return true;
    }

    private void updateUI() {
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
        } else {
            if (turnIndicatorLabel != null)
                turnIndicatorLabel.setText("Player 2's Turn");

            if (player1TurnLabel != null) {
                player1TurnLabel.setText("Waiting...");
                player1TurnLabel.getStyleClass().removeAll("player-turn-label-p1", "player-turn-label-p2");
                player1TurnLabel.getStyleClass().add("player-waiting-label");
            }

            if (player2StatusLabel != null) {
                player2StatusLabel.setText("Your Turn");
                player2StatusLabel.getStyleClass().removeAll("player-waiting-label", "player-turn-label-p1");
                player2StatusLabel.getStyleClass().add("player-turn-label-p2");
            }
        }

        updateScores();

        if (player1Panel != null && player2Panel != null) {
            String blueStyle = "-fx-border-color: #4A90E2; -fx-border-width: 2; -fx-background-radius: 24; -fx-border-radius: 24; -fx-background-color: white;";
            String redStyle = "-fx-border-color: #D0021B; -fx-border-width: 2; -fx-background-radius: 24; -fx-border-radius: 24; -fx-background-color: white;";

            if (playerCharacter == 'X') {
                player1Panel.setStyle(blueStyle);
                player2Panel.setStyle(redStyle);
            } else {
                player1Panel.setStyle(redStyle);
                player2Panel.setStyle(blueStyle);
            }
        }
    }

    private void updateScores() {
        if (player1ScoreLabel != null)
            player1ScoreLabel.setText(String.valueOf(player1Wins));
        if (player2ScoreLabel != null)
            player2ScoreLabel.setText(String.valueOf(player2Wins));
        if (drawsLabel != null)
            drawsLabel.setText(String.valueOf(draws));

        if (player1WinsLabel != null)
            player1WinsLabel.setText(String.valueOf(player1Wins));
        if (player2WinsLabel != null)
            player2WinsLabel.setText(String.valueOf(player2Wins));
    }

    private void resetGame() {
        initializeBoard();
        updateUI();
        startTimer();
    }

    private void showPlayAgainDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(message);
            alert.setContentText("Do you want to play again?");

            ButtonType playAgainButton = new ButtonType("Play Again");
            ButtonType cancelButton = new ButtonType("Cancel");

            alert.getButtonTypes().setAll(playAgainButton, cancelButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == playAgainButton) {
                    resetGame();
                } else {
                    handleBackButton();
                }
            });
        });
    }

    private void initializeTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimerDisplay();

            if (timeRemaining <= 0) {
                handleTimeUp();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
    }

    private void startTimer() {
        if (gameOver)
            return;
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
        if (!gameOver) {
            timeRemaining = TURN_TIME;
            updateTimerDisplay();
            startTimer();
        }
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

    private void handleTimeUp() {
        if (gameOver)
            return;
        stopTimer();

        isPlayer1Turn = !isPlayer1Turn;
        updateUI();

        String player = isPlayer1Turn ? "Player 1" : "Player 2";
        if (turnIndicatorLabel != null)
            turnIndicatorLabel.setText("Time's up! " + player + "'s turn");

        resetTimer();
    }

    private void handleAITurn() {
    }

    private void handleOnlineMove(String move) {
    }

    private void loadReplay(Object jsonObject) {
    }

    private void highlightWin(WinInfo win) {
        switch (win.type) {
            case HORIZONTAL:
                for (int c = 0; c < 3; c++)
                    cells[win.index][c].getStyleClass().add("winning-cell");
                break;
            case VERTICAL:
                for (int r = 0; r < 3; r++)
                    cells[r][win.index].getStyleClass().add("winning-cell");
                break;
            case DIAGONAL_MAIN:
                cells[0][0].getStyleClass().add("winning-cell");
                cells[1][1].getStyleClass().add("winning-cell");
                cells[2][2].getStyleClass().add("winning-cell");
                break;
            case DIAGONAL_ANTI:
                cells[0][2].getStyleClass().add("winning-cell");
                cells[1][1].getStyleClass().add("winning-cell");
                cells[2][0].getStyleClass().add("winning-cell");
                break;
        }
    }

    private enum WinType {
        HORIZONTAL, VERTICAL, DIAGONAL_MAIN, DIAGONAL_ANTI
    }

    private static class WinInfo {
        WinType type;
        int index;

        public WinInfo(WinType type, int index) {
            this.type = type;
            this.index = index;
        }
    }

    public void startNewGame(GameMode mode, Difficulty difficulty) {
        this.gameMode = mode;
        this.difficulty = difficulty;

        // Reset scores for a new session
        player1Wins = 0;
        player2Wins = 0;
        draws = 0;

        // Default turn
        isPlayer1Turn = true;

        // Update UI labels based on mode
        if (player1NameLabel != null && player2NameLabel != null) {
            if (mode == GameMode.SINGLE_PLAYER) {
                player1NameLabel.setText("You");
                if (difficulty != null) {
                    String diffStr = difficulty.toString().charAt(0) + difficulty.toString().substring(1).toLowerCase();
                    player2NameLabel.setText("Computer (" + diffStr + ")");
                } else {
                    player2NameLabel.setText("Computer");
                }
            } else {
                player1NameLabel.setText("Player 1");
                player2NameLabel.setText("Player 2");
            }
        }

        initializeBoard();
        updateUI();
        resetTimer();
    }
}
