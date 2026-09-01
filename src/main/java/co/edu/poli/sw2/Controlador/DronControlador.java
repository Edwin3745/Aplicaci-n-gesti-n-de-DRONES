package co.edu.poli.sw2.Controlador;

import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.TipoDron;
import co.edu.poli.sw2.servicios.DronDAOImpl;
import co.edu.poli.sw2.servicios.DronFactory;
import co.edu.poli.sw2.servicios.GenericDAO;
import co.edu.poli.sw2.servicios.ServicioException;

import java.util.List;

/**
 * Controlador de las operaciones de negocio sobre drones.
 *
 * <p>Media entre la vista y la capa de persistencia: delega la construcción
 * de objetos en {@link DronFactory}, el almacenamiento en un
 * {@link GenericDAO}, y traduce los fallos técnicos a mensajes que la vista
 * puede mostrar sin conocer detalles de la base de datos.</p>
 */
public class DronControlador {

    private final GenericDAO<Dron, Integer> dronDAO;

    /**
     * Construye el controlador con la implementación de DAO por defecto.
     */
    public DronControlador() {
        this(new DronDAOImpl());
    }

    /**
     * Construye el controlador con el DAO indicado.
     *
     * <p>Permite sustituir la implementación de persistencia sin modificar
     * esta clase, lo que habilita las pruebas unitarias con dobles de prueba.</p>
     *
     * @param dronDAO implementación de acceso a datos a utilizar.
     */
    public DronControlador(GenericDAO<Dron, Integer> dronDAO) {
        this.dronDAO = dronDAO;
    }

    /**
     * Registra un nuevo dron.
     *
     * @param tipo             subtipo de dron a registrar.
     * @param serial           número de serie, debe ser único.
     * @param modelo           modelo del dron.
     * @param fabricante       fabricante del dron.
     * @param peso             peso en kilogramos.
     * @param capacidadTanque  litros del tanque; solo aplica a AGRICULTURA.
     * @param deteccionTermica cámara térmica; solo aplica a VIGILANCIA.
     * @throws OperacionFallidaException si la operación no puede completarse.
     */
    public void registrarDron(TipoDron tipo, String serial, String modelo,
                              String fabricante, double peso,
                              double capacidadTanque, boolean deteccionTermica) {

        validarDatosComunes(serial, modelo, fabricante, peso);

        // El id lo genera la base de datos, por eso se envía 0.
        Dron dron = DronFactory.crearDron(tipo, 0, serial, modelo, fabricante,
                                          peso, capacidadTanque, deteccionTermica);

        try {
            dronDAO.guardar(dron);
        } catch (ServicioException e) {
            throw traducir(e, "registrar", serial);
        }
    }

    /**
     * Elimina un dron por su identificador.
     *
     * @param id identificador del dron.
     * @return {@code true} si se eliminó algún registro.
     * @throws OperacionFallidaException si la operación no puede completarse.
     */
    public boolean eliminarDron(int id) {
        try {
            return dronDAO.eliminar(id);
        } catch (ServicioException e) {
            throw traducir(e, "eliminar", String.valueOf(id));
        }
    }

    /**
     * Busca un dron por su identificador.
     *
     * @param id identificador del dron.
     * @return el dron encontrado, o {@code null} si no existe.
     * @throws OperacionFallidaException si la consulta no puede completarse.
     */
    public Dron buscarDron(int id) {
        try {
            return dronDAO.buscarPorId(id);
        } catch (ServicioException e) {
            throw traducir(e, "consultar", String.valueOf(id));
        }
    }

    /**
     * Obtiene todos los drones registrados.
     *
     * @return lista de drones, vacía si no hay ninguno.
     * @throws OperacionFallidaException si la consulta no puede completarse.
     */
    public List<Dron> listarDrones() {
        try {
            return dronDAO.listarTodos();
        } catch (ServicioException e) {
            throw traducir(e, "listar", null);
        }
    }

    /**
     * Actualiza un dron existente.
     *
     * @param dron dron con los datos actualizados.
     * @return {@code true} si se actualizó algún registro.
     * @throws OperacionFallidaException si la operación no puede completarse.
     */
    public boolean actualizarDron(Dron dron) {
        if (dron == null) {
            throw new OperacionFallidaException("No hay ningún dron seleccionado.");
        }
        validarDatosComunes(dron.getSerial(), dron.getModelo(),
                            dron.getFabricante(), dron.getPeso());
        try {
            return dronDAO.actualizar(dron);
        } catch (ServicioException e) {
            throw traducir(e, "actualizar", dron.getSerial());
        }
    }

    // ------------------------------------------------------------------
    // Validación y traducción de errores
    // ------------------------------------------------------------------

    /**
     * Comprueba las reglas de negocio comunes antes de tocar la base de datos.
     *
     * @throws OperacionFallidaException si algún dato no cumple las reglas.
     */
    private void validarDatosComunes(String serial, String modelo,
                                     String fabricante, double peso) {
        if (serial == null || serial.isBlank()) {
            throw new OperacionFallidaException("El serial es obligatorio.");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new OperacionFallidaException("El modelo es obligatorio.");
        }
        if (fabricante == null || fabricante.isBlank()) {
            throw new OperacionFallidaException("El fabricante es obligatorio.");
        }
        if (peso < 0) {
            throw new OperacionFallidaException("El peso no puede ser negativo.");
        }
    }

    /**
     * Convierte un fallo técnico de la capa de servicio en un mensaje que la
     * vista puede mostrar directamente al usuario.
     *
     * <p>La excepción original se conserva como causa para el registro de
     * errores, pero su texto técnico nunca llega a la interfaz.</p>
     *
     * @param e         excepción originada en la capa de servicio.
     * @param operacion verbo de la operación que se intentaba, para el mensaje.
     * @param referencia dato identificador del registro implicado, puede ser nulo.
     * @return excepción con un mensaje apto para el usuario.
     */
    private OperacionFallidaException traducir(ServicioException e,
                                               String operacion, String referencia) {
        if (e.esDuplicado()) {
            return new OperacionFallidaException(
                    "Ya existe un dron con el serial " + referencia + ".", e);
        }
        if (e.esViolacionDeRegla()) {
            return new OperacionFallidaException(
                    "Los datos del dron no cumplen las reglas del sistema. "
                    + "Revisa que el tipo y sus atributos correspondan.", e);
        }
        if (e.esReferenciaInvalida()) {
            return new OperacionFallidaException(
                    "El dron está asociado a otros registros y no puede modificarse.", e);
        }
        return new OperacionFallidaException(
                "No se pudo " + operacion + " el dron. "
                + "Verifica la conexión con la base de datos.", e);
    }
}