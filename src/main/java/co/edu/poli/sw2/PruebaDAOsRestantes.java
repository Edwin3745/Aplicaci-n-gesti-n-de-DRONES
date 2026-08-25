package co.edu.poli.sw2;

import co.edu.poli.sw2.Modelo.Mision;
import co.edu.poli.sw2.Modelo.Piloto;
import co.edu.poli.sw2.Modelo.Sensor;
import co.edu.poli.sw2.Servicio.MisionDAO;
import co.edu.poli.sw2.Servicio.PilotoDAO;
import co.edu.poli.sw2.Servicio.SensorDAO;

import java.util.Date;

/**
 * Prueba de integración manual de PilotoDAO, SensorDAO y MisionDAO.
 */
public class PruebaDAOsRestantes {

    public static void main(String[] args) {

        // ---------------- PILOTO ----------------
        PilotoDAO pilotoDAO = new PilotoDAO();
        System.out.println("--- Pilotos en la base: " + pilotoDAO.listarTodos().size() + " ---");
        pilotoDAO.listarTodos().forEach(p ->
                System.out.println("  " + p.getId() + " - " + p.getNombre()));

        Piloto nuevoPiloto = new Piloto();
        nuevoPiloto.setNombre("Piloto Prueba");
        nuevoPiloto.setExperiencia(3);
        nuevoPiloto.setTelefono("3000000000");
        pilotoDAO.guardar(nuevoPiloto);
        System.out.println("Guardado con id: " + nuevoPiloto.getId());

        nuevoPiloto.setNombre("Piloto Modificado");
        System.out.println("Actualizado: " + pilotoDAO.actualizar(nuevoPiloto));
        System.out.println("Releido:     " + pilotoDAO.buscarPorId(nuevoPiloto.getId()).getNombre());
        System.out.println("Eliminado:   " + pilotoDAO.eliminar(nuevoPiloto.getId()));

        // ---------------- SENSOR ----------------
        SensorDAO sensorDAO = new SensorDAO();
        System.out.println("\n--- Sensores en la base: " + sensorDAO.listarTodos().size() + " ---");
        System.out.println("Sensores del dron 1: " + sensorDAO.listarPorDron(1).size());

        Sensor nuevoSensor = new Sensor();
        nuevoSensor.setTipo("Sensor Prueba");
        nuevoSensor.setFabricante("TestCorp");
        sensorDAO.guardar(nuevoSensor);
        System.out.println("Guardado con id: " + nuevoSensor.getId());
        System.out.println("Eliminado:       " + sensorDAO.eliminar(nuevoSensor.getId()));

        // ---------------- MISION ----------------
        MisionDAO misionDAO = new MisionDAO();
        System.out.println("\n--- Misiones en la base: " + misionDAO.listarTodos().size() + " ---");
        misionDAO.listarTodos().forEach(m ->
                System.out.println("  " + m.getNombre() + " (" + m.getFecha() + ")"));

        Mision nuevaMision = new Mision();
        nuevaMision.setNombre("Mision Prueba");
        nuevaMision.setDescripcion("Descripcion de prueba");
        nuevaMision.setUbicacion("Lote de pruebas");
        nuevaMision.setFecha(new Date());
        misionDAO.guardar(nuevaMision);
        System.out.println("Guardada con id: " + nuevaMision.getId());

        Mision leida = misionDAO.buscarPorId(nuevaMision.getId());
        System.out.println("Ubicacion releida: " + leida.getUbicacion());
        System.out.println("Eliminada:         " + misionDAO.eliminar(leida.getId()));
    }
}