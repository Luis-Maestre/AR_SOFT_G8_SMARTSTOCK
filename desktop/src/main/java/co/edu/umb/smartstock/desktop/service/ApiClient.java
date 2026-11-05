package co.edu.umb.smartstock.desktop.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/** Se comunica con el servidor (backend), envia el token de la sesion y entrega los datos a las pantallas. */
public class ApiClient {

    /** Error devuelto por el servidor (con el mensaje listo para mostrar al usuario). */
    public static class ApiError extends IOException {
        private final int status;
        public ApiError(int status, String mensaje) { super(mensaje); this.status = status; }
        public int getStatus() { return status; }
    }

    public static final ApiClient INSTANCIA = new ApiClient();

    private static final ObjectMapper JSON = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final String urlBase;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    public ApiClient() {
        this(urlPorDefecto());
    }

    public ApiClient(String urlBase) {
        this.urlBase = urlBase.endsWith("/") ? urlBase.substring(0, urlBase.length() - 1) : urlBase;
    }

    /** Se puede cambiar con -Dsmartstock.api=... o con la variable SMARTSTOCK_API_URL. */
    private static String urlPorDefecto() {
        String propiedad = System.getProperty("smartstock.api");
        if (propiedad != null && !propiedad.isBlank()) return propiedad;
        String entorno = System.getenv("SMARTSTOCK_API_URL");
        if (entorno != null && !entorno.isBlank()) return entorno;
        return "http://localhost:8080/api";
    }

    public static String codificar(String texto) {
        return URLEncoder.encode(texto == null ? "" : texto, StandardCharsets.UTF_8);
    }

    // ---------- operaciones genericas ----------

    public <T> T obtener(String ruta, Class<T> tipo) throws IOException, InterruptedException {
        return JSON.readValue(ejecutar("GET", ruta, null, "application/json"), tipo);
    }

    public <T> List<T> lista(String ruta, Class<T> tipo) throws IOException, InterruptedException {
        byte[] cuerpo = ejecutar("GET", ruta, null, "application/json");
        return JSON.readValue(cuerpo, JSON.getTypeFactory().constructCollectionType(List.class, tipo));
    }

    public <T> T enviar(String metodo, String ruta, Object cuerpo, Class<T> tipo) throws IOException, InterruptedException {
        byte[] respuesta = ejecutar(metodo, ruta, cuerpo, "application/json");
        return JSON.readValue(respuesta, tipo);
    }

    public <T> List<T> enviarLista(String metodo, String ruta, Object cuerpo, Class<T> tipo) throws IOException, InterruptedException {
        byte[] respuesta = ejecutar(metodo, ruta, cuerpo, "application/json");
        return JSON.readValue(respuesta, JSON.getTypeFactory().constructCollectionType(List.class, tipo));
    }

    /** Para respuestas sin contenido (DELETE, cambio de contraseña). */
    public void enviarSinRespuesta(String metodo, String ruta, Object cuerpo) throws IOException, InterruptedException {
        ejecutar(metodo, ruta, cuerpo, "application/json");
    }

    /** Descarga un archivo (por ejemplo un CSV). */
    public byte[] descargar(String ruta) throws IOException, InterruptedException {
        return ejecutar("GET", ruta, null, "*/*");
    }

    // ---------- acceso a pantallas ya conocidas ----------

    public static <T> T leer(byte[] json, Class<T> tipo) throws IOException {
        return JSON.readValue(json, tipo);
    }

    // ---------- nucleo ----------

    private byte[] ejecutar(String metodo, String ruta, Object cuerpo, String accept) throws IOException, InterruptedException {
        HttpRequest.Builder pedido = HttpRequest.newBuilder(URI.create(urlBase + ruta))
                .timeout(Duration.ofSeconds(40))
                .header("Accept", accept);
        if (Sesion.activa()) {
            pedido.header("Authorization", "Bearer " + Sesion.token());
        }
        if (cuerpo == null) {
            pedido.method(metodo, HttpRequest.BodyPublishers.noBody());
        } else {
            pedido.header("Content-Type", "application/json");
            pedido.method(metodo, HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(cuerpo)));
        }
        HttpResponse<byte[]> r = http.send(pedido.build(), HttpResponse.BodyHandlers.ofByteArray());
        int codigo = r.statusCode();
        if (codigo >= 200 && codigo < 300) {
            return r.body();
        }
        throw new ApiError(codigo, mensajeDeError(codigo, r.body(), ruta));
    }

    private static String mensajeDeError(int codigo, byte[] cuerpo, String ruta) {
        try {
            JsonNode nodo = JSON.readTree(cuerpo);
            if (nodo != null && nodo.hasNonNull("mensaje")) {
                return nodo.get("mensaje").asText();
            }
        } catch (IOException e) {
            // el cuerpo no era JSON: se usa el mensaje generico
        }
        if (codigo == 401) {
            return ruta.startsWith("/auth/login") ? "Correo o contraseña incorrectos"
                    : "Tu sesión expiró. Cierra sesión e ingresa de nuevo.";
        }
        if (codigo == 403) return "No tienes permiso para realizar esta acción";
        return "El servidor respondió con el código " + codigo;
    }
}
