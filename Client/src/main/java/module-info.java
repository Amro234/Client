module com.mycompany.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.media;
    requires org.json;

    opens com.mycompany.client to javafx.fxml;
    opens com.mycompany.client.mainmenu to javafx.fxml;
    opens com.mycompany.client.difficulty to javafx.fxml;
    opens com.mycompany.client.settings.manager to javafx.fxml;
    opens com.mycompany.client.settings.controller to javafx.fxml;
    opens com.mycompany.client.gameboard.controller to javafx.fxml;
    opens com.mycompany.client.auth.controller to javafx.fxml;
    opens com.mycompany.client.auth.model to javafx.fxml;
    opens com.mycompany.client.gameLobby.controller to javafx.fxml;

    exports com.mycompany.client;
    exports com.mycompany.client.mainmenu;
    exports com.mycompany.client.difficulty;
    exports com.mycompany.client.settings.manager;
    exports com.mycompany.client.settings.controller;
    exports com.mycompany.client.auth.controller;
    exports com.mycompany.client.auth.model;
    exports com.mycompany.client.gameLobby.controller;
    exports com.mycompany.client.gameLobby.networking.model.challenge;
    exports com.mycompany.client.gameLobby.networking.model.user;
    exports com.mycompany.client.gameboard.controller;
    exports com.mycompany.client.core.server;
    exports com.mycompany.client.core.navigation;

}
