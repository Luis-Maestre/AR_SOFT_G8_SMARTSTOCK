package co.edu.umb.smartstock.service;

import co.edu.umb.smartstock.exception.ApiException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class PrediccionClient {
    private static final Logger log = LoggerFactory.getLogger(PrediccionClient.class);

    public record Venta(String fecha, int cantidad) { }

    public record ProductoEntrada(Long productoId, int existencia, int stockMinimo, List<Venta> historial) { }

    public record Peticion(List<ProductoEntrada> productos, int horizonteDias, String fechaReferencia) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Prediccion(Long productoId, String modelo, double demandaDiaria, double demandaHorizonte,
                             int cantidadSugerida, Double mae, int diasHistorial, int horizonteDias, String motivo) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Respuesta(List<Prediccion> predicciones) { }

    private final RestClient cliente;
    private final String apiKey;

    public PrediccionClient(@Value("${smartstock.prediccion.url}") String url,
                            @Value("${smartstock.prediccion.api-key:}") String apiKey) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(3000);
        fabrica.setReadTimeout(30000);
        this.cliente = RestClient.builder().baseUrl(url).requestFactory(fabrica).build();
        this.apiKey = apiKey == null ? "" : apiKey;
    }

    public List<Prediccion> predecir(Peticion peticion) {
        try {
            Respuesta r = cliente.post().uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> {
                        if (!apiKey.isBlank()) {
                            h.set("X-API-Key", apiKey);
                        }
                    })
                    .body(peticion)
                    .retrieve()
                    .body(Respuesta.class);
            if (r == null || r.predicciones() == null) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "El servicio de predicción respondió sin datos");
            }
            return r.predicciones();
        } catch (RestClientException e) {
            log.warn("Servicio de prediccion no disponible: {}", e.getMessage());
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio de predicción no está disponible. Inícialo e intenta de nuevo.");
        }
    }
}
