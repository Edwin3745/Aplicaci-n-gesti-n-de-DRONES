package co.edu.poli.sw2;

import co.edu.poli.sw2.Controlador.DronControlador;
import co.edu.poli.sw2.Controlador.OperacionFallidaException;
import co.edu.poli.sw2.Modelo.TipoDron;

/**
 * Verifica que los fallos técnicos lleguen traducidos a mensajes legibles.
 */
public class PruebaErrores {

    public static void main(String[] args) {

        DronControlador controlador = new DronControlador();

        System.out.println("--- Serial duplicado (AGR-001 ya existe) ---");
        intentar(() -> controlador.registrarDron(TipoDron.AGRICULTURA, "AGR-001",
                "Modelo X", "Fab Y", 20.0, 30.0, false));

        System.out.println("\n--- Serial vacio ---");
        intentar(() -> controlador.registrarDron(TipoDron.VIGILANCIA, "  ",
                "Modelo X", "Fab Y", 5.0, 0.0, true));

        System.out.println("\n--- Peso negativo ---");
        intentar(() -> controlador.registrarDron(TipoDron.VIGILANCIA, "VIG-999",
                "Modelo X", "Fab Y", -3.0, 0.0, true));

        System.out.println("\n--- Registro valido ---");
        intentar(() -> {
            controlador.registrarDron(TipoDron.AGRICULTURA, "AGR-TEST",
                    "Modelo OK", "Fab Y", 25.0, 35.0, false);
            System.out.println("  OK: dron registrado");
        });

        System.out.println("\n--- Total en la base: " + controlador.listarDrones().size() + " ---");
    }

    private static void intentar(Runnable accion) {
        try {
            accion.run();
        } catch (OperacionFallidaException e) {
            System.out.println("  " + e.getMessage());
        }
    }
}