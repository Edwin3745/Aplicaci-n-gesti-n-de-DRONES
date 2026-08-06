package co.edu.poli.sw2.Servicio;

import co.edu.poli.sw2.Modelo.Dron;

import java.util.List;

public interface DronDAO {

    void guardar(Dron dron);

    boolean eliminar(int id);

    Dron buscarPorId(int id);

    List<Dron> listarTodos();

    boolean actualizar(Dron dron);
}