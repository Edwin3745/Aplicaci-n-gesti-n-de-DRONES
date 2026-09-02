package co.edu.poli.sw2;

import co.edu.poli.sw2.Modelo.Agricultura;
import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.Sensor;
import co.edu.poli.sw2.servicios.AgriculturaFactory;
import co.edu.poli.sw2.servicios.DronPrototypeManager;
import co.edu.poli.sw2.servicios.InformeDeIdentidad;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del informe que la interfaz muestra como evidencia de los patrones.
 *
 * <p>Comprueban el contenido del texto sin necesidad de levantar JavaFX, que es
 * la razón de que la redacción viva en la capa de servicios y no en la vista.</p>
 */
class InformeDeIdentidadTest {

    private final DronPrototypeManager manager = new DronPrototypeManager();

    private Agricultura dronDePrueba() {
        return AgriculturaFactory.crearDron(7, "AGR-INF", "Agras T40", "DJI", 38.0, 40.0);
    }

    @Test
    void informe_debeMostrarLasIdentidadesEnHexadecimal() {
        Dron original = dronDePrueba();
        Dron copia = manager.clonar(original);

        String informe = InformeDeIdentidad.compararIdentidades(original, copia);

        String refOriginal = Integer.toHexString(System.identityHashCode(original));
        String refCopia = Integer.toHexString(System.identityHashCode(copia));

        assertTrue(informe.contains("Agricultura@" + refOriginal),
                "El informe debe mostrar la identidad real del original");
        assertTrue(informe.contains("Agricultura@" + refCopia),
                "El informe debe mostrar la identidad real de la copia");
        assertNotEquals(refOriginal, refCopia,
                "Original y copia deben tener identidades distintas");
    }

    @Test
    void informe_debeUsarIdentityHashCodeYNoElHashCodeSobrescrito() {
        // Dos drones distintos con el MISMO id: hashCode() los da iguales porque
        // está sobrescrito por id de negocio, pero identityHashCode no.
        Dron uno = AgriculturaFactory.crearDron(5, "A", "M", "F", 1.0, 2.0);
        Dron otro = AgriculturaFactory.crearDron(5, "A", "M", "F", 1.0, 2.0);

        assertEquals(uno.hashCode(), otro.hashCode(),
                "hashCode() está sobrescrito por id: no sirve como evidencia");

        String informe = InformeDeIdentidad.compararIdentidades(uno, otro);

        assertFalse(informe.contains("@" + Integer.toHexString(uno.hashCode())),
                "El informe no debe apoyarse en el hashCode sobrescrito");
        assertTrue(informe.contains("@" + Integer.toHexString(System.identityHashCode(uno))),
                "El informe debe apoyarse en System.identityHashCode");
    }

    @Test
    void informe_debeIncluirLasComparacionesPedidas() {
        Dron original = dronDePrueba();
        Dron copia = manager.clonar(original);

        String informe = InformeDeIdentidad.compararIdentidades(original, copia);

        assertTrue(informe.contains("original == copia   -> false"),
                "La comparación por referencia debe salir false y verse en el informe");
        assertTrue(informe.contains("original.equals()"),
                "El informe debe incluir el resultado de equals()");
        assertTrue(informe.contains("Sensores original"),
                "El informe debe incluir la línea de sensores del original");
        assertTrue(informe.contains("Sensores copia"),
                "El informe debe incluir la línea de sensores de la copia");
    }

    @Test
    void informe_debeAdvertirQueLaComparacionDeListasNoPruebaNada() {
        Dron original = dronDePrueba();
        Dron copia = manager.clonar(original);

        String informe = InformeDeIdentidad.compararIdentidades(original, copia);

        assertTrue(informe.contains("Atención"),
                "getSensores() envuelve la lista en cada llamada, y el informe debe "
                + "advertir de que esa comparación no demuestra nada por sí sola");
    }

    @Test
    void informe_debeDemostrarQueLaListaNoSeComparte() {
        Dron original = dronDePrueba();
        original.agregarSensor(new Sensor(1, "multiespectral", "MicaSense"));
        Dron copia = manager.clonar(original);

        String informe = InformeDeIdentidad.compararIdentidades(original, copia);

        assertTrue(informe.contains("la lista NO se comparte"),
                "La comprobación en vivo debe confirmar que la copia es profunda");
    }

    @Test
    void informe_noDebeDejarRastroEnLosDronesQueExamina() {
        Dron original = dronDePrueba();
        original.agregarSensor(new Sensor(1, "multiespectral", "MicaSense"));
        Dron copia = manager.clonar(original);

        InformeDeIdentidad.compararIdentidades(original, copia);

        assertEquals(1, original.getSensores().size(),
                "El informe nunca debe modificar el dron original del usuario");
        assertEquals(1, copia.getSensores().size(),
                "El sensor de prueba debe retirarse tras la comprobación");
    }

    @Test
    void informe_debeFuncionarConDronesSinSensores() {
        // Es el caso normal: el DAO no carga los sensores de la fila.
        Dron original = dronDePrueba();
        Dron copia = manager.clonar(original);

        String informe = InformeDeIdentidad.compararIdentidades(original, copia);

        assertTrue(informe.contains("la lista NO se comparte"),
                "La prueba de independencia debe funcionar aunque no haya sensores");
        assertTrue(original.getSensores().isEmpty());
        assertTrue(copia.getSensores().isEmpty());
    }

    @Test
    void describirConstruccion_debeMostrarLaSecuenciaYElObjeto() {
        Dron dron = dronDePrueba();

        String informe = InformeDeIdentidad.describirConstruccion(
                dron, "  new DronBuilder().conSerial(\"AGR-INF\").build();");

        assertTrue(informe.contains("PATRÓN BUILDER"),
                "El informe debe identificar el patrón que demuestra");
        assertTrue(informe.contains(".conSerial(\"AGR-INF\")"),
                "El informe debe reproducir la secuencia de llamadas ejecutada");
        assertTrue(informe.contains("co.edu.poli.sw2.modelo.Agricultura@"),
                "El informe debe mostrar la referencia del objeto resultante");
        assertTrue(informe.contains(dron.descripcionOperativa()),
                "El informe debe describir el dron construido");
    }
}
