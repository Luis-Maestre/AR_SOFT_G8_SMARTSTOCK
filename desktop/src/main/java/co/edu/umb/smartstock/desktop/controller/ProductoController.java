package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.component.FormularioProducto;
import co.edu.umb.smartstock.desktop.model.Api;
import co.edu.umb.smartstock.desktop.service.ApiClient;
import co.edu.umb.smartstock.desktop.service.Sesion;
import co.edu.umb.smartstock.desktop.util.Avisos;
import co.edu.umb.smartstock.desktop.util.Tablas;
import co.edu.umb.smartstock.desktop.util.Tareas;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** RF03: CRUD completo de productos (la baja es lógica: el producto queda inactivo). */
public class ProductoController {

    @FXML private TextField campoBuscar;
    @FXML private CheckBox checkInactivos;
    @FXML private Button botonNuevo;
    @FXML private Button botonEditar;
    @FXML private Button botonEstado;
    @FXML private Label estado;

    @FXML private TableView<Api.Producto> tablaProductos;
    @FXML private TableColumn<Api.Producto, String> colCodigo;
    @FXML private TableColumn<Api.Producto, String> colNombre;
    @FXML private TableColumn<Api.Producto, String> colCategoria;
    @FXML private TableColumn<Api.Producto, String> colPrecio;
    @FXML private TableColumn<Api.Producto, Integer> colExistencia;
    @FXML private TableColumn<Api.Producto, Integer> colStockMinimo;
    @FXML private TableColumn<Api.Producto, String> colUbicacion;
    @FXML private TableColumn<Api.Producto, String> colEstado;

    @FXML
    private void initialize() {
        Tablas.columna(colCodigo, p -> p.codigo() == null ? "" : p.codigo());
        Tablas.columna(colNombre, Api.Producto::nombre);
        Tablas.columna(colCategoria, p -> p.categoria() == null ? "" : p.categoria());
        Tablas.columna(colPrecio, p -> Tablas.dinero(p.precio()));
        Tablas.columna(colExistencia, Api.Producto::cantidadActual);
        Tablas.columna(colStockMinimo, Api.Producto::stockMinimo);
        Tablas.columna(colUbicacion, p -> p.ubicacion() == null ? "" : p.ubicacion());
        Tablas.columna(colEstado, p -> Boolean.TRUE.equals(p.estado()) ? "Activo" : "Inactivo");

        // Los productos con existencia igual o menor al mínimo se resaltan en rojo.
        tablaProductos.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Api.Producto p, boolean vacio) {
                super.updateItem(p, vacio);
                boolean bajo = !vacio && p != null && Boolean.TRUE.equals(p.estado())
                        && p.cantidadActual() != null && p.stockMinimo() != null
                        && p.cantidadActual() <= p.stockMinimo();
                setStyle(bajo ? "-fx-text-fill: #B3261E;" : "");
            }
        });

        boolean gestiona = Sesion.puedeGestionar();
        botonNuevo.setDisable(!gestiona);
        botonEditar.setDisable(true);
        botonEstado.setDisable(true);
        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, antes, p) -> {
            botonEditar.setDisable(p == null || !gestiona);
            botonEstado.setDisable(p == null || !gestiona);
            if (p != null) {
                botonEstado.setText(Boolean.TRUE.equals(p.estado()) ? "Desactivar" : "Reactivar");
            }
        });
        campoBuscar.setOnAction(e -> cargar());
        cargar();
    }

    @FXML
    private void cargar() {
        Avisos.info(estado, "Cargando...");
        String ruta = "/productos?q=" + ApiClient.codificar(campoBuscar.getText().trim())
                + "&incluirInactivos=" + checkInactivos.isSelected();
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista(ruta, Api.Producto.class), this::mostrar,
                error -> Avisos.error(estado, error));
    }

    private void mostrar(List<Api.Producto> productos) {
        tablaProductos.getItems().setAll(productos);
        Avisos.info(estado, productos.size() + " productos");
    }

    @FXML
    private void nuevo() {
        abrirFormulario(null);
    }

    @FXML
    private void editar() {
        Api.Producto p = tablaProductos.getSelectionModel().getSelectedItem();
        if (p != null) {
            abrirFormulario(p);
        }
    }

    private void abrirFormulario(Api.Producto existente) {
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/categorias", Api.Categoria.class), categorias ->
                FormularioProducto.mostrar(existente, categorias).ifPresent(cuerpo -> {
                    String metodo = existente == null ? "POST" : "PUT";
                    String ruta = existente == null ? "/productos" : "/productos/" + existente.idProducto();
                    Tareas.ejecutar(() -> ApiClient.INSTANCIA.enviar(metodo, ruta, cuerpo, Api.Producto.class),
                            guardado -> {
                                cargar();
                                Avisos.exito(estado, existente == null ? "Producto creado." : "Producto actualizado.");
                            },
                            error -> Avisos.error(estado, error));
                }),
                error -> Avisos.error(estado, error));
    }

    @FXML
    private void cambiarEstado() {
        Api.Producto p = tablaProductos.getSelectionModel().getSelectedItem();
        if (p == null) {
            return;
        }
        boolean activo = Boolean.TRUE.equals(p.estado());
        if (activo && !Avisos.confirmar("¿Desactivar \"" + p.nombre() + "\"? Conserva su historial pero ya no aparecerá en el inventario.")) {
            return;
        }
        Tareas.ejecutarSinResultado(() -> {
            if (activo) {
                ApiClient.INSTANCIA.enviarSinRespuesta("DELETE", "/productos/" + p.idProducto(), null);
            } else {
                ApiClient.INSTANCIA.enviar("PATCH", "/productos/" + p.idProducto() + "/reactivar", null, Api.Producto.class);
            }
        }, () -> {
            cargar();
            Avisos.exito(estado, activo ? "Producto desactivado." : "Producto reactivado.");
        }, error -> Avisos.error(estado, error));
    }
}
