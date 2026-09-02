package co.edu.poli.sw2.servicios;

import co.edu.poli.sw2.Modelo.Dron;
import co.edu.poli.sw2.Modelo.Sensor;

import java.util.List;

/**
 * Redacta los informes que la interfaz muestra para evidenciar el
 * funcionamiento de los patrones de creación.
 *
 * <p>Vive en la capa de servicios y no en la vista porque componer el texto es
 * lógica, no presentación: la vista se limita a volcar en su {@code TextArea}
 * la cadena que recibe. Esto además permite probar el contenido del informe sin
 * levantar JavaFX.</p>
 *
 * <p>Ninguna salida de esta clase va a consola: todo se devuelve como
 * {@link String} para que lo muestre quien lo pidió.</p>
 */
public final class InformeDeIdentidad {

    /** Salto de línea usado en los informes. */
    private static final String NL = System.lineSeparator();

    /**
     * Constructor privado: la clase solo expone métodos estáticos y no debe
     * instanciarse.
     */
    private InformeDeIdentidad() {
    }

    /**
     * Redacta la evidencia de que un clon es un objeto distinto del original.
     *
     * <p>Usa {@link System#identityHashCode(Object)} y no {@code hashCode()}:
     * {@link Dron} sobrescribe {@code hashCode()} a partir del id de negocio,
     * de modo que dos objetos distintos con el mismo id darían el mismo valor y
     * el informe no probaría nada. {@code identityHashCode} devuelve la
     * identidad que la JVM asigna a cada objeto, que es lo que aquí interesa.</p>
     *
     * <p>Sobre la comparación de las listas de sensores conviene una
     * advertencia, que el propio informe incluye: {@code Dron.getSensores()}
     * devuelve la lista envuelta con {@code Collections.unmodifiableList()}, y
     * ese envoltorio se crea <em>en cada llamada</em>. Sus identidades difieren
     * siempre, incluso cuando detrás está la misma lista, así que esa línea por
     * sí sola no demuestra que la copia sea profunda. Por eso el informe añade
     * una comprobación que sí lo demuestra: se agrega un sensor únicamente al
     * clon y se muestran los tamaños de ambas listas. Si la lista se
     * compartiera, el original también crecería.</p>
     *
     * @param original dron del que se partió.
     * @param copia    clon obtenido de {@link DronPrototypeManager#clonar(Dron)}.
     * @return informe listo para mostrarse en la interfaz.
     */
    public static String compararIdentidades(Dron original, Dron copia) {
        StringBuilder informe = new StringBuilder();

        informe.append("=== PATRÓN PROTOTYPE — evidencia de identidad ===").append(NL)
               .append(NL)
               .append("Original : ").append(referencia(original)).append(NL)
               .append("Copia    : ").append(referencia(copia)).append(NL)
               .append(NL);

        List<Sensor> sensoresOriginal = original.getSensores();
        List<Sensor> sensoresCopia = copia.getSensores();

        informe.append("Sensores original : ").append(referencia(sensoresOriginal)).append(NL)
               .append("Sensores copia    : ").append(referencia(sensoresCopia)).append(NL)
               .append("   Atención: getSensores() envuelve la lista en cada llamada,").append(NL)
               .append("   así que estos dos valores difieren siempre. Por sí solos no").append(NL)
               .append("   demuestran nada. La prueba real está más abajo.").append(NL)
               .append(NL);

        informe.append("original == copia   -> ").append(original == copia)
               .append("  (son objetos distintos en memoria)").append(NL)
               .append("original.equals()   -> ").append(original.equals(copia))
               .append("  (identidad por id de negocio: el original tiene id ")
               .append(original.getId()).append(" y la copia ").append(copia.getId())
               .append(")").append(NL)
               .append(NL);

        informe.append(comprobarIndependenciaDeSensores(original, copia));

        informe.append(NL)
               .append("Qué demuestra cada comparación:").append(NL)
               .append("  · identityHashCode distinto -> la JVM les dio identidades").append(NL)
               .append("    separadas: son dos objetos, no dos nombres del mismo.").append(NL)
               .append("  · original == copia false   -> confirma lo anterior comparando").append(NL)
               .append("    referencias, no contenido.").append(NL)
               .append("  · equals() compara el id de negocio, no la memoria. Por eso").append(NL)
               .append("    da false aquí: el clon nace sin id, a la espera del que le").append(NL)
               .append("    asigne PostgreSQL al guardarlo.").append(NL)
               .append("  · el recuento de sensores demuestra que la copia es profunda:").append(NL)
               .append("    la lista del clon es suya, no una referencia compartida.").append(NL);

        return informe.toString();
    }

    /**
     * Comprueba en vivo que la lista de sensores del clon es independiente.
     *
     * <p>Agrega un sensor de prueba <strong>solo al clon</strong> —nunca al
     * original, que es un dron real del usuario— y compara los tamaños. Después
     * lo retira, para que el dron que llega al formulario quede como estaba.
     * Funciona incluso cuando el dron no tiene sensores, que es lo habitual al
     * venir de la base de datos.</p>
     *
     * @param original dron del que se partió.
     * @param copia    clon a comprobar.
     * @return fragmento del informe con el resultado de la comprobación.
     */
    private static String comprobarIndependenciaDeSensores(Dron original, Dron copia) {
        int antesOriginal = original.getSensores().size();
        int antesCopia = copia.getSensores().size();

        Sensor sensorDePrueba = new Sensor(0, "sensor de prueba", "—");
        copia.agregarSensor(sensorDePrueba);

        int despuesOriginal = original.getSensores().size();
        int despuesCopia = copia.getSensores().size();

        copia.removerSensor(sensorDePrueba);

        String veredicto = despuesOriginal == antesOriginal
                ? "la lista NO se comparte: la copia es profunda"
                : "¡LA LISTA SE COMPARTE! la copia sería superficial";

        return new StringBuilder()
                .append("Prueba real de independencia de la lista:").append(NL)
                .append("  se agrega un sensor SOLO a la copia").append(NL)
                .append("  sensores en el original -> ").append(antesOriginal)
                .append(" antes, ").append(despuesOriginal).append(" después").append(NL)
                .append("  sensores en la copia    -> ").append(antesCopia)
                .append(" antes, ").append(despuesCopia).append(" después").append(NL)
                .append("  ").append(veredicto).append(NL)
                .toString();
    }

    /**
     * Redacta la traza de la construcción de un dron con el patrón Builder.
     *
     * @param dron              dron recién construido.
     * @param llamadas          secuencia de llamadas encadenadas que se ejecutó.
     * @return informe listo para mostrarse en la interfaz.
     */
    public static String describirConstruccion(Dron dron, String llamadas) {
        return new StringBuilder()
                .append("=== PATRÓN BUILDER — construcción encadenada ===").append(NL)
                .append(NL)
                .append("Secuencia ejecutada:").append(NL)
                .append(llamadas).append(NL)
                .append(NL)
                .append("Objeto resultante:").append(NL)
                .append("  Referencia : ").append(referencia(dron)).append(NL)
                .append("  Clase real : ").append(dron.getClass().getName()).append(NL)
                .append("  toString() : ").append(dron).append(NL)
                .append("  Descripción: ").append(dron.descripcionOperativa()).append(NL)
                .append(NL)
                .append("Qué demuestra:").append(NL)
                .append("  · cada método del builder devolvió this, y por eso las").append(NL)
                .append("    llamadas pudieron encadenarse en una sola expresión.").append(NL)
                .append("  · build() validó los datos ANTES de instanciar, así que no").append(NL)
                .append("    llega a existir un dron a medio construir.").append(NL)
                .append("  · el builder no invocó ningún constructor: delegó en la").append(NL)
                .append("    fábrica del subtipo, que decidió la clase concreta.").append(NL)
                .toString();
    }

    /**
     * Compone la referencia de un objeto igual que lo haría
     * {@code Object.toString()} de serie: clase, arroba e identidad en
     * hexadecimal.
     *
     * @param objeto objeto a describir.
     * @return texto con la clase y la identidad en memoria del objeto.
     */
    private static String referencia(Object objeto) {
        return objeto.getClass().getName()
                + "@" + Integer.toHexString(System.identityHashCode(objeto));
    }
}
