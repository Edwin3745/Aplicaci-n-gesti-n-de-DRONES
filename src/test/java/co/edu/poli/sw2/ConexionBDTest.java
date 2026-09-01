package co.edu.poli.sw2;

import co.edu.poli.sw2.Servicio.ConexionBD;   
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración del Singleton ConexionBD contra PostgreSQL real.
 */
class ConexionBDTest {

    @Test
    void abrirConexion_debeDevolverConexionAbierta() throws Exception {
        try (Connection con = ConexionBD.obtenerConexion()) {
            assertNotNull(con, "La conexión no debería ser null");
            assertFalse(con.isClosed(), "La conexión debería estar abierta");
        }
    }

    @Test
    void getInstancia_llamadoDosVeces_debeDevolverLaMismaInstancia() {
        ConexionBD primera = ConexionBD.getInstancia();
        ConexionBD segunda = ConexionBD.getInstancia();
        assertSame(primera, segunda,
                "El patrón Singleton debe devolver siempre la misma instancia");
    }

    @Test
    void abrirConexion_debeDevolverConexionesIndependientes() throws Exception {
        try (Connection primera = ConexionBD.obtenerConexion();
             Connection segunda = ConexionBD.obtenerConexion()) {

            assertNotSame(primera, segunda,
                    "Cada llamada debe devolver una conexión nueva: compartir una sola "
                    + "haría que al cerrarla en una operación fallaran las demás");
        }
    }

    @Test
    void cerrarUnaConexion_noDebeAfectarALasSiguientes() throws Exception {
        try (Connection primera = ConexionBD.obtenerConexion()) {
            assertFalse(primera.isClosed());
        }
        try (Connection segunda = ConexionBD.obtenerConexion()) {
            assertFalse(segunda.isClosed(),
                    "Tras cerrar una conexión, las siguientes deben seguir funcionando");
        }
    }
}