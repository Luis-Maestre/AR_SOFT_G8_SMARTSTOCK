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

    /** Texto de un numero entero o vacio si es nulo. */
    public static String num(Number n) {
        return n == null ? "" : String.valueOf(n);
    }

    /** Importe con separador de miles, ej. 12.500. */
    public static String dinero(java.math.BigDecimal v) {
        if (v == null) return "";
        return java.text.NumberFormat.getIntegerInstance(new java.util.Locale("es", "CO")).format(v.setScale(0, java.math.RoundingMode.HALF_UP));
    }
}
