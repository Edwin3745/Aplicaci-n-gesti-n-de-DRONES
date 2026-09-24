package co.edu.poli.sw2.servicios.proxy;

/**
 * Interfaz común del patrón Proxy. Solo expone la eliminación de un dron.
 */
public interface DronServicio {

    /**
     * Elimina el dron con el id indicado.
     *
     * @param id identificador del dron a eliminar.
     * @return {@code true} si se eliminó algún registro.
     */
    boolean eliminar(int id);
}