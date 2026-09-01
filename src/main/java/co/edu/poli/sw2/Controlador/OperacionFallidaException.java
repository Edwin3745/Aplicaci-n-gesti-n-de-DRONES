package co.edu.poli.sw2.Controlador;

/**
 * Excepción de la capa de control cuyo mensaje está redactado para mostrarse
 * directamente al usuario.
 *
 * <p>La vista solo necesita capturar este tipo y presentar
 * {@link #getMessage()}: no tiene que conocer códigos SQL ni tipos de
 * excepción de la capa de persistencia.</p>
 */
public class OperacionFallidaException extends RuntimeException {

    /**
     * Crea la excepción con el mensaje que verá el usuario.
     *
     * @param mensajeParaElUsuario texto redactado para mostrarse en pantalla.
     */
    public OperacionFallidaException(String mensajeParaElUsuario) {
        super(mensajeParaElUsuario);
    }

    /**
     * Crea la excepción conservando el fallo técnico que la originó.
     *
     * @param mensajeParaElUsuario texto redactado para mostrarse en pantalla.
     * @param causa                excepción original, que se conserva para el
     *                             registro de errores pero no llega a la vista.
     */
    public OperacionFallidaException(String mensajeParaElUsuario, Throwable causa) {
        super(mensajeParaElUsuario, causa);
    }
}