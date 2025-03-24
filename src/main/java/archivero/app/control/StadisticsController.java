
package archivero.app.control;

import archivero.app.ExpedienteController;
import archivero.app.App;
import archivero.app.modelo.Expediente;

import java.io.IOException;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;

/**
 * Clase que se encarga de controlar y asignar la vista de las estadísticas así
 * como sus respectivas gráficas
 *
 * @author Cristian Delgado Cruz
 * @version 1.0
 * @since 2025-02-27
 */
public class StadisticsController extends BaseController implements Initializable {

    /*//////////////
    /// GRÁFICAS ///
    //////////////*/
    @FXML
    private PieChart pieChartFicheros;
    @FXML
    private BarChart barChartFicheros;
    @FXML
    private ComboBox elecBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarMovimientoVentana();

        // Añadir los items a los combobox
        elecBox.getItems().addAll("Núcleo", "Año");
        elecBox.getSelectionModel().selectFirst();

        // Añadir y configurar los axis de la gráfica de barras
        NumberAxis yAxis = (NumberAxis) barChartFicheros.getYAxis();
        yAxis.setTickUnit(1);
        yAxis.setMinorTickVisible(false);
        yAxis.setForceZeroInRange(true);

        generarGraficas("Núcleo");
    }

    /*//////////////
    /// GRAFICAS ///
    //////////////*/
    private void generarGraficas(String campo) {
        Function<Expediente, String> campoFunc;

        switch (campo) {
            case "Año":
                campoFunc = fichero -> String.valueOf(fichero.getAnio());
                break;
            case "Núcleo":
                campoFunc = fichero -> fichero.getNucleo();
                break;
            default:
                System.out.println("Campo no reconocido: " + campo);
                return;
        }

        generarPieChart(campo, campoFunc);
        generarBarChart(campo, campoFunc);
    }

    /**
     * Metodo para configurar la gráfica circular, respecto a un campo
     * utilizando un HashMap para realizar la diferenciación por campo
     *
     * @param campo grupo por el cual ordenar.
     * @param campoFunc función que devuelve un string de un atributo asignado
     * respecto a un fichero
     */
    private void generarPieChart(String campo, Function<Expediente, String> campoFunc) {

        // Contar los elementos según el campo especificado
        Map<String, Integer> conteo = new HashMap<>();

        for (Expediente fichero : ExpedienteController.getListaFicheros()) {
            String clave = campoFunc.apply(fichero);

            // Si el campo es "Nucleo", agrupar otros valores en "Otros"
            if (campo.equals("Núcleo")
                    && !clave.equalsIgnoreCase("Almonte")
                    && !clave.equalsIgnoreCase("Matalascañas")
                    && !clave.equalsIgnoreCase("El Rocío")) {
                clave = "Otros";
            }

            conteo.put(clave, conteo.getOrDefault(clave, 0) + 1);
        }

        // Crear la lista de datos para el PieChart
        ObservableList<PieChart.Data> datosPie = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
            datosPie.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Asignar los datos al PieChart
        pieChartFicheros.setData(datosPie);
    }

    /**
     * Metodo para configurar la gráfica de barras, respecto a un campo
     * utilizando un HashMap para realizar la diferenciación por campo
     *
     * @param campo grupo por el cual ordenar.
     * @param campoFunc función que devuelve un string de un atributo asignado
     * respecto a un fichero
     */
    public void generarBarChart(String campo, Function<Expediente, String> campoFunc) {

        // Contar los elementos según el campo especificado
        Map<String, Integer> conteo = new HashMap<>();

        for (Expediente fichero : ExpedienteController.getListaFicheros()) {
            String clave = campoFunc.apply(fichero);

            // Si el campo es "Nucleo", agrupar otros valores en "Otros"
            if (campo.equals("Núcleo")
                    && !clave.equalsIgnoreCase("Almonte")
                    && !clave.equalsIgnoreCase("Matalascañas")
                    && !clave.equalsIgnoreCase("El Rocío")) {
                clave = "Otros";
            }

            conteo.put(clave, conteo.getOrDefault(clave, 0) + 1);
        }

        // Limpiar datos previos y añadir los nuevos
        barChartFicheros.getData().clear();

        BarChart.Series<String, Number> serie = new BarChart.Series<>();
        for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
            serie.getData().add(new BarChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Asignar los datos al BarChart
        barChartFicheros.getData().add(serie);
    }

    /*///////////
    // BOTONES //
    ///////////*/
    @FXML
    private void listado() throws IOException {
        App.setRoot("/archivero/vistas/home" , "Expedientes");
        App.setDim(1000, 625);
    }

    @FXML
    private void elecFiltro() {
        generarGraficas(elecBox.getValue().toString());
    }

}
