package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.Navegador;
import co.edu.umb.smartstock.desktop.model.Api;
import co.edu.umb.smartstock.desktop.service.ApiClient;
import co.edu.umb.smartstock.desktop.service.Sesion;
import co.edu.umb.smartstock.desktop.util.Avisos;
import co.edu.umb.smartstock.desktop.util.Tareas;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/** RF01: inicio de sesión real contra el servidor. */
public class LoginController {

    @FXML private TextField correo;
    @FXML private PasswordField clave;
    @FXML private Label mensaje;
    @FXML private Button botonIngresar;

    @FXML
    private void ingresar() {
        String c = correo.getText().trim();
        String p = clave.getText();
        if (c.isEmpty() || p.isEmpty()) {
            Avisos.error(mensaje, "Escribe tu correo y tu contraseña.");
            return;
        }
        botonIngresar.setDisable(true);
        Avisos.info(mensaje, "Verificando...");
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("correo", c);
        cuerpo.put("password", p);
        Tareas.ejecutar(
                () -> ApiClient.INSTANCIA.enviar("POST", "/auth/login", cuerpo, Api.LoginResponse.class),
                r -> {
                    Sesion.iniciar(r.token(), r.usuario());
                    clave.clear();
                    Navegador.irA("principal");
                },
                error -> {
                    botonIngresar.setDisable(false);
                    Avisos.error(mensaje, error);
                });
    }
}
