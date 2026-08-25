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

    public OperacionFallidaException(String mensajeParaElUsuario) {
        super(mensajeParaElUsuario);
    }

    public OperacionFallidaException(String mensajeParaElUsuario, Throwable causa) {
        super(mensajeParaElUsuario, causa);
    }
}