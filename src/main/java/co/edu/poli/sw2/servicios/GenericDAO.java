package co.edu.poli.sw2.servicios;

import java.util.List;

/**
 * Contrato genérico para las operaciones básicas de persistencia de entidades.
 *
 * Esta interfaz define un conjunto de métodos reutilizables que permiten a la capa
 * de servicio gestionar cualquier tipo de entidad dentro del patrón MVC. Su objetivo
 * es abstraer la lógica de almacenamiento y ofrecer un comportamiento uniforme para
 * implementaciones concretas, como las que manejan drones, pilotos o misiones.
 *
 * @param <T> tipo de entidad que será gestionada.
 * @param <ID> tipo del identificador único de la entidad.
 */
public interface GenericDAO<T, ID> {

    /**
     * Guarda una nueva entidad.
     *
     * @param entidad instancia de la entidad a registrar.
     */
    void guardar(T entidad);

    /**
     * Elimina una entidad a partir de su identificador.
     *
     * @param id identificador de la entidad a eliminar.
     * @return {@code true} si la eliminación fue exitosa; {@code false} en caso contrario.
     */
    boolean eliminar(ID id);

    /**
     * Busca una entidad por su identificador.
     *
     * @param id identificador de la entidad buscada.
     * @return entidad encontrada o {@code null} si no existe.
     */
    T buscarPorId(ID id);

    /**
     * Obtiene todas las entidades registradas.
     *
     * @return lista con todas las entidades almacenadas.
     */
    List<T> listarTodos();

    /**
     * Actualiza los datos de una entidad existente.
     *
     * @param entidad entidad con la información actualizada.
     * @return {@code true} si la actualización fue exitosa; {@code false} si no se pudo aplicar.
     */
    boolean actualizar(T entidad);
}