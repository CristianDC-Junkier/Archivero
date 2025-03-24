
package archivero.app.control;

import archivero.app.App;
import archivero.app.UsuarioController;

import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Clase que se encarga de controlar el login de los usuarios, ya sea de forma
 * anónima o con la contraseña y usuario
 *
 * @author Cristian Delgado Cruz
 * @version 1.0
 * @since 2025-02-27
 */
public class LoginController extends BaseController implements Initializable {

    @FXML
    private TextField usuario;

    @FXML
    private PasswordField contrasena;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarMovimientoVentana();
        // Eliminamos la identificación por si vuelve de una pestaña posterior
        UsuarioController.setIdentificado(false);
    }

    /**
     * Metodo para continuar, comprobando si existe error en la clave o usuario
     * si es necesario,
     * El usuario sera asignado en la clase UsuarioController.
     */
    @FXML
    private void continuar() throws IOException {
        Stage stage = (Stage) barraArriba.getScene().getWindow();

        if (!usuario.getText().equals(UsuarioController.getNombre())) {
            FailsController.mostrarError(stage, "Fallo reconociendo el usuario, introdúcelo de nuevo.");
        } else if (!contrasena.getText().equals(UsuarioController.getContraseña())) {
            FailsController.mostrarError(stage, "Fallo reconociendo la contraseña, introdúcela de nuevo.");
        } else {
            UsuarioController.setIdentificado(true);
            App.setRoot("/archivero/vistas/home" , "Expedientes");
            App.setDim(1000, 625);
        }
    }

    @FXML
    private void anonimo() throws IOException {

        App.setRoot("/archivero/vistas/home", "Expedientes");
        App.setDim(1000, 625);
    }


}
