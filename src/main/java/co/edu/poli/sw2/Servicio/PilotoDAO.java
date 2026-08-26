package co.edu.poli.sw2.Servicio;

import co.edu.poli.sw2.Config.ConexionBD;
import co.edu.poli.sw2.Modelo.Piloto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link GenericDAO} para la entidad {@link Piloto}.
 */
public class PilotoDAO implements GenericDAO<Piloto, Integer> {

    private static final String COLUMNAS = "id, nombre, experiencia, telefono";

    /**
     * Guarda un nuevo piloto. El identificador lo genera la base de datos.
     *
     * @param piloto piloto a registrar.
     */
    @Override
    public void guardar(Piloto piloto) {
        String sql = "INSERT INTO piloto (nombre, experiencia, telefono) VALUES (?, ?, ?)";


        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, piloto.getNombre());
            ps.setInt(2, piloto.getExperiencia());
            ps.setString(3, piloto.getTelefono());

            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    piloto.setId(claves.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al guardar el piloto", e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM piloto WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new ServicioException("Error al eliminar el piloto", e);
        }
    }

    @Override
    public Piloto buscarPorId(Integer id) {
        String sql = "SELECT " + COLUMNAS + " FROM piloto WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPiloto(rs);
                }
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al buscar el piloto", e);
        }

        return null;
    }

    @Override
    public List<Piloto> listarTodos() {
        List<Piloto> pilotos = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM piloto ORDER BY id";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pilotos.add(mapearPiloto(rs));
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al listar pilotos", e);
        }

        return pilotos;
    }

    @Override
    public boolean actualizar(Piloto piloto) {
        String sql = "UPDATE piloto SET nombre = ?, experiencia = ?, telefono = ? WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, piloto.getNombre());
            ps.setInt(2, piloto.getExperiencia());
            ps.setString(3, piloto.getTelefono());
            ps.setInt(4, piloto.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new ServicioException("Error al actualizar el piloto", e);
        }
    }

    /**
     * Convierte la fila actual del {@link ResultSet} en un {@link Piloto}.
     *
     * @param rs resultado posicionado en la fila a convertir.
     * @return piloto construido a partir de la fila.
     * @throws SQLException si falla la lectura.
     */
    private Piloto mapearPiloto(ResultSet rs) throws SQLException {
        Piloto piloto = new Piloto();
        piloto.setId(rs.getInt("id"));
        piloto.setNombre(rs.getString("nombre"));
        piloto.setExperiencia(rs.getInt("experiencia"));
        piloto.setTelefono(rs.getString("telefono"));
        return piloto;
    }
}