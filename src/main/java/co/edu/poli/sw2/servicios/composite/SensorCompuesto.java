package co.edu.poli.sw2.servicios.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agrupa varios elementos de la jerarquía de sensores bajo un mismo nombre.
 *
 * <p>Es el compuesto del patrón Composite: implementa {@link SensorComponente}
 * y a la vez contiene una lista de {@link SensorComponente}. Esa doble
 * condición es lo que permite anidar grupos dentro de grupos sin límite, ya que
 * un hijo puede ser tanto una hoja como otro grupo.</p>
 *
 * <p>Cuando se le pide una operación, la resuelve preguntando a cada hijo y
 * combinando las respuestas. La recursión vive aquí dentro, de modo que quien
 * usa la jerarquía solo hace una llamada, sin bucles ni comprobaciones de
 * tipo.</p>
 */
public class SensorCompuesto implements SensorComponente {

    /** Nombre del grupo dentro de la jerarquía. */
    private final String nombre;

    /** Elementos que cuelgan de este grupo: sensores individuales u otros grupos. */
    private final List<SensorComponente> hijos = new ArrayList<>();

    /**
     * @param nombre nombre del grupo; no puede estar vacío.
     * @throws IllegalArgumentException si el nombre es nulo o está en blanco.
     */
    public SensorCompuesto(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del grupo no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    /**
     * Añade un elemento a este grupo.
     *
     * <p>Acepta cualquier {@link SensorComponente}, así que el elemento añadido
     * puede ser tanto un sensor individual como otro grupo completo. Es el
     * punto exacto en el que el patrón permite construir árboles de cualquier
     * profundidad.</p>
     *
     * @param sensor elemento a añadir; no puede ser nulo ni este mismo grupo.
     * @return mensaje describiendo la operación realizada.
     * @throws IllegalArgumentException si el elemento es nulo o es este mismo
     *                                  grupo, lo que crearía un ciclo.
     */
    public String agregar(SensorComponente sensor) {
        if (sensor == null) {
            throw new IllegalArgumentException("No se puede agregar un componente nulo.");
        }
        if (sensor == this) {
            throw new IllegalArgumentException(
                    "Un grupo no puede contenerse a sí mismo: se formaría un ciclo.");
        }
        hijos.add(sensor);
        return "Agregado '" + sensor.getNombre() + "' a '" + nombre + "'.";
    }

    /**
     * Retira un elemento de este grupo.
     *
     * @param sensor elemento a retirar.
     * @return mensaje describiendo el resultado de la operación.
     */
    public String remover(SensorComponente sensor) {
        boolean retirado = hijos.remove(sensor);
        return retirado
                ? "Retirado '" + sensor.getNombre() + "' de '" + nombre + "'."
                : "'" + nombre + "' no contiene el elemento indicado.";
    }

    /**
     * Elementos que cuelgan directamente de este grupo.
     *
     * <p>Se devuelve como vista no modificable para que la única forma de
     * alterar la jerarquía sea {@link #agregar(SensorComponente)} y
     * {@link #remover(SensorComponente)}.</p>
     *
     * @return vista no modificable de los hijos directos.
     */
    public List<SensorComponente> getHijos() {
        return Collections.unmodifiableList(hijos);
    }

    @Override
    public String mostrarInfo() {
        return mostrarInfo(0);
    }

    /**
     * Describe este grupo y, recursivamente, todo lo que cuelga de él.
     *
     * <p>Cada hijo se consulta con un nivel más de profundidad, y es él quien
     * decide cómo responder: si es una hoja devuelve una línea, y si es otro
     * grupo repite este mismo proceso con sus propios hijos.</p>
     *
     * @param nivel profundidad de este grupo en el árbol.
     * @return el grupo y su rama completa, una línea por elemento.
     */
        String mostrarInfo(int nivel) {
        String sangria = "   ".repeat(Math.max(0, nivel));

        StringBuilder texto = new StringBuilder();
        texto.append(sangria)
             .append("+ ").append(nombre)
             .append(" [").append(hijos.size()).append(" elementos]");

        for (SensorComponente hijo : hijos) {
            texto.append(System.lineSeparator());
            if (hijo instanceof SensorCompuesto grupo) {
                texto.append(grupo.mostrarInfo(nivel + 1));
            } else if (hijo instanceof SensorHoja hoja) {
                texto.append(hoja.mostrarInfo(nivel + 1));
            } else {
                texto.append("   ".repeat(Math.max(0, nivel + 1)))
                     .append(hijo.mostrarInfo());
            }
        }
        return texto.toString();
    }

    /**
     * Suma los sensores individuales de toda la rama.
     *
     * <p>No recorre el árbol a mano: pregunta a cada hijo cuántos tiene y suma.
     * Si el hijo es un grupo, repetirá esta misma operación con los suyos, de
     * modo que el recuento baja hasta las hojas por sí solo.</p>
     *
     * @return número total de sensores individuales bajo este grupo.
     */
    @Override
    public int contarSensores() {
        int total = 0;
        for (SensorComponente hijo : hijos) {
            total += hijo.contarSensores();
        }
        return total;
    }
}