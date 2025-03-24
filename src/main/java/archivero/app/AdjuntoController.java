package archivero.app;

import archivero.app.modelo.Adjunto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase Adjunto Controller, es un Singleton que se encarga del control de
 * Ficheros
 *
 * @author Cristian Delgado Cruz
 * @version 1.2
 * @since 2025-02-27
 */
public class AdjuntoController {

    // Path de la carpeta de DATA
    private static final String DATA_PATH = System.getenv("APPDATA")
            + File.separator + "Archivero"
            + File.separator + "data";

    // Path de la carpeta de JSON
    private static final String JSON_PATH = DATA_PATH
            + File.separator + "json";

    // La lista estatica de ficheros
    private static final List<Adjunto> listaAdjuntos = new ArrayList<>();

    public AdjuntoController() throws IOException {
        // Crear directorio de json si no existe
        Files.createDirectories(Paths.get(DATA_PATH + File.separator + "ficheros"));
        cargarJSON();
    }

    /**
     * Carga el JSON, si no existe, lo crea
     */
    protected static void cargarJSON() {
        ObjectMapper objectMapper = new ObjectMapper();
        File archivoJSON = new File(JSON_PATH + File.separator + "adjuntos.json");

        if (!archivoJSON.exists()) {
            if (!BackupController.restaurarDesdeBackup("adjuntos.json")) {
                LoggerController.log("Se creará un nuevo JSON de adjuntos");
            }
        }

        try {
            List<Adjunto> adjuntosCargados = objectMapper.readValue(archivoJSON, new TypeReference<List<Adjunto>>() {
            });
            listaAdjuntos.clear();
            listaAdjuntos.addAll(adjuntosCargados);

            LoggerController.log("Adjuntos cargados de forma correcta");

        } catch (IOException e) {
            LoggerController.log("Error al cargar los adjuntos");
        }
    }

    /**
     * Devuelve una lista pasada por copia, de todos los adjuntos de un
     * expediente
     *
     * @param nExp el numero de expediente por el que buscar.
     * @return la lista pasada por copia
     */
    public static List<Adjunto> obtenerAdjuntosPorExpediente(String nExp) {
        return listaAdjuntos.stream()
                .filter(adj -> adj.getNExp().equals(nExp))
                .collect(Collectors.toList());
    }

    /**
     * Añade un nuevo adjunto, introduciendo una entrada en el JSON y colocando
     * una copia en la carpeta correspondiente
     *
     * @param nuevoAdjunto el adjunto que añades al json
     * @param archivoSeleccionado el archivo que se añade a la carpeta
     */
    public static void agregarAdjunto(Adjunto nuevoAdjunto, File archivoSeleccionado) {
        // Ruta destino
        File archivoDestino = new File(nuevoAdjunto.getDir());

        // Copiar el archivo a la carpeta destino
        try {
            Files.copy(archivoSeleccionado.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LoggerController.log("Fallo al agregar el adjunto: " + nuevoAdjunto.getNExp());
        }

        listaAdjuntos.add(nuevoAdjunto);

        guardarJSON();

        LoggerController.log("Adjunto Agregado: " + nuevoAdjunto.getNExp() + ", con dirección: " + nuevoAdjunto.getNExp());
    }

    /**
     * Elimina un adjunto
     *
     * @param nExp numero de expediente del adjunto
     * @param dir dirección del adjunto
     */
    public static void eliminarAdjunto(String nExp, String dir) {
        Iterator<Adjunto> iterator = listaAdjuntos.iterator();

        while (iterator.hasNext()) {
            Adjunto adjunto = iterator.next();
            if (adjunto.getNExp().equals(nExp) && adjunto.getDir().equals(dir)) {
                // Intentar eliminar el archivo físico
                File archivo = new File(dir);
                if (archivo.exists()) {
                    archivo.delete();
                }

                // Eliminar de la lista
                iterator.remove();
                guardarJSON();

                LoggerController.log("Adjunto Eliminado: " + nExp);

                break;
            }
        }
    }

    /**
     * Elimina todos los adjuntos de un expediente
     *
     * @param nExp numero de expediente
     */
    public static void eliminarAdjuntos(String nExp) {
        Iterator<Adjunto> iterator = listaAdjuntos.iterator();

        while (iterator.hasNext()) {
            Adjunto adjunto = iterator.next();
            if (adjunto.getNExp().equals(nExp)) {
                // Intentar eliminar el archivo físico
                File archivo = new File(adjunto.getDir());
                if (archivo.exists()) {
                    archivo.delete();
                }
                // Eliminar de la lista
                iterator.remove();
            }
        }
        LoggerController.log("Adjuntos Eliminados: " + nExp);
        guardarJSON();
    }

    private static void guardarJSON() {
        try {
            File archivoJSON = new File(JSON_PATH + File.separator + "adjuntos.json");

            File directorioPadre = archivoJSON.getParentFile();
            if (directorioPadre != null && !directorioPadre.exists()) {
                directorioPadre.mkdirs(); // Crear directorios si no existen
            }

            // Guardar el JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(archivoJSON, listaAdjuntos);

        } catch (Exception e) {
            LoggerController.log("Error guardando la lista de adjuntos");
        }
    }

    /**
     * Busca un adjunto por el nombre final del archivo
     *
     * @param nombre nombre sin la extensión del archivo
     * @@return si existe o no
     */
    public static boolean existeAdjuntoPorNombre(String nombre) {
        Iterator<Adjunto> iterator = listaAdjuntos.iterator();
        while (iterator.hasNext()) {
            Adjunto adjunto = iterator.next();
            String nombreFichero = Paths.get(adjunto.getDir().trim()).getFileName().toString();
            if (nombreFichero.equalsIgnoreCase(nombre.trim())) {
                return true;
            }
        }
        return false;
    }

}
