package ar.edu.utn.dds.k3003.repositories.donaciones.Donacion;

import ar.edu.utn.dds.k3003.model.donaciones.Donacion;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryDonacionesRepo implements DonacionesRepository {

    private List<Donacion> donaciones;
    private AtomicLong idSecuencia;

    public InMemoryDonacionesRepo() {
        this.donaciones = new ArrayList<>();
        this.idSecuencia = new AtomicLong(1);
    }

    @Override
    public Optional<Donacion> buscarPorId(String id) {
        return this.donaciones.stream().filter(donacion -> donacion.tieneID(id)).findFirst();
    }

    @Override
    public Donacion guardar(Donacion donacion) {
        donacion.setId(this.generarId());
        this.donaciones.add(donacion);

        return donacion;
    }

    private String generarId() {
        return String.valueOf(this.idSecuencia.getAndIncrement());
    }

    @Override
    public  List<Donacion> buscarPorIDyFecha(String donadorID, LocalDate fecha) {
        return this.donaciones.stream().filter(donacion -> donacion.donadorConID(donadorID) && donacion.aPartirDeFecha(fecha)).collect(Collectors.toList());
    }

    @Override
    public void actualizar(Donacion donacion) {
    }
}
