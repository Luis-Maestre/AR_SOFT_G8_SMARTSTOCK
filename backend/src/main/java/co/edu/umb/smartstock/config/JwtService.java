package co.edu.umb.smartstock.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey clave;
    private final long expiracionSegundos;

    public JwtService(@Value("${smartstock.jwt.secret:}") String secreto,
                      @Value("${smartstock.jwt.expiracion-minutos:480}") long expiracionMinutos) {
        if (secreto == null || secreto.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET no esta definida o tiene menos de 32 caracteres. "
                    + "Genera una con: openssl rand -base64 48");
        }
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionSegundos = expiracionMinutos * 60;
    }

    public long getExpiracionSegundos() { return expiracionSegundos; }

    public String generar(Long idUsuario, String correo) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(idUsuario))
                .claim("correo", correo)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusSeconds(expiracionSegundos)))
                .signWith(clave)
                .compact();
    }

    public Long validarYObtenerIdUsuario(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
            return Long.valueOf(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
