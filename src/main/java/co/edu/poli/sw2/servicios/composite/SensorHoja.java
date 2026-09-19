package co.edu.poli.sw2.servicios.composite;

import co.edu.poli.sw2.modelo.Sensor;

/**
 * Elemento terminal de la jerarquía: un sensor concreto, sin hijos.
 *
 * <p>Es la hoja del patrón Composite. Puede representar únicamente una
 * categoría por su nombre, o envolver un {@link Sensor} del modelo, en cuyo
 * caso aporta también su fabricante. Envolverlo permite que el patrón funcione
 * sin que el dominio tenga que conocerlo, igual que hace {@code DronBase} en el
 * Decorator.</p>
 */
public class SensorHoja implements SensorComponente {

    /** Nombre del sensor dentro de la jerarquía. */
    private final String nombre;

    /** Sensor del modelo al que corresponde esta hoja, o {@code null} si no lo hay. */
    private final Sensor sensor;

    /**
     * Crea una hoja identificada solo por su nombre.
     *
     * @param nombre nombre del sensor; no puede estar vacío.
     * @throws IllegalArgumentException si el nombre es nulo o está en blanco.
     */
    public SensorHoja(String nombre) {
        this(nombre, null);
    }

    /**
     * Crea una hoja que envuelve un sensor del modelo.
     *
     * @param nombre nombre del sensor en la jerarquía; no puede estar vacío.
     * @param sensor sensor del modelo al que corresponde; puede ser nulo.
     * @throws IllegalArgumentException si el nombre es nulo o está en blanco.
     */
    public SensorHoja(String nombre, Sensor sensor) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del sensor no puede estar vacío.");
        }
        this.nombre = nombre.trim();
        this.sensor = sensor;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    /**
     * Describe este sensor en una sola línea.
     *
     * <p>Al no tener hijos, la recursión termina aquí: es el caso base del
     * recorrido del árbol.</p>
     *
     * @return una línea con el nombre del sensor y su fabricante, si lo tiene.
     */
    @Override
    public String mostrarInfo() {
        return mostrarInfo(0);
    }

    /**
     * Describe este sensor con la sangría del nivel indicado.
     *
     * @param nivel profundidad de este elemento en el árbol.
     * @return una línea con la sangría correspondiente.
     */
    String mostrarInfo(int nivel) {
        String sangria = "   ".repeat(Math.max(0, nivel));
        String detalle = (sensor != null && sensor.getFabricante() != null)
                ? " (" + sensor.getFabricante() + ")"
                : "";
        return sangria + "- " + nombre + detalle;
    }

    /**
     * Una hoja representa siempre un único sensor.
     *
     * @return {@code 1}.
     */
    @Override
    public int contarSensores() {
        return 1;
    }

    /**
     * @return el sensor del modelo envuelto, o {@code null} si esta hoja solo
     *         representa una categoría.
     */
    public Sensor getSensor() {
        return sensor;
    }
}