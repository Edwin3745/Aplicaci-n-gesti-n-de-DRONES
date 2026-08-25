package co.edu.poli.sw2.Servicio;

import co.edu.poli.sw2.Config.ConexionBD;
import co.edu.poli.sw2.Modelo.Mision;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link GenericDAO} para la entidad {@link Mision}.
 */
public class MisionDAO implements GenericDAO<Mision, Integer> {

    private static final String COLUMNAS = "id, nombre, descripcion, ubicacion, fecha";

    @Override
    public void guardar(Mision mision) {
        String sql = "INSERT INTO mision (nombre, descripcion, ubicacion, fecha) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, mision.getNombre());
            ps.setString(2, mision.getDescripcion());
            ps.setString(3, mision.getUbicacion());
            ps.setDate(4, new java.sql.Date(mision.getFecha().getTime()));

            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    mision.setId(claves.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al guardar la misión", e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM mision WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new ServicioException("Error al eliminar la misión", e);
        }
    }

    @Override
    public Mision buscarPorId(Integer id) {
        String sql = "SELECT " + COLUMNAS + " FROM mision WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMision(rs);
                }
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al buscar la misión", e);
        }

        return null;
    }

    @Override
    public List<Mision> listarTodos() {
        List<Mision> misiones = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM mision ORDER BY fecha DESC";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                misiones.add(mapearMision(rs));
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al listar misiones", e);
        }

        return misiones;
    }

    @Override
    public boolean actualizar(Mision mision) {
        String sql = "UPDATE mision SET nombre = ?, descripcion = ?, ubicacion = ?, "
                   + "fecha = ? WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mision.getNombre());
            ps.setString(2, mision.getDescripcion());
            ps.setString(3, mision.getUbicacion());
            ps.setDate(4, new java.sql.Date(mision.getFecha().getTime()));
            ps.setInt(5, mision.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new ServicioException("Error al actualizar la misión", e);
        }
    }

    /**
     * Convierte la fila actual del {@link ResultSet} en una {@link Mision}.
     *
     * @param rs resultado posicionado en la fila a convertir.
     * @return misión construida a partir de la fila.
     * @throws SQLException si falla la lectura.
     */
    private Mision mapearMision(ResultSet rs) throws SQLException {
        Mision mision = new Mision();
        mision.setId(rs.getInt("id"));
        mision.setNombre(rs.getString("nombre"));
        mision.setDescripcion(rs.getString("descripcion"));
        mision.setUbicacion(rs.getString("ubicacion"));
        mision.setFecha(rs.getDate("fecha"));
        return mision;
    }
}