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
     Número de licencia de vuelo. */
    private String licencia;

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
     * @param licencia número de licencia de vuelo
     * @param telefono número de contacto del piloto.
     */
    public Piloto(int id, String nombre, String licencia, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.licencia = licencia;
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
     * Obtiene el número de licencia de vuelo.
     *
     * @return número de licencia del piloto.
     */
    public String getLicencia() { return licencia; }

    /**
     * Actualiza el número de licencia de vuelo.
     *
     * @param licencia nuevo número de licencia.
     */
    public void setLicencia(String licencia) { this.licencia = licencia; }

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
     * Asigna un dron a este piloto manteniendo la coherencia de la relación
     * en ambos extremos y liberando cualquier vínculo previo, tanto del
     * piloto como del dron entrante.
     *
     * @param nuevoDron dron a asignar.
     * @throws IllegalArgumentException si el dron es nulo.
     */
    public void asignarDron(Dron nuevoDron) {
        if (nuevoDron == null) {
            throw new IllegalArgumentException("El dron no puede ser nulo.");
        }
        if (this.dron == nuevoDron) {
            return;
        }

        liberarDron();

        Piloto pilotoAnterior = nuevoDron.getPiloto();
        if (pilotoAnterior != null && pilotoAnterior != this) {
            pilotoAnterior.liberarDron();
        }

        this.dron = nuevoDron;
        nuevoDron.setPiloto(this);
    }

    /**
     * Libera el dron asignado, dejando ambos extremos de la relación limpios.
     */
    public void liberarDron() {
        if (this.dron != null) {
            Dron anterior = this.dron;
            this.dron = null;
            anterior.setPiloto(null);
        }
    }

    @Override
    public String toString() {
        return "Piloto{id=" + id +
                ", nombre='" + nombre + '\'' +
                ", licencia='" + licencia + '\'' +
                ", telefono='" + telefono + '\'' +
                ", dron=" + (dron != null ? dron.getSerial() : "sin asignar") + '}';
    }
}