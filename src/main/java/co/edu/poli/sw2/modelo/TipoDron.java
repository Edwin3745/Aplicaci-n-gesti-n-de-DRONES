package co.edu.poli.sw2.modelo;

/**
 * Enumera los tipos concretos de dron que maneja el sistema.
 *
 * Cada constante conoce el código textual con el que se persiste en la base
 * de datos (columna discriminadora), de modo que ese literal exista en un
 * único lugar del proyecto.
 */
public enum TipoDron {

    /** Dron destinado a labores agrícolas. */
    AGRICULTURA("agricultura"),

    /** Dron destinado a labores de vigilancia. */
    VIGILANCIA("vigilancia");

    /** Código con el que el tipo se guarda en la base de datos. */
    private final String codigo;

    /**
     * Asocia a cada constante su código de persistencia.
     *
     * @param codigo valor que se escribe en la columna discriminadora.
     */
    TipoDron(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Obtiene el código con el que este tipo se guarda en la base de datos.
     *
     * @return código persistido en la columna discriminadora.
     */
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