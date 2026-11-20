package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.Navegador;
import co.edu.umb.smartstock.desktop.component.MenuLateral;
<<<<<<< HEAD
import co.edu.umb.smartstock.desktop.service.Sesion;
=======
>>>>>>> d73906b8395de950b3b26a93762371c581175868
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
<<<<<<< HEAD
import javafx.scene.control.Label;
=======
>>>>>>> d73906b8395de950b3b26a93762371c581175868
import javafx.scene.layout.BorderPane;

/**
 * Marco de la aplicación: barra superior + menú lateral + zona central.
 * La zona central cambia según el botón del menú que se presione.
 */
public class PrincipalController {

    @FXML private BorderPane marco;
<<<<<<< HEAD
    @FXML private Label etiquetaSesion;

    @FXML
    private void initialize() {
        if (Sesion.usuario() != null) {
            etiquetaSesion.setText(Sesion.usuario().nombreCompleto() + " (" + Sesion.usuario().rol().replace('_', ' ') + ")");
        }
=======

    @FXML
    private void initialize() {
>>>>>>> d73906b8395de950b3b26a93762371c581175868
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
<<<<<<< HEAD
        Sesion.cerrar();
=======
>>>>>>> d73906b8395de950b3b26a93762371c581175868
        Navegador.irA("login");
    }
}
