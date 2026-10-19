package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.model.UsuarioFila;
import co.edu.umb.smartstock.desktop.service.DatosDemo;
import co.edu.umb.smartstock.desktop.util.Avisos;
import co.edu.umb.smartstock.desktop.util.Tablas;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** RF02: crear usuarios, asignarles rol e inactivarlos (modo demostración). */
public class UsuarioController {

    @FXML private TextField campoNombre;
    @FXML private TextField campoCorreo;
    @FXML private ComboBox<String> comboRol;
    @FXML private Label mensaje;

    @FXML private TableView<UsuarioFila> tabla;
    @FXML private TableColumn<UsuarioFila, String> colNombre;
    @FXML private TableColumn<UsuarioFila, String> colCorreo;
    @FXML private TableColumn<UsuarioFila, String> colRol;
    @FXML private TableColumn<UsuarioFila, String> colEstado;

    @FXML
    private void initialize() {
        comboRol.setItems(DatosDemo.ROLES);
        comboRol.getSelectionModel().selectLast();

        Tablas.columna(colNombre, UsuarioFila::getNombre);
        Tablas.columna(colCorreo, UsuarioFila::getCorreo);
        Tablas.columna(colRol, UsuarioFila::getRol);
        Tablas.columna(colEstado, UsuarioFila::getEstado);
        tabla.setItems(DatosDemo.USUARIOS);
    }

    @FXML
    private void crear() {
        String nombre = campoNombre.getText().trim();
        String correo = campoCorreo.getText().trim();
        if (nombre.isEmpty() || !correo.contains("@")) {
            Avisos.error(mensaje, "Escribe un nombre y un correo válido.");
            return;
        }
        if (comboRol.getValue() == null) {
            Avisos.error(mensaje, "Elige un rol.");
            return;
        }
        boolean repetido = DatosDemo.USUARIOS.stream().anyMatch(u -> u.getCorreo().equalsIgnoreCase(correo));
        if (repetido) {
            Avisos.error(mensaje, "Ya existe un usuario con ese correo.");
            return;
        }
        DatosDemo.USUARIOS.add(new UsuarioFila(nombre, correo, comboRol.getValue()));
        Avisos.exito(mensaje, "Usuario creado: " + nombre);
        campoNombre.clear();
        campoCorreo.clear();
    }

    @FXML
    private void cambiarEstado() {
        UsuarioFila fila = tabla.getSelectionModel().getSelectedItem();
        if (fila == null) {
            Avisos.error(mensaje, "Selecciona un usuario de la tabla.");
            return;
        }
        fila.setActivo(!fila.isActivo());
        tabla.getItems().set(tabla.getItems().indexOf(fila), fila); // redibuja la fila
        Avisos.exito(mensaje, fila.getNombre() + " ahora está " + fila.getEstado().toLowerCase() + ".");
    }
}
