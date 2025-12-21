package com.mycompany.client.gameLobby.controller.uicomponents;

import com.mycompany.client.gameLobby.networking.model.user.OnlineUser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class PlayerTableCell extends TableCell<OnlineUser, String> {

    private final ImageView imageView = new ImageView();
    private final Label label = new Label();
    private final HBox box = new HBox(10, imageView, label);

    public PlayerTableCell() {
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
            imageView.setImage(new Image(getClass().getResourceAsStream("/assets/images/avatar1.png")));
            label.setText(item);
            setGraphic(box);
            setAlignment(Pos.CENTER_LEFT);
            setPadding(new Insets(0, 0, 0, -10));
        }
    }
}
