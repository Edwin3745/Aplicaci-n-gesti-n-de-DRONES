package co.edu.poli.sw2.servicios;

import co.edu.poli.sw2.modelo.Vigilancia;

/**
 * Fábrica del subtipo {@link Vigilancia}.
 *
 * <p>Concentra la construcción de los drones de vigilancia, de modo que
 * ninguna otra capa necesite invocar directamente su constructor. Si mañana
 * crear un dron de vigilancia exigiera un paso adicional —un valor por
 * defecto, una validación propia del subtipo—, este es el único archivo que
 * habría que tocar.</p>
 *
 * <p>Sustituye, junto con {@link AgriculturaFactory}, a la antigua fábrica
 * única que decidía el subtipo con un {@code switch}. Cada subtipo tiene ahora
 * su propia fábrica, y quien construye drones elige cuál usar.</p>
 */
public final class VigilanciaFactory {

    /**
     * Constructor privado: la clase solo expone métodos estáticos y no debe
     * instanciarse.
     */
    private VigilanciaFactory() {
    }

    /**
     * Crea un dron de vigilancia.
     *
     * @param id               identificador del dron; se envía 0 cuando aún no
     *                         existe en la base de datos y el id lo generará ella.
     * @param serial           número de serie del dron.
     * @param modelo           modelo del dron.
     * @param fabricante       fabricante del dron.
     * @param peso             peso en kilogramos.
     * @param deteccionTermica {@code true} si el dron lleva cámara térmica.
     * @return instancia de {@link Vigilancia} con los datos indicados.
     */
    public static Vigilancia crearDron(int id, String serial, String modelo,
                                       String fabricante, double peso,
                                       boolean deteccionTermica) {
        return new Vigilancia(id, serial, modelo, fabricante, peso, deteccionTermica);
    }
}
