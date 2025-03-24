
package archivero.app.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase modelo de Adjunto para los archivos pdf,jpeg etc asociados
 *
 * @author Cristian Delgado Cruz
 * @version 1.0
 * @since 2025-02-27
 */
public class Adjunto {

    @JsonProperty("nexp")
    private String nExp;
    @JsonProperty("dir")
    private String dir;

    public Adjunto() {
    }

    public Adjunto(String nExp, String Dir) {
        this.nExp = nExp;
        this.dir = Dir;
    }

    public String getNExp() {
        return nExp;
    }

    public void setNExp(String nExp) {
        this.nExp = nExp;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String Dir) {
        this.dir = Dir;
    }

}
