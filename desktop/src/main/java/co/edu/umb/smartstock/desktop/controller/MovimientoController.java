package co.edu.umb.smartstock.desktop.controller;

<<<<<<< HEAD
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
=======
import co.edu.umb.smartstock.desktop.model.InventarioFila;
import co.edu.umb.smartstock.desktop.model.MovimientoFila;
import co.edu.umb.smartstock.desktop.service.DatosDemo;
import co.edu.umb.smartstock.desktop.util.Avisos;
import co.edu.umb.smartstock.desktop.util.Tablas;
import java.time.LocalDate;
import javafx.fxml.FXML;
>>>>>>> d73906b8395de950b3b26a93762371c581175868
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

<<<<<<< HEAD
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
=======
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
>>>>>>> d73906b8395de950b3b26a93762371c581175868
    }

    @FXML
    private void registrar() {
<<<<<<< HEAD
        Api.Producto producto = comboProducto.getValue();
        String tipo = comboTipo.getValue();
        String motivo = campoMotivo.getText().trim();
=======
        InventarioFila producto = comboProducto.getValue();
        String tipo = comboTipo.getValue();
        String motivo = campoMotivo.getText().trim();

>>>>>>> d73906b8395de950b3b26a93762371c581175868
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
<<<<<<< HEAD
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
=======

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
>>>>>>> d73906b8395de950b3b26a93762371c581175868
    }
}
