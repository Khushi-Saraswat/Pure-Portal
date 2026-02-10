module com.pure {
    // JavaFX modules you need
    requires javafx.controls; // Button, TextField, ComboBox, Alert, Text, etc.
    requires javafx.fxml; // FXMLLoader, @FXML injection
    requires javafx.graphics; // Required for Scene, Stage (included by javafx.controls usually)
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    // Optional: if you use HTTP client
    requires java.net.http;

    // Open your packages for FXML injection
    opens com.pure to javafx.fxml;

    // Export your packages
    exports com.pure;
    exports com.pure.Dto;
}
