package co.edu.poli.sw2.servicios.bridge;

/**
 * Control autónomo: el dron ejecuta la misión por sí mismo a partir de una ruta
 * programada.
 *
 * <p>Es la segunda implementación intercambiable del Bridge. Ofrece exactamente
 * las mismas operaciones que {@link ControlBasico}, pero resueltas de otra
 * manera; por eso quien las use no necesita saber cuál de las dos tiene
 * delante.</p>
 */
public class ControlAutonomo implements ControlDron {

    /** Nombre con el que este modo se identifica en la interfaz. */
    private static final String MODO = "Autónomo";

    @Override
    public String despegar() {
        return "[" + MODO + "] Secuencia de despegue automática iniciada; "
             + "verificación de sensores completada.";
    }

    @Override
    public String navegar(String destino) {
        return "[" + MODO + "] Ruta calculada hacia " + destino
             + "; navegación GPS activa con evasión de obstáculos.";
    }

    @Override
    public String aterrizar() {
        return "[" + MODO + "] Descenso autónomo sobre el punto de origen; "
             + "motores detenidos al contacto.";
    }

    @Override
    public String getModo() {
        return MODO;
    }
}