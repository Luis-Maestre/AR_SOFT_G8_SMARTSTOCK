package co.edu.umb.smartstock.config;

import co.edu.umb.smartstock.model.*;
import co.edu.umb.smartstock.repository.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InicializadorDatos implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(InicializadorDatos.class);

    private final RolRepository roles;
    private final UsuarioRepository usuarios;
    private final CategoriaRepository categorias;
    private final ProveedorRepository proveedores;
    private final ProductoRepository productos;
    private final InventarioRepository inventarios;
    private final PasswordEncoder encoder;
    private final Environment entorno;
    private final String adminCorreo;
    private final String adminPassword;
    private final String adminNombre;

    public InicializadorDatos(RolRepository roles, UsuarioRepository usuarios, CategoriaRepository categorias,
                              ProveedorRepository proveedores, ProductoRepository productos,
                              InventarioRepository inventarios, PasswordEncoder encoder, Environment entorno,
                              @Value("${smartstock.admin.correo}") String adminCorreo,
                              @Value("${smartstock.admin.password:}") String adminPassword,
                              @Value("${smartstock.admin.nombre:Administrador}") String adminNombre) {
        this.roles = roles;
        this.usuarios = usuarios;
        this.categorias = categorias;
        this.proveedores = proveedores;
        this.productos = productos;
        this.inventarios = inventarios;
        this.encoder = encoder;
        this.entorno = entorno;
        this.adminCorreo = adminCorreo;
        this.adminPassword = adminPassword;
        this.adminNombre = adminNombre;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        asegurarRol(Rol.ADMINISTRADOR, "Acceso total al sistema");
        asegurarRol(Rol.ENCARGADO_INVENTARIO, "Gestiona productos, inventario, movimientos y reposiciones");
        asegurarRol(Rol.USUARIO, "Solo consulta");

        if (usuarios.count() == 0) {
            if (adminPassword == null || adminPassword.length() < 8) {
                log.warn("No hay usuarios y ADMIN_PASSWORD no esta definida (minimo 8 caracteres): "
                        + "no se creo el administrador inicial.");
            } else {
                Usuario u = new Usuario();
                u.setNombre(adminNombre);
                u.setCorreo(adminCorreo.trim().toLowerCase());
                u.setPasswordHash(encoder.encode(adminPassword));
                u.setEstado(true);
                u.setRol(roles.findByNombre(Rol.ADMINISTRADOR).orElseThrow());
                usuarios.save(u);
                log.info("Administrador inicial creado: {}", u.getCorreo());
            }
        }
        if (entorno.acceptsProfiles(Profiles.of("dev")) && productos.count() == 0) {
            cargarDatosDeEjemplo();
        }
    }

    private void asegurarRol(String nombre, String descripcion) {
        if (roles.findByNombre(nombre).isEmpty()) {
            roles.save(new Rol(nombre, descripcion));
        }
    }

    private void cargarDatosDeEjemplo() {
        String[][] cats = {{"Analgésicos", "Alivian el dolor"}, {"Antiinflamatorios", "Reducen la inflamación"},
                {"Antibióticos", "Combaten infecciones"}, {"Antialérgicos", "Alivian las alergias"}};
        Categoria[] c = new Categoria[cats.length];
        for (int i = 0; i < cats.length; i++) {
            c[i] = new Categoria();
            c[i].setNombre(cats[i][0]);
            c[i].setDescripcion(cats[i][1]);
            categorias.save(c[i]);
        }
        Proveedor p = new Proveedor();
        p.setNombre("Distribuidora Andina");
        p.setTelefono("601 555 0101");
        p.setCorreo("ventas@andina.example");
        proveedores.save(p);

        Object[][] datos = {
                {"Acetaminofén 500 mg", "Caja x 20 tabletas", "MED-001", "5200", 50, 0, 320, "Estante A1"},
                {"Ibuprofeno 400 mg", "Caja x 10 tabletas", "MED-002", "6800", 40, 1, 35, "Estante A2"},
                {"Amoxicilina 500 mg", "Caja x 12 cápsulas", "MED-003", "12500", 30, 2, 90, "Estante B1"},
                {"Loratadina 10 mg", "Caja x 10 tabletas", "MED-004", "4300", 25, 3, 45, "Estante B2"}};
        for (Object[] d : datos) {
            Producto pr = new Producto();
            pr.setNombre((String) d[0]);
            pr.setDescripcion((String) d[1]);
            pr.setCodigo((String) d[2]);
            pr.setPrecio(new BigDecimal((String) d[3]));
            pr.setStockMinimo((Integer) d[4]);
            pr.setEstado(true);
            pr.setCategoria(c[(Integer) d[5]]);
            productos.save(pr);
            Inventario inv = new Inventario();
            inv.setProducto(pr);
            inv.setCantidadActual((Integer) d[6]);
            inv.setUbicacion((String) d[7]);
            inv.setFechaActualizacion(LocalDateTime.now());
            inventarios.save(inv);
        }
        log.info("Datos de ejemplo cargados (perfil dev)");
    }
}
