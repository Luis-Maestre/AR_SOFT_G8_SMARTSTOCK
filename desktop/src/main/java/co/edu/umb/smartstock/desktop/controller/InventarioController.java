package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.model.InventarioFila;
import co.edu.umb.smartstock.desktop.service.DatosDemo;
import co.edu.umb.smartstock.desktop.util.Tablas;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** RF04: consultar cuánto hay de cada producto y dónde está. */
public class InventarioController {

    @FXML private TextField buscar;
    @FXML private TableView<InventarioFila> tabla;
    @FXML private TableColumn<InventarioFila, String> colCodigo;
    @FXML private TableColumn<InventarioFila, String> colProducto;
    @FXML private TableColumn<InventarioFila, String> colCategoria;
    @FXML private TableColumn<InventarioFila, Integer> colCantidad;
    @FXML private TableColumn<InventarioFila, Integer> colMinimo;
    @FXML private TableColumn<InventarioFila, String> colUbicacion;
    @FXML private TableColumn<InventarioFila, String> colEstado;

    @FXML
    private void initialize() {
        Tablas.columna(colCodigo, InventarioFila::getCodigo);
        Tablas.columna(colProducto, InventarioFila::getProducto);
        Tablas.columna(colCategoria, InventarioFila::getCategoria);
        Tablas.columna(colCantidad, InventarioFila::getCantidad);
        Tablas.columna(colMinimo, InventarioFila::getStockMinimo);
        Tablas.columna(colUbicacion, InventarioFila::getUbicacion);
        Tablas.columna(colEstado, InventarioFila::getEstado);

        // La lista filtrada muestra solo lo que coincide con lo escrito en el buscador.
        FilteredList<InventarioFila> filtrada = new FilteredList<>(DatosDemo.INVENTARIO, f -> true);
        buscar.textProperty().addListener((obs, antes, texto) -> {
            String t = texto.toLowerCase().trim();
            filtrada.setPredicate(f -> t.isEmpty()
                    || f.getProducto().toLowerCase().contains(t)
                    || f.getCodigo().toLowerCase().contains(t));
        });
        tabla.setItems(filtrada);
    }
}
