package co.edu.poli.sw2;

import co.edu.poli.sw2.Modelo.Agricultura;
import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.TipoDron;
import co.edu.poli.sw2.Modelo.Vigilancia;
import co.edu.poli.sw2.servicios.DronBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del patrón Builder.
 *
 * <p>Cubren las tres responsabilidades de la clase: producir cualquiera de los
 * dos subtipos, permitir el encadenamiento de llamadas, y rechazar los datos
 * incompletos o inadmisibles antes de construir nada.</p>
 */
class DronBuilderTest {

    // ------------------------------------------------------------------
    // Construcción de los dos subtipos
    // ------------------------------------------------------------------

    @Test
    void build_conTipoAgricultura_debeConstruirUnaAgricultura() {
        Dron dron = new DronBuilder()
                .conTipo(TipoDron.AGRICULTURA)
                .conSerial("AGR-B01")
                .conModelo("Agras T40")
                .conFabricante("DJI")
                .conPeso(38.0)
                .conCapacidadTanque(40.0)
                .build();

        assertInstanceOf(Agricultura.class, dron,
                "El builder debe producir la subclase que corresponde al tipo");
        assertEquals(40.0, ((Agricultura) dron).getCapacidadTanque(),
                "El atributo propio del subtipo debe llegar al objeto construido");
        assertEquals(TipoDron.AGRICULTURA, dron.getTipo());
    }

    @Test
    void build_conTipoVigilancia_debeConstruirUnaVigilancia() {
        Dron dron = new DronBuilder()
                .conTipo(TipoDron.VIGILANCIA)
                .conSerial("VIG-B01")
                .conModelo("Matrice 30T")
                .conFabricante("DJI")
                .conPeso(3.7)
                .conDeteccionTermica(true)
                .build();

        assertInstanceOf(Vigilancia.class, dron,
                "El builder debe producir la subclase que corresponde al tipo");
        assertTrue(((Vigilancia) dron).isDeteccionTermica(),
                "El atributo propio del subtipo debe llegar al objeto construido");
        assertEquals(TipoDron.VIGILANCIA, dron.getTipo());
    }

    // ------------------------------------------------------------------
    // Interfaz fluida
    // ------------------------------------------------------------------

    @Test
    void cadaSetter_debeDevolverElMismoBuilder() {
        DronBuilder builder = new DronBuilder();

        assertSame(builder, builder.conTipo(TipoDron.VIGILANCIA),
                "Cada método debe devolver this para poder encadenar");
        assertSame(builder, builder.conSerial("VIG-B02"));
        assertSame(builder, builder.conModelo("Anafi"));
        assertSame(builder, builder.conFabricante("Parrot"));
        assertSame(builder, builder.conPeso(0.5));
        assertSame(builder, builder.conId(3));
        assertSame(builder, builder.conCapacidadTanque(0.0));
        assertSame(builder, builder.conDeteccionTermica(false));
    }

    @Test
    void build_debeAsignarTodosLosAtributosComunes() {
        Dron dron = new DronBuilder()
                .conTipo(TipoDron.VIGILANCIA)
                .conId(42)
                .conSerial("VIG-B03")
                .conModelo("Anafi")
                .conFabricante("Parrot")
                .conPeso(0.5)
                .conDeteccionTermica(false)
                .build();

        assertEquals(42, dron.getId());
        assertEquals("VIG-B03", dron.getSerial());
        assertEquals("Anafi", dron.getModelo());
        assertEquals("Parrot", dron.getFabricante());
        assertEquals(0.5, dron.getPeso());
    }

    @Test
    void sinIndicarId_debeQuedarEnCeroParaQueLoGenereLaBaseDeDatos() {
        Dron dron = new DronBuilder()
                .conTipo(TipoDron.AGRICULTURA)
                .conSerial("AGR-B04")
                .conModelo("Agras T25")
                .conFabricante("XAG")
                .conPeso(26.5)
                .conCapacidadTanque(20.0)
                .build();

        assertEquals(0, dron.getId(),
                "En un alta el id debe ir en 0: lo asigna PostgreSQL");
    }

    @Test
    void elMismoBuilder_debePoderConstruirVariosDrones() {
        DronBuilder builder = new DronBuilder()
                .conTipo(TipoDron.AGRICULTURA)
                .conModelo("Agras T40")
                .conFabricante("DJI")
                .conPeso(38.0)
                .conCapacidadTanque(40.0);

        Dron primero = builder.conSerial("AGR-B05").build();
        Dron segundo = builder.conSerial("AGR-B06").build();

        assertNotSame(primero, segundo, "Cada build debe producir un objeto nuevo");
        assertEquals("AGR-B05", primero.getSerial());
        assertEquals("AGR-B06", segundo.getSerial());
        assertEquals("Agras T40", segundo.getModelo(),
                "Los datos que no se cambian deben conservarse entre construcciones");
    }

    // ------------------------------------------------------------------
    // Validaciones
    // ------------------------------------------------------------------

    @Test
    void build_sinTipo_debeFallar() {
        IllegalStateException error = assertThrows(IllegalStateException.class, () ->
                new DronBuilder()
                        .conSerial("X-001")
                        .conModelo("Modelo")
                        .conFabricante("Fabricante")
                        .conPeso(10.0)
                        .build());

        assertTrue(error.getMessage().toLowerCase().contains("tipo"),
                "El mensaje debe decir qué dato falta");
    }

    @Test
    void build_sinSerial_debeFallar() {
        IllegalStateException error = assertThrows(IllegalStateException.class, () ->
                new DronBuilder()
                        .conTipo(TipoDron.VIGILANCIA)
                        .conModelo("Modelo")
                        .conFabricante("Fabricante")
                        .conPeso(10.0)
                        .build());

        assertTrue(error.getMessage().toLowerCase().contains("serial"),
                "El mensaje debe decir qué dato falta");
    }

    @Test
    void build_conSerialEnBlanco_debeFallar() {
        IllegalStateException error = assertThrows(IllegalStateException.class, () ->
                new DronBuilder()
                        .conTipo(TipoDron.VIGILANCIA)
                        .conSerial("   ")
                        .conModelo("Modelo")
                        .conFabricante("Fabricante")
                        .conPeso(10.0)
                        .build());

        assertTrue(error.getMessage().toLowerCase().contains("serial"),
                "Un texto de solo espacios no es un serial válido");
    }

    @Test
    void build_sinModelo_debeFallar() {
        IllegalStateException error = assertThrows(IllegalStateException.class, () ->
                new DronBuilder()
                        .conTipo(TipoDron.VIGILANCIA)
                        .conSerial("X-001")
                        .conFabricante("Fabricante")
                        .conPeso(10.0)
                        .build());

        assertTrue(error.getMessage().toLowerCase().contains("modelo"),
                "El mensaje debe decir qué dato falta");
    }

    @Test
    void build_sinFabricante_debeFallar() {
        IllegalStateException error = assertThrows(IllegalStateException.class, () ->
                new DronBuilder()
                        .conTipo(TipoDron.VIGILANCIA)
                        .conSerial("X-001")
                        .conModelo("Modelo")
                        .conPeso(10.0)
                        .build());

        assertTrue(error.getMessage().toLowerCase().contains("fabricante"),
                "El mensaje debe decir qué dato falta");
    }

    @Test
    void build_conPesoNegativo_debeFallar() {
        IllegalStateException error = assertThrows(IllegalStateException.class, () ->
                new DronBuilder()
                        .conTipo(TipoDron.VIGILANCIA)
                        .conSerial("X-001")
                        .conModelo("Modelo")
                        .conFabricante("Fabricante")
                        .conPeso(-1.0)
                        .build());

        assertTrue(error.getMessage().toLowerCase().contains("peso"),
                "El mensaje debe decir qué dato es inadmisible");
    }

    @Test
    void build_conCapacidadNegativaEnAgricultura_debeFallar() {
        IllegalStateException error = assertThrows(IllegalStateException.class, () ->
                new DronBuilder()
                        .conTipo(TipoDron.AGRICULTURA)
                        .conSerial("AGR-B07")
                        .conModelo("Agras T40")
                        .conFabricante("DJI")
                        .conPeso(38.0)
                        .conCapacidadTanque(-5.0)
                        .build());

        assertTrue(error.getMessage().toLowerCase().contains("tanque"),
                "El mensaje debe decir qué dato es inadmisible");
    }

    @Test
    void build_conDatosIncompletos_noDebeConstruirNadaAMedias() {
        DronBuilder builder = new DronBuilder()
                .conTipo(TipoDron.AGRICULTURA)
                .conSerial("AGR-B08")
                .conModelo("Agras T40")
                .conFabricante("DJI")
                .conPeso(-2.0)
                .conCapacidadTanque(40.0);

        assertThrows(IllegalStateException.class, builder::build,
                "La validación ocurre antes de instanciar");

        // Corregido el dato que fallaba, el mismo builder debe funcionar.
        Dron dron = builder.conPeso(38.0).build();
        assertNotNull(dron, "Tras corregir el dato, la construcción debe completarse");
        assertEquals(38.0, dron.getPeso());
    }
}
