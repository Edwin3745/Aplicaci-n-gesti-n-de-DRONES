package co.edu.poli.sw2.Modelo;

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
     * Obtiene el identificador del sensor.
     *
     * @return identificador del sensor.
     */
    public int getId() { return id; }

    /**
     * Asigna el identificador del sensor.
     *
     * @param id nuevo identificador.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Obtiene la clase de sensor.
     *
     * @return clase de sensor: térmico, RGB, LiDAR, multiespectral, etc.
     */
    public String getTipo() { return tipo; }

    /**
     * Actualiza la clase de sensor.
     *
     * @param tipo nueva clase de sensor.
     */
    public void setTipo(String tipo) { this.tipo = tipo; }

    /**
     * Obtiene el fabricante del sensor.
     *
     * @return fabricante del sensor.
     */
    public String getFabricante() { return fabricante; }

    /**
     * Actualiza el fabricante del sensor.
     *
     * @param fabricante nuevo fabricante.
     */
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }

    /**
     * Representación textual del sensor.
     *
     * @return descripción del sensor para diagnóstico.
     */
    @Override
    public String toString() {
        return "Sensor{" + "id=" + id + ", tipo='" + tipo + '\'' +
                ", fabricante='" + fabricante + '\'' + '}';
    }
}