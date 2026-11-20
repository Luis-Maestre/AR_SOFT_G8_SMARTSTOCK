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

/** Gestión de categorías de productos. */
public class CategoriaController {

    @FXML private TableView<Api.Categoria> tabla;
    @FXML private TableColumn<Api.Categoria, String> colNombre;
    @FXML private TableColumn<Api.Categoria, String> colDescripcion;
    @FXML private TableColumn<Api.Categoria, String> colEstado;
    @FXML private TextField campoNombre;
    @FXML private TextField campoDescripcion;
    @FXML private CheckBox checkActiva;
    @FXML private Button botonGuardar;
    @FXML private Button botonDesactivar;
    @FXML private Label mensaje;

    @FXML
    private void initialize() {
        Tablas.columna(colNombre, Api.Categoria::nombre);
        Tablas.columna(colDescripcion, c -> c.descripcion() == null ? "" : c.descripcion());
        Tablas.columna(colEstado, c -> Boolean.TRUE.equals(c.estado()) ? "Activa" : "Inactiva");
        boolean gestiona = Sesion.puedeGestionar();
        botonGuardar.setDisable(!gestiona);
        botonDesactivar.setDisable(true);
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, antes, c) -> {
            if (c != null) {
                campoNombre.setText(c.nombre());
                campoDescripcion.setText(c.descripcion() == null ? "" : c.descripcion());
                checkActiva.setSelected(Boolean.TRUE.equals(c.estado()));
            }
            botonDesactivar.setDisable(c == null || !gestiona);
        });
        nueva();
        cargar();
    }

    @FXML
    private void cargar() {
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/categorias?incluirInactivas=true", Api.Categoria.class),
                lista -> tabla.getItems().setAll(lista), error -> Avisos.error(mensaje, error));
    }

    @FXML
    private void nueva() {
        tabla.getSelectionModel().clearSelection();
        campoNombre.clear();
        campoDescripcion.clear();
        checkActiva.setSelected(true);
    }

    @FXML
    private void guardar() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            Avisos.error(mensaje, "El nombre es obligatorio.");
            return;
        }
        Api.Categoria elegida = tabla.getSelectionModel().getSelectedItem();
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("nombre", nombre);
        cuerpo.put("descripcion", campoDescripcion.getText().trim());
        cuerpo.put("estado", checkActiva.isSelected());
        String metodo = elegida == null ? "POST" : "PUT";
        String ruta = elegida == null ? "/categorias" : "/categorias/" + elegida.idCategoria();
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.enviar(metodo, ruta, cuerpo, Api.Categoria.class),
                guardada -> {
                    Avisos.exito(mensaje, "Categoría guardada.");
                    nueva();
                    cargar();
                }, error -> Avisos.error(mensaje, error));
    }

    @FXML
    private void desactivar() {
        Api.Categoria elegida = tabla.getSelectionModel().getSelectedItem();
        if (elegida == null || !Avisos.confirmar("¿Desactivar la categoría \"" + elegida.nombre() + "\"?")) {
            return;
        }
        Tareas.ejecutarSinResultado(
                () -> ApiClient.INSTANCIA.enviarSinRespuesta("DELETE", "/categorias/" + elegida.idCategoria(), null),
                () -> {
                    Avisos.exito(mensaje, "Categoría desactivada.");
                    nueva();
                    cargar();
                }, error -> Avisos.error(mensaje, error));
    }
}
