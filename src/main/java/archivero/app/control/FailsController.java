
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Clase que se encarga del control de la vista para mostrar un error en
 * cualquier fallo realizado por el usuario
 *
 * @author Cristian Delgado Cruz
 * @version 1.0
 * @since 2025-02-27
 */
public class FailsController extends BaseController implements Initializable {

    @FXML
    private Text error_msg;
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

    private void setMensaje(String mensaje) {
        error_msg.setText(mensaje);
    }

    /**
     * Metodo que llama la vista padre para iniciar el dialog de forma modal.
     *
     * @param padre la vista padre que realiza la petición al dialog.
     * @param error error que se le quiere trasmitir al usuario
     */
    public static void mostrarError(Stage padre, String error) {
        try {
            FXMLLoader loader = new FXMLLoader(FailsController.class.getResource("/archivero/vistas/fails.fxml"));
            DialogPane dialogPane = loader.load();

            FailsController failsController = loader.getController();
            failsController.setMensaje(error);

            // Creamos y mostramos el diálogo
            Dialog<Void> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.initOwner(padre);

            // Obtener el Stage y quitar decoración
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.initStyle(StageStyle.UNDECORATED);

            dialog.showAndWait();

            LoggerController.log("Dialogo de Error: " + error);

        } catch (IOException ex) {
            LoggerController.log("Error mostrando el diálogo de error, con padre: " + padre.getTitle());
        }
    }

}
