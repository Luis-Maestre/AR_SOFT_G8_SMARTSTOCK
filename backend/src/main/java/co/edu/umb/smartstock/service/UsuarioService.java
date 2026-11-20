package co.edu.umb.smartstock.service;

import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.exception.ApiException;
import co.edu.umb.smartstock.model.Rol;
import co.edu.umb.smartstock.model.Usuario;
import co.edu.umb.smartstock.repository.RolRepository;
import co.edu.umb.smartstock.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PasswordEncoder encoder;
    private final AuditoriaService auditoria;

    public UsuarioService(UsuarioRepository usuarios, RolRepository roles, PasswordEncoder encoder,
                          AuditoriaService auditoria) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.encoder = encoder;
        this.auditoria = auditoria;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> listar() {
        return usuarios.findAll().stream().map(Mapeador::usuario).toList();
    }

    @Transactional(readOnly = true)
    public List<RolDto> roles() {
        return roles.findAll().stream().map(r -> new RolDto(r.getIdRol(), r.getNombre(), r.getDescripcion())).toList();
    }

    @Transactional
    public UsuarioDto crear(UsuarioRequest req, Long idActor) {
        String correo = req.correo().trim().toLowerCase();
        if (usuarios.existsByCorreoIgnoreCase(correo)) {
            throw ApiException.conflicto("Ya existe un usuario con ese correo");
        }
        if (req.password() == null || req.password().isBlank()) {
            throw ApiException.solicitudInvalida("La contraseña es obligatoria");
        }
        Usuario u = new Usuario();
        u.setNombre(req.nombre().trim());
        u.setApellido(req.apellido() == null ? null : req.apellido().trim());
        u.setCorreo(correo);
        u.setPasswordHash(encoder.encode(req.password()));
        u.setEstado(req.estado() == null || req.estado());
        u.setRol(rol(req.idRol()));
        u = usuarios.save(u);
        auditoria.registrar(idActor, "CREAR_USUARIO", "usuario", u.getIdUsuario(),
                "Usuario " + u.getCorreo() + " con rol " + u.getRol().getNombre());
        return Mapeador.usuario(u);
    }

    @Transactional
    public UsuarioDto actualizar(Long id, UsuarioRequest req, Long idActor) {
        Usuario u = usuarios.findById(id).orElseThrow(() -> ApiException.noEncontrado("Usuario no encontrado"));
        String correo = req.correo().trim().toLowerCase();
        if (usuarios.existsByCorreoIgnoreCaseAndIdUsuarioNot(correo, id)) {
            throw ApiException.conflicto("Ya existe un usuario con ese correo");
        }
        Rol nuevoRol = rol(req.idRol());
        boolean seraActivo = req.estado() == null || req.estado();
        if (id.equals(idActor) && (!seraActivo || !Rol.ADMINISTRADOR.equals(nuevoRol.getNombre()))) {
            throw ApiException.conflicto("No puedes quitarte el rol de administrador ni desactivarte a ti mismo");
        }
        u.setNombre(req.nombre().trim());
        u.setApellido(req.apellido() == null ? null : req.apellido().trim());
        u.setCorreo(correo);
        u.setRol(nuevoRol);
        u.setEstado(seraActivo);
        if (req.password() != null && !req.password().isBlank()) {
            u.setPasswordHash(encoder.encode(req.password()));
        }
        u = usuarios.save(u);
        auditoria.registrar(idActor, "EDITAR_USUARIO", "usuario", id, "Usuario " + u.getCorreo());
        return Mapeador.usuario(u);
    }

    /** Baja logica: el usuario conserva su historial de movimientos y auditoria. */
    @Transactional
    public void desactivar(Long id, Long idActor) {
        if (id.equals(idActor)) {
            throw ApiException.conflicto("No puedes desactivarte a ti mismo");
        }
        Usuario u = usuarios.findById(id).orElseThrow(() -> ApiException.noEncontrado("Usuario no encontrado"));
        u.setEstado(false);
        usuarios.save(u);
        auditoria.registrar(idActor, "DESACTIVAR_USUARIO", "usuario", id, "Usuario " + u.getCorreo());
    }

    private Rol rol(Long idRol) {
        return roles.findById(idRol).orElseThrow(() -> ApiException.solicitudInvalida("El rol indicado no existe"));
    }
}
