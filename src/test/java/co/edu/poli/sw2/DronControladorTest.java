package co.edu.poli.sw2;

import co.edu.poli.sw2.Controlador.DronControlador;
import co.edu.poli.sw2.Controlador.OperacionFallidaException;
import co.edu.poli.sw2.modelo.Dron;
import co.edu.poli.sw2.modelo.TipoDron;
import co.edu.poli.sw2.servicios.AgriculturaFactory;
import co.edu.poli.sw2.servicios.DemostracionPatron;
import co.edu.poli.sw2.servicios.GenericDAO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DronControladorTest {

    @Test
    void testRegistrarYBuscarDron() {
        DronControlador controlador = new DronControlador(new InMemoryDronDAO());

        controlador.registrarDron(
                TipoDron.AGRICULTURA,
                "SN-001",
                "Mavic 3",
                "DJI",
                0.9,
                2.0,
                false
        );

        Dron dron = controlador.buscarDron(1);

        assertNotNull(dron, "El dron no debería ser null después de registrarlo");
        assertEquals("SN-001", dron.getSerial());
    }

    @Test
    void crearDesdePlantilla_debeDevolverUnDronListoParaEditar() {
        DronControlador controlador = new DronControlador(new InMemoryDronDAO());

        Dron desdePlantilla = controlador.crearDesdePlantilla("Fumigador estándar");

        assertNotNull(desdePlantilla, "La plantilla registrada debe poder usarse");
        assertEquals(0, desdePlantilla.getId(),
                "El dron llega sin id: todavía no se ha guardado");
        assertEquals("Agras T40", desdePlantilla.getModelo(),
                "Los datos de la configuración base deben llegar al formulario");
    }

    @Test
    void crearDesdePlantilla_dosVeces_debeDevolverObjetosIndependientes() {
        DronControlador controlador = new DronControlador(new InMemoryDronDAO());

        Dron primero = controlador.crearDesdePlantilla("Vigilancia nocturna");
        primero.setModelo("Modelo alterado por el usuario");

        Dron segundo = controlador.crearDesdePlantilla("Vigilancia nocturna");

        assertEquals("Matrice 30T", segundo.getModelo(),
                "Editar un dron sacado de la plantilla no puede alterar la plantilla");
    }

    @Test
    void crearDesdePlantilla_conNombreDesconocido_debeLanzarOperacionFallida() {
        DronControlador controlador = new DronControlador(new InMemoryDronDAO());

        OperacionFallidaException ex = assertThrows(OperacionFallidaException.class,
                () -> controlador.crearDesdePlantilla("plantilla inexistente"));

        assertTrue(ex.getMessage().contains("plantilla inexistente"),
                "El mensaje debe llegar a la vista nombrando la plantilla buscada");
    }

    @Test
    void nombresDePlantillas_debeOfrecerLasConfiguracionesBase() {
        DronControlador controlador = new DronControlador(new InMemoryDronDAO());

        assertTrue(controlador.nombresDePlantillas().contains("Fumigador estándar"));
        assertTrue(controlador.nombresDePlantillas().contains("Vigilancia nocturna"));
    }

    // ------------------------------------------------------------------
    // Botones de demostración de patrones
    // ------------------------------------------------------------------

    @Test
    void clonarDron_debeDevolverCopiaEInforme() {
        DronControlador controlador = new DronControlador(new InMemoryDronDAO());
        Dron original = AgriculturaFactory.crearDron(9, "AGR-CTRL",
                "Agras T40", "DJI", 38.0, 40.0);

        DemostracionPatron resultado = controlador.clonarDron(original);

        assertNotSame(original, resultado.dron(), "El clon debe ser otro objeto");
        assertEquals(0, resultado.dron().getId(), "El clon llega sin id");
        assertEquals("AGR-CTRL", resultado.dron().getSerial());
        assertTrue(resultado.informe().contains("original == copia   -> false"),
                "El informe debe acreditar que son objetos distintos");
    }

    @Test
    void clonarDron_sinSeleccion_debeLanzarOperacionFallida() {
        DronControlador controlador = new DronControlador(new InMemoryDronDAO());

        OperacionFallidaException ex = assertThrows(OperacionFallidaException.class,
                () -> controlador.clonarDron(null));

        assertTrue(ex.getMessage().toLowerCase().contains("selecciona"),
                "El mensaje debe guiar al usuario, no exponer el fallo técnico");
    }

    @Test
    void construirConBuilder_debeDevolverDronEInformeConLaSecuencia() {
        DronControlador controlador = new DronControlador(new InMemoryDronDAO());

        DemostracionPatron resultado = controlador.construirConBuilder(
                TipoDron.VIGILANCIA, "VIG-CTRL", "Matrice 30T", "DJI", 3.7, 0.0, true);

        assertEquals("VIG-CTRL", resultado.dron().getSerial());
        assertEquals(TipoDron.VIGILANCIA, resultado.dron().getTipo());
        assertTrue(resultado.informe().contains(".conSerial(\"VIG-CTRL\")"),
                "El informe debe reproducir la llamada encadenada con su valor real");
        assertTrue(resultado.informe().contains(".build();"),
                "El informe debe mostrar el cierre de la cadena");
    }

    @Test
    void construirConBuilder_conCamposVacios_debeUsarValoresDeMuestra() {
        DronControlador controlador = new DronControlador(new InMemoryDronDAO());

        // El botón debe funcionar aunque el formulario esté recién abierto.
        DemostracionPatron resultado = controlador.construirConBuilder(
                null, "  ", "", null, 0.0, 40.0, false);

        assertNotNull(resultado.dron(), "La demostración debe producir un dron igualmente");
        assertFalse(resultado.dron().getSerial().isBlank(),
                "Los campos vacíos se completan con valores de muestra");
        assertTrue(resultado.dron().getPeso() > 0,
                "El peso de muestra debe superar la validación del builder");
    }

    private static class InMemoryDronDAO implements GenericDAO<Dron, Integer> {
        private final Map<Integer, Dron> drones = new HashMap<>();
        private int nextId = 1;

        @Override
        public void guardar(Dron entidad) {
            entidad.setId(nextId++);
            drones.put(entidad.getId(), entidad);
        }

        @Override
        public boolean eliminar(Integer id) {
            return drones.remove(id) != null;
        }

        @Override
        public Dron buscarPorId(Integer id) {
            return drones.get(id);
        }

        @Override
        public List<Dron> listarTodos() {
            return new ArrayList<>(drones.values());
        }

        @Override
        public boolean actualizar(Dron entidad) {
            if (!drones.containsKey(entidad.getId())) {
                return false;
            }
            drones.put(entidad.getId(), entidad);
            return true;
        }
    }
}