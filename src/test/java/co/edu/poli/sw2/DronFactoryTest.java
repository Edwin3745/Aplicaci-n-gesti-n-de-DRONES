package co.edu.poli.sw2;

import co.edu.poli.sw2.Modelo.Agricultura;
import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.TipoDron;
import co.edu.poli.sw2.Modelo.Vigilancia;
import co.edu.poli.sw2.Servicio.DronFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del patrón Factoría.
 *
 * <p>Verifican que la fábrica devuelva la subclase concreta que corresponde al
 * tipo solicitado, que asigne correctamente los atributos específicos de cada
 * subtipo y que el comportamiento polimórfico funcione sobre la colección
 * resultante.</p>
 */
class DronFactoryTest {

    @Test
    void crearDron_conTipoAgricultura_debeDevolverInstanciaDeAgricultura() {
        Dron dron = DronFactory.crearDron(TipoDron.AGRICULTURA, 0, "AGR-TEST",
                "Agras T40", "DJI", 38.0, 40.0, false);

        assertInstanceOf(Agricultura.class, dron,
                "La fábrica debe construir la subclase que corresponde al tipo");
        assertEquals(TipoDron.AGRICULTURA, dron.getTipo());
        assertEquals(40.0, ((Agricultura) dron).getCapacidadTanque(),
                "La capacidad del tanque debe asignarse al subtipo Agricultura");
    }

    @Test
    void crearDron_conTipoVigilancia_debeDevolverInstanciaDeVigilancia() {
        Dron dron = DronFactory.crearDron(TipoDron.VIGILANCIA, 0, "VIG-TEST",
                "Matrice 30T", "DJI", 3.7, 0.0, true);

        assertInstanceOf(Vigilancia.class, dron,
                "La fábrica debe construir la subclase que corresponde al tipo");
        assertEquals(TipoDron.VIGILANCIA, dron.getTipo());
        assertTrue(((Vigilancia) dron).isDeteccionTermica(),
                "La detección térmica debe asignarse al subtipo Vigilancia");
    }

    @Test
    void crearDron_debeAsignarLosAtributosComunes() {
        Dron dron = DronFactory.crearDron(TipoDron.AGRICULTURA, 7, "AGR-042",
                "Agras T25", "XAG", 26.5, 20.0, false);

        assertEquals(7, dron.getId());
        assertEquals("AGR-042", dron.getSerial());
        assertEquals("Agras T25", dron.getModelo());
        assertEquals("XAG", dron.getFabricante());
        assertEquals(26.5, dron.getPeso());
    }

    @Test
    void crearDron_conTipoNulo_debeLanzarExcepcion() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> DronFactory.crearDron(null, 0, "X-001",
                        "Modelo", "Fabricante", 10.0, 0.0, false),
                "Un tipo nulo no permite decidir qué subclase construir");

        assertTrue(error.getMessage().contains("tipo"),
                "El mensaje debe indicar cuál es el dato inválido");
    }

    @Test
    void dronesCreados_debenResponderPolimorficamente() {
        List<Dron> flota = new ArrayList<>();
        flota.add(DronFactory.crearDron(TipoDron.AGRICULTURA, 1, "AGR-001",
                "Agras T40", "DJI", 38.0, 40.0, false));
        flota.add(DronFactory.crearDron(TipoDron.VIGILANCIA, 2, "VIG-001",
                "Matrice 30T", "DJI", 3.7, 0.0, true));
        flota.add(DronFactory.crearDron(TipoDron.VIGILANCIA, 3, "VIG-002",
                "Anafi", "Parrot", 0.5, 0.0, false));

        // El mismo método sobre la colección produce una respuesta distinta
        // según la clase real de cada objeto, sin necesidad de instanceof.
        List<String> descripciones = flota.stream()
                .map(Dron::descripcionOperativa)
                .toList();

        assertEquals(3, descripciones.stream().distinct().count(),
                "Cada dron debe describir su propia función operativa");
        assertTrue(descripciones.get(0).contains("40.0"),
                "El dron de agricultura debe mencionar su capacidad de tanque");
        assertTrue(descripciones.get(1).contains("térmica"),
                "El dron de vigilancia con cámara térmica debe indicarlo");
    }

    @Test
    void dronesConMismoId_debenConsiderarseElMismo() {
        Dron original = DronFactory.crearDron(TipoDron.VIGILANCIA, 5, "VIG-005",
                "Anafi", "Parrot", 0.5, 0.0, false);
        Dron copia = DronFactory.crearDron(TipoDron.VIGILANCIA, 5, "VIG-005",
                "Anafi", "Parrot", 0.5, 0.0, false);

        assertEquals(original, copia,
                "La identidad se determina por id, no por referencia en memoria");
        assertEquals(original.hashCode(), copia.hashCode(),
                "Objetos iguales deben tener el mismo hashCode");

        List<Dron> flota = new ArrayList<>();
        flota.add(original);
        assertTrue(flota.remove(copia),
                "Un objeto construido aparte debe poder localizarse en la colección");
    }
}