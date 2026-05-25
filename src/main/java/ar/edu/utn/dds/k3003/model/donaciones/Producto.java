package ar.edu.utn.dds.k3003.model.donaciones;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Setter
@Getter
public class Producto {

    private String id;
    private String nombre;
    private String descripcion;
    private String subcategoriaID;
    private Identificador identificador;

    public Producto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public boolean tieneID(String id) {
        return this.id.equals(id);

    }

    public boolean tieneSubcategoria(String subcategoriaID) {
        return this.subcategoriaID.equals(subcategoriaID);
    }
}
