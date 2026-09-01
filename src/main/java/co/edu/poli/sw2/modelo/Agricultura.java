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
     */
    public Agricultura(int id, String serial, String modelo,
                       String fabricante, double peso,
                       double capacidadTanque) {
        super(id, serial, modelo, fabricante, peso);
        this.capacidadTanque = capacidadTanque;
    }

    public double getCapacidadTanque() {
        return capacidadTanque;
    }

    public void setCapacidadTanque(double capacidadTanque) {
        this.capacidadTanque = capacidadTanque;
    }

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