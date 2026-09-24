package co.edu.poli.sw2;

import co.edu.poli.sw2.modelo.Agricultura;
import co.edu.poli.sw2.modelo.Dron;
import co.edu.poli.sw2.servicios.bridge.ControlAutonomo;
import co.edu.poli.sw2.servicios.bridge.ControlBasico;
import co.edu.poli.sw2.servicios.facade.VueloFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del patrón Facade aplicado a la preparación del vuelo.
 *
 * <p>No tocan la base de datos: comprueban que la fachada coordina los
 * subsistemas en el orden correcto y que el cliente obtiene el resultado con
 * una sola llamada.</p>
 */
class VueloFacadeTest {

    private VueloFacade fachada;
    private Dron dron;

    @BeforeEach
    void preparar() {
        fachada = new VueloFacade();
        dron = new Agricultura(1, "AGR-001", "Agras T40", "DJI", 38.0, 40.0);
    }

    @Test
    void sinFachada_volarSinControl_falla() {
        // Es el problema que la fachada resuelve: el orden importa.
        assertThrows(IllegalStateException.class,
                () -> dron.ejecutarMision("Lote Norte"),
                "Un dron sin control asignado no puede volar");
    }

    @Test
    void conFachada_elMismoDronVuela() {
        String resumen = fachada.prepararVuelo(dron, false, false, "Lote Norte");

        assertTrue(resumen.contains("Lote Norte"),
                "La fachada asigna el control antes del vuelo, así que el vuelo ocurre");
    }

    @Test
    void modoBasico_asignaControlBasico() {
        fachada.prepararVuelo(dron, false, false, "Lote Norte");

        assertInstanceOf(ControlBasico.class, dron.getControl());
    }

    @Test
    void modoAutonomo_asignaControlAutonomo() {
        fachada.prepararVuelo(dron, true, false, "Lote Norte");

        assertInstanceOf(ControlAutonomo.class, dron.getControl());
    }

    @Test
    void conBateria_laIncluyeEnElEquipo() {
        String resumen = fachada.prepararVuelo(dron, false, true, "Lote Norte");

        assertTrue(resumen.contains("batería adicional"),
                "El resumen debe describir el equipo con la batería montada");
    }

    @Test
    void sinBateria_noLaIncluye() {
        String resumen = fachada.prepararVuelo(dron, false, false, "Lote Norte");

        assertFalse(resumen.contains("batería adicional"));
    }

    @Test
    void resumen_recogeLosTresPasos() {
        String resumen = fachada.prepararVuelo(dron, true, true, "Lote Norte");

        assertTrue(resumen.contains("Bridge"), "Paso 1: modo de control");
        assertTrue(resumen.contains("Decorator"), "Paso 2: equipo");
        assertTrue(resumen.contains("Vuelo"), "Paso 3: ejecución");
    }

    @Test
    void noModificaLosDatosDelDron() {
        fachada.prepararVuelo(dron, true, true, "Lote Norte");

        assertEquals("AGR-001", dron.getSerial());
        assertEquals(38.0, dron.getPeso(),
                "La batería se describe por decoración: no altera el dron");
    }

    @Test
    void dronNulo_seRechaza() {
        assertThrows(IllegalArgumentException.class,
                () -> fachada.prepararVuelo(null, false, false, "Lote Norte"));
    }

    @Test
    void destinoVacio_seRechaza() {
        assertThrows(IllegalArgumentException.class,
                () -> fachada.prepararVuelo(dron, false, false, "   "));
    }
}