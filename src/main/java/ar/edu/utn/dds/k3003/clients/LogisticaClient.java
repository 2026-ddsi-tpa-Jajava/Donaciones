package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.AsignacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.DepositoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.PaqueteDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.TipoAlgoritmoEnum;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import ar.edu.utn.dds.k3003.exceptions.FalloServicioExternoException;
import ar.edu.utn.dds.k3003.exceptions.PeticionExternaInvalidaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class LogisticaClient implements FachadaLogistica {

    private final RestClient restClient;

    public LogisticaClient(
            @Value("${url.logistica}") String urlBase,
            RestClient.Builder restClientBuilder) {

        this.restClient = restClientBuilder
                .baseUrl(urlBase)
                .defaultStatusHandler(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND), (request, response) -> {
                    throw new NoSuchElementException(
                            String.format("Logistica: Recurso no encontrado. Petición: %s %s",
                                    request.getMethod(), request.getURI())
                    );
                })

                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new PeticionExternaInvalidaException(
                            String.format("Logistica: Petición rechazada (Estado %d). Petición: %s %s",
                                    response.getStatusCode().value(), request.getMethod(), request.getURI()),
                            response.getStatusCode().value()
                    );
                })

                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new FalloServicioExternoException(
                            String.format("Logistica: Fallo interno del servicio externo (Estado %d). Petición: %s %s",
                                    response.getStatusCode().value(), request.getMethod(), request.getURI()),
                            null
                    );
                })
                .build();
    }

    @Override
    public DepositoDTO gestionarDonacion(String depositoID, String donacionID, String productoID, Integer cantidad) throws NoSuchElementException {


        PaqueteDTO requestBody = new PaqueteDTO(null, donacionID, productoID, cantidad);

        return this.restClient.post()
                .uri("/depositos/{depositoID}/donacion", depositoID)
                .body(requestBody)
                .retrieve()
                .body(DepositoDTO.class);
    }

    @Override
    public DepositoDTO agregarDeposito(DepositoDTO deposito) {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public DepositoDTO buscarDepositoPorID(String depositoID) throws NoSuchElementException {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public AsignacionDTO buscarAsignacionPorPaqueteID(String paqueteID) throws NoSuchElementException {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public void setAlgoritmoMM(String depositoID, TipoAlgoritmoEnum tipoAlgoritmo) {
    }

    @Override
    public AsignacionDTO ejecutarMatchmaking(String depositoID, PaqueteDTO paqueteDTO, List<NecesidadMaterialDTO> necesidades) {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public void reportarEntrega(PaqueteDTO paqueteDTO) {
        throw new UnsupportedOperationException("Operación no implementada en este cliente");
    }

    @Override
    public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {
    }

    @Override
    public void setFachadaDonaciones(FachadaDonaciones fachadaDonaciones) {
    }
}