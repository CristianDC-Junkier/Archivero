
package archivero.app.control;

import archivero.app.AdjuntoController;
import archivero.app.ExpedienteController;
import archivero.app.App;
import archivero.app.UsuarioController;
import archivero.app.modelo.Expediente;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Clase que se encarga de controlar y asignar la vista de las estadísticas así
 * como sus respectivas gráficas
 *
 * @author Cristian Delgado Cruz
 * @version 1.1
 * @since 2025-02-27
 */
public class HomeController extends BaseController implements Initializable {

    // Filtros activos para mostras los adjuntos Filtrados
    private final Map<String, Predicate<Expediente>> filtrosActivos = new HashMap<>();

    /*///////////
    // BOTONES //
    ///////////*/
    @FXML
    private Button añadirFichero;
    @FXML
    private Button eliminarFichero;
    @FXML
    private Button datosBoton;

    /*///////////
    /// TABLA ///
    ///////////*/
    @FXML
    private TableView<Expediente> tablaFichero;
    @FXML
    private TableColumn<Expediente, String> colNExp;
    @FXML
    private TableColumn<Expediente, String> colTitulo;
    @FXML
    private TableColumn<Expediente, Float> colNLeg;
    @FXML
    private TableColumn<Expediente, Integer> colAnio;
    @FXML
    private TableColumn<Expediente, String> colNucleo;
    @FXML
    private TableColumn<Expediente, String> colDireccion;
    @FXML
    private TableColumn<Expediente, String> colPromotor;

    /*///////////
    // FILTROS //
    ///////////*/
    // Expediente
    @FXML
    private TextField expedienteField;

    // Titulo
    @FXML
    private TextField tituloField;

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
    private TextField añoInicio;
    @FXML
    private TextField añoFin;
    @FXML
    private ComboBox<String> añoBox;

    // Nucleo
    @FXML
    private CheckBox nucleoAlmonte;
    @FXML
    private CheckBox nucleoMatalascañas;
    @FXML
    private CheckBox nucleoElRocio;
    @FXML
    private CheckBox nucleoOtros;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarMovimientoVentana();

        // Cargamos la tabla
        colAnio.setCellValueFactory(new PropertyValueFactory<>("Anio"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("Titulo"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("Direccion"));
        colNExp.setCellValueFactory(new PropertyValueFactory<>("nExp"));
        colPromotor.setCellValueFactory(new PropertyValueFactory<>("Promotor"));
        colNucleo.setCellValueFactory(new PropertyValueFactory<>("Nucleo"));
        colNLeg.setCellValueFactory(new PropertyValueFactory<>("NLegajo"));

        // Cargar los datos en la tabla
        tablaFichero.setPlaceholder(new Label("No hay expedientes disponibles."));
        tablaFichero.setItems(FXCollections.observableArrayList(ExpedienteController.getListaFicheros()));

        // Añadir los items a los combobox
        añoBox.getItems().addAll("<", "<=");
        añoBox.getSelectionModel().selectFirst();

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

        añoInicio.setTextFormatter(new TextFormatter<>(filtroInt));
        añoFin.setTextFormatter(new TextFormatter<>(filtroInt));

        // Para saber si esta identificado
        if (!UsuarioController.isIdentificado()) {
            añadirFichero.setVisible(false);
            añadirFichero.setManaged(false);

            eliminarFichero.setVisible(false);
            eliminarFichero.setManaged(false);

            datosBoton.setVisible(false);
            datosBoton.setManaged(false);
        }
    }

    /*///////////
    // BOTONES //
    ///////////*/
    @FXML
    private void ver() throws IOException {
        Stage stage = (Stage) barraArriba.getScene().getWindow();
        Expediente seleccionado = tablaFichero.getSelectionModel().getSelectedItem();

        // Verificar si hay un elemento seleccionado
        if (seleccionado != null) {
            FileController.setFichero(seleccionado);

            App.setRoot("/archivero/vistas/file", "Expediente");
            App.setDim(750, 560);
        } else {
            FailsController.mostrarError(stage, "Debe seleccionar un expediente para poder visualizarlo.");
        }
    }

    @FXML
    private void volver() throws IOException {
        App.setRoot("/archivero/vistas/login", "Inicio de Sesión");
        App.setDim(680, 450);
    }

    @FXML
    private void anadir() throws IOException {
        App.setRoot("/archivero/vistas/newFile", "Nuevo Expediente");
        App.setDim(780, 500);
    }

    @FXML
    private void eliminar() throws IOException {
        Stage stage = (Stage) barraArriba.getScene().getWindow();
        Expediente seleccionado = tablaFichero.getSelectionModel().getSelectedItem();

        // Verificar si hay un elemento seleccionado
        if (seleccionado != null) {

            DeleteFileController.mostrarConfirmacion(stage);

            if (DeleteFileController.comprobarConfirmacion() == true) {
                String nExp = seleccionado.getNExp();
                ExpedienteController.eliminarFicheroPorNExp(nExp);
                tablaFichero.getItems().remove(seleccionado);
                AdjuntoController.eliminarAdjuntos(nExp);
            }
        } else {
            FailsController.mostrarError(stage, "Debe seleccionar un expediente para poder eliminarlo.");
        }
    }

    @FXML
    private void datos() throws IOException{
        App.setRoot("/archivero/vistas/data" , "Importar y Exportar Datos");
        App.setDim(450, 400);
    }

    @FXML
    private void estadisticas() throws IOException {
        App.setRoot("/archivero/vistas/stadistics", "Estadísticas");
        App.setDim(1000, 525);
    }

    /*///////////
    // FILTROS //
    ///////////*/
    /**
     * Metodo para aplicar los filtros existentes o mostrar la tabla normal si
     * no hay ninguno activo
     */
    private void aplicarFiltros() {
        if (filtrosActivos.isEmpty()) {
            tablaFichero.setItems(FXCollections.observableArrayList(ExpedienteController.getListaFicheros()));
            return;
        }

        Predicate<Expediente> filtroFinal = filtrosActivos.values().stream()
                .reduce(Predicate::and)
                .orElse(f -> true);

        tablaFichero.setItems(FXCollections.observableArrayList(ExpedienteController.filtrarPor(filtroFinal)));
    }

    /**
     * Metodo para añadir un filtro y actualizar la tabla
     *
     * @param clave el nombre del filtro
     * @param filtro el filtro en si
     */
    private void cambiarFiltro(String clave, Predicate<Expediente> filtro) {
        filtrosActivos.put(clave, filtro);
        aplicarFiltros();
    }

    /**
     * Metodo para eliminar un filtro y actualizar la tabla
     *
     * @param clave el nombre del filtro
     */
    private void eliminarFiltro(String clave) {
        filtrosActivos.remove(clave);
        aplicarFiltros();
    }

    /**
     * Metodo para añadir reiniciar todos los filtros y los fields
     */
    @FXML
    private void reiniciarFiltros() {
        // Limpiar los filtros activos
        filtrosActivos.clear();
        aplicarFiltros();

        // Limpiar todos los campos de texto
        expedienteField.clear();
        tituloField.clear();
        direccionField.clear();
        promotorField.clear();
        legajoField.clear();
        añoInicio.clear();
        añoFin.clear();

        // Restablecer el ComboBox al primer elemento si tiene valores
        if (!añoBox.getItems().isEmpty()) {
            añoBox.getSelectionModel().select(0);
        }

        // Desmarcar todos los CheckBox
        nucleoAlmonte.setSelected(false);
        nucleoMatalascañas.setSelected(false);
        nucleoElRocio.setSelected(false);
        nucleoOtros.setSelected(false);
    }

    @FXML
    private void expedienteFiltro() {
        String texto = expedienteField.getText().trim();

        if (texto.isEmpty()) {
            eliminarFiltro("expediente");
            return;
        }

        // Crear el filtro que busca expedientes que contengan el texto
        Predicate<Expediente> filtro = fichero -> fichero.getNExp().toUpperCase().contains(texto.toUpperCase());

        cambiarFiltro("expediente", filtro);
    }

    @FXML
    private void tituloFiltro() {
        String texto = tituloField.getText().trim();

        if (texto.isEmpty()) {
            eliminarFiltro("titulo");
            return;
        }

        // Crear el filtro que busca titulos que contengan el texto
        Predicate<Expediente> filtro = fichero -> fichero.getTitulo().toUpperCase().contains(texto.toUpperCase());

        cambiarFiltro("titulo", filtro);
    }

    @FXML
    private void direccionFiltro() {
        String texto = direccionField.getText().trim();

        if (texto.isEmpty()) {
            eliminarFiltro("direccion");
            return;
        }

        // Crear el filtro que busca direccion que contengan el texto
        Predicate<Expediente> filtro = fichero -> fichero.getDireccion().toUpperCase().contains(texto.toUpperCase());

        cambiarFiltro("direccion", filtro);
    }

    @FXML
    private void promotorFiltro() {
        String texto = promotorField.getText().trim();

        if (texto.isEmpty()) {
            eliminarFiltro("promotor");
            return;
        }

        // Crear el filtro que busca promotores que contengan el texto
        Predicate<Expediente> filtro = fichero -> fichero.getPromotor().toUpperCase().contains(texto.toUpperCase());

        cambiarFiltro("promotor", filtro);
    }

    @FXML
    private void legajoFiltro() {
        String texto = legajoField.getText().trim();

        if (texto.isEmpty()) {
            eliminarFiltro("legajo");
            return;
        }

        // Crear el filtro que busca legajos que contengan el numero
        Predicate<Expediente> filtro = fichero -> fichero.getNLegajo() == Float.parseFloat(texto);

        cambiarFiltro("legajo", filtro);
    }

    /**
     * Filtro que muestra los ficheros/expedientes de un año a otro, o en caso
     * de que solo haya una fecha.
     */
    @FXML
    private void añoFiltro() {
        String inicioTexto = añoInicio.getText().trim();
        String finTexto = añoFin.getText().trim();
        String operador = añoBox.getValue();

        Predicate<Expediente> filtroFinal;

        // Si ambos campos tienen valores de 4 dígitos
        if (inicioTexto.matches("\\d{4}") && finTexto.matches("\\d{4}")) {
            int inicio = Integer.parseInt(inicioTexto);
            int fin = Integer.parseInt(finTexto);

            if ("<".equals(operador)) {
                filtroFinal = fichero -> inicio <= fichero.getAnio() && fichero.getAnio() < fin;
            } else { // "<="
                filtroFinal = fichero -> inicio <= fichero.getAnio() && fichero.getAnio() <= fin;
            }

            // Guardamos el filtro en el manager
            cambiarFiltro("año", filtroFinal);

        } // Si solo el campo de inicio tiene 4 dígitos
        else if (inicioTexto.matches("\\d{4}")) {
            int inicio = Integer.parseInt(inicioTexto);
            filtroFinal = fichero -> inicio <= fichero.getAnio();

            // Guardamos el filtro
            cambiarFiltro("año", filtroFinal);

        } // Si solo el campo de fin tiene 4 dígitos
        else if (finTexto.matches("\\d{4}")) {
            int fin = Integer.parseInt(finTexto);

            if ("<".equals(operador)) {
                filtroFinal = fichero -> fichero.getAnio() < fin;
            } else { // "<="
                filtroFinal = fichero -> fichero.getAnio() <= fin;
            }

            // Guardamos el filtro
            cambiarFiltro("año", filtroFinal);

        } else {
            eliminarFiltro("año");
        }
    }

    /**
     * Filtro que utiliza el checkbox para mostrar por núcleo.
     *
     */
    @FXML
    private void nucleoFiltro() {
        // Mapa de CheckBox a valores para el filtro
        Map<CheckBox, String> valoresNucleos = new HashMap<>();
        valoresNucleos.put(nucleoAlmonte, "Almonte");
        valoresNucleos.put(nucleoMatalascañas, "Matalascañas");
        valoresNucleos.put(nucleoElRocio, "El Rocío");

        List<Predicate<Expediente>> filtrosSeleccionados = valoresNucleos.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected()) // Filtrar los seleccionados
                .map(entry -> (Predicate<Expediente>) (f -> f.getNucleo().equalsIgnoreCase(entry.getValue())))
                .collect(Collectors.toList());

        // Si el CheckBox "Otros" está seleccionado, agregar un filtro adicional
        if (nucleoOtros.isSelected()) {
            Predicate<Expediente> filtroOtros = f
                    -> !f.getNucleo().equalsIgnoreCase("Almonte")
                    && !f.getNucleo().equalsIgnoreCase("Matalascañas")
                    && !f.getNucleo().equalsIgnoreCase("El Rocío");

            filtrosSeleccionados.add(filtroOtros);
        }

        // Si no hay filtros seleccionados, eliminar el filtro "nucleo"
        if (filtrosSeleccionados.isEmpty()) {
            eliminarFiltro("nucleo");
            return;
        }

        // Si hay filtros, combinarlos con OR
        Predicate<Expediente> filtroFinal = filtrosSeleccionados.stream()
                .reduce(Predicate::or)
                .orElse(f -> true); // Aceptar todos si falla por alguna razón

        cambiarFiltro("nucleo", filtroFinal);
    }

}
