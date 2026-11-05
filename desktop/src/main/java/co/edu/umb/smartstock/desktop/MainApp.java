package co.edu.umb.smartstock.desktop;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage ventana) {
        ventana.setTitle("SmartStock ERP");
        Navegador.iniciar(ventana);
        Navegador.irA("login");
        ventana.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
