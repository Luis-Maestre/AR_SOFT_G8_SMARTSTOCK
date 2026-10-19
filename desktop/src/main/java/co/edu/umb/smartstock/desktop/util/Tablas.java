package co.edu.umb.smartstock.desktop.util;

import java.util.function.Function;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;

/** Ayudante para llenar columnas de tabla sin repetir código. */
public final class Tablas {

    private Tablas() { }

    /** Dice qué dato de la fila va en la columna: columna(colNombre, f -> f.getNombre()). */
    public static <T, V> void columna(TableColumn<T, V> columna, Function<T, V> dato) {
        columna.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(dato.apply(c.getValue())));
    }
}
