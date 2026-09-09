package co.edu.poli.sw2.servicios;

import co.edu.poli.sw2.modelo.Dron;

/**
 * Resultado de ejecutar la demostración de un patrón de creación.
 *
 * <p>Agrupa las dos cosas que la interfaz necesita tras pulsar los botones
 * "Clonar" y "Builder": el dron obtenido, que se carga en el formulario, y el
 * informe redactado, que se vuelca en el área de evidencia. Devolver ambas
 * juntas evita que la vista tenga que pedirlas por separado y mantener
 * sincronizados los datos de las dos llamadas.</p>
 *
 * @param dron    dron producido por el patrón.
 * @param informe texto que explica lo ocurrido, listo para mostrarse.
 */
public record DemostracionPatron(Dron dron, String informe) {
}
