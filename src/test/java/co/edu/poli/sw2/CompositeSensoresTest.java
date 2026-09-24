package co.edu.poli.sw2;

import co.edu.poli.sw2.modelo.Sensor;
import co.edu.poli.sw2.servicios.composite.SensorComponente;
import co.edu.poli.sw2.servicios.composite.SensorCompuesto;
import co.edu.poli.sw2.servicios.composite.SensorHoja;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del patrón Composite aplicado a la jerarquía de sensores.
 *
 * <p>No tocan la base de datos: comprueban la estructura del árbol y el
 * comportamiento recursivo de las operaciones, que es lo que el patrón
 * aporta.</p>
 */
class CompositeSensoresTest {

    // ------------------------------------------------------------------
    // Hojas
    // ------------------------------------------------------------------

    @Test
    void hoja_debeContarseComoUnSensor() {
        SensorComponente hoja = new SensorHoja("RTD");

        assertEquals(1, hoja.contarSensores(),
                "Una hoja representa siempre un único sensor");
        assertEquals("RTD", hoja.getNombre());
    }

    @Test
    void hoja_puedeEnvolverUnSensorDelModelo() {
        Sensor sensor = new Sensor(1, "Térmico", "FLIR");
        SensorHoja hoja = new SensorHoja("Sensor Infrarrojo", sensor);

        assertSame(sensor, hoja.getSensor(),
                "La hoja debe conservar el sensor del modelo que envuelve");
        assertTrue(hoja.mostrarInfo().contains("FLIR"),
                "Al envolver un sensor, la descripción debe incluir su fabricante");
    }

    @Test
    void hoja_conNombreVacio_debeRechazarse() {
        assertThrows(IllegalArgumentException.class, () -> new SensorHoja("  "),
                "Un sensor sin nombre no identificaría nada en la jerarquía");
        assertThrows(IllegalArgumentException.class, () -> new SensorHoja(null));
    }

    // ------------------------------------------------------------------
    // Grupos
    // ------------------------------------------------------------------

    @Test
    void grupoVacio_debeContarCero() {
        SensorComponente grupo = new SensorCompuesto("Sensor Temperatura");

        assertEquals(0, grupo.contarSensores(),
                "Un grupo sin hijos no contiene ningún sensor");
    }

    @Test
    void grupo_debeSumarSusHijosDirectos() {
        SensorCompuesto temperatura = new SensorCompuesto("Sensor Temperatura");
        temperatura.agregar(new SensorHoja("Sensor Infrarrojo"));
        temperatura.agregar(new SensorHoja("RTD"));

        assertEquals(2, temperatura.contarSensores());
        assertEquals(2, temperatura.getHijos().size());
    }

    @Test
    void grupo_conNombreVacio_debeRechazarse() {
        assertThrows(IllegalArgumentException.class, () -> new SensorCompuesto(""));
    }

    @Test
    void agregar_devuelveMensajeDescribiendoLaOperacion() {
        SensorCompuesto grupo = new SensorCompuesto("Sensor Cámara");
        String mensaje = grupo.agregar(new SensorHoja("Sensor CMOS"));

        assertTrue(mensaje.contains("Sensor CMOS") && mensaje.contains("Sensor Cámara"),
                "El mensaje debe nombrar qué se agregó y dónde");
    }

    @Test
    void agregar_nulo_debeRechazarse() {
        SensorCompuesto grupo = new SensorCompuesto("Sensor Cámara");

        assertThrows(IllegalArgumentException.class, () -> grupo.agregar(null));
    }

    @Test
    void grupo_noPuedeContenerseASiMismo() {
        SensorCompuesto grupo = new SensorCompuesto("Sensor General");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> grupo.agregar(grupo),
                "Un grupo que se contiene a sí mismo provocaría recursión infinita");

        assertTrue(error.getMessage().toLowerCase().contains("ciclo"),
                "El mensaje debe explicar por qué no se permite");
    }

    @Test
    void remover_debeRetirarElElementoIndicado() {
        SensorCompuesto grupo = new SensorCompuesto("Sensor Sonido");
        SensorComponente analogico = new SensorHoja("Sensor Analógico");
        grupo.agregar(analogico);

        assertEquals(1, grupo.contarSensores());
        grupo.remover(analogico);
        assertEquals(0, grupo.contarSensores(),
                "Tras retirarlo, el recuento debe reflejar el cambio");
    }

    @Test
    void remover_loQueNoEsta_noFalla() {
        SensorCompuesto grupo = new SensorCompuesto("Sensor Sonido");
        String mensaje = grupo.remover(new SensorHoja("Sensor Ajeno"));

        assertTrue(mensaje.contains("no contiene"),
                "Retirar algo que no está debe informarlo, no lanzar excepción");
    }

    @Test
    void hijos_debeSerUnaVistaNoModificable() {
        SensorCompuesto grupo = new SensorCompuesto("Sensor Cámara");
        grupo.agregar(new SensorHoja("Sensor CCD"));

        assertThrows(UnsupportedOperationException.class,
                () -> grupo.getHijos().add(new SensorHoja("Intruso")),
                "La jerarquía solo debe alterarse mediante agregar() y remover()");
    }

    // ------------------------------------------------------------------
    // Recursión: lo que el patrón aporta
    // ------------------------------------------------------------------

    @Test
    void grupoDentroDeGrupo_debeContarseEnProfundidad() {
        SensorCompuesto digital = new SensorCompuesto("Sensor Digital");
        digital.agregar(new SensorHoja("SPI"));
        digital.agregar(new SensorHoja("UART"));

        SensorCompuesto sonido = new SensorCompuesto("Sensor Sonido");
        sonido.agregar(new SensorHoja("Sensor Analógico"));
        sonido.agregar(digital);

        assertEquals(2, sonido.getHijos().size(),
                "Sonido tiene dos hijos directos: una hoja y un grupo");
        assertEquals(3, sonido.contarSensores(),
                "Pero contiene tres sensores: el recuento baja hasta las hojas");
    }

    @Test
    void grupoYHoja_seAgreganConElMismoMetodo() {
        SensorCompuesto general = new SensorCompuesto("Sensor General");

        // La uniformidad es el propósito del patrón: el mismo método acepta
        // los dos tipos de nodo sin que quien lo llama tenga que distinguirlos.
        assertDoesNotThrow(() -> {
            general.agregar(new SensorCompuesto("Sensor Temperatura"));
            general.agregar(new SensorHoja("Sensor Inteligente"));
        });

        assertEquals(2, general.getHijos().size());
    }

        @Test
    void arbolCompleto_debeContarSieteSensores() {
        SensorComponente raiz = construirArbolDeLaEntrega();

        assertEquals(7, raiz.contarSensores(),
                "El árbol de la especificación tiene siete sensores individuales");
    }

    @Test
    void arbolCompleto_debeIndentarSegunLaProfundidad() {
        String arbol = construirArbolDeLaEntrega().mostrarInfo();

        assertTrue(arbol.contains("+ Sensor General"),
                "La raíz aparece sin sangría");
        assertTrue(arbol.contains("   + Sensor Temperatura"),
                "Un hijo de la raíz lleva un nivel de sangría");
        assertTrue(arbol.contains("      - Sensor Infrarrojo"),
                "Un nieto lleva dos niveles");
        assertTrue(arbol.contains("         - SPI"),
                "Un bisnieto lleva tres: la sangría refleja la profundidad real");
    }

    @Test
    void arbolCompleto_debeRecorrerseConUnaSolaLlamada() {
        String arbol = construirArbolDeLaEntrega().mostrarInfo();

        // Ninguna de estas comprobaciones exige recorrer el árbol desde fuera:
        // una única llamada a la raíz produce la representación completa.
        for (String nombre : new String[]{
                "Sensor General", "Sensor Temperatura", "Sensor Infrarrojo", "RTD",
                "Sensor Cámara", "Sensor CMOS", "Sensor CCD", "Sensor Sonido",
                "Sensor Analógico", "Sensor Digital", "SPI", "UART",
                "Sensor Inteligente"}) {

            assertTrue(arbol.contains(nombre),
                    "El recorrido debe alcanzar '" + nombre + "'");
        }
    }

    /**
     * Construye la jerarquía que la entrega exige mostrar.
     *
     * @return raíz del árbol de sensores.
     */
        private SensorComponente construirArbolDeLaEntrega() {
        SensorCompuesto general = new SensorCompuesto("Sensor General");

        SensorCompuesto rtd = new SensorCompuesto("RTD");
        rtd.agregar(new SensorHoja("Sensor Inteligente"));

        SensorCompuesto temperatura = new SensorCompuesto("Sensor Temperatura");
        temperatura.agregar(new SensorHoja("Sensor Infrarrojo"));
        temperatura.agregar(rtd);

        SensorCompuesto camara = new SensorCompuesto("Sensor Cámara");
        camara.agregar(new SensorHoja("Sensor CMOS"));
        camara.agregar(new SensorHoja("Sensor CCD"));

        SensorCompuesto digital = new SensorCompuesto("Sensor Digital");
        digital.agregar(new SensorHoja("SPI"));
        digital.agregar(new SensorHoja("UART"));

        SensorCompuesto sonido = new SensorCompuesto("Sensor Sonido");
        sonido.agregar(new SensorHoja("Sensor Analógico"));
        sonido.agregar(digital);

        general.agregar(temperatura);
        general.agregar(camara);
        general.agregar(sonido);

        return general;
    }
}