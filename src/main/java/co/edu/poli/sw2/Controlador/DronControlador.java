package co.edu.poli.sw2.Controlador;

import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Servicio.DronDAO;
import co.edu.poli.sw2.Servicio.DronDAOImpl;

import java.util.List;

public class DronControlador {

    private final DronDAO dronDAO;

    public DronControlador() {
        this.dronDAO = new DronDAOImpl();
    }

    public void registrarDron(int id, String serial, String modelo, String fabricante, float peso) {
        Dron dron = new Dron(id, serial, modelo, fabricante, peso);
        dronDAO.guardar(dron);
    }

    public boolean eliminarDron(int id) {
        return dronDAO.eliminar(id);
    }

    public Dron buscarDron(int id) {
        return dronDAO.buscarPorId(id);
    }

    public List<Dron> listarDrones() {
        return dronDAO.listarTodos();
    }

    public boolean actualizarDron(Dron dron) {
        return dronDAO.actualizar(dron);
    }
}