package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import org.springframework.stereotype.Component;

@Component
public class CodigoDeBarras implements ValidadorIdentificador {

    @Override
    public boolean esValido(String nombre, String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return false;
        }
        String[] palabras = descripcion.trim().split("\\s+");
        return palabras.length >= 3;
    }

    @Override
    public TipoIdentificadorEnum getTipo() {
        return TipoIdentificadorEnum.CODIGODEBARRAS;
    }
}
