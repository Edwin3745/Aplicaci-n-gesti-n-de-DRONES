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

    /**
     * Obtiene el identificador del dron.
     *
     * @return identificador del dron; 0 si todavía no se ha guardado.
     */
    public int getId() {
        return id;
    }

    /**
     * Asigna el identificador del dron.
     *
     * <p>Lo usa el DAO tras un alta, para reflejar en el objeto el id que
     * generó la base de datos.</p>
     *
     * @param id nuevo identificador del dron.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el número de serie del dron.
     *
     * @return número de serie.
     */
    public String getSerial() {
        return serial;
    }

    /**
     * Actualiza el número de serie del dron.
     *
     * @param serial nuevo número de serie; debe ser único en el sistema.
     */
    public void setSerial(String serial) {
        this.serial = serial;
    }

    /**
     * Obtiene el modelo del dron.
     *
     * @return modelo del dron.
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Actualiza el modelo del dron.
     *
     * @param modelo nuevo modelo del dron.
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * Obtiene el fabricante del dron.
     *
     * @return fabricante del dron.
     */
    public String getFabricante() {
        return fabricante;
    }

    /**
     * Actualiza el fabricante del dron.
     *
     * @param fabricante nuevo fabricante del dron.
     */
    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    /**
     * Obtiene el peso del dron.
     *
     * @return peso en kilogramos.
     */
    public double getPeso() {
        return peso;
    }

    /**
     * Actualiza el peso del dron.
     *
     * @param peso peso en kilogramos.
     * @throws IllegalArgumentException si el peso es negativo.
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

    /**
     * Obtiene el piloto asignado a este dron.
     *
     * @return piloto asignado, o {@code null} si no tiene ninguno.
     */
    public Piloto getPiloto() {
        return piloto;
    }

    /**
     * Asigna el piloto de este dron.
     *
     * <p>Visibilidad de paquete a propósito: la relación solo puede modificarse
     * mediante {@code Piloto.asignarDron()} y {@code Piloto.liberarDron()}, que
     * mantienen ambos extremos sincronizados.</p>
     *
     * @param piloto piloto a asignar, o {@code null} para dejarlo sin asignar.
     */
    void setPiloto(Piloto piloto) {
        this.piloto = piloto;
    }

    /**
     * Indica si el dron tiene un piloto asignado.
     *
     * @return {@code true} si hay un piloto asignado.
     */
    public boolean tienePilotoAsignado() {
        return piloto != null;
    }

    // ------------------------------------------------------------------
    // Composición Dron -> Sensor
    // ------------------------------------------------------------------

    /**
     * Obtiene los sensores montados en el dron.
     *
     * <p>La lista se devuelve como vista no modificable para que la única
     * forma de alterar la composición sea {@link #agregarSensor(Sensor)} y
     * {@link #removerSensor(Sensor)}.</p>
     *
     * @return vista no modificable de los sensores montados.
     */
    public List<Sensor> getSensores() {
        return Collections.unmodifiableList(sensores);
    }

    /**
     * Monta un sensor en el dron.
     *
     * @param sensor sensor a montar; no puede ser nulo.
     * @throws IllegalArgumentException si el sensor es nulo.
     */
    public void agregarSensor(Sensor sensor) {
        if (sensor == null) {
            throw new IllegalArgumentException("El sensor no puede ser nulo.");
        }
        sensores.add(sensor);
    }

    /**
     * Retira un sensor del dron.
     *
     * @param sensor sensor a retirar.
     * @return {@code true} si el sensor estaba montado y se retiró.
     */
    public boolean removerSensor(Sensor sensor) {
        return sensores.remove(sensor);
    }

    // ------------------------------------------------------------------
    // Identidad y representación
    // ------------------------------------------------------------------

    /**
     * Compara dos drones por su identificador.
     *
     * <p>Dos drones son el mismo si comparten identificador, sin importar si
     * son instancias distintas en memoria.</p>
     *
     * @param obj objeto con el que compararse.
     * @return {@code true} si el otro objeto es un dron con el mismo id.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Dron)) return false;
        return this.id == ((Dron) obj).id;
    }

    /**
     * Calcula el código hash a partir del identificador, en coherencia con
     * {@link #equals(Object)}.
     *
     * @return código hash del dron.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Compone la parte común de la representación textual del dron.
     *
     * <p>Bloque reutilizable por las subclases en su propio {@code toString()}.</p>
     *
     * @return los atributos comunes del dron en forma de texto.
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

    /**
     * Representación textual del dron, encabezada por su clase concreta.
     *
     * @return descripción del dron para diagnóstico.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + datosBase() + "}";
    }
}
