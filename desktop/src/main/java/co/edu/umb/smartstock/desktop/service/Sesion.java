package co.edu.umb.smartstock.desktop.service;

import co.edu.umb.smartstock.desktop.model.Api;

/** Guarda quién inició sesión y su token mientras la aplicación está abierta. */
public final class Sesion {
    private static String token;
    private static Api.Usuario usuario;

    private Sesion() { }

    public static void iniciar(String t, Api.Usuario u) { token = t; usuario = u; }
    public static void cerrar() { token = null; usuario = null; }

    public static boolean activa() { return token != null; }
    public static String token() { return token; }
    public static Api.Usuario usuario() { return usuario; }

    public static boolean esAdmin() { return usuario != null && "administrador".equals(usuario.rol()); }

    /** Administrador y encargado de inventario pueden crear, editar y registrar. */
    public static boolean puedeGestionar() {
        return usuario != null && ("administrador".equals(usuario.rol()) || "encargado_inventario".equals(usuario.rol()));
    }
}
