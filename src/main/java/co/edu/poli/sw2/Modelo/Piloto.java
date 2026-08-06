package co.edu.poli.sw2.Modelo;

public class Piloto {

    private int id;
    private String nombre;
    private int experiencia;
    private String telefono;

    public Piloto() {
    }

    public Piloto(int id, String nombre, int experiencia, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.experiencia = experiencia;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getExperiencia() { return experiencia; }
    public void setExperiencia(int experiencia) { this.experiencia = experiencia; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return "Piloto{" + "id=" + id + ", nombre='" + nombre + '\'' +
                ", experiencia=" + experiencia + ", telefono='" + telefono + '\'' + '}';
    }
}