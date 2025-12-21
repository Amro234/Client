package com.mycompany.client.gameLobby.controller;

import com.mycompany.client.auth.model.User;
import com.mycompany.client.core.navigation.NavigationService;
import com.mycompany.client.core.server.ServerConnection;
import com.mycompany.client.core.session.UserSession;
import com.mycompany.client.gameLobby.controller.uicomponents.ActionTableCell;
import com.mycompany.client.gameLobby.controller.uicomponents.PlayerTableCell;
import com.mycompany.client.gameLobby.controller.uicomponents.StatusTableCell;
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
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;

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

    @FXML
    private Circle profileAvatar;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button lobbyBtn;
    @FXML
    private Button statsBtn;
    @FXML
    private Button friendsBtn;
    @FXML
    private Label titleLabel;
    @FXML
    private Circle onlineDot;
    @FXML
    private Label onlineCountLabel;
    @FXML
    private Circle matchesDot;
    @FXML
    private Label matchesCountLabel;

    private final ObservableList<OnlineUser> playerData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateUserProfile();
        setupStatusComboBox();
        setupTableColumns();
        loadPlayersData();
        ServerConnection.startMessageListener();
        setupFilters();
        setupRowHeight();
    }

    private void setupStatusComboBox() {
        statusComboBox.getItems().add(null);
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
    }

    private void setupTableColumns() {
        playerColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        statusColumn.setCellValueFactory(cellData -> {
            PlayerStatus status = cellData.getValue().getStatus();
            return new javafx.beans.property.SimpleObjectProperty<>(status);
        });

        playerColumn.setCellFactory(column -> new PlayerTableCell());
        statusColumn.setCellFactory(column -> new StatusTableCell());
        actionColumn.setCellFactory(column -> new ActionTableCell());
    }

    private void setupFilters() {
        filteredPlayers = new FilteredList<>(playerData, p -> true);
        playerTable.setItems(filteredPlayers);

        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
        statusComboBox.valueProperty().addListener((obs, o, n) -> applyFilters());
    }

    private void setupRowHeight() {
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

            PlayerStatus userStatus = user.getStatus();
            boolean matchesStatus = selectedStatus == null || userStatus == selectedStatus;

            return matchesSearch && matchesStatus;
        });
    }

    // Update profile section with current user data
    private void updateUserProfile() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
            scoreLabel.setText(currentUser.getScore() + " pts");
        }
    }

    // Data
    private void loadPlayersData() {
        try {
            User currentUser = UserSession.getInstance().getCurrentUser();
            int currentUserId = currentUser != null ? currentUser.getId() : -1;

            OnlineUsersResponse response = GameLobbyClient.getOnlineUsers();
            playerData.clear();

            for (OnlineUser user : response.getUsers()) {
                if (user.getUserId() != currentUserId) {
                    playerData.add(user);
                }
            }
            onlineCountLabel.setText( " "+String.valueOf(response.getCount())+ " Online");
            System.out.println(
                    "Loaded " + playerData.size() + " other players (total online: " + response.getCount() + ")");
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
        Parent root = NavigationService.loadFXML("settings");
        NavigationService.navigateTo(root);
    }

    @FXML
    private void onQuickPlayPressed(ActionEvent event) throws IOException {
        Parent root = NavigationService.loadFXML("difficulty");
        NavigationService.navigateTo(root);
    }
}
