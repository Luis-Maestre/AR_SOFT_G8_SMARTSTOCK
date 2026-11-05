package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.model.Api;
import co.edu.umb.smartstock.desktop.service.ApiClient;
import co.edu.umb.smartstock.desktop.service.Sesion;
import co.edu.umb.smartstock.desktop.util.Avisos;
import co.edu.umb.smartstock.desktop.util.Tablas;
import co.edu.umb.smartstock.desktop.util.Tareas;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * RF08 y RF09: recomendaciones de compra calculadas por el servicio de predicción.
 * Flujo: "Generar sugerencias" -> se revisa y ajusta la cantidad -> Confirmar (con proveedor) o Rechazar.
 */
public class ReposicionController {

    private static final String[] FILTROS = {"Sugeridas", "Confirmadas", "Rechazadas", "Todas"};
    private static final String[] VALORES = {"sugerida", "confirmada", "rechazada", "todas"};

    @FXML private ComboBox<String> comboFiltro;
    @FXML private Button botonGenerar;
    @FXML private Button botonConfirmar;
    @FXML private Button botonRechazar;
    @FXML private ComboBox<Api.Proveedor> comboProveedor;
    @FXML private TextField campoCantidad;
    @FXML private TextField campoNota;
    @FXML private Label mensaje;

    @FXML private TableView<Api.Reposicion> tabla;
    @FXML private TableColumn<Api.Reposicion, String> colProducto;
    @FXML private TableColumn<Api.Reposicion, Integer> colExistencia;
    @FXML private TableColumn<Api.Reposicion, Integer> colMinimo;
    @FXML private TableColumn<Api.Reposicion, String> colDemanda;
    @FXML private TableColumn<Api.Reposicion, Integer> colSugerida;
    @FXML private TableColumn<Api.Reposicion, String> colModelo;
    @FXML private TableColumn<Api.Reposicion, String> colProveedor;
    @FXML private TableColumn<Api.Reposicion, Integer> colSolicitada;
    @FXML private TableColumn<Api.Reposicion, String> colEstado;

    @FXML
    private void initialize() {
        comboFiltro.getItems().addAll(FILTROS);
        comboFiltro.getSelectionModel().selectFirst();
        comboFiltro.setOnAction(e -> cargar());

        Tablas.columna(colProducto, Api.Reposicion::producto);
        Tablas.columna(colExistencia, Api.Reposicion::existencia);
        Tablas.columna(colMinimo, Api.Reposicion::stockMinimo);
        Tablas.columna(colDemanda, r -> r.demandaEstimada() == null ? "" : String.format("%.1f", r.demandaEstimada()));
        Tablas.columna(colSugerida, Api.Reposicion::cantidadSugerida);
        Tablas.columna(colModelo, r -> r.modeloPrediccion() == null ? "" : r.modeloPrediccion().replace('_', ' '));
        Tablas.columna(colProveedor, r -> r.proveedor() == null ? "" : r.proveedor());
        Tablas.columna(colSolicitada, Api.Reposicion::cantidadSolicitada);
        Tablas.columna(colEstado, r -> r.estado().substring(0, 1).toUpperCase() + r.estado().substring(1));

        boolean gestiona = Sesion.puedeGestionar();
        botonGenerar.setDisable(!gestiona);
        botonConfirmar.setDisable(true);
        botonRechazar.setDisable(true);
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, antes, r) -> {
            boolean pendiente = r != null && "sugerida".equals(r.estado());
            botonConfirmar.setDisable(!pendiente || !gestiona);
            botonRechazar.setDisable(!pendiente || !gestiona);
            if (pendiente) {
                campoCantidad.setText(String.valueOf(r.cantidadSugerida()));
            }
        });
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/proveedores", Api.Proveedor.class),
                lista -> comboProveedor.getItems().setAll(lista), error -> Avisos.error(mensaje, error));
        cargar();
    }

    @FXML
    private void cargar() {
        int i = Math.max(0, comboFiltro.getSelectionModel().getSelectedIndex());
        String ruta = "/reposiciones?estado=" + VALORES[i];
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista(ruta, Api.Reposicion.class), lista -> {
            tabla.getItems().setAll(lista);
            Avisos.info(mensaje, lista.size() + " reposiciones");
        }, error -> Avisos.error(mensaje, error));
    }

    @FXML
    private void generar() {
        botonGenerar.setDisable(true);
        Avisos.info(mensaje, "Calculando la demanda esperada de cada producto...");
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.enviarLista("POST", "/reposiciones/generar", null, Api.Reposicion.class),
                (List<Api.Reposicion> nuevas) -> {
                    botonGenerar.setDisable(!Sesion.puedeGestionar());
                    comboFiltro.getSelectionModel().select(0);
                    cargar();
                    Avisos.exito(mensaje, nuevas.isEmpty()
                            ? "No hay productos que necesiten reposición por ahora."
                            : nuevas.size() + " sugerencias generadas.");
                },
                error -> {
                    botonGenerar.setDisable(!Sesion.puedeGestionar());
                    Avisos.error(mensaje, error);
                });
    }

    @FXML
    private void confirmar() {
        Api.Reposicion r = tabla.getSelectionModel().getSelectedItem();
        if (r == null) {
            return;
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(campoCantidad.getText().trim());
        } catch (NumberFormatException e) {
            Avisos.error(mensaje, "La cantidad a solicitar debe ser un número entero.");
            return;
        }
        if (cantidad <= 0) {
            Avisos.error(mensaje, "La cantidad a solicitar debe ser mayor que cero.");
            return;
        }
        Api.Proveedor proveedor = comboProveedor.getValue();
        if (proveedor == null) {
            Avisos.error(mensaje, "Elige el proveedor al que se le compra.");
            return;
        }
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("cantidadSolicitada", cantidad);
        cuerpo.put("idProveedor", proveedor.idProveedor());
        cuerpo.put("nota", campoNota.getText().trim());
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.enviar("PUT", "/reposiciones/" + r.idReposicion() + "/confirmar",
                        cuerpo, Api.Reposicion.class),
                ok -> {
                    Avisos.exito(mensaje, "Reposición confirmada: " + cantidad + " u. de " + r.producto() + " a " + proveedor.nombre());
                    campoNota.clear();
                    cargar();
                }, error -> Avisos.error(mensaje, error));
    }

    @FXML
    private void rechazar() {
        Api.Reposicion r = tabla.getSelectionModel().getSelectedItem();
        if (r == null) {
            return;
        }
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("nota", campoNota.getText().trim());
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.enviar("PUT", "/reposiciones/" + r.idReposicion() + "/rechazar",
                        cuerpo, Api.Reposicion.class),
                ok -> {
                    Avisos.exito(mensaje, "Reposición rechazada.");
                    campoNota.clear();
                    cargar();
                }, error -> Avisos.error(mensaje, error));
    }
}
