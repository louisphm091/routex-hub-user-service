package vn.com.routex.hub.user.service.infrastructure.persistence.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "notification.email.sendgrid")
public class SendGridMailProperties {
    private String baseUrl;
    private String apiKey;
    private String fromEmail;
    private String fromName;
    private String verifySubject;
}