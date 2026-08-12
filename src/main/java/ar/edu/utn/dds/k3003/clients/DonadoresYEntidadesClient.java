package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.BooleanDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import ar.edu.utn.dds.k3003.exceptions.FalloServicioExternoException;
import ar.edu.utn.dds.k3003.exceptions.PeticionExternaInvalidaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class DonadoresYEntidadesClient implements FachadaDonadoresYEntidades {

    private final RestClient restClient;

    public DonadoresYEntidadesClient(
            @Value("${url.donadores}") String urlBase,
            RestClient.Builder restClientBuilder) {

        this.restClient = restClientBuilder
                .baseUrl(urlBase)
                .defaultStatusHandler(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND), (request, response) -> {
                    throw new NoSuchElementException(
                            String.format("Donadores y Entidades: Recurso no encontrado. Petición: %s %s",
                                    request.getMethod(), request.getURI())
                    );
                })

                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new PeticionExternaInvalidaException(
                            String.format("Donadores y Entidades: Petición rechazada (Estado %d). Petición: %s %s",
                                    response.getStatusCode().value(), request.getMethod(), request.getURI()),
                            response.getStatusCode().value()
                    );
                })

                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new FalloServicioExternoException(
                            String.format("Donadores y Entidades: Fallo interno del servicio externo (Estado %d). Petición: %s %s",
                                    response.getStatusCode().value(), request.getMethod(), request.getURI()),
                            null
                    );
                })
                .build();
    }

    @Override
    public DonadorDTO buscarDonadorPorID(String donadorID) throws NoSuchElementException {
        return this.restClient.get()
                .uri("/donadores/{id}", donadorID)
                .retrieve()
                .body(DonadorDTO.class);
    }

    @Override
    public Boolean puedeDonar(String donadorID) throws NoSuchElementException {
        BooleanDTO respuesta = this.restClient.get()
                .uri("/donadores/{id}/puede-donar", donadorID)
                .retrieve()
                .body(BooleanDTO.class);
        return respuesta != null ? respuesta.puedeDonar() : false;
    }

    @Override
    public QuejaDTO agregarQueja(QuejaDTO quejaDTO) throws NoSuchElementException {

        record QuejaRequest(String donacionID, String descripcion) {}

        QuejaRequest requestBody = new QuejaRequest(quejaDTO.donacionID(), quejaDTO.descripcion());

        return this.restClient.post()
                .uri("/donadores/{id}/quejas", quejaDTO.donadorID())
                .body(requestBody)
                .retrieve()
                .body(QuejaDTO.class);
    }

    @Override
    public DonadorDTO agregarDonador(DonadorDTO donadorDTO) {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public EntidadBeneficaDTO agregarEntidad(EntidadBeneficaDTO entidadBeneficaDTO) {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public EntidadBeneficaDTO buscarEntidadPorID(String entidadID) throws NoSuchElementException {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public NecesidadMaterialDTO registrarNecesidad(NecesidadMaterialDTO necesidadMaterialDTO) {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public List<QuejaDTO> obtenerQuejasDe(String donadorID) throws NoSuchElementException {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public DonadorDTO modificarEstado(String donadorID, EstadoDonadorEnum estado) throws NoSuchElementException {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public DonadorDTO modifcarCategoria(String donadorID, String categoria) throws NoSuchElementException {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(String productoSolicitadoID) {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public NecesidadMaterialDTO satisfacerNecesidad(String necesidadID, Integer cantidad) throws NoSuchElementException {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public DonadorStatsDTO estadisticasDonador(String donadorID) {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public void setFachadaIncentivos(FachadaIncentivos fachadaIncentivos) {
    }
}