package co.edu.umb.smartstock.desktop.controller;

import co.edu.umb.smartstock.desktop.model.ReposicionFila;
import co.edu.umb.smartstock.desktop.service.DatosDemo;
import co.edu.umb.smartstock.desktop.util.Avisos;
import co.edu.umb.smartstock.desktop.util.Tablas;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** RF08 y RF09: ver las sugerencias de reposición y confirmarlas o rechazarlas. */
public class ReposicionController {

    @FXML private TableView<ReposicionFila> tabla;
    @FXML private TableColumn<ReposicionFila, String> colProducto;
    @FXML private TableColumn<ReposicionFila, String> colProveedor;
    @FXML private TableColumn<ReposicionFila, Integer> colSugerida;
    @FXML private TableColumn<ReposicionFila, Integer> colSolicitada;
    @FXML private TableColumn<ReposicionFila, String> colEstado;
    @FXML private TextField campoCantidad;
    @FXML private Label mensaje;

    @FXML
    private void initialize() {
        Tablas.columna(colProducto, ReposicionFila::getProducto);
        Tablas.columna(colProveedor, ReposicionFila::getProveedor);
        Tablas.columna(colSugerida, ReposicionFila::getSugerida);
        Tablas.columna(colSolicitada, ReposicionFila::getSolicitada);
        Tablas.columna(colEstado, ReposicionFila::getEstado);
        tabla.setItems(DatosDemo.REPOSICIONES);
    }

    @FXML
    private void confirmar() {
        ReposicionFila fila = filaElegida();
        if (fila == null) {
            return;
        }
        String texto = campoCantidad.getText().trim();
        if (!texto.isEmpty()) {
            try {
                int cantidad = Integer.parseInt(texto);
                if (cantidad <= 0) {
                    Avisos.error(mensaje, "La cantidad debe ser mayor que cero.");
                    return;
                }
                fila.setSolicitada(cantidad);
            } catch (NumberFormatException e) {
                Avisos.error(mensaje, "La cantidad debe ser un número entero.");
                return;
            }
        }
        cambiarEstado(fila, "Confirmada");
        Avisos.exito(mensaje, "Reposición confirmada: " + fila.getSolicitada() + " unidades de " + fila.getProducto());
    }

    @FXML
    private void rechazar() {
        ReposicionFila fila = filaElegida();
        if (fila == null) {
            return;
        }
        cambiarEstado(fila, "Rechazada");
        Avisos.exito(mensaje, "Reposición rechazada.");
    }

    private ReposicionFila filaElegida() {
        ReposicionFila fila = tabla.getSelectionModel().getSelectedItem();
        if (fila == null) {
            Avisos.error(mensaje, "Selecciona una reposición de la tabla.");
        } else if (!"Sugerida".equals(fila.getEstado())) {
            Avisos.error(mensaje, "Esa reposición ya fue " + fila.getEstado().toLowerCase() + ".");
            return null;
        }
        return fila;
    }

    /** Cambia el estado y vuelve a poner la fila en la lista para que la tabla se redibuje. */
    private void cambiarEstado(ReposicionFila fila, String estado) {
        fila.setEstado(estado);
        int posicion = tabla.getItems().indexOf(fila);
        tabla.getItems().set(posicion, fila);
        campoCantidad.clear();
    }
}
