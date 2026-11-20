package co.edu.umb.smartstock.desktop.util;

import co.edu.umb.smartstock.desktop.service.ApiClient;
import java.net.ConnectException;
import java.net.http.HttpConnectTimeoutException;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javafx.concurrent.Task;

/** Ejecuta las llamadas al servidor fuera del hilo de la pantalla para que la ventana no se congele. */
public final class Tareas {

    private Tareas() { }

    public static <T> void ejecutar(Callable<T> trabajo, Consumer<T> alTerminar, Consumer<String> alFallar) {
        Task<T> tarea = new Task<>() {
            @Override
            protected T call() throws Exception {
                return trabajo.call();
            }
        };
        tarea.setOnSucceeded(e -> alTerminar.accept(tarea.getValue()));
        tarea.setOnFailed(e -> alFallar.accept(mensaje(tarea.getException())));
        Thread hilo = new Thread(tarea, "smartstock-api");
        hilo.setDaemon(true);
        hilo.start();
    }

    /** Version para acciones que no devuelven datos. */
    public static void ejecutarSinResultado(ThrowingRunnable trabajo, Runnable alTerminar, Consumer<String> alFallar) {
        ejecutar(() -> {
            trabajo.run();
            return Boolean.TRUE;
        }, ok -> alTerminar.run(), alFallar);
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    public static String mensaje(Throwable e) {
        if (e instanceof ApiClient.ApiError) {
            return e.getMessage();
        }
        if (e instanceof ConnectException || e instanceof HttpConnectTimeoutException) {
            return "No se pudo conectar con el servidor. Verifica que esté encendido.";
        }
        if (e instanceof java.net.http.HttpTimeoutException) {
            return "El servidor tardó demasiado en responder.";
        }
        String m = e == null ? null : e.getMessage();
        return m == null || m.isBlank() ? "Ocurrió un error inesperado." : m;
    }
}
