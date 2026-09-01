package co.edu.poli.sw2.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa la entidad base del sistema de gestión de drones.
 *
 * Dron es una clase abstracta que contiene los atributos comunes
 * de los diferentes tipos de drones y declara el comportamiento que
 * cada subtipo concreto debe definir.
 *
 * Las clases Agricultura y Vigilancia heredan de esta clase.
 *
 * <p>Implementa {@link Prototipo}: cualquier dron sabe producir una copia
 * independiente de sí mismo, que es lo que permite registrar configuraciones
 * base y partir de ellas en vez de rellenar los datos desde cero.</p>
 */
public abstract class Dron implements Prototipo {

    /** Identificador único del dron. Clave técnica, corresponde a la columna id de la BD. */
    private int id;

    /** Número de serie del dron. Identificador del mundo real, ej: "AGR-001". */
    private String serial;

    /** Modelo del dron. */
    private String modelo;

    /** Fabricante del dron. */
    private String fabricante;

    /** Peso del dron en kilogramos. */
    private double peso;

    /** Piloto asignado al dron. */
    private Piloto piloto;

    /** Sensores montados en el dron. */
    private final List<Sensor> sensores = new ArrayList<>();

    /**
     * Constructor vacío para reconstrucción desde la capa de persistencia.
     *
     * Es protected porque solo las subclases deben poder invocarlo:
     * en el dominio no existe un dron sin tipo.
     */
    protected Dron() {
    }

    /**
     * Constructor con los atributos comunes.
     *
     * @param id identificador del dron
     * @param serial número de serie
     * @param modelo modelo del dron
     * @param fabricante fabricante del dron
     * @param peso peso del dron en kilogramos
     */
    protected Dron(int id, String serial, String modelo,
                   String fabricante, double peso) {
        this.id = id;
        this.serial = serial;
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.peso = peso;
    }

    /**
     * Constructor copia, base del patrón Prototype.
     *
     * <p>Copia los atributos comunes y decide qué <em>no</em> se hereda de la
     * plantilla:</p>
     * <ul>
     *   <li><strong>El identificador queda en 0.</strong> El id es la identidad
     *       del dron en la base de datos, y la copia todavía no existe allí.
     *       Arrastrarlo haría que una actualización sobre la copia sobrescribiera
     *       la fila del original.</li>
     *   <li><strong>El piloto queda sin asignar.</strong> Un piloto conduce un
     *       solo dron —la columna {@code piloto_id} es UNIQUE—, así que copiar
     *       la referencia le robaría el piloto al original.</li>
     *   <li><strong>Los sensores se duplican de verdad.</strong> Se construye
     *       una lista nueva y, dentro, un {@link Sensor} nuevo por cada uno: el
     *       sensor es una pieza física montada en un dron concreto, no algo que
     *       dos drones puedan compartir. Copiar solo la lista dejaría objetos
     *       compartidos; copiar solo la referencia a la lista haría que agregar
     *       un sensor a la copia se lo agregara también al original.</li>
     * </ul>
     *
     * <p>El serial <em>sí</em> se copia, aunque la base lo exija único: la copia
     * está pensada para editarse antes de guardarla, y dejar el serial del
     * original a la vista permite reconocer de qué plantilla proviene.</p>
     *
     * @param original dron del que se toman los datos.
     */
    protected Dron(Dron original) {
        this.serial = original.serial;
        this.modelo = original.modelo;
        this.fabricante = original.fabricante;
        this.peso = original.peso;

        for (Sensor sensor : original.sensores) {
            this.sensores.add(new Sensor(sensor));
        }
    }

    // ------------------------------------------------------------------
    // Comportamiento que cada subtipo debe definir
    // ------------------------------------------------------------------

    /**
     * Identifica el subtipo concreto del dron.
     *
     * La capa DAO usa este valor para escribir la columna discriminadora,
     * por lo que cada subclase está obligada a declararlo.
     *
     * @return tipo concreto de este dron
     */
    public abstract TipoDron getTipo();

    /**
     * Describe la función operativa del dron en lenguaje de negocio.
     *
     * Permite recorrer una colección heterogénea de drones y obtener la
     * descripción correcta de cada uno sin inspeccionar su clase.
     *
     * @return descripción legible de la misión que cumple el equipo
     */
    public abstract String descripcionOperativa();

    // ------------------------------------------------------------------
    // Atributos comunes
    // ------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public double getPeso() {
        return peso;
    }

    /**
     * @param peso peso en kilogramos
     * @throws IllegalArgumentException si el peso es negativo
     */
    public void setPeso(double peso) {
        if (peso < 0) {
            throw new IllegalArgumentException("El peso no puede ser negativo.");
        }
        this.peso = peso;
    }

    // ------------------------------------------------------------------
    // Asociación bidireccional con Piloto
    // ------------------------------------------------------------------

    public Piloto getPiloto() {
        return piloto;
    }

    /**
     * Visibilidad de paquete a propósito: la relación solo puede modificarse
     * mediante Piloto.asignarDron() y Piloto.liberarDron(), que mantienen
     * ambos extremos sincronizados.
     */
    void setPiloto(Piloto piloto) {
        this.piloto = piloto;
    }

    public boolean tienePilotoAsignado() {
        return piloto != null;
    }

    // ------------------------------------------------------------------
    // Composición Dron -> Sensor
    // ------------------------------------------------------------------

    /**
     * @return vista no modificable de los sensores montados
     */
    public List<Sensor> getSensores() {
        return Collections.unmodifiableList(sensores);
    }

    public void agregarSensor(Sensor sensor) {
        if (sensor == null) {
            throw new IllegalArgumentException("El sensor no puede ser nulo.");
        }
        sensores.add(sensor);
    }

    public boolean removerSensor(Sensor sensor) {
        return sensores.remove(sensor);
    }

    // ------------------------------------------------------------------
    // Identidad y representación
    // ------------------------------------------------------------------

    /**
     * Dos drones son el mismo si comparten identificador, sin importar si son
     * instancias distintas en memoria.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Dron)) return false;
        return this.id == ((Dron) obj).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Bloque común reutilizable por las subclases en su propio toString().
     */
    protected String datosBase() {
        return "id=" + id +
                ", tipo=" + getTipo().getCodigo() +
                ", serial='" + serial + '\'' +
                ", modelo='" + modelo + '\'' +
                ", fabricante='" + fabricante + '\'' +
                ", peso=" + peso +
                ", piloto=" + (piloto != null ? piloto.getId() : "sin asignar") +
                ", sensores=" + sensores.size();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + datosBase() + "}";
    }
}
