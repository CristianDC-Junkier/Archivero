
package archivero.app;

/**
 * Clase Usuario Controller, es un Singleton que se encarga del control del
 * Usuario
 *
 * @author Cristian Delgado Cruz
 * @version 1.0
 * @since 2025-02-27
 */
public class UsuarioController {

    private static final String contrasena = "";
    private static final String nombre = "";
    
    /** 
     * Valor que se utiliza para identificar si 
     * el usuario esta o no está conectado
     */
    private static boolean identificado;

    public UsuarioController() {
        identificado = false;
    }

    public static String getContraseña() {
        return contrasena;
    }

    public static String getNombre() {
        return nombre;
    }

    public static boolean isIdentificado() {
        return identificado;
    }

    public static void setIdentificado(boolean identificado) {
        UsuarioController.identificado = identificado;
    }

}
