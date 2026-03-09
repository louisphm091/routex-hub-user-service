package vn.com.routex.hub.user.service.infrastructure.persistence.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.user.service.application.service.authorization.UserAuthorizationService;
import vn.com.routex.hub.user.service.domain.role.AuthoritiesRepository;
import vn.com.routex.hub.user.service.domain.user.User;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service

public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;
    private final AuthoritiesRepository authoritiesRepository;
    private final UserAuthorizationService userAuthorizationService;

    public JwtService(JwtProperties jwtProperties, AuthoritiesRepository authoritiesRepository, UserAuthorizationService userAuthorizationService) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        this.authoritiesRepository = authoritiesRepository;
        this.userAuthorizationService = userAuthorizationService;
    }
    /*
    * Map<String, Object> claims = new HashMap();
    *
    * */
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.getAccessTokenExpirationMinutes(), ChronoUnit.MINUTES);


        Set<String> roles = userAuthorizationService.getRoles(user.getId());
        Set<String> authorities = userAuthorizationService.getAuthorities(user.getId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("roles", roles);
        claims.put("authorities", authorities);

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

    public String extractTokenType(String token) {
        Object tokenType = extractAllClaims(token).get("tokenType");
        return tokenType == null ? null : tokenType.toString();
    }

    public OffsetDateTime extractIssuedAt(String token) {
        Date issuedAt = extractAllClaims(token).getIssuedAt();
        return issuedAt == null ? null : OffsetDateTime.ofInstant(issuedAt.toInstant(), ZoneOffset.UTC);
    }

    public OffsetDateTime extractExpiration(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration == null ? null : OffsetDateTime.ofInstant(expiration.toInstant(), ZoneOffset.UTC);
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
