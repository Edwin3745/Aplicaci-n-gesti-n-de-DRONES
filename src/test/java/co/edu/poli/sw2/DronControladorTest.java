package co.edu.poli.sw2;

import co.edu.poli.sw2.Controlador.DronControlador;
import co.edu.poli.sw2.Controlador.OperacionFallidaException;
import co.edu.poli.sw2.modelo.Dron;
import co.edu.poli.sw2.modelo.TipoDron;
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