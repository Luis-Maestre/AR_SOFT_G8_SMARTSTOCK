package co.edu.umb.smartstock.desktop.controller;

<<<<<<< HEAD
import co.edu.umb.smartstock.desktop.model.Api;
import co.edu.umb.smartstock.desktop.service.ApiClient;
import co.edu.umb.smartstock.desktop.util.Avisos;
import co.edu.umb.smartstock.desktop.util.Tablas;
import co.edu.umb.smartstock.desktop.util.Tareas;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

/** RF10: reportes de inventario y de movimientos, con exportación a CSV (se abre en Excel). */
public class ReporteController {

    @FXML private Label mensaje;
    @FXML private Label totalInventario;

    @FXML private TableView<Api.Inventario> tablaInventario;
    @FXML private TableColumn<Api.Inventario, String> iCodigo;
    @FXML private TableColumn<Api.Inventario, String> iProducto;
    @FXML private TableColumn<Api.Inventario, String> iCategoria;
    @FXML private TableColumn<Api.Inventario, Integer> iCantidad;
    @FXML private TableColumn<Api.Inventario, Integer> iMinimo;
    @FXML private TableColumn<Api.Inventario, String> iNivel;
    @FXML private TableColumn<Api.Inventario, String> iValor;

    @FXML private DatePicker fechaDesde;
    @FXML private DatePicker fechaHasta;
    @FXML private TableView<Api.Movimiento> tablaMovimientos;
    @FXML private TableColumn<Api.Movimiento, String> mFecha;
    @FXML private TableColumn<Api.Movimiento, String> mProducto;
    @FXML private TableColumn<Api.Movimiento, String> mTipo;
    @FXML private TableColumn<Api.Movimiento, Integer> mCantidad;
    @FXML private TableColumn<Api.Movimiento, String> mMotivo;
    @FXML private TableColumn<Api.Movimiento, String> mUsuario;

    @FXML
    private void initialize() {
        Tablas.columna(iCodigo, i -> i.codigo() == null ? "" : i.codigo());
        Tablas.columna(iProducto, Api.Inventario::producto);
        Tablas.columna(iCategoria, i -> i.categoria() == null ? "" : i.categoria());
        Tablas.columna(iCantidad, Api.Inventario::cantidadActual);
        Tablas.columna(iMinimo, Api.Inventario::stockMinimo);
        Tablas.columna(iNivel, i -> "agotado".equals(i.nivel()) ? "Agotado" : "bajo".equals(i.nivel()) ? "Stock bajo" : "Normal");
        Tablas.columna(iValor, i -> "$ " + Tablas.dinero(i.valor()));

        Tablas.columna(mFecha, m -> Api.fecha(m.fecha()));
        Tablas.columna(mProducto, Api.Movimiento::producto);
        Tablas.columna(mTipo, Api.Movimiento::tipoMovimiento);
        Tablas.columna(mCantidad, Api.Movimiento::cantidad);
        Tablas.columna(mMotivo, m -> m.motivo() == null ? "" : m.motivo());
        Tablas.columna(mUsuario, Api.Movimiento::usuario);

        fechaHasta.setValue(LocalDate.now());
        fechaDesde.setValue(LocalDate.now().minusDays(30));
        cargarInventario();
        cargarMovimientos();
    }

    @FXML
    private void cargarInventario() {
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/inventario", Api.Inventario.class), lista -> {
            tablaInventario.getItems().setAll(lista);
            java.math.BigDecimal total = lista.stream().map(Api.Inventario::valor)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            totalInventario.setText("Valor total del inventario: $ " + Tablas.dinero(total));
        }, error -> Avisos.error(mensaje, error));
    }

    @FXML
    private void cargarMovimientos() {
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/movimientos?limite=5000" + rango(), Api.Movimiento.class),
                lista -> {
                    tablaMovimientos.getItems().setAll(lista);
                    Avisos.info(mensaje, lista.size() + " movimientos en el periodo");
                }, error -> Avisos.error(mensaje, error));
    }

    @FXML
    private void exportarInventario() {
        exportar("/reportes/inventario/csv", "inventario.csv");
    }

    @FXML
    private void exportarMovimientos() {
        exportar("/reportes/movimientos/csv?x=1" + rango(), "movimientos.csv");
    }

    private String rango() {
        String r = "";
        if (fechaDesde.getValue() != null) r += "&desde=" + fechaDesde.getValue();
        if (fechaHasta.getValue() != null) r += "&hasta=" + fechaHasta.getValue();
        return r;
    }

    private void exportar(String ruta, String nombreSugerido) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar reporte");
        selector.setInitialFileName(nombreSugerido);
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (Excel)", "*.csv"));
        File destino = selector.showSaveDialog(mensaje.getScene().getWindow());
        if (destino == null) {
            return;
        }
        Tareas.ejecutarSinResultado(() -> Files.write(destino.toPath(), ApiClient.INSTANCIA.descargar(ruta)),
                () -> Avisos.exito(mensaje, "Reporte guardado en " + destino.getAbsolutePath()),
                error -> Avisos.error(mensaje, error));
=======
import co.edu.umb.smartstock.desktop.model.FilaReporte;
import co.edu.umb.smartstock.desktop.model.InventarioFila;
import co.edu.umb.smartstock.desktop.model.MovimientoFila;
import co.edu.umb.smartstock.desktop.service.DatosDemo;
import co.edu.umb.smartstock.desktop.util.Tablas;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/** RF10: los tres reportes pedidos (por categoría, bajo el mínimo y movimientos por periodo). */
public class ReporteController {

    private static final String POR_CATEGORIA = "Productos por categoría";
    private static final String BAJO_MINIMO = "Productos en o bajo el mínimo";
    private static final String POR_PERIODO = "Movimientos por periodo";

    @FXML private ComboBox<String> comboReporte;
    @FXML private TableView<FilaReporte> tabla;
    @FXML private TableColumn<FilaReporte, String> col1;
    @FXML private TableColumn<FilaReporte, String> col2;
    @FXML private TableColumn<FilaReporte, String> col3;

    @FXML
    private void initialize() {
        Tablas.columna(col1, FilaReporte::a);
        Tablas.columna(col2, FilaReporte::b);
        Tablas.columna(col3, FilaReporte::c);

        comboReporte.getItems().addAll(POR_CATEGORIA, BAJO_MINIMO, POR_PERIODO);
        comboReporte.valueProperty().addListener((obs, antes, reporte) -> mostrar(reporte));
        comboReporte.getSelectionModel().selectFirst();
    }

    private void mostrar(String reporte) {
        List<FilaReporte> filas = new ArrayList<>();
        switch (reporte) {
            case POR_CATEGORIA -> {
                titulos("Categoría", "Productos", "Unidades en existencia");
                Map<String, int[]> grupos = new TreeMap<>();
                for (InventarioFila f : DatosDemo.INVENTARIO) {
                    int[] total = grupos.computeIfAbsent(f.getCategoria(), k -> new int[2]);
                    total[0]++;
                    total[1] += f.getCantidad();
                }
                grupos.forEach((cat, t) -> filas.add(new FilaReporte(cat, String.valueOf(t[0]), String.valueOf(t[1]))));
            }
            case BAJO_MINIMO -> {
                titulos("Producto", "Existencia", "Stock mínimo");
                for (InventarioFila f : DatosDemo.INVENTARIO) {
                    if (f.bajoMinimo()) {
                        filas.add(new FilaReporte(f.getProducto(),
                                String.valueOf(f.getCantidad()), String.valueOf(f.getStockMinimo())));
                    }
                }
            }
            default -> {
                titulos("Fecha", "Movimientos", "Unidades movidas");
                Map<String, int[]> dias = new TreeMap<>();
                for (MovimientoFila m : DatosDemo.MOVIMIENTOS) {
                    int[] total = dias.computeIfAbsent(m.fecha(), k -> new int[2]);
                    total[0]++;
                    total[1] += m.cantidad();
                }
                dias.forEach((dia, t) -> filas.add(new FilaReporte(dia, String.valueOf(t[0]), String.valueOf(t[1]))));
            }
        }
        tabla.setItems(FXCollections.observableArrayList(filas));
    }

    private void titulos(String a, String b, String c) {
        col1.setText(a);
        col2.setText(b);
        col3.setText(c);
>>>>>>> d73906b8395de950b3b26a93762371c581175868
    }
}
