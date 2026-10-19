package co.edu.umb.smartstock.desktop.component;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** Componente: tarjeta con un número grande y su título (se usa en el dashboard). */
public class TarjetaIndicador extends VBox {

    public TarjetaIndicador(String titulo, String valor, boolean esAlerta) {
        getStyleClass().add("tarjeta-indicador");
        if (esAlerta) {
            getStyleClass().add("tarjeta-alerta");
        }
        setSpacing(4);

        Label etiquetaValor = new Label(valor);
        etiquetaValor.getStyleClass().add("indicador-valor");
        Label etiquetaTitulo = new Label(titulo);
        etiquetaTitulo.getStyleClass().add("indicador-titulo");

        getChildren().addAll(etiquetaValor, etiquetaTitulo);
    }
}
