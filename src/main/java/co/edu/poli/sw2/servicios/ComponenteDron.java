package co.edu.poli.sw2.servicios;

/**
 * Define lo que se puede describir de un dron y de todo lo que se le añada.
 *
 * <p>Es el componente del patrón Decorator: tanto el dron sin extras como
 * cualquier accesorio que lo envuelva implementan esta misma interfaz. Gracias
 * a eso quien pide la descripción no necesita saber cuántos accesorios lleva
 * montados el equipo, ni en qué orden.</p>
 */
public interface ComponenteDron {

    /**
     * Describe el equipo tal como está configurado en este momento.
     *
     * @return descripción legible del dron con los accesorios que tenga.
     */
    String getDescripcion();
}