package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.model.InventarioFila;
import co.edu.umb.smartstock.desktop.model.MovimientoFila;
import co.edu.umb.smartstock.desktop.service.DatosDemo;
import co.edu.umb.smartstock.desktop.util.Avisos;
import co.edu.umb.smartstock.desktop.util.Tablas;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** RF05: registrar entradas, salidas y ajustes (modo demostración, sin servidor). */
public class MovimientoController {

    @FXML private ComboBox<InventarioFila> comboProducto;
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField campoCantidad;
    @FXML private TextField campoMotivo;
    @FXML private Label mensaje;

    @FXML private TableView<MovimientoFila> tabla;
    @FXML private TableColumn<MovimientoFila, String> colFecha;
    @FXML private TableColumn<MovimientoFila, String> colProducto;
    @FXML private TableColumn<MovimientoFila, String> colTipo;
    @FXML private TableColumn<MovimientoFila, Integer> colCantidad;
    @FXML private TableColumn<MovimientoFila, String> colMotivo;
    @FXML private TableColumn<MovimientoFila, String> colUsuario;

    @FXML
    private void initialize() {
        comboProducto.setItems(DatosDemo.INVENTARIO);
        // Muestra el nombre del producto en la lista desplegable y en el campo.
        comboProducto.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(InventarioFila f) { return f == null ? "" : f.getProducto(); }
            @Override public InventarioFila fromString(String s) { return null; }
        });
        comboTipo.getItems().addAll("Entrada", "Salida", "Ajuste");
        comboTipo.getSelectionModel().selectFirst();

        Tablas.columna(colFecha, MovimientoFila::fecha);
        Tablas.columna(colProducto, MovimientoFila::producto);
        Tablas.columna(colTipo, MovimientoFila::tipo);
        Tablas.columna(colCantidad, MovimientoFila::cantidad);
        Tablas.columna(colMotivo, MovimientoFila::motivo);
        Tablas.columna(colUsuario, MovimientoFila::usuario);
        tabla.setItems(DatosDemo.MOVIMIENTOS);
    }

    @FXML
    private void registrar() {
        InventarioFila producto = comboProducto.getValue();
        String tipo = comboTipo.getValue();
        String motivo = campoMotivo.getText().trim();

        if (producto == null) {
            Avisos.error(mensaje, "Elige un producto.");
            return;
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(campoCantidad.getText().trim());
        } catch (NumberFormatException e) {
            Avisos.error(mensaje, "La cantidad debe ser un número entero.");
            return;
        }
        if (cantidad < 0 || (cantidad == 0 && !"Ajuste".equals(tipo))) {
            Avisos.error(mensaje, "La cantidad debe ser mayor que cero.");
            return;
        }
        if (motivo.isEmpty()) {
            Avisos.error(mensaje, "Escribe el motivo del movimiento.");
            return;
        }

        switch (tipo) {
            case "Entrada" -> producto.setCantidad(producto.getCantidad() + cantidad);
            case "Salida" -> {
                // RNF04: la existencia nunca queda negativa.
                if (cantidad > producto.getCantidad()) {
                    Avisos.error(mensaje, "La salida (" + cantidad + ") supera la existencia ("
                            + producto.getCantidad() + "). No se registró.");
                    return;
                }
                producto.setCantidad(producto.getCantidad() - cantidad);
            }
            default -> producto.setCantidad(cantidad); // Ajuste: fija la existencia en ese valor
        }

        DatosDemo.MOVIMIENTOS.add(0, new MovimientoFila(
                LocalDate.now().toString(), producto.getProducto(), tipo, cantidad, motivo, "demo"));
        Avisos.exito(mensaje, "Movimiento registrado. Existencia actual de "
                + producto.getProducto() + ": " + producto.getCantidad());
        campoCantidad.clear();
        campoMotivo.clear();
    }
}
