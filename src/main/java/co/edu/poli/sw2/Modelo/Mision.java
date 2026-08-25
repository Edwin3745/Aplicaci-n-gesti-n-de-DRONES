package co.edu.poli.sw2.Modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Mision {

    private int id;
    private String nombre;
    private String ubicacion;
    private Date fecha;
    /** Descripcion de la mision. */
    private String descripcion;
    // Composición: la Misión es el "todo", los Drones son las "partes".
    // Se inicializa siempre (nunca null) para evitar NullPointerException.
    private final List<Dron> drones = new ArrayList<>();

    public Mision() {
    }

    public Mision(int id, String nombre, String ubicacion, Date fecha) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    // --- Gestión de la composición Mision -> Dron ---

    /**
     * Devuelve una vista no modificable de los drones.
     * Así se obliga a que la única forma de añadir/quitar sea
     * a través de agregarDron/removerDron (encapsula la composición).
     */
    public List<Dron> getDrones() {
        return Collections.unmodifiableList(drones);
    }

    public void agregarDron(Dron dron) {
        if (dron == null) {
            throw new IllegalArgumentException("El dron no puede ser nulo.");
        }
        drones.add(dron);
    }

    public boolean removerDron(Dron dron) {
        return drones.remove(dron);
    }

    public boolean removerDronPorId(int idDron) {
        return drones.removeIf(d -> d.getId() == idDron);
    }

    @Override
    public String toString() {
        return "Mision{" + "id=" + id + ", nombre='" + nombre + '\'' +
                ", ubicacion='" + ubicacion + '\'' + ", fecha=" + fecha +
                ", drones=" + drones.size() + '}';
    }
}