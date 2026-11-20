package co.edu.umb.smartstock.desktop.service;

import co.edu.umb.smartstock.desktop.model.InventarioFila;
import co.edu.umb.smartstock.desktop.model.MovimientoFila;
import co.edu.umb.smartstock.desktop.model.ReposicionFila;
import co.edu.umb.smartstock.desktop.model.UsuarioFila;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Datos de ejemplo para las pantallas nuevas.
 * En la Fase 3 estas listas se reemplazan por llamadas al servidor (ApiClient).
 */
public final class DatosDemo {

    private DatosDemo() { }

    public static final ObservableList<InventarioFila> INVENTARIO = FXCollections.observableArrayList(
        new InventarioFila("MED-001", "Acetaminofén 500 mg", "Analgésicos", 320, 50, "Estante A1"),
        new InventarioFila("MED-002", "Ibuprofeno 400 mg", "Antiinflamatorios", 60, 40, "Estante A2"),
        new InventarioFila("MED-003", "Amoxicilina 500 mg", "Antibióticos", 90, 30, "Estante B1"),
        new InventarioFila("MED-004", "Loratadina 10 mg", "Antialérgicos", 20, 25, "Estante B2"),
        new InventarioFila("MED-005", "Omeprazol 20 mg", "Gástricos", 35, 35, "Estante C1")
    );

    public static final ObservableList<MovimientoFila> MOVIMIENTOS = FXCollections.observableArrayList(
        new MovimientoFila("2026-10-16", "Acetaminofén 500 mg", "Entrada", 100, "Compra a proveedor", "encargado"),
        new MovimientoFila("2026-10-16", "Loratadina 10 mg", "Salida", 15, "Venta", "encargado"),
        new MovimientoFila("2026-10-17", "Ibuprofeno 400 mg", "Salida", 10, "Venta", "auxiliar"),
        new MovimientoFila("2026-10-17", "Omeprazol 20 mg", "Ajuste", 35, "Conteo físico", "encargado")
    );

    public static final ObservableList<ReposicionFila> REPOSICIONES = FXCollections.observableArrayList(
        new ReposicionFila("Loratadina 10 mg", "Distribuidora Andina", 60),
        new ReposicionFila("Omeprazol 20 mg", "Distribuidora Andina", 45)
    );

    public static final ObservableList<UsuarioFila> USUARIOS = FXCollections.observableArrayList(
        new UsuarioFila("Administrador General", "admin@drogueria.com", "Administrador"),
        new UsuarioFila("Laura Gómez", "laura@drogueria.com", "Encargado de inventario"),
        new UsuarioFila("Carlos Pérez", "carlos@drogueria.com", "Usuario")
    );

    public static final ObservableList<String> ROLES = FXCollections.observableArrayList(
        "Administrador", "Encargado de inventario", "Usuario"
    );
}
