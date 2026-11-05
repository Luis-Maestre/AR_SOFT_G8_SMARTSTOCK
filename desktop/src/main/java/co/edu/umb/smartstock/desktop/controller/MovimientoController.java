package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.model.Api;
import co.edu.umb.smartstock.desktop.service.ApiClient;
import co.edu.umb.smartstock.desktop.service.Sesion;
import co.edu.umb.smartstock.desktop.util.Avisos;
import co.edu.umb.smartstock.desktop.util.Tablas;
import co.edu.umb.smartstock.desktop.util.Tareas;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** RF05: registrar entradas, salidas y ajustes. El servidor valida el stock (nunca negativo). */
public class MovimientoController {

    @FXML private ComboBox<Api.Producto> comboProducto;
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField campoCantidad;
    @FXML private TextField campoMotivo;
    @FXML private Button botonRegistrar;
    @FXML private Label mensaje;

    @FXML private TableView<Api.Movimiento> tabla;
    @FXML private TableColumn<Api.Movimiento, String> colFecha;
    @FXML private TableColumn<Api.Movimiento, String> colProducto;
    @FXML private TableColumn<Api.Movimiento, String> colTipo;
    @FXML private TableColumn<Api.Movimiento, Integer> colCantidad;
    @FXML private TableColumn<Api.Movimiento, String> colMotivo;
    @FXML private TableColumn<Api.Movimiento, String> colUsuario;

    @FXML
    private void initialize() {
        comboTipo.getItems().addAll("Entrada", "Salida", "Ajuste");
        comboTipo.getSelectionModel().selectFirst();
        comboTipo.valueProperty().addListener((obs, antes, tipo) ->
                campoCantidad.setPromptText("Ajuste".equals(tipo) ? "Nueva existencia" : "Cantidad"));
        botonRegistrar.setDisable(!Sesion.puedeGestionar());

        Tablas.columna(colFecha, m -> co.edu.umb.smartstock.desktop.model.Api.fecha(m.fecha()));
        Tablas.columna(colProducto, Api.Movimiento::producto);
        Tablas.columna(colTipo, m -> m.tipoMovimiento().substring(0, 1).toUpperCase() + m.tipoMovimiento().substring(1));
        Tablas.columna(colCantidad, Api.Movimiento::cantidad);
        Tablas.columna(colMotivo, m -> m.motivo() == null ? "" : m.motivo());
        Tablas.columna(colUsuario, Api.Movimiento::usuario);
        cargar();
    }

    private void cargar() {
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/productos", Api.Producto.class),
                lista -> comboProducto.getItems().setAll(lista), error -> Avisos.error(mensaje, error));
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/movimientos?limite=300", Api.Movimiento.class),
                lista -> tabla.getItems().setAll(lista), error -> Avisos.error(mensaje, error));
    }

    @FXML
    private void registrar() {
        Api.Producto producto = comboProducto.getValue();
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
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("idProducto", producto.idProducto());
        cuerpo.put("tipoMovimiento", tipo.toLowerCase());
        cuerpo.put("cantidad", cantidad);
        cuerpo.put("motivo", motivo);

        botonRegistrar.setDisable(true);
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.enviar("POST", "/movimientos", cuerpo, Api.Movimiento.class),
                m -> {
                    botonRegistrar.setDisable(!Sesion.puedeGestionar());
                    Avisos.exito(mensaje, "Movimiento registrado. Existencia actual de " + m.producto()
                            + ": " + m.existenciaResultante());
                    campoCantidad.clear();
                    campoMotivo.clear();
                    cargar();
                },
                error -> {
                    botonRegistrar.setDisable(!Sesion.puedeGestionar());
                    Avisos.error(mensaje, error);
                });
    }
}
