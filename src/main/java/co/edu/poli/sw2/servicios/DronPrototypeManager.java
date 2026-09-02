package co.edu.poli.sw2.servicios;

import co.edu.poli.sw2.modelo.Agricultura;
import co.edu.poli.sw2.modelo.Dron;
import co.edu.poli.sw2.modelo.Sensor;
import co.edu.poli.sw2.modelo.Vigilancia;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registro de configuraciones base de dron y única clase capaz de copiarlos,
 * según el patrón Prototype.
 *
 * <p>Guarda drones ya configurados bajo una clave legible —"fumigador
 * estándar", "vigilancia nocturna"— y entrega copias de ellos cuando se
 * piden. El usuario que necesita dar de alta un dron parecido a uno habitual
 * parte de la plantilla en lugar de rellenar el formulario desde cero. Con
 * {@link #clonar(Dron)} puede además duplicarse cualquier dron, no solo los
 * registrados.</p>
 *
 * <p><strong>Toda la mecánica de la copia vive aquí, no en el modelo.</strong>
 * Las clases de {@code modelo} no conocen el patrón: no implementan ninguna
 * interfaz de clonación ni tienen constructores copia. Este servicio construye
 * el duplicado usando únicamente la API pública del modelo, de modo que el
 * dominio queda libre de responsabilidades de infraestructura.</p>
 *
 * <p>El registro nunca entrega el prototipo guardado, sino una copia. De lo
 * contrario, quien lo recibiera podría modificarlo y contaminar la plantilla
 * para todos los usos siguientes. Por la misma razón, lo que se guarda al
 * registrar es también una copia y no el objeto recibido.</p>
 */
public class DronPrototypeManager {

    /**
     * Prototipos registrados, indexados por su nombre.
     *
     * <p>Se usa {@link LinkedHashMap} para que el orden de presentación en la
     * interfaz sea el orden en que se registraron, y no uno arbitrario.</p>
     */
    private final Map<String, Dron> prototipos = new LinkedHashMap<>();

    /**
     * Crea un registro de prototipos vacío.
     */
    public DronPrototypeManager() {
    }

    /**
     * Registra un dron como plantilla bajo la clave indicada.
     *
     * <p>Lo que se guarda es una copia del dron recibido, no el dron mismo: si
     * quien lo registró siguiera modificándolo, la plantilla no debe cambiar
     * con él.</p>
     *
     * @param clave nombre con el que se recuperará la plantilla; no puede estar
     *              vacío. Si la clave ya existía, se reemplaza.
     * @param dron  dron que sirve de modelo; no puede ser nulo.
     * @throws IllegalArgumentException si la clave está vacía o el dron es nulo.
     */
    public void registrar(String clave, Dron dron) {
        if (clave == null || clave.isBlank()) {
            throw new IllegalArgumentException(
                    "La clave del prototipo no puede estar vacía.");
        }
        prototipos.put(clave, clonar(dron));
    }

    /**
     * Entrega una copia independiente de la plantilla registrada.
     *
     * <p>Cada llamada devuelve un objeto nuevo: dos clones de la misma
     * plantilla no comparten nada, ni siquiera su lista de sensores.</p>
     *
     * @param clave nombre de la plantilla buscada.
     * @return copia del prototipo, lista para modificarse y guardarse.
     * @throws IllegalArgumentException si no hay ninguna plantilla con esa clave.
     */
    public Dron obtenerClon(String clave) {
        Dron prototipo = prototipos.get(clave);
        if (prototipo == null) {
            throw new IllegalArgumentException(
                    "No hay ninguna configuración registrada con el nombre: " + clave);
        }
        return clonar(prototipo);
    }

    /**
     * Crea una copia independiente de cualquier dron, esté registrado o no.
     *
     * <p>Es el punto de entrada que usa la vista para duplicar el dron
     * seleccionado en la tabla, y el que emplean internamente
     * {@link #registrar(String, Dron)} y {@link #obtenerClon(String)}.</p>
     *
     * <p>Qué se copia y qué no:</p>
     * <ul>
     *   <li><strong>El identificador queda en 0.</strong> El id es la identidad
     *       del dron en la base de datos, y la copia todavía no existe allí.
     *       Arrastrarlo haría que una actualización sobre la copia sobrescribiera
     *       la fila del original.</li>
     *   <li><strong>El piloto queda sin asignar.</strong> Un piloto conduce un
     *       solo dron —la columna {@code piloto_id} es UNIQUE—, así que copiar
     *       la referencia le robaría el piloto al original. El modelo lo
     *       garantiza además por diseño: {@code setPiloto} tiene visibilidad de
     *       paquete y esta clase no puede invocarlo.</li>
     *   <li><strong>Los sensores se duplican de verdad.</strong> El dron nuevo
     *       estrena su propia lista y dentro va un {@link Sensor} nuevo por cada
     *       uno: el sensor es una pieza física montada en un dron concreto, no
     *       algo que dos drones puedan compartir.</li>
     *   <li><strong>El serial sí se copia</strong>, aunque la base lo exija
     *       único: la copia está pensada para editarse antes de guardarla, y
     *       conservarlo permite reconocer de qué dron proviene. Es la vista
     *       quien lo deja en blanco al cargar el formulario.</li>
     * </ul>
     *
     * @param original dron a duplicar; no puede ser nulo.
     * @return copia independiente del dron, sin id y sin piloto.
     * @throws IllegalArgumentException si el dron es nulo.
     */
    public Dron clonar(Dron original) {
        if (original == null) {
            throw new IllegalArgumentException("El dron a copiar no puede ser nulo.");
        }

        Dron copia = copiarAtributos(original);

        // Lista nueva con sensores nuevos: ni la colección ni sus elementos se
        // comparten con el original. El id del sensor tampoco se copia, por la
        // misma razón que el del dron.
        for (Sensor sensor : original.getSensores()) {
            copia.agregarSensor(new Sensor(0, sensor.getTipo(), sensor.getFabricante()));
        }

        return copia;
    }

    /**
     * Construye el duplicado con los atributos del original, incluidos los
     * propios de su subtipo.
     *
     * <p><strong>Este es el único punto del proyecto donde la copia distingue
     * el subtipo concreto.</strong> Al no existir un método {@code copiar()} en
     * el modelo —que es justo lo que se ha evitado para no darle al dominio
     * responsabilidades de infraestructura—, el servicio no tiene forma
     * polimórfica de preguntar por los atributos que solo existen en una
     * subclase: {@code capacidadTanque} no tiene equivalente en
     * {@link Vigilancia}, ni {@code deteccionTermica} en {@link Agricultura}.
     * El precio de sacar el patrón del modelo es este {@code switch}, y queda
     * confinado a este método privado.</p>
     *
     * <p>El {@code switch} se hace sobre {@link Dron#getTipo()} y no con
     * {@code instanceof} encadenado porque al ser exhaustivo sobre el enumerado
     * obliga al compilador a exigir el caso nuevo si algún día se añade un
     * tercer subtipo: el proyecto no compilará hasta actualizar este método.
     * La construcción se delega en las fábricas por subtipo, de modo que aquí
     * tampoco se invoca ningún constructor del modelo directamente.</p>
     *
     * @param original dron del que se toman los datos.
     * @return dron nuevo con los mismos atributos, sin sensores todavía.
     */
    private Dron copiarAtributos(Dron original) {
        return switch (original.getTipo()) {

            case AGRICULTURA -> AgriculturaFactory.crearDron(
                    0,
                    original.getSerial(),
                    original.getModelo(),
                    original.getFabricante(),
                    original.getPeso(),
                    ((Agricultura) original).getCapacidadTanque());

            case VIGILANCIA -> VigilanciaFactory.crearDron(
                    0,
                    original.getSerial(),
                    original.getModelo(),
                    original.getFabricante(),
                    original.getPeso(),
                    ((Vigilancia) original).isDeteccionTermica());
        };
    }

    /**
     * Elimina una plantilla del registro.
     *
     * @param clave nombre de la plantilla a eliminar.
     * @return {@code true} si existía y se eliminó; {@code false} si no había
     *         ninguna con esa clave.
     */
    public boolean eliminar(String clave) {
        return prototipos.remove(clave) != null;
    }

    /**
     * Indica si hay una plantilla registrada con esa clave.
     *
     * @param clave nombre a comprobar.
     * @return {@code true} si la plantilla existe.
     */
    public boolean contiene(String clave) {
        return prototipos.containsKey(clave);
    }

    /**
     * Nombres de las plantillas disponibles, en el orden en que se registraron.
     *
     * <p>Es lo que la vista necesita para ofrecerlas al usuario.</p>
     *
     * @return conjunto no modificable con las claves registradas.
     */
    public Set<String> nombresRegistrados() {
        return Collections.unmodifiableSet(prototipos.keySet());
    }
}
