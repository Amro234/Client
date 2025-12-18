module com.mycompany.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.media;

    opens com.mycompany.client to javafx.fxml;

    exports com.mycompany.client;

}
