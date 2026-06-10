package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FabricaValidadoresIdentificador {
    private final Map<TipoIdentificadorEnum, ValidadorIdentificador> validadores;

    public FabricaValidadoresIdentificador(List<ValidadorIdentificador> listaValidadores) {
        this.validadores = listaValidadores.stream()
                .collect(Collectors.toMap(ValidadorIdentificador::getTipo, v -> v));
    }

    public ValidadorIdentificador obtenerValidador(TipoIdentificadorEnum tipo) {
        ValidadorIdentificador validador = validadores.get(tipo);
        if (validador == null) {
            throw new IllegalArgumentException("Estrategia no implementada para: " + tipo);
        }
        return validador;
    }
}

