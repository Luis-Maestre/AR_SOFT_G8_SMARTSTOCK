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
<<<<<<< HEAD
            double ancho = ventana.getScene() == null ? 1180 : ventana.getScene().getWidth();
            double alto = ventana.getScene() == null ? 720 : ventana.getScene().getHeight();
            Scene escena = new Scene(raiz, ancho, alto);
=======
            Scene escena = new Scene(raiz, 1100, 680);
>>>>>>> d73906b8395de950b3b26a93762371c581175868
            escena.getStylesheets().add(Navegador.class.getResource("/css/estilos.css").toExternalForm());
            ventana.setScene(escena);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo abrir la pantalla: " + pantalla, e);
        }
    }
}
