package co.edu.poli.sw2.modelo;

/**
 * Representa un dron destinado a actividades de vigilancia.
 */
public class Vigilancia extends Dron {

    /**
     * Indica si el dron posee detección térmica.
     */
    private boolean deteccionTermica;

    /**
     * Constructor vacío.
     */
    public Vigilancia() {
        super();
    }

    /**
     * Constructor completo.
     *
     * @param id               identificador del dron.
     * @param serial           número de serie.
     * @param modelo           modelo del dron.
     * @param fabricante       fabricante del dron.
     * @param peso             peso en kilogramos.
     * @param deteccionTermica {@code true} si el dron lleva cámara térmica.
     */
    public Vigilancia(int id, String serial, String modelo,
                      String fabricante, double peso,
                      boolean deteccionTermica) {
        super(id, serial, modelo, fabricante, peso);
        this.deteccionTermica = deteccionTermica;
    }

    /**
     * Indica si el dron cuenta con detección térmica.
     *
     * @return {@code true} si lleva cámara térmica.
     */
    public boolean isDeteccionTermica() {
        return deteccionTermica;
    }

    /**
     * Actualiza si el dron cuenta con detección térmica.
     *
     * @param deteccionTermica {@code true} si lleva cámara térmica.
     */
    public void setDeteccionTermica(boolean deteccionTermica) {
        this.deteccionTermica = deteccionTermica;
    }

    /**
     * Representación textual del dron de vigilancia.
     *
     * @return descripción del dron para diagnóstico.
     */
    @Override
    public String toString() {
        return "Vigilancia{" +
                "id='" + getId() + '\'' +
                ", serial='" + getSerial() + '\'' +
                ", modelo='" + getModelo() + '\'' +
                ", fabricante='" + getFabricante() + '\'' +
                ", peso=" + getPeso() +
                ", deteccionTermica=" + deteccionTermica +
                '}';
    }
        @Override
    public TipoDron getTipo() {
        return TipoDron.VIGILANCIA;
    }

    @Override
    public String descripcionOperativa() {
        return deteccionTermica
                ? "Patrullaje y monitoreo con cámara térmica (opera de noche)"
                : "Patrullaje y monitoreo con cámara RGB (requiere luz diurna)";
    }
}
