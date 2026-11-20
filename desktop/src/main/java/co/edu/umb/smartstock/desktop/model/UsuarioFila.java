package co.edu.umb.smartstock.desktop.model;

/** Un usuario del sistema con su rol. */
public class UsuarioFila {

    private final String nombre;
    private final String correo;
    private final String rol;
    private boolean activo = true;

    public UsuarioFila(String nombre, String correo, String rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
    }

    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getEstado() {
        return activo ? "Activo" : "Inactivo";
    }
}
