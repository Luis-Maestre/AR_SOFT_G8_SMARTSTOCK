package co.edu.umb.smartstock.service;

import co.edu.umb.smartstock.config.JwtService;
import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.exception.ApiException;
import co.edu.umb.smartstock.model.Usuario;
import co.edu.umb.smartstock.repository.UsuarioRepository;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final int MAX_INTENTOS = 5;
    private static final long BLOQUEO_SEGUNDOS = 300;

    private record Intentos(int fallos, Instant bloqueadoHasta) { }

    private final Map<String, Intentos> intentos = new ConcurrentHashMap<>();
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final AuditoriaService auditoria;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt, AuditoriaService auditoria) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
        this.auditoria = auditoria;
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        String clave = req.correo().trim().toLowerCase();
        Intentos previo = intentos.get(clave);
        if (previo != null && previo.bloqueadoHasta() != null && Instant.now().isBefore(previo.bloqueadoHasta())) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS,
                    "Demasiados intentos fallidos. Intenta de nuevo en unos minutos.");
        }
        Usuario u = usuarios.findByCorreoIgnoreCase(clave).orElse(null);
        boolean valido = u != null && Boolean.TRUE.equals(u.getEstado())
                && encoder.matches(req.password(), u.getPasswordHash());
        if (!valido) {
            registrarFallo(clave);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
        }
        intentos.remove(clave);
        auditoria.registrar(u.getIdUsuario(), "LOGIN", "usuario", u.getIdUsuario(), "Inicio de sesión");
        return new LoginResponse(jwt.generar(u.getIdUsuario(), u.getCorreo()), "Bearer",
                jwt.getExpiracionSegundos(), Mapeador.usuario(u));
    }

    private void registrarFallo(String clave) {
        intentos.merge(clave, new Intentos(1, null), (viejo, nuevo) -> {
            int fallos = viejo.fallos() + 1;
            return fallos >= MAX_INTENTOS
                    ? new Intentos(0, Instant.now().plusSeconds(BLOQUEO_SEGUNDOS))
                    : new Intentos(fallos, null);
        });
    }

    @Transactional(readOnly = true)
    public UsuarioDto me(Long idUsuario) {
        return Mapeador.usuario(usuarios.findById(idUsuario)
                .orElseThrow(() -> ApiException.noEncontrado("Usuario no encontrado")));
    }

    @Transactional
    public void cambiarPassword(Long idUsuario, CambiarPasswordRequest req) {
        Usuario u = usuarios.findById(idUsuario).orElseThrow(() -> ApiException.noEncontrado("Usuario no encontrado"));
        if (!encoder.matches(req.actual(), u.getPasswordHash())) {
            throw ApiException.solicitudInvalida("La contraseña actual no es correcta");
        }
        u.setPasswordHash(encoder.encode(req.nueva()));
        usuarios.save(u);
        auditoria.registrar(idUsuario, "CAMBIO_PASSWORD", "usuario", idUsuario, "Cambio de contraseña");
    }
}
