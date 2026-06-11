package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.AsignacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.DepositoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.PaqueteDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.TipoAlgoritmoEnum;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class LogisticaClient implements FachadaLogistica {

//    @Value("https://logistica-hjaw.onrender.com/")
    private String urlBase= "https://logistica-hjaw.onrender.com";

    @Override
    public DepositoDTO gestionarDonacion(String depositoID, String donacionID, String productoID, Integer cantidad) throws NoSuchElementException {
        try {
            String url =  urlBase + "/depositos/" + depositoID + "/donacion";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("donacionID", donacionID);
            requestBody.put("productoID", productoID);
            requestBody.put("cantidad", cantidad);

            return HttpClientBuilder.post(url, requestBody, DepositoDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al gestionar donacion con ID " + donacionID, e);
        }
    }

    @Override
    public DepositoDTO agregarDeposito(DepositoDTO deposito) { return null; }

    @Override
    public DepositoDTO buscarDepositoPorID(String depositoID) throws NoSuchElementException { return null; }

    @Override
    public AsignacionDTO buscarAsignacionPorPaqueteID(String paqueteID) throws NoSuchElementException { return null; }

    @Override
    public void setAlgoritmoMM(String depositoID, TipoAlgoritmoEnum tipoAlgoritmo) {}

    @Override
    public AsignacionDTO ejecutarMatchmaking(String depositoID, PaqueteDTO paqueteDTO, List<NecesidadMaterialDTO> necesidades) { return null; }

    @Override
    public void reportarEntrega(PaqueteDTO paqueteDTO) {}

    @Override
    public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {}

    @Override
    public void setFachadaDonaciones(FachadaDonaciones fachadaDonaciones) {}
}
