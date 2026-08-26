package co.edu.poli.sw2.Servicio;

import co.edu.poli.sw2.Config.ConexionBD;
import co.edu.poli.sw2.Modelo.Sensor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link GenericDAO} para la entidad {@link Sensor}.
 */
public class SensorDAO implements GenericDAO<Sensor, Integer> {

    private static final String COLUMNAS = "id, tipo, fabricante, dron_id";

    @Override
    public void guardar(Sensor sensor) {
        String sql = "INSERT INTO sensor (tipo, fabricante, dron_id) VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, sensor.getTipo());
            ps.setString(2, sensor.getFabricante());
            ps.setNull(3, Types.INTEGER);

            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    sensor.setId(claves.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al guardar el sensor", e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM sensor WHERE id = ?";

        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new ServicioException("Error al eliminar el sensor", e);
        }
    }

    @Override
    public Sensor buscarPorId(Integer id) {
        String sql = "SELECT " + COLUMNAS + " FROM sensor WHERE id = ?";

        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearSensor(rs);
                }
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al buscar el sensor", e);
        }

        return null;
    }

    @Override
    public List<Sensor> listarTodos() {
        List<Sensor> sensores = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM sensor ORDER BY id";

        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sensores.add(mapearSensor(rs));
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al listar sensores", e);
        }

        return sensores;
    }

    /**
     * Lista los sensores montados en un dron concreto.
     *
     * @param dronId identificador del dron.
     * @return sensores asociados a ese dron.
     */
    public List<Sensor> listarPorDron(int dronId) {
        List<Sensor> sensores = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM sensor WHERE dron_id = ? ORDER BY id";

        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, dronId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sensores.add(mapearSensor(rs));
                }
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al listar sensores del dron", e);
        }

        return sensores;
    }

    @Override
    public boolean actualizar(Sensor sensor) {
        String sql = "UPDATE sensor SET tipo = ?, fabricante = ? WHERE id = ?";

        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sensor.getTipo());
            ps.setString(2, sensor.getFabricante());
            ps.setInt(3, sensor.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new ServicioException("Error al actualizar el sensor", e);
        }
    }

    private Sensor mapearSensor(ResultSet rs) throws SQLException {
        Sensor sensor = new Sensor();
        sensor.setId(rs.getInt("id"));
        sensor.setTipo(rs.getString("tipo"));
        sensor.setFabricante(rs.getString("fabricante"));
        return sensor;
    }
}