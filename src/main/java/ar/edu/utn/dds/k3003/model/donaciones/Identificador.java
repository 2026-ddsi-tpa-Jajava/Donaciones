package ar.edu.utn.dds.k3003.model.donaciones;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import ar.edu.utn.dds.k3003.exceptions.donaciones.ProductoInvalidoException;
import lombok.Getter;
import lombok.Setter;

public class Identificador {

    @Setter
    @Getter
    private String id;
    @Getter
    private String descripcion;
    private TipoIdentificador tipoIdentificador;

    public Identificador(String descripcion, TipoIdentificador tipoIdentificador) {
        this.descripcion = descripcion;
        this.tipoIdentificador = tipoIdentificador;
    }

    public boolean tieneID(String id) {
        return  this.id.equals(id);
    }

    public void validar(Producto producto) {
        if (!tipoIdentificador.esValido(producto))
        {
            throw new ProductoInvalidoException("Producto Invalido");
        }
    }

    public TipoIdentificadorEnum getTipoIdentificador() {
        return tipoIdentificador.getEnum();
    }

    public void setTipoIdentificador(TipoIdentificadorEnum tipoIdentificadorEnum) {
        // TODO
    }
}
