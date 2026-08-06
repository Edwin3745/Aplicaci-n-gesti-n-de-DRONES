package co.edu.poli.sw2.Vista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/co/edu/poli/sw2/Vista/DrownView.fxml"));
        Scene scene = new Scene(root, 750, 500);
        scene.getStylesheets().add(getClass().getResource("/co/edu/poli/sw2/Vista/styles.css").toExternalForm());
        stage.setTitle("Gestión de Drones");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
