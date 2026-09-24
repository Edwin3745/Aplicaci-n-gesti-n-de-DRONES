package co.edu.poli.sw2;

import co.edu.poli.sw2.Controlador.DronControlador;
import co.edu.poli.sw2.Controlador.OperacionFallidaException;
import co.edu.poli.sw2.modelo.Dron;
import co.edu.poli.sw2.modelo.TipoDron;
import co.edu.poli.sw2.servicios.builder.DronBuilder;
import co.edu.poli.sw2.servicios.dao.GenericDAO;
import co.edu.poli.sw2.servicios.proxy.DronServicioProxy;
import co.edu.poli.sw2.servicios.proxy.DronServicioReal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del patrón Proxy: la eliminación de un dron solo procede cuando la
 * contraseña ingresada es correcta.
 *
 * <p>Usan un DAO en memoria que cuenta las llamadas a {@code eliminar}, por lo
 * que no requieren base de datos y permiten comprobar que, con una contraseña
 * inválida, el objeto real ni siquiera es invocado.</p>
 */
class DronServicioProxyTest {

    private static final String CLAVE = "secreta";

    private EspiaDAO dao;
    private DronServicioProxy proxy;
    private Dron dron;

    /**
     * Prepara un DAO en memoria con un dron guardado y un proxy configurado
     * con la contraseña de prueba.
     */
    @BeforeEach
    void preparar() {
        dao = new EspiaDAO();
        proxy = new DronServicioProxy(new DronServicioReal(dao), CLAVE);

        dron = new DronBuilder()
                .conTipo(TipoDron.AGRICULTURA).conSerial("PX-1").conModelo("M")
                .conFabricante("F").conPeso(10.0).conCapacidadTanque(20.0).build();
        dao.guardar(dron);
    }

    /** Con la contraseña correcta el dron debe eliminarse. */
    @Test
    void eliminar_conContrasenaCorrecta_debeBorrarElDron() {
        proxy.setContrasenaIngresada(CLAVE);

        assertTrue(proxy.eliminar(dron.getId()));
        assertNull(dao.buscarPorId(dron.getId()));
    }

    /** Con una contraseña incorrecta no se elimina y el DAO no se toca. */
    @Test
    void eliminar_conContrasenaIncorrecta_noDebeBorrarNiTocarElDao() {
        proxy.setContrasenaIngresada("mala");

        assertFalse(proxy.eliminar(dron.getId()));
        assertNotNull(dao.buscarPorId(dron.getId()), "El dron debe seguir existiendo");
        assertEquals(0, dao.llamadasAEliminar, "El DAO ni siquiera debe ser invocado");
    }

    /** Sin contraseña, o con solo espacios, la eliminación se rechaza. */
    @Test
    void eliminar_sinContrasena_debeSerRechazado() {
        assertFalse(proxy.eliminar(dron.getId()));

        proxy.setContrasenaIngresada("  ");
        assertFalse(proxy.eliminar(dron.getId()));

        assertNotNull(dao.buscarPorId(dron.getId()));
    }

    /** La contraseña vale para un solo intento: hay que ingresarla de nuevo. */
    @Test
    void laContrasenaSirveParaUnSoloIntento() {
        proxy.setContrasenaIngresada(CLAVE);
        assertTrue(proxy.eliminar(dron.getId()));

        // Sin volver a ingresarla, el siguiente intento se rechaza.
        assertFalse(proxy.eliminar(dron.getId()));
    }

    /**
     * Desde el controlador, una contraseña inválida produce un mensaje para el
     * usuario y el dron no se elimina; con la contraseña por defecto sí.
     */
    @Test
    void controlador_debeMostrarMensajeCuandoLaContrasenaNoEsValida() {
        DronControlador controlador = new DronControlador(dao);
        controlador.registrarDron(TipoDron.AGRICULTURA, "PX-2", "M", "F", 10.0, 20.0, false);
        List<Dron> todos = controlador.listarDrones();
        int id = todos.get(todos.size() - 1).getId();

        OperacionFallidaException ex = assertThrows(OperacionFallidaException.class,
                () -> controlador.eliminarDron(id, "incorrecta"));
        assertTrue(ex.getMessage().toLowerCase().contains("contraseña"));
        assertNotNull(controlador.buscarDron(id), "El dron no debe haberse eliminado");

        assertTrue(controlador.eliminarDron(id, DronServicioProxy.CONTRASENA_POR_DEFECTO));
        assertNull(controlador.buscarDron(id));
    }

    /** DAO en memoria que cuenta cuántas veces se le pidió eliminar. */
    private static class EspiaDAO implements GenericDAO<Dron, Integer> {
        private final Map<Integer, Dron> drones = new HashMap<>();
        private int siguienteId = 1;
        int llamadasAEliminar = 0;

        @Override public void guardar(Dron d) { d.setId(siguienteId++); drones.put(d.getId(), d); }
        @Override public boolean eliminar(Integer id) { llamadasAEliminar++; return drones.remove(id) != null; }
        @Override public Dron buscarPorId(Integer id) { return drones.get(id); }
        @Override public List<Dron> listarTodos() { return new ArrayList<>(drones.values()); }
        @Override public boolean actualizar(Dron d) { return drones.containsKey(d.getId()); }
    }
}