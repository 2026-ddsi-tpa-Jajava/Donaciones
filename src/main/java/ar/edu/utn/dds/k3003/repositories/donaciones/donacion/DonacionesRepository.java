package ar.edu.utn.dds.k3003.repositories.donaciones.donacion;

import ar.edu.utn.dds.k3003.model.donaciones.Donacion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface DonacionesRepository {

    Optional<Donacion> buscarPorId(String id);

    Donacion guardar (Donacion donacion);

    List<Donacion> buscarPorIDyFecha(String donadorID, LocalDate fecha);

    void actualizar(Donacion donacion);

    List<Donacion> buscarTodas();

    void eliminarPorId(String id);
}
