package vn.com.routex.hub.user.service.infrastructure.persistence.jwt;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "identity.jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenExpirationMinutes;
    private long refreshTokenExpirationDays;
    private String issuer;
}
