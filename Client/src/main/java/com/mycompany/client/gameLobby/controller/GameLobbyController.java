package com.mycompany.client.gameLobby.controller;

import com.mycompany.client.auth.model.User;
import com.mycompany.client.core.navigation.NavigationService;
import com.mycompany.client.core.notification.ToastNotification;
import com.mycompany.client.core.server.ServerConnection;
import com.mycompany.client.core.session.UserSession;
import com.mycompany.client.gameLobby.controller.uicomponents.ActionTableCell;
import com.mycompany.client.gameLobby.controller.uicomponents.PlayerTableCell;
import com.mycompany.client.gameLobby.controller.uicomponents.StatusTableCell;
import com.mycompany.client.gameLobby.enums.PlayerStatus;
import com.mycompany.client.gameLobby.networking.GameLobbyClient;
import com.mycompany.client.gameLobby.networking.GameLobbyNotificationHandler;
import com.mycompany.client.gameLobby.networking.GameLobbyNotificationListener;
import com.mycompany.client.gameLobby.networking.exception.GameLobbyException;
import com.mycompany.client.gameLobby.networking.model.notification.ChallengeAcceptedNotification;
import com.mycompany.client.gameLobby.networking.model.notification.ChallengeDeclinedNotification;
import com.mycompany.client.gameLobby.networking.model.notification.ChallengeReceivedNotification;
import com.mycompany.client.gameLobby.networking.model.user.OnlineUser;
import com.mycompany.client.gameLobby.networking.model.user.OnlineUsersResponse;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

public class GameLobbyController implements Initializable, GameLobbyNotificationListener {

    private FilteredList<OnlineUser> filteredPlayers;
    private GameLobbyNotificationHandler notificationHandler;
    private ScheduledExecutorService refreshExecutor;

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
        setupNotificationHandler();
        loadPlayersData();
        ServerConnection.startMessageListener();
        setupFilters();
        setupRowHeight();
        startAutoRefresh();
    }

    private void setupNotificationHandler() {
        notificationHandler = new GameLobbyNotificationHandler();
        notificationHandler.setNotificationListener(this);
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

    private void startAutoRefresh() {
        refreshExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });

        refreshExecutor.scheduleAtFixedRate(() -> {
            loadPlayersData();
        }, 4, 4, TimeUnit.SECONDS);
    }

    public void shutdown() {
        if (refreshExecutor != null && !refreshExecutor.isShutdown()) {
            refreshExecutor.shutdown();
            try {
                if (!refreshExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                    refreshExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                refreshExecutor.shutdownNow();
            }
        }
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

            Platform.runLater(() -> {
                playerData.clear();

                for (OnlineUser user : response.getUsers()) {
                    if (user.getUserId() != currentUserId) {
                        playerData.add(user);
                    }
                }
                onlineCountLabel.setText(" " + String.valueOf(response.getCount()) + " Online");
                System.out.println(
                        "Loaded " + playerData.size() + " other players (total online: " + response.getCount() + ")");
            });
        } catch (GameLobbyException e) {
            System.err.println("Failed to load online users: " + e.getMessage());
            // Show error to user
            // Platform.runLater(() -> {
            // Alert alert = new Alert(Alert.AlertType.ERROR);
            // alert.setTitle("Error");
            // alert.setHeaderText("Failed to load online players");
            // alert.setContentText(e.getMessage());
            // alert.showAndWait();
            // });
        }
    }

    // Buttons
    @FXML
    private void onLogOutPressed(ActionEvent event) {
        try {
            // Stop lobby refresher
            shutdown();

            // Clear session
            UserSession.getInstance().clearSession();

            // Disconnect from server
            ServerConnection.disconnect();

            // Navigate back to main menu
            Parent root = NavigationService.loadFXML("main-menu");
            NavigationService.replaceWith(root);

        } catch (IOException e) {
            System.err.println("Error navigating to main menu after logout: " + e.getMessage());
            e.printStackTrace();
        }
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

    // Notifications
    @Override
    public void onChallengeReceived(ChallengeReceivedNotification notification) {
        ChallengeDialog dialog = new ChallengeDialog(
                notification.getChallengerUsername(),
                (javafx.stage.Stage) playerTable.getScene().getWindow());
        dialog.show();
    }

    @Override
    public void onChallengeAccepted(ChallengeAcceptedNotification notification) {
        ToastNotification.success(notification.getAcceptedByUsername() + " accepted your challenge!",
                playerTable.getScene().getWindow());
    }

    @Override
    public void onChallengeDeclined(ChallengeDeclinedNotification notification) {
        ToastNotification.error(notification.getDeclinedByUsername() + " declined your challenge.",
                playerTable.getScene().getWindow());
    }

    @Override
    public void onGameStarted(String sessionId, String opponentName, String mySymbol) {
        System.out.println("[GameLobby] Navigating to GameBoard for session: " + sessionId);

        // Stop lobby refresher
        shutdown();

        try {
            javafx.fxml.FXMLLoader loader = NavigationService.getFXMLLoader("game_board");
            Parent root = loader.load();

            com.mycompany.client.gameboard.controller.GameBoardController controller = loader.getController();
            controller.startOnlineGame(opponentName, mySymbol);

            NavigationService.navigateTo(root);

        } catch (IOException e) {
            e.printStackTrace();
            ToastNotification.error("Failed to start game: " + e.getMessage(), playerTable.getScene().getWindow());
        }
    }
}
