package co.edu.poli.sw2.Controlador;

import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Servicio.GenericDAO;
import co.edu.poli.sw2.Servicio.DronDAOImpl;

import java.util.List;

/**
 * Controlador responsable de gestionar las operaciones de negocio relacionadas
 * con los drones dentro del patrón MVC.
 *
 * <p>Esta clase actúa como intermediario entre la capa de vista y la capa de
 * acceso a datos. Su función principal es coordinar la creación, consulta,
 * actualización y eliminación de instancias de {@link Dron}, delegando la
 * persistencia en la implementación de {@link GenericDAO}.</p>
 *
 */
public class DronControlador {

    /**
     * Referencia a la capa de acceso a datos para gestionar objetos {@link Dron}.
     */
    private final GenericDAO<Dron, Integer> dronDAO;

    /**
     * Construye un nuevo controlador de drones.
     *
     * <p>En este constructor se crea la implementación concreta del DAO que
     * será utilizada para persistir y consultar los drones.</p>
     */
    public DronControlador() {
        this.dronDAO = new DronDAOImpl();
    }

    /**
     * Registra un nuevo dron en el sistema.
     *
     */
    public void registrarDron(int id, String serial, String modelo, String fabricante, float peso) {
        Dron dron = new Dron(id, serial, modelo, fabricante, peso);
        dronDAO.guardar(dron);
    }

    /**
     * Elimina un dron del sistema según su identificador.
    
     */
    public boolean eliminarDron(int id) {
        return dronDAO.eliminar(id);
    }

    /**
     * Busca un dron por su identificador.
     */
    public Dron buscarDron(int id) {
        return dronDAO.buscarPorId(id);
    }

    /**
     * Obtiene la lista completa de drones registrados.
     */
    public List<Dron> listarDrones() {
        return dronDAO.listarTodos();
    }

    /**
     * Actualiza la información de un dron existente.
     * <p>La actualización se realiza sobre la entidad recibida, delegando la
     * operación de persistencia a la capa DAO correspondiente.</p>
     */
    public boolean actualizarDron(Dron dron) {
        return dronDAO.actualizar(dron);
    }
}