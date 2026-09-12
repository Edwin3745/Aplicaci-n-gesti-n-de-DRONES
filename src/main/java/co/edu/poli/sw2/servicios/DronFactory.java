package co.edu.poli.sw2.servicios;

import co.edu.poli.sw2.modelo.Dron;
import co.edu.poli.sw2.modelo.TipoDron;

import java.util.EnumMap;
import java.util.Map;

/**
 * Base común de las fábricas de drones.
 *
 * <p>Declara el contrato que cumple toda fábrica concreta y mantiene el
 * registro de las disponibles, de modo que quien necesite un dron pida la
 * fábrica por su tipo sin conocer las clases concretas que existen.</p>
 *
 * <p>Los métodos de creación son de instancia y no estáticos: en Java un método
 * estático no puede declararse abstracto ni sobrescribirse, así que sin
 * instancias no habría polimorfismo y las subclases no podrían especializar la
 * construcción.</p>
 */
public abstract class DronFactory {

    /** Fábrica registrada para cada subtipo de dron. */
    private static final Map<TipoDron, DronFactory> FABRICAS = new EnumMap<>(TipoDron.class);

    static {
        FABRICAS.put(TipoDron.AGRICULTURA, new AgriculturaFactory());
        FABRICAS.put(TipoDron.VIGILANCIA, new VigilanciaFactory());
    }

    /**
     * Devuelve la fábrica que corresponde al tipo indicado.
     *
     * <p>Es el único punto del sistema que conoce todas las fábricas concretas.
     * Quien la use recibe un {@code DronFactory} y trabaja contra el contrato,
     * no contra la clase.</p>
     *
     * @param tipo subtipo de dron a construir.
     * @return fábrica capaz de construir ese subtipo.
     * @throws IllegalArgumentException si el tipo es nulo o no tiene fábrica.
     */
    public static DronFactory para(TipoDron tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de dron no puede ser nulo.");
        }
        DronFactory fabrica = FABRICAS.get(tipo);
        if (fabrica == null) {
            throw new IllegalArgumentException("No hay fábrica registrada para el tipo " + tipo);
        }
        return fabrica;
    }

    /**
     * Subtipo de dron que produce esta fábrica.
     *
     * @return tipo que esta fábrica sabe construir.
     */
    public abstract TipoDron getTipo();

    /**
     * Construye un dron del subtipo de esta fábrica.
     *
     * <p>La firma incluye los atributos de todos los subtipos; cada fábrica usa
     * los que le corresponden e ignora el resto.</p>
     *
     * @param id               identificador; 0 si lo genera la base de datos.
     * @param serial           número de serie.
     * @param modelo           modelo del dron.
     * @param fabricante       fabricante del dron.
     * @param peso             peso en kilogramos.
     * @param capacidadTanque  litros del tanque; solo aplica a agricultura.
     * @param deteccionTermica cámara térmica; solo aplica a vigilancia.
     * @return dron del subtipo correspondiente.
     */
    public abstract Dron crearDron(int id, String serial, String modelo,
                                   String fabricante, double peso,
                                   double capacidadTanque, boolean deteccionTermica);
                                   }