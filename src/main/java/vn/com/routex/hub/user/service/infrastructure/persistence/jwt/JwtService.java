package vn.com.routex.hub.user.service.infrastructure.persistence.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.user.service.domain.user.Authorities;
import vn.com.routex.hub.user.service.domain.user.AuthoritiesRepository;
import vn.com.routex.hub.user.service.domain.user.User;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;
    private final AuthoritiesRepository authoritiesRepository;

    public JwtService(JwtProperties jwtProperties, AuthoritiesRepository authoritiesRepository) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        this.authoritiesRepository = authoritiesRepository;
    }
    /*
    * Map<String, Object> claims = new HashMap();
    *
    * */
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.getAccessTokenExpirationMinutes(), ChronoUnit.MINUTES);

        List<Authorities> authorities = authoritiesRepository.findByUserId(user.getId());

        Set<String> roles = authorities.stream()
                .map(Authorities::getRole)
                .collect(Collectors.toSet());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("username", user.getUsername());

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claims(claims)
                .signWith(secretKey)
                .compact();

    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.getRefreshTokenExpirationDays(), ChronoUnit.DAYS);

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(user.getId())
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isRefreshToken(String token) {
        Object type = extractAllClaims(token).get("type");
        return "refresh".equals(type);
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public long getAccessTokenExpiresInSeconds() {
        return jwtProperties.getAccessTokenExpirationMinutes() * 60L;
    }

}
