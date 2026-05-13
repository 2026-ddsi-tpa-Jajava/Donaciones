package ar.edu.utn.dds.k3003.model.donaciones;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Categoria {
    @Setter
    @Getter
    String id;
    @Getter
    private String nombre;
    @Getter
    private String descripcion;
    @Getter
    private List<Subcategoria> subcategorias;

    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.subcategorias = new ArrayList<>();
    }

    public void agregarSubcategoria(Subcategoria nuevaSubcategoria) {
        this.subcategorias.add(nuevaSubcategoria);
    }
}
