package co.edu.umb.smartstock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class FlujoApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private String token() throws Exception {
        String cuerpo = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@smartstock.com\",\"password\":\"Admin123*\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(cuerpo).get("token").asText();
    }

    @Test
    void sinTokenDevuelve401() throws Exception {
        mvc.perform(get("/api/productos")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginConClaveIncorrectaDevuelve401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@smartstock.com\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crudProductoYMovimientos() throws Exception {
        String auth = "Bearer " + token();

        String creado = mvc.perform(post("/api/productos").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Producto de prueba\",\"codigo\":\"TST-001\",\"precio\":1500,"
                                + "\"stockMinimo\":5,\"cantidadInicial\":10,\"ubicacion\":\"Estante T\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cantidadActual").value(10))
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(creado).get("idProducto").asLong();

        // codigo duplicado -> 409
        mvc.perform(post("/api/productos").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Otro\",\"codigo\":\"TST-001\",\"stockMinimo\":1}"))
                .andExpect(status().isConflict());

        // salida mayor que el stock -> 409 y no cambia nada
        mvc.perform(post("/api/movimientos").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idProducto\":" + id + ",\"tipoMovimiento\":\"salida\",\"cantidad\":11}"))
                .andExpect(status().isConflict());

        // salida valida deja 4 (<= minimo 5) y genera alerta
        mvc.perform(post("/api/movimientos").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idProducto\":" + id + ",\"tipoMovimiento\":\"salida\",\"cantidad\":6,\"motivo\":\"Venta\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.existenciaResultante").value(4));
        mvc.perform(get("/api/alertas").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.idProducto==" + id + ")]").exists());

        // la entrada recupera el stock y la alerta se resuelve sola
        mvc.perform(post("/api/movimientos").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idProducto\":" + id + ",\"tipoMovimiento\":\"entrada\",\"cantidad\":20}"))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/alertas").header("Authorization", auth))
                .andExpect(jsonPath("$[?(@.idProducto==" + id + ")]").isEmpty());

        // baja logica: ya no aparece en el listado normal
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/productos/" + id).header("Authorization", auth))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/productos").header("Authorization", auth).param("q", "TST-001"))
                .andExpect(jsonPath("$").isEmpty());
    }
}
