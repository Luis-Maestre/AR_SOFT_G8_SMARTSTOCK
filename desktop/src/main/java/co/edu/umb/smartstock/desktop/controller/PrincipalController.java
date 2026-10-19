package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.Navegador;
import co.edu.umb.smartstock.desktop.component.MenuLateral;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

/**
 * Marco de la aplicación: barra superior + menú lateral + zona central.
 * La zona central cambia según el botón del menú que se presione.
 */
public class PrincipalController {

    @FXML private BorderPane marco;

    @FXML
    private void initialize() {
        MenuLateral menu = new MenuLateral(this::mostrar);
        marco.setLeft(menu);
        menu.seleccionar("dashboard"); // pantalla inicial
    }

    /** Carga /views/{pantalla}.fxml en el centro del marco. */
    private void mostrar(String pantalla) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource("/views/" + pantalla + ".fxml"));
            marco.setCenter(vista);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo abrir la pantalla: " + pantalla, e);
        }
    }

    @FXML
    private void cerrarSesion() {
        Navegador.irA("login");
    }
}
