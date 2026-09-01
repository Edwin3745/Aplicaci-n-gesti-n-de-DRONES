package co.edu.poli.sw2;

/**
 * Lanzador de la aplicación.
 *
 * <p>Existe como clase aparte de {@link MainApp} porque, cuando JavaFX no se
 * carga como módulo, la JVM rechaza arrancar directamente una clase que herede
 * de {@code Application}. Delegando desde aquí, el programa se puede ejecutar
 * también con un {@code java -jar} corriente.</p>
 */
public class Main {

    /**
     * Crea el lanzador. No se usa: la clase solo aporta el método
     * {@link #main(String[])}.
     */
    public Main() {
    }

    /**
     * Punto de entrada del programa.
     *
     * @param args argumentos de la línea de comandos, que se pasan tal cual a
     *             la aplicación JavaFX.
     */
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
