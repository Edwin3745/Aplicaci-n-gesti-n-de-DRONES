package co.edu.poli.sw2.servicios;

import co.edu.poli.sw2.modelo.Agricultura;
import co.edu.poli.sw2.modelo.Dron;
import co.edu.poli.sw2.modelo.TipoDron;

/**
 * Fábrica del subtipo {@link Agricultura}.
 *
 * <p>Expone un método estático de conveniencia con la firma propia del
 * subtipo, y además implementa el contrato de {@link DronFactory} para poder
 * participar del registro polimórfico ({@code DronFactory.para(tipo)}).</p>
 */
public final class AgriculturaFactory extends DronFactory {

    /**
     * Crea un dron de agricultura.
     *
     * @param id              identificador del dron; 0 si aún no existe en la
     *                        base de datos.
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

    @Override
    public TipoDron getTipo() {
        return TipoDron.AGRICULTURA;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delega en {@link #crearDron(int, String, String, String, double, double)};
     * {@code deteccionTermica} se ignora porque no aplica a este subtipo.</p>
     */
    @Override
    public Dron crearDron(int id, String serial, String modelo,
                           String fabricante, double peso,
                           double capacidadTanque, boolean deteccionTermica) {
        return crearDron(id, serial, modelo, fabricante, peso, capacidadTanque);
    }
}