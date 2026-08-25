package co.edu.poli.sw2;

import co.edu.poli.sw2.Controlador.DronControlador;
import co.edu.poli.sw2.Modelo.Dron;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DronControladorTest {

    @Test
    void testRegistrarYBuscarDron() {
        DronControlador controlador = new DronControlador();

        controlador.registrarDron(1, "SN-001", "Mavic 3", "DJI", 0.9f);
        Dron dron = controlador.buscarDron(1);

        assertNotNull(dron, "El dron no debería ser null después de registrarlo");
        assertEquals("SN-001", dron.getSerial());
    }
}