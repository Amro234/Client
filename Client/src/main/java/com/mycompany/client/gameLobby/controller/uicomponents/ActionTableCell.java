package com.mycompany.client.gameLobby.controller.uicomponents;

import com.mycompany.client.core.notification.ToastNotification;
import com.mycompany.client.gameLobby.networking.GameLobbyClient;
import com.mycompany.client.gameLobby.networking.exception.GameLobbyException;
import com.mycompany.client.gameLobby.networking.model.challenge.ChallengeResponse;
import com.mycompany.client.gameLobby.networking.model.user.OnlineUser;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class ActionTableCell extends TableCell<OnlineUser, Void> {

    private final Button btn = new Button();
    private final Region spacer = new Region();
    private final HBox box = new HBox(10, spacer, btn);

    public ActionTableCell() {
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

            btn.setText("Challenge");
            btn.getStyleClass().add("action-button");

            btn.setOnAction(event -> {
                btn.setDisable(true);
                sendChallenge(user);

                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    javafx.application.Platform.runLater(() -> btn.setDisable(false));
                }).start();
            });

            setGraphic(box);
        }
    }

    private void sendChallenge(OnlineUser user) {
        new Thread(() -> {
            try {
                ChallengeResponse response = GameLobbyClient.sendChallenge(user.getUserId());

                if (response.isSuccess()) {
                    ToastNotification.success(response.getMessage());
                } else {
                    ToastNotification.error(response.getMessage());
                    javafx.application.Platform.runLater(() -> btn.setDisable(false));
                }
            } catch (GameLobbyException e) {
                ToastNotification.error("Failed to send challenge: " + e.getMessage());
                javafx.application.Platform.runLater(() -> btn.setDisable(false));
            }
        }).start();
    }
}
