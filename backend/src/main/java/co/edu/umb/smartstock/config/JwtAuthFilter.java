package co.edu.umb.smartstock.config;

import co.edu.umb.smartstock.model.Usuario;
import co.edu.umb.smartstock.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final UsuarioRepository usuarios;

    public JwtAuthFilter(JwtService jwt, UsuarioRepository usuarios) {
        this.jwt = jwt;
        this.usuarios = usuarios;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String cabecera = request.getHeader("Authorization");
        if (cabecera != null && cabecera.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            Long id = jwt.validarYObtenerIdUsuario(cabecera.substring(7).trim());
            if (id != null) {
                Usuario u = usuarios.findById(id).orElse(null);
                if (u != null && Boolean.TRUE.equals(u.getEstado())) {
                    String rol = u.getRol().getNombre();
                    UsuarioActual actual = new UsuarioActual(u.getIdUsuario(), u.getCorreo(),
                            u.getNombre() + (u.getApellido() == null ? "" : " " + u.getApellido()), rol);
                    var autenticacion = new UsernamePasswordAuthenticationToken(actual, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase())));
                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
