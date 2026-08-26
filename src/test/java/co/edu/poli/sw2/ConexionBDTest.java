package co.edu.poli.sw2;

import co.edu.poli.sw2.Config.ConexionBD;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración del Singleton ConexionBD contra PostgreSQL real.
 */
class ConexionBDTest {

    @Test
    void getInstancia_debeRetornarConexionAbierta() throws Exception {
        Connection con = ConexionBD.getInstancia().getConexion();

        assertNotNull(con, "La conexión no debería ser null");
        assertFalse(con.isClosed(), "La conexión debería estar abierta");
    }

    @Test
    void getInstancia_llamadoDosVeces_debeRetornarLaMismaConexion() {
        Connection primera = ConexionBD.getInstancia().getConexion();
        Connection segunda = ConexionBD.getInstancia().getConexion();

        assertSame(primera, segunda,
                "El patrón Singleton debe reutilizar la misma instancia de Connection");
    }
}