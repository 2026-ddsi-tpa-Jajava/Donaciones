package ar.edu.utn.dds.k3003.model.donaciones;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public class Producto {

    @Setter
    @Getter
    private String id;
    @Getter
    private String nombre;
    @Getter
    private String descripcion;
    @Getter
    private Subcategoria subcategoria;
    @Getter
    private Optional<Identificador> identificador;
    @Setter
    @Getter
    private boolean estaDadoDeAlta = true;

    public Producto(String id, String nombre, String descripcion, Subcategoria subcategoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.subcategoria = subcategoria;
        this.identificador = Optional.empty();
    }

    public Producto(String id, String nombre, String descripcion, Subcategoria subcategoria, Identificador identificador) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.subcategoria = subcategoria;
        this.identificador = Optional.ofNullable(identificador);
    }

    public boolean tieneID(String id) {
        return this.id.equals(id);

    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = Optional.ofNullable(identificador);
    }

    public void darDeBaja()
    {
        this.setEstaDadoDeAlta(false);
    }

    public boolean tieneSubcategoria(String subcategoriaID) {
        return this.subcategoria.getId().equals(id);
    }
}
