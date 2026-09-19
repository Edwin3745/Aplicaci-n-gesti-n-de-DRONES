package co.edu.poli.sw2;

import co.edu.poli.sw2.modelo.Agricultura;
import co.edu.poli.sw2.modelo.Mision;
import co.edu.poli.sw2.servicios.ExportadorDeArchivos;
import co.edu.poli.sw2.servicios.adapter.ExportableJson;
import co.edu.poli.sw2.servicios.adapter.MisionJsonAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class MisionJsonAdapterTest {

    private Mision crearMision(String nombre) {
        Mision mision = new Mision(1, nombre, "Lote 4",
                new GregorianCalendar(2026, Calendar.SEPTEMBER, 19).getTime());
        mision.agregarDron(new Agricultura(7, "AGR-001", "Agras T40", "DJI", 38.0, 40.0));
        return mision;
    }

    @Test
    void toJson_incluyeLosDatosDeLaMisionYSusDrones() {
        String json = new MisionJsonAdapter(crearMision("Fumigación norte")).toJson();

        assertTrue(json.contains("\"nombre\": \"Fumigación norte\""));
        assertTrue(json.contains("\"fecha\": \"2026-09-19\""));
        assertTrue(json.contains("\"serial\": \"AGR-001\""));
        assertTrue(json.contains("\"tipo\": \"agricultura\""));
    }

    @Test
    void toJson_escapaLasComillasDelTexto() {
        String json = new MisionJsonAdapter(crearMision("Lote \"norte\"")).toJson();

        assertTrue(json.contains("\"nombre\": \"Lote \\\"norte\\\"\""));
    }

    @Test
    void guardarEn_escribeElArchivo(@TempDir Path carpeta) throws Exception {
        Path destino = carpeta.resolve("mision.json");
        ExportableJson doc = new MisionJsonAdapter(crearMision("Fumigación norte"));

        new ExportadorDeArchivos().exportar(doc, destino.toString());

        assertEquals(doc.toJson(), Files.readString(destino));
    }

    @Test
    void guardarEn_conRutaVacia_lanzaExcepcion() {
        ExportableJson doc = new MisionJsonAdapter(crearMision("X"));

        assertThrows(IllegalArgumentException.class, () -> doc.guardarEn("  "));
    }

    @Test
    void constructor_conMisionNula_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new MisionJsonAdapter(null));
    }
}