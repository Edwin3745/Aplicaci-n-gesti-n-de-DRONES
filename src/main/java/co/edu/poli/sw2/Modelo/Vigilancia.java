package co.edu.poli.sw2.Modelo;

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
     */
    public Vigilancia(String id, String serial, String modelo,
                      String fabricante, double peso,
                      boolean deteccionTermica) {

        super(id, serial, modelo, fabricante, peso);
        this.deteccionTermica = deteccionTermica;
    }

    public boolean isDeteccionTermica() {
        return deteccionTermica;
    }

    public void setDeteccionTermica(boolean deteccionTermica) {
        this.deteccionTermica = deteccionTermica;
    }

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
}