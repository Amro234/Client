module com.mycompany.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires javafx.base;
    requires javafx.graphics;

    opens com.mycompany.client to javafx.fxml;
    exports com.mycompany.client;
}
