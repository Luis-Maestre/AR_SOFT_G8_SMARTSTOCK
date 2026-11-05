package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.model.Api;
import co.edu.umb.smartstock.desktop.service.ApiClient;
import co.edu.umb.smartstock.desktop.service.Sesion;
import co.edu.umb.smartstock.desktop.util.Avisos;
import co.edu.umb.smartstock.desktop.util.Tablas;
import co.edu.umb.smartstock.desktop.util.Tareas;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

/** RF04 y RF06: existencias por producto, con el nivel de stock resaltado. */
public class InventarioController {

    @FXML private TextField buscar;
    @FXML private CheckBox checkBajos;
    @FXML private Button botonUbicacion;
    @FXML private Label mensaje;
    @FXML private TableView<Api.Inventario> tabla;
    @FXML private TableColumn<Api.Inventario, String> colCodigo;
    @FXML private TableColumn<Api.Inventario, String> colProducto;
    @FXML private TableColumn<Api.Inventario, String> colCategoria;
    @FXML private TableColumn<Api.Inventario, Integer> colCantidad;
    @FXML private TableColumn<Api.Inventario, Integer> colMinimo;
    @FXML private TableColumn<Api.Inventario, String> colUbicacion;
    @FXML private TableColumn<Api.Inventario, String> colEstado;

    private List<Api.Inventario> todos = new ArrayList<>();

    @FXML
    private void initialize() {
        Tablas.columna(colCodigo, i -> i.codigo() == null ? "" : i.codigo());
        Tablas.columna(colProducto, Api.Inventario::producto);
        Tablas.columna(colCategoria, i -> i.categoria() == null ? "" : i.categoria());
        Tablas.columna(colCantidad, Api.Inventario::cantidadActual);
        Tablas.columna(colMinimo, Api.Inventario::stockMinimo);
        Tablas.columna(colUbicacion, i -> i.ubicacion() == null ? "" : i.ubicacion());
        Tablas.columna(colEstado, i -> switch (i.nivel() == null ? "ok" : i.nivel()) {
            case "agotado" -> "Agotado";
            case "bajo" -> "Stock bajo";
            default -> "Normal";
        });
        tabla.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Api.Inventario i, boolean vacio) {
                super.updateItem(i, vacio);
                setStyle(!vacio && i != null && !"ok".equals(i.nivel()) ? "-fx-text-fill: #B3261E;" : "");
            }
        });
        botonUbicacion.setDisable(true);
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, antes, i) ->
                botonUbicacion.setDisable(i == null || !Sesion.puedeGestionar()));
        buscar.textProperty().addListener((obs, antes, ahora) -> filtrar());
        cargar();
    }

    @FXML
    private void cargar() {
        Avisos.info(mensaje, "Cargando...");
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/inventario", Api.Inventario.class), lista -> {
            todos = lista;
            filtrar();
        }, error -> Avisos.error(mensaje, error));
    }

    @FXML
    private void filtrar() {
        String texto = buscar.getText() == null ? "" : buscar.getText().trim().toLowerCase();
        List<Api.Inventario> visibles = todos.stream()
                .filter(i -> texto.isEmpty()
                        || i.producto().toLowerCase().contains(texto)
                        || (i.codigo() != null && i.codigo().toLowerCase().contains(texto)))
                .filter(i -> !checkBajos.isSelected() || !"ok".equals(i.nivel()))
                .toList();
        tabla.getItems().setAll(visibles);
        Avisos.info(mensaje, visibles.size() + " productos");
    }

    @FXML
    private void cambiarUbicacion() {
        Api.Inventario i = tabla.getSelectionModel().getSelectedItem();
        if (i == null) {
            return;
        }
        TextInputDialog dialogo = new TextInputDialog(i.ubicacion() == null ? "" : i.ubicacion());
        dialogo.setTitle("Ubicación");
        dialogo.setHeaderText(null);
        dialogo.setContentText("Ubicación de " + i.producto() + ":");
        dialogo.showAndWait().ifPresent(nueva -> {
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("ubicacion", nueva.trim());
            Tareas.ejecutar(() -> ApiClient.INSTANCIA.enviar("PUT", "/inventario/" + i.idProducto() + "/ubicacion",
                            cuerpo, Api.Inventario.class),
                    ok -> {
                        cargar();
                        Avisos.exito(mensaje, "Ubicación actualizada.");
                    }, error -> Avisos.error(mensaje, error));
        });
    }
}
