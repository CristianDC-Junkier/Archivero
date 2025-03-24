package archivero.app.control;

import archivero.app.AdjuntoController;
import archivero.app.ExpedienteController;
import archivero.app.App;
import archivero.app.modelo.Adjunto;
import archivero.app.modelo.Expediente;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Clase que se encarga de controlar y asignar la vista de las estadísticas así
 * como sus respectivas gráficas
 *
 * @author Cristian Delgado Cruz
 * @version 1.0
 * @since 2025-02-27
 */
public class NewFileController extends BaseController implements Initializable {

    // El Adjunto básico a anadir, si lo hubiera
    private File adjuntoAñadir = null;

    // Path de la carpeta de DATA
    private static final String DATA_PATH = System.getenv("APPDATA")
            + File.separator + "Archivero"
            + File.separator + "data";

    // Path de la carpeta de los Adjuntos
    private static final String FICHERO_PATH = DATA_PATH
            + File.separator + "ficheros";

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

    // Adjunto
    @FXML
    private Label direccionAdjunto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarMovimientoVentana();

        // Añadir los items a los combobox
        nucleoBox.getItems().addAll("Almonte", "El Rocío", "Matalascañas", "Otros");
        nucleoBox.getSelectionModel().selectFirst();

        // Filtro en el Legajo, para que solo puedas anadir valores de tipo float
        UnaryOperator<TextFormatter.Change> filtroFloat = change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.matches("\\d*(\\.\\d*)?")) {
                return change;
            }
            return null;
        };

        legajoField.setTextFormatter(new TextFormatter<>(filtroFloat));

        // Filtro en el año, para que solo puedas anadir valores de tipo int
        UnaryOperator<TextFormatter.Change> filtroInt = change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.matches("\\d*")) {
                return change;
            }
            return null;
        };

        añoField.setTextFormatter(new TextFormatter<>(filtroInt));
    }

    /*///////////
    // BOTONES //
    ///////////*/
    /**
     * Metodo para añadir el nuevo Fichero/Expediente, comprobando si el usuario
     * erró en algun campo, y llamando al dialog modal con el error en cada
     * caso, además de añadir el adjunto si es rellenado. Columinando en la
     * vista home, o normal.
     */
    @FXML
    private void anadir() throws IOException {
        Stage stage = (Stage) barraArriba.getScene().getWindow();
        Expediente fileañadir = new Expediente();
        Adjunto adjuntoañadir = new Adjunto();

        if (expedienteField.getText().isBlank() || expedienteField.getText().matches(".*[\\\\/:*?\"<>|].*")) {
            FailsController.mostrarError(stage, "Introduce un número de expediente válido.");
        } else if (añoField.getText().isBlank() || añoField.getText().length() < 4) {
            FailsController.mostrarError(stage, "Introduce un año válido.");
        } else if (ExpedienteController.existeFicheroPorNExp(expedienteField.getText(), Integer.parseInt(añoField.getText()))) {
            FailsController.mostrarError(stage, "El número de expediente introducido ya existe.");
        } else if (tituloField.getText().isBlank()) {
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

            ExpedienteController.agregarFichero(fileañadir);

            // Añadir Adjunto si existe
            if (this.adjuntoAñadir != null && this.adjuntoAñadir.exists()) {
                // Crear carpeta si no existe
                File carpetaDestino = new File(FICHERO_PATH + File.separator + añoField.getText());
                if (!carpetaDestino.exists()) {
                    carpetaDestino.mkdirs();
                }

                adjuntoañadir.setNExp(fileañadir.getNExp());
                adjuntoañadir.setDir(new File(carpetaDestino, fileañadir.getNExp() + "_" + adjuntoAñadir.getName()).getAbsolutePath());

                AdjuntoController.agregarAdjunto(adjuntoañadir, adjuntoAñadir);
            }

            App.setRoot("/archivero/vistas/home", "Expedientes");
            App.setDim(1000, 625);
        }
    }

    /**
     * Adjunta un archivo de tipo PDF,DOC,DOCX,JPG,PNG,TXT al Expediente/Expediente
     *
     * @throws da fallo si el fichero no es encontrado
     */
    @FXML
    private void adjuntar() throws IOException {
        Stage stage = (Stage) barraArriba.getScene().getWindow();
        if (añoField.getText().isBlank() || añoField.getText().length() < 4) {
            FailsController.mostrarError(stage, "Introduce un año válido primero.");
        } else {

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Documentos y imágenes (PDF, DOC, DOCX, JPG, PNG, TXT)",
                    "*.pdf", "*.doc", "*.docx", "*.jpg", "*.png", "*.txt"
            ));

            this.adjuntoAñadir = fileChooser.showOpenDialog(stage);

            if (adjuntoAñadir != null && this.adjuntoAñadir.exists()) {
                direccionAdjunto.setText(adjuntoAñadir.getAbsolutePath());
            }
        }
    }

    @FXML
    private void atras() throws IOException {
        App.setRoot("/archivero/vistas/home", "Expedientes");
        App.setDim(1000, 625);
    }

}
