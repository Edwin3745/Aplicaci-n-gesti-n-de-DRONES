package co.edu.poli.sw2.servicios;

import co.edu.poli.sw2.modelo.Dron;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registro de configuraciones base de dron, según el patrón Prototype.
 *
 * <p>Guarda drones ya configurados bajo una clave legible —"fumigador
 * estándar", "vigilancia nocturna"— y entrega copias de ellos cuando se
 * piden. El usuario que necesita dar de alta un dron parecido a uno habitual
 * parte de la plantilla en lugar de rellenar el formulario desde cero.</p>
 *
 * <p>El registro nunca entrega el prototipo guardado, sino una copia obtenida
 * con {@link Dron#copiar()}. De lo contrario, quien lo recibiera podría
 * modificarlo y contaminar la plantilla para todos los usos siguientes.</p>
 *
 * <p>Las copias llegan sin identificador y sin piloto, tal como documenta el
 * constructor copia de {@link Dron}: son drones nuevos que todavía no existen
 * en la base de datos.</p>
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
        if (dron == null) {
            throw new IllegalArgumentException("El prototipo no puede ser nulo.");
        }
        prototipos.put(clave, dron.copiar());
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
        return prototipo.copiar();
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
