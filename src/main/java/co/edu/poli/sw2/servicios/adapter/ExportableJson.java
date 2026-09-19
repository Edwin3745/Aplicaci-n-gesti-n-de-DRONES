package co.edu.poli.sw2.servicios.adapter;

/**
 * Define lo que el sistema espera de cualquier objeto que se pueda exportar
 * como JSON.
 *
 * <p>Es el Target del patrón Adapter: el cliente trabaja únicamente contra
 * esta interfaz, sin saber qué clase hay detrás ni cómo se construye el
 * texto.</p>
 */
public interface ExportableJson {

    /**
     * Genera la representación JSON del objeto.
     *
     * @return texto JSON válido y listo para escribirse en un archivo.
     */
    String toJson();

    /**
     * Escribe la representación JSON en un archivo.
     *
     * @param ruta ruta del archivo de destino; se crea o se sobrescribe.
     * @throws IllegalArgumentException si la ruta es nula o está vacía.
     * @throws co.edu.poli.sw2.servicios.ServicioException si no se puede
     *         escribir el archivo.
     */
    void guardarEn(String ruta);
}