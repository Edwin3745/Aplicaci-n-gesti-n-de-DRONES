package co.edu.poli.sw2;

import co.edu.poli.sw2.Controlador.DronControlador;
import co.edu.poli.sw2.modelo.Agricultura;
import co.edu.poli.sw2.modelo.Dron;
import co.edu.poli.sw2.modelo.TipoDron;
import co.edu.poli.sw2.servicios.ConexionBD;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración del ciclo completo de operaciones sobre drones contra
 * la base de datos real.
 *
 * <p>A diferencia de las pruebas que ejercitan una sola operación, esta
 * encadena varias seguidas sobre la conexión compartida, que es la única forma
 * de detectar que algún punto del código la esté cerrando: el fallo no aparece
 * en la primera operación, sino en la siguiente.</p>
 */
class FlujoCrudDronTest {

    /** Prefijo de los seriales de prueba, para poder limpiarlos al terminar. */
    private static final String PREFIJO = "FLUJO-TEST-";

    @Test
    void variasOperacionesSeguidas_debenCompletarseSinCerrarLaConexion() {
        DronControlador controlador = new DronControlador();
        Connection conexionInicial = obtenerConexion();

        try {
            // --- Alta de varios drones seguidos ---
            controlador.registrarDron(TipoDron.AGRICULTURA, PREFIJO + "1",
                    "Agras T40", "DJI", 38.0, 40.0, false);
            controlador.registrarDron(TipoDron.VIGILANCIA, PREFIJO + "2",
                    "Matrice 30T", "DJI", 3.7, 0.0, true);
            controlador.registrarDron(TipoDron.AGRICULTURA, PREFIJO + "3",
                    "Agras T25", "XAG", 26.5, 20.0, false);

            List<Dron> registrados = buscarDeLaPrueba(controlador);
            assertEquals(3, registrados.size(),
                    "Las tres altas consecutivas deben haberse guardado");

            // --- Actualización sobre la misma conexión ---
            Dron primero = registrados.get(0);
            primero.setModelo("Agras T40 Pro");
            assertTrue(controlador.actualizarDron(primero),
                    "La actualización posterior a las altas debe aplicarse");

            Dron releido = controlador.buscarDron(primero.getId());
            assertEquals("Agras T40 Pro", releido.getModelo(),
                    "La consulta posterior a la actualización debe ver el cambio");
            assertInstanceOf(Agricultura.class, releido,
                    "El subtipo debe reconstruirse desde la columna discriminadora");

            // --- Bajas encadenadas ---
            for (Dron dron : buscarDeLaPrueba(controlador)) {
                assertTrue(controlador.eliminarDron(dron.getId()),
                        "Cada baja consecutiva debe completarse");
            }

            assertTrue(buscarDeLaPrueba(controlador).isEmpty(),
                    "No debe quedar ningún dron de la prueba");

            // La conexión sigue siendo la misma de partida: ninguna operación la cerró.
            assertSame(conexionInicial, obtenerConexion(),
                    "El ciclo completo debe haber viajado por una única conexión");

        } finally {
            limpiar(controlador);
        }
    }

    /**
     * Obtiene la conexión compartida sin propagar la excepción comprobada.
     *
     * @return la conexión que mantiene el Singleton.
     */
    private Connection obtenerConexion() {
        try {
            return ConexionBD.getInstancia().getConexion();
        } catch (Exception e) {
            throw new AssertionError("No se pudo obtener la conexión compartida", e);
        }
    }

    /**
     * Recupera los drones creados por esta prueba.
     *
     * @param controlador controlador con el que consultar.
     * @return los drones cuyo serial empieza por el prefijo de la prueba.
     */
    private List<Dron> buscarDeLaPrueba(DronControlador controlador) {
        return controlador.listarDrones().stream()
                .filter(d -> d.getSerial().startsWith(PREFIJO))
                .toList();
    }

    /**
     * Borra cualquier resto que haya dejado la prueba, incluso si falló a mitad.
     *
     * @param controlador controlador con el que eliminar.
     */
    private void limpiar(DronControlador controlador) {
        buscarDeLaPrueba(controlador)
                .forEach(dron -> controlador.eliminarDron(dron.getId()));
    }
}
