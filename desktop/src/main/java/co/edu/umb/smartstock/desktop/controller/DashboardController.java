package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.component.TarjetaIndicador;
<<<<<<< HEAD
import co.edu.umb.smartstock.desktop.model.Api;
import co.edu.umb.smartstock.desktop.service.ApiClient;
import co.edu.umb.smartstock.desktop.util.Tablas;
import co.edu.umb.smartstock.desktop.util.Tareas;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;

/** RF06 y RF10: resumen general y alertas de stock mínimo. */
public class DashboardController {

    @FXML private FlowPane tarjetas;
    @FXML private ListView<String> listaAlertas;
    @FXML private ListView<String> listaMovimientos;
    @FXML private Label estado;

    @FXML
    private void initialize() {
        cargar();
    }

    @FXML
    private void cargar() {
        estado.setText("Cargando...");
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.obtener("/reportes/dashboard", Api.Dashboard.class),
                this::mostrarTarjetas, e -> estado.setText(e));
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/alertas?estado=pendiente", Api.Alerta.class),
                this::mostrarAlertas, e -> estado.setText(e));
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/movimientos?limite=15", Api.Movimiento.class),
                this::mostrarMovimientos, e -> estado.setText(e));
    }

    private void mostrarTarjetas(Api.Dashboard d) {
        estado.setText("");
        tarjetas.getChildren().setAll(
                new TarjetaIndicador("Productos activos", String.valueOf(d.totalProductos()), false),
                new TarjetaIndicador("Bajo el mínimo", String.valueOf(d.productosBajoMinimo()), d.productosBajoMinimo() > 0),
                new TarjetaIndicador("Alertas pendientes", String.valueOf(d.alertasPendientes()), d.alertasPendientes() > 0),
                new TarjetaIndicador("Reposiciones sugeridas", String.valueOf(d.reposicionesSugeridas()), false),
                new TarjetaIndicador("Valor del inventario", "$ " + Tablas.dinero(d.valorInventario()), false),
                new TarjetaIndicador("Movimientos hoy", String.valueOf(d.movimientosHoy()), false));
    }

    private void mostrarAlertas(List<Api.Alerta> alertas) {
        if (alertas.isEmpty()) {
            listaAlertas.getItems().setAll("Sin alertas pendientes");
            return;
        }
        listaAlertas.getItems().setAll(alertas.stream().map(a -> Api.fecha(a.fecha()) + "  -  " + a.mensaje()).toList());
    }

    private void mostrarMovimientos(List<Api.Movimiento> movimientos) {
        if (movimientos.isEmpty()) {
            listaMovimientos.getItems().setAll("Aún no hay movimientos");
            return;
        }
        listaMovimientos.getItems().setAll(movimientos.stream()
                .map(m -> Api.fecha(m.fecha()) + "  -  " + m.tipoMovimiento() + " de " + m.cantidad()
                        + " u. de " + m.producto() + " (" + m.usuario() + ")")
                .toList());
=======
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
>>>>>>> d73906b8395de950b3b26a93762371c581175868
    }
}
