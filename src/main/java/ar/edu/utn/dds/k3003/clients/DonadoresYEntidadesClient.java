package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.BooleanDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import ar.edu.utn.dds.k3003.exceptions.FalloServicioExternoException;
import ar.edu.utn.dds.k3003.exceptions.PeticionExternaInvalidaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class DonadoresYEntidadesClient implements FachadaDonadoresYEntidades {

    @Value("${url.donadores}")
    private String urlBase;

    @Override
    public DonadorDTO buscarDonadorPorID(String donadorID) throws NoSuchElementException {
        try {
            String url = this.urlBase + "/donadores/" + donadorID;

            return HttpClientBuilder.get(url, DonadorDTO.class);
        } catch (PeticionExternaInvalidaException e) {
            throw e;
        } catch (Exception e) {
            throw new FalloServicioExternoException("Error al buscar Donador por ID: " + donadorID, e);
        }
    }

    @Override
    public Boolean puedeDonar(String donadorID) throws NoSuchElementException
    {
        try {
            String url = this.urlBase + "/donadores/" + donadorID + "/puede-donar";
            BooleanDTO respuesta = HttpClientBuilder.get(url, BooleanDTO.class);
            return respuesta.puedeDonar();
        } catch (PeticionExternaInvalidaException e) {
            throw e;
        } catch (Exception e) {
            throw new FalloServicioExternoException("Error al buscar Donador por ID: " + donadorID, e);
        }
    }

    @Override
    public QuejaDTO agregarQueja(QuejaDTO quejaDTO) throws NoSuchElementException {
        try {
            String url = this.urlBase + "/donadores/" + quejaDTO.donadorID() + "/quejas";
            return HttpClientBuilder.post(url, quejaDTO,  QuejaDTO.class);
        } catch (PeticionExternaInvalidaException e) {
            throw e;
        } catch (Exception e) {
            throw new FalloServicioExternoException("Error al agregar queja", e);
        }
    }

    @Override
    public DonadorDTO agregarDonador(DonadorDTO donadorDTO) { return null; }

    @Override
    public EntidadBeneficaDTO agregarEntidad(EntidadBeneficaDTO entidadBeneficaDTO) { return null; }

    @Override
    public EntidadBeneficaDTO buscarEntidadPorID(String entidadID) throws NoSuchElementException { return null; }

    @Override
    public NecesidadMaterialDTO registrarNecesidad(NecesidadMaterialDTO necesidadMaterialDTO) { return null; }


    @Override
    public List<QuejaDTO> obtenerQuejasDe(String donadorID) throws NoSuchElementException { return null; }

    @Override
    public DonadorDTO modificarEstado(String donadorID, EstadoDonadorEnum estado) throws NoSuchElementException { return null; }

    @Override
    public DonadorDTO modifcarCategoria(String donadorID, String categoria) throws NoSuchElementException { return null; }

    @Override
    public List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(String productoSolicitadoID) { return null; }

    @Override
    public NecesidadMaterialDTO satisfacerNecesidad(String necesidadID, Integer cantidad) throws NoSuchElementException { return null; }

    @Override
    public DonadorStatsDTO estadisticasDonador(String donadorID) { return null; }

    @Override
    public void setFachadaIncentivos(FachadaIncentivos fachadaIncentivos) {}

}
