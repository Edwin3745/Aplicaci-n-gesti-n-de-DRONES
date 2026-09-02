package co.edu.poli.sw2.servicios;

import co.edu.poli.sw2.Modelo.Agricultura;

/**
 * Fábrica del subtipo {@link Agricultura}.
 *
 * <p>Concentra la construcción de los drones de agricultura, de modo que
 * ninguna otra capa necesite invocar directamente su constructor. Si mañana
 * crear un dron de agricultura exigiera un paso adicional —un valor por
 * defecto, una validación propia del subtipo—, este es el único archivo que
 * habría que tocar.</p>
 *
 * <p>Sustituye, junto con {@link VigilanciaFactory}, a la antigua fábrica única
 * que decidía el subtipo con un {@code switch}. Cada subtipo tiene ahora su
 * propia fábrica, y quien construye drones elige cuál usar.</p>
 */
public final class AgriculturaFactory {

    /**
     * Constructor privado: la clase solo expone métodos estáticos y no debe
     * instanciarse.
     */
    private AgriculturaFactory() {
    }

    /**
     * Crea un dron de agricultura.
     *
     * @param id              identificador del dron; se envía 0 cuando aún no
     *                        existe en la base de datos y el id lo generará ella.
     * @param serial          número de serie del dron.
     * @param modelo          modelo del dron.
     * @param fabricante      fabricante del dron.
     * @param peso            peso en kilogramos.
     * @param capacidadTanque capacidad del tanque en litros.
     * @return instancia de {@link Agricultura} con los datos indicados.
     */
    public static Agricultura crearDron(int id, String serial, String modelo,
                                        String fabricante, double peso,
                                        double capacidadTanque) {
        return new Agricultura(id, serial, modelo, fabricante, peso, capacidadTanque);
    }
}
