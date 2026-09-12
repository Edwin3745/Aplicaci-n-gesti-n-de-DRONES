package co.edu.poli.sw2.servicios.decorator;

import co.edu.poli.sw2.modelo.Dron;

/**
 * Envuelve un dron sin accesorios: es el punto de partida de la decoración.
 *
 * <p>Es el componente concreto del patrón Decorator. Adapta un {@link Dron} del
 * modelo a la interfaz {@link ComponenteDron}, de modo que el patrón funcione
 * sin que el dominio tenga que conocerlo.</p>
 */
public class DronBase implements ComponenteDron {

    /** Dron al que corresponde esta descripción. */
    private final Dron dron;

    /**
     * @param dron dron a describir; no puede ser nulo.
     * @throws IllegalArgumentException si el dron es nulo.
     */
    public DronBase(Dron dron) {
        if (dron == null) {
            throw new IllegalArgumentException("El dron no puede ser nulo.");
        }
        this.dron = dron;
    }

    /**
     * Describe el dron con su configuración de fábrica, sin accesorios.
     *
     * @return descripción base del equipo.
     */
    @Override
    public String getDescripcion() {
        return dron.getTipo().getCodigo() + " " + dron.getSerial()
             + " (" + dron.getModelo() + ", " + dron.getPeso() + " kg)";
    }

    /**
     * @return el dron envuelto.
     */
    public Dron getDron() {
        return dron;
    }
}