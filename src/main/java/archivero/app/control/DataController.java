
package archivero.app.control;

import archivero.app.App;
import archivero.app.DatosController;
import archivero.app.LoggerController;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Clase que se encarga de conrolar la vista que permite al usuario
 * exportar e importar los datos.
 *
 * @author Cristian Delgado Cruz
 * @version 1.1
 * @since 2025-03-06
 */
public class DataController extends BaseController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarMovimientoVentana();
    }

    @FXML
    private void importar() throws IOException{
        Stage stage = (Stage) barraArriba.getScene().getWindow();

        // Crear un DirectoryChooser para seleccionar directorios
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar el archivo de data.zip para importar");

        // Filtro para que solo muestre "data.zip"
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo ZIP", "data.zip", "data_backup.zip"));
        // Mostrar el diálogo para seleccionar el archivo
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Desktop"));

        // Mostrar el diálogo para seleccionar el archivo
        File archivoSeleccionado = fileChooser.showOpenDialog(stage);

        // Si se seleccionó una carpeta, pasarla al método importarDatos
        if (archivoSeleccionado != null) {
            if (archivoSeleccionado.getName().equals("data.zip") || archivoSeleccionado.getName().equals("data_backup.zip")) {
                DatosController.importarDatos(archivoSeleccionado);
                atras();
            }else{
                LoggerController.log("El fichero seleccionado no es data.zip o data_backup.zip");
            }
        }
    }

    @FXML
    private void exportar() throws IOException{
        Stage stage = (Stage) barraArriba.getScene().getWindow();

        // Crear un DirectoryChooser para seleccionar directorios
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar la carpeta de destino del archivo data.zip para exportar");

        // Mostrar el diálogo para seleccionar una carpeta
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Desktop"));

        // Mostrar el diálogo para seleccionar una carpeta
        File carpetaSeleccionada = directoryChooser.showDialog(stage);

        // Si se seleccionó una carpeta, pasarla al método exportarDatos
        if (carpetaSeleccionada != null) {
            DatosController.exportarDatos(carpetaSeleccionada);
            atras();
        }
    }

    @FXML
    private void atras() throws IOException {
        App.setRoot("/archivero/vistas/home", "Expedientes");
        App.setDim(1000, 625);
    }
}
