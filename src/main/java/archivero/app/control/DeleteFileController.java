
package archivero.app.control;

import archivero.app.LoggerController;
import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Clase que se encarga del control de la vista para la confirmación de eliminar
 * un fichero.
 *
 * @author Cristian Delgado Cruz
 * @version 1.0
 * @since 2025-02-27
 */
public class DeleteFileController extends BaseController implements Initializable {

    // Valor estatico que será recogido para saber si fue aceptado o no
    private static boolean confirmado;

    @FXML
    private Button closedialogButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarMovimientoVentana();
    }

    @FXML
    private void cerrar_dialog() {
        Stage stage = (Stage) closedialogButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void eliminarFichero() {
        confirmado = true;
        cerrar_dialog();
    }

    @FXML
    private void noeliminarFichero() {
        cerrar_dialog();
    }

    /**
     * Devuelve si fue confirmada o no la eliminación
     *
     * @return la confirmación 
     */
    public static boolean comprobarConfirmacion() {
        return confirmado;
    }

    /**
     * Metodo que llama la vista padre para iniciar el dialog
     * de forma modal.
     *
     * @param padre la vista padre que realiza la petición al dialog.
     */
    public static void mostrarConfirmacion(Stage padre) {
        try {
            FXMLLoader loader = new FXMLLoader(FailsController.class.getResource("/archivero/vistas/deleteFile.fxml"));
            DialogPane dialogPane = loader.load();

            // Creamos y mostramos el diálogo
            Dialog<Void> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.initOwner(padre);

            // Obtener el Stage y quitar decoración
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.initStyle(StageStyle.UNDECORATED);

            // Establecemos confirmado como falso;
            confirmado = false;

            dialog.showAndWait();

        } catch (IOException ex) {
            LoggerController.log("Error mostrando el diálogo de eliminar fichero");
        }
    }

}
