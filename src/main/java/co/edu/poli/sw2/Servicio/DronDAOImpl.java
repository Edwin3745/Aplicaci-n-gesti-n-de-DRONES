package co.edu.poli.sw2.Servicio;

import co.edu.poli.sw2.Modelo.Dron;

import java.util.ArrayList;
import java.util.List;

public class DronDAOImpl implements DronDAO {

    private final List<Dron> drones = new ArrayList<>();

    @Override
    public void guardar(Dron dron) {
        drones.add(dron);
    }

    @Override
    public boolean eliminar(int id) {
        return drones.removeIf(d -> d.getId() == id);
    }

    @Override
    public Dron buscarPorId(int id) {
        return drones.stream()
                .filter(d -> d.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Dron> listarTodos() {
        return new ArrayList<>(drones);
    }

    @Override
    public boolean actualizar(Dron dron) {
        Dron existente = buscarPorId(dron.getId());
        if (existente == null) {
            return false;
        }
        existente.setSerial(dron.getSerial());
        existente.setModelo(dron.getModelo());
        existente.setFabricante(dron.getFabricante());
        existente.setPeso(dron.getPeso());
        return true;
    }
}