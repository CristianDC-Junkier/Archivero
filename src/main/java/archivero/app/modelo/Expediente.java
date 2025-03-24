
package archivero.app.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase Modelo de Expediente para los expedientes
 *
 * @author Cristian Delgado Cruz
 * @version 1.1
 * @since 2025-02-27
 */
public class Expediente {

    @JsonProperty("anio")
    private int anio;
    @JsonProperty("direccion")
    private String direccion;
    @JsonProperty("nexp")
    private String nexp;
    @JsonProperty("promotor")
    private String promotor;
    @JsonProperty("nucleo")
    private String nucleo;
    @JsonProperty("nlegajo")
    private float nlegajo;
    @JsonProperty("titulo")
    private String titulo;

    public Expediente() {
    }

    public Expediente(int anio, String direccion, String nexp, String promotor, String nucleo, float nlegajo, String titulo) {
        this.anio = anio;
        this.direccion = direccion;
        this.nexp = nexp;
        this.promotor = promotor;
        this.nucleo = nucleo;
        this.nlegajo = nlegajo;
        this.titulo = titulo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNExp() {
        return nexp;
    }

    public void setNExp(String nexp) {
        this.nexp = nexp;
    }

    public String getPromotor() {
        return promotor;
    }

    public void setPromotor(String promotor) {
        this.promotor = promotor;
    }

    public String getNucleo() {
        return nucleo;
    }

    public void setNucleo(String nucleo) {
        this.nucleo = nucleo;
    }

    public float getNLegajo() {
        return nlegajo;
    }

    public void setNLegajo(float nLegajo) {
        this.nlegajo = nLegajo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }



}
