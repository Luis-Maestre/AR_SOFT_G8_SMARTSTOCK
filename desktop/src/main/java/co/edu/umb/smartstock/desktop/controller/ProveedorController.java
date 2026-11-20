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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** RF07: gestión de proveedores. */
public class ProveedorController {

    @FXML private TableView<Api.Proveedor> tabla;
    @FXML private TableColumn<Api.Proveedor, String> colNombre;
    @FXML private TableColumn<Api.Proveedor, String> colTelefono;
    @FXML private TableColumn<Api.Proveedor, String> colCorreo;
    @FXML private TableColumn<Api.Proveedor, String> colEstado;
    @FXML private TextField campoNombre;
    @FXML private TextField campoTelefono;
    @FXML private TextField campoCorreo;
    @FXML private CheckBox checkActivo;
    @FXML private Button botonGuardar;
    @FXML private Button botonDesactivar;
    @FXML private Label mensaje;

    @FXML
    private void initialize() {
        Tablas.columna(colNombre, Api.Proveedor::nombre);
        Tablas.columna(colTelefono, p -> p.telefono() == null ? "" : p.telefono());
        Tablas.columna(colCorreo, p -> p.correo() == null ? "" : p.correo());
        Tablas.columna(colEstado, p -> Boolean.TRUE.equals(p.estado()) ? "Activo" : "Inactivo");
        boolean gestiona = Sesion.puedeGestionar();
        botonGuardar.setDisable(!gestiona);
        botonDesactivar.setDisable(true);
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, antes, p) -> {
            if (p != null) {
                campoNombre.setText(p.nombre());
                campoTelefono.setText(p.telefono() == null ? "" : p.telefono());
                campoCorreo.setText(p.correo() == null ? "" : p.correo());
                checkActivo.setSelected(Boolean.TRUE.equals(p.estado()));
            }
            botonDesactivar.setDisable(p == null || !gestiona);
        });
        nuevo();
        cargar();
    }

    @FXML
    private void cargar() {
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/proveedores?incluirInactivos=true", Api.Proveedor.class),
                lista -> tabla.getItems().setAll(lista), error -> Avisos.error(mensaje, error));
    }

    @FXML
    private void nuevo() {
        tabla.getSelectionModel().clearSelection();
        campoNombre.clear();
        campoTelefono.clear();
        campoCorreo.clear();
        checkActivo.setSelected(true);
    }

    @FXML
    private void guardar() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            Avisos.error(mensaje, "El nombre es obligatorio.");
            return;
        }
        Api.Proveedor elegido = tabla.getSelectionModel().getSelectedItem();
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("nombre", nombre);
        cuerpo.put("telefono", campoTelefono.getText().trim());
        cuerpo.put("correo", campoCorreo.getText().trim());
        cuerpo.put("estado", checkActivo.isSelected());
        String metodo = elegido == null ? "POST" : "PUT";
        String ruta = elegido == null ? "/proveedores" : "/proveedores/" + elegido.idProveedor();
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.enviar(metodo, ruta, cuerpo, Api.Proveedor.class),
                guardado -> {
                    Avisos.exito(mensaje, "Proveedor guardado.");
                    nuevo();
                    cargar();
                }, error -> Avisos.error(mensaje, error));
    }

    @FXML
    private void desactivar() {
        Api.Proveedor elegido = tabla.getSelectionModel().getSelectedItem();
        if (elegido == null || !Avisos.confirmar("¿Desactivar al proveedor \"" + elegido.nombre() + "\"?")) {
            return;
        }
        Tareas.ejecutarSinResultado(
                () -> ApiClient.INSTANCIA.enviarSinRespuesta("DELETE", "/proveedores/" + elegido.idProveedor(), null),
                () -> {
                    Avisos.exito(mensaje, "Proveedor desactivado.");
                    nuevo();
                    cargar();
                }, error -> Avisos.error(mensaje, error));
    }
}
