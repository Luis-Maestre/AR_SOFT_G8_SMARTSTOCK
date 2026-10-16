package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.Navegador;
import co.edu.umb.smartstock.desktop.model.ProductoDto;
import co.edu.umb.smartstock.desktop.service.DatosPrueba;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/** Pantalla de productos: muestra el catalogo en una tabla. */
public class ProductoController {

    @FXML private TableView<ProductoDto> tablaProductos;
    @FXML private TableColumn<ProductoDto, String> colCodigo;
    @FXML private TableColumn<ProductoDto, String> colNombre;
    @FXML private TableColumn<ProductoDto, String> colCategoria;
    @FXML private TableColumn<ProductoDto, Integer> colStockMinimo;
    @FXML private Label estado;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoriaNombre"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        cargar();
    }

    @FXML
    private void cargar() {
        tablaProductos.setItems(FXCollections.observableArrayList(DatosPrueba.productos()));
        estado.setText("Datos de prueba (sin conexión al servidor todavía).");
    }

    @FXML
    private void cerrarSesion() {
        Navegador.irA("login");
    }
}
