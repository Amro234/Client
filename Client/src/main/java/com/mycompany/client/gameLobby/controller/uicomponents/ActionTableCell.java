package com.mycompany.client.gameLobby.controller.uicomponents;

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
            btn.setText("Challenge");
            btn.getStyleClass().add("action-button");
            setGraphic(box);
        }
    }
}
