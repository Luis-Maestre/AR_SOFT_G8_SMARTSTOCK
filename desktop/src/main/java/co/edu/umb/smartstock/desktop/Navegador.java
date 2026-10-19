package co.edu.umb.smartstock.desktop;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navegador {

    private static Stage ventana;

    public static void iniciar(Stage v) {
        ventana = v;
    }

    public static void irA(String pantalla) {
        try {
            Parent raiz = FXMLLoader.load(Navegador.class.getResource("/views/" + pantalla + ".fxml"));
            Scene escena = new Scene(raiz, 1100, 680);
            escena.getStylesheets().add(Navegador.class.getResource("/css/estilos.css").toExternalForm());
            ventana.setScene(escena);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo abrir la pantalla: " + pantalla, e);
        }
    }
}
