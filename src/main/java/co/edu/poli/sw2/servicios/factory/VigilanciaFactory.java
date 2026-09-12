package co.edu.poli.sw2.servicios.factory;

import co.edu.poli.sw2.modelo.Dron;
import co.edu.poli.sw2.modelo.TipoDron;
import co.edu.poli.sw2.modelo.Vigilancia;

/**
 * Fábrica del subtipo {@link Vigilancia}.
 *
 * <p>Expone un método estático de conveniencia con la firma propia del
 * subtipo, y además implementa el contrato de {@link DronFactory} para poder
 * participar del registro polimórfico ({@code DronFactory.para(tipo)}).</p>
 */
public final class VigilanciaFactory extends DronFactory {

    /**
     * Crea un dron de vigilancia.
     *
     * @param id               identificador del dron; 0 si aún no existe en
     *                         la base de datos.
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

    @Override
    public TipoDron getTipo() {
        return TipoDron.VIGILANCIA;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delega en {@link #crearDron(int, String, String, String, double, boolean)};
     * {@code capacidadTanque} se ignora porque no aplica a este subtipo.</p>
     */
    @Override
    public Dron crearDron(int id, String serial, String modelo,
                           String fabricante, double peso,
                           double capacidadTanque, boolean deteccionTermica) {
        return crearDron(id, serial, modelo, fabricante, peso, deteccionTermica);
    }
}