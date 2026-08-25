package co.edu.poli.sw2;

import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.Piloto;
import co.edu.poli.sw2.Modelo.TipoDron;
import co.edu.poli.sw2.Servicio.DronFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Verificación manual del modelo de dominio y de la fábrica, sin base de datos.
 *
 * Comprueba herencia, polimorfismo, la relación bidireccional con Piloto
 * y la identidad por id.
 */
public class PruebaModelo {

    public static void main(String[] args) {

        List<Dron> flota = new ArrayList<>();
        flota.add(DronFactory.crearDron(TipoDron.AGRICULTURA, 1, "AGR-001",
                  "Agras T40", "DJI", 38.0, 40.0, false));
        flota.add(DronFactory.crearDron(TipoDron.VIGILANCIA, 2, "VIG-001",
                  "Matrice 30T", "DJI", 3.7, 0.0, true));
        flota.add(DronFactory.crearDron(TipoDron.VIGILANCIA, 3, "VIG-002",
                  "Anafi", "Parrot", 0.5, 0.0, false));

        System.out.println("--- Polimorfismo: mismo metodo, respuesta distinta ---");
        for (Dron d : flota) {
            System.out.println(d.getSerial() + " [" + d.getTipo().getCodigo() + "] -> "
                    + d.descripcionOperativa());
        }

        System.out.println("\n--- La fabrica devuelve la clase correcta ---");
        System.out.println("Primero:  " + flota.get(0).getClass().getSimpleName());
        System.out.println("Segundo:  " + flota.get(1).getClass().getSimpleName());

        System.out.println("\n--- Relacion bidireccional con Piloto ---");
        Piloto ana = new Piloto(10, "Ana Restrepo", 5, "3001234567");
        ana.asignarDron(flota.get(0));
        System.out.println("Piloto -> dron: " + ana.getDron().getSerial());
        System.out.println("Dron -> piloto: " + flota.get(0).getPiloto().getNombre());

        Piloto luis = new Piloto(11, "Luis Gomez", 2, "3009876543");
        luis.asignarDron(flota.get(0));
        System.out.println("Dueno actual:   " + flota.get(0).getPiloto().getNombre());

        System.out.println("\n--- Identidad por id ---");
        Dron copia = DronFactory.crearDron(TipoDron.VIGILANCIA, 2, "VIG-001",
                     "Matrice 30T", "DJI", 3.7, 0.0, true);
        System.out.println("copia.equals(original): " + copia.equals(flota.get(1)));
        System.out.println("La remueve de la lista: " + flota.remove(copia));
        System.out.println("Quedan en la flota:     " + flota.size());
    }
}