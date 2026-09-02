package co.edu.poli.sw2;

import co.edu.poli.sw2.Modelo.Agricultura;
import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.Piloto;
import co.edu.poli.sw2.Modelo.Sensor;
import co.edu.poli.sw2.Modelo.Vigilancia;
import co.edu.poli.sw2.servicios.AgriculturaFactory;
import co.edu.poli.sw2.servicios.DronPrototypeManager;
import co.edu.poli.sw2.servicios.VigilanciaFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del patrón Prototype.
 *
 * <p>La copia la realiza {@link DronPrototypeManager}, en la capa de servicios:
 * el modelo no participa en el patrón. Lo que se verifica no es que la copia
 * exista, sino que sea de verdad independiente, empezando por la lista de
 * sensores, que es donde una copia superficial se delata.</p>
 */
class DronPrototypeTest {

    private DronPrototypeManager manager;

    @BeforeEach
    void prepararManager() {
        manager = new DronPrototypeManager();
    }

    // ------------------------------------------------------------------
    // El modelo permanece ajeno al patrón
    // ------------------------------------------------------------------

    @Test
    void elModelo_noDebeConocerElPatronPrototype() throws Exception {
        // El requisito es explícito: la lógica de copia vive en servicios, no
        // en el dominio. Esta prueba falla si alguien vuelve a añadir copiar()
        // o un constructor copia a las clases del modelo.
        assertThrows(NoSuchMethodException.class,
                () -> Dron.class.getMethod("copiar"),
                "Dron no debe exponer un método copiar()");
        assertThrows(NoSuchMethodException.class,
                () -> Agricultura.class.getDeclaredConstructor(Agricultura.class),
                "Agricultura no debe tener constructor copia");
        assertThrows(NoSuchMethodException.class,
                () -> Vigilancia.class.getDeclaredConstructor(Vigilancia.class),
                "Vigilancia no debe tener constructor copia");
        assertThrows(NoSuchMethodException.class,
                () -> Sensor.class.getDeclaredConstructor(Sensor.class),
                "Sensor no debe tener constructor copia");

        assertEquals(0, Dron.class.getInterfaces().length,
                "Dron no debe implementar ninguna interfaz de clonación");
    }

    // ------------------------------------------------------------------
    // La copia conserva el subtipo y sus datos
    // ------------------------------------------------------------------

    @Test
    void clonar_debeDevolverLaMismaClaseConcreta() {
        Agricultura agricola = AgriculturaFactory.crearDron(1, "AGR-P01",
                "Agras T40", "DJI", 38.0, 40.0);
        Vigilancia vigilante = VigilanciaFactory.crearDron(2, "VIG-P01",
                "Matrice 30T", "DJI", 3.7, true);

        assertInstanceOf(Agricultura.class, manager.clonar(agricola),
                "Clonar un dron de agricultura debe producir otro de agricultura");
        assertInstanceOf(Vigilancia.class, manager.clonar(vigilante),
                "Clonar un dron de vigilancia debe producir otro de vigilancia");
    }

    @Test
    void clonar_debeConservarLosDatosYLosAtributosDelSubtipo() {
        Agricultura original = AgriculturaFactory.crearDron(1, "AGR-P02",
                "Agras T40", "DJI", 38.0, 40.0);

        Dron copia = manager.clonar(original);

        assertEquals("AGR-P02", copia.getSerial());
        assertEquals("Agras T40", copia.getModelo());
        assertEquals("DJI", copia.getFabricante());
        assertEquals(38.0, copia.getPeso());
        assertEquals(40.0, ((Agricultura) copia).getCapacidadTanque(),
                "El atributo propio del subtipo debe viajar en la copia");
    }

    @Test
    void clonar_noDebeArrastrarElIdDelOriginal() {
        Vigilancia original = VigilanciaFactory.crearDron(77, "VIG-P02",
                "Anafi", "Parrot", 0.5, false);

        Dron copia = manager.clonar(original);

        assertEquals(77, original.getId(), "El original conserva su id");
        assertEquals(0, copia.getId(),
                "La copia todavía no existe en la base de datos: su id debe ser 0, "
                + "o una actualización sobre ella sobrescribiría la fila del original");
    }

    @Test
    void clonar_noDebeRobarleElPilotoAlOriginal() {
        Vigilancia original = VigilanciaFactory.crearDron(5, "VIG-P03",
                "Anafi", "Parrot", 0.5, true);
        Piloto piloto = new Piloto(1, "Ana Restrepo", "LIC-0001", "3001234567");
        piloto.asignarDron(original);

        Dron copia = manager.clonar(original);

        assertFalse(copia.tienePilotoAsignado(),
                "La copia debe nacer sin piloto: la columna piloto_id es única");
        assertSame(original, piloto.getDron(),
                "El piloto debe seguir asignado al dron original");
        assertTrue(original.tienePilotoAsignado(),
                "Clonar no puede alterar la relación del original");
    }

    // ------------------------------------------------------------------
    // Independencia real: la copia no comparte estado con el original
    // ------------------------------------------------------------------

    @Test
    void clonar_debeProducirUnObjetoDistintoEnMemoria() {
        Agricultura original = AgriculturaFactory.crearDron(1, "AGR-P06",
                "Agras T40", "DJI", 38.0, 40.0);

        Dron copia = manager.clonar(original);

        assertNotSame(original, copia,
                "El clon es otro objeto: original == copia debe ser false");
        assertNotEquals(System.identityHashCode(original), System.identityHashCode(copia),
                "Las identidades en memoria deben ser distintas");
    }

    @Test
    void modificarLaListaDeSensoresDelClon_noDebeAfectarAlOriginal() {
        Agricultura original = AgriculturaFactory.crearDron(1, "AGR-P03",
                "Agras T40", "DJI", 38.0, 40.0);
        original.agregarSensor(new Sensor(1, "multiespectral", "MicaSense"));

        Dron copia = manager.clonar(original);
        copia.agregarSensor(new Sensor(2, "LiDAR", "Velodyne"));

        assertEquals(1, original.getSensores().size(),
                "Agregar un sensor al clon no puede agregárselo al original: "
                + "si esto falla, la copia comparte la lista en vez de duplicarla");
        assertEquals(2, copia.getSensores().size(),
                "El clon debe conservar el sensor heredado y el nuevo");
    }

    @Test
    void modificarUnSensorDelClon_noDebeAfectarAlDelOriginal() {
        Agricultura original = AgriculturaFactory.crearDron(1, "AGR-P04",
                "Agras T40", "DJI", 38.0, 40.0);
        original.agregarSensor(new Sensor(1, "multiespectral", "MicaSense"));

        Dron copia = manager.clonar(original);
        copia.getSensores().get(0).setFabricante("OtroFabricante");

        assertEquals("MicaSense", original.getSensores().get(0).getFabricante(),
                "Los sensores también se duplican: un sensor es una pieza física "
                + "montada en un dron concreto, no algo que dos drones compartan");
        assertNotSame(original.getSensores().get(0), copia.getSensores().get(0),
                "Cada sensor de la copia debe ser un objeto nuevo");
    }

    @Test
    void modificarLosAtributosDelClon_noDebeAfectarAlOriginal() {
        Agricultura original = AgriculturaFactory.crearDron(1, "AGR-P05",
                "Agras T40", "DJI", 38.0, 40.0);

        Dron copia = manager.clonar(original);
        copia.setSerial("AGR-NUEVO");
        copia.setModelo("Agras T50");
        copia.setPeso(45.0);
        ((Agricultura) copia).setCapacidadTanque(50.0);

        assertEquals("AGR-P05", original.getSerial());
        assertEquals("Agras T40", original.getModelo());
        assertEquals(38.0, original.getPeso());
        assertEquals(40.0, original.getCapacidadTanque());
    }

    @Test
    void clonar_conDronNulo_debeFallar() {
        assertThrows(IllegalArgumentException.class, () -> manager.clonar(null),
                "No se puede copiar un dron inexistente");
    }

    // ------------------------------------------------------------------
    // Registro de prototipos
    // ------------------------------------------------------------------

    @Test
    void obtenerClon_debeDevolverUnObjetoDistintoDelPrototipo() {
        manager.registrar("fumigador estándar", AgriculturaFactory.crearDron(
                0, "AGR-BASE", "Agras T40", "DJI", 38.0, 40.0));

        Dron primero = manager.obtenerClon("fumigador estándar");
        Dron segundo = manager.obtenerClon("fumigador estándar");

        assertNotSame(primero, segundo,
                "Cada petición debe entregar un objeto nuevo");
        assertEquals("AGR-BASE", primero.getSerial());
        assertEquals("AGR-BASE", segundo.getSerial());
    }

    @Test
    void modificarUnClon_noDebeContaminarElPrototipoRegistrado() {
        manager.registrar("vigilancia nocturna", VigilanciaFactory.crearDron(
                0, "VIG-BASE", "Matrice 30T", "DJI", 3.7, true));

        Dron clon = manager.obtenerClon("vigilancia nocturna");
        clon.setModelo("Modelo alterado");
        clon.agregarSensor(new Sensor(1, "térmico", "FLIR"));

        Dron nuevoClon = manager.obtenerClon("vigilancia nocturna");

        assertEquals("Matrice 30T", nuevoClon.getModelo(),
                "El prototipo guardado no puede cambiar porque alguien altere un clon");
        assertTrue(nuevoClon.getSensores().isEmpty(),
                "El prototipo guardado no puede heredar los sensores de un clon");
    }

    @Test
    void registrar_debeGuardarUnaCopiaYNoElObjetoRecibido() {
        Agricultura base = AgriculturaFactory.crearDron(
                0, "AGR-BASE", "Agras T40", "DJI", 38.0, 40.0);

        manager.registrar("fumigador estándar", base);
        // Quien registró la plantilla sigue usando su objeto y lo modifica.
        base.setModelo("Modelo cambiado después de registrar");

        assertEquals("Agras T40", manager.obtenerClon("fumigador estándar").getModelo(),
                "La plantilla debe ser inmune a los cambios posteriores del objeto original");
    }

    @Test
    void obtenerClon_conClaveDesconocida_debeFallarConMensajeClaro() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> manager.obtenerClon("no existe"));

        assertTrue(error.getMessage().contains("no existe"),
                "El mensaje debe nombrar la clave que se buscaba");
    }

    @Test
    void eliminar_debeQuitarLaPlantillaDelRegistro() {
        manager.registrar("temporal", VigilanciaFactory.crearDron(
                0, "VIG-TMP", "Anafi", "Parrot", 0.5, false));

        assertTrue(manager.contiene("temporal"));
        assertTrue(manager.eliminar("temporal"), "Eliminar una plantilla existente");
        assertFalse(manager.contiene("temporal"));
        assertFalse(manager.eliminar("temporal"),
                "Eliminar dos veces la misma plantilla debe devolver false");
    }

    @Test
    void registrar_conDatosInvalidos_debeFallar() {
        Agricultura base = AgriculturaFactory.crearDron(
                0, "AGR-BASE", "Agras T40", "DJI", 38.0, 40.0);

        assertThrows(IllegalArgumentException.class,
                () -> manager.registrar("  ", base),
                "Una clave en blanco no permite recuperar la plantilla");
        assertThrows(IllegalArgumentException.class,
                () -> manager.registrar("sin dron", null),
                "No se puede registrar un prototipo nulo");
    }

    @Test
    void nombresRegistrados_debeConservarElOrdenDeRegistro() {
        manager.registrar("fumigador estándar", AgriculturaFactory.crearDron(
                0, "AGR-BASE", "Agras T40", "DJI", 38.0, 40.0));
        manager.registrar("vigilancia nocturna", VigilanciaFactory.crearDron(
                0, "VIG-BASE", "Matrice 30T", "DJI", 3.7, true));

        assertIterableEquals(
                java.util.List.of("fumigador estándar", "vigilancia nocturna"),
                manager.nombresRegistrados(),
                "La vista muestra las plantillas en el orden en que se registraron");
    }
}
