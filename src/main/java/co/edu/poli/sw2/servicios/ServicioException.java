package co.edu.poli.sw2.servicios;

/**
 * Excepción propia de la capa de servicio.
 *
 * <p>Envuelve los fallos técnicos de la persistencia (por ejemplo
 * {@link java.sql.SQLException}) para que las capas superiores no dependan
 * de JDBC. Conserva la excepción original como causa, de modo que la
 * información técnica siga disponible para el registro de errores sin
 * llegar a la interfaz de usuario.</p>
 */
public class ServicioException extends RuntimeException {

    /** Código SQLSTATE devuelto por la base de datos, si lo hubo. */
    private final String codigoSql;

    public ServicioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigoSql = extraerCodigo(causa);
    }

    public ServicioException(String mensaje) {
        super(mensaje);
        this.codigoSql = null;
    }

    /**
     * @return código SQLSTATE de la causa, o {@code null} si no procede de la BD.
     */
    public String getCodigoSql() {
        return codigoSql;
    }

    /** @return {@code true} si el fallo fue por violar una restricción UNIQUE. */
    public boolean esDuplicado() {
        return "23505".equals(codigoSql);
    }

    /** @return {@code true} si el fallo fue por violar una restricción CHECK o NOT NULL. */
    public boolean esViolacionDeRegla() {
        return "23514".equals(codigoSql) || "23502".equals(codigoSql);
    }

    /** @return {@code true} si el fallo fue por violar una clave foránea. */
    public boolean esReferenciaInvalida() {
        return "23503".equals(codigoSql);
    }

    private static String extraerCodigo(Throwable causa) {
        if (causa instanceof java.sql.SQLException sql) {
            return sql.getSQLState();
        }
        return null;
    }
}