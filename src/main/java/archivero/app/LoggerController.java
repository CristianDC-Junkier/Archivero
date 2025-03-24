
package archivero.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Clase Logger Controller, es un Singleton que se encarga del control del Log
 *
 * @author Cristian Delgado Cruz
 * @version 1.0
 * @since 2025-03-03
 */
public class LoggerController {

    // Path de la carpeta de DATA
    private static final String DATA_PATH = System.getenv("APPDATA") 
        + File.separator + "Archivero"
        + File.separator + "data";

    // Path de la carpeta de JSON
    private static final String LOG_PATH = DATA_PATH
            + File.separator + "logs";

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final int MAX_LOGS = 10;

    private static String logFilePath;
    private static int connectionCount = 1;

    public LoggerController() {
        try {
            // Crear directorio de logger si no existe
            Files.createDirectories(Paths.get(LOG_PATH));

            // Obtener fecha actual
            String fechaHoy = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            logFilePath = LOG_PATH + "/log_" + fechaHoy + ".txt";

            inicializarLog();

            limpiarLogsAntiguos();

        } catch (IOException e) {
            System.err.println("Error al inicializar el log: " + e.getMessage());
        }
    }

    /**
     * Elimina los últimos log
     */
    private void limpiarLogsAntiguos() {
        try {
            File logDir = new File(LOG_PATH);
            File[] archivos = logDir.listFiles((dir, name) -> name.startsWith("log_") && name.endsWith(".txt"));

            if (archivos != null && archivos.length > MAX_LOGS) {
                // Ordenar archivos por fecha (más antiguos primero)
                Arrays.sort(archivos, Comparator.comparingLong(File::lastModified));

                // Eliminar el más antiguo
                if (archivos[0].delete()) {
                    System.out.println("Log antiguo eliminado: " + archivos[0].getName());
                } else {
                    System.err.println("No se pudo eliminar el log antiguo: " + archivos[0].getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al limpiar logs antiguos: " + e.getMessage());
        }
    }

    /**
     * Inicializa o carga el log de hoy
     */
    private static void inicializarLog() {
        try {
            File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                // Si el log no existe, crearlo y agregar "Conexión 1"
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                    writer.write("Conexión 1\n");
                    writer.write("--------------------------\n");
                }
            } else {
                // Si el log ya existe, contar conexiones previas
                List<String> lineas = Files.readAllLines(logFile.toPath());
                for (String linea : lineas) {
                    if (linea.startsWith("Conexión")) {
                        connectionCount++;
                    }
                }
                // Agregar nueva conexión
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                    writer.write("\nConexión " + connectionCount + "\n");
                    writer.write("--------------------------\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error al inicializar el log: " + e.getMessage());
        }
    }

    /**
     * Para escribir un mensaje en el log
     *
     * @param mensaje mensaje que se escribe en el log
     */
    public static void log(String mensaje) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            String timestamp = new SimpleDateFormat(TIME_FORMAT).format(new Date());
            writer.write(timestamp + " - " + mensaje + "\n");
        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }
}
