package co.edu.poli.sw2.servicios;

/**
 * Define cómo se gobierna un dron durante una operación.
 *
 * <p>Es el lado del implementador en el patrón Bridge: separa <em>qué</em> hace
 * el dron de <em>cómo</em> se controla, de modo que ambas cosas puedan variar
 * por separado. Sin esta separación, cada combinación de operación y modo de
 * control exigiría una clase propia, y el número de clases crecería
 * multiplicándose en vez de sumándose.</p>
 *
 * <p>Las implementaciones son intercambiables en tiempo de ejecución: quien use
 * un control puede sustituirlo por otro sin recrear nada más.</p>
 */
public interface ControlDron {

    /**
     * Ejecuta la maniobra de despegue según el modo de control.
     *
     * @return descripción de lo ocurrido, para mostrarla al usuario.
     */
    String despegar();

    /**
     * Dirige el dron hacia el destino indicado.
     *
     * @param destino lugar al que debe desplazarse el dron.
     * @return descripción de lo ocurrido, para mostrarla al usuario.
     */
    String navegar(String destino);

    /**
     * Ejecuta la maniobra de aterrizaje según el modo de control.
     *
     * @return descripción de lo ocurrido, para mostrarla al usuario.
     */
    String aterrizar();

    /**
     * Nombre legible del modo de control, para identificarlo en la interfaz.
     *
     * @return nombre del modo, por ejemplo "Básico (manual)".
     */
    String getModo();
}