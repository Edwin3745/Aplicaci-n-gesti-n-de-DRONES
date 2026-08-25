package co.edu.poli.sw2.Modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa la entidad base del sistema de gestión de drones.
 *
 * Dron es una clase abstracta que contiene los atributos comunes
 * de los diferentes tipos de drones.
 *
 * Las clases Agricultura y Vigilancia heredan de esta clase.
 */
public abstract class Dron {

    /**
     * Identificador único del dron.
     */
    private String id;

    /**
     * Número de serie del dron.
     */
    private String serial;

    /**
     * Modelo del dron.
     */
    private String modelo;

    /**
     * Fabricante del dron.
     */
    private String fabricante;

    /**
     * Peso del dron.
     */
    private double peso;

    /**
     * Piloto asignado al dron.
     */
    private Piloto piloto;

    /**
     * Sensores asociados al dron.
     */
    private List<Sensor> sensores;

    /**
     * Constructor vacío.
     */
    public Dron() {
        this.sensores = new ArrayList<>();
    }

    /**
     * Constructor con los atributos comunes.
     *
     * @param id identificador del dron
     * @param serial número de serie
     * @param modelo modelo del dron
     * @param fabricante fabricante del dron
     * @param peso peso del dron
     */
    public Dron(String id, String serial, String modelo,
                String fabricante, double peso) {

        this.id = id;
        this.serial = serial;
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.peso = peso;
        this.sensores = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public Piloto getPiloto() {
        return piloto;
    }

    void setPiloto(Piloto piloto) {
        this.piloto = piloto;
    }

    public List<Sensor> getSensores() {
        return sensores;
    }

    public void setSensores(List<Sensor> sensores) {
        this.sensores = sensores;
    }

    @Override
    public String toString() {
        return "Dron{" +
                "id='" + id + '\'' +
                ", serial='" + serial + '\'' +
                ", modelo='" + modelo + '\'' +
                ", fabricante='" + fabricante + '\'' +
                ", peso=" + peso +
                '}';
    }
}