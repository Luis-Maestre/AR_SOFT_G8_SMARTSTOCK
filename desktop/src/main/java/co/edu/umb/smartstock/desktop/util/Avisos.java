package co.edu.umb.smartstock.desktop.util;

import javafx.scene.control.Label;

/** Muestra mensajes de error (rojo) o de éxito (verde) en una etiqueta. */
public final class Avisos {

    private Avisos() { }

    public static void error(Label etiqueta, String texto) {
        etiqueta.getStyleClass().setAll("label", "error");
        etiqueta.setText(texto);
    }

    public static void exito(Label etiqueta, String texto) {
        etiqueta.getStyleClass().setAll("label", "exito");
        etiqueta.setText(texto);
    }
}
