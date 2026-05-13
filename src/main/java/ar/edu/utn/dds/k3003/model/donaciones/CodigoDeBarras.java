package ar.edu.utn.dds.k3003.model.donaciones;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import lombok.val;

public class CodigoDeBarras implements TipoIdentificador {

    public CodigoDeBarras() {
    }

    @Override
    public boolean esValido(Producto producto) {
        val cantidadDePalabras = producto.getDescripcion().trim().split("\\s+").length;

        return cantidadDePalabras >= 3;
    }

    @Override
    public TipoIdentificadorEnum getEnum() {
        return TipoIdentificadorEnum.CODIGODEBARRAS;
    }
}
