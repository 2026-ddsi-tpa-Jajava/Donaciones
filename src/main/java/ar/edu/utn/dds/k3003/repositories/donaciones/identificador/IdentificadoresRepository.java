package ar.edu.utn.dds.k3003.repositories.donaciones.identificador;

import ar.edu.utn.dds.k3003.model.donaciones.Donacion;
import ar.edu.utn.dds.k3003.model.donaciones.Identificador;

import java.util.List;
import java.util.Optional;

public interface IdentificadoresRepository {

    Optional<Identificador> buscarPorId(String id);

    Identificador guardar (Identificador  identificador);

    List<Identificador> buscarTodos();

    void eliminarPorID(String id);
}
