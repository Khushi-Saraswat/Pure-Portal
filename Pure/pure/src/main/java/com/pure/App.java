package com.pure;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/primary.fxml"));

        Parent root = fxmlLoader.load();

        PrimaryController controller = fxmlLoader.getController();

        scene = new Scene(root);
        stage.setTitle("PureAir");
        stage.setScene(scene);
        stage.show();

        // Background thread
        new Thread(() -> {
            boolean connected = InternetChecker.isInternetAvailable();
            Platform.runLater(() -> controller.updateConnectivityStatus(connected));
        }).start();
    }

    // Scene switching
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // Generic loader using "/" + fxml + ".fxml"
    private static Parent loadFXML(String fxml) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/" + fxml + ".fxml"));

        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
