package co.edu.poli.sw2.Modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Representa una misión planificada para la flota de drones.
 *
 * <p>La misión es el "todo" y los drones asignados son sus "partes": la lista
 * se inicializa siempre, nunca es nula, y solo puede alterarse mediante los
 * métodos de la clase.</p>
 */
public class Mision {

    /** Identificador único de la misión. */
    private int id;

    /** Nombre con el que se conoce la misión. */
    private String nombre;

    /** Lugar donde se desarrolla la misión. */
    private String ubicacion;

    /** Fecha prevista para la misión. */
    private Date fecha;

    /** Drones asignados a la misión. */
    private final List<Dron> drones = new ArrayList<>();

    /**
     * Crea una misión vacía.
     */
    public Mision() {
    }

    /**
     * Crea una misión con sus datos principales.
     *
     * @param id        identificador de la misión.
     * @param nombre    nombre de la misión.
     * @param ubicacion lugar donde se desarrolla.
     * @param fecha     fecha prevista.
     */
    public Mision(int id, String nombre, String ubicacion, Date fecha) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
    }

    /**
     * Obtiene el identificador de la misión.
     *
     * @return identificador de la misión.
     */
    public int getId() { return id; }

    /**
     * Asigna el identificador de la misión.
     *
     * @param id nuevo identificador.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Obtiene el nombre de la misión.
     *
     * @return nombre de la misión.
     */
    public String getNombre() { return nombre; }

    /**
     * Actualiza el nombre de la misión.
     *
     * @param nombre nuevo nombre.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene el lugar donde se desarrolla la misión.
     *
     * @return ubicación de la misión.
     */
    public String getUbicacion() { return ubicacion; }

    /**
     * Actualiza el lugar donde se desarrolla la misión.
     *
     * @param ubicacion nueva ubicación.
     */
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    /**
     * Obtiene la fecha prevista para la misión.
     *
     * @return fecha de la misión.
     */
    public Date getFecha() { return fecha; }

    /**
     * Actualiza la fecha prevista para la misión.
     *
     * @param fecha nueva fecha.
     */
    public void setFecha(Date fecha) { this.fecha = fecha; }

    // --- Gestión de la composición Mision -> Dron ---

    /**
     * Obtiene los drones asignados a la misión.
     *
     * <p>Se devuelve una vista no modificable para obligar a que la única forma
     * de añadir o quitar drones sea {@link #agregarDron(Dron)} y
     * {@link #removerDron(Dron)}, que es lo que encapsula la composición.</p>
     *
     * @return vista no modificable de los drones asignados.
     */
    public List<Dron> getDrones() {
        return Collections.unmodifiableList(drones);
    }

    /**
     * Asigna un dron a la misión.
     *
     * @param dron dron a asignar; no puede ser nulo.
     * @throws IllegalArgumentException si el dron es nulo.
     */
    public void agregarDron(Dron dron) {
        if (dron == null) {
            throw new IllegalArgumentException("El dron no puede ser nulo.");
        }
        drones.add(dron);
    }

    /**
     * Retira un dron de la misión.
     *
     * @param dron dron a retirar.
     * @return {@code true} si el dron estaba asignado y se retiró.
     */
    public boolean removerDron(Dron dron) {
        return drones.remove(dron);
    }

    /**
     * Retira de la misión el dron que tenga el identificador indicado.
     *
     * @param idDron identificador del dron a retirar.
     * @return {@code true} si se retiró algún dron.
     */
    public boolean removerDronPorId(int idDron) {
        return drones.removeIf(d -> d.getId() == idDron);
    }

    /**
     * Representación textual de la misión.
     *
     * @return descripción de la misión para diagnóstico.
     */
    @Override
    public String toString() {
        return "Mision{" + "id=" + id + ", nombre='" + nombre + '\'' +
                ", ubicacion='" + ubicacion + '\'' + ", fecha=" + fecha +
                ", drones=" + drones.size() + '}';
    }
}