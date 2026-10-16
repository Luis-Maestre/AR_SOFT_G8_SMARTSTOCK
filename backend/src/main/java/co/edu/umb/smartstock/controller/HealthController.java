package co.edu.umb.smartstock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/** HealthController — permite comprobar que el servidor esta encendido. */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, String> estado() {
        return Map.of("estado", "ok", "servicio", "smartstock-backend");
    }
}
