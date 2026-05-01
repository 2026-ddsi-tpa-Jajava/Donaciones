package ar.edu.utn.dds.k3003.model.donaciones;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.exceptions.donaciones.CambioEstadoInvalidoException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Donacion {

    @Setter
    @Getter
    private String id;
    @Getter
    private String donadorID;
    @Getter
    private String depositoID;
    @Setter
    @Getter
    private String descripcion;
    @Getter
    private EstadoDonacionEnum estado;
    @Getter
    private Producto producto;
    @Getter
    private Integer cantidad;
    private LocalDate fecha;
    @Getter
    private List<HistorialEstado> historialEstados;

    public Donacion(String donadorID, String depositoID, String descripcion, Producto producto, Integer cantidad) {
        this.donadorID = donadorID;
        this.descripcion = descripcion;
        this.producto = producto;
        this.cantidad = cantidad;
        this.estado = EstadoDonacionEnum.INGRESADA;
        this.fecha = LocalDate.now();
        this.historialEstados = new ArrayList<>();
        this.historialEstados.add(new HistorialEstado(this.estado, this.fecha));
    }

    public void agregarQueja(String descripcion) {

        if(this.estado == EstadoDonacionEnum.INGRESADA){
            throw new RuntimeException("No se puede agregar una queja en este momento");
        }

        if(this.estado == EstadoDonacionEnum.ACEPTADA){
            this.setDescripcion(this.descripcion + ". Cuenta con la siguiente Queja: " + descripcion);
            this.cambiarEstado(EstadoDonacionEnum.CONQUEJA);
        }
    }

    public void cambiarEstado(EstadoDonacionEnum nuevoEstado) {
        if(!this.cambioValido(nuevoEstado)) {
            throw new CambioEstadoInvalidoException("No se puede cambiar a el estado solicitado");
        }

        this.estado = nuevoEstado;
        this.historialEstados.add(new HistorialEstado(nuevoEstado, LocalDate.now()));
    }

    private boolean cambioValido(EstadoDonacionEnum nuevoEstado) {
        return switch (this.estado) {
            case INGRESADA ->
                    nuevoEstado == EstadoDonacionEnum.ACEPTADA;

            case ACEPTADA ->
                    nuevoEstado == EstadoDonacionEnum.CONQUEJA;

            case CONQUEJA ->
                    false;
        };
    }

    public boolean tieneID(String id) {
        return this.getId().equals(id);
    }

    public boolean donadorConID(String donadorID){
        return this.getDonadorID().equals(donadorID);
    }

    public boolean aPartirDeFecha(LocalDate fecha){
        return !this.fecha.isBefore(fecha);
    }
}