/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mohamed_Ali
 */
public class GameLobbyController implements Initializable {

    private FilteredList<Player> filteredPlayers;

    @FXML
    private Button logOutBtn;
    @FXML
    private Button settingsBtn;
    @FXML
    private Button quickPlayBtn;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TableView<Player> playerTable;
    @FXML
    private TableColumn<Player, String> playerColumn;
    @FXML
    private TableColumn<Player, Integer> rankColumn;
    @FXML
    private TableColumn<Player, String> statusColumn;
    @FXML
    private TableColumn<Player, Void> actionColumn;
    private ObservableList<Player> playerData = FXCollections.observableArrayList();
    @FXML
    private TextField searchField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

      
        statusComboBox.getItems().addAll("ALL", "READY", "IN GAME", "AWAY");
        statusComboBox.setValue("ALL");

        playerColumn.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadPlayersData();

        filteredPlayers = new FilteredList<>(playerData, p -> true);
        playerTable.setItems(filteredPlayers);

        // listeners(Search + ComboBox)
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
        statusComboBox.valueProperty().addListener((obs, o, n) -> applyFilters());

        // Player Column 
        playerColumn.setCellFactory(column -> new TableCell<Player, String>() {

            private ImageView imageView = new ImageView();
            private Label label = new Label();
            private HBox box = new HBox(10, imageView, label);

            {
                imageView.setFitWidth(32);
                imageView.setFitHeight(32);
                box.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Player p = getTableView().getItems().get(getIndex());
                    imageView.setImage(
                            new Image(getClass().getResourceAsStream("/assets/images/" + p.getAvatar()))
                    );
                    label.setText(item);
                    setGraphic(box);
                }
            }
        });

        // Status Column 
        statusColumn.setCellFactory(column -> new TableCell<Player, String>() {

            private Circle dot = new Circle(5);
            private Label label = new Label();
            private HBox box = new HBox(8, dot, label);

            {
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Player p = getTableView().getItems().get(getIndex());
                    dot.setStyle("-fx-fill: " + p.getStatusColor());
                    label.setText(item);
                    setGraphic(box);
                }
            }
        });

        // Rank Column
        rankColumn.setCellFactory(column -> new TableCell<Player, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Action Column 
        actionColumn.setCellFactory(column -> new TableCell<Player, Void>() {

            private Button btn = new Button();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Player p = getTableView().getItems().get(getIndex());
                    btn.setText(
                            p.getStatus().equals("READY") ? "Challenge"
                            : p.getStatus().equals("IN GAME") ? "Spectate" : "Invite"
                    );
                    btn.getStyleClass().add("action-button");
                    setGraphic(btn);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Row Height 
        playerTable.setRowFactory(tv -> {
            TableRow<Player> row = new TableRow<>();
            row.setPrefHeight(70);
            return row;
        });
    }

    
    private void applyFilters() {
        String searchText = searchField.getText();
        String selectedStatus = statusComboBox.getValue();

        filteredPlayers.setPredicate(player -> {

            boolean matchesSearch = true;
            boolean matchesStatus = true;

            if (searchText != null && !searchText.isBlank()) {
                matchesSearch = player.getPlayerName()
                        .toLowerCase()
                        .contains(searchText.toLowerCase());
            }

            if (selectedStatus != null && !"ALL".equals(selectedStatus)) {
                matchesStatus = player.getStatus().equalsIgnoreCase(selectedStatus);
            }

            return matchesSearch && matchesStatus;
        });
    }

    private void loadPlayersData() {
        playerData.add(new Player("PlayerOne", 1200, "READY", "avatar1.png", "#10B981"));
        playerData.add(new Player("PlayerTwo", 1150, "IN GAME", "avatar2.png", "#f59e0b"));
        playerData.add(new Player("PlayerThree", 1300, "AWAY", "avatar3.png", "#94a3b8"));
        playerData.add(new Player("PlayerFour", 980, "READY", "avatar4.png", "#10B981"));
        playerData.add(new Player("PlayerFive", 1050, "READY", "avatar5.png", "#10B981"));
    }

    
    
    @FXML
    private void onLogOutPressed(ActionEvent event) {
        showConfirmationDialog("Info", "Are you sure you want to leave the app?");
    }

    private void showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText("Choose your option:");

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            showGoodbyeMessage();
        }
    }

    private void showGoodbyeMessage() {
        Alert goodbyeAlert = new Alert(Alert.AlertType.INFORMATION);
        goodbyeAlert.setTitle("Goodbye");
        goodbyeAlert.setHeaderText("Thank you for using the app!");
        goodbyeAlert.setContentText("We hope to see you again soon.");
        goodbyeAlert.showAndWait();

        Platform.exit();
    }
   
    @FXML
    private void onSettingsPressed(ActionEvent event) throws IOException {

        Parent secondScreenParent = FXMLLoader.load(getClass().getResource("/com/mycompany/client/settings.fxml"));
        Scene secondScene = new Scene(secondScreenParent);
        Stage currentStage = (Stage) settingsBtn.getScene().getWindow();
        currentStage.setScene(secondScene);
        currentStage.show();
    }

    @FXML
    private void onQuickPlayPressed(ActionEvent event) throws IOException {
        Parent secondScreenParent = FXMLLoader.load(getClass().getResource("/com/mycompany/client/game_board.fxml"));
        Scene secondScene = new Scene(secondScreenParent);
        Stage currentStage = (Stage) settingsBtn.getScene().getWindow();
        currentStage.setScene(secondScene);
        currentStage.show();
    }
}
