package co.edu.poli.sw2.servicios.composite;

/**
 * Define lo que se puede pedir a cualquier elemento de la jerarquía de sensores.
 *
 * <p>Es el componente del patrón Composite: tanto un sensor individual como un
 * grupo que contiene otros sensores implementan esta misma interfaz. Gracias a
 * eso, quien recorre la jerarquía no necesita distinguir si tiene delante una
 * hoja o un grupo, ni cuántos niveles de anidamiento hay por debajo.</p>
 *
 * <p>Sin esta interfaz común, cada operación sobre el árbol tendría que
 * comprobar el tipo de cada elemento con {@code instanceof} y recorrer los
 * hijos a mano, repitiendo esa lógica en cada operación nueva.</p>
 */
public interface SensorComponente {

    /**
     * Nombre con el que se identifica este elemento de la jerarquía.
     *
     * @return nombre del sensor o del grupo.
     */
    String getNombre();

    /**
     * Describe este elemento y, si tiene hijos, todo lo que cuelga de él.
     *
     * <p>Una hoja devuelve una sola línea; un grupo devuelve la suya y a
     * continuación la de cada uno de sus hijos, con la sangría que corresponda
     * a su profundidad.</p>
     *
     * @return representación textual de la rama, lista para mostrarse.
     */
    String mostrarInfo();

    /**
     * Cuenta los sensores individuales que hay bajo este elemento.
     *
     * <p>Una hoja cuenta uno; un grupo suma lo que reporten sus hijos. Es la
     * operación que mejor muestra la recursión del patrón: el mismo método
     * resuelve tanto un sensor suelto como un árbol de cuatro niveles.</p>
     *
     * @return número de sensores individuales de esta rama.
     */
    int contarSensores();
}