package co.edu.poli.sw2;

import co.edu.poli.sw2.Modelo.Mision;
import co.edu.poli.sw2.Modelo.Piloto;
import co.edu.poli.sw2.Modelo.Sensor;
import co.edu.poli.sw2.Servicio.MisionDAO;
import co.edu.poli.sw2.Servicio.PilotoDAO;
import co.edu.poli.sw2.Servicio.SensorDAO;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración de PilotoDAO, SensorDAO y MisionDAO contra la base
 * de datos real. Cada test crea su propio dato de prueba y lo limpia al final.
 */
class DAOsRestantesTest {

    // ---------------- PILOTO ----------------

    @Test
    void piloto_guardarActualizarYEliminar() {
        PilotoDAO pilotoDAO = new PilotoDAO();

        Piloto piloto = new Piloto();
        piloto.setNombre("Piloto Prueba");
        piloto.setExperiencia(3);
        piloto.setTelefono("3000000000");

        pilotoDAO.guardar(piloto);
        assertTrue(piloto.getId() > 0, "El id debería generarse al guardar");

        piloto.setNombre("Piloto Modificado");
        assertTrue(pilotoDAO.actualizar(piloto), "La actualización debería aplicarse");

        Piloto releido = pilotoDAO.buscarPorId(piloto.getId());
        assertEquals("Piloto Modificado", releido.getNombre());

        assertTrue(pilotoDAO.eliminar(piloto.getId()));
        assertNull(pilotoDAO.buscarPorId(piloto.getId()));
    }

    // ---------------- SENSOR ----------------

    @Test
    void sensor_guardarYEliminar() {
        SensorDAO sensorDAO = new SensorDAO();

        Sensor sensor = new Sensor();
        sensor.setTipo("Sensor Prueba");
        sensor.setFabricante("TestCorp");

        sensorDAO.guardar(sensor);
        assertTrue(sensor.getId() > 0, "El id debería generarse al guardar");

        assertTrue(sensorDAO.eliminar(sensor.getId()));
        assertTrue(sensorDAO.listarTodos().stream()
                .noneMatch(s -> s.getId() == sensor.getId()));
    }

    // ---------------- MISION ----------------

    @Test
    void mision_guardarBuscarYEliminar() {
        MisionDAO misionDAO = new MisionDAO();

        Mision mision = new Mision();
        mision.setNombre("Mision Prueba");
        mision.setDescripcion("Descripcion de prueba");
        mision.setUbicacion("Lote de pruebas");
        mision.setFecha(new Date());

        misionDAO.guardar(mision);
        assertTrue(mision.getId() > 0, "El id debería generarse al guardar");

        Mision releida = misionDAO.buscarPorId(mision.getId());
        assertEquals("Lote de pruebas", releida.getUbicacion());

        assertTrue(misionDAO.eliminar(mision.getId()));
        assertNull(misionDAO.buscarPorId(mision.getId()));
    }
}