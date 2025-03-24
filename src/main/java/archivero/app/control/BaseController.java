
package archivero.app.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Clase abstracta que se encarga del panel de arriba, su movimiento, sus
 * botones y de la cual extienden todos los demas controladores
 *
 * @author Cristian Delgado Cruz
 * @version 1.1
 * @since 2025-02-27
 */
public abstract class BaseController {

    /*///////////
    /// PANEL ///
    ///////////*/
    protected double xOffset = 0;
    protected double yOffset = 0;

    @FXML
    protected Pane barraArriba;

    /**
     * Configura los eventos de arrastre de la ventana en la `barraArriba`. Este
     * método debe llamarse en `initialize()` de cada controlador que extienda
     * esta clase.
     */
    protected void configurarMovimientoVentana() {
        if (barraArriba != null) {
            barraArriba.setOnMousePressed((MouseEvent event) -> {
                Stage stage = (Stage) barraArriba.getScene().getWindow();
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            barraArriba.setOnMouseDragged((MouseEvent event) -> {
                Stage stage = (Stage) barraArriba.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
        }
    }

    /**
     * Cierra la aplicación.
     */
    @FXML
    protected void salir() {
        System.exit(0);
    }

    /**
     * Minimiza la ventana actual.
     *
     * @param event Evento de acción.
     */
    @FXML
    protected void minimizar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
}
