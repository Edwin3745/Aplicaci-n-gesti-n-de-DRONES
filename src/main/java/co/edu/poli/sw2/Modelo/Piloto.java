package co.edu.poli.sw2.Modelo;

/**
 * Representa al operador responsable de dirigir un dron dentro del sistema.
 *
 * Esta clase pertenece a la capa Modelo del patrón MVC y encapsula la información
 * personal y profesional del piloto. Además, mantiene la relación con el dron que
 * tiene asignado en una asociación bidireccional, permitiendo que el estado del
 * piloto y del dron permanezca sincronizado.
 */
public class Piloto {

    /**
     * Identificador único del piloto.
     */
    private int id;

    /**
     * Nombre completo del piloto.
     */
    private String nombre;

    /**
     * Nivel de experiencia del piloto.
     */
    private int experiencia;

    /**
     * Número de contacto del piloto.
     */
    private String telefono;

    /**
     * Dron asignado al piloto en una relación bidireccional.
     */
    private Dron dron;

    /**
     * Crea una instancia vacía del piloto.
     */
    public Piloto() {
    }

    /**
     * Crea un piloto con los datos iniciales principales.
     *
     * @param id identificador único del piloto.
     * @param nombre nombre completo del piloto.
     * @param experiencia años o nivel de experiencia del piloto.
     * @param telefono número de contacto del piloto.
     */
    public Piloto(int id, String nombre, int experiencia, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.experiencia = experiencia;
        this.telefono = telefono;
    }

    /**
     * Obtiene el identificador del piloto.
     *
     * @return identificador del piloto.
     */
    public int getId() { return id; }

    /**
     * Asigna un nuevo identificador al piloto.
     *
     * @param id nuevo valor del identificador.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Obtiene el nombre del piloto.
     *
     * @return nombre del piloto.
     */
    public String getNombre() { return nombre; }

    /**
     * Actualiza el nombre del piloto.
     *
     * @param nombre nuevo nombre del piloto.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene la experiencia del piloto.
     *
     * @return nivel o cantidad de experiencia del piloto.
     */
    public int getExperiencia() { return experiencia; }

    /**
     * Actualiza la experiencia del piloto.
     *
     * @param experiencia nueva experiencia del piloto.
     */
    public void setExperiencia(int experiencia) { this.experiencia = experiencia; }

    /**
     * Obtiene el teléfono de contacto del piloto.
     *
     * @return teléfono del piloto.
     */
    public String getTelefono() { return telefono; }

    /**
     * Actualiza el teléfono de contacto del piloto.
     *
     * @param telefono nuevo número de contacto.
     */
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /**
     * Obtiene el dron asignado al piloto.
     *
     * @return instancia del dron asignado o {@code null} si no tiene ninguno.
     */
    public Dron getDron() {
        return dron;
    }

    /**
     * Verifica si el piloto tiene un dron asignado.
     *
     * @return {@code true} si existe un dron asociado; {@code false} en caso contrario.
     */
    public boolean tieneDronAsignado() {
        return dron != null;
    }

    /**
     * Asigna un dron al piloto y mantiene sincronizada la relación en ambos sentidos.
     *
     * @param nuevoDron dron que se va a asignar al piloto.
     * @throws IllegalArgumentException si se intenta asignar un dron nulo.
     */
    public void asignarDron(Dron nuevoDron) {
        if (nuevoDron == null) {
            throw new IllegalArgumentException("El dron no puede ser nulo.");
        }
        if (this.dron != null && this.dron != nuevoDron) {
            liberarDron();
        }
        this.dron = nuevoDron;
        nuevoDron.setPiloto(this);
    }

    /**
     * Libera la relación entre el piloto y el dron asignado.
     */
    public void liberarDron() {
        if (this.dron != null) {
            Dron dronAnterior = this.dron;
            this.dron = null;
            dronAnterior.setPiloto(null);
        }
    }

    /**
     * Devuelve una representación textual del piloto y su dron asociado.
     *
     * @return cadena con el estado actual del piloto.
     */
    @Override
    public String toString() {
        return "Piloto{" + "id=" + id + ", nombre='" + nombre + '\'' +
                ", experiencia=" + experiencia + ", telefono='" + telefono + '\'' +
                ", dron=" + (dron != null ? dron.getId() : "sin asignar") + '}';
    }
}