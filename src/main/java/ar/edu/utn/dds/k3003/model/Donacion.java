package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.exceptions.donaciones.CambioEstadoInvalidoException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "Donacion")
public class Donacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "donador_id")
    private String donadorID;
    @Column(name = "deposito_id")
    private String depositoID;
    @Column(length = 500)
    private String descripcion;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDonacionEnum estado;
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    @Column(nullable = false)
    private Integer cantidad;
    @Column(nullable = false)
    private LocalDate fecha;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "donacion_id", nullable = false)
    private List<HistorialEstado> historialEstados;

    @ElementCollection
    @CollectionTable(name = "donacion_quejas", joinColumns = @JoinColumn(name = "donacion_id"))
    @Column(name = "queja")
    private List<String> quejas;

    public Donacion() {
    }

    public Donacion(String donadorID, String depositoID, String descripcion, Producto producto, Integer cantidad) {
        this.donadorID = donadorID;
        this.depositoID = depositoID;
        this.descripcion = descripcion;
        this.estado = EstadoDonacionEnum.INGRESADA;
        this.producto = producto;
        this.cantidad = cantidad;
        this.fecha = LocalDate.now();
        this.historialEstados = new ArrayList<>();
        this.historialEstados.add(new HistorialEstado(this.estado, this.fecha));
        this.quejas = new ArrayList<>();
    }

    public void agregarQueja(String descripcion) {

        this.quejas.add(descripcion);
        this.cambiarEstado(EstadoDonacionEnum.CONQUEJA);
    }

    public void retirarQueja(String descripcion) {
        this.quejas.remove(descripcion);
        this.setEstado(EstadoDonacionEnum.ACEPTADA);
        if (!this.historialEstados.isEmpty()) {
            this.historialEstados.removeLast();
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
}