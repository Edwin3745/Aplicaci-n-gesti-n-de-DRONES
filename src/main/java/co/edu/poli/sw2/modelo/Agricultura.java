package co.edu.poli.sw2.modelo;

/**
 * Representa un dron destinado a actividades agrícolas.
 */
public class Agricultura extends Dron {

    /**
     * Capacidad del tanque del dron.
     */
    private double capacidadTanque;

    /**
     * Constructor vacío.
     */
    public Agricultura() {
        super();
    }

    /**
     * Constructor completo.
     *
     * @param id              identificador del dron.
     * @param serial          número de serie.
     * @param modelo          modelo del dron.
     * @param fabricante      fabricante del dron.
     * @param peso            peso en kilogramos.
     * @param capacidadTanque capacidad del tanque en litros.
     */
    public Agricultura(int id, String serial, String modelo,
                       String fabricante, double peso,
                       double capacidadTanque) {
        super(id, serial, modelo, fabricante, peso);
        this.capacidadTanque = capacidadTanque;
    }

    /**
     * Constructor copia.
     *
     * <p>Delega en el de {@link Dron} los atributos comunes —incluida la
     * duplicación de los sensores y el descarte del id y del piloto— y añade
     * lo propio del subtipo.</p>
     *
     * @param original dron de agricultura del que se toman los datos.
     */
    protected Agricultura(Agricultura original) {
        super(original);
        this.capacidadTanque = original.capacidadTanque;
    }

    /**
     * Crea una copia independiente de este dron de agricultura.
     *
     * <p>El tipo de retorno es {@code Agricultura} y no {@code Dron}: clonar un
     * dron de agricultura produce siempre otro dron de agricultura, y así queda
     * declarado para quien llame al método sobre una referencia concreta.</p>
     *
     * @return copia de este dron, sin identificador y sin piloto asignado.
     */
    @Override
    public Agricultura copiar() {
        return new Agricultura(this);
    }

    /**
     * Obtiene la capacidad del tanque.
     *
     * @return capacidad del tanque en litros.
     */
    public double getCapacidadTanque() {
        return capacidadTanque;
    }

    /**
     * Actualiza la capacidad del tanque.
     *
     * @param capacidadTanque nueva capacidad en litros.
     */
    public void setCapacidadTanque(double capacidadTanque) {
        this.capacidadTanque = capacidadTanque;
    }

    /**
     * Representación textual del dron de agricultura.
     *
     * @return descripción del dron para diagnóstico.
     */
    @Override
    public String toString() {
        return "Agricultura{" +
                "id='" + getId() + '\'' +
                ", serial='" + getSerial() + '\'' +
                ", modelo='" + getModelo() + '\'' +
                ", fabricante='" + getFabricante() + '\'' +
                ", peso=" + getPeso() +
                ", capacidadTanque=" + capacidadTanque +
                '}';
    }
    @Override
    public TipoDron getTipo() {
        return TipoDron.AGRICULTURA;
    }

    @Override
    public String descripcionOperativa() {
        return "Fumigación y dispersión de insumos con tanque de " + capacidadTanque + " L";
    }
}