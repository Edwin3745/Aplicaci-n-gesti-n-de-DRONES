package co.edu.poli.sw2.servicios.bridge;

/**
 * Control manual: cada maniobra la ordena el piloto desde la estación de mando.
 *
 * <p>Es una de las dos implementaciones intercambiables del Bridge. El dron no
 * toma ninguna decisión por su cuenta: responde a las órdenes que recibe.</p>
 */
public class ControlBasico implements ControlDron {

    /** Nombre con el que este modo se identifica en la interfaz. */
    private static final String MODO = "Básico (manual)";

    @Override
    public String despegar() {
        return "[" + MODO + "] El piloto acciona el despegue manualmente.";
    }

    @Override
    public String navegar(String destino) {
        return "[" + MODO + "] El piloto dirige el dron hacia " + destino
             + ", manteniendo contacto visual.";
    }

    @Override
    public String aterrizar() {
        return "[" + MODO + "] El piloto ejecuta el aterrizaje y corta motores.";
    }

    @Override
    public String getModo() {
        return MODO;
    }
}