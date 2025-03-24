
package archivero.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Clase Backup Controller, es un Singleton que se encarga del control de las
 * las copias de seguridad
 *
 * @author Cristian Delgado Cruz
 * @version 1.0
 * @since 2025-03-03
 */
public class BackupController {

    // Path de la carpeta de DATA
    private static final String DATA_PATH = System.getenv("APPDATA") 
        + File.separator + "Archivero"
        + File.separator + "data";

    // Path de la carpeta de JSON
    private static final String JSON_PATH = DATA_PATH
            + File.separator + "json";

    // Path de la carpeta de BACKUPS
    private static final String BACKUP_PATH = JSON_PATH + File.separator + "backups";

    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    private static final int MAX_BACKUPS = 20;
    private static final List<String> FILES_TO_BACKUP = Arrays.asList("expedientes.json", "adjuntos.json");

    public BackupController() {
        try {
            // Crear directorio de backups si no existe
            Files.createDirectories(Paths.get(BACKUP_PATH));

            crearBackup();

        } catch (IOException e) {
            LoggerController.log("Error al crear los Backup");
        }
    }

    /**
     * Crea las copias de seguridad
     */
    public static void crearBackup() throws IOException {
        // Obtener la fecha y hora actual para el nombre del backup
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());

        // Copiar cada archivo de la lista FILES_TO_BACKUP
        for (String fileName : FILES_TO_BACKUP) {
            Path sourcePath = Paths.get(JSON_PATH, fileName);
            Path backupPath = Paths.get(BACKUP_PATH, fileName.replace(".json", "_" + timestamp + ".json"));

            // Verificar si el archivo original existe antes de copiarlo y cambiarle la fecha de actualización
            if (Files.exists(sourcePath)) {
                Files.copy(sourcePath, backupPath, StandardCopyOption.REPLACE_EXISTING);

                // Actualizar la fecha de modificación del backup
                Files.setLastModifiedTime(backupPath, FileTime.fromMillis(System.currentTimeMillis()));

                LoggerController.log("Backup creado: " + backupPath.getFileName());
            }

        }

        // Limpiar backups antiguos si hay más de 10
        limpiarBackupsAntiguos();
    }

    /**
     * Elimina los backups más antiguos hasta mantener solo MAX_BACKUPS archivos
     * en total.
     */
    private static void limpiarBackupsAntiguos() {
        try {
            File backupDir = new File(BACKUP_PATH);
            File[] backups = backupDir.listFiles((dir, name) -> name.endsWith(".json"));

            if (backups != null && backups.length > MAX_BACKUPS) {
                // Ordenar todos los backups por fecha de modificación (más antiguos primero)
                Arrays.sort(backups, Comparator.comparingLong(File::lastModified));

                // Calcular cuántos archivos hay que eliminar
                int backupsAEliminar = backups.length - MAX_BACKUPS;

                // Eliminar los backups más antiguos, sin importar si son de "ficheros" o "adjuntos"
                for (int i = 0; i < backupsAEliminar; i++) {
                    if (backups[i].delete()) {
                        LoggerController.log("Backup antiguo eliminado: " + backups[i].getName());
                    } else {
                        LoggerController.log("No se pudo eliminar el backup antiguo: " + backups[i].getName());
                    }
                }
            }
        } catch (Exception e) {
            LoggerController.log("Error al limpiar backups antiguos: ");
        }
    }

    /**
     * Restaura el archivo desde el backup más reciente.
     *
     * @param nombreArchivo Nombre del archivo original EJ: ficheros.json
     * @return true si la restauración fue exitosa, false en caso contrario.
     */
    public static boolean restaurarDesdeBackup(String nombreArchivo) {
        try {
            // Buscar el backup más reciente en la carpeta de backups
            Path backupDir = Paths.get(BACKUP_PATH);
            Optional<Path> backupMasReciente = Files.list(backupDir)
                    .filter(path -> path.getFileName().toString().startsWith(nombreArchivo.replace(".json", "_")))
                    .map(Path::toFile) // Convertir a File antes de acceder a lastModified()
                    .sorted(Comparator.comparingLong(File::lastModified).reversed()) // Ordenar por fecha de modificación
                    .map(File::toPath) // Convertir de nuevo a Path
                    .findFirst();

            if (backupMasReciente.isPresent()) {
                Path backupPath = backupMasReciente.get();
                Path destinoPath = Paths.get(JSON_PATH, nombreArchivo);

                // Restaurar el backup sobrescribiendo el archivo original
                Files.copy(backupPath, destinoPath, StandardCopyOption.REPLACE_EXISTING);
                LoggerController.log("Restaurado desde backup: " + backupPath.getFileName());
                return true;
            } else {
                LoggerController.log("No se encontró ningún backup para restaurar.");
                return false;
            }
        } catch (IOException e) {
            LoggerController.log("Error al restaurar desde backup");
            return false;
        }
    }
}
