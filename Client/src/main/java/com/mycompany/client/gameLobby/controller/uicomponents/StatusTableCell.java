package com.mycompany.client.gameLobby.controller.uicomponents;

import com.mycompany.client.gameLobby.enums.PlayerStatus;
import com.mycompany.client.gameLobby.networking.model.user.OnlineUser;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class StatusTableCell extends TableCell<OnlineUser, PlayerStatus> {

    private final Circle dot = new Circle(5);
    private final Label label = new Label();
    private final HBox box = new HBox(8, dot, label);

    public StatusTableCell() {
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
}
