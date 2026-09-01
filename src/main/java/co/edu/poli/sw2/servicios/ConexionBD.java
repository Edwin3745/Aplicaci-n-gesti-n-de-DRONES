package co.edu.poli.sw2.servicios;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Punto único de acceso a la base de datos, siguiendo el patrón Singleton.
 *
 * <p>La clase cumple dos funciones. Primero, lee una sola vez los datos de
 * conexión (URL, usuario, contraseña) desde un archivo {@code .env} o desde
 * las variables de entorno del sistema operativo. Segundo, mantiene una
 * <em>única</em> {@link Connection} compartida por toda la aplicación, que
 * entrega mediante {@link #getConexion()}.</p>
 *
 * <p>Compartir una sola conexión es viable en este proyecto porque JavaFX
 * atiende los eventos de la interfaz en un único hilo: nunca hay dos
 * operaciones simultáneas compitiendo por ella. A cambio se evita abrir y
 * cerrar una conexión TCP contra PostgreSQL en cada operación, que es la parte
 * más costosa de cada consulta.</p>
 *
 * <p>La contrapartida del diseño es que la conexión <strong>pertenece al
 * Singleton</strong>: ningún DAO debe cerrarla, porque dejaría inservibles a
 * todas las operaciones posteriores. La única clase autorizada a cerrarla es
 * {@code MainApp}, al terminar la aplicación, mediante
 * {@link #cerrarConexion()}.</p>
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
     * Conexión compartida por toda la aplicación.
     *
     * <p>Se abre de forma perezosa en la primera llamada a
     * {@link #getConexion()} y se reutiliza a partir de ahí.</p>
     */
    private Connection conexion;

    /**
     * Constructor privado: obliga a obtener la instancia mediante
     * {@link #getInstancia()}.
     *
     * @throws IllegalStateException si la configuración no puede cargarse o si
     *                               falta el driver JDBC de PostgreSQL.
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
     * Entrega la conexión compartida con la base de datos.
     *
     * <p>La primera llamada la abre; las siguientes devuelven exactamente la
     * misma instancia. Si la conexión se hubiera perdido o cerrado, se
     * restablece de forma transparente antes de devolverla, de modo que quien
     * la pide siempre recibe una conexión utilizable.</p>
     *
     * <p><strong>La conexión pertenece al Singleton: quien la recibe no debe
     * cerrarla.</strong> En particular, no debe colocarse en un
     * {@code try-with-resources}: cerrarla desde un DAO haría fallar todas las
     * operaciones posteriores de la aplicación. Su cierre corresponde
     * únicamente a {@link #cerrarConexion()}, al terminar el programa.</p>
     *
     * @return la conexión compartida, abierta y lista para usarse.
     * @throws SQLException si la conexión no puede establecerse.
     */
    public Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, password);
        }
        return conexion;
    }

    /**
     * Cierra la conexión compartida y la descarta.
     *
     * <p>Está pensado para invocarse una sola vez, al terminar la aplicación,
     * desde {@code MainApp.stop()}. Tras llamarlo, una nueva petición a
     * {@link #getConexion()} vuelve a abrir la conexión, de modo que el
     * Singleton nunca queda inservible.</p>
     *
     * <p>Si el cierre falla no se propaga la excepción: la aplicación ya está
     * terminando y no hay ninguna acción que el usuario pueda emprender.</p>
     */
    public void cerrarConexion() {
        if (conexion == null) {
            return;
        }
        try {
            if (!conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("No se pudo cerrar la conexión con la base de datos: "
                    + e.getMessage());
        } finally {
            conexion = null;
        }
    }

    /**
     * Atajo para obtener la conexión compartida sin pedir antes la instancia.
     *
     * <p>Es el método que utilizan los DAO; delega en {@link #getConexion()},
     * por lo que rigen las mismas condiciones: la conexión es del Singleton y
     * no debe cerrarse.</p>
     *
     * @return la conexión compartida, abierta y lista para usarse.
     * @throws SQLException si la conexión no puede establecerse.
     */
    public static Connection obtenerConexion() throws SQLException {
        return getInstancia().getConexion();
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