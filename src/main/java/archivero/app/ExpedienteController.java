
package archivero.app;

import archivero.app.modelo.Expediente;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Clase Expediente Controller, es un Singleton que se encarga del control de
 * los Expedientes
 *
 * @author Cristian Delgado Cruz
 * @version 2.0
 * @since 2025-02-27
 */
public class ExpedienteController {

    // Path de la carpeta de DATA
    private static final String DATA_PATH = System.getenv("APPDATA") 
        + File.separator + "Archivero"
        + File.separator + "data";

    // Path de la carpeta de JSON
    private static final String JSON_PATH = DATA_PATH
            + File.separator + "json";

    // La lista estatica de ficheros
    private static List<Expediente> listaFicheros = new ArrayList<>();

    public ExpedienteController() throws IOException {
        // Crear directorio de json si no existe
        Files.createDirectories(Paths.get(JSON_PATH));
        
        cargarJSON();
    }

    /**
     * Carga el JSON, si no existe, lo crea
     */
    protected static void cargarJSON() {
        ObjectMapper objectMapper = new ObjectMapper();
        File archivoJSON = new File(JSON_PATH + File.separator + "expedientes.json");

        if (!archivoJSON.exists()) {
            if (!BackupController.restaurarDesdeBackup("expedientes.json")) {
                LoggerController.log("Se creará un nuevo JSON de expedientes");
            }
        }

        try {
            listaFicheros = objectMapper.readValue(archivoJSON, new TypeReference<List<Expediente>>() {
            });
            listaFicheros.sort(Comparator.comparingInt(Expediente::getAnio));

            LoggerController.log("Expedientes cargados de forma correcta");
        } catch (IOException e) {
            LoggerController.log("Error al cargar los expedientes");
        }
    }

    public static void agregarFichero(Expediente nuevoFichero) {
        listaFicheros.add(nuevoFichero);
        LoggerController.log("Expediente añadido: " + nuevoFichero.getNExp() + "/" + nuevoFichero.getAnio());
        guardarJSON();
    }

    public static void modificarFichero(Expediente ficheroModificado) {
        for (int i = 0; i < listaFicheros.size(); i++) {
            if (listaFicheros.get(i).getNExp().equals(ficheroModificado.getNExp())) {
                listaFicheros.set(i, ficheroModificado);
                break;
            }
        }
        guardarJSON();
        LoggerController.log("Expediente modificado: " + ficheroModificado.getNExp() + "/" + ficheroModificado.getAnio());

    }

    public static void eliminarFicheroPorNExp(String nExp) {
        Iterator<Expediente> iterator = listaFicheros.iterator();
        while (iterator.hasNext()) {
            Expediente fichero = iterator.next();
            if (fichero.getNExp().trim().equalsIgnoreCase(nExp.trim())) {
                iterator.remove();
                break;
            }
        }
        guardarJSON();
        LoggerController.log("Expediente eliminado: " + nExp);
    }

    public static boolean existeFicheroPorNExp(String nExp, int anio) {
        Iterator<Expediente> iterator = listaFicheros.iterator();
        while (iterator.hasNext()) {
            Expediente fichero = iterator.next();
            if (fichero.getNExp().trim().equalsIgnoreCase(nExp.trim()) && fichero.getAnio() == anio) {
                return true;
            }
        }
        return false;
    }

    public static void guardarJSON() {
        try {
            File archivoJSON = new File(JSON_PATH + File.separator + "expedientes.json");

            File directorioPadre = archivoJSON.getParentFile();
            if (directorioPadre != null && !directorioPadre.exists()) {
                directorioPadre.mkdirs(); // Crear directorios
            }

            // Guardar el JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(archivoJSON, listaFicheros);

        } catch (Exception e) {
            LoggerController.log("Error guardando la lista de expedientes");
        }
    }

    /**
     * Devuelve la lista de fichero, por copia
     */
    public static List<Expediente> getListaFicheros() {
        return new ArrayList<>(listaFicheros);
    }

    /**
     * Filtra la lista, devolviendela por un criterio
     *
     * @param criterio predicado por el cual se filtra en la lista
     */
    public static List<Expediente> filtrarPor(Predicate<Expediente> criterio) {
        return listaFicheros.stream()
                .filter(criterio)
                .collect(Collectors.toList());
    }

}
