package co.edu.poli.sw2.Servicio;

import co.edu.poli.sw2.Config.ConexionBD;
import co.edu.poli.sw2.Modelo.Dron;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de la interfaz {@link GenericDAO} para la entidad {@link Dron}.
 *
 * Esta clase forma parte de la capa de servicio dentro del patrón MVC y se encarga
 * de persistir los datos de los drones en una base de datos PostgreSQL. Su objetivo
 * es encapsular todas las consultas SQL necesarias para guardar, consultar,
 * actualizar y eliminar instancias de {@link Dron} sin que la lógica del controlador
 * conozca los detalles de la base de datos.
 */
public class DronDAOImpl implements GenericDAO<Dron, Integer> {

    /**
     * Guarda un nuevo dron en la base de datos.
     *
     * @param dron instancia de {@link Dron} a registrar.
     */
    @Override
    public void guardar(Dron dron) {
        String sql = "INSERT INTO dron (id, serial, modelo, fabricante, peso) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, dron.getId());
            ps.setString(2, dron.getSerial());
            ps.setString(3, dron.getModelo());
            ps.setString(4, dron.getFabricante());
            ps.setFloat(5, dron.getPeso());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el dron", e);
        }
    }

    /**
     * Elimina un dron identificado por su ID.
     *
     * @param id identificador del dron a eliminar.
     * @return {@code true} si se eliminó el registro; {@code false} en caso contrario.
     */
    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM dron WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el dron", e);
        }
    }

    /**
     * Busca un dron por su identificador.
     *
     * @param id identificador del dron.
     * @return instancia de {@link Dron} encontrada o {@code null} si no existe.
     */
    @Override
    public Dron buscarPorId(Integer id) {
        String sql = "SELECT * FROM dron WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearDron(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el dron", e);
        }

        return null;
    }

    /**
     * Obtiene la lista completa de drones registrados en la base de datos.
     *
     * @return colección con todos los drones almacenados.
     */
    @Override
    public List<Dron> listarTodos() {
        List<Dron> drones = new ArrayList<>();
        String sql = "SELECT * FROM dron ORDER BY id";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                drones.add(mapearDron(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar drones", e);
        }

        return drones;
    }

    /**
     * Actualiza la información de un dron existente.
     *
     * @param dron objeto {@link Dron} con los datos actualizados.
     * @return {@code true} si la actualización fue exitosa; {@code false} si no se encontró el registro.
     */
    @Override
    public boolean actualizar(Dron dron) {
        String sql = "UPDATE dron SET serial = ?, modelo = ?, fabricante = ?, peso = ? WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dron.getSerial());
            ps.setString(2, dron.getModelo());
            ps.setString(3, dron.getFabricante());
            ps.setFloat(4, dron.getPeso());
            ps.setInt(5, dron.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el dron", e);
        }
    }

    /**
     * Convierte un registro de base de datos en un objeto {@link Dron}.
     *
     * @param rs resultado de la consulta SQL.
     * @return instancia de {@link Dron} mapeada desde la fila actual.
     * @throws SQLException si ocurre un error al leer los datos del ResultSet.
     */
    private Dron mapearDron(ResultSet rs) throws SQLException {
        Dron dron = new Dron();
        dron.setId(rs.getInt("id"));
        dron.setSerial(rs.getString("serial"));
        dron.setModelo(rs.getString("modelo"));
        dron.setFabricante(rs.getString("fabricante"));
        dron.setPeso(rs.getFloat("peso"));
        return dron;
    }
}