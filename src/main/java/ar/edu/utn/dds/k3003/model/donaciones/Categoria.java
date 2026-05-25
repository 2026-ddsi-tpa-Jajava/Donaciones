package ar.edu.utn.dds.k3003.model.donaciones;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Categoria {
    String id;
    private String nombre;
    private String descripcion;
    private String subcategoriaID;

    public Categoria(String nombre, String descripcion, String subcategoriaID) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.subcategoriaID = subcategoriaID;
    }

    public boolean tieneID(String categoriaID) {
        return this.getId().equals(categoriaID);
    }
}
