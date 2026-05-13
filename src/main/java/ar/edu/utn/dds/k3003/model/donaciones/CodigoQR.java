package ar.edu.utn.dds.k3003.model.donaciones;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import lombok.val;

public class CodigoQR implements TipoIdentificador {

    public CodigoQR() {
    }

    @Override
    public boolean esValido(Producto producto) {
        val cantidadDeLetras = producto.getNombre().length();

        return  this.esPar(cantidadDeLetras);
    }

    @Override
    public TipoIdentificadorEnum getEnum() {
        return TipoIdentificadorEnum.QR;
    }

    private boolean esPar(Integer cantidadDeLetras) {
        return cantidadDeLetras % 2 == 0;
    }


}
