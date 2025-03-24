package archivero.app.control;

import archivero.app.AdjuntoController;
import archivero.app.App;
import archivero.app.ExpedienteController;
import archivero.app.UsuarioController;
import archivero.app.modelo.Adjunto;
import archivero.app.modelo.Expediente;

import java.awt.Desktop;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Clase que se encarga de controlar la vista del fichero
 *
 * @author Cristian Delgado Cruz
 * @version 1.0
 * @since 2025-02-27
 */
public class FileController extends BaseController implements Initializable {

    private static Expediente ficheroActual;

    // Path de la carpeta de DATA
    private static final String DATA_PATH = System.getenv("APPDATA")
            + File.separator + "Archivero"
            + File.separator + "data";

    // Path de la carpeta de los Adjuntos
    private static final String FICHERO_PATH = DATA_PATH
            + File.separator + "ficheros";

    /*///////////
    // BOTONES //
    ///////////*/
    @FXML
    private Button eliminarFicheroBoton;
    @FXML
    private Button modificarExpedienteBoton;
    @FXML
    private Button adjuntarBoton;
    @FXML
    private Button eliminarAdjuntoBoton;

    /*///////////
    /// DATOS ///
    ///////////*/
    @FXML
    private TextField expedienteField;
    @FXML
    private TextField añoField;
    @FXML
    private TextField nucleoField;
    @FXML
    private TextArea tituloField;
    @FXML
    private TextField promotorField;
    @FXML
    private TextField direccionField;
    @FXML
    private TextField legajoField;

    /*///////////
    /// TABLA ///
    ///////////*/
    @FXML
    private TableView<Adjunto> tablaAdjunto;
    @FXML
    private TableColumn<Adjunto, String> colNExp;
    @FXML
    private TableColumn<Adjunto, String> colDireccion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarMovimientoVentana();

        // Asignamos a la vista los valores del fichero actual
        expedienteField.setText(ficheroActual.getNExp());
        añoField.setText(String.valueOf(ficheroActual.getAnio()));
        nucleoField.setText(ficheroActual.getNucleo());
        tituloField.setText(ficheroActual.getTitulo());
        promotorField.setText(ficheroActual.getPromotor());
        direccionField.setText(ficheroActual.getDireccion());
        legajoField.setText(String.valueOf(ficheroActual.getNLegajo()));

        // Cargamos la tabla
        colNExp.setCellValueFactory(new PropertyValueFactory<>("nExp"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("Dir"));
        tablaAdjunto.setPlaceholder(new Label("No hay adjuntos disponibles."));

        // Cargar los datos de la Tabla
        cargarTabla();

        // Para saber si esta identificado
        if (!UsuarioController.isIdentificado()) {
            eliminarFicheroBoton.setVisible(false);
            modificarExpedienteBoton.setManaged(false);
            adjuntarBoton.setVisible(false);
            eliminarAdjuntoBoton.setManaged(false);

            eliminarFicheroBoton.setVisible(false);
            modificarExpedienteBoton.setManaged(false);
            adjuntarBoton.setVisible(false);
            eliminarAdjuntoBoton.setManaged(false);
        }
    }

    public static Expediente getFichero() {
        return FileController.ficheroActual;
    }

    public static void setFichero(Expediente actual) {
        FileController.ficheroActual = actual;
    }

    public void cargarTabla() {
        tablaAdjunto.setItems(FXCollections.observableArrayList(AdjuntoController.obtenerAdjuntosPorExpediente(ficheroActual.getNExp())));
    }

    /*////////////
    // FICHERO //
    ////////////*/
    @FXML
    private void volver() throws IOException {
        ficheroActual = null;
        App.setRoot("/archivero/vistas/home", "Expedientes");
        App.setDim(1000, 625);
    }

    @FXML
    private void eliminarFichero() throws IOException {
        Stage stage = (Stage) barraArriba.getScene().getWindow();

        DeleteFileController.mostrarConfirmacion(stage);

        if (DeleteFileController.comprobarConfirmacion() == true) {
            String nExp = ficheroActual.getNExp();
            ExpedienteController.eliminarFicheroPorNExp(nExp);
            AdjuntoController.eliminarAdjuntos(nExp);

            volver();
        }
    }

    @FXML
    private void modificarExpediente() throws IOException {
        App.setRoot("/archivero/vistas/modifyFile", "Modificar Expediente");
        App.setDim(780, 500);
    }

    /*////////////
    // ADJUNTOS //
    ////////////*/
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

            File archivoSeleccionado = fileChooser.showOpenDialog(stage);

            if (archivoSeleccionado != null) {

                if (AdjuntoController.existeAdjuntoPorNombre(ficheroActual.getNExp() + "_" + archivoSeleccionado.getName())) {
                    FailsController.mostrarError(stage, "El archivo introducido ya existe, cambia el nombre de este o elimine el anterior");
                } else {

                    File carpetaDestino = new File(FICHERO_PATH + File.separator + ficheroActual.getAnio());
                    if (!carpetaDestino.exists()) {
                        carpetaDestino.mkdirs();
                    }

                    Adjunto adjuntoañadir = new Adjunto();
                    adjuntoañadir.setNExp(ficheroActual.getNExp());
                    adjuntoañadir.setDir(new File(carpetaDestino, ficheroActual.getNExp() + "_" + archivoSeleccionado.getName()).getAbsolutePath());

                    AdjuntoController.agregarAdjunto(adjuntoañadir, archivoSeleccionado);
                    cargarTabla();
                }
            }
        }
    }

    /**
     * Muestra en la web normal del Desktop el archivo adjunto
     *
     * @throws da fallo si el fichero no es encontrado
     */
    @FXML
    private void visualizarAdjunto() throws IOException {
        Stage stage = (Stage) barraArriba.getScene().getWindow();
        Adjunto seleccionado = tablaAdjunto.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            Desktop.getDesktop().browse(new File(seleccionado.getDir()).toURI());
        } else {
            FailsController.mostrarError(stage, "Debe seleccionar un adjunto para poder visualizarlo.");
        }
    }

    /**
     * Elimina el adjunto seleccionado o da error si no hay nada seleccionado
     *
     * @throws da fallo si el fichero no es encontrado
     */
    @FXML
    private void eliminarAdjunto() throws IOException {
        Stage stage = (Stage) barraArriba.getScene().getWindow();
        Adjunto seleccionado = tablaAdjunto.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            DeleteFileController.mostrarConfirmacion(stage);
            if (DeleteFileController.comprobarConfirmacion() == true) {
                AdjuntoController.eliminarAdjunto(seleccionado.getNExp(), seleccionado.getDir());
                cargarTabla();
            }
        } else {
            FailsController.mostrarError(stage, "Debe seleccionar un adjunto para poder eliminarlo.");
        }
    }

}
