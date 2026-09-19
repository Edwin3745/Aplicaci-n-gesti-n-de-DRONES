package co.edu.poli.sw2.servicios;

import co.edu.poli.sw2.servicios.adapter.ExportableJson;

/**
 * Exporta a archivo cualquier objeto que sepa presentarse como JSON.
 *
 * <p>Es el cliente del patrón Adapter: solo conoce {@link ExportableJson}. No
 * sabe si detrás hay una misión u otra cosa, y por eso no depende del modelo.</p>
 */
public class ExportadorDeArchivos {

    /**
     * Exporta el documento a la ruta indicada.
     *
     * @param doc  documento a exportar; no puede ser nulo.
     * @param ruta ruta del archivo de destino.
     * @throws IllegalArgumentException si el documento es nulo.
     */
    public void exportar(ExportableJson doc, String ruta) {
        if (doc == null) {
            throw new IllegalArgumentException("El documento a exportar no puede ser nulo.");
        }
        doc.guardarEn(ruta);
    }
}