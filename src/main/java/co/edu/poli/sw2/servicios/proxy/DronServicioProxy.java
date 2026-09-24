package co.edu.poli.sw2.servicios.proxy;

import java.util.Objects;

/**
 * Proxy de protección para la eliminación de drones.
 *
 * <p>Valida la contraseña y solo entonces delega en el servicio real. Si la
 * contraseña no coincide, no se llama al servicio real y el dron no se
 * elimina.</p>
 */
public class DronServicioProxy implements DronServicio {

    /** Contraseña por defecto que autoriza la eliminación. */
    public static final String CONTRASENA_POR_DEFECTO = "admin123";

    private final DronServicio servicioReal;
    private final String contrasenaValida;
    private String contrasenaIngresada;

    /**
     * Crea el proxy con la contraseña por defecto.
     *
     * @param servicioReal objeto al que se delega cuando la contraseña es válida.
     */
    public DronServicioProxy(DronServicio servicioReal) {
        this(servicioReal, CONTRASENA_POR_DEFECTO);
    }

    /**
     * Crea el proxy con una contraseña específica.
     *
     * @param servicioReal     objeto al que se delega cuando la contraseña es válida.
     * @param contrasenaValida contraseña que autoriza la eliminación.
     */
    public DronServicioProxy(DronServicio servicioReal, String contrasenaValida) {
        this.servicioReal = Objects.requireNonNull(servicioReal);
        this.contrasenaValida = Objects.requireNonNull(contrasenaValida);
    }

    /**
     * Registra la contraseña que escribió el usuario, para validarla en el
     * próximo {@link #eliminar(int)}.
     *
     * @param contrasenaIngresada contraseña escrita por el usuario.
     */
    public void setContrasenaIngresada(String contrasenaIngresada) {
        this.contrasenaIngresada = contrasenaIngresada;
    }

    /**
     * Indica si la contraseña ingresada coincide con la válida.
     *
     * @return {@code true} si coincide.
     */
    public boolean contrasenaEsCorrecta() {
        return contrasenaIngresada != null && contrasenaValida.equals(contrasenaIngresada);
    }

    /**
     * Elimina el dron solo si la contraseña es correcta.
     *
     * @param id identificador del dron.
     * @return {@code true} si se eliminó; {@code false} si la contraseña no
     *         coincide (en ese caso no se toca el servicio real).
     */
    @Override
    public boolean eliminar(int id) {
        try {
            if (!contrasenaEsCorrecta()) {
                return false;
            }
            return servicioReal.eliminar(id);
        } finally {
            contrasenaIngresada = null; // la contraseña sirve para un solo intento
        }
    }
}