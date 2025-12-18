
package com.mycompany.client;

import java.io.IOException;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Ahmed
 */
public class GameBoardController {
    
    
    @FXML
    private Button backButton;
    
    @FXML
    private Label matchLabel;
    
    @FXML
    private Button settingsButton;
    
    @FXML
    private Button menuButton;
    
    
    @FXML
    private Circle player1Avatar;
    
    @FXML
    private Label player1NameLabel;
    
    @FXML
    private Label player1TurnLabel;
    
    @FXML
    private Label player1WinsLabel;
    
    
    @FXML
    private Label player1ScoreLabel;
    
    @FXML
    private Label drawsLabel;
    
    @FXML
    private Label player2ScoreLabel;
    
    
    @FXML
    private Label turnIndicatorLabel;
    
    
    @FXML
    private GridPane boardGrid;
    
    
    @FXML
    private Label label00, label01, label02;
    
    @FXML
    private Label label10, label11, label12;
    
    @FXML
    private Label label20, label21, label22;
    
   
    @FXML
    private StackPane cell00, cell01, cell02;
    
    @FXML
    private StackPane cell10, cell11, cell12;
    
    @FXML
    private StackPane cell20, cell21, cell22;
    
    
    @FXML
    private Label timerLabel;
    
    
    @FXML
    private Circle player2Avatar;
    
    @FXML
    private Label player2NameLabel;
    
    @FXML
    private Label player2StatusLabel;
    
    @FXML
    private Label player2WinsLabel;
    
 
    private char[][] board = new char[3][3];
    private boolean isPlayer1Turn = true;
    private boolean gameOver = false;
    private StackPane[][] cells;
    private Label[][] labels;
    
    
    private int player1Wins = 0;
    private int player2Wins = 0;
    private int draws = 0;
    
  
    private Timeline timer;
    private int timeRemaining = 20; 
    private static final int TURN_TIME = 20;
    
    @FXML
    private void initialize() {
        initializeBoard();
        
        cells = new StackPane[][]{
            {cell00, cell01, cell02},
            {cell10, cell11, cell12},
            {cell20, cell21, cell22}
        };
        
        labels = new Label[][]{
            {label00, label01, label02},
            {label10, label11, label12},
            {label20, label21, label22}
        };
        
        setupCellClickHandlers();
        
        initializeTimer();
        
        updateUI();
        
        startTimer();
    }
    
    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }
    
    private void setupCellClickHandlers() {
        cell00.setOnMouseClicked(e -> handleCellClick(0, 0));
        cell01.setOnMouseClicked(e -> handleCellClick(0, 1));
        cell02.setOnMouseClicked(e -> handleCellClick(0, 2));
        cell10.setOnMouseClicked(e -> handleCellClick(1, 0));
        cell11.setOnMouseClicked(e -> handleCellClick(1, 1));
        cell12.setOnMouseClicked(e -> handleCellClick(1, 2));
        cell20.setOnMouseClicked(e -> handleCellClick(2, 0));
        cell21.setOnMouseClicked(e -> handleCellClick(2, 1));
        cell22.setOnMouseClicked(e -> handleCellClick(2, 2));
    }
    
    private void handleCellClick(int row, int col) {
        if (gameOver || board[row][col] != ' ') {
            return;
        }
        
        char symbol = isPlayer1Turn ? 'X' : 'O';
        board[row][col] = symbol;
        
        
        Label label = labels[row][col];
        label.setText(String.valueOf(symbol));
        label.getStyleClass().removeAll("cell-label", "cell-label-x", "cell-label-o");
        if (symbol == 'X') {
            label.getStyleClass().add("cell-label-x");
        } else {
            label.getStyleClass().add("cell-label-o");
        }
        
        
        WinInfo winInfo = checkWin(row, col, symbol);
        if (winInfo != null) {
            gameOver = true;
            stopTimer(); 
            String winner = isPlayer1Turn ? "Player 1" : "Player 2";
            turnIndicatorLabel.setText(winner + " Wins!");
            
            
            if (isPlayer1Turn) {
                player1Wins++;
            } else {
                player2Wins++;
            }
            updateScores();
            
            
            Platform.runLater(() -> {
                drawWinningLine(winInfo);
                showPlayAgainDialog(winner + " Wins!");
            });
        } else if (isBoardFull()) {
            gameOver = true;
            stopTimer(); 
            turnIndicatorLabel.setText("Draw!");
            
            draws++;
            updateScores();
            
            Platform.runLater(() -> showPlayAgainDialog("It's a Draw!"));
        } else {
            isPlayer1Turn = !isPlayer1Turn;
            updateUI();
            resetTimer(); 
        }
    }
    
    private WinInfo checkWin(int row, int col, char symbol) {

        if (board[row][0] == symbol && board[row][1] == symbol && board[row][2] == symbol) {
            return new WinInfo(WinType.HORIZONTAL, row);
        }
        
      
        if (board[0][col] == symbol && board[1][col] == symbol && board[2][col] == symbol) {
            return new WinInfo(WinType.VERTICAL, col);
        }
        
       
        if (row == col && board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) {
            return new WinInfo(WinType.DIAGONAL_MAIN, -1);
        }
        
        
        if (row + col == 2 && board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol) {
            return new WinInfo(WinType.DIAGONAL_ANTI, -1);
        }
        
        return null;
    }
    
    private void drawWinningLine(WinInfo winInfo) {
        boardGrid.layout();
        
        Pane overlayPane = new Pane();
        overlayPane.setMouseTransparent(true); 
        overlayPane.prefWidthProperty().bind(boardGrid.widthProperty());
        overlayPane.prefHeightProperty().bind(boardGrid.heightProperty());
        
        Line line = new Line();
        line.setStrokeWidth(6);
        line.setStroke(javafx.scene.paint.Color.valueOf("#10B981")); 
        
        double padding = 15.0; 
        double hGap = 25.0; 
        double vGap = 30.0; 
        double cellSize = 96.0; 
        
        switch (winInfo.type) {
            case HORIZONTAL:
                int row = winInfo.index;
                StackPane firstCell = cells[row][0];
                StackPane lastCell = cells[row][2];
                
               
                double firstCellCenterX = firstCell.getBoundsInParent().getMinX() + firstCell.getBoundsInParent().getWidth() / 2;
                double lastCellCenterX = lastCell.getBoundsInParent().getMinX() + lastCell.getBoundsInParent().getWidth() / 2;
                double y = firstCell.getBoundsInParent().getMinY() + firstCell.getBoundsInParent().getHeight() / 2;
                
                double verticalOffset = -15.0;
                
                double extension = 10.0;
                line.setStartX(firstCellCenterX - extension);
                line.setEndX(lastCellCenterX + extension);
                line.setStartY(y + verticalOffset);
                line.setEndY(y + verticalOffset);
                break;
                
            case VERTICAL:
                int col = winInfo.index;
                StackPane topCell = cells[0][col];
                StackPane bottomCell = cells[2][col];
                
                double x = topCell.getBoundsInParent().getMinX() + topCell.getBoundsInParent().getWidth() / 2;
                double topCellCenterY = topCell.getBoundsInParent().getMinY() + topCell.getBoundsInParent().getHeight() / 2;
                double bottomCellCenterY = bottomCell.getBoundsInParent().getMinY() + bottomCell.getBoundsInParent().getHeight() / 2;
                
                double horizontalOffset = -15.0;
                
                double extensionV = 10.0;
                line.setStartX(x + horizontalOffset);
                line.setEndX(x + horizontalOffset);
                line.setStartY(topCellCenterY - extensionV);
                line.setEndY(bottomCellCenterY + extensionV);
                break;
                
            case DIAGONAL_MAIN:
                StackPane topLeft = cells[0][0];
                StackPane bottomRight = cells[2][2];
                
                double topLeftCenterX = topLeft.getBoundsInParent().getMinX() + topLeft.getBoundsInParent().getWidth() / 2;
                double bottomRightCenterX = bottomRight.getBoundsInParent().getMinX() + bottomRight.getBoundsInParent().getWidth() / 2;
                double topLeftCenterY = topLeft.getBoundsInParent().getMinY() + topLeft.getBoundsInParent().getHeight() / 2;
                double bottomRightCenterY = bottomRight.getBoundsInParent().getMinY() + bottomRight.getBoundsInParent().getHeight() / 2;
                
                double diagonalOffset = -15.0;
                
                double dx = bottomRightCenterX - topLeftCenterX;
                double dy = bottomRightCenterY - topLeftCenterY;
                double length = Math.sqrt(dx * dx + dy * dy);
                double unitX = dx / length;
                double unitY = dy / length;
                
                double perpX = -unitY; 
                double perpY = unitX;  
                
                
                double diagonalExtension = 10.0;
                
                line.setStartX(topLeftCenterX - unitX * diagonalExtension + perpX * diagonalOffset);
                line.setEndX(bottomRightCenterX + unitX * diagonalExtension + perpX * diagonalOffset);
                line.setStartY(topLeftCenterY - unitY * diagonalExtension + perpY * diagonalOffset);
                line.setEndY(bottomRightCenterY + unitY * diagonalExtension + perpY * diagonalOffset);
                break;
                
            case DIAGONAL_ANTI:
                StackPane topRight = cells[0][2];
                StackPane bottomLeft = cells[2][0];
                
                double topRightCenterX = topRight.getBoundsInParent().getMinX() + topRight.getBoundsInParent().getWidth() / 2;
                double bottomLeftCenterX = bottomLeft.getBoundsInParent().getMinX() + bottomLeft.getBoundsInParent().getWidth() / 2;
                double topRightCenterY = topRight.getBoundsInParent().getMinY() + topRight.getBoundsInParent().getHeight() / 2;
                double bottomLeftCenterY = bottomLeft.getBoundsInParent().getMinY() + bottomLeft.getBoundsInParent().getHeight() / 2;
                
                
                double diagonalOffset2 = -15.0;
                
               
                double dx2 = bottomLeftCenterX - topRightCenterX;
                double dy2 = bottomLeftCenterY - topRightCenterY;
                double length2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
                double unitX2 = dx2 / length2;
                double unitY2 = dy2 / length2;
                
                
                
                double perpX2 = unitY2;  
                double perpY2 = -unitX2;
                
           
                double diagonalExtension2 = 10.0;
                
                line.setStartX(topRightCenterX - unitX2 * diagonalExtension2 + perpX2 * diagonalOffset2);
                line.setEndX(bottomLeftCenterX + unitX2 * diagonalExtension2 + perpX2 * diagonalOffset2);
                line.setStartY(topRightCenterY - unitY2 * diagonalExtension2 + perpY2 * diagonalOffset2);
                line.setEndY(bottomLeftCenterY + unitY2 * diagonalExtension2 + perpY2 * diagonalOffset2);
                break;
        }
        
        overlayPane.getChildren().add(line);
        boardGrid.getChildren().add(overlayPane);
        
        highlightWinningCells(winInfo);
    }
    
    private void highlightWinningCells(WinInfo winInfo) {
        switch (winInfo.type) {
            case HORIZONTAL:
                int row = winInfo.index;
                for (int col = 0; col < 3; col++) {
                    cells[row][col].getStyleClass().remove("game-cell");
                    cells[row][col].getStyleClass().add("winning-cell");
                }
                break;
                
            case VERTICAL:
                int col = winInfo.index;
                for (int r = 0; r < 3; r++) {
                    cells[r][col].getStyleClass().remove("game-cell");
                    cells[r][col].getStyleClass().add("winning-cell");
                }
                break;
                
            case DIAGONAL_MAIN:
                cells[0][0].getStyleClass().remove("game-cell");
                cells[0][0].getStyleClass().add("winning-cell");
                cells[1][1].getStyleClass().remove("game-cell");
                cells[1][1].getStyleClass().add("winning-cell");
                cells[2][2].getStyleClass().remove("game-cell");
                cells[2][2].getStyleClass().add("winning-cell");
                break;
                
            case DIAGONAL_ANTI:
                cells[0][2].getStyleClass().remove("game-cell");
                cells[0][2].getStyleClass().add("winning-cell");
                cells[1][1].getStyleClass().remove("game-cell");
                cells[1][1].getStyleClass().add("winning-cell");
                cells[2][0].getStyleClass().remove("game-cell");
                cells[2][0].getStyleClass().add("winning-cell");
                break;
        }
    }
    
    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void updateUI() {
        if (isPlayer1Turn) {
            turnIndicatorLabel.setText("Your Turn");
            player1TurnLabel.setText("Your Turn");
            player1TurnLabel.getStyleClass().removeAll("player-waiting-label", "player-turn-label-p2");
            player1TurnLabel.getStyleClass().add("player-turn-label-p1");
            // Player 2: Waiting (gray, no background)
            player2StatusLabel.setText("Waiting...");
            player2StatusLabel.getStyleClass().removeAll("player-turn-label-p1", "player-turn-label-p2");
            player2StatusLabel.getStyleClass().add("player-waiting-label");
        } else {
            turnIndicatorLabel.setText("Player 2's Turn");
            player1TurnLabel.setText("Waiting...");
            player1TurnLabel.getStyleClass().removeAll("player-turn-label-p1", "player-turn-label-p2");
            player1TurnLabel.getStyleClass().add("player-waiting-label");
            player2StatusLabel.setText("Your Turn");
            player2StatusLabel.getStyleClass().removeAll("player-waiting-label", "player-turn-label-p1");
            player2StatusLabel.getStyleClass().add("player-turn-label-p2");
        }
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
        if (gameOver) return;
        
        timeRemaining = TURN_TIME;
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
        if (!gameOver) {
            timeRemaining = TURN_TIME;
            updateTimerDisplay();
            startTimer();
        }
    }
    
    private void updateTimerDisplay() {
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
    
    private void handleTimeUp() {
        if (gameOver) return;
        
        stopTimer();
        
        isPlayer1Turn = !isPlayer1Turn;
        updateUI();
        
        String player = isPlayer1Turn ? "Player 1" : "Player 2";
        turnIndicatorLabel.setText("Time's up! " + player + "'s turn");
        
        resetTimer();
    }
    
    private void updateScores() {
        player1ScoreLabel.setText(String.valueOf(player1Wins));
        drawsLabel.setText(String.valueOf(draws));
        player2ScoreLabel.setText(String.valueOf(player2Wins));
        
        player1WinsLabel.setText(String.valueOf(player1Wins));
        player2WinsLabel.setText(String.valueOf(player2Wins));
    }
    
    private void showPlayAgainDialog(String message) {
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
            }
        });
    }
    
    private void resetGame() {

        stopTimer();
        
        
        initializeBoard();
        gameOver = false;
        isPlayer1Turn = true;
        
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                labels[i][j].setText("");
                labels[i][j].getStyleClass().removeAll("cell-label", "cell-label-x", "cell-label-o");
                labels[i][j].getStyleClass().add("cell-label");
                cells[i][j].getStyleClass().remove("winning-cell");
                cells[i][j].getStyleClass().add("game-cell");
            }
        }
        
        boardGrid.getChildren().removeIf(node -> {
            if (node instanceof Pane) {
                Pane pane = (Pane) node;
                return pane.getChildren().stream().anyMatch(child -> child instanceof Line);
            }
            return false;
        });
        
        
        updateUI();
        
        
        startTimer();
    }
    
    @FXML
    private void handleBackButton() throws IOException {
        try{
            stopTimer();
            App.setRoot("main-menu");
        }catch (IOException ex) {
            System.getLogger(LoginController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    @FXML
    private void handleSettingsButton() {
    
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("settings.fxml"));
            Parent root = loader.load();

            Stage settingsStage = new Stage();
            settingsStage.setTitle("Settings");
            settingsStage.initOwner(settingsButton.getScene().getWindow()); 
            settingsStage.initModality(Modality.WINDOW_MODAL);              
            settingsStage.setScene(new Scene(root));
            settingsStage.show();                                          
        } catch (IOException ex) {
            System.getLogger(LoginController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);

        }
    }
    
    @FXML
    private void handleMenuButton() {
  
        try{
            stopTimer();
            App.setRoot("main-menu");
        }catch (IOException ex) {
            System.getLogger(LoginController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    
    private enum WinType {
        HORIZONTAL, VERTICAL, DIAGONAL_MAIN, DIAGONAL_ANTI
    }
    
    private static class WinInfo {
        WinType type;
        int index; 
        
        WinInfo(WinType type, int index) {
            this.type = type;
            this.index = index;
        }
    }
}
