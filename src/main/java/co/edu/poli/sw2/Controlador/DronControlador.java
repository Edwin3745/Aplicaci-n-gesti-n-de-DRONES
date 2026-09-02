package co.edu.poli.sw2.Controlador;

import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.TipoDron;
import co.edu.poli.sw2.servicios.DemostracionPatron;
import co.edu.poli.sw2.servicios.DronBuilder;
import co.edu.poli.sw2.servicios.DronDAOImpl;
import co.edu.poli.sw2.servicios.DronPrototypeManager;
import co.edu.poli.sw2.servicios.GenericDAO;
import co.edu.poli.sw2.servicios.InformeDeIdentidad;
import co.edu.poli.sw2.servicios.ServicioException;

import java.util.List;
import java.util.Set;

/**
 * Controlador de las operaciones de negocio sobre drones.
 *
 * <p>Media entre la vista y la capa de persistencia: delega la construcción
 * de objetos en {@link DronBuilder}, el almacenamiento en un
 * {@link GenericDAO}, y traduce los fallos técnicos a mensajes que la vista
 * puede mostrar sin conocer detalles de la base de datos.</p>
 */
public class DronControlador {

    private final GenericDAO<Dron, Integer> dronDAO;

    /** Configuraciones base de dron que el usuario puede tomar como punto de partida. */
    private final DronPrototypeManager plantillas = new DronPrototypeManager();

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
        registrarPlantillasBase();
    }

    // ------------------------------------------------------------------
    // Configuraciones base (patrón Prototype)
    // ------------------------------------------------------------------

    /**
     * Registra las configuraciones de dron más habituales de la operación.
     *
     * <p>Aquí se ve cómo se reparten el trabajo los dos patrones de creación:
     * el {@link DronBuilder} arma la plantilla una sola vez, dato a dato, y a
     * partir de ese momento el {@link DronPrototypeManager} produce copias de
     * ella sin volver a pasar por la construcción ni por sus validaciones.</p>
     */
    private void registrarPlantillasBase() {
        plantillas.registrar("Fumigador estándar", new DronBuilder()
                .conTipo(TipoDron.AGRICULTURA)
                .conSerial("AGR-BASE")
                .conModelo("Agras T40")
                .conFabricante("DJI")
                .conPeso(38.0)
                .conCapacidadTanque(40.0)
                .build());

        plantillas.registrar("Vigilancia nocturna", new DronBuilder()
                .conTipo(TipoDron.VIGILANCIA)
                .conSerial("VIG-BASE")
                .conModelo("Matrice 30T")
                .conFabricante("DJI")
                .conPeso(3.7)
                .conDeteccionTermica(true)
                .build());
    }

    /**
     * Nombres de las configuraciones base disponibles.
     *
     * @return conjunto de nombres, en el orden en que fueron registrados.
     */
    public Set<String> nombresDePlantillas() {
        return plantillas.nombresRegistrados();
    }

    /**
     * Entrega una copia de la configuración base indicada.
     *
     * <p>El dron devuelto no se guarda: llega sin identificador para que el
     * usuario lo revise, ajuste lo que necesite y lo registre después como
     * cualquier otro dron.</p>
     *
     * @param nombre nombre de la configuración base.
     * @return copia independiente de la plantilla.
     * @throws OperacionFallidaException si no existe ninguna con ese nombre.
     */
    public Dron crearDesdePlantilla(String nombre) {
        try {
            return plantillas.obtenerClon(nombre);
        } catch (IllegalArgumentException e) {
            throw new OperacionFallidaException(e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    // Demostración de los patrones de creación
    // ------------------------------------------------------------------

    /**
     * Duplica el dron indicado y redacta la evidencia de que el clon es otro
     * objeto en memoria.
     *
     * <p>Es lo que ejecuta el botón "Clonar" de la interfaz. La copia la hace
     * {@link DronPrototypeManager}, y el informe deja constancia de las
     * identidades de ambos objetos y de que sus listas de sensores son
     * independientes.</p>
     *
     * @param original dron seleccionado en la tabla.
     * @return el clon obtenido y el informe que lo acredita.
     * @throws OperacionFallidaException si no hay ningún dron que copiar.
     */
    public DemostracionPatron clonarDron(Dron original) {
        try {
            Dron copia = plantillas.clonar(original);
            return new DemostracionPatron(
                    copia, InformeDeIdentidad.compararIdentidades(original, copia));

        } catch (IllegalArgumentException e) {
            throw new OperacionFallidaException(
                    "Selecciona un dron de la tabla para clonarlo.", e);
        }
    }

    /**
     * Construye un dron con {@link DronBuilder} encadenando las llamadas, y
     * redacta la secuencia ejecutada junto con el objeto resultante.
     *
     * <p>Es lo que ejecuta el botón "Builder" de la interfaz. Los datos son los
     * del formulario; los que estén vacíos se sustituyen por valores de muestra
     * para que la demostración funcione siempre, y el informe indica cuáles se
     * completaron.</p>
     *
     * @param tipo             subtipo a construir; si es nulo se usa AGRICULTURA.
     * @param serial           número de serie; si está vacío se usa uno de muestra.
     * @param modelo           modelo; si está vacío se usa uno de muestra.
     * @param fabricante       fabricante; si está vacío se usa uno de muestra.
     * @param peso             peso en kilogramos.
     * @param capacidadTanque  litros del tanque; solo aplica a AGRICULTURA.
     * @param deteccionTermica cámara térmica; solo aplica a VIGILANCIA.
     * @return el dron construido y el informe de su construcción.
     * @throws OperacionFallidaException si el builder rechaza los datos.
     */
    public DemostracionPatron construirConBuilder(TipoDron tipo, String serial,
                                                  String modelo, String fabricante,
                                                  double peso, double capacidadTanque,
                                                  boolean deteccionTermica) {

        TipoDron tipoUsado = tipo != null ? tipo : TipoDron.AGRICULTURA;
        String serialUsado = valorOMuestra(serial, "DEMO-BUILDER-01");
        String modeloUsado = valorOMuestra(modelo, "Agras T40");
        String fabricanteUsado = valorOMuestra(fabricante, "DJI");
        double pesoUsado = peso > 0 ? peso : 38.0;

        String llamadas = componerTraza(tipoUsado, serialUsado, modeloUsado,
                fabricanteUsado, pesoUsado, capacidadTanque, deteccionTermica);

        try {
            Dron dron = new DronBuilder()
                    .conTipo(tipoUsado)
                    .conSerial(serialUsado)
                    .conModelo(modeloUsado)
                    .conFabricante(fabricanteUsado)
                    .conPeso(pesoUsado)
                    .conCapacidadTanque(capacidadTanque)
                    .conDeteccionTermica(deteccionTermica)
                    .build();

            return new DemostracionPatron(
                    dron, InformeDeIdentidad.describirConstruccion(dron, llamadas));

        } catch (IllegalStateException e) {
            throw new OperacionFallidaException(e.getMessage(), e);
        }
    }

    /**
     * Escribe la secuencia de llamadas encadenadas tal como se ejecutó, con los
     * valores reales, para que el informe muestre el patrón en acción.
     *
     * @param tipo             subtipo construido.
     * @param serial           número de serie empleado.
     * @param modelo           modelo empleado.
     * @param fabricante       fabricante empleado.
     * @param peso             peso empleado.
     * @param capacidadTanque  capacidad del tanque empleada.
     * @param deteccionTermica valor de detección térmica empleado.
     * @return las llamadas encadenadas, una por línea.
     */
    private String componerTraza(TipoDron tipo, String serial, String modelo,
                                 String fabricante, double peso,
                                 double capacidadTanque, boolean deteccionTermica) {
        String salto = System.lineSeparator();
        return "  new DronBuilder()" + salto
             + "      .conTipo(TipoDron." + tipo.name() + ")" + salto
             + "      .conSerial(\"" + serial + "\")" + salto
             + "      .conModelo(\"" + modelo + "\")" + salto
             + "      .conFabricante(\"" + fabricante + "\")" + salto
             + "      .conPeso(" + peso + ")" + salto
             + "      .conCapacidadTanque(" + capacidadTanque + ")" + salto
             + "      .conDeteccionTermica(" + deteccionTermica + ")" + salto
             + "      .build();";
    }

    /**
     * Devuelve el texto recibido o, si viene vacío, un valor de muestra.
     *
     * @param valor   texto proporcionado por el formulario.
     * @param muestra valor a usar cuando el anterior está vacío.
     * @return el texto útil de los dos.
     */
    private static String valorOMuestra(String valor, String muestra) {
        return (valor == null || valor.isBlank()) ? muestra : valor.trim();
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

        // El Builder reúne los datos del formulario, los valida y decide qué
        // subtipo construir. El id no se le pasa: lo genera la base de datos.
        Dron dron;
        try {
            dron = new DronBuilder()
                    .conTipo(tipo)
                    .conSerial(serial)
                    .conModelo(modelo)
                    .conFabricante(fabricante)
                    .conPeso(peso)
                    .conCapacidadTanque(capacidadTanque)
                    .conDeteccionTermica(deteccionTermica)
                    .build();

        } catch (IllegalStateException e) {
            // El builder informa del dato que falla en un lenguaje que la vista
            // puede mostrar tal cual; solo hay que cambiar el tipo de excepción.
            throw new OperacionFallidaException(e.getMessage(), e);
        }

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