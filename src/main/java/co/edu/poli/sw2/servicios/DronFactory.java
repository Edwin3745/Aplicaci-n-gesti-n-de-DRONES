package co.edu.poli.sw2.servicios;

import co.edu.poli.sw2.Modelo.Agricultura;
import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.TipoDron;
import co.edu.poli.sw2.Modelo.Vigilancia;

/**
 * Fábrica de drones.
 *
 * Centraliza la construcción de las subclases concretas de {@link Dron}, de modo
 * que ninguna otra capa necesite conocerlas ni decidir cuál instanciar. El
 * Controlador pide un dron indicando su tipo y recibe la instancia adecuada.
 */
public class DronFactory {

    /**
     * Constructor privado: esta clase solo expone métodos estáticos y no debe
     * instanciarse.
     */
    private DronFactory() {
    }

    /**
     * Crea la instancia concreta de dron que corresponda al tipo indicado.
     *
     * @param tipo              subtipo de dron a construir
     * @param id                identificador del dron
     * @param serial            número de serie
     * @param modelo            modelo del dron
     * @param fabricante        fabricante del dron
     * @param peso              peso en kilogramos
     * @param capacidadTanque   litros del tanque; solo se usa si el tipo es AGRICULTURA
     * @param deteccionTermica  cámara térmica; solo se usa si el tipo es VIGILANCIA
     * @return instancia de {@link Agricultura} o {@link Vigilancia} según el tipo
     * @throws IllegalArgumentException si el tipo es nulo
     */
    public static Dron crearDron(
            TipoDron tipo,
            int id,
            String serial,
            String modelo,
            String fabricante,
            double peso,
            double capacidadTanque,
            boolean deteccionTermica) {

        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de dron no puede ser nulo.");
        }

        switch (tipo) {

            case AGRICULTURA:
                return new Agricultura(
                        id,
                        serial,
                        modelo,
                        fabricante,
                        peso,
                        capacidadTanque
                );

            case VIGILANCIA:
                return new Vigilancia(
                        id,
                        serial,
                        modelo,
                        fabricante,
                        peso,
                        deteccionTermica
                );

            default:
                throw new IllegalArgumentException("Tipo de dron no soportado: " + tipo);
        }
    }
}