package ar.edu.utn.dds.k3003.mappers;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.model.Donacion;

public class DonacionesDataMapper {

    public DonacionDTO toDonacionDTO(Donacion donacion){
        return new DonacionDTO(
                donacion.getId().toString(),
                donacion.getDonadorID(),
                donacion.getDepositoID(),
                donacion.getDescripcion(),
                donacion.getProducto().getId().toString(),
                donacion.getCantidad(),
                donacion.getEstado()
        );
    }
}
