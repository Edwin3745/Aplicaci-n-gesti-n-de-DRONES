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
     */
      public Vigilancia(int id, String serial, String modelo,
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
