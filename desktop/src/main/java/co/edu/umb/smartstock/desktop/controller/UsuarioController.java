package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.model.Api;
import co.edu.umb.smartstock.desktop.service.ApiClient;
import co.edu.umb.smartstock.desktop.util.Avisos;
import co.edu.umb.smartstock.desktop.util.Tablas;
import co.edu.umb.smartstock.desktop.util.Tareas;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** RF02: usuarios y roles (solo administrador). Contraseña vacía al editar = no se cambia. */
public class UsuarioController {

    @FXML private TextField campoNombre;
    @FXML private TextField campoApellido;
    @FXML private TextField campoCorreo;
    @FXML private PasswordField campoClave;
    @FXML private ComboBox<Api.Rol> comboRol;
    @FXML private CheckBox checkActivo;
    @FXML private Button botonDesactivar;
    @FXML private Label mensaje;

    @FXML private TableView<Api.Usuario> tabla;
    @FXML private TableColumn<Api.Usuario, String> colNombre;
    @FXML private TableColumn<Api.Usuario, String> colCorreo;
    @FXML private TableColumn<Api.Usuario, String> colRol;
    @FXML private TableColumn<Api.Usuario, String> colEstado;

    @FXML
    private void initialize() {
        Tablas.columna(colNombre, Api.Usuario::nombreCompleto);
        Tablas.columna(colCorreo, Api.Usuario::correo);
        Tablas.columna(colRol, u -> u.rol().replace('_', ' '));
        Tablas.columna(colEstado, u -> Boolean.TRUE.equals(u.estado()) ? "Activo" : "Inactivo");
        botonDesactivar.setDisable(true);
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, antes, u) -> {
            botonDesactivar.setDisable(u == null);
            if (u != null) {
                campoNombre.setText(u.nombre());
                campoApellido.setText(u.apellido() == null ? "" : u.apellido());
                campoCorreo.setText(u.correo());
                campoClave.clear();
                checkActivo.setSelected(Boolean.TRUE.equals(u.estado()));
                for (Api.Rol r : comboRol.getItems()) {
                    if (r.idRol().equals(u.idRol())) {
                        comboRol.getSelectionModel().select(r);
                    }
                }
            }
        });
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/roles", Api.Rol.class), roles -> {
            comboRol.getItems().setAll(roles);
            comboRol.getSelectionModel().selectLast(); // el último creado es "usuario" (menos privilegios)
        }, error -> Avisos.error(mensaje, error));
        cargar();
    }

    @FXML
    private void cargar() {
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/usuarios", Api.Usuario.class),
                lista -> tabla.getItems().setAll(lista), error -> Avisos.error(mensaje, error));
    }

    @FXML
    private void nuevo() {
        tabla.getSelectionModel().clearSelection();
        campoNombre.clear();
        campoApellido.clear();
        campoCorreo.clear();
        campoClave.clear();
        checkActivo.setSelected(true);
        comboRol.getSelectionModel().selectLast();
    }

    @FXML
    private void guardar() {
        Api.Usuario elegido = tabla.getSelectionModel().getSelectedItem();
        String nombre = campoNombre.getText().trim();
        String correo = campoCorreo.getText().trim();
        String clave = campoClave.getText();
        if (nombre.isEmpty() || correo.isEmpty() || comboRol.getValue() == null) {
            Avisos.error(mensaje, "Nombre, correo y rol son obligatorios.");
            return;
        }
        if (elegido == null && clave.length() < 8) {
            Avisos.error(mensaje, "La contraseña debe tener al menos 8 caracteres.");
            return;
        }
        if (elegido != null && !clave.isEmpty() && clave.length() < 8) {
            Avisos.error(mensaje, "La nueva contraseña debe tener al menos 8 caracteres.");
            return;
        }
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("nombre", nombre);
        cuerpo.put("apellido", campoApellido.getText().trim());
        cuerpo.put("correo", correo);
        cuerpo.put("password", clave.isEmpty() ? null : clave);
        cuerpo.put("idRol", comboRol.getValue().idRol());
        cuerpo.put("estado", checkActivo.isSelected());
        String metodo = elegido == null ? "POST" : "PUT";
        String ruta = elegido == null ? "/usuarios" : "/usuarios/" + elegido.idUsuario();
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.enviar(metodo, ruta, cuerpo, Api.Usuario.class),
                ok -> {
                    Avisos.exito(mensaje, "Usuario guardado.");
                    nuevo();
                    cargar();
                }, error -> Avisos.error(mensaje, error));
    }

    @FXML
    private void desactivar() {
        Api.Usuario elegido = tabla.getSelectionModel().getSelectedItem();
        if (elegido == null || !Avisos.confirmar("¿Desactivar al usuario " + elegido.correo() + "? Ya no podrá ingresar.")) {
            return;
        }
        Tareas.ejecutarSinResultado(
                () -> ApiClient.INSTANCIA.enviarSinRespuesta("DELETE", "/usuarios/" + elegido.idUsuario(), null),
                () -> {
                    Avisos.exito(mensaje, "Usuario desactivado.");
                    nuevo();
                    cargar();
                }, error -> Avisos.error(mensaje, error));
    }
}
