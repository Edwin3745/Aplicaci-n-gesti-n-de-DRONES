package co.edu.poli.sw2.servicios.facade;

import co.edu.poli.sw2.modelo.Dron;
import co.edu.poli.sw2.servicios.bridge.ControlAutonomo;
import co.edu.poli.sw2.servicios.bridge.ControlBasico;
import co.edu.poli.sw2.servicios.bridge.ControlDron;
import co.edu.poli.sw2.servicios.decorator.BateriaAdicional;
import co.edu.poli.sw2.servicios.decorator.ComponenteDron;
import co.edu.poli.sw2.servicios.decorator.DronBase;

/**
 * Fachada que prepara y ejecuta el vuelo del dron seleccionado en una sola
 * llamada.
 *
 * <p>Sin esta clase, quien quiera poner un dron a volar tiene que coordinar
 * por su cuenta tres subsistemas y conocer el orden correcto: asignar el modo
 * de control (patrón Bridge), equipar los accesorios (patrón Decorator) y
 * ejecutar el vuelo. La fachada esconde esa secuencia detrás de un único
 * método, de modo que el cliente depende de una sola clase en lugar de
 * cinco.</p>
 *
 * <p>Además garantiza el orden: el vuelo solo puede ejecutarse después de
 * asignar el control, porque un dron sin control lanza
 * {@link IllegalStateException}. Al encapsular la secuencia, el cliente no
 * puede ejecutarla en un orden incorrecto.</p>
 *
 * <p>La fachada no guarda estado: crea y usa los objetos de los subsistemas
 * dentro de cada llamada, y opera sobre el dron que recibe sin crear
 * entidades nuevas.</p>
 */
public class VueloFacade {

    /**
     * Prepara el dron indicado y ejecuta su vuelo.
     *
     * <p>Los pasos, en este orden obligatorio, son:</p>
     * <ol>
     *   <li>Asignar el modo de control, básico o autónomo (Bridge).</li>
     *   <li>Equipar la batería adicional, si se solicita (Decorator).</li>
     *   <li>Ejecutar el vuelo hacia el destino, delegando cada maniobra en el
     *       control asignado.</li>
     * </ol>
     *
     * @param dron       dron seleccionado; no puede ser nulo.
     * @param autonomo   {@code true} para control autónomo, {@code false}
     *                   para control básico.
     * @param conBateria {@code true} para montar una batería adicional.
     * @param destino    lugar hacia el que vuela el dron; no puede estar vacío.
     * @return resumen de la preparación y del vuelo, listo para mostrarse.
     * @throws IllegalArgumentException si el dron es nulo o el destino está
     *                                  vacío.
     */
    public String prepararVuelo(Dron dron, boolean autonomo,
                                boolean conBateria, String destino) {
        if (dron == null) {
            throw new IllegalArgumentException(
                    "Selecciona un dron de la tabla para preparar el vuelo.");
        }
        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("Indica un destino para el vuelo.");
        }

        // 1. Bridge: el modo de control se asigna antes que nada.
        ControlDron control = autonomo ? new ControlAutonomo() : new ControlBasico();
        dron.setControl(control);

        // 2. Decorator: el equipo se envuelve sin modificar la clase del dron.
        ComponenteDron equipo = new DronBase(dron);
        if (conBateria) {
            equipo = new BateriaAdicional(equipo);
        }

        // 3. Vuelo: ya es seguro ejecutarlo, porque el control está asignado.
        String relato = dron.ejecutarMision(destino.trim());

        return componerResumen(control, equipo, relato);
    }

    /**
     * Redacta el resumen de los tres pasos ejecutados.
     *
     * @param control modo de control asignado.
     * @param equipo  equipo con los accesorios montados.
     * @param relato  relato del vuelo ejecutado.
     * @return resumen legible de la operación.
     */
    private String componerResumen(ControlDron control, ComponenteDron equipo,
                                   String relato) {
        String salto = System.lineSeparator();
        return "1. Modo de control (Bridge): " + control.getModo() + salto
             + "2. Equipo (Decorator):       " + equipo.getDescripcion() + salto
             + "3. Vuelo:" + salto
             + sangrar(relato);
    }

    /**
     * Añade sangría a cada línea del texto recibido.
     *
     * @param texto texto de varias líneas.
     * @return el mismo texto con sangría.
     */
    private String sangrar(String texto) {
        return "   " + texto.replace(System.lineSeparator(),
                System.lineSeparator() + "   ");
    }
}