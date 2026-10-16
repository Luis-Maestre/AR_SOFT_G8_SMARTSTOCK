package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.Navegador;
import co.edu.umb.smartstock.desktop.model.LoteDto;
import co.edu.umb.smartstock.desktop.model.ProductoDto;
import co.edu.umb.smartstock.desktop.service.ApiClient;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/** Pantalla principal: productos y lotes ordenados por vencimiento (FEFO). */
public class ProductoController {

    @FXML private TableView<ProductoDto> tablaProductos;
    @FXML private TableColumn<ProductoDto, String> colCodigo;
    @FXML private TableColumn<ProductoDto, String> colNombre;
    @FXML private TableColumn<ProductoDto, String> colCategoria;
    @FXML private TableColumn<ProductoDto, Integer> colStockMinimo;

    @FXML private TableView<LoteDto> tablaLotes;
    @FXML private TableColumn<LoteDto, String> colLoteProducto;
    @FXML private TableColumn<LoteDto, String> colLoteNumero;
    @FXML private TableColumn<LoteDto, String> colLoteVence;
    @FXML private TableColumn<LoteDto, Integer> colLoteCantidad;

    @FXML private Label estado;

    private final ApiClient api = new ApiClient();

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoriaNombre"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));

        colLoteProducto.setCellValueFactory(new PropertyValueFactory<>("productoNombre"));
        colLoteNumero.setCellValueFactory(new PropertyValueFactory<>("numeroLote"));
        colLoteVence.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));
        colLoteCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        cargar();
    }

    /** Pide los datos al servidor sin congelar la ventana. */
    @FXML
    private void cargar() {
        estado.setText("Conectando con el servidor...");
        CompletableFuture.supplyAsync(() -> {
            try {
                return new Datos(api.productos(), api.lotesFefo());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).whenComplete((datos, error) -> Platform.runLater(() -> {
            if (error != null) {
                tablaProductos.getItems().clear();
                tablaLotes.getItems().clear();
                estado.setText("No se pudo conectar con el servidor. Enciéndelo y presiona Actualizar.");
            } else {
                tablaProductos.setItems(FXCollections.observableArrayList(datos.productos()));
                tablaLotes.setItems(FXCollections.observableArrayList(datos.lotes()));
                estado.setText("Datos cargados del servidor.");
            }
        }));
    }

    @FXML
    private void cerrarSesion() {
        Navegador.irA("login");
    }

    private record Datos(List<ProductoDto> productos, List<LoteDto> lotes) { }
}
