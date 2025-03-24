
package archivero.app.control;

import archivero.app.AdjuntoController;
import archivero.app.App;
import archivero.app.ExpedienteController;
import archivero.app.modelo.Expediente;

import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

/**
 * Clase que se encarga de controlar la modificación de un archivo previamenente
 * asignado o pasado por parametros de un metodo interno utilizado por una clase
 * externa
 *
 * @author Cristian Delgado Cruz
 * @version 1.1
 * @since 2025-02-27
 */
public class ModifyFileController extends BaseController implements Initializable {

    // Expediente el cual se esta modificando
    private Expediente ficheroMod = null;


    /*////////////
    /// CAMPOS ///
    ////////////*/
    // Expediente
    @FXML
    private TextField expedienteField;

    // Titulo
    @FXML
    private TextArea tituloField;

    // Direccion
    @FXML
    private TextField direccionField;

    // Promotor
    @FXML
    private TextField promotorField;

    // Legajo
    @FXML
    private TextField legajoField;

    // Año
    @FXML
    private TextField añoField;

    // Nucleo
    @FXML
    private ComboBox<String> nucleoBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarMovimientoVentana();

        // Añadir los items a los combobox
        nucleoBox.getItems().addAll("Almonte", "El Rocío", "Matalascañas", "Otros");

        // Para el float de legajo
        UnaryOperator<TextFormatter.Change> filtroFloat = change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.matches("\\d*(\\.\\d*)?")) {
                return change;
            }
            return null;
        };

        legajoField.setTextFormatter(new TextFormatter<>(filtroFloat));

        // Para los años
        UnaryOperator<TextFormatter.Change> filtroInt = change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.matches("\\d*")) {
                return change;
            }
            return null;
        };

        añoField.setTextFormatter(new TextFormatter<>(filtroInt));

        ficheroMod = FileController.getFichero();

        expedienteField.setText(ficheroMod.getNExp());
        direccionField.setText(ficheroMod.getDireccion());
        promotorField.setText(ficheroMod.getPromotor());
        tituloField.setText(ficheroMod.getTitulo());
        añoField.setText(String.valueOf(ficheroMod.getAnio()));
        legajoField.setText(String.valueOf(ficheroMod.getNLegajo()));
        nucleoBox.getSelectionModel().select(ficheroMod.getNucleo());

    }

    /*///////////
    // BOTONES //
    ///////////*/
    /**
     * Metodo para modificar elFichero/Expediente, comprobando si el usuario
     * erró en algun campo, y llamando al dialog modal con el error en cada
     * caso, culminando en la vista del fichero en si. La excepción es número de
     * expediente y año, que no se puede modificar al ser las claves primarias.
     * También mueve los adjuntos a un nuevo año si es necesario
     */
    @FXML
    private void modificar() throws IOException {
        Stage stage = (Stage) barraArriba.getScene().getWindow();
        Expediente fileañadir = new Expediente();

        fileañadir.setNExp(ficheroMod.getNExp());

        if (tituloField.getText().isBlank()) {
            FailsController.mostrarError(stage, "Introduce un título válido.");
        } else if (legajoField.getText().isBlank()) {
            FailsController.mostrarError(stage, "Introduce un número de legajo válido.");
        } else {
            fileañadir.setNExp(expedienteField.getText());
            fileañadir.setTitulo(tituloField.getText());
            fileañadir.setAnio(Integer.parseInt(añoField.getText()));
            fileañadir.setNLegajo(Float.parseFloat(legajoField.getText()));
            if (direccionField.getText().isBlank()) {
                fileañadir.setDireccion("-");
            } else {
                fileañadir.setDireccion(direccionField.getText());
            }
            if (promotorField.getText().isBlank()) {
                fileañadir.setPromotor("-");
            } else {
                fileañadir.setPromotor(promotorField.getText());
            }
            fileañadir.setNucleo(nucleoBox.getValue());

            // Una vez modificado lo actualizamos/asignamos a la vista del fichero 
            // para continuar observandolo
            ExpedienteController.modificarFichero(fileañadir);
            FileController.setFichero(fileañadir);

            App.setRoot("/archivero/vistas/file" , "Expediente");
            App.setDim(750, 560);
        }
    }

    @FXML
    private void atras() throws IOException {
        App.setRoot("/archivero/vistas/file" , "Expediente");
        App.setDim(750, 560);
    }

}
