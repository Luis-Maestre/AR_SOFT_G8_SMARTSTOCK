package co.edu.umb.smartstock.desktop;

/**
 * Punto de entrada para el ejecutable (.exe). Se necesita una clase que NO extienda Application,
 * porque JavaFX falla con "faltan componentes de JavaFX" si el arranque lo hace directamente MainApp.
 */
public final class Launcher {
    private Launcher() { }

    public static void main(String[] args) {
        MainApp.main(args);
    }
}
