package co.edu.umb.smartstock.desktop.component;

import co.edu.umb.smartstock.desktop.model.Api;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Componente: ventana para crear o editar un producto.
 * Devuelve el cuerpo JSON (como Map) listo para enviar al servidor, o vacío si se cancela.
 */
public final class FormularioProducto {

    private FormularioProducto() { }

    public static Optional<Map<String, Object>> mostrar(Api.Producto existente, List<Api.Categoria> categorias) {
        boolean esNuevo = existente == null;
        Dialog<Map<String, Object>> dialogo = new Dialog<>();
        dialogo.setTitle(esNuevo ? "Nuevo producto" : "Editar producto");
        ButtonType guardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(guardar, ButtonType.CANCEL);

        TextField nombre = new TextField(esNuevo ? "" : nz(existente.nombre()));
        TextField codigo = new TextField(esNuevo ? "" : nz(existente.codigo()));
        TextField descripcion = new TextField(esNuevo ? "" : nz(existente.descripcion()));
        TextField precio = new TextField(esNuevo || existente.precio() == null ? "" : existente.precio().toPlainString());
        TextField stockMinimo = new TextField(esNuevo ? "0" : String.valueOf(existente.stockMinimo()));
        TextField ubicacion = new TextField(esNuevo ? "" : nz(existente.ubicacion()));
        TextField cantidadInicial = new TextField("0");

        Api.Categoria sinCategoria = new Api.Categoria(null, "(sin categoría)", null, true);
        ComboBox<Api.Categoria> comboCategoria = new ComboBox<>();
        comboCategoria.getItems().add(sinCategoria);
        comboCategoria.getItems().addAll(categorias);
        comboCategoria.getSelectionModel().select(sinCategoria);
        if (!esNuevo && existente.idCategoria() != null) {
            for (Api.Categoria c : categorias) {
                if (existente.idCategoria().equals(c.idCategoria())) {
                    comboCategoria.getSelectionModel().select(c);
                }
            }
        }
        comboCategoria.setMaxWidth(Double.MAX_VALUE);

        Label error = new Label();
        error.getStyleClass().add("error");

        GridPane rejilla = new GridPane();
        rejilla.setHgap(10);
        rejilla.setVgap(8);
        rejilla.setPadding(new Insets(14));
        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints(280);
        c2.setHgrow(Priority.ALWAYS);
        rejilla.getColumnConstraints().addAll(c1, c2);
        int fila = 0;
        rejilla.addRow(fila++, new Label("Nombre *"), nombre);
        rejilla.addRow(fila++, new Label("Código"), codigo);
        rejilla.addRow(fila++, new Label("Descripción"), descripcion);
        rejilla.addRow(fila++, new Label("Categoría"), comboCategoria);
        rejilla.addRow(fila++, new Label("Precio"), precio);
        rejilla.addRow(fila++, new Label("Stock mínimo *"), stockMinimo);
        rejilla.addRow(fila++, new Label("Ubicación"), ubicacion);
        if (esNuevo) {
            rejilla.addRow(fila++, new Label("Cantidad inicial"), cantidadInicial);
        }
        rejilla.add(error, 0, fila, 2, 1);
        dialogo.getDialogPane().setContent(rejilla);

        // Validación: si algo está mal, la ventana no se cierra y se muestra el motivo.
        Button botonGuardar = (Button) dialogo.getDialogPane().lookupButton(guardar);
        botonGuardar.addEventFilter(ActionEvent.ACTION, ev -> {
            String problema = validar(nombre.getText(), precio.getText(), stockMinimo.getText(),
                    esNuevo ? cantidadInicial.getText() : "0");
            if (problema != null) {
                error.setText(problema);
                ev.consume();
            }
        });

        dialogo.setResultConverter(boton -> {
            if (boton != guardar) {
                return null;
            }
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("nombre", nombre.getText().trim());
            cuerpo.put("codigo", codigo.getText().trim());
            cuerpo.put("descripcion", descripcion.getText().trim());
            cuerpo.put("precio", precio.getText().isBlank() ? null : numero(precio.getText()));
            cuerpo.put("stockMinimo", Integer.parseInt(stockMinimo.getText().trim()));
            cuerpo.put("idCategoria", comboCategoria.getValue() == null ? null : comboCategoria.getValue().idCategoria());
            cuerpo.put("ubicacion", ubicacion.getText().trim());
            if (esNuevo) {
                cuerpo.put("cantidadInicial", Integer.parseInt(cantidadInicial.getText().trim()));
            }
            return cuerpo;
        });
        return dialogo.showAndWait();
    }

    private static String validar(String nombre, String precio, String minimo, String inicial) {
        if (nombre.isBlank()) return "El nombre es obligatorio.";
        if (!precio.isBlank()) {
            try {
                if (numero(precio).signum() < 0) return "El precio no puede ser negativo.";
            } catch (NumberFormatException e) {
                return "El precio debe ser un número (ej. 5200 o 5200.50).";
            }
        }
        try {
            if (Integer.parseInt(minimo.trim()) < 0) return "El stock mínimo no puede ser negativo.";
        } catch (NumberFormatException e) {
            return "El stock mínimo debe ser un número entero.";
        }
        try {
            if (Integer.parseInt(inicial.trim()) < 0) return "La cantidad inicial no puede ser negativa.";
        } catch (NumberFormatException e) {
            return "La cantidad inicial debe ser un número entero.";
        }
        return null;
    }

    private static BigDecimal numero(String texto) {
        return new BigDecimal(texto.trim().replace(',', '.'));
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
