package co.edu.poli.sw2.Vista;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de entrada principal de la aplicación JavaFX de gestión de drones.
 *
 * Esta clase pertenece a la capa Vista dentro del patrón MVC y se encarga de
 * inicializar la interfaz gráfica cargando el archivo FXML correspondiente y
 * configurando la escena principal. Su función es levantar la aplicación y
 * mostrar la pantalla principal del sistema.
 */
public class MainApp extends Application {

    /**
     * Inicializa la ventana principal de la aplicación.
     *
     * Carga la vista definida en el archivo FXML, establece el tamaño de la
     * ventana, aplica la hoja de estilos y muestra la interfaz al usuario.
     *
     * @param stage escenario principal proporcionado por JavaFX.
     * @throws Exception si ocurre un error al cargar la vista o los recursos asociados.
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/co/edu/poli/sw2/Vista/DrownView.fxml"));
        Scene scene = new Scene(root, 750, 500);
        scene.getStylesheets().add(getClass().getResource("/co/edu/poli/sw2/Vista/styles.css").toExternalForm());
        stage.setTitle("Gestión de Drones");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Ejecuta la aplicación JavaFX.
     *
     * @param args argumentos de la línea de comandos pasados al programa.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
