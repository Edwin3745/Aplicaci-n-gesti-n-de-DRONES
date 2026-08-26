package co.edu.poli.sw2.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static final Properties props = new Properties();

    static {
        try {
            cargarConfiguracionDesdeEnv();
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver de PostgreSQL", e);
        }
    }

    private static void cargarConfiguracionDesdeEnv() {
        Properties env = new Properties();

        Path[] posiblesArchivos = {
                Path.of(System.getProperty("user.dir"), ".env"),
                Path.of(System.getProperty("user.dir"), "src", "main", "resources", ".env")
        };

        for (Path ruta : posiblesArchivos) {
            if (Files.exists(ruta)) {
                try {
                    Files.readAllLines(ruta, StandardCharsets.UTF_8).forEach(linea -> {
                        String contenido = linea.trim();
                        if (contenido.isEmpty() || contenido.startsWith("#")) {
                            return;
                        }
                        int index = contenido.indexOf('=');
                        if (index <= 0) {
                            return;
                        }
                        String clave = contenido.substring(0, index).trim();
                        String valor = contenido.substring(index + 1).trim();
                        if (valor.startsWith("\"") && valor.endsWith("\"")) {
                            valor = valor.substring(1, valor.length() - 1);
                        }
                        env.setProperty(clave, valor);
                    });
                } catch (IOException e) {
                    throw new RuntimeException("Error al leer el archivo .env", e);
                }
                break;
            }
        }

        String url = obtenerValor(env, "DB_URL", "db.url");
        String user = obtenerValor(env, "DB_USER", "db.user");
        String password = obtenerValor(env, "DB_PASSWORD", "db.password");

        props.setProperty("db.url", url);
        props.setProperty("db.user", user);
        props.setProperty("db.password", password);
    }

    private static String obtenerValor(Properties env, String claveAmbiente, String claveLegacy) {
        String valor = env.getProperty(claveAmbiente);
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(claveAmbiente);
        }
        if (valor == null || valor.isBlank()) {
            valor = env.getProperty(claveLegacy);
        }
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(claveLegacy);
        }
        if (valor == null || valor.isBlank()) {
            throw new RuntimeException(
                    "Falta la configuración de la base de datos. Define DB_URL, DB_USER y DB_PASSWORD en el .env o como variables de entorno."
            );
        }
        return valor.trim();
    }

    public static Connection obtenerConexion() throws SQLException {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }
}