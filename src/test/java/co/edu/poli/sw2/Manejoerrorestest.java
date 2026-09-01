package co.edu.poli.sw2;

import co.edu.poli.sw2.Controlador.DronControlador;
import co.edu.poli.sw2.Controlador.OperacionFallidaException;
import co.edu.poli.sw2.modelo.TipoDron;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica que los fallos técnicos (SQLException, restricciones de la BD,
 * validaciones de negocio) lleguen al llamador como {@link OperacionFallidaException}
 * con mensajes legibles, en vez de propagarse como excepciones técnicas o
 * imprimirse por consola.
 */
class ManejoErroresTest {

    private final DronControlador controlador = new DronControlador();

    @Test
    void registrarDron_conSerialVacio_debeLanzarOperacionFallida() {
        OperacionFallidaException ex = assertThrows(OperacionFallidaException.class, () ->
                controlador.registrarDron(TipoDron.VIGILANCIA, "  ",
                        "Modelo X", "Fab Y", 5.0, 0.0, true));

        assertTrue(ex.getMessage().toLowerCase().contains("serial"));
    }

    @Test
    void registrarDron_conPesoNegativo_debeLanzarOperacionFallida() {
        OperacionFallidaException ex = assertThrows(OperacionFallidaException.class, () ->
                controlador.registrarDron(TipoDron.VIGILANCIA, "VIG-NEG-TEST",
                        "Modelo X", "Fab Y", -3.0, 0.0, true));

        assertTrue(ex.getMessage().toLowerCase().contains("peso"));
    }

    @Test
    void registrarDron_conSerialDuplicado_debeLanzarOperacionFallida() {
        // Primero se registra uno válido...
        controlador.registrarDron(TipoDron.AGRICULTURA, "DUP-TEST",
                "Modelo X", "Fab Y", 20.0, 30.0, false);

        // ...y se intenta registrar otro con el mismo serial.
        OperacionFallidaException ex = assertThrows(OperacionFallidaException.class, () ->
                controlador.registrarDron(TipoDron.AGRICULTURA, "DUP-TEST",
                        "Modelo Y", "Fab Z", 15.0, 10.0, false));

        assertTrue(ex.getMessage().toLowerCase().contains("existe"));

        // Limpieza
        controlador.listarDrones().stream()
                .filter(d -> d.getSerial().equals("DUP-TEST"))
                .findFirst()
                .ifPresent(d -> controlador.eliminarDron(d.getId()));
    }

    @Test
    void registrarDron_conDatosValidos_noDebeLanzarExcepcion() {
        assertDoesNotThrow(() ->
                controlador.registrarDron(TipoDron.AGRICULTURA, "OK-TEST",
                        "Modelo OK", "Fab Y", 25.0, 35.0, false));

        // Limpieza
        controlador.listarDrones().stream()
                .filter(d -> d.getSerial().equals("OK-TEST"))
                .findFirst()
                .ifPresent(d -> controlador.eliminarDron(d.getId()));
    }
}