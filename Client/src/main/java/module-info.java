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
    opens com.mycompany.client.settings to javafx.fxml;
    opens com.mycompany.client.gameboard.controller to javafx.fxml;
    opens com.mycompany.client.auth.controller to javafx.fxml;
    opens com.mycompany.client.auth.model to javafx.fxml;

    exports com.mycompany.client;
    exports com.mycompany.client.mainmenu;
    exports com.mycompany.client.difficulty;
    exports com.mycompany.client.settings;
    exports com.mycompany.client.auth.controller;
    exports com.mycompany.client.auth.model;

}
