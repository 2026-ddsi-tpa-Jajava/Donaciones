package ar.edu.utn.dds.k3003.model.donaciones;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public class Producto {

    @Setter
    @Getter
    private String id;
    @Setter
    @Getter
    private String nombre;
    @Setter
    @Getter
    private String descripcion;
    @Setter
    @Getter
    private Subcategoria subcategoria;
    @Setter
    @Getter
    private Identificador identificador;

    public Producto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public boolean tieneID(String id) {
        return this.id.equals(id);

    }

    public boolean tieneSubcategoria(String subcategoriaID) {
        return this.subcategoria.getId().equals(subcategoriaID);
    }
}
