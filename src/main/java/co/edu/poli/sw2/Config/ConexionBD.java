package co.edu.poli.sw2.Config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = ConexionBD.class.getResourceAsStream("/co/edu/poli/sw2/Vista/db.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró db.properties en la ruta /co/edu/poli/sw2/Vista/");
            }
            props.load(input);
            Class.forName("org.postgresql.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar configuración de la BD", e);
        }
    }

    public static Connection obtenerConexion() throws SQLException {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        System.out.println("Conectando a la base de datos con URL: " + url);
        return DriverManager.getConnection(url, user, password);
    }
}