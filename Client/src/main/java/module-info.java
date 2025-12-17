module com.mycompany.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires javafx.media;
    opens com.mycompany.client to javafx.fxml;
    exports com.mycompany.client;
    
}
