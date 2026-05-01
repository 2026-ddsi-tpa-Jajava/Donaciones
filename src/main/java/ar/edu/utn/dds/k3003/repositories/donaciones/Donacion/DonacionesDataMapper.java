package ar.edu.utn.dds.k3003.repositories.donaciones.Donacion;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.model.donaciones.Donacion;
import ar.edu.utn.dds.k3003.model.donaciones.Producto;

public class DonacionesDataMapper {
    public Donacion toDonacion(DonacionDTO donacionDTO, Producto producto){
        return new Donacion(
                donacionDTO.donadorID(),
                donacionDTO.depositoID(),
                donacionDTO.descripcion(),
                producto,
                donacionDTO.cantidad());
    }

    public DonacionDTO toDonacionDTO(Donacion donacion){
        return new DonacionDTO(
                donacion.getId(),
                donacion.getDonadorID(),
                donacion.getDepositoID(),
                donacion.getDescripcion(),
                donacion.getProducto().getId(),
                donacion.getCantidad(),
                donacion.getEstado());
    }
}
