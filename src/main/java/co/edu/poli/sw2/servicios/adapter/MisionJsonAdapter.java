package co.edu.poli.sw2.servicios.adapter;

import co.edu.poli.sw2.modelo.Dron;
import co.edu.poli.sw2.modelo.Mision;
import co.edu.poli.sw2.servicios.ServicioException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Adapta una {@link Mision} a la interfaz {@link ExportableJson}.
 *
 * <p>Es el Adapter del patrón: implementa el Target y contiene una instancia
 * del Adaptee. {@code Mision} no se modifica ni conoce este adaptador; toda la
 * traducción a JSON se hace aquí, leyendo únicamente sus getters públicos.</p>
 *
 * <p>El JSON se compone a mano para no añadir dependencias al proyecto.</p>
 */
public class MisionJsonAdapter implements ExportableJson {

    /** Salto de línea usado en el JSON generado. */
    private static final String NL = System.lineSeparator();

    /** Un nivel de sangría del JSON. */
    private static final String SANGRIA = "  ";

    /** Formato con el que se escribe la fecha de la misión. */
    private static final String FORMATO_FECHA = "yyyy-MM-dd";

    /** Misión que se adapta. */
    private final Mision mision;

    /**
     * Envuelve una misión para poder exportarla.
     *
     * @param mision misión a adaptar; no puede ser nula.
     * @throws IllegalArgumentException si la misión es nula.
     */
    public MisionJsonAdapter(Mision mision) {
        if (mision == null) {
            throw new IllegalArgumentException("La misión a adaptar no puede ser nula.");
        }
        this.mision = mision;
    }

    /**
     * Traduce la misión y sus drones a JSON.
     *
     * @return texto JSON con los datos de la misión.
     */
    @Override
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{").append(NL)
            .append(SANGRIA).append("\"id\": ").append(mision.getId()).append(",").append(NL)
            .append(SANGRIA).append("\"nombre\": ").append(comoTexto(mision.getNombre())).append(",").append(NL)
            .append(SANGRIA).append("\"ubicacion\": ").append(comoTexto(mision.getUbicacion())).append(",").append(NL)
            .append(SANGRIA).append("\"fecha\": ").append(comoTexto(formatearFecha(mision.getFecha()))).append(",").append(NL)
            .append(SANGRIA).append("\"drones\": ").append(dronesComoJson()).append(NL)
            .append("}");
        return json.toString();
    }

    /**
     * Escribe el JSON de la misión en un archivo UTF-8.
     *
     * @param ruta ruta del archivo de destino; se crea o se sobrescribe.
     * @throws IllegalArgumentException si la ruta es nula o está vacía.
     * @throws ServicioException si no se puede escribir el archivo.
     */
    @Override
    public void guardarEn(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía.");
        }
        Path destino = Path.of(ruta.trim());
        try {
            Path carpeta = destino.toAbsolutePath().getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            Files.writeString(destino, toJson(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ServicioException("No se pudo guardar el archivo JSON en " + destino, e);
        }
    }

    /**
     * Compone el arreglo JSON con los drones de la misión.
     *
     * @return arreglo JSON, o {@code []} si la misión no tiene drones.
     */
    private String dronesComoJson() {
        List<Dron> drones = mision.getDrones();
        if (drones.isEmpty()) {
            return "[]";
        }
        String n2 = SANGRIA.repeat(2);
        String n3 = SANGRIA.repeat(3);

        StringBuilder json = new StringBuilder("[").append(NL);
        for (int i = 0; i < drones.size(); i++) {
            Dron dron = drones.get(i);
            json.append(n2).append("{").append(NL)
                .append(n3).append("\"id\": ").append(dron.getId()).append(",").append(NL)
                .append(n3).append("\"serial\": ").append(comoTexto(dron.getSerial())).append(",").append(NL)
                .append(n3).append("\"tipo\": ").append(comoTexto(dron.getTipo().getCodigo())).append(NL)
                .append(n2).append("}");
            if (i < drones.size() - 1) {
                json.append(",");
            }
            json.append(NL);
        }
        json.append(SANGRIA).append("]");
        return json.toString();
    }

    /**
     * Convierte una fecha a texto ISO (año-mes-día).
     *
     * @param fecha fecha a convertir; puede ser nula.
     * @return fecha formateada, o {@code null} si no hay fecha.
     */
    private String formatearFecha(Date fecha) {
        if (fecha == null) {
            return null;
        }
        // Se crea en cada llamada porque SimpleDateFormat no es seguro entre hilos.
        return new SimpleDateFormat(FORMATO_FECHA).format(fecha);
    }

    /**
     * Convierte un valor a literal JSON de texto.
     *
     * @param valor texto a convertir; puede ser nulo.
     * @return el texto entre comillas y escapado, o {@code null} (sin comillas)
     *         si el valor es nulo.
     */
    private String comoTexto(String valor) {
        return valor == null ? "null" : "\"" + escapar(valor) + "\"";
    }

    /**
     * Escapa los caracteres que romperían un texto JSON.
     *
     * @param texto texto original.
     * @return texto con comillas, barras y caracteres de control escapados.
     */
    private String escapar(String texto) {
        StringBuilder salida = new StringBuilder();
        for (char c : texto.toCharArray()) {
            switch (c) {
                case '"'  -> salida.append("\\\"");
                case '\\' -> salida.append("\\\\");
                case '\n' -> salida.append("\\n");
                case '\r' -> salida.append("\\r");
                case '\t' -> salida.append("\\t");
                default -> {
                    if (c < 0x20) {
                        salida.append(String.format("\\u%04x", (int) c));
                    } else {
                        salida.append(c);
                    }
                }
            }
        }
        return salida.toString();
    }
}