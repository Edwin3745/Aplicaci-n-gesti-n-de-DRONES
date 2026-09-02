package co.edu.poli.sw2.servicios;


import co.edu.poli.sw2.Modelo.Agricultura;
import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.TipoDron;
import co.edu.poli.sw2.Modelo.Vigilancia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link GenericDAO} para la jerarquía {@link Dron}.
 *
 * <p>La herencia se persiste con la estrategia de tabla única con columna
 * discriminadora: todos los subtipos comparten la tabla {@code dron} y la
 * columna {@code tipo} indica de qué subclase es cada fila. Los atributos
 * propios de un subtipo quedan en NULL en las filas de los demás.</p>
 *
 * <p>Todos los métodos trabajan sobre la conexión compartida que entrega
 * {@link ConexionBD}. El DAO cierra únicamente lo que él mismo abre
 * ({@link PreparedStatement} y {@link ResultSet}); la conexión no se cierra
 * nunca aquí, porque es propiedad del Singleton y las operaciones posteriores
 * la siguen necesitando.</p>
 */
public class DronDAOImpl implements GenericDAO<Dron, Integer> {

    /** Columnas comunes a todos los subtipos, en el orden en que se leen y escriben. */
    private static final String COLUMNAS =
            "id, tipo, serial, modelo, fabricante, peso, capacidad_tanque, deteccion_termica";

    /**
     * Crea el DAO. No abre ninguna conexión: cada operación pide la compartida
     * a {@link ConexionBD} en el momento de usarla.
     */
    public DronDAOImpl() {
    }

    /**
     * Guarda un nuevo dron.
     *
     * <p>El identificador lo genera la base de datos (columna SERIAL), por lo
     * que no se envía en la sentencia y se recupera después para asignarlo al
     * objeto recibido.</p>
     *
     * @param dron dron a registrar.
     */
    @Override
    public void guardar(Dron dron) {
        String sql = "INSERT INTO dron (tipo, serial, modelo, fabricante, peso, "
                   + "capacidad_tanque, deteccion_termica) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // La conexión no entra en el try-with-resources: pertenece al Singleton
        // y cerrarla dejaría inservibles las operaciones siguientes.
        try (PreparedStatement ps = ConexionBD.obtenerConexion()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // El tipo lo declara el propio objeto: no hace falta inspeccionar su clase.
            ps.setString(1, dron.getTipo().getCodigo());
            ps.setString(2, dron.getSerial());
            ps.setString(3, dron.getModelo());
            ps.setString(4, dron.getFabricante());
            ps.setDouble(5, dron.getPeso());

            asignarAtributosEspecificos(ps, dron, 6, 7);

            ps.executeUpdate();

            // Se recupera el id generado por la base y se refleja en el objeto.
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    dron.setId(claves.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al guardar el dron", e);
        }
    }

    /**
     * Elimina un dron por su identificador.
     *
     * @param id identificador del dron.
     * @return {@code true} si se eliminó alguna fila.
     */
    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM dron WHERE id = ?";

        // La conexión no entra en el try-with-resources: pertenece al Singleton
        // y cerrarla dejaría inservibles las operaciones siguientes.
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new ServicioException("Error al eliminar el dron", e);
        }
    }

    /**
     * Busca un dron por su identificador.
     *
     * @param id identificador del dron.
     * @return el dron encontrado, o {@code null} si no existe.
     */
    @Override
    public Dron buscarPorId(Integer id) {
        String sql = "SELECT " + COLUMNAS + " FROM dron WHERE id = ?";

        // La conexión no entra en el try-with-resources: pertenece al Singleton
        // y cerrarla dejaría inservibles las operaciones siguientes.
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearDron(rs);
                }
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al buscar el dron", e);
        }

        return null;
    }

    /**
     * Obtiene todos los drones registrados.
     *
     * @return lista con todos los drones, vacía si no hay ninguno.
     */
    @Override
    public List<Dron> listarTodos() {
        List<Dron> drones = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM dron ORDER BY id";

        // La conexión no entra en el try-with-resources: pertenece al Singleton
        // y cerrarla dejaría inservibles las operaciones siguientes.
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                drones.add(mapearDron(rs));
            }

        } catch (SQLException e) {
            throw new ServicioException("Error al listar drones", e);
        }

        return drones;
    }

    /**
     * Actualiza un dron existente.
     *
     * <p>El tipo también se actualiza: si el dron cambió de subtipo, la fila
     * debe reflejarlo junto con sus atributos específicos.</p>
     *
     * @param dron dron con los datos actualizados.
     * @return {@code true} si se actualizó alguna fila.
     */
    @Override
    public boolean actualizar(Dron dron) {
        String sql = "UPDATE dron SET tipo = ?, serial = ?, modelo = ?, fabricante = ?, "
                   + "peso = ?, capacidad_tanque = ?, deteccion_termica = ? WHERE id = ?";

        // La conexión no entra en el try-with-resources: pertenece al Singleton
        // y cerrarla dejaría inservibles las operaciones siguientes.
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {

            ps.setString(1, dron.getTipo().getCodigo());
            ps.setString(2, dron.getSerial());
            ps.setString(3, dron.getModelo());
            ps.setString(4, dron.getFabricante());
            ps.setDouble(5, dron.getPeso());

            asignarAtributosEspecificos(ps, dron, 6, 7);

            ps.setInt(8, dron.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new ServicioException("Error al actualizar el dron", e);
        }
    }

    // ------------------------------------------------------------------
    // Traducción entre la fila y el objeto
    // ------------------------------------------------------------------

    /**
     * Escribe en la sentencia los atributos que solo existen en un subtipo.
     *
     * <p>Es el único punto del DAO que necesita distinguir la subclase concreta.
     * La columna que no corresponde al subtipo se marca explícitamente como NULL,
     * tal como exige la restricción de coherencia del esquema.</p>
     *
     * @param ps            sentencia en preparación.
     * @param dron          dron cuyos datos se están escribiendo.
     * @param posTanque     posición del parámetro de capacidad_tanque.
     * @param posTermica    posición del parámetro de deteccion_termica.
     * @throws SQLException si falla la asignación de parámetros.
     */
    private void asignarAtributosEspecificos(PreparedStatement ps, Dron dron,
                                             int posTanque, int posTermica) throws SQLException {

        if (dron instanceof Agricultura agricultura) {
            ps.setDouble(posTanque, agricultura.getCapacidadTanque());
            ps.setNull(posTermica, Types.BOOLEAN);

        } else if (dron instanceof Vigilancia vigilancia) {
            ps.setNull(posTanque, Types.DOUBLE);
            ps.setBoolean(posTermica, vigilancia.isDeteccionTermica());

        } else {
            throw new IllegalStateException(
                    "Subtipo de dron no soportado por la persistencia: "
                            + dron.getClass().getSimpleName());
        }
    }

    /**
     * Convierte la fila actual del {@link ResultSet} en la subclase de
     * {@link Dron} que corresponda.
     *
     * <p>La columna discriminadora determina qué construir, y la construcción
     * se delega en la fábrica del subtipo correspondiente, de modo que este DAO
     * no invoca directamente el constructor de ninguna subclase.</p>
     *
     * @param rs resultado posicionado en la fila a convertir.
     * @return instancia de {@link Agricultura} o {@link Vigilancia}.
     * @throws SQLException si falla la lectura de la fila.
     */
    private Dron mapearDron(ResultSet rs) throws SQLException {

        TipoDron tipo = TipoDron.desdeCodigo(rs.getString("tipo"));

        int id = rs.getInt("id");
        String serial = rs.getString("serial");
        String modelo = rs.getString("modelo");
        String fabricante = rs.getString("fabricante");
        double peso = rs.getDouble("peso");

        // getDouble y getBoolean devuelven 0 y false cuando la columna es NULL,
        // así que cada rama lee únicamente la columna que su subtipo utiliza.
        return switch (tipo) {
            case AGRICULTURA -> AgriculturaFactory.crearDron(
                    id, serial, modelo, fabricante, peso,
                    rs.getDouble("capacidad_tanque"));

            case VIGILANCIA -> VigilanciaFactory.crearDron(
                    id, serial, modelo, fabricante, peso,
                    rs.getBoolean("deteccion_termica"));
        };
    }
}