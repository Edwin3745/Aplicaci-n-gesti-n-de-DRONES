package co.edu.poli.sw2.Controlador;

import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.TipoDron;
import co.edu.poli.sw2.Servicio.DronDAOImpl;
import co.edu.poli.sw2.Servicio.DronFactory;
import co.edu.poli.sw2.Servicio.GenericDAO;

import java.util.List;

/**
 * Controlador responsable de gestionar las operaciones de negocio relacionadas
 * con los drones dentro del patrón MVC.
 *
 * <p>Esta clase actúa como intermediario entre la capa de vista y la capa de
 * acceso a datos. Su función principal es coordinar la creación, consulta,
 * actualización y eliminación de instancias de {@link Dron}, delegando la
 * construcción en {@link DronFactory} y la persistencia en la implementación
 * de {@link GenericDAO}.</p>
 */
public class DronControlador {

    /**
     * Referencia a la capa de acceso a datos para gestionar objetos {@link Dron}.
     */
    private final GenericDAO<Dron, Integer> dronDAO;

    /**
     * Construye un nuevo controlador de drones.
     */
    public DronControlador() {
        this.dronDAO = new DronDAOImpl();
    }

    /**
     * Registra un nuevo dron en el sistema.
     *
     * <p>El controlador no construye el dron directamente: delega esa decisión
     * en {@link DronFactory}, que devuelve la subclase concreta según el tipo
     * indicado.</p>
     *
     * @param tipo              subtipo de dron a registrar
     * @param id                identificador del dron
     * @param serial            número de serie
     * @param modelo            modelo del dron
     * @param fabricante        fabricante del dron
     * @param peso              peso en kilogramos
     * @param capacidadTanque   litros del tanque; solo aplica si el tipo es AGRICULTURA
     * @param deteccionTermica  cámara térmica; solo aplica si el tipo es VIGILANCIA
     */
        // TODO PASO 5: agregar el ComboBox de tipo y los campos de capacidadTanque
        // y deteccionTermica al formulario, y pasarlos aquí.
        // controlador.registrarDron(tipo, id, serial, modelo, fabricante, peso,
        //                           capacidadTanque, deteccionTermica);
    public void registrarDron(TipoDron tipo, int id, String serial, String modelo,
                              String fabricante, double peso,
                              double capacidadTanque, boolean deteccionTermica) {

        Dron dron = DronFactory.crearDron(tipo, id, serial, modelo, fabricante,
                                          peso, capacidadTanque, deteccionTermica);
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
     */
    public boolean actualizarDron(Dron dron) {
        return dronDAO.actualizar(dron);
    }
}