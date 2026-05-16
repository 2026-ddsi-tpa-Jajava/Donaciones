package ar.edu.utn.dds.k3003.repositories.donaciones.identificador;

import ar.edu.utn.dds.k3003.exceptions.donaciones.IdentificadorNoEncontradoException;
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
        if(identificador.getId() == null){
            identificador.setId(this.generarId());
            identificadores.add(identificador);
        } else {
            this.identificadores.removeIf(i -> i.tieneID(identificador.getId()));
            this.identificadores.add(identificador);
        }


        return identificador;
    }

    @Override
    public List<Identificador> buscarTodos() {
        return new ArrayList<>(this.identificadores);
    }

    @Override
    public void eliminarPorID(String id) {
        boolean eliminado = this.identificadores.removeIf(i -> i.tieneID(id));

        if(eliminado){
            throw new IdentificadorNoEncontradoException("No se encontró el identificador con ID: " + id);
        }
    }

    private String generarId() {
        return String.valueOf(this.idSecuencia.getAndIncrement());
    }
}
