package co.edu.umb.smartstock.desktop.component;

import java.util.function.Consumer;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Componente: menú lateral de navegación.
 * No sabe cómo se abre cada pantalla; solo avisa cuál eligió el usuario
 * (el controlador principal se encarga de mostrarla).
 */
public class MenuLateral extends VBox {

    /** {texto del botón, nombre del archivo .fxml en /views}. */
    private static final String[][] OPCIONES = {
        {"Dashboard", "dashboard"},
        {"Productos", "productos"},
        {"Inventario", "inventario"},
        {"Movimientos", "movimientos"},
        {"Reposiciones", "reposiciones"},
        {"Reportes", "reportes"},
        {"Usuarios", "usuarios"}
    };

    private final Consumer<String> alElegir;

    public MenuLateral(Consumer<String> alElegir) {
        this.alElegir = alElegir;
        getStyleClass().add("menu");
        setSpacing(4);
        setPrefWidth(190);

        for (String[] opcion : OPCIONES) {
            Button boton = new Button(opcion[0]);
            boton.setUserData(opcion[1]);
            boton.setMaxWidth(Double.MAX_VALUE);
            boton.getStyleClass().add("menu-boton");
            boton.setOnAction(e -> seleccionar(opcion[1]));
            getChildren().add(boton);
        }
    }

    /** Marca como activo el botón de esa pantalla y avisa que se debe abrir. */
    public void seleccionar(String pantalla) {
        for (var nodo : getChildren()) {
            nodo.getStyleClass().remove("activo");
            if (pantalla.equals(nodo.getUserData())) {
                nodo.getStyleClass().add("activo");
            }
        }
        alElegir.accept(pantalla);
    }
}
