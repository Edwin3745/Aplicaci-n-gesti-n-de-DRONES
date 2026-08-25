package co.edu.poli.sw2;

import co.edu.poli.sw2.Modelo.Agricultura;
import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.TipoDron;
import co.edu.poli.sw2.Servicio.DronDAOImpl;
import co.edu.poli.sw2.Servicio.DronFactory;

import java.util.List;

/**
 * Prueba de integración manual del DAO contra la base de datos real.
 */
public class PruebaDAO {

    public static void main(String[] args) {

        DronDAOImpl dao = new DronDAOImpl();

        System.out.println("--- Listar: cada fila vuelve como su subclase ---");
        List<Dron> drones = dao.listarTodos();
        for (Dron d : drones) {
            System.out.printf("%-10s %-14s %s%n",
                    d.getSerial(),
                    d.getClass().getSimpleName(),
                    d.descripcionOperativa());
        }

        System.out.println("\n--- Guardar: el id lo genera la base ---");
        Dron nuevo = DronFactory.crearDron(TipoDron.AGRICULTURA, 0, "AGR-999",
                "Prueba DAO", "TestCorp", 30.0, 25.0, false);
        System.out.println("Id antes de guardar:   " + nuevo.getId());
        dao.guardar(nuevo);
        System.out.println("Id despues de guardar: " + nuevo.getId());

        System.out.println("\n--- Buscar por id: se reconstruye la subclase ---");
        Dron leido = dao.buscarPorId(nuevo.getId());
        System.out.println("Clase:  " + leido.getClass().getSimpleName());
        System.out.println("Tanque: " + ((Agricultura) leido).getCapacidadTanque() + " L");

        System.out.println("\n--- Actualizar ---");
        leido.setModelo("Modelo Actualizado");
        System.out.println("Actualizado: " + dao.actualizar(leido));
        System.out.println("Releido:     " + dao.buscarPorId(leido.getId()).getModelo());

        System.out.println("\n--- Eliminar ---");
        System.out.println("Eliminado:   " + dao.eliminar(leido.getId()));
        System.out.println("Sigue ahi?:  " + (dao.buscarPorId(leido.getId()) != null));
    }
}