package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Identificador")
public class Identificador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoIdentificadorEnum tipo;

    public Identificador() {
    }

    public Identificador(String descripcion, TipoIdentificadorEnum tipo) {
        this.descripcion = descripcion;
        this.tipo = tipo;
    }
}
