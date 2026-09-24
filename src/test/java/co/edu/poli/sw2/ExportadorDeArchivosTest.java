package co.edu.poli.sw2;

import co.edu.poli.sw2.servicios.adapter.ExportableJson;
import co.edu.poli.sw2.servicios.adapter.ExportadorDeArchivos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExportadorDeArchivosTest {

    @Test
    void exportar_delegaEnElDocumentoConLaRutaIndicada() {
        String[] rutaRecibida = new String[1];
        ExportableJson falso = new ExportableJson() {
            @Override public String toJson() { return "{}"; }
            @Override public void guardarEn(String ruta) { rutaRecibida[0] = ruta; }
        };

        new ExportadorDeArchivos().exportar(falso, "salida.json");

        assertEquals("salida.json", rutaRecibida[0]);
    }

    @Test
    void exportar_conDocumentoNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExportadorDeArchivos().exportar(null, "salida.json"));
    }
}