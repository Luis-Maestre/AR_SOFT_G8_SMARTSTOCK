package co.edu.umb.smartstock.config;

public final class Permisos {
    private Permisos() { }

    public static final String ADMIN = "hasRole('ADMINISTRADOR')";
    public static final String GESTION = "hasAnyRole('ADMINISTRADOR','ENCARGADO_INVENTARIO')";
}
