package co.edu.umb.smartstock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de arranque de SmartStock ERP.
 * Fase 2: esqueleto del backend, sin conexion real a base de datos
 * configurada todavia (ver application.properties).
 */
@SpringBootApplication
public class SmartStockApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartStockApplication.class, args);
    }
}
