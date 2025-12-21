package com.mycompany.client.gameLobby.controller;

import com.mycompany.client.core.server.ServerConnection;
import com.mycompany.client.gameLobby.enums.PlayerStatus;
import com.mycompany.client.gameLobby.networking.GameLobbyClient;
import com.mycompany.client.gameLobby.networking.exception.GameLobbyException;
import com.mycompany.client.gameLobby.networking.model.user.OnlineUser;
import com.mycompany.client.gameLobby.networking.model.user.OnlineUsersResponse;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class GameLobbyController implements Initializable {

    private FilteredList<OnlineUser> filteredPlayers;

    @FXML
    private Button logOutBtn;
    @FXML
    private Button settingsBtn;
    @FXML
    private Button quickPlayBtn;

    @FXML
    private ComboBox<PlayerStatus> statusComboBox;

    @FXML
    private TableView<OnlineUser> playerTable;
    @FXML
    private TableColumn<OnlineUser, String> playerColumn;
    @FXML
    private TableColumn<OnlineUser, PlayerStatus> statusColumn;
    @FXML
    private TableColumn<OnlineUser, Void> actionColumn;

    @FXML
    private TextField searchField;

    private final ObservableList<OnlineUser> playerData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // ComboBox
        statusComboBox.getItems().add(null); // ALL
        statusComboBox.getItems().addAll(PlayerStatus.values());
        statusComboBox.setValue(null);

        statusComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(PlayerStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "ALL" : item.getDisplayName());
            }
        });
        statusComboBox.setButtonCell(statusComboBox.getCellFactory().call(null));

        // Columns
        playerColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        statusColumn.setCellValueFactory(cellData -> {
            PlayerStatus status = cellData.getValue().isInGame() ? PlayerStatus.IN_GAME : PlayerStatus.READY;
            return new javafx.beans.property.SimpleObjectProperty<>(status);
        });

        // Load data BEFORE starting listener (to avoid race condition)
        loadPlayersData();

        // Start server message listener AFTER loading initial data
        ServerConnection.startMessageListener();

        filteredPlayers = new FilteredList<>(playerData, p -> true);
        playerTable.setItems(filteredPlayers);

        // Filters
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
        statusComboBox.valueProperty().addListener((obs, o, n) -> applyFilters());

        // Player Column
        playerColumn.setCellFactory(column -> new TableCell<>() {

            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final HBox box = new HBox(10, imageView, label);

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
                    OnlineUser user = getTableView().getItems().get(getIndex());
                    // Use default avatar for now
                    imageView.setImage(
                            new Image(getClass().getResourceAsStream("/assets/images/avatar1.png")));
                    label.setText(item);
                    setGraphic(box);
                    setAlignment(Pos.CENTER_LEFT);
                    setPadding(new Insets(0, 0, 0, -10));
                }
            }
        });

        // Status Column
        statusColumn.setCellFactory(column -> new TableCell<>() {

            private final Circle dot = new Circle(5);
            private final Label label = new Label();
            private final HBox box = new HBox(8, dot, label);

            {
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(PlayerStatus status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    dot.setStyle("-fx-fill: " + status.getColor());
                    label.setText(status.getDisplayName());
                    setGraphic(box);
                }
            }
        });

        // Action Column
        actionColumn.setCellFactory(column -> new TableCell<>() {

            private final Button btn = new Button();
            private final Region spacer = new Region();
            private final HBox box = new HBox(10, spacer, btn);

            {
                HBox.setHgrow(spacer, Priority.ALWAYS);
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    OnlineUser user = getTableView().getItems().get(getIndex());

                    btn.setText(
                            user.isInGame() ? "Spectate" : "Challenge");

                    btn.getStyleClass().add("action-button");

                    setGraphic(box);
                }
            }
        });

        // Row Height
        playerTable.setRowFactory(tv -> {
            TableRow<OnlineUser> row = new TableRow<>();
            row.setPrefHeight(70);
            return row;
        });
    }

    // Filtering Logic
    private void applyFilters() {
        String searchText = searchField.getText();
        PlayerStatus selectedStatus = statusComboBox.getValue();

        filteredPlayers.setPredicate(user -> {

            boolean matchesSearch = searchText == null || searchText.isBlank()
                    || user.getUsername().toLowerCase().contains(searchText.toLowerCase());

            PlayerStatus userStatus = user.isInGame() ? PlayerStatus.IN_GAME : PlayerStatus.READY;
            boolean matchesStatus = selectedStatus == null || userStatus == selectedStatus;

            return matchesSearch && matchesStatus;
        });
    }

    // Data
    private void loadPlayersData() {
        try {

            OnlineUsersResponse response = GameLobbyClient.getOnlineUsers();
            playerData.clear();
            playerData.addAll(response.getUsers());
            System.out.println("Loaded " + response.getCount() + " online users");
        } catch (GameLobbyException e) {
            System.err.println("Failed to load online users: " + e.getMessage());
            // Show error to user
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to load online players");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });
        }
    }

    // Buttons
    @FXML
    private void onLogOutPressed(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void onSettingsPressed(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/client/settings.fxml"));
        Stage stage = (Stage) settingsBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void onQuickPlayPressed(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/mycompany/client/game_board.fxml"));
        Stage stage = (Stage) quickPlayBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
