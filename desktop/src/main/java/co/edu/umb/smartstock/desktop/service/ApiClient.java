package co.edu.umb.smartstock.desktop.service;

import co.edu.umb.smartstock.desktop.model.LoteDto;
import co.edu.umb.smartstock.desktop.model.ProductoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/** Se comunica con el servidor (backend) y entrega los datos a las pantallas. */
public class ApiClient {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final String urlBase;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    public ApiClient() {
        this("http://localhost:8080/api");
    }

    public ApiClient(String urlBase) {
        this.urlBase = urlBase;
    }

    public List<ProductoDto> productos() throws IOException, InterruptedException {
        return pedirLista("/productos", ProductoDto.class);
    }

    /** Lotes con existencias, ordenados del que vence primero al que vence despues. */
    public List<LoteDto> lotesFefo() throws IOException, InterruptedException {
        return pedirLista("/inventario/lotes-fefo", LoteDto.class);
    }

    private <T> List<T> pedirLista(String ruta, Class<T> tipo) throws IOException, InterruptedException {
        HttpRequest pedido = HttpRequest.newBuilder(URI.create(urlBase + ruta))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        HttpResponse<String> respuesta = http.send(pedido, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() != 200) {
            throw new IOException("El servidor respondió con el código " + respuesta.statusCode());
        }
        return leerLista(respuesta.body(), tipo);
    }

    /** Convierte el texto JSON recibido en una lista de objetos. */
    static <T> List<T> leerLista(String json, Class<T> tipo) throws IOException {
        return JSON.readValue(json, JSON.getTypeFactory().constructCollectionType(List.class, tipo));
    }
}
