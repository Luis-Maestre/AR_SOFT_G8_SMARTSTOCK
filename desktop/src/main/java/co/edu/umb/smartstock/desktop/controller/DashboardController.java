package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.component.TarjetaIndicador;
import co.edu.umb.smartstock.desktop.model.InventarioFila;
import co.edu.umb.smartstock.desktop.service.DatosDemo;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

/** RF10: indicadores y alertas pendientes. */
public class DashboardController {

    @FXML private HBox tarjetas;
    @FXML private ListView<String> listaAlertas;
    @FXML private ListView<String> listaMovimientos;

    @FXML
    private void initialize() {
        long bajoMinimo = DatosDemo.INVENTARIO.stream().filter(InventarioFila::bajoMinimo).count();
        long sugeridas = DatosDemo.REPOSICIONES.stream()
                .filter(r -> "Sugerida".equals(r.getEstado())).count();

        tarjetas.getChildren().addAll(
            new TarjetaIndicador("Productos en inventario", String.valueOf(DatosDemo.INVENTARIO.size()), false),
            new TarjetaIndicador("En o bajo el mínimo", String.valueOf(bajoMinimo), bajoMinimo > 0),
            new TarjetaIndicador("Reposiciones por revisar", String.valueOf(sugeridas), false),
            new TarjetaIndicador("Movimientos registrados", String.valueOf(DatosDemo.MOVIMIENTOS.size()), false)
        );

        for (InventarioFila fila : DatosDemo.INVENTARIO) {
            if (fila.bajoMinimo()) {
                listaAlertas.getItems().add(fila.getProducto() + ": quedan " + fila.getCantidad()
                        + " unidades (mínimo " + fila.getStockMinimo() + ")");
            }
        }
        if (listaAlertas.getItems().isEmpty()) {
            listaAlertas.getItems().add("No hay alertas pendientes.");
        }

        DatosDemo.MOVIMIENTOS.stream().limit(6).forEach(m -> listaMovimientos.getItems().add(
                m.fecha() + " · " + m.tipo() + " · " + m.producto() + " · " + m.cantidad() + " und."));
    }
}
