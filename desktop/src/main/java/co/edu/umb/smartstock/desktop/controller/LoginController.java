package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.Navegador;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField correo;
    @FXML private PasswordField clave;
    @FXML private Label mensaje;

    @FXML
    private void ingresar() {
        if (correo.getText().isBlank() || clave.getText().isBlank()) {
            mensaje.setText("Escribe tu correo y tu contraseña.");
            return;
        }
        // Fase 2: modo demostracion, acepta cualquier dato.
        Navegador.irA("productos");
    }
}
