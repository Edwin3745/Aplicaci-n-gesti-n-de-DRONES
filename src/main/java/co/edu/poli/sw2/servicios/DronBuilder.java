package co.edu.poli.sw2.servicios;

import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.TipoDron;

/**
 * Constructor paso a paso de drones, según el patrón Builder.
 *
 * <p>Un dron tiene siete datos y no todos aplican a todos los subtipos, así que
 * llamar al constructor con la lista completa de parámetros es fácil de
 * equivocar: basta intercambiar dos cadenas contiguas para guardar el modelo en
 * el campo del fabricante sin que el compilador se entere. Este builder
 * sustituye esa lista por una secuencia de llamadas con nombre, donde cada dato
 * se ve junto a su significado.</p>
 *
 * <p>Uso típico:</p>
 * <pre>
 * Dron dron = new DronBuilder()
 *         .conTipo(TipoDron.AGRICULTURA)
 *         .conSerial("AGR-001")
 *         .conModelo("Agras T40")
 *         .conFabricante("DJI")
 *         .conPeso(38.0)
 *         .conCapacidadTanque(40.0)
 *         .build();
 * </pre>
 *
 * <p>El builder no construye ninguna subclase por su cuenta: {@link #build()}
 * delega en {@link AgriculturaFactory} o {@link VigilanciaFactory} según el
 * tipo indicado. Los dos patrones se reparten el trabajo: el Builder reúne y
 * valida los datos, la fábrica sabe qué subclase corresponde a cada tipo. Como
 * consecuencia, este es el <em>único</em> punto del proyecto que traduce un
 * {@link TipoDron} en una subclase concreta.</p>
 *
 * <p>La instancia es reutilizable: tras un {@code build()} se pueden cambiar
 * algunos datos y volver a construir, lo que resulta cómodo para dar de alta
 * varios drones parecidos.</p>
 */
public class DronBuilder {

    private TipoDron tipo;
    private int id;
    private String serial;
    private String modelo;
    private String fabricante;
    private double peso;
    private double capacidadTanque;
    private boolean deteccionTermica;

    /**
     * Crea un builder sin ningún dato fijado.
     */
    public DronBuilder() {
    }

    /**
     * Indica el subtipo de dron que se va a construir.
     *
     * @param tipo subtipo deseado.
     * @return este mismo builder, para poder encadenar la siguiente llamada.
     */
    public DronBuilder conTipo(TipoDron tipo) {
        this.tipo = tipo;
        return this;
    }

    /**
     * Fija el identificador del dron.
     *
     * <p>Solo debe usarse al reconstruir un dron que ya existe en la base de
     * datos. En un alta se omite: el identificador lo genera PostgreSQL.</p>
     *
     * @param id identificador del dron.
     * @return este mismo builder, para poder encadenar la siguiente llamada.
     */
    public DronBuilder conId(int id) {
        this.id = id;
        return this;
    }

    /**
     * Fija el número de serie del dron.
     *
     * @param serial número de serie; es obligatorio y único en el sistema.
     * @return este mismo builder, para poder encadenar la siguiente llamada.
     */
    public DronBuilder conSerial(String serial) {
        this.serial = serial;
        return this;
    }

    /**
     * Fija el modelo del dron.
     *
     * @param modelo modelo del dron; es obligatorio.
     * @return este mismo builder, para poder encadenar la siguiente llamada.
     */
    public DronBuilder conModelo(String modelo) {
        this.modelo = modelo;
        return this;
    }

    /**
     * Fija el fabricante del dron.
     *
     * @param fabricante fabricante del dron; es obligatorio.
     * @return este mismo builder, para poder encadenar la siguiente llamada.
     */
    public DronBuilder conFabricante(String fabricante) {
        this.fabricante = fabricante;
        return this;
    }

    /**
     * Fija el peso del dron.
     *
     * @param peso peso en kilogramos; no puede ser negativo.
     * @return este mismo builder, para poder encadenar la siguiente llamada.
     */
    public DronBuilder conPeso(double peso) {
        this.peso = peso;
        return this;
    }

    /**
     * Fija la capacidad del tanque. Solo aplica al subtipo AGRICULTURA.
     *
     * @param capacidadTanque capacidad en litros; no puede ser negativa.
     * @return este mismo builder, para poder encadenar la siguiente llamada.
     */
    public DronBuilder conCapacidadTanque(double capacidadTanque) {
        this.capacidadTanque = capacidadTanque;
        return this;
    }

    /**
     * Indica si el dron lleva cámara térmica. Solo aplica al subtipo VIGILANCIA.
     *
     * @param deteccionTermica {@code true} si dispone de detección térmica.
     * @return este mismo builder, para poder encadenar la siguiente llamada.
     */
    public DronBuilder conDeteccionTermica(boolean deteccionTermica) {
        this.deteccionTermica = deteccionTermica;
        return this;
    }

    /**
     * Valida los datos reunidos y construye el dron.
     *
     * <p>La validación ocurre antes de instanciar nada, de modo que nunca llega
     * a existir un dron a medio construir. El tipo determina a qué fábrica se
     * delega la creación.</p>
     *
     * @return el dron construido: una {@code Agricultura} o una
     *         {@code Vigilancia}, según el tipo indicado.
     * @throws IllegalStateException si falta algún dato obligatorio o si alguno
     *                               tiene un valor inadmisible. El mensaje
     *                               indica cuál es el dato que falla.
     */
    public Dron build() {
        validar();

        return switch (tipo) {
            case AGRICULTURA -> AgriculturaFactory.crearDron(
                    id, serial.trim(), modelo.trim(), fabricante.trim(),
                    peso, capacidadTanque);

            case VIGILANCIA -> VigilanciaFactory.crearDron(
                    id, serial.trim(), modelo.trim(), fabricante.trim(),
                    peso, deteccionTermica);
        };
    }

    /**
     * Comprueba que los datos reunidos permitan construir un dron válido.
     *
     * @throws IllegalStateException si algún dato falta o es inadmisible.
     */
    private void validar() {
        if (tipo == null) {
            throw new IllegalStateException(
                    "Falta el tipo de dron: sin él no se sabe qué subtipo construir.");
        }
        if (esVacio(serial)) {
            throw new IllegalStateException("Falta el serial del dron.");
        }
        if (esVacio(modelo)) {
            throw new IllegalStateException("Falta el modelo del dron.");
        }
        if (esVacio(fabricante)) {
            throw new IllegalStateException("Falta el fabricante del dron.");
        }
        if (peso < 0) {
            throw new IllegalStateException(
                    "El peso del dron no puede ser negativo. Valor recibido: " + peso);
        }
        if (tipo == TipoDron.AGRICULTURA && capacidadTanque < 0) {
            throw new IllegalStateException(
                    "La capacidad del tanque no puede ser negativa. Valor recibido: "
                    + capacidadTanque);
        }
    }

    /**
     * Indica si un texto obligatorio no se ha proporcionado.
     *
     * @param texto texto a comprobar.
     * @return {@code true} si es nulo o solo contiene espacios.
     */
    private static boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
