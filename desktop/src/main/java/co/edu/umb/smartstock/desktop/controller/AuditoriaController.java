package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.model.Api;
import co.edu.umb.smartstock.desktop.service.ApiClient;
import co.edu.umb.smartstock.desktop.util.Avisos;
import co.edu.umb.smartstock.desktop.util.Tablas;
import co.edu.umb.smartstock.desktop.util.Tareas;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/** RF11: registro de auditoría (quién hizo qué y cuándo). Solo administrador. */
public class AuditoriaController {

    @FXML private Label mensaje;
    @FXML private TableView<Api.Auditoria> tabla;
    @FXML private TableColumn<Api.Auditoria, String> colFecha;
    @FXML private TableColumn<Api.Auditoria, String> colUsuario;
    @FXML private TableColumn<Api.Auditoria, String> colAccion;
    @FXML private TableColumn<Api.Auditoria, String> colTabla;
    @FXML private TableColumn<Api.Auditoria, String> colDetalle;

    @FXML
    private void initialize() {
        Tablas.columna(colFecha, a -> Api.fecha(a.fecha()));
        Tablas.columna(colUsuario, Api.Auditoria::usuario);
        Tablas.columna(colAccion, a -> a.accion().replace('_', ' '));
        Tablas.columna(colTabla, a -> a.tablaAfectada() == null ? "" : a.tablaAfectada());
        Tablas.columna(colDetalle, a -> a.descripcion() == null ? "" : a.descripcion());
        cargar();
    }

    @FXML
    private void cargar() {
        Avisos.info(mensaje, "Cargando...");
        Tareas.ejecutar(() -> ApiClient.INSTANCIA.lista("/auditoria?limite=500", Api.Auditoria.class), lista -> {
            tabla.getItems().setAll(lista);
            Avisos.info(mensaje, "Últimos " + lista.size() + " registros");
        }, error -> Avisos.error(mensaje, error));
    }
}
