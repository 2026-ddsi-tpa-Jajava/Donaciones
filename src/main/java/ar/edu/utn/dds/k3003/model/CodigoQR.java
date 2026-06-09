package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import org.springframework.stereotype.Component;

@Component
public class CodigoQR implements ValidadorIdentificador {

    @Override
    public boolean esValido(String nombre, String descripcion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        String nombreSinEspacios = nombre.replace(" ", "");
        return nombreSinEspacios.length() % 2 == 0;
    }

    @Override
    public TipoIdentificadorEnum getTipo() {
        return TipoIdentificadorEnum.QR;
    }
}
