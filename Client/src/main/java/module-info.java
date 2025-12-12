module com.mycompany.client {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.client to javafx.fxml;
    exports com.mycompany.client;
}
