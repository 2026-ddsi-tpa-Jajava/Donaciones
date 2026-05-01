package ar.edu.utn.dds.k3003.model.donaciones;

import lombok.Getter;
import lombok.Setter;

public class Subcategoria {

    @Getter
    private String id;
    private String nombre;
    private String descripcion;
    private Categoria categoria;
    @Setter
    @Getter
    private boolean estaDadoDeAlta = true;

    public Subcategoria(String id, String nombre, String descripcion, Categoria categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public void darDeBaja()
    {
        this.setEstaDadoDeAlta(false);
    }
}
