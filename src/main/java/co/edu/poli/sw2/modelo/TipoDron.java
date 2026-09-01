package co.edu.poli.sw2.modelo;

/**
 * Enumera los tipos concretos de dron que maneja el sistema.
 *
 * Cada constante conoce el código textual con el que se persiste en la base
 * de datos (columna discriminadora), de modo que ese literal exista en un
 * único lugar del proyecto.
 */
public enum TipoDron {

    AGRICULTURA("agricultura"),
    VIGILANCIA("vigilancia");

    private final String codigo;

    TipoDron(String codigo) {
        this.codigo = codigo;
    }

    /** @return código persistido en la columna discriminadora de la BD. */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Reconstruye el tipo a partir del código leído de la base de datos.
     *
     * @param codigo valor de la columna discriminadora
     * @return constante correspondiente
     * @throws IllegalArgumentException si el código no corresponde a ningún tipo
     */
    public static TipoDron desdeCodigo(String codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException("El código de tipo no puede ser nulo.");
        }
        for (TipoDron tipo : values()) {
            if (tipo.codigo.equalsIgnoreCase(codigo.trim())) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de dron desconocido: " + codigo);
    }
}