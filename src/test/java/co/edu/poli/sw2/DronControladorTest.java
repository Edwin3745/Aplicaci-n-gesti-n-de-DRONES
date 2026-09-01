package co.edu.poli.sw2;

import co.edu.poli.sw2.Controlador.DronControlador;
import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.TipoDron;
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