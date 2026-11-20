package co.edu.umb.smartstock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

<<<<<<< HEAD
=======
/** HealthController — permite comprobar que el servidor esta encendido. */
>>>>>>> d73906b8395de950b3b26a93762371c581175868
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, String> estado() {
        return Map.of("estado", "ok", "servicio", "smartstock-backend");
    }
}
