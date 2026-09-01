package co.edu.poli.sw2.modelo;

/**
 * Contrato de los objetos capaces de producir una copia independiente de sí
 * mismos, tal como exige el patrón Prototype.
 *
 * <p>Se define una interfaz propia en lugar de recurrir a {@code Cloneable} por
 * tres razones concretas. {@code Cloneable} no declara ningún método, de modo
 * que no sirve como contrato; {@code Object.clone()} es {@code protected},
 * devuelve {@code Object} y obliga a capturar una excepción comprobada que aquí
 * nunca podría ocurrir; y, sobre todo, {@code super.clone()} produce una copia
 * superficial que habría que reparar campo por campo, algo imposible cuando el
 * campo es {@code final}, como la lista de sensores de {@link Dron}.</p>
 *
 * <p>Al declarar el método aquí, el tipo de retorno es {@link Dron} y cada
 * subclase decide explícitamente qué se copia y qué no.</p>
 */
public interface Prototipo {

    /**
     * Crea una copia independiente de este objeto.
     *
     * <p>Independiente significa que modificar la copia —incluidas sus
     * colecciones— no debe alterar el original, ni al revés.</p>
     *
     * @return una copia del objeto, de su misma clase concreta.
     */
    Dron copiar();
}
