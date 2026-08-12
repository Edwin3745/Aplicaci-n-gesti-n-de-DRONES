package co.edu.poli.sw2.Modelo;

/**
 * Representa la entidad principal del sistema de gestión de drones.
 *
 * Esta clase forma parte de la capa Modelo dentro del patrón MVC y encapsula
 * la información básica que identifica y describe a cada dron en la aplicación.
 * Define atributos como el identificador, número de serie, modelo, fabricante y
 * peso, que permiten representar el estado real del equipo y gestionarlo desde la
 * lógica de negocio y la interfaz gráfica.
 *
 * Su responsabilidad es mantener los datos del dron de forma consistente y
 * proporcionar acceso controlado a cada propiedad mediante los métodos de lectura
 * y escritura. La clase no contiene lógica de negocio compleja, sino que actúa
 * como estructura de dominio para ser utilizada por el controlador y la vista.
 */
public class Dron {

    /**
     * Identificador único del dron dentro del sistema.
     */
    private int id;

    /**
     * Número de serie asignado al dron para diferenciarlo de otros equipos.
     */
    private String serial;

    /**
     * Modelo del dron, por ejemplo, el tipo o referencia tecnológica del equipo.
     */
    private String modelo;

    /**
     * Fabricante o empresa responsable de la construcción del dron.
     */
    private String fabricante;

    /**
     * Peso del dron, expresado en la unidad definida por el dominio de la aplicación.
     */
    private float peso;

    /**
     * Piloto asignado al dron en una relación bidireccional.
     */
    private Piloto piloto;

    /**
     * Crea una instancia vacía de dron.
     *
     * Este constructor es útil cuando se desea crear un objeto y completar sus
     * atributos más adelante, por ejemplo durante la carga de datos o la edición
     * desde una vista.
     */
    public Dron() {
    }

    /**
     * Crea un dron con todos sus datos iniciales.
     *
     * @param id identificador único del dron.
     * @param serial número de serie del equipo.
     * @param modelo modelo del dron.
     * @param fabricante fabricante del equipo.
     * @param peso peso del dron.
     */
    public Dron(int id, String serial, String modelo, String fabricante, float peso) {
        this.id = id;
        this.serial = serial;
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.peso = peso;
    }

    /**
     * Obtiene el identificador del dron.
     *
     * @return el valor del identificador.
     */
    public int getId() {
        return id;
    }

    /**
     * Asigna un nuevo identificador al dron.
     *
     * @param id nuevo identificador a guardar.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el número de serie del dron.
     *
     * @return número de serie asociado al equipo.
     */
    public String getSerial() {
        return serial;
    }

    /**
     * Actualiza el número de serie del dron.
     *
     * @param serial nuevo número de serie.
     */
    public void setSerial(String serial) {
        this.serial = serial;
    }

    /**
     * Obtiene el modelo del dron.
     *
     * @return nombre o referencia del modelo.
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Define el modelo del dron.
     *
     * @param modelo nuevo nombre o referencia del modelo.
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * Obtiene el fabricante del dron.
     *
     * @return nombre del fabricante o empresa responsable.
     */
    public String getFabricante() {
        return fabricante;
    }

    /**
     * Asigna el fabricante del dron.
     *
     * @param fabricante nombre del fabricante.
     */
    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    /**
     * Obtiene el peso del dron.
     *
     * @return valor del peso actual del equipo.
     */
    public float getPeso() {
        return peso;
    }

    /**
     * Actualiza el peso del dron.
     *
     * @param peso nuevo valor del peso.
     */
    public void setPeso(float peso) {
        this.peso = peso;
    }

    /**
     * Obtiene el piloto asignado al dron.
     *
     * @return piloto asociado o {@code null} si no tiene ninguno.
     */
    public Piloto getPiloto() {
        return piloto;
    }

    /**
     * Asigna o desasigna el piloto del dron.
     *
     * @param piloto nuevo piloto asociado; puede ser {@code null} para liberar la relación.
     */
    void setPiloto(Piloto piloto) {
        this.piloto = piloto;
    }

    /**
     * Devuelve una representación textual del dron con sus atributos principales.
     *
     * Este método resulta útil para depuración, trazas y visualización rápida de
     * la información del objeto en consola o interfaces de desarrollo.
     *
     * @return cadena con el estado actual del dron.
     */
    @Override
    public String toString() {
        return "Dron{" +
                "id=" + id +
                ", serial='" + serial + '\'' +
                ", modelo='" + modelo + '\'' +
                ", fabricante='" + fabricante + '\'' +
                ", peso=" + peso +
                '}';
    }
}
