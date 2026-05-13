package ar.edu.utn.dds.k3003.repositories.donaciones.identificador;

import ar.edu.utn.dds.k3003.model.donaciones.Donacion;
import ar.edu.utn.dds.k3003.model.donaciones.Identificador;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryIdentificadoresRepo implements IdentificadoresRepository {

    private List<Identificador> identificadores;
    private AtomicLong idSecuencia;

    public InMemoryIdentificadoresRepo() {
        this.identificadores = new ArrayList<>();
    }

    @Override
    public Optional<Identificador> buscarPorId(String id) {
        return this.identificadores.stream().filter(identificador -> identificador.tieneID(id)).findFirst();
    }

    @Override
    public Identificador guardar(Identificador identificador) {
        identificador.setId(this.generarId());
        identificadores.add(identificador);

        return identificador;
    }

    private String generarId() {
        return String.valueOf(this.idSecuencia.getAndIncrement());
    }
}
