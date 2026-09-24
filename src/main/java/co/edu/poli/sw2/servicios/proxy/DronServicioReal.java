package co.edu.poli.sw2.servicios.proxy;

import co.edu.poli.sw2.modelo.Dron;
import co.edu.poli.sw2.servicios.dao.GenericDAO;

/**
 * Objeto real del patrón Proxy. Elimina el dron usando el DAO,
 * sin ninguna regla de acceso.
 */
public class DronServicioReal implements DronServicio {

    private final GenericDAO<Dron, Integer> dao;

    /**
     * Crea el servicio real sobre el DAO indicado.
     *
     * @param dao acceso a datos de los drones.
     */
    public DronServicioReal(GenericDAO<Dron, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public boolean eliminar(int id) {
        return dao.eliminar(id);
    }
}