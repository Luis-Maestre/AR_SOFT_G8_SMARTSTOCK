package co.edu.umb.smartstock.desktop.controller;

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
    }
}
