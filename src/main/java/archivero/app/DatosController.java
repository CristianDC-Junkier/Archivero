
package archivero.app;

import java.io.BufferedOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Clase DatosController, es un Singleton que se encarga del control de
 * Ficheros, permitiendo exportar y descomprimir archivos RAR en un directorio
 * específico.
 *
 * @author Cristian Delgado Cruz
 * @version 1.0
 * @since 2025-03-06
 */
public class DatosController {

    // Path de la carpeta de data
    private static final String DATA_PATH = System.getenv("APPDATA")
            + File.separator + "Archivero"
            + File.separator + "data";

    // Path de la carpeta backup-data de salida
    private static final String BACKUPDATA_PATH = System.getenv("APPDATA")
            + File.separator + "Archivero"
            + File.separator + "backup-data";

    
    // Path del archivo ZIP de salida
    private static final String ZIP_PATH = BACKUPDATA_PATH
            + File.separator + "data_backup.zip";

    public DatosController() throws IOException {
            // Crear directorio de backup-data si no existe
            Files.createDirectories(Paths.get(BACKUPDATA_PATH));
    }

    
    
    /**
     * Método para exportar (comprimir en un archivo ZIP) los ficheros
     * contenidos en el directorio DATA_PATH.
     *
     * @param destinoCarpeta La ruta del archivo ZIP donde se guardarán los
     * datos.
     */
    public static void exportarDatos(File destinoCarpeta) {
        try {
            // Recoger los archivos de la carpeta DATA_PATH
            File datosCarpeta = new File(DATA_PATH);
            if (datosCarpeta.exists() && datosCarpeta.isDirectory()) {
                // Si el usuario seleccionó una carpeta, asegurarse de que el ZIP tenga un nombre válido
                if (destinoCarpeta.isDirectory()) {
                    destinoCarpeta = new File(destinoCarpeta, "data.zip");
                }

                // Llamar al método de compresión pasándole la ruta de destino
                comprimirDirectoryToZip(datosCarpeta.toPath(), destinoCarpeta.toPath());
                LoggerController.log("Exportación completada, archivo ZIP guardado en: " + destinoCarpeta.getAbsolutePath());
            } else {
                LoggerController.log("La carpeta " + DATA_PATH + " no existe o no es un directorio.");
            }
        } catch (IOException e) {
            LoggerController.log("Error en el exportar datos: " + e.getMessage());
        }
    }

    /**
     * Método para comprimir un directorio en un archivo ZIP.
     *
     * @param comprimirDir El directorio fuente que contiene los archivos a
     * comprimir.
     * @param resultadoZIP El archivo ZIP de salida.
     * @throws IOException Si ocurre un error al leer los archivos o escribir en
     * el archivo ZIP.
     */
    private static void comprimirDirectoryToZip(Path comprimirDir, Path resultadoZIP) throws IOException {
        try (ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(new File(resultadoZIP.toString()))) {
            zipOutputStream.setLevel(9);  // Establecer el nivel máximo de compresión

            // Recorrer todos los archivos y subdirectorios en comprimirDir
            Files.walk(comprimirDir).forEach(filePath -> {
                // Crear la entrada para cada archivo
                File file = filePath.toFile();
                String entryName = comprimirDir.relativize(filePath).toString();
                ZipArchiveEntry zipEntry = new ZipArchiveEntry(file, entryName);

                try {
                    zipOutputStream.putArchiveEntry(zipEntry);

                    // Si es un archivo, agregar su contenido al archivo ZIP
                    if (file.isFile()) {
                        try (InputStream inputStream = new FileInputStream(file)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                zipOutputStream.write(buffer, 0, bytesRead);
                            }
                        }
                    }

                    zipOutputStream.closeArchiveEntry();
                } catch (IOException e) {
                }
            });
        }
    }

    /**
     * Método para reemplazar el contenido de DATA_PATH con el contenido del
     * archivo ZIP y llamar a crear la copia de seguridad.
     *
     * @param zipFichero El archivo ZIP que se desea descomprimir.
     */
    public static void importarDatos(File zipFichero) {

        // Creamos la copia de seguridad si no estamos importando la copia de seguridad
        if (!zipFichero.getName().equals("data_backup.zip")) {
            crearCopiaSeguridad();
        }

        try {
            File destinationFolder = new File(DATA_PATH);

            // Llamar al método para descomprimir y reemplazar el contenido
            descomprimirZip(zipFichero, destinationFolder);
            LoggerController.log("Datos importados correctamente en " + DATA_PATH);
            ExpedienteController.cargarJSON();
            AdjuntoController.cargarJSON();

        } catch (IOException e) {
            LoggerController.log("Error en el exportar datos: " + e.getMessage());
        }

        // Creamos la copia de seguridad si estamos importando la copia de seguridad
        if (zipFichero.getName().equals("data_backup.zip")) {
            crearCopiaSeguridad();
        }
    }

    /**
     * Método para descomprimir un archivo ZIP en el directorio DATA_PATH,
     * reemplazando los archivos existentes.
     *
     * @param zipFichero El archivo ZIP que se desea descomprimir.
     * @param destinoCarpeta El directorio donde se extraerán los archivos (en
     * este caso, DATA_PATH).
     * @throws IOException Si ocurre un error al leer el archivo ZIP o escribir
     * en el directorio de destino.
     */
    private static void descomprimirZip(File zipFichero, File destinoCarpeta) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFichero))) {
            ZipEntry zipEntry;

            // Recorrer todas las entradas del archivo ZIP
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File newFile = new File(destinoCarpeta, zipEntry.getName());

                // Si la entrada es un directorio, crearlo
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    // Si es un archivo, extraer su contenido
                    // Crear las carpetas necesarias para el archivo
                    File parentDir = newFile.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    // Extraer el archivo al directorio de destino
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile))) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipInputStream.closeEntry();  // Cerrar la entrada del archivo ZIP
            }
        }
    }

    /**
     * Método para comprimir automáticamente la carpeta DATA en un ZIP dentro de
     * Archivero.
     */
    public static void crearCopiaSeguridad() {
        try {
            comprimirDirectoryToZip(Paths.get(DATA_PATH), Paths.get(ZIP_PATH));
            LoggerController.log("Copia de seguridad de Data creada en: " + ZIP_PATH);
        } catch (IOException e) {
            LoggerController.log("Error al crear la copia de seguridad de Data: " + e.getMessage());
        }
    }

}
