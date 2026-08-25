package co.edu.poli.sw2.Servicio;

import co.edu.poli.sw2.Modelo.Agricultura;
import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.vigilancia;

/**
 * Factory encargada de crear instancias concretas de Dron.
 *
 * Esta clase centraliza la creación de objetos Agricultura y Vigilancia,
 * evitando que el controlador tenga que instanciar directamente las
 * clases concretas.
 */
public class DronFactory {

    /**
     * Constructor privado para evitar la creación de instancias
     * innecesarias de la Factory.
     */
    private DronFactory() {
    }

    /**
     * Crea un dron según el tipo indicado.
     *
     * @param tipo tipo de dron: "agricultura" o "vigilancia"
     * @param id identificador del dron
     * @param serial número de serie
     * @param modelo modelo del dron
     * @param fabricante fabricante del dron
     * @param peso peso del dron
     * @param capacidadTanque capacidad del tanque para drones agrícolas
     * @param deteccionTermica indica si el dron de vigilancia tiene
     *                         detección térmica
     * @return una instancia de Agricultura o Vigilancia
     * @throws IllegalArgumentException si el tipo no es válido
     */
    public static Dron crearDron(
            String tipo,
            String id,
            String serial,
            String modelo,
            String fabricante,
            double peso,
            double capacidadTanque,
            boolean deteccionTermica) {

        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de dron no puede ser nulo.");
        }

        switch (tipo.toLowerCase()) {

            case "agricultura":
                return new Agricultura(
                        id,
                        serial,
                        modelo,
                        fabricante,
                        peso,
                        capacidadTanque
                );

            case "vigilancia":
                return new Vigilancia(
                        id,
                        serial,
                        modelo,
                        fabricante,
                        peso,
                        deteccionTermica
                );

            default:
                throw new IllegalArgumentException(
                        "Tipo de dron no válido: " + tipo
                );
        }
    }
}