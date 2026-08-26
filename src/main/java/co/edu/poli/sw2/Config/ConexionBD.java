package co.edu.poli.sw2.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Punto único de acceso a la configuración de la base de datos, siguiendo el
 * patrón Singleton.
 *
 * <p>Los datos de conexión (URL, usuario, contraseña) se leen una sola vez
 * desde un archivo {@code .env} o desde variables de entorno del sistema
 * operativo, y quedan disponibles para toda la aplicación.</p>
 *
 * <p>Lo que el Singleton comparte es la <em>configuración</em>, no la conexión:
 * cada llamada a {@link #abrirConexion()} devuelve una {@link Connection}
 * independiente que quien la pide debe cerrar, preferiblemente con
 * try-with-resources. Compartir una única conexión entre operaciones haría que
 * la primera en cerrarse dejara inservibles a las demás, y provocaría
 * interferencias entre transacciones simultáneas.</p>
 *
 * <p>Las credenciales nunca se exponen: no se imprimen por consola ni se
 * ofrecen mediante métodos públicos.</p>
 */
public final class ConexionBD {

    /** Única instancia de la clase. Volatile para la publicación segura entre hilos. */
    private static volatile ConexionBD instancia;

    private final String url;
    private final String usuario;
    private final String password;

    /**
     * Constructor privado: obliga a obtener la instancia mediante
     * {@link #getInstancia()}.
     *
     * @throws IllegalStateException si la configuración no puede cargarse.
     */
    private ConexionBD() {
        Properties config = cargarConfiguracion();

        this.url = config.getProperty("db.url");
        this.usuario = config.getProperty("db.user");
        this.password = config.getProperty("db.password");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No se encontró el driver JDBC de PostgreSQL.", e);
        }
    }

    /**
     * Punto único de acceso a la instancia del Singleton.
     *
     * <p>Usa doble comprobación con bloqueo: el candado solo se toma en la
     * primera llamada, de modo que las siguientes no pagan su coste.</p>
     *
     * @return la única instancia de {@code ConexionBD}.
     */
    public static ConexionBD getInstancia() {
        ConexionBD resultado = instancia;
        if (resultado == null) {
            synchronized (ConexionBD.class) {
                resultado = instancia;
                if (resultado == null) {
                    resultado = new ConexionBD();
                    instancia = resultado;
                }
            }
        }
        return resultado;
    }

    /**
     * Abre una nueva conexión con la base de datos.
     *
     * <p>Cada llamada devuelve una conexión independiente. Quien la obtiene es
     * responsable de cerrarla.</p>
     *
     * @return conexión abierta con la base de datos.
     * @throws SQLException si la conexión no puede establecerse.
     */
    public Connection abrirConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }

    /**
     * Acceso directo a una conexión sin obtener antes la instancia.
     *
     * <p>Es el método que utilizan los DAO; internamente delega en la instancia
     * única.</p>
     *
     * @return conexión abierta con la base de datos.
     * @throws SQLException si la conexión no puede establecerse.
     */
    public static Connection obtenerConexion() throws SQLException {
        return getInstancia().abrirConexion();
    }

    /**
     * @return la URL de conexión, sin credenciales, apta para diagnóstico.
     */
    public String getUrlSegura() {
        return url;
    }

    // ===================== Carga de configuración desde .env =====================

    /**
     * Lee la configuración desde el archivo {@code .env} o, en su defecto,
     * desde las variables de entorno del sistema.
     *
     * @return propiedades con las claves db.url, db.user y db.password.
     * @throws IllegalStateException si falta alguna de las tres.
     */
    private static Properties cargarConfiguracion() {
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
                    throw new IllegalStateException("Error al leer el archivo .env", e);
                }
                break;
            }
        }

        Properties config = new Properties();
        config.setProperty("db.url", obtenerValor(env, "DB_URL", "db.url"));
        config.setProperty("db.user", obtenerValor(env, "DB_USER", "db.user"));
        config.setProperty("db.password", obtenerValor(env, "DB_PASSWORD", "db.password"));
        return config;
    }

    /**
     * Busca un valor en el archivo .env, en las variables de entorno del
     * sistema y en las claves antiguas, en ese orden.
     *
     * @param env           propiedades leídas del archivo .env.
     * @param claveAmbiente nombre de la variable de entorno (ej. DB_URL).
     * @param claveLegacy   nombre antiguo de la propiedad (ej. db.url).
     * @return valor encontrado.
     * @throws IllegalStateException si no se encuentra en ninguna fuente.
     */
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
            throw new IllegalStateException(
                    "Falta la configuración de la base de datos. Define DB_URL, DB_USER "
                    + "y DB_PASSWORD en el archivo .env o como variables de entorno.");
        }
        return valor.trim();
    }
}