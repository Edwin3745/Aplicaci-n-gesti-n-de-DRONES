package co.edu.poli.sw2.Modelo;

import java.util.Date;

public class Mision {

    private int id;
    private String nombre;
    private String ubicacion;
    private Date fecha;

    public Mision() {
    }

    public Mision(int id, String nombre, String ubicacion, Date fecha) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    @Override
    public String toString() {
        return "Mision{" + "id=" + id + ", nombre='" + nombre + '\'' +
                ", ubicacion='" + ubicacion + '\'' + ", fecha=" + fecha + '}';
    }
}