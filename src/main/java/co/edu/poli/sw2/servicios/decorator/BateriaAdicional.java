package co.edu.poli.sw2.servicios.decorator;

/**
 * Añade una batería adicional a un dron ya configurado.
 *
 * <p>Es el decorador del patrón: implementa {@link ComponenteDron} y a la vez
 * contiene otro {@link ComponenteDron}. Esa doble condición es lo que define el
 * patrón, y es lo que permite envolver un equipo sin modificar su clase ni
 * crear una subclase por cada combinación de accesorios.</p>
 *
 * <p>Al pedirle la descripción, delega primero en lo que envuelve y añade
 * después su propia aportación, de modo que el resultado refleja el equipo
 * completo.</p>
 */
public class BateriaAdicional implements ComponenteDron {

    /** Capacidad por defecto de la batería, en miliamperios-hora. */
    public static final int CAPACIDAD_POR_DEFECTO = 5000;

    /** Equipo sobre el que se monta la batería. */
    private final ComponenteDron componente;

    /** Capacidad de la batería en miliamperios-hora. */
    private final int capacidadMah;

    /**
     * Monta una batería de capacidad estándar.
     *
     * @param componente equipo a ampliar; no puede ser nulo.
     */
    public BateriaAdicional(ComponenteDron componente) {
        this(componente, CAPACIDAD_POR_DEFECTO);
    }

    /**
     * Monta una batería de la capacidad indicada.
     *
     * @param componente   equipo a ampliar; no puede ser nulo.
     * @param capacidadMah capacidad en miliamperios-hora; debe ser positiva.
     * @throws IllegalArgumentException si el componente es nulo o la capacidad
     *                                  no es positiva.
     */
    public BateriaAdicional(ComponenteDron componente, int capacidadMah) {
        if (componente == null) {
            throw new IllegalArgumentException("El componente a decorar no puede ser nulo.");
        }
        if (capacidadMah <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }
        this.componente = componente;
        this.capacidadMah = capacidadMah;
    }

    /**
     * Describe el equipo envuelto y añade la batería adicional.
     *
     * @return descripción del equipo incluyendo la batería.
     */
    @Override
    public String getDescripcion() {
        return componente.getDescripcion()
             + " + batería adicional de " + capacidadMah + " mAh";
    }

    /**
     * @return capacidad de esta batería en miliamperios-hora.
     */
    public int getCapacidadMah() {
        return capacidadMah;
    }
}