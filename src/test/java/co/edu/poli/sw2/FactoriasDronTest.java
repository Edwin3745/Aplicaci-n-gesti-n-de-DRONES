package co.edu.poli.sw2;

import co.edu.poli.sw2.Modelo.Agricultura;
import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.TipoDron;
import co.edu.poli.sw2.Modelo.Vigilancia;
import co.edu.poli.sw2.servicios.AgriculturaFactory;
import co.edu.poli.sw2.servicios.VigilanciaFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del patrón Factoría, ahora repartido en una fábrica por subtipo.
 *
 * <p>Verifican que cada fábrica construya exactamente su subclase, que asigne
 * los atributos comunes y los propios del subtipo, y que los objetos
 * resultantes respondan polimórficamente al recorrerlos como {@link Dron}.</p>
 */
class FactoriasDronTest {

    @Test
    void agriculturaFactory_debeDevolverInstanciaDeAgricultura() {
        Agricultura dron = AgriculturaFactory.crearDron(0, "AGR-TEST",
                "Agras T40", "DJI", 38.0, 40.0);

        assertInstanceOf(Agricultura.class, dron,
                "La fábrica de agricultura solo puede construir drones de agricultura");
        assertEquals(TipoDron.AGRICULTURA, dron.getTipo(),
                "El objeto debe declarar su propio tipo, que es el que persiste el DAO");
        assertEquals(40.0, dron.getCapacidadTanque(),
                "La capacidad del tanque debe asignarse al subtipo Agricultura");
    }

    @Test
    void vigilanciaFactory_debeDevolverInstanciaDeVigilancia() {
        Vigilancia dron = VigilanciaFactory.crearDron(0, "VIG-TEST",
                "Matrice 30T", "DJI", 3.7, true);

        assertInstanceOf(Vigilancia.class, dron,
                "La fábrica de vigilancia solo puede construir drones de vigilancia");
        assertEquals(TipoDron.VIGILANCIA, dron.getTipo(),
                "El objeto debe declarar su propio tipo, que es el que persiste el DAO");
        assertTrue(dron.isDeteccionTermica(),
                "La detección térmica debe asignarse al subtipo Vigilancia");
    }

    @Test
    void ambasFactorias_debenAsignarLosAtributosComunes() {
        Dron agricola = AgriculturaFactory.crearDron(7, "AGR-042",
                "Agras T25", "XAG", 26.5, 20.0);

        assertEquals(7, agricola.getId());
        assertEquals("AGR-042", agricola.getSerial());
        assertEquals("Agras T25", agricola.getModelo());
        assertEquals("XAG", agricola.getFabricante());
        assertEquals(26.5, agricola.getPeso());

        Dron vigilante = VigilanciaFactory.crearDron(9, "VIG-009",
                "Anafi", "Parrot", 0.5, false);

        assertEquals(9, vigilante.getId());
        assertEquals("VIG-009", vigilante.getSerial());
        assertEquals("Anafi", vigilante.getModelo());
        assertEquals("Parrot", vigilante.getFabricante());
        assertEquals(0.5, vigilante.getPeso());
    }

    @Test
    void elTipoLoDeclaraElObjeto_noQuienLoConstruye() {
        // Al separar la fábrica en dos, ya no se pasa el tipo como parámetro:
        // cada subclase lo declara por sí misma. Esto es lo que permite que el
        // DAO escriba la columna discriminadora sin inspeccionar la clase.
        List<Dron> flota = List.of(
                AgriculturaFactory.crearDron(1, "AGR-001", "Agras T40", "DJI", 38.0, 40.0),
                VigilanciaFactory.crearDron(2, "VIG-001", "Matrice 30T", "DJI", 3.7, true));

        assertEquals("agricultura", flota.get(0).getTipo().getCodigo());
        assertEquals("vigilancia", flota.get(1).getTipo().getCodigo());
    }

    @Test
    void dronesCreados_debenResponderPolimorficamente() {
        List<Dron> flota = new ArrayList<>();
        flota.add(AgriculturaFactory.crearDron(1, "AGR-001", "Agras T40", "DJI", 38.0, 40.0));
        flota.add(VigilanciaFactory.crearDron(2, "VIG-001", "Matrice 30T", "DJI", 3.7, true));
        flota.add(VigilanciaFactory.crearDron(3, "VIG-002", "Anafi", "Parrot", 0.5, false));

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
        Dron original = VigilanciaFactory.crearDron(5, "VIG-005",
                "Anafi", "Parrot", 0.5, false);
        Dron copia = VigilanciaFactory.crearDron(5, "VIG-005",
                "Anafi", "Parrot", 0.5, false);

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
