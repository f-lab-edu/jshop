package jshop.domain.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secretKey) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
            Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Long getId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .get("id", Long.class);
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .get("email", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .get("role", String.class);
    }

    public boolean isExpired(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .getExpiration()
                .before(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        } catch (ExpiredJwtException ex) {
            System.err.println(ex.getMessage());
            return true;
        }
    }

    public String createJwt(Long id, String email, String role, long expiredMs) {
        return Jwts.builder().claim("id", id).claim("email", email).claim("role", role)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiredMs)).signWith(secretKey)
            .compact();
    }

}
