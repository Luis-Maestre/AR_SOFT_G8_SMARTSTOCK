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

    public static void info(Label etiqueta, String texto) {
        etiqueta.getStyleClass().setAll("label", "aviso");
        etiqueta.setText(texto);
    }

    /** Muestra una ventana de confirmacion; devuelve true si el usuario acepta. */
    public static boolean confirmar(String texto) {
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION, texto,
                javafx.scene.control.ButtonType.OK, javafx.scene.control.ButtonType.CANCEL);
        a.setHeaderText(null);
        return a.showAndWait().filter(b -> b == javafx.scene.control.ButtonType.OK).isPresent();
    }
}
