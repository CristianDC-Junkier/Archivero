
package archivero.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;

/**
 * Clase Principal, se encarga de iniciar la aplicación.
 *
 * @author Cristian Delgado Cruz
 * @version 1.1
 * @since 2025-02-27
 */
public class App extends Application {

    // Escena principal
    private static Scene scene;

    /**
     * Inicia la aplicación llamando a la primera pantalla, llamada login
     *
     * @param stage La ventana principal de la aplicación.
     * @throws IOException Si hay un error al cargar la vista
     */
    @Override
    public void start(Stage stage) throws IOException {
        LoggerController LC = new LoggerController();
        BackupController BC = new BackupController();

        DatosController DC = new DatosController();

        ExpedienteController FC = new ExpedienteController();
        AdjuntoController AC = new AdjuntoController();
        

        scene = new Scene(loadFXML("/archivero/vistas/login"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/archivero/iconos/ayuntamiento de almonte a color letras blancas.png")));
        stage.setTitle("Archivero - Inicio de Sesión");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Introduce una nueva ventana en la escena
     *
     * @param fxml la ventana en si, sin extensión, de eso se encarga otro
     * metodo
     * @param title Título que se mostrará en la barra de la ventana.
     * @throws IOException Si hay un error al cargar la vista
     */
    public static void setRoot(String fxml, String title) throws IOException {
        scene.setRoot(loadFXML(fxml));

        // Obtener la ventana actual y actualizar el título
        Stage stage = (Stage) scene.getWindow();
        stage.setTitle("Archivero - " + title);
    }

    /**
     * Introduce las nuevas dimensiones de la escena
     *
     * @param width el ancho de la ventana
     * @param height la altura de la ventana
     * @throws IOException Si hay un error al cargar la vista
     */
    public static void setDim(int width, int height) throws IOException {
        Stage stage = (Stage) scene.getWindow();
        stage.setWidth(width);
        stage.setHeight(height);
    }

    /**
     * Carga una vista FXML y actualiza el título de la ventana.
     *
     * @param fxml Nombre del archivo FXML sin la extensión.
     * @return El nodo raíz de la vista cargada.
     * @throws IOException Si hay un error al cargar la vista.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();

        return root;
    }

    public static void main(String[] args) {
        launch();
    }

}
