package co.edu.poli.sw2;

import co.edu.poli.sw2.servicios.ConexionBD;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración del Singleton {@link ConexionBD} contra PostgreSQL
 * real.
 *
 * <p>Verifican las dos garantías del diseño acordado para la entrega 4: que la
 * conexión es única y se reutiliza entre operaciones, y que el Singleton la
 * restablece si alguien la cierra, de modo que la aplicación nunca queda
 * bloqueada.</p>
 */
class ConexionBDTest {

    @Test
    void getInstancia_llamadoDosVeces_debeDevolverLaMismaInstancia() {
        ConexionBD primera = ConexionBD.getInstancia();
        ConexionBD segunda = ConexionBD.getInstancia();

        assertSame(primera, segunda,
                "El patrón Singleton debe devolver siempre la misma instancia");
    }

    @Test
    void getConexion_debeDevolverUnaConexionAbierta() throws Exception {
        Connection con = ConexionBD.getInstancia().getConexion();

        assertNotNull(con, "La conexión no debería ser null");
        assertFalse(con.isClosed(), "La conexión debería estar abierta");
    }

    @Test
    void getConexion_llamadoDosVeces_debeReutilizarLaMismaConexion() throws Exception {
        Connection primera = ConexionBD.getInstancia().getConexion();
        Connection segunda = ConexionBD.getInstancia().getConexion();

        assertSame(primera, segunda,
                "La conexión es única y compartida: abrir una nueva en cada llamada "
                + "supondría una conexión TCP por operación");
    }

    @Test
    void getConexion_trasCerrarla_debeRestablecerla() throws Exception {
        Connection anterior = ConexionBD.getInstancia().getConexion();
        ConexionBD.getInstancia().cerrarConexion();
        assertTrue(anterior.isClosed(), "cerrarConexion debe cerrar la conexión vigente");

        Connection nueva = ConexionBD.getInstancia().getConexion();

        assertNotSame(anterior, nueva,
                "Tras cerrarla, el Singleton debe entregar una conexión nueva");
        assertFalse(nueva.isClosed(),
                "La conexión restablecida debe poder usarse");
    }

    @Test
    void operacionesEncadenadas_debenCompartirLaMismaConexion() throws Exception {
        // Reproduce lo que hace la aplicación: varias operaciones seguidas sobre
        // la conexión compartida. Si algún punto la cerrara, la segunda fallaría.
        Connection usadaPrimero = ejecutarConsultaDePrueba();
        Connection usadaDespues = ejecutarConsultaDePrueba();

        assertSame(usadaPrimero, usadaDespues,
                "Dos operaciones consecutivas deben viajar por la misma conexión");
        assertFalse(usadaDespues.isClosed(),
                "Ninguna operación debe dejar cerrada la conexión compartida");
    }

    /**
     * Ejecuta una consulta trivial cerrando solo lo que abre, tal como hace el
     * DAO, y devuelve la conexión sobre la que trabajó.
     *
     * @return la conexión compartida utilizada en la consulta.
     * @throws Exception si la consulta no puede ejecutarse.
     */
    private Connection ejecutarConsultaDePrueba() throws Exception {
        Connection con = ConexionBD.getInstancia().getConexion();

        try (PreparedStatement ps = con.prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next(), "La consulta de prueba debería devolver una fila");
        }

        return con;
    }
}
