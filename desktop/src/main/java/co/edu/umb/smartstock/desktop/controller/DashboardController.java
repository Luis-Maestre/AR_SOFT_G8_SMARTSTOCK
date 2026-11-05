package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.component.TarjetaIndicador;
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
    }
}
