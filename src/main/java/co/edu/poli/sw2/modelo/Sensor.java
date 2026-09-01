package co.edu.poli.sw2.modelo;

/**
 * Representa un sensor montado en un dron.
 *
 * <p>Forma parte de una composición: el sensor pertenece a un dron concreto y
 * no tiene sentido por separado, razón por la cual la tabla {@code sensor}
 * declara {@code ON DELETE CASCADE} sobre el dron que lo lleva.</p>
 */
public class Sensor {

    /** Identificador único del sensor. */
    private int id;

    /** Clase de sensor: térmico, RGB, LiDAR, multiespectral, etc. */
    private String tipo;

    /** Fabricante del sensor. */
    private String fabricante;

    /**
     * Crea un sensor vacío.
     */
    public Sensor() {
    }

    /**
     * Crea un sensor con todos sus datos.
     *
     * @param id         identificador del sensor.
     * @param tipo       clase de sensor.
     * @param fabricante fabricante del sensor.
     */
    public Sensor(int id, String tipo, String fabricante) {
        this.id = id;
        this.tipo = tipo;
        this.fabricante = fabricante;
    }

    /**
     * Constructor copia, usado al clonar un dron.
     *
     * <p>El identificador no se copia: la pieza duplicada es otro sensor y
     * recibirá su propio id al guardarse. Como los demás atributos son cadenas
     * inmutables, basta con reasignarlos para que la copia sea independiente.</p>
     *
     * @param original sensor del que se toman los datos.
     */
    public Sensor(Sensor original) {
        this.tipo = original.tipo;
        this.fabricante = original.fabricante;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }

    @Override
    public String toString() {
        return "Sensor{" + "id=" + id + ", tipo='" + tipo + '\'' +
                ", fabricante='" + fabricante + '\'' + '}';
    }
}